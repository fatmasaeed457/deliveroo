package Tests;

import Pages.HomePage;
import constants.GeneralConstants;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import utils.Log;

public class AutoCompleteTest extends BaseTest {
    @Test(description = "Validate on Auto Complete", priority = 1, dataProvider = "searchDP", enabled = true)
    public void autoCompleteTest(String searchString) {

        test = extent.createTest("Validate on Auto Complete");
        Log.test = test;
        Log.startTestCase("Validate on Auto Complete");

        HomePage homePage = new HomePage(driver);
        String actual = homePage.searchAndCheckAutoCompleteResult(searchString);

        Log.info("Check that Auto Complete Passed Successfully");
        Assert.assertEquals(actual.toLowerCase(), GeneralConstants.SUCCESS.toLowerCase(),
                "Validate on Auto Complete" + GeneralConstants.FAILED);
        Log.info("Auto Complete Passed Successfully");
    }

    @DataProvider(name = "searchDP")
    public Object[][] searchDP() {
        return new Object[][]{{"EGYPT"}, {"egypt"}};
    }
}
