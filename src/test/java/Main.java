import com.google.common.io.Files;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static By voucherActive = By.xpath("/html/body/div[2]/div[2]/div[2]/div/div/div/div/div/div[2]/div[1]/div[6]/div");
    private static By elmentyBarcode = By.cssSelector(".hqSGmB > div:nth-child(2)");
    private static List<String> listActive = new ArrayList<>();
    private static List<String> listUsed = new ArrayList<>();
    private static By lastMonth = By.cssSelector(".fQLuJl.jvngYq ");
    private static String rowsVouchers_xpath = "//*[@id=\"root\"]/div[2]/div[1]/div/div[3]/div[2]/table/tbody/tr";
    private static By rowsVouchers = By.xpath(rowsVouchers_xpath);
    private static int monthUrl = 2;

    public static void main(String[] args) throws IOException, InterruptedException {
        WebDriver driver = createDriver();
        //Enter the billing report if you are not logged in, log in and then it will continue

        driver.get(GetProperties.getUrl());
        System.out.println("getTextVouchers: " + GetProperties.getTextVouchers());
        System.out.println("GetProperties.getEmail()" + GetProperties.getEmail());
        System.out.println("GetProperties.getMonths()" + GetProperties.getMonths());


//        ResourceBundle rb1 = ResourceBundle.getBundle(new Locale("he"));
//        System.out.println(rb1.getString(GetProperties.getTextVouchers()));
//        String strISO1 = rb1.getString(GetProperties.getTextVouchers());
//        System.out.println(new String(strISO1.getBytes("ISO-8859-1"), "UTF-8"));


        driver.findElement(By.id("email")).sendKeys(GetProperties.getEmail());
        driver.findElement(By.cssSelector("#login_tab_controls > div > form > button")).click();
        new WebDriverWait(driver, Duration.ofMinutes(5)).until(ExpectedConditions.urlToBe(GetProperties.getUrl()));
        for (int i = 0; i < GetProperties.getMonths(); i++) {
            try {
                WebElement lastMonth_Element = driver.findElement(lastMonth);
                new Actions(driver).scrollToElement(lastMonth_Element).perform();
                lastMonth_Element.click();

            } catch (Exception e) {
                driver.get("https://www.10bis.co.il/next/user-report?dateBias=" + monthUrl);
                monthUrl++;

            }
            findVoucherofthemonth(driver);
        }
        addToReport();


    }

    private static void addToReport() {
        for (String s : listActive) {
            System.out.println("שוברים פעילים");
            System.out.println(s);
        }
        for (String s : listUsed) {
            System.out.println("שוברים  לא פעילים");
            System.out.println(s);
        }
    }

    private static void findVoucherofthemonth(WebDriver driver) throws InterruptedException {
        List<WebElement> rowTableElements = driver.findElements(rowsVouchers);
        for (int i = 1; i < rowTableElements.size(); i++) {
            try {
                String typeOrder = driver.findElement(By.xpath(rowsVouchers_xpath + "[" + i + "]/td[3]/div")).getText();
                System.out.println("typeOrder " + typeOrder);
                System.out.println(typeOrder.contains(GetProperties.getTextVouchers()));
                if (typeOrder.contains(GetProperties.getTextVouchers())) {
                    driver.findElement(By.xpath(rowsVouchers_xpath + "[" + i + "]/td/div/button")).click();
                    driver.findElement(By.xpath(rowsVouchers_xpath + "[" + i + "]/td[6]/div/div/button[1]")).click();

                    String numBarcode = "";
                    try {
                        numBarcode = driver.findElement(elmentyBarcode).getText();

                    } catch (Exception e) {
                        System.out.println("not voucher");
                        continue;
                    }
                    new voucherHandling(driver, numBarcode);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println("try :" + rowsVouchers_xpath + "[" + i + "]/td/div/button");
                System.out.println("rowTableElements.size()" + rowTableElements.size());
            }

        }


    }


    public static WebDriver createDriver() throws IOException {
        WebDriver driver = null;
        switch (GetProperties.getBrowser()) {
            case "firefox" -> {
//                WebDriverManager.firefoxdriver();
                driver = new FirefoxDriver();
            }
            case "edge" -> {
//                WebDriverManager.edgedriver().setup();
                driver = new EdgeDriver();
            }
            default -> {
//                WebDriverManager.chromedriver().setup();
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments(

                );
                driver = new ChromeDriver(chromeOptions);
            }
        }
        driver.manage().window().maximize();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        return driver;
    }


    private static class voucherHandling {
        private static int numVoucher = 0;
        private static int numVoucherActive = 0;
        private final By closeVoucher = By.cssSelector("button[data-test-id='modalCloseButton']");

        public voucherHandling(WebDriver driver, String numBarcode) {

            numVoucher++;
            String textDetailVoucher = driver.findElement(By.cssSelector(".jZvnOx > div:nth-child(6)")).getText();
            //רה של מומש בתאריך
//            String textDetailVoucher = driver.findElement(By.cssSelector(".jZvnOx")).getText();
            System.out.println("textDetailVoucher.contains(\"מומש\")" + textDetailVoucher.contains("מומש"));
            boolean checkUsed = !textDetailVoucher.contains("מומש");
            String allTextDetailVoucher = "numBarcode: " + numBarcode + "\nnumVoucher: " + numVoucher + "\ntextDetailVoucher";
            System.out.println(allTextDetailVoucher);
            if (checkUsed) {
                listActive.add(allTextDetailVoucher);
                numVoucherActive++;
            } else
                listUsed.add(allTextDetailVoucher);


            PrepareTakeScreenshot_mark(driver, checkUsed);
            takeScreenshot_voucher(driver, numBarcode, checkUsed);
            driver.findElement(closeVoucher).click();

        }


        private static void PrepareTakeScreenshot_mark(WebDriver driver, boolean checkUsed) {
            String active = "מספר שובר: ";
            active = checkUsed ? active + numVoucherActive : "";
            WebElement titleVoucher = driver.findElement(By.id("modal-title"));
            JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
            jsExecutor.executeScript("arguments[0].style.border='4px solid red'", titleVoucher);
            jsExecutor.executeScript("arguments[0].innerHTML = ' " + numVoucher + active +
                    "'", titleVoucher);

        }


        public static void takeScreenshot_voucher(WebDriver driver, String numBarcode, boolean checkUsed) {
//            String nameFile = "num Voucher " + numVoucher;
            String nameFile = "" + checkUsed + numVoucher;
            WebElement zoomPage = driver.findElement(By.tagName("html"));
            //zoom out
            zoomPage.sendKeys(Keys.chord(Keys.CONTROL, Keys.SUBTRACT));
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            //without zoom
            zoomPage.sendKeys(Keys.chord(Keys.CONTROL, "0"));
            try {
                try {
                    nameFile = checkUsed ? numBarcode : nameFile;
                    Files.move(screenshot, new File(System.getProperty("user.dir") + "/" + nameFile +
                            ".png"));

                } catch (Exception e) {
                    System.out.println("catch-170*****************");
                    try {

                        Files.move(screenshot, new File(System.getProperty("user.dir") + "\\report\\" + nameFile +
                                ".png"));///(The system cannot find the path specified)
                    } catch (Exception ee) {
                        System.out.println("catch catch");
                        Files.move(screenshot, new File(System.getProperty("user.dir") + nameFile +
                                ".png"));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
