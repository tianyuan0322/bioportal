package org.ncbo.stanford.manager.purl;

public interface PurlClientManager {

	public boolean createAdvancedPartialPurl(String path, String targetUrl);
	public boolean createSimplePurl(String path, String targetUrl);
	boolean doesPurlExist(String path);

	public String getHost();
	public void setHost(String host);

	public String getPort();
	public void setPort(String port);

	public String getUserName();
	public void setUserName(String userName) ;
	
	public String getPassword();
	public void setPassword(String password);

	public String getMaintainers();
	public void setMaintainers(String maintainers);
	
	public String getTargetUrlPrefix();
	public void setTargetUrlPrefix(String targetUrlPrefix);
	
	public boolean isConfigured();
	public void setConfigured(boolean configured) ;
}
