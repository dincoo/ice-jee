package org.ice.jee.spring.web.core.util;

import org.springframework.session.Session;

public interface IWebSession {

	public Session getSession() ;
	
	public Object getUser();
	
	public boolean isLogin();
	
}
