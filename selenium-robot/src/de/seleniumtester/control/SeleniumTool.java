package de.seleniumtester.control;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.CommandProcessor;
import com.thoughtworks.selenium.DefaultSelenium;
import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;

public class SeleniumTool {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WebDriver driver= new FirefoxDriver();
		Selenium sel= new WebDriverBackedSelenium(driver, "http://www.google.com");
		
		

		sel.open("");
		
		
		SeleniumTool tool= new SeleniumTool(sel);
		tool.clickOn("English");

		tool.typeAt("Google Search", "Selenium is perfect for easy testing... NOW !");
		
		
		
		
		
		
		tool.typeAt("Google Search", "Find elements, even if the structure completely changed");

		
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		tool.clickOn("Google Search"); 
		
		sel.close();
	}

	private void typeAt(String whereToType, String whatToType) {
		
		List<String> locators= getButtonLocators(whereToType);
		String locator= getLocator(locators);
		
		locator+="//preceding::input[@type='text']";
		
		
		
		browser.highlight(locator);
		System.out.println("you tyed at: " + whereToType + ":>" + whatToType + ":<");
		browser.type(locator, whatToType);
		
	}

	private Selenium browser;
	
	public SeleniumTool(Selenium browserVal)  {
		browser= browserVal;
	}
	private List<String> getButtonLocators(String text) {
		ArrayList<String> locators= new ArrayList<String>();
		locators.add("//*[text()='"+text+"']");
		locators.add("//*[contains(text(),'"+text+"']");
		locators.add("//*[contains(@aria-label,'"+text+"')]");
		return locators;
	}
	
	public void clickOn(String text){
		List<String> locators=getButtonLocators(text);
		String locator= getLocator(locators);
		try {


			browser.highlight(locator);
			browser.click(locator);
		}catch(SeleniumException exc) {
			throw new SeleniumException("Failed to press locator " + locator,exc);
		}
		
	}
	
	private String getLocator (List<String> locators) {
		String result=null;
		for(int i=0; i< locators.size(); i++ ) {
			String candidate= locators.get(i);
			if(browser.isElementPresent(candidate)) {
				//+check visibility
				
				boolean hasInvisibleParent= browser.isElementPresent(candidate + "/ancestor-or-self::*[contains(@style,'display: none')]");

				System.out.println("checking visibility for: " + candidate + " is invisible: " + hasInvisibleParent);
				if(!hasInvisibleParent) {
				result= candidate;
				}
				
			}
		}
		if(result==null) {
			throw new SeleniumException("no matching locator found in:\n " + locators);
		}
		return result;
	}

	
	
}