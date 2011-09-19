package org.ncbo.stanford.manager.purl.impl;


public class CookieFactory {
	
	    private static String cookie;
	    
	public synchronized static void setCookie(String cookie) {
	        CookieFactory.cookie = cookie;
	    }
	    
	    public synchronized static String getCookie() {
	        return CookieFactory.cookie;
	    }

	}

