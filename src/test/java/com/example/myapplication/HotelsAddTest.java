package com.example.myapplication;

import com.example.entities.Hotel;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.Random;

public class HotelsAddTest extends BaseUITest {

    @Test
    public void testHotelsAdd() throws InterruptedException {
        mDriver.get(ENDPOINT);
        waitForLoading();

        addHotel(new Hotel("Hotel1", "Address1", 1, "Desc1", "www.1.com"));
        addHotel(new Hotel("Hotel2", "Address2", 2, "Desc2", "www.2.com"));
        addHotel(new Hotel("Hotel3", "Address3", 3, "Desc3", "www.3.com"));

    }

    private void addHotel(Hotel hotel) throws InterruptedException {
        mDriver.findElement(By.xpath("//span[contains(text(), 'Add hotel')]/parent::*/parent::*"))
                .click();
        waitForLoading();

        List<WebElement> fields = mDriver.findElements(By.tagName("tbody")).get(1).findElements(By.tagName("tr"));

        fields.get(0).findElement(By.tagName("input")).sendKeys(hotel.getName());
        waitForLoading();
        fields.get(1).findElement(By.tagName("input")).sendKeys(hotel.getAddress());
        waitForLoading();
        fields.get(2).findElement(By.tagName("input")).sendKeys(String.valueOf(hotel.getRating()));
        waitForLoading();

        mDriver.findElement(By.className("v-datefield-button")).click();
        mDriver.findElement(By.className("v-button-prevmonth")).click();
        mDriver.findElement(By.xpath("//span[contains(text(), '10')]/parent::*"))
                .click();
        waitForLoading();

        Select select = new Select(mDriver.findElement(By.className("v-select-select")));
        select.selectByIndex(new Random().nextInt(3) + 1);
        waitForLoading();

        mDriver.findElement(By.xpath("//label[contains(text(), 'Cash')]/parent::*"))
                .click();
        waitForLoading();

        fields.get(6).findElement(By.tagName("textarea")).sendKeys(hotel.getDescription());
        waitForLoading();

        fields.get(7).findElement(By.tagName("input")).sendKeys(hotel.getUrl());
        waitForLoading();

        mDriver.findElement(By.xpath("//span[contains(text(), 'Save')]/parent::*/parent::*"))
                .click();
        waitForLoading();
    }
}
