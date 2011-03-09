package selenium;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.testng.ITestContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class SetupAndTearDown {
	private Configuration config;
	private Selenium selenium;

	@BeforeSuite
	public void setUp(ITestContext context) throws Exception {
		try {
			config = new PropertiesConfiguration("src/test/resources/configuration.properties");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		String host = config.getString("host") + ":"+ config.getString("port"); 
		System.out.println("Found host " + host);

		this.selenium = new DefaultSelenium("localhost", 4444, config.getString("browser"), host);
		selenium.start();
		context.setAttribute("seleniuminstance", selenium);
	}

	@AfterSuite
	public void tearDown() throws Exception {
		System.out.println("Stopping Selenium");
		selenium.stop();
	}
	
}
