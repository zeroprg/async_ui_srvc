package net.bloberry.async_ui.dpe.servlets;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import net.bloberry.async_ui.common.Constants;
import net.bloberry.async_ui.common.DataExchangeConfigureException;
import net.bloberry.async_ui.common.User;
import net.bloberry.async_ui.dpe.MessageSeverity;
import net.bloberry.async_ui.dpe.models.Window;
import net.bloberry.async_ui.services.ServiceFactory;
import net.bloberry.async_ui.services.ServiceType;
import net.bloberry.async_ui.services.UIService;

public class PushUpServlet extends HttpServlet {

	private static final long serialVersionUID = 7971634779508722160L;
	private static Logger log = Logger.getLogger(PushUpServlet.class);
    private UIService uiService = null;
	
	public void init() throws ServletException {
		
        try {
        	
        	 uiService = ((UIService) ServiceFactory.getService(ServiceType.UI)); 
        	 
        } catch (DataExchangeConfigureException e) {
        	// TODO Auto-generated catch block
        	e.printStackTrace();
    		return;
        }
	}
		public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
			processRequest(request,response);
		}
		public void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
			processRequest(request,response);
		}
		
		
		public void processRequest(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
	     
			//content type must be set to text/event-stream
			// this is first type of request/response event based
	        response.setContentType("text/event-stream");   
	        //encoding must be set to UTF-8
	        response.setCharacterEncoding("UTF-8");
	        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	        response.setDateHeader("Expires", -1);
	        String windowToken=request.getParameter(Constants.WINDOWTOKEN);
			Enumeration <String> names=request.getParameterNames();
			int parameterCount=0;
	        while(names.hasMoreElements())
	        {
	        	String name=names.nextElement();
	        	parameterCount++;
	        	//System.out.println(name+":"+request.getParameter(name));
	        }

	        Map<String,Window> windowMap = uiService.getWindowMap();
	        if(windowMap != null)
	        {
	        	String windowClass=request.getParameter(Constants.WINDOWCLASS);
        		String loginName=request.getParameter(Constants.LOGINNAME);
        		String password=request.getParameter(Constants.PASSWORD);
        		
		        Window window=windowMap.get(windowToken);
		        
		        if(window==null)
		        {
	        		User user=null;
		        	if(windowClass!=null)// && loginName != null && !loginName.isEmpty() )
		        	{
			        	user=new User();
		        		user.setLoginName(loginName);
		        		user.setPassword(password);
		        		
			           	window=getWindowModel(windowClass,windowToken,user);
			           	
			           	window.setUI(windowClass);
			           	
		            	window.setConnected();
		        		windowMap.put(windowToken, window);
			//			window.setConnected();
						try
						{
							window.flashState();
							// this is second type of request/response not event based
					        response.setContentType("application/json; charset=utf-8");
					        //response.setContentType("Origin: http://skiplagged.com");
					        response.setContentType("Access-Control-Allow-Origin: *");
							window.superUpdateAndSendMessage(request,response.getWriter());
						}
						catch(DataExchangeConfigureException e){
							window.forceToDispose();
							e.printStackTrace();
							window.updateMessageInfo(e.getMessage(), MessageSeverity.ERROR, response.getWriter());
						}
		        	}
		        	else
		        	{
		        		Window.requestWindowClass(response.getWriter());
		        	}
		        	

		        }
		        else
		        {
						window.setConnected();
						
						if(window.isExpired()||parameterCount>1){
							try{
								window.superUpdateAndSendMessage(request,response.getWriter());
								
							}catch(DataExchangeConfigureException e){
								window.forceToDispose();
								e.printStackTrace();
								window.updateMessageInfo(e.toString(),MessageSeverity.ERROR,response.getWriter());
							}
						}
						else
						{
							//log.debug("Got request,but not process.");
						}
		        }
	          }
	    }

		
		final Window getWindowModel(String className,String windowToken,User user){
			Window ret=null;
	       
	            Class<?> c=null;
				try {
					if(className!=null){
						c = Class.forName("net.bloberry.async_ui.dpe.models."+className);
					}else{
						throw new ClassNotFoundException();
					}
				} catch (ClassNotFoundException e) {
					log.error("class "+className);
				}
				try {
					ret =(Window)c.asSubclass(Window.class).getConstructor(String.class,User.class).newInstance(windowToken,user);
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InstantiationException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
	 		
			
			return ret;
		}

	}