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


@Stateless
@Path("report")
public class ReportManager {
	
	@Inject 
	private OrderDAO orderDAO;
	
	@Inject
	private UserContext context;
	
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public String estimateProfitBetweenTwoDates( String Dates ) throws  Exception
	{
		String d1=Dates.substring(1, 11);
		String d2=Dates.substring(11, 21);
		
		Date begDate=null;
		Date endDate=null;
		
		
			begDate =new SimpleDateFormat("yyyy/MM/dd").parse(d1);
	    	
			endDate =new SimpleDateFormat("yyyy/MM/dd").parse(d2);
	    	 
		
		float f = orderDAO.estimateProfitBetweenTwoDates(begDate,endDate);
		return Float.toString(f);
	}


}
