package net.bloberry.async_ui.services;

import javax.naming.AuthenticationException;

import net.bloberry.async_ui.common.DataExchangeConfigureException;
import net.bloberry.async_ui.common.User;

public interface Service {
	public User authenticate(String loginName,String password) throws AuthenticationException, DataExchangeConfigureException;
	public boolean getStatus();
}
