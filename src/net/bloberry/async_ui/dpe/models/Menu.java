package net.bloberry.async_ui.dpe.models;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import net.bloberry.async_ui.common.User;

public class Menu extends Window {


	public Menu(String wondowToken, User user) {
		super(wondowToken, user);
		log = Logger.getLogger(Menu.class);
	}

	@Override
	public void updateAndSendMessage(HttpServletRequest request,
			PrintWriter writer) {
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
