package net.bloberry.async_ui.dpe.models;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import net.bloberry.async_ui.common.DataExchangeConfigureException;
import net.bloberry.async_ui.common.User;
import net.bloberry.async_ui.services.ServicesEngine;

public class ServicesStatus extends Window {
	private String environment=null;
	private boolean wasFlashed=false;

	public ServicesStatus(String wondowToken, User user) {
		super(wondowToken, user);
		log = Logger.getLogger(ServicesStatus.class);
	}

	@Override
	public void updateAndSendMessage(HttpServletRequest request,
			PrintWriter writer) {

		//START OF COMMON PART 
		ServicesEngine proxy = null;
		try {
			proxy = ServicesEngine.getInstance();
		} catch (DataExchangeConfigureException e) {
			e.printStackTrace();
			sendErrorMessage(writer,e);
		}
		
		
		String action=request.getParameter("action");
		if("restoreState".equalsIgnoreCase(action)){
			restoreState();
		}	
	

		if(environment==null){
			environment=proxy.getEnviromentName();
		    // update element inner HTML
			updateText("environment",environment,writer);
			updateText("connectionLost",proxy.getProperty(ServicesEngine.CONNECTION_LOST_MESSAGE),writer);
		}
	    //END  OF COMMON PART 
	

  	    
  	    String startTime = proxy.getElapsedTime();
  		updateText("startTime",startTime,writer);
		updateText("environment",environment,writer);
		String statusIco;
/*		try {
			//ArgoService argoService = (ArgoService) ServiceFactory.getService(ServiceType.ARGOSERVICE);
			// =(argoService.getStatus()?"greenDot.ico":"redDot.ico");
		
		} 
		catch (DataExchangeConfigureException e) {
			statusIco = "redDot.ico";
		}*/
	//	updateAttributes("argoService","[\"src=../Images/" + statusIco + "\"]",writer);
/*
		try {
			//DataExchangeService dee = (DataExchangeService) ServiceFactory.getService(ServiceType.DATAEXCHANGE);
			//statusIco = (dee.getStatus()?"greenDot.ico":"redDot.ico");		} 
		catch (DataExchangeConfigureException e) {
			statusIco = "redDot.ico";
		}*/

	//	 updateAttributes("deeService","[\"src=../Images/" + statusIco + "\"]",writer);

		//START OF COMMON PART 
		wasFlashed=false;
		//END OF COMMON PART
	
	}

	@Override
	public void restoreState() {
		environment=null;
	}

	@Override
	public void flashState() {
		environment=null;
		wasFlashed=true;
	}

	// do the same authentification as Ajax call from client  when initializing the model
	@Override
	void doAuthentification(HttpServletRequest request, PrintWriter writer) {
		String loginName = user.getLoginName();
		String password = user.getPassword(); 
		super.doAuthByAjaxCall(loginName, password, request, writer);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		
	}
	
}
