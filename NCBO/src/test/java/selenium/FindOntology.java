package selenium;


import org.testng.Assert;
import org.testng.ITestContext;
import org.testng.annotations.Test;

import com.thoughtworks.selenium.Selenium;

public class FindOntology {
	private Selenium selenium = null;

	@Test
	public void search(ITestContext context) throws Exception {
		selenium = (Selenium)context.getAttribute("seleniuminstance");
		System.out.println("Starting to search " + selenium);
		
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
}
