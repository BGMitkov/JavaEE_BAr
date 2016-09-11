package bar.services;

import java.net.HttpURLConnection;
import java.util.Collection;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import bar.dao.OrderDAO;
import bar.model.Order;

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

	// @Path("/order")
	// @POST
	// @Consumes(MediaType.APPLICATION_JSON)	
	// public Response order(Order newOrder) {
	// orderDAO.addOrder(newOrder);
	// return Response.noContent().build();
	// }

	// @Path("/accept")
	// @PUT
	// @RolesAllowed({"Manager", "Barman"})
	// public Response accept() {
	// if (!(context.isCallerInRole("Manager") |
	// context.isCallerInRole("Barman"))) {
	// return Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).build();
	// }
	// orderDAO.setOrderAsAccepted(order, user);
	//
	// }

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

	@Path("/overdue")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response setOrderAsOverdue(@QueryParam("orderId") String orderId) {
		Order orderOverdue = orderDAO.findById(Long.parseLong(orderId));
		if(orderOverdue != null) {
			orderDAO.setOrderAsOverdue(orderOverdue);
		}
		return Response.noContent().build();
	}
}
