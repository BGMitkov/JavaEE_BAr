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

	private static final Response RESPONSE_OK = Response.ok().build();

	@Inject
	private OrderDAO orderDAO;

	@Inject
	private UserContext context;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response order(Order newOrder) {
		if (!context.isManager() && !context.isWaiter()) {
			return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).build();
		}
		newOrder.setDateOfOrder(new Date());
		newOrder.calculateTotalPrice();
		newOrder.setStatus(Status.WAITING);
		orderDAO.addOrder(newOrder);
		return RESPONSE_OK;
	}

	@Path("/waiting")
	@GET
	@Produces("application/json")
	public Collection<Order> getAllWaitingOrders() {
		if (!context.isManager() && !context.isBarman()) {
			return null;
		}
		return orderDAO.getAllWaitingOrders();
	}

	@Path("/orders")
	@GET
	@Produces("application/json")
	public Collection<Order> getCurrentUserOrders() {
		if (!context.isManager() && !context.isBarman()) {
			return null;
		}
		return orderDAO.getCurrentUserOrders(context.getCurrentUser());
	}

	@PUT
	@Path("/accept")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setOrderAsAccepted(@QueryParam("orderId") String orderId) {
		if (!context.isManager() && !context.isBarman()) {
			return null;
		}
		Order orderToAccept = orderDAO.findById(Long.parseLong(orderId));
		if (orderToAccept != null) {
			if (orderToAccept.getExecutor() == null) {
				orderDAO.setOrderAsAccepted(orderToAccept, context.getCurrentUser());
				return Response.status(HttpURLConnection.HTTP_OK).build();
			} else {
				return Response.status(HttpURLConnection.HTTP_CONFLICT).build();
			}
		}
		return Response.status(HttpURLConnection.HTTP_NO_CONTENT).build();

	}

	@PUT
	@Path("/overdue")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setOrderAsOverdue(@QueryParam("orderId") String orderId) {
		Order orderOverdue = orderDAO.findById(Long.parseLong(orderId));
		if (orderOverdue != null) {
			if (orderOverdue.getStatus() == Status.ACCEPTED) {
				short minutes = orderDAO.getOrderActiveTime(orderOverdue);
				if (minutes >= 0) {
					orderDAO.setOrderAsOverdue(orderOverdue);
					return Response.status(HttpURLConnection.HTTP_OK).build();
				} else {
					return Response.status(HttpURLConnection.HTTP_NOT_ACCEPTABLE).build();
				}
			} else {
				return Response.status(HttpURLConnection.HTTP_CONFLICT).build();
			}
		}
		return Response.status(HttpURLConnection.HTTP_NO_CONTENT).build();
	}

	@PUT
	@Path("/complete")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setOrderAsComplete(@QueryParam("orderId") String orderId) {
		Order orderCompleted = orderDAO.findById(Long.parseLong(orderId));
		if (orderCompleted != null) {
			if (orderCompleted.getStatus() != Status.COMPLETE
					&& orderCompleted.getStatus() != Status.OVERDUE_COMPLETED) {
				orderDAO.setOrderAsCompleted(orderCompleted);
				return Response.status(HttpURLConnection.HTTP_OK).build();
			} else {
				return Response.status(HttpURLConnection.HTTP_NOT_MODIFIED).build();
			}
		} else {
			return Response.status(HttpURLConnection.HTTP_NO_CONTENT).build();
		}
	}
}
