package selenium;


import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;

public class FindOntology {
	private Selenium selenium;
	private Configuration config;

	@BeforeClass
	public void setUp() throws Exception {
		try {
			config = new PropertiesConfiguration("src/test/resources/configuration.properties");
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
		String host = config.getString("host") + ":"+ config.getString("port"); 
		System.out.println("Found host " + host);

		this.selenium = new DefaultSelenium("localhost", 4444, config.getString("browser"), host);
		selenium.start();
	}

	@Test
	public void search() throws Exception {
		selenium.open("/");
		selenium.click("find_ontology");
		selenium.type("find_ontology", "Body System");
		selenium.click("//input[@value='Explore']");
		selenium.waitForPageToLoad("30000");

		String target = "//a[text()='http://who.int/bodysystem.owl#BodySystem']";
		Assert.assertTrue(selenium.isElementPresent(target));
		
	}

	@Test (dependsOnMethods = { "search" })
	public void bodySystem() throws Exception {
		
		selenium.click("//li[@id='BodySystem']/img");
		Thread.sleep(2000);
		selenium.click("//li[@id='MusculoskeletalSystem']/img");
		Thread.sleep(2000);
		selenium.click("//li[@id='SceletalSystem']/span");
		Thread.sleep(2000);

		String target = "link=Musculoskeletal System";
		Assert.assertTrue(selenium.isElementPresent(target));
		selenium.click(target);

		selenium.waitForPageToLoad("30000");
	}

	
	@AfterClass
	public void tearDown() throws Exception {
		selenium.stop();
	}
}
