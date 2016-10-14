package net.bloberry.async_ui.dpe.servlets;


import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.AuthenticationException;
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
import net.bloberry.async_ui.dpe.models.EmptyWindow;
import net.bloberry.async_ui.dpe.models.Window;
import net.bloberry.async_ui.services.ServiceFactory;
import net.bloberry.async_ui.services.ServiceType;
import net.bloberry.async_ui.services.UIService;

public class LoadStaticContentServlet extends HttpServlet {
	     
	private static final long serialVersionUID = -402428576977198939L;
	private static Logger log = Logger.getLogger(LoadStaticContentServlet.class);

	public void init() throws ServletException {
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        BufferedReader br=request.getReader();
        log.debug(br.readLine());
		
		return;
	}
		public void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {
    		    		
        	String windowClass=request.getParameter(Constants.WINDOWCLASS);
    		String loginName=request.getParameter(Constants.LOGINNAME);
    		String password=request.getParameter(Constants.PASSWORD);
    		
    		String ui = request.getPathInfo().replaceAll(".html", "").substring(1);
    		
			//content type must be set to text/event-stream
	        response.setContentType("text/event-stream");   
	 
	        //encoding must be set to UTF-8
	        response.setCharacterEncoding("UTF-8");
	        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
	        response.setDateHeader("Expires", -1);
	        String windowToken="";
	        User user = null;
	        UIService uiService = null;
	        try {
	        	
	        	 uiService = ((UIService) ServiceFactory.getService(ServiceType.UI)); 
	        	 
	        } catch (DataExchangeConfigureException e) {
	        	// TODO Auto-generated catch block
	        	e.printStackTrace();
        		request.getRequestDispatcher("error.html").forward(request, response);
        		return;
	        }
	        if(uiService!=null){
	        	try{
	        		user = uiService.authenticate(loginName, password);
	        	} catch (AuthenticationException e1) {
	        		user=new User();
	        		user.setLoginName(loginName);
	        		user.setPassword(password);
	        	} catch (DataExchangeConfigureException e) {
					e.printStackTrace();
				}
	        }
	        if(user!=null){
	        	windowToken=request.getParameter("windowToken");
	        	if(windowToken==null){ windowToken=uiService.getNextWindowToken(); }
	            String redirectRequestParams=excludeParamsFromRequest(request,new String[] {Constants.LOGINNAME, Constants.PASSWORD,"timeStamp","ui",Constants.WINDOWCLASS,Constants.WINDOWTOKEN},Constants.WINDOWTOKEN+"="+windowToken);
				log.debug("redirectRequestParams = " + redirectRequestParams);
				if (windowClass == null)
				{
					windowClass = getModelByUI(ui);
				}

	            Window window=null;
	            if(windowClass!=null)
	            {
	            	window=getWindowModel(windowClass,windowToken,user);
	            	window.setUI(ui);
	            	window.setConnected();
	            }else{
	            	window=new EmptyWindow(windowToken,user);
	            }
	            Map<String,Window> windowMap =  uiService.getWindowMap();
	            if(windowMap==null){
	            	windowMap=new ConcurrentHashMap<String,Window>();
	            	uiService.setWindowMap(windowMap);
//	            	context.setAttribute(Constants.WINDOWMAP, windowMap);
	            }
	            
	        	windowMap.put(windowToken, window);
	        	

	            
		        response.setContentType("text/html");   
		   	 
		        //encoding must be set to UTF-8
		        response.setCharacterEncoding("UTF-8");
		        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
		        response.setDateHeader("Expires", -1);
		        String uri=request.getScheme() + "://" +   // "http" + "://
	            request.getServerName() +       // "myhost"
	            ":" +                           // ":"
	            request.getServerPort()+
	            request.getContextPath()+
	            "/pages"+
	            request.getPathInfo()+
	            "?"+
	            redirectRequestParams+
	            "&"+Constants.WINDOWCLASS+"="+windowClass;

		        response.sendRedirect(uri);
	        }
	        
	    }

		/**
		 * Get right side of next string expressions: getModelByUI ('abcd_efg') return 'efg' 
		 * @param ui
		 * @return
		 */
		final private String getModelByUI(String ui){
			
		String ret =  ui;//.substring(0,ui.indexOf("."));
		String[] str_arr = ret.split("_",2);
		if( str_arr.length == 2) {
			ret = str_arr[1];
		}
		return ret;
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
		
		final static String excludeParamsFromRequest(HttpServletRequest request,String[] paramToRemove,String paramToAdd){
			StringBuffer b=new StringBuffer();
			Enumeration<String> enumer=request.getParameterNames();
			while(enumer.hasMoreElements()){
				String name=enumer.nextElement();
				boolean found=false;
				if(paramToRemove!=null){
					for (int i = 0; i < paramToRemove.length; i++) {
						if(name.equalsIgnoreCase(paramToRemove[i])){
							found=true;
							break;
						}
					}
				}
				if(!found){
					if(b.length()>0){
						b.append('&');
					}
					b.append(name);
					b.append('=');
					b.append(request.getParameter(name));
				}
			}
			if(paramToAdd!=null){
				if(b.length()>0){
					b.append('&');
				}
				b.append(paramToAdd);
			}
			
			return b.toString();
		}
	}