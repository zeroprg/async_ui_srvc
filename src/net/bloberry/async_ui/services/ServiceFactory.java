package net.bloberry.async_ui.services;

import net.bloberry.async_ui.common.DataExchangeConfigureException;

public class ServiceFactory {

	
	public static Service getService(ServiceType serviceType) throws DataExchangeConfigureException {
		Service service = null;
		switch(serviceType) 
		{
		case DATAEXCHANGE:
			// = (Service) DataExchangeService.getInstance();
		    break;
		    
		case ARGOSERVICE:
			//service = (Service) ArgoService.getInstance();
		    break;
		    
		case UI:
			service = (Service) UIService.getInstance();
		    break;	
    
		    
		default:
			break;
	   }	
			
  	    return service;
	}
	

}
