package bar.services;

import java.text.SimpleDateFormat;
import java.util.Date;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import bar.dao.OrderDAO;
import bar.model.DateContainer;


@Stateless
@Path("/report")
public class ReportManager {
	
	@Inject 
	private OrderDAO orderDAO;
	
	@Inject
	private UserContext context;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String estimateProfitBetweenTwoDates( DateContainer dates ) //throws  Exception
	{
		Date begDate = dates.getBegDate();
	    Date endDate = dates.getEndDate();	 
		
	    System.out.println(begDate.toString());
	    System.out.println(endDate.toString());
	    
	    try
	    {
		float f = orderDAO.estimateProfitBetweenTwoDates(begDate,endDate);
		
		return Float.toString(f);
	    }
	    
	    catch(Exception e){
	    	return null;
	    } 
		
	}


}
