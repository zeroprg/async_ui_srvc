package net.bloberry.async_ui.dpe.servlets;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import net.bloberry.async_ui.common.Constants;
import net.bloberry.async_ui.common.DataExchangeConfigureException;
import net.bloberry.async_ui.dpe.models.Window;
import net.bloberry.async_ui.dpe.utils.PurgeWindowsThread;
import net.bloberry.async_ui.services.ServiceFactory;
import net.bloberry.async_ui.services.ServiceType;
import net.bloberry.async_ui.services.ServicesEngine;
import net.bloberry.async_ui.services.UIService;

public class LoadOnStartupServlet extends HttpServlet {

	private static final long serialVersionUID = 1344624634070704038L;
	
	 private static Logger log = Logger.getLogger(LoadOnStartupServlet.class);
	
	/**
     * init:
     *
     *
     */
	
	
    public void init() 
    {
    	log.info("gstMobile LoadOnStartup init..........");
        try {
       		ServletConfig config = getServletConfig();
            ServicesEngine proxy = ServicesEngine.getInstance();
            
            
       		log.info("version: " + proxy.getVersion()+"...");
       		log.info("environment: "+ proxy.getEnviromentName()+"...");

            Map <String,Window> windowMap = ((UIService) ServiceFactory.getService(ServiceType.UI)).getWindowMap();
 
            // Initialize service proxy
			ServicesEngine.getInstance().init();

       		PurgeWindowsThread.getInstance(windowMap, ((UIService) ServiceFactory.getService(ServiceType.UI)) .getPurgeTimerInterval());
       		log.debug("successfull");
        } catch (DataExchangeConfigureException e) {
    		log.error("failed:DataExchangeConfigureException:",e);
        	destroy();
        }
    	
    }
    public void destroy()
    {
    	System.out.print("gstMobile LoadOnStartup destroy..........");
//        DataExchangeService dei=null;
/*        try {
        	
//			dei =  (DataExchangeService)ServiceFactory.getService(ServiceType.DATAEXCHANGE);
//       		dei.shutdown();
       		
        } catch (DataExchangeConfigureException e) {
        	e.printStackTrace();
        }
*/ 
    	PurgeWindowsThread purge=PurgeWindowsThread.getInstance();
        if(purge!=null){
        	purge.stopPurgeWindowsThread();
        }
     	super.destroy();
		log.debug("successfull");
    }
	
}
