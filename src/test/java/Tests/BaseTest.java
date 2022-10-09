package Tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import constants.GeneralConstants;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.opera.OperaDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.testng.ITestResult;
import org.testng.annotations.*;
import utils.Log;
import utils.PropertiesFilesHandler;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class BaseTest {
    public static ExtentReports extent;
    public WebDriver driver;
    public ExtentHtmlReporter htmlReporter;
    public ExtentTest test;

    JavascriptExecutor jsDriver;


    //Initialize instances of properties files to be used in all tests
    PropertiesFilesHandler propHandler = new PropertiesFilesHandler();
    Properties generalConfigsProps = propHandler.loadPropertiesFile(GeneralConstants.GENERAL_CONFIG_FILE_NAME);

    // Browser's default download path config from properties file
    String browserDefaultDownloadPath = System.getProperty("user.dir") + generalConfigsProps.getProperty(GeneralConstants.DEFAULT_DOWNLOAD_PATH);
    String dateTime = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

    @BeforeSuite(description = "Setting up extent report", alwaysRun = true)
    @Parameters("browserType")
    public void setExtent(String browserType) {
        try {
            Log.info("Setting up extent report before test on browser: " + browserType);
            // get report file path
            String extentReportFilePath = generalConfigsProps.getProperty(GeneralConstants.EXTENT_REPORT_FILE_PATH);
            // specify location of the report
            htmlReporter = new ExtentHtmlReporter(System.getProperty("user.dir") + extentReportFilePath + dateTime + ".html");

            htmlReporter.config().setDocumentTitle(generalConfigsProps.getProperty(GeneralConstants.EXTENT_REPORT_TITLE)); // Tile of report
            htmlReporter.config().setReportName(generalConfigsProps.getProperty(GeneralConstants.EXTENT_REPORT_NAME)); // Name of the report
            htmlReporter.config().setTheme(Theme.DARK);

            extent = new ExtentReports();
            extent.attachReporter(htmlReporter);

            // Passing General information
            extent.setSystemInfo("Environment", "QA");
            extent.setSystemInfo("Browser", browserType);
        } catch (Exception e) {
            Log.error("Error occurred while setting up extent reports on " + browserType, e);
        }

    }

    @Parameters({"url", "browserType"})
    @BeforeClass(description = "Setting up selenium WebDriver before each class run", alwaysRun = true)
    public void loadConfiguration(String url, String browserType) {
        try {
            Log.info("Initialize Selenium WebDriver before tests' Class");

            // initialize selenium driver that is set as a config in testng.xml
            switch (browserType) {
                case ("Chrome"):
                    WebDriverManager.chromedriver().setup();
                    driver = new ChromeDriver(setChromeOption());
                    break;
                case ("Firefox"):
                    WebDriverManager.firefoxdriver().setup();
                    driver = new FirefoxDriver(setFireFoxOption());
                    break;
                case ("IE"):
                    WebDriverManager.iedriver().setup();
                    driver = new InternetExplorerDriver();
                    break;
                case ("Edge"):
                    WebDriverManager.edgedriver().setup();
                    driver = new EdgeDriver();
                    break;
                case ("Opera"):
                    WebDriverManager.operadriver().setup();
                    driver = new OperaDriver();
                    break;
            }

            // initialize angular WebDriver
            jsDriver = (JavascriptExecutor) driver;
            driver.manage().window().maximize();
            driver.get(url);
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

            Log.info("Selenium WebDriver was initialized successfully");
        } catch (Exception e) {
            Log.error("Error occurred while initializing selenium web driver", e);
        }

    }

    private ChromeOptions setChromeOption() {
        ChromeOptions options = new ChromeOptions();
        HashMap<String, Object> ChromePrefs = new HashMap<>();
        ChromePrefs.put("profile.default.content_settings.popups", 0);


        ChromePrefs.put("download.default_directory", browserDefaultDownloadPath);
        options.setExperimentalOption("prefs", ChromePrefs);
        options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);

        return options;
    }

    private FirefoxOptions setFireFoxOption() {
        FirefoxOptions option = new FirefoxOptions();
        option.addPreference("browser.download.folderlist", 2);
        option.addPreference("browser.download.dir", browserDefaultDownloadPath);
        option.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/octet-stream");
        option.addPreference("browser.download.manager.showWhenStarting", false);
        return option;
    }


    @AfterMethod(description = "Logging test status to log file and extent report", alwaysRun = true)
    public void logTestStatusForReport(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE) {
                Log.info("logging Testcase FAILED " + result.getName() + " in Extent Report");
                test.log(Status.FAIL, result.getName() + " HAS FAILED"); // to add name in extent report
                test.log(Status.FAIL, "EXCEPTION Thrown is " + result.getThrowable()); // to add error/exception in extent report

            } else if (result.getStatus() == ITestResult.SKIP) {
                Log.info("logging Testcase SKIPPED " + result.getName() + " in Extent Report");
                test.log(Status.SKIP, "Test Case SKIPPED is " + result.getName());
            } else if (result.getStatus() == ITestResult.SUCCESS) {
                Log.info("logging Testcase SUCCESS " + result.getName() + " in Extent Report");
                test.log(Status.PASS, "Test Case PASSED is " + result.getName());
            }
            // log that test case has ended
            Log.endTestCase(result.getName());
        } catch (Exception e) {
            Log.warn("Error occurred while logging testcase " + result.getName() + " result to extent report", e);
            e.printStackTrace();
        }
    }


    @AfterClass(description = "Quitting selenium driver after each class run", alwaysRun = true)
    public void closeDriver() {
        Log.info("Closing selenium WebDriver after Class");
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite(description = "Closing extent report after running all tests", alwaysRun = true)
    public void endReport() {
        try {
            Log.info("Closing Extent report after Suite");
            if (extent != null)
                extent.flush();
        } catch (Exception e) {
            Log.error("Error occurred while sending test report to recipients " + new Object() {
            }
                    .getClass()
                    .getName() + "." + new Object() {
            }
                    .getClass()
                    .getEnclosingMethod()
                    .getName(), e);
        }

    }
}
