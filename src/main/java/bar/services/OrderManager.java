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
		return orderDAO.getAllWaitingOrders();
	}

	@Path("/orders")
	@GET
	@Produces("application/json")
	public Collection<Order> getCurrentUserOrders() {
		return orderDAO.getCurrentUserOrders(context.getCurrentUser());
	}

	@PUT
	@Path("/accept")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setOrderAsAccepted(@QueryParam("orderId") String orderId) {
		Order orderToAccept = orderDAO.findById(Long.parseLong(orderId));
		if(orderToAccept != null) {
			if(orderToAccept.getExecutor() == null){
				orderDAO.setOrderAsAccepted(orderToAccept,context.getCurrentUser());
				return RESPONSE_OK;
			}
			else {
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
		if(orderOverdue != null) {
			if(orderOverdue.getStatus() != Status.OVERDUE){
				orderDAO.setOrderAsOverdue(orderOverdue);
				return RESPONSE_OK;
			}
			else {
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
		if(orderCompleted != null) {
			if(orderCompleted.getStatus() != Status.COMPLETE && orderCompleted.getStatus() != Status.OVERDUE_COMPLETED){
				orderDAO.setOrderAsCompleted(orderCompleted);
				return RESPONSE_OK;
			}
			else {
				return Response.status(HttpURLConnection.HTTP_NOT_MODIFIED).build();
			}
		}
		else {
			return Response.status(HttpURLConnection.HTTP_NO_CONTENT).build();
		}
	}
}
