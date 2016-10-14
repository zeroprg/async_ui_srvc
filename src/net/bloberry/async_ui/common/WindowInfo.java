package net.bloberry.async_ui.common;

import net.bloberry.async_ui.dpe.utils.SafeHtml;

public class WindowInfo {
	private String windowToken;
	private String windowClass;
	private String windowUI;
	private String userName;
	private long connectedTime;
	private boolean disposed;
	private long createdTime;

	public WindowInfo() {
		// TODO Auto-generated constructor stub
	}
	
	
    public String getWindowToken() {
		return SafeHtml.htmlEscape(windowToken, false);
	}


	public void setWindowToken(String windowToken) {
		this.windowToken = windowToken;
	}


	public String getWindowClass() {
		return SafeHtml.htmlEscape(windowClass, false);
	}


	public void setWindowClass(String windowClass) {
		this.windowClass = windowClass;
	}


	public String getUserName() {
		return SafeHtml.htmlEscape(userName, false);
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String isConnected() {
		return SafeHtml.htmlEscape(Long.toString((	System.currentTimeMillis()-connectedTime)/1000)+" sec", false);
	}


	public void setConnected(long connectedTime) {
		this.connectedTime = connectedTime;
	}


	public String isDisposed() {
		return SafeHtml.htmlEscape(disposed?"Yes":"No", false);
	}


	public void setDisposed(boolean disposed) {
		this.disposed = disposed;
	}


	public String getElapsedTime() {
		return SafeHtml.htmlEscape(Long.toString((	System.currentTimeMillis()-createdTime)/1000)+" sec", false);
	}


	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}


	public String getWindowUI() {
		return SafeHtml.htmlEscape(windowUI,false);
	}


	public void setWindowUI(String windowUI) {
		this.windowUI = windowUI;
	}


	public String toString(){
		String ret="";
		ret=ret+windowToken+"\t";
		ret=ret+windowUI+"\t";
		ret=ret+windowClass+"\t";
		ret=ret+userName+"\t";
		ret=ret+(isConnected())+"\t";
		ret=ret+(isDisposed())+"\t";
		ret=ret+getElapsedTime();
		return ret;
	}
	public static String toStringNames(){
		String ret="";
		ret=ret+"Window"+"\t";
		ret=ret+"UI"+"\t";
		ret=ret+"Model"+"\t";
		ret=ret+"User"+"\t";
		ret=ret+"Connected"+"\t";
		ret=ret+"Disposed"+"\t";
		ret=ret+"Lifetime";
		
		return ret;
	}
	public String toJson(){
		return "[\"" + getWindowToken() + "\",\"" + getWindowUI()  + "\",\"" + getWindowClass()  + "\",\"" + getUserName()  + "\",\"" + isConnected()  + "\",\"" + isDisposed() + "\",\"" + getElapsedTime()  + "\"]";  
		
	}
}
