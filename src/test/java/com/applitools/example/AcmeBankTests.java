package com.applitools.example;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.EyesRunner;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.TestResultsSummary;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.model.ChromeEmulationInfo;
import com.applitools.eyes.visualgrid.model.DesktopBrowserInfo;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import java.time.Duration;

public class AcmeBankTests {

    private final static BatchInfo BATCH = new BatchInfo("Selenium Java Basic Quickstart");

    public static void main(String [] args) {

        EyesRunner runner = null;
        Eyes eyes = null;
        WebDriver driver = null;

        try {
            // Configure Applitools SDK to run on the Ultrafast Grid
            runner = new VisualGridRunner(new RunnerOptions().testConcurrency(5));
            eyes = new Eyes(runner);
            Configuration config = eyes.getConfiguration();
            config.setApiKey(System.getenv("APPLITOOLS_API_KEY"));
            config.setBatch(BATCH);
            config.addBrowsers(
                new DesktopBrowserInfo(800, 1024, BrowserType.CHROME),
                new DesktopBrowserInfo(1600, 1200, BrowserType.FIREFOX),
                new DesktopBrowserInfo(1024, 768, BrowserType.SAFARI),
                new ChromeEmulationInfo(DeviceName.Pixel_2, ScreenOrientation.PORTRAIT),
                new ChromeEmulationInfo(DeviceName.Nexus_10, ScreenOrientation.LANDSCAPE)
            );
            eyes.setConfiguration(config);
            ChromeOptions options = new ChromeOptions().addArguments("--headless=new");
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            // Start Applitools Visual AI Test
            eyes.open(driver,"ACME Bank", "Selenium Java Basic: Quickstart", new RectangleSize(1200, 600));
            driver.get("https://sandbox.applitools.com/bank?layoutAlgo=true");

            // Full Page - Visual AI Assertion
            eyes.check(Target.window().fully().withName("Login page"));

            driver.findElement(By.id("username")).sendKeys("user");
            driver.findElement(By.id("password")).sendKeys("password");
            driver.findElement(By.id("log-in")).click();

            // Full Page - Visual AI Assertion
            eyes.check(
                Target.window().fully().withName("Main page")
                // Uncomment to apply Layout regions and have test pass
                /* .layout(
                    By.cssSelector(".dashboardOverview_accountBalances__3TUPB"),
                    By.cssSelector(".dashboardTable_dbTable___R5Du")
                ) */
            );

            // End Applitools Visual AI Test
            eyes.closeAsync();
        }
        catch (Exception e) {
            e.printStackTrace();
            if (eyes != null)
                eyes.abortAsync();
        } finally {
            if (driver != null)
                driver.quit();
            if (runner != null) {
                TestResultsSummary allTestResults = runner.getAllTestResults();
                System.out.println(allTestResults);
            }
            System.exit(0);
        }
    }
}