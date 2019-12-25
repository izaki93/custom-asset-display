package lb.gov.dawlati.customassets.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.portlet.ResourceResponse;

public class ResponseWriterUtil {
	
	public static void write(ResourceResponse resourceResponse,String josn) throws IOException {
		resourceResponse.setContentType("application/json");
		PrintWriter out = resourceResponse.getWriter();
		out.println(josn);
		out.flush();
	}

}
