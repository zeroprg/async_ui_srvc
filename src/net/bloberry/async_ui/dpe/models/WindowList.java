package net.bloberry.async_ui.dpe.models;

import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import net.bloberry.async_ui.common.DataExchangeConfigureException;
import net.bloberry.async_ui.common.NoPrivilegesException;
import net.bloberry.async_ui.common.User;
import net.bloberry.async_ui.common.WindowInfo;
import net.bloberry.async_ui.common.WindowInfoList;
import net.bloberry.async_ui.dpe.MessageSeverity;
import net.bloberry.async_ui.services.ServiceFactory;
import net.bloberry.async_ui.services.ServiceType;
import net.bloberry.async_ui.services.ServicesEngine;
import net.bloberry.async_ui.services.UIService;

public class WindowList extends Window {

	private String environment=null;
	//private WindowList workList=null;
	private boolean wasFlashed=false;
	private boolean serviceConnection=false;
	private WindowInfoList windowList=new WindowInfoList();

	public WindowList(String wondowToken, User user) {
		super(wondowToken, user);
		log = Logger.getLogger(WindowList.class);
		setExpirationTime(10000);
	}

	@Override
	public void updateAndSendMessage(HttpServletRequest request,
			PrintWriter writer) {

		ServicesEngine proxy;
		try {
			 proxy = ServicesEngine.getInstance();
			} catch (DataExchangeConfigureException e) {
			// connection problem exception 
			e.printStackTrace();
			sendErrorMessage(writer,e);
			return;
		}



		boolean workListChanged=false;

		WindowInfoList updatedWindowList=null;

		try {
			updatedWindowList = getWindowInfoList(user, request);

		}
		catch (NoPrivilegesException e) {
			log.debug(e.getMessage());
			updateMessageInfo(e.getMessage(),MessageSeverity.ERROR,writer);
			return;
		}
		catch (DataExchangeConfigureException e) {
			log.debug(e.getMessage());
			updateMessageInfo(e.getMessage(),MessageSeverity.ERROR,writer);
			return;
		}			

		
		if(!isEquals(windowList,updatedWindowList)){
			windowList=updatedWindowList;
			if( windowList == null || windowList.size() == 0) {
				updateJSONObject("updateTable", "table", "[]", writer);
			} else {
				updateJSONObject("updateTable", "table", windowList.convertWindowInfoToJson(), writer);
			}
			workListChanged=true;
		}

	}

	@Override
	public void restoreState() {
		environment=null;
		windowList.clear();
		serviceConnection=false;
		wasFlashed=true;
	}

	@Override
	public void flashState() {
		environment=null;
		wasFlashed=true;
		serviceConnection=false;
		windowList.clear();
	}
	
	
	
	public WindowInfoList getWindowInfoList(User user, HttpServletRequest request) throws NoPrivilegesException, DataExchangeConfigureException{
		WindowInfoList ret=new WindowInfoList();
		UIService uiService = (UIService)ServiceFactory.getService(ServiceType.UI); 
		Iterator<Window> iter = uiService.getWindowMap().values().iterator();
		while (iter.hasNext()){
			Window window=iter.next();
			WindowInfo wi=new WindowInfo();
			wi.setWindowToken(window.getWindowToken());
			wi.setWindowUI(window.getUI());
			wi.setWindowClass(window.getClass().getSimpleName());
			wi.setUserName(window.getUser().getLoginName());
			wi.setConnected(window.getLastConnectedTime());
			wi.setDisposed(window.isDisposed());
			wi.setCreatedTime(window.getCreatedTime());
			ret.add(wi);
		}
		return ret;
	}
	
	protected boolean isEquals(WindowInfoList listOne,WindowInfoList listTwo){
		if(listOne==null && listTwo==null){
			return true;
		}else if((listOne==null && listTwo!=null)||(listOne!=null && listTwo==null)){
			return false;
		}else if(listOne.equals(listTwo)){
			return true;
		}else{
			return false;
		}
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
