package net.bloberry.async_ui.common;

import java.io.Serializable;

public class ObjectNotFoundException extends Exception  implements Serializable  {



	/**
	 * 
	 */
	private static final long serialVersionUID = -1390960177429197351L;

	public ObjectNotFoundException() {
		// TODO Auto-generated constructor stub
	}

	public ObjectNotFoundException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ObjectNotFoundException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ObjectNotFoundException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public ObjectNotFoundException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
