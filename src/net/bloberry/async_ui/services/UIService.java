package net.bloberry.async_ui.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.naming.AuthenticationException;

import org.apache.log4j.Logger;

import net.bloberry.async_ui.common.Constants;
import net.bloberry.async_ui.common.DataExchangeConfigureException;
import net.bloberry.async_ui.common.User;
import net.bloberry.async_ui.dpe.models.Window;

public class UIService implements Service {
	private static Logger log = Logger.getLogger(UIService.class);
	private static UIService uiService;
	private long lastWindowToken=System.currentTimeMillis();
	private  Map<String, Object> groupedObjects = Collections.synchronizedMap( new HashMap<String, Object>());
	private Map<String, Window> windowMap;
 


	private UIService() {
	}
	
	public static UIService getInstance() throws DataExchangeConfigureException {

		if(uiService==null){
			uiService=new UIService();
			uiService.configure();
		}
		return uiService;
	}
	
	
	public List<String> getPublicGroupList(){
		List<String> list = new ArrayList<String>(5);
		for (String group: groupedObjects.keySet())
		{
			if( !isPrivate(group) )
			{
				list.add(group);	
			}
		}
		return list;
	}

	private boolean isPrivate(String group) {
		return group.startsWith("private_");
	}

	
	private void configure() {
        if(windowMap==null)
        {
        	windowMap=new ConcurrentHashMap<String,Window>();
        }
	}
	
	
	// default UI service authentification
	@Override
	public User authenticate(String loginName, String password)
			throws AuthenticationException, DataExchangeConfigureException
	{
		// always authentificate this user
		User user = new User();
	    user.setLoginName(loginName);
	    user.setPassword(password);
		return user;
		//ServiceFactory.getService(ServiceType.DATAEXCHANGE).authenticate(loginName, password);
	}
	
	@Override
	public boolean getStatus() {
		// suggest it's always Ok
		return true;
	}
	
	public long getPurgeTimerInterval() throws DataExchangeConfigureException{
		try{
			return Integer.parseInt(ServicesEngine.getInstance().getProperty(ServicesEngine.EVENT_PURGE_TIMER_INTERVAL));
		}catch(NumberFormatException e){
			log.debug("event.purge.timer.interval not set. use default 10000");
			return 10000;
		}
	}
	public long getConnectionTimeout() throws DataExchangeConfigureException{
		try{
			return Integer.parseInt(ServicesEngine.getInstance().getProperty(ServicesEngine.EVENT_CONNECTION_TIMEOUT));
		}catch(NumberFormatException e){
			log.debug("event.connection.timeout not set. use default 10000");
			return 10000;
		}
	}
	
	public long getRetryInterval() throws DataExchangeConfigureException{
		try{
			return Integer.parseInt(ServicesEngine.getInstance().getProperty(ServicesEngine.EVENT_RETRY_INTERVAL));
		}catch(NumberFormatException e){
			log.debug("event.retry.interval not set. use default 500");
			return 500;
		}
	}
	
	public synchronized  String getNextWindowToken(){
		lastWindowToken++;
		return Long.toString(lastWindowToken);
	}


	// Methods responsible for grouping
	public Map<String, Object> getGroupedObject() {
		return groupedObjects;
	}
	
	public Object getObjectByGroup(String group) {
		return groupedObjects.get(group);
	}
	
	public void setObjectToGroup(String group, Object object) {		
		groupedObjects.put(group, object);
		
	}
	
	public void removeGroup(String group) {
		groupedObjects.remove(group);
	}

	public void removeAllGroups() {
		groupedObjects.clear();
	}


	public Map<String, Window> getWindowMap() {
		return windowMap;
	}

	public void setWindowMap(Map<String, Window> windowMap) {
		this.windowMap = windowMap;
	}

}
