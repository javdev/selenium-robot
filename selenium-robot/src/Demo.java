import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverBackedSelenium;
import org.openqa.selenium.firefox.FirefoxDriver;

import com.thoughtworks.selenium.Selenium;

import de.seleniumrobot.control.Page;


public class Demo {
	public static void main(String[] args) {
		WebDriver driver= new FirefoxDriver();
		Selenium sel= new WebDriverBackedSelenium(driver, "http://gmail.google.com");
		
		

		sel.open("");
		
		
		Page gMailSignInPage= new Page(sel);


		gMailSignInPage.typeAt("Username", "TestUser");
		gMailSignInPage.typeAt("Password", "Test Password");
		gMailSignInPage.clickOn("Sign in");
		

		gMailSignInPage.waitFor("The username or password you entered is incorrect.",60*1000);
		
		
		
		
		
		
		sel.close();
	}
	
	
	

}