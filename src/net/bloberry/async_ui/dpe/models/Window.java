package net.bloberry.async_ui.dpe.models;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import javax.naming.AuthenticationException;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import net.bloberry.async_ui.common.Constants;
import net.bloberry.async_ui.common.DataExchangeConfigureException;
import net.bloberry.async_ui.common.User;
import net.bloberry.async_ui.common.WorkList;
import net.bloberry.async_ui.dpe.MessageSeverity;
import net.bloberry.async_ui.dpe.utils.SafeHtml;
import net.bloberry.async_ui.services.ServiceFactory;
import net.bloberry.async_ui.services.ServiceType;
import net.bloberry.async_ui.services.ServicesEngine;
import net.bloberry.async_ui.services.UIService;

public abstract class Window {
	protected String windowToken=null;;
	protected User user=null;
	
	private long lastUpdatedTime=0;
	private long expirationTime=-2;
	private boolean forceToUpdate=false;
	
	private long lastConnectedTime=0;
	private long connectTimeout=0;
	private long retryInterval=0;
	private boolean isRetryIntervalSet=false;
	
	private boolean forceToDispose =false;
	
	private String environment=null;
	
    public static Logger log=null ;
	private UIService uiService; // very stable no connection required to run
	protected String group="undefined";
	protected boolean private_ = false;
	private boolean authenticate = false;
	private ServicesEngine proxy = null;
	private String ui;
	private long createdTime=System.currentTimeMillis();

	public Window(String wondowToken,User user) {
		this.windowToken=wondowToken;
		this.user=user;
    	setExpirationTime(10000);
		try {

			 uiService = ((UIService)ServiceFactory.getService(ServiceType.UI));
			 connectTimeout = uiService.getConnectionTimeout();
 			 proxy = ServicesEngine.getInstance();
		} catch (DataExchangeConfigureException e) {
			return;
		}
	
	
	}


	public User authenticate(String loginName, String password) throws DataExchangeConfigureException, AuthenticationException {
			return (user = uiService.authenticate(loginName, password));
	}
	

	public static Logger getLog(){
		return log;
	}
	
	public boolean isExpired(){
		if(forceToUpdate){
			return true;
		}else if(expirationTime==-1){
			return false;
		}else if((System.currentTimeMillis()-lastUpdatedTime)>expirationTime){
			return true;
		}else{
			return false;
		}
	}
	
	public void setExpirationTime(long expirationTime) {
		this.expirationTime = expirationTime;
	}
	public void forceToUpdate(){
		forceToUpdate=true;
	}
	public void setUpdated(){
		forceToUpdate=false;
		lastUpdatedTime=System.currentTimeMillis();
	}
	
	public boolean isDisposed() {
		if(forceToDispose){
			return true;
		}else if(connectTimeout==-1){
			return false;
		}else if((System.currentTimeMillis()-lastConnectedTime)>connectTimeout){
			return true;
		}else{
			return false;
		}
	}
	
	public void forceToDispose() {
		forceToDispose=true;
	}
	
	public void setConnected(){
		lastConnectedTime=System.currentTimeMillis();
	}

	public void resetRetryInterval() {
		isRetryIntervalSet=false;
	}
	public void setRetryInterval() throws DataExchangeConfigureException {
		 retryInterval=uiService.getRetryInterval();
	}
	
	public void setRetryIntervalSet(boolean isRetryIntervalSet) {
		this.isRetryIntervalSet = isRetryIntervalSet;
	}
	public User getUser() {
		return user;
	}
	public void sendDisposedMessage(PrintWriter writer){
		writer.write("event: disposal\n");
		writer.write("data: "+ "Your window is disposed. please disconnect and close" +"\n\n" );
		writer.flush();
	}
	public void sendRetryMessage(PrintWriter writer){
		writer.write("retry: "+retryInterval+"\n\n");
		writer.flush();
	}
	
	public void sendErrorMessage(PrintWriter writer,DataExchangeConfigureException dece){
		 updateMessageInfo("Some problems on server. please contact ISD. "+dece.toString(),
				MessageSeverity.FAIL, writer); 
	}
	
