package steps;

import com.microsoft.playwright.*;
import io.cucumber.java.en.*;
import java.util.*;
import java.lang.*;

public class MyntraScraperSteps {

    public Browser browser;
    public Page page;
    public String brand;
    List<Map<String, String>> tshirts = new ArrayList<>();

    @Given("I navigate to {string}")
    public void NavigateToUrl(String url) {
        Playwright playwright = Playwright.create();
        BrowserType browserType = playwright.chromium();
        browser = browserType.launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate(url);
    }

    @When("I select the {string} category")
    public void SelectCategory(String category) {
        // Hover over MENS category
        page.hover("text=" + category);
    }

    @And("I filter by type {string}")
    public void FilterByType(String Type) {
        // Click the T-shirts
        page.click("a[href='/men-"+ Type.toLowerCase() + "']");
    }

    @And("I filter by brand {string}")
    public void FilterByBrand(String shirt_brand) {
        brand = shirt_brand;
        // Click search icon
        page.click(".filter-search-iconSearch");
        // Type the brand name
        page.fill(".filter-search-inputBox", brand);
        // Simulate pressing the "Enter" key
        page.press(".filter-search-inputBox", "Enter");
        // Improvement Required : Additional click function because the checkbox is a pseudo-element
        page.locator("input[type='checkbox'][value='" + brand + "']").dispatchEvent("click");
    }

    @Then("I extract the discounted T-shirts data")
    public void ExtractDiscountedTshirts() {

        // Locate all product elements on the page
        Locator products = page.locator(".product-base");
        int count = products.count();

        // Iterate through each product
        for (int i = 0; i < count; i++) {

            // Locate all the discounted products
            Locator discountedProducts = products.nth(i).locator(".product-strike");

            // Check if the discounted price element is visible
            if (discountedProducts.isVisible()) {
                // Extract the discounted price, original price, discount percentage, and product link
                String discountedPrice = products.nth(i).locator(".product-discountedPrice").textContent().trim();
                String originalPrice = discountedProducts.textContent().trim();
                String discount = products.nth(i).locator(".product-discountPercentage").textContent().trim();
                String link = "https://www.myntra.com/" + products.nth(i).locator("a").getAttribute("href");

                // Check if the discount is not null and contains percentage symbol (To ignore discounts of numeric type)
                // Improvement Required : Code can be optimized to compare numeric and percentage values of discounts
                if (discount!= null && discount.contains("%")) {
                    // Create a map to store T-shirt data
                    Map<String, String> tshirt = new HashMap<>();
                    tshirt.put("discountedPrice", discountedPrice);
                    tshirt.put("originalPrice", originalPrice);
                    tshirt.put("discount", discount);
                    tshirt.put("link", link);

                    // Add the T-shirt data to list
                    tshirts.add(tshirt);
                }
            }
        }
    }


    @Then("I sort the tshirts by highest discount")
    public void SortDiscountedTshirts() {
        // Length of the List
        int n = tshirts.size();

        // Perform Bubble Sort
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                // Extract the numeric discount values
                int discountA = extractNumericValue(tshirts.get(j).get("discount"));
                int discountB = extractNumericValue(tshirts.get(j + 1).get("discount"));

                // If the next T-shirt has a higher discount swap them
                if (discountA < discountB) {
                    // Swap the T-shirts
                    Map<String, String> temp = tshirts.get(j);
                    tshirts.set(j, tshirts.get(j + 1));
                    tshirts.set(j + 1, temp);
                }
            }
        }
    }

    // Extract the numeric value from discount ( removing % )
    private int extractNumericValue(String discountString) {
        return Integer.parseInt(discountString.replaceAll("[^0-9]", ""));
    }


    @Then("I print the sorted data to the console")
    public void DisplaySortedData() {
        System.out.println("DISCOUNTS FOR BRAND: " + brand);
        System.out.println("*****************************");
        tshirts.forEach(tshirt -> {
            System.out.println("Discounted Price: " + tshirt.get("discountedPrice"));
            System.out.println("Original Price: " + tshirt.get("originalPrice"));
            System.out.println("Discount: " + tshirt.get("discount"));
            System.out.println("Link: " + tshirt.get("link"));
            System.out.println("------------------------------");
        });
        browser.close();
    }

}
