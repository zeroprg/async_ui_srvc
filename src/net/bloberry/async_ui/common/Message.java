package net.bloberry.async_ui.common;

public class Message {
	private String loginName;
	private Object message;

	public Message(String loginName, Object message) {
		this.loginName = loginName;
		this.message = message;
		
	}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public Object getMessage() {
		return message;
	}

	public void setMessage(Object message) {
		message = message;
	}

}
