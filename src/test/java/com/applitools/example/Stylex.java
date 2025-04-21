package com.applitools.example;

import com.applitools.eyes.selenium.Eyes;
import com.applitools.eyes.RectangleSize;
import com.applitools.eyes.selenium.BrowserType;
import com.applitools.eyes.selenium.Configuration;
import com.applitools.eyes.selenium.fluent.Target;
import com.applitools.eyes.visualgrid.services.VisualGridRunner;
import com.applitools.eyes.visualgrid.model.ChromeEmulationInfo;
import com.applitools.eyes.visualgrid.model.DesktopBrowserInfo;
import com.applitools.eyes.visualgrid.model.DeviceName;
import com.applitools.eyes.visualgrid.model.ScreenOrientation;
import com.applitools.eyes.visualgrid.services.RunnerOptions;
import com.applitools.eyes.BatchInfo;
import com.applitools.eyes.TestResultsSummary;

import io.github.bonigarcia.wdm.WebDriverManager;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.time.Duration;

public class Stylex {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        VisualGridRunner runner = new VisualGridRunner(new RunnerOptions().testConcurrency(5));
        Eyes eyes = new Eyes(runner);
        WebDriver driver = null;

        try {
            WebDriverManager.chromedriver().setup();
            ChromeOptions options = new ChromeOptions();
            // Comment out headless to view browser
            options.addArguments("--headless=new");
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            // Applitools configuration
            Configuration config = new Configuration();
            config.setApiKey("AqXKP8VJ2hm2q110U5NwL4YblUhRHc1qFt48it52qZ0d8110"); // Replace with your key
            config.setBatch(new BatchInfo("Wipro Stylex"));
             config.addBrowsers(
                new DesktopBrowserInfo(800, 1024, BrowserType.CHROME),
                new DesktopBrowserInfo(1600, 1200, BrowserType.FIREFOX),
                new DesktopBrowserInfo(1024, 768, BrowserType.SAFARI),
                new ChromeEmulationInfo(DeviceName.Pixel_2, ScreenOrientation.PORTRAIT),
                new ChromeEmulationInfo(DeviceName.Nexus_10, ScreenOrientation.LANDSCAPE)
            );
            eyes.setConfiguration(config);

            // Load JSON with page names as keys and URLs as values
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> urlMap = mapper.readValue(new File("url.json"), Map.class);

            for (Map.Entry<String, String> entry : urlMap.entrySet()) {
                String pageName = entry.getKey();
                String url = entry.getValue();

                System.out.println("Navigating to: " + pageName + " => " + url);
                eyes.open(driver, "Stylex Project", pageName, new RectangleSize(1366, 768));
                driver.get(url);
                Thread.sleep(3000); // Optional wait for page to load
                eyes.check(Target.window().fully().withName(pageName)); 
                eyes.closeAsync();
            }

        } catch (Exception e) {
            e.printStackTrace();
            eyes.abortAsync();
        } finally {
            if (driver != null)
                driver.quit();
            TestResultsSummary allTestResults = runner.getAllTestResults();
            System.out.println(allTestResults);
        }
    }
}