	public void superUpdateAndSendMessage(HttpServletRequest request,PrintWriter writer) throws DataExchangeConfigureException{
		if(!isRetryIntervalSet){
			setRetryInterval();
			sendRetryMessage(writer);
			isRetryIntervalSet=true;
		}
		
		String action=request.getParameter("action");
		
		if("restoreState".equalsIgnoreCase(action)){
			restoreState();

		}	
	
		// Initializations starts
		if(environment==null){

			doAuthentification(request, writer);			
			
			environment=proxy.getEnviromentName();
			updateText("environment",environment,writer);
			updateText("connectionLost",proxy.getProperty(ServicesEngine.CONNECTION_LOST_MESSAGE),writer);
		}
		
		
		if ("doAuthent".equalsIgnoreCase(action)) {
			String loginName = request.getParameter(Constants.LOGINNAME);
			String password = request.getParameter(Constants.PASSWORD);
			doAuthByAjaxCall(loginName, password, request, writer);
		} 
		else 
		{			
			updateAndSendMessage(request,writer);
		}
		  
		setUpdated();
		
	}

	abstract void doAuthentification( HttpServletRequest request, PrintWriter writer);
	
	
	protected void doAuthByAjaxCall(String loginName, String password, HttpServletRequest request, PrintWriter writer) {
		try {
			 User user = uiService.authenticate(loginName, password);
			 user.setAuthenticate(true);
			 this.setAuthenticate(true);
			 this.user = user;
			
		} catch (AuthenticationException e) {
			 user.setAuthenticate(false);
			 this.setAuthenticate(false);
			 this.updateMessageInfo("Authentification failed for login:" +  loginName +" password:" + password, MessageSeverity.ERROR, writer);

		} catch (DataExchangeConfigureException e) {
		  // Only can happened when service is not available	
		 	updateMessageInfo(e.getMessage(), MessageSeverity.ERROR, writer);
		}
		
		 updateCredentials( user.getLoginName(), user.getPassword(),user.isAuthenticate(), writer);

	}
	
	
	public void setAuthenticate(boolean b) {
		this.authenticate = true;
		
	}
	public boolean isAuthenticate() {
		return authenticate;
	}
	



