package net.bloberry.async_ui.common;

import java.io.Serializable;
import java.util.*;

public class User extends HashSet<String> implements Serializable{
	private String loginName;
	private String password;
	private String userName;
	private boolean authenticate=false;

	public User() {}

	public String getLoginName() {
		return loginName;
	}

	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public boolean isAuthenticate() {
		return authenticate;
	}

	public void setAuthenticate(boolean authenticate) {
		this.authenticate = authenticate;
	}
	
}
