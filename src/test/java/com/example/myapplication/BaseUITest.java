package com.example.myapplication;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.concurrent.TimeUnit;

public class BaseUITest {

    public static final String ENDPOINT = "localhost:8080";

    protected WebDriver mDriver;

    public BaseUITest() {
        System.setProperty("webdriver.chrome.driver", "C:\\chromedriver.exe");
    }

    @Before
    public void initDriver() {
        mDriver = new ChromeDriver();
    }

    protected void waitForLoading() throws InterruptedException {
        mDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        Thread.sleep(1000);
    }

    @After
    public void tearDown() throws InterruptedException {
        waitForLoading();
        mDriver.quit();
    }
}
