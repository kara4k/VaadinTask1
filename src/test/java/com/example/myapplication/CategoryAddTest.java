package com.example.myapplication;

import org.junit.Test;
import org.openqa.selenium.By;

public class CategoryAddTest extends BaseUITest {

    @Test
    public void testCategoryAdd() throws InterruptedException {
        navigateCategories();
        addCategory("Category1");
        addCategory("Category2");
        addCategory("Category3");
    }

    private void navigateCategories() throws InterruptedException {
        mDriver.get(ENDPOINT);
        waitForLoading();
        mDriver.findElement(By.xpath("//span[contains(text(), 'Category')]/parent::*"))
                .click();
        waitForLoading();
    }

    private void addCategory(String name) throws InterruptedException {
        mDriver.findElement(By.xpath("//span[contains(text(), 'Add category')]/parent::*/parent::*"))
                .click();
        waitForLoading();
        mDriver.findElement(By.xpath("//input[@type='text']")).sendKeys(name);
        mDriver.findElement(By.xpath("//span[contains(text(), 'Save')]/parent::*/parent::*"))
                .click();
        waitForLoading();
    }
}
