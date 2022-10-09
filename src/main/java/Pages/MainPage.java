package Pages;

import constants.GeneralConstants;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

public class MainPage {

    // Initialize web drivers
    public WebDriver driver;
    public JavascriptExecutor jsDriver;

    public MainPage(WebDriver driver) {
        this.driver = driver;
        this.jsDriver = (JavascriptExecutor) driver;

        //Set a delay of 30 secs to wait for elements' visibility
        AjaxElementLocatorFactory factory = new AjaxElementLocatorFactory(driver, 10);
        PageFactory.initElements(factory, this);
    }

    public void setTextValue(WebElement inputText, String testDataText) {
        if ((!testDataText.isEmpty() && testDataText != null) && inputText.getAttribute("aria-invalid").equalsIgnoreCase(GeneralConstants.FALSE)) {
            new Actions(driver).moveToElement(inputText).perform();
            inputText.clear();
            inputText.sendKeys(testDataText);
        }
    }

    public void scrollToElement(WebElement element) {
        jsDriver.executeScript("arguments[0].scrollIntoView();", element);
    }

}
