package Pages;

import constants.GeneralConstants;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.Log;

public class HomePage extends MainPage {
    @FindBy(id = "location-search")
    public WebElement searchInput;

    @FindBy(xpath = "//button//div//p")
    public WebElement autoCompleteFirstResult;

    WebDriverWait wait = new WebDriverWait(driver, 10);

    public HomePage(WebDriver driver) {
        super(driver);
    }

    public String searchAndCheckAutoCompleteResult(String searchKey) {
        try {
            Log.info("Search With " + searchKey);
            searchInput.sendKeys(Keys.CONTROL + "a");
            searchInput.sendKeys(Keys.DELETE);
            setTextValue(searchInput, searchKey);

            wait.until(ExpectedConditions.elementToBeClickable(autoCompleteFirstResult));
            if (autoCompleteFirstResult.getText().contains(searchKey)) {
                return GeneralConstants.SUCCESS;
            }
        } catch (Exception e) {
            Log.error("Error occurred in " + new Object() {
            }
                    .getClass().getName() + "." + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName(), e);
            return GeneralConstants.FAILED;
        }
        return GeneralConstants.SUCCESS;
    }
}
