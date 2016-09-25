package bar.services;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import bar.dao.OrderDAO;
import bar.model.Order;
import bar.model.Role;
import bar.model.Status;

@Stateless
@Path("order")
public class OrderManager {

	private static final Response RESPONSE_OK = Response.status(HttpURLConnection.HTTP_OK).build();
	private static final Response RESPONSE_UNAUTHORIZED = Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).build();
	private static final Response RESPONSE_NO_CONTENT = Response.status(HttpURLConnection.HTTP_NO_CONTENT).build();
	private static final Response RESPONSE_CONFLICT = Response.status(HttpURLConnection.HTTP_CONFLICT).build();
	private static final Response RESPONSE_NOT_ACCEPTABLE = Response.status(HttpURLConnection.HTTP_NOT_ACCEPTABLE)
			.build();
	private static final Response RESPONSE_NOT_MODIFIED = Response.status(HttpURLConnection.HTTP_NOT_MODIFIED).build();

	@Inject
	private OrderDAO orderDAO;

	@Inject
	private UserContext context;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response order(Order newOrder) {
		if (!context.isUserInRole(Role.Manager, Role.Waiter)) {
			return RESPONSE_UNAUTHORIZED;
		}
		newOrder.setDateOfOrder(new Date());
		newOrder.calculateTotalPrice();
		newOrder.setStatus(Status.WAITING);
		orderDAO.addOrder(newOrder);
		return Response.ok().build();
	}

	@Path("/waiting")
	@GET
	@Produces("application/json")
	public Collection<Order> getAllWaitingOrders() {
		if (!context.isUserInRole(Role.Manager, Role.Barman)) {
			return null;
		}
		return orderDAO.getAllWaitingOrders();
	}

	@Path("/current")
	@GET
	@Produces("application/json")
	public Collection<Order> getCurrentUserOrders() {
		if (!context.isUserInRole(Role.Manager, Role.Barman)) {
			return null;
		}
		return orderDAO.getCurrentUserOrders(context.getCurrentUser());
		
	}

	@PUT
	@Path("/accept")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setOrderAsAccepted(@QueryParam("orderId") String orderId) {
		if (!context.isUserInRole(Role.Manager, Role.Barman)) {
			return RESPONSE_UNAUTHORIZED;
		}
		Order orderToAccept = orderDAO.findById(Long.parseLong(orderId));
		if (orderToAccept != null) {
			if (orderToAccept.getExecutor() == null) {
				orderDAO.setOrderAsAccepted(orderToAccept, context.getCurrentUser());
				return RESPONSE_OK;
			} else {
				return RESPONSE_CONFLICT;
			}
		}
		return RESPONSE_NO_CONTENT;

	}

	@PUT
	@Path("/overdue")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setOrderAsOverdue(@QueryParam("orderId") String orderId) {
		if (!context.isUserInRole(Role.Manager, Role.Barman)) {
			return RESPONSE_UNAUTHORIZED;
		}
		Order orderOverdue = orderDAO.findById(Long.parseLong(orderId));
		if (orderOverdue != null) {
			if (orderOverdue.getStatus() == Status.ACCEPTED) {
				short minutes = orderDAO.getOrderActiveTime(orderOverdue);
				if (minutes >= 0) {
					orderDAO.setOrderAsOverdue(orderOverdue);
					return RESPONSE_OK;
				} else {
					return RESPONSE_NOT_ACCEPTABLE;
				}
			} else {
				return RESPONSE_CONFLICT;
			}
		}
		return RESPONSE_NO_CONTENT;
	}

	@PUT
	@Path("/complete")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setOrderAsComplete(@QueryParam("orderId") String orderId) {
		if (!context.isUserInRole(Role.Manager, Role.Barman)) {
			return RESPONSE_UNAUTHORIZED;
		}
		Order orderCompleted = orderDAO.findById(Long.parseLong(orderId));
		if (orderCompleted != null) {
			if (orderCompleted.getStatus() == Status.ACCEPTED || orderCompleted.getStatus() == Status.OVERDUE) {
				orderDAO.setOrderAsCompleted(orderCompleted);
				return RESPONSE_OK;
			} else {
				return RESPONSE_NOT_MODIFIED;
			}
		} else {
			return RESPONSE_NO_CONTENT;
		}
	}
}
