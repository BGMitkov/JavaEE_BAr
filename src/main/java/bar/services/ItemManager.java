package bar.services;

import java.net.HttpURLConnection;
import java.util.Collection;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import bar.dao.ItemDAO;
import bar.model.Item;
import bar.model.Role;

@Stateless
@Path("item")
public class ItemManager {

	private static final Response RESPONSE_OK = Response.ok().build();
	private static final Response RESPONSE_UNAUTHORIZED = Response.status(HttpURLConnection.HTTP_UNAUTHORIZED).build();

	@Inject
	private ItemDAO itemDAO;
	
	@Inject
	private UserContext context;

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public Response addNewItem(Item newItem) {
		if(!context.isManager()){
			return RESPONSE_UNAUTHORIZED;
		}
		itemDAO.addItem(newItem);
		return RESPONSE_OK;
	}

	@Path("/items")
	@GET
	@Produces("application/json")
	public Collection<Item> getAllItems() {
		if(!context.isManager() && !context.isWaiter()){
			return null;
		}
		return itemDAO.getAllItems();
	}

}