	public abstract void updateAndSendMessage(HttpServletRequest request,PrintWriter writer);
	protected boolean isEquals(ArrayList<String> listOne,ArrayList<String> listTwo){
		if(listOne==null && listTwo==null){
			return true;
		}else if((listOne==null && listTwo!=null)||(listOne!=null && listTwo==null)){
			return false;
		}else if(listOne.containsAll(listTwo)&&listTwo.containsAll(listOne)){
			return true;
		}else{
			return false;
		}
	}
	protected boolean isEquals(WorkList listOne,WorkList listTwo){
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

	protected void updateBox (String id,String text,PrintWriter writer) {
		if(id==null) throw new NullPointerException();
		text = (text == null) ? "": text;
		text=text.replaceAll("\n","<br>");
		text = SafeHtml.htmlEscape(text, false);
	    writer.write("event: updateBox\n");
	    writer.write("id: "+id+"\n");
	    writer.write("data: "+ text +"\n\n" );
	    writer.flush();
	}

	
	protected void updateText (String id,String text,PrintWriter writer) {
		if(id==null) throw new NullPointerException();
		text = (text == null) ? "": text;
		text=text.replaceAll("\n","<br>");
		text = SafeHtml.htmlEscape(text, false);
	    writer.write("event: updateText\n");
	    writer.write("id: "+id+"\n");
	    writer.write("data: "+ text +"\n\n" );
	    writer.flush();
	}
	
	protected void updateAttributes (String id,String text,PrintWriter writer) {
		if(id==null) throw new NullPointerException();
		text = (text == null) ? "": text;
		text=text.replaceAll("\n","<br>");
	    writer.write("event: updateAttributes\n");
	    writer.write("id: "+id+"\n");
	    writer.write("data: "+ text +"\n\n" );
	    writer.flush();
	}

	
	
	
	protected void updateListBox (String id,ArrayList<String> items, PrintWriter writer) {
		if(id==null) throw new NullPointerException();
		String result = null;
		if(items != null && !items.isEmpty() ){
			result = convertToJson(items);
		} else {
			result = "[]";
		}
	    writer.write("event: updateListBox\n" );
	    writer.write("id: "+id+"\n");
	    writer.write("data: "+ result + "\n\n" );
	    
	    writer.flush();
	    log.debug("event: updateListBox:id="+id +":result="+result);
	}
	protected void updateListBoxSelection(String id,String item,PrintWriter writer) {
		if(id==null) throw new NullPointerException();
		item = (item == null) ? "": item;
		item = SafeHtml.htmlEscape(item, false);
	    writer.write("event: updateListBoxSelection\n" );
	    writer.write("id: "+id+"\n");
	    writer.write("data: "+ item + "\n\n" );
	    writer.flush();
	}
	protected void updateText(String eventName,String id,String item, PrintWriter writer) {
		// id optional as soon can update multiple ids
		item = (item == null) ? "": item;
		//item = SafeHtml.htmlEscape(item, false);
	    writer.write("event: " + eventName+ "\n" );
	    if(id != null)  writer.write("id: "+id+"\n");
	    writer.write("data: "+ item + "\n\n" );
	    writer.flush();
	}

	
    /**
     * Send message with Severity to the client
     * @param messageInfo
     * @param messageSeverity
     * @param writer
     */
	public void updateMessageInfo(String messageInfo,
			MessageSeverity messageSeverity, PrintWriter writer) {
		log.debug("Message Info: " + messageInfo);
		messageInfo = (messageInfo == null) ? "": messageInfo;
		messageInfo=messageInfo.replaceAll("\n", "<br>");
		writer.write("event: msg\n");
		writer.write("data: " + "{\"message\":\"" + messageInfo
				+ "\" , \"severity\":\"" + messageSeverity.name() + "\"}\n\n");
		writer.flush();
	}
	/**
	 * Send JSON to  with Severity to the client
	 * @param id
	 * @param item
	 * @param writer
	 */
	protected void updateJSONObject(String eventName, String id,String item,PrintWriter writer) {
		if(id==null) throw new NullPointerException();
		log.debug(" item: " + item);
		item = (item == null) ? "": item;
	    writer.write("event: "+ eventName + "\n" );
	    writer.write("id: "+id+"\n");
	    writer.write("data: "+ item + "\n\n" );
	    writer.flush();
	}
	
	
	
		

	/**
 *  Send credentials event only during refreshing or first time 	
 * @param loginName
 * @param password
 * @param writer
 */
	public void updateCredentials( String loginName, String password,boolean authenticated, PrintWriter writer) {	
	    writer.write("event: refreshCredentials\n" );
	    writer.write("id: "+ (loginName==null?"":loginName)+(authenticated?'Y':'N') + "\n" );
	    writer.write("data: "+ (password==null?"":password) + "\n\n" );
	    writer.flush();
	}
	
	
	public static void requestWindowClass( PrintWriter writer) {	
	    writer.write("event: requestWindowClass\n" );
	    writer.write("data: requestWindowClass\n\n" );
	    writer.flush();
	    
	}

	

	public void restoreState() {
		environment=null;
	}

	public void flashState() {
		environment=null;
	}
	
	
	/**
	 * Generate array of JSON which represent the ArrayList<String> 	
	 * @param pows
	 * @return
	 */
	protected String convertToJson(List<String> items){
    String result = ""; 
    for (int index = 0; index < items.size() ; index++) 
    {
    	String item = items.get(index);
        result +=  "\"" + SafeHtml.htmlEscape( item, false)  +  (index==(items.size()-1)? "\"" : "\",");
    }
    if( !result.isEmpty() ) 
    {
    	result =  "[" +  result  + "]";
    }
    return result;
	}
	
	public String getAllParameters(ServletRequest request){
		StringBuffer ret=new StringBuffer();
		Enumeration<String> enumer= request.getParameterNames();
		while(enumer.hasMoreElements()){
			String name=enumer.nextElement();
			if(ret.length()==0){
				ret.append(name+"="+request.getParameter(name));
			}else{
				ret.append(":"+name+"="+request.getParameter(name));
			}
		}
		
		return ret.toString();
	}

	
	protected void setWindowToGroup(String group){
	   this.group = group;	
	}
	
	public long getLastConnectedTime() {
		return lastConnectedTime;
	}
	public String getUI() {
		return ui;
	}
	public void setUI(String ui) {
		this.ui = ui;
	}
	public long getCreatedTime() {
		return createdTime;
	}
	
	public String getWindowToken() {
		return windowToken;
	}
	/*

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((windowToken == null) ? 0 : windowToken.hashCode());
		return result;
	}
	

	@Override
	public boolean equals(Object  window) {
		return window != null && ((Window)window).windowToken.equals(this.windowToken) && this.hashCode() == window.hashCode();
	}
	*/
    /**
     * Clean all window's references ( used before window disposal)
     */
	public abstract void destroy();


	public boolean isPrivate() {
		return private_;
	}
}
