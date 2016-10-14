package net.bloberry.async_ui.dpe.servlets;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**

 */
public class URIFilter implements Filter {
	private static final String CORSWebSite = "skiplagged.com";
	FilterConfig config; 
	 ServletContext context;
    public void init(FilterConfig filterConfig) throws ServletException {
    	this.config = filterConfig;
        context =  config.getServletContext();

    }

	public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain)
    throws IOException, ServletException {
		
	/*	
	  Window window = null;
	  
	  
      String windowToken= request.getParameter("windowToken");
      Map <String,Window> windowMap = (Map<String,Window>)context.getAttribute("WindowMap");
      if(windowMap!=null)
      {
        window = windowMap.get(windowToken);
      }
      if(window==null)
      {
      	RequestDispatcher rd = request.getRequestDispatcher("error.html");
      	rd.forward(request, response);
       }
      else
      {
      	 filterChain.doFilter(request, response);
      }
      */
		
		
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response; 
        String requestURI = req.getRequestURI();
        int indx = 0;
        if ((indx = requestURI.indexOf(CORSWebSite)) > 0) {
        	String newURL = requestURI.substring(indx);
           	newURL = request.getScheme() + "://" + newURL +
	            "?"+ LoadStaticContentServlet.excludeParamsFromRequest(req,null, null);

	       makeACall(newURL, request.getServerName() + ":" + request.getServerPort() + requestURI, res);
        } 


//   		 filterChain.doFilter(request, response);

		
		
    }

	
	private void makeACall(String url, String oldUrl,  HttpServletResponse res) throws ClientProtocolException, IOException
	{
		HttpGet httpget = new HttpGet(url);
	   	byte[] b = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = httpclient.execute(httpget);
		try {
		    HttpEntity entity = response.getEntity();
		    if (entity != null) {
		        InputStream in = entity.getContent();
        	    PrintWriter writer = res.getWriter();
		        try {
		            // read remote host respponse
					// copy from one response to another one
		        	
		        	ByteArrayOutputStream out = new ByteArrayOutputStream();
		        	byte[] buffer = new byte[1024];
		        	while (true) {
		        	    int r = in.read(buffer);
		        	    if (r == -1) break;
		        	    out.write(buffer, 0, r);
		        	}

		        	byte[] ret = out.toByteArray();
		        	
		        	out.write(buffer);
		        	out.flush();
		        	out.close();
					
					// populate local host response

		        	    writer.print(new String(ret));
					    res.setStatus(HttpServletResponse.SC_CREATED);
						res.setContentLength(ret.length);
					    res.setContentType("text/json");
					    res.setHeader("Location", oldUrl);
					   					
			        res.setHeader("Access-Control-Allow-Origin", oldUrl);
			        res.setHeader("Content-Type", "application/json");
			        
		        } finally {

	        	    writer.flush();
	        	    writer.close();
		            in.close();
		        }
		    }
		} finally {
		    response.close();
		}
	}

    public void destroy() {
    	
    }
    
    
   

}