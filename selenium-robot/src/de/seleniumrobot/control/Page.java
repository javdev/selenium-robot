package de.seleniumrobot.control;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.thoughtworks.selenium.Selenium;
import com.thoughtworks.selenium.SeleniumException;


public class Page {

	public void typeAt(String whereToType, String whatToType) {
		List<String> locators= getTextLocators(whereToType);
		String locator= getLocator(locators);
	
		locator+="//following::input[@type='text' or @type='email' or @type='password']";

		browser.highlight(locator);
		browser.type(locator, whatToType);

	}
	
		/**
		 * @param args
		 */
	
		public void typeBeforeButton(String whereToType, String whatToType) {
			
			List<String> locators= getTextLocators(whereToType);
			String locator= getLocator(locators);
			
			locator+="//preceding::input[@type='text' or @type='email' or @type='password']";
			
			
			browser.highlight(locator);
			browser.type(locator, whatToType);
			
		}

		private Selenium browser;
		
		public Page(Selenium browserVal)  {
			browser= browserVal;
		}
		private List<String> getTextLocators(String text) {
			ArrayList<String> locators= new ArrayList<String>();
			locators.add("//*[text()='"+text+"']");
			locators.add("//*[contains(text(),'"+text+"']");
			locators.add("//*[contains(@aria-label,'"+text+"')]");
			//+now button texts (value element)
			locators.add("//input[@type='submit' and contains(@value,'"+text+"')]");
			return locators;
		}
		
		public void clickOn(String text){
			List<String> locators=getTextLocators(text);
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
		
		class Searching extends Thread{
			boolean found=false;
			
			boolean hasFound() {
				return found;
			}
		}

		public void waitFor(final String text, final long timeout) {
			Searching t= new Searching() {
				
				public void run() {
					Date startingTime= new Date();
					String locator= "//*[contains(normalize-space(text()),'"+text+"')]";
					
					while(!found && timeout> new Date().getTime() -startingTime.getTime() ) {
						boolean isTextPresent= browser.isElementPresent(locator);
						found= isTextPresent;
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					System.out.println("locator: " + locator + " yielded result: " + found);
				}
			};
			List<Searching> searchingThreads= new ArrayList<Searching>();
			List<Exception> occurredExceptions= new ArrayList<Exception>();
			
			//+add all searching threads to the list of searching threads
			searchingThreads.add(t);
			//+start all searching threads
			for(int i=0; i< searchingThreads.size(); i++) {
				Searching candidate= searchingThreads.get(i);
				candidate.start();
			}
			//+join all threads
			try {
				for(int i=0; i< searchingThreads.size(); i++) {
					Searching candidate= searchingThreads.get(i);
					candidate.join();
				}
			} catch (InterruptedException e) {
				//+we catch it here. Shouldn't happen in a testing environment.
				//+if it happens we mark this step in the test as a failure
				//+as only one thread failed, another might have found the text, we have to remember the failed threads and their reasons:
				
				occurredExceptions.add(e);
			}
			boolean found=false;
			for(int i=0; i< searchingThreads.size(); i++) {
				Searching candidate= searchingThreads.get(i);
				found= found || candidate.hasFound();
				if(found) {
					break;
				}
			}
			if(!found) {
				String errorMessagesOfNotProperlyEndedThreads="Following threads did not terminate properly: ";
				for(int i=0; i< occurredExceptions.size(); i++) {
					errorMessagesOfNotProperlyEndedThreads += " \n " + occurredExceptions.get(i).getMessage() + " " +occurredExceptions.get(i).getStackTrace();
				}
				throw new SeleniumException("Text: " + text + " could not be found." + errorMessagesOfNotProperlyEndedThreads);
			}
		}
		
	
}