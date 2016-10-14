package net.bloberry.async_ui.dpe.models;

import java.io.PrintWriter;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import net.bloberry.async_ui.common.DataExchangeConfigureException;
import net.bloberry.async_ui.common.Message;
import net.bloberry.async_ui.common.User;
import net.bloberry.async_ui.services.ServiceFactory;
import net.bloberry.async_ui.services.ServiceType;
import net.bloberry.async_ui.services.ServicesEngine;
import net.bloberry.async_ui.services.UIService;

public class Messanger extends Window {

	private ServicesEngine proxy = null;
	private UIService uiService = null;
	
	private String loginName = null;
	

	private Queue<Message> myMsgs;
	private GroupArtifats groupArtifacts;
	

	
	public Messanger(String wondowToken, User user) {
		super(wondowToken, user);
		setExpirationTime(1000);
		log = Logger.getLogger(Messanger.class);
		try {
			proxy = ServicesEngine.getInstance();
			uiService = (UIService)ServiceFactory.getService(ServiceType.UI); 
			

			
		} catch (DataExchangeConfigureException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void updateAndSendMessage(HttpServletRequest request,
			PrintWriter writer) {
		
		String action=request.getParameter("action");
		
		
		populateLocalQueue(writer);   
	
		// UI send the message to all members of the group
		if ("message".equalsIgnoreCase(action)) 
		{
			


			resonseGroupList(writer);	
			this.group = request.getParameter("group");
			
			if( this.group != null && !this.group.isEmpty() ) 
			{	
	 			 this.private_=request.getParameter("private") == null? false: "true".equals(request.getParameter("private"));
				 String data = request.getParameter("data");
				 String _private_ = private_?"private_"+group:group;
				// initialise GroupArtifact 
				groupArtifacts  = (GroupArtifats) uiService.getObjectByGroup(_private_);
				if( groupArtifacts == null  ) // first initiate chat channel
				{
					groupArtifacts = new GroupArtifats(_private_);
				}
				groupArtifacts.addActiveMember(loginName);
				uiService.setObjectToGroup(_private_ , groupArtifacts);
				
				Queue<Message> msgQueue = groupArtifacts.getMsgQueue();

				try { 
					if( msgQueue.size()== GroupArtifats.MAXIMUM_MESSAGES_INQUEUE ) msgQueue.poll();
					msgQueue.add(new Message(loginName, data));
				} catch (java.lang.IllegalStateException e) {
					msgQueue.poll();
					msgQueue.add(new Message(loginName, data));
				}
				
				data = loginName + "|" + data;
				setUpdated();
			}
		}

		// UI remove itself from the group
		if ("remove".equalsIgnoreCase(action)) {
		//	List<Window> windows = this.getObjectByGroup(group);
		  // 	windows.remove(this);
		}
			
	}
	
	
	private void resonseGroupList(PrintWriter writer)
	{
		List<String> groups = uiService.getPublicGroupList();
		String json = "";// "group1|45,";
		for(String group : groups)
		{
			GroupArtifats groupArtifacts  = (GroupArtifats)uiService.getObjectByGroup(group);
			json += group + "|" + groupArtifacts.getActiveMembers() + ",";
		}
		 updateText("updateTextArea", "grouplist", json, writer);
	}

	private synchronized void populateLocalQueue(PrintWriter writer)
	{
		GroupArtifats groupArtifacts  = (GroupArtifats)uiService.getObjectByGroup(private_?"private_"+group:group);

		if (groupArtifacts != null && loginName != null)
		{
			Queue<Message> msgs_= groupArtifacts.getMsgQueue();
					msgs_.forEach((msg)->{
					if( myMsgs == null )
					{
						myMsgs = new ArrayBlockingQueue<Message>(GroupArtifats.MAXIMUM_MESSAGES_INQUEUE);
					}
					if(!myMsgs.contains(msg) && msg.getMessage() != null && !((String)msg.getMessage()).isEmpty()) {
						String data = msg.getLoginName() + "|" + (String) msg.getMessage();
						updateText("updateTextArea", "message", data, writer);
						
						try { 
							if( myMsgs.size() == GroupArtifats.MAXIMUM_MESSAGES_INQUEUE ) myMsgs.poll();
							myMsgs.add(msg);
						} catch (java.lang.IllegalStateException e) {
							myMsgs.poll();
							myMsgs.add(msg);
						}
					}
				});
	   }
	}

	//@Override
	//public void restoreState() {
	//@Override
	//public void flashState()
	
	@Override
	public void doAuthByAjaxCall(String loginName, String password, HttpServletRequest request, PrintWriter writer)
	{
		this.loginName = loginName;
		this.getUser().setUserName(loginName);
		this.getUser().setPassword(password);
		
		this.group = request.getParameter("group");
		this.private_ = request.getParameter("private") != null ? "true".equals(request.getParameter("private")) :false;
	
		updateCredentials(request, writer);
	}
	
	
	
	// DO not authenticate this model use own auth. when model instantiated 
	@Override
	public void doAuthentification(HttpServletRequest request, PrintWriter writer)
	{
		this.loginName = this.getUser().getLoginName();
		this.getUser().setUserName(loginName);
		updateCredentials( loginName, null, ( loginName != null ) , writer);
	}
	
	private void updateCredentials(HttpServletRequest request, PrintWriter writer){
		// UI register itself to the group
		//if("doAuthent".equalsIgnoreCase(action))...  already  catched in parent
   		 setWindowToGroup(group);
		//If there is login functionality already on this screen send logical authentification = true (user.getLoginName()+'Y')
		updateCredentials( loginName, null, ( loginName != null ) , writer);
	}




	@Override
	public void destroy() 
	{
	//	this.groupArtifacts.getUsers().remove(loginName);
		if ( groupArtifacts != null){
			this.groupArtifacts.removeActiveMember(loginName);
			if ( this.groupArtifacts.getActiveMembers() == 0 ) 
			{
				uiService.removeGroup(groupArtifacts.getName());
				this.groupArtifacts = null;
			}
		}
	}

}
