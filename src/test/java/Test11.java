import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;

import static org.testng.Assert.*;

public class Test11 {
    private WebDriver driver;
    private String baseUrl;


    @BeforeClass(alwaysRun = true)
    public void setUp() throws Exception {
        System.setProperty("webdriver.chrome.driver", "src/chromedriver.exe");
        driver = new ChromeDriver();
        baseUrl = "https://sandbox.cardpay.com/MI/cardpayment2.html?orderXml=PE9SREVSIFdBTExFVF9JRD0nODI5OScgT1JERVJfTlVNQkVSPSc0NTgyMTEnIEFNT1VOVD0nMjkxLjg2JyBDVVJSRU5DWT0nRVVSJyAgRU1BSUw9J2N1c3RvbWVyQGV4YW1wbGUuY29tJz4KPEFERFJFU1MgQ09VTlRSWT0nVVNBJyBTVEFURT0nTlknIFpJUD0nMTAwMDEnIENJVFk9J05ZJyBTVFJFRVQ9JzY3NyBTVFJFRVQnIFBIT05FPSc4NzY5OTA5MCcgVFlQRT0nQklMTElORycvPgo8L09SREVSPg==&sha512=998150a2b27484b776a1628bfe7505a9cb430f276dfa35b14315c1c8f03381a90490f6608f0dcff789273e05926cd782e1bb941418a9673f43c47595aa7b8b0d";
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
        driver.manage().window().fullscreen();
    }

    //Проверка статуса "Успешно"
    @Test
    public void testCardNumber() {
        driver.get(baseUrl);
        WebElement cardNumber = driver.findElement(By.id("input-card-number"));
        WebElement cardHolder = driver.findElement(By.id("input-card-holder"));
        WebElement cardExpiresMonth = driver.findElement(By.id("card-expires-month"));
        WebElement cardYear = driver.findElement(By.id("card-expires-year"));
        WebElement cardCVC = driver.findElement(By.id("input-card-cvc"));

        cardNumber.clear();
        cardNumber.sendKeys("4000 0000 0000 0036");
        cardHolder.clear();
        cardHolder.sendKeys("DMITRII IVANOV");
        cardExpiresMonth.click();
        new Select(cardExpiresMonth).selectByVisibleText("02");
        cardYear.click();
        new Select(cardYear).selectByVisibleText("2023");
        cardCVC.clear();
        cardCVC.sendKeys("111");
        driver.findElement(By.id("action-submit")).click();

        assertEquals(driver.findElement(By.xpath("//div[@id='payment-item-status']/div[2]")).getText(), "Confirmed");
    }

    //Проверка номера карты (вводим все нули, ожидая ошибку)

    @Test
    public void checkCardValidator() {
        driver.get(baseUrl);
        WebElement cardNumber = driver.findElement(By.id("input-card-number"));
        WebElement cardHolder = driver.findElement(By.id("input-card-holder"));
        cardNumber.clear();
        cardNumber.sendKeys("0000 0000 0000 0000");
        cardHolder.click();

        assertEquals(driver.findElement(By.xpath("//*[@id=\"card-number-field\"]/div/label")).getText(), "Card number is not valid");

    }


    //Мы наводим мышь
    @Test
    public void mouse() throws InterruptedException {

        driver.get(baseUrl);
        WebElement cvcHint = driver.findElement(By.id("cvc-hint"));
        //Навели мышь на элемент
        Actions actions = new Actions(driver);
        actions.moveToElement(cvcHint).click().build().perform();
        driver.wait(3000);

    }


    //Тест на проверку номера карты и имени на карте
    @Test
    public void declinedStatus() {
        driver.get(baseUrl);
        WebElement cardNumber = driver.findElement(By.id("input-card-number"));
        WebElement cardHolder = driver.findElement(By.id("input-card-holder"));
        WebElement cardExpiresMonth = driver.findElement(By.id("card-expires-month"));
        WebElement cardYear = driver.findElement(By.id("card-expires-year"));
        WebElement cardCVC = driver.findElement(By.id("input-card-cvc"));
        cardNumber.clear();
        cardNumber.sendKeys("555555555555444");
        cardHolder.clear();
        cardHolder.sendKeys("Semen 1");
        cardExpiresMonth.click();
        new Select(cardExpiresMonth).selectByVisibleText("03");
        cardYear.click();
        new Select(cardYear).selectByVisibleText("2024");
        cardCVC.clear();
        cardCVC.sendKeys("113");
        driver.findElement(By.id("action-submit")).click();

        //Проверка введенных неправильных данных (номер карты и имя на карте)
        assertEquals(driver.findElement(By.xpath("//label[text()='Card number is not valid']")).getText(), "Card number is not valid");
        assertEquals(driver.findElement(By.xpath("//label[text()='Cardholder name is not valid']")).getText(), "Cardholder name is not valid");
    }


//Тест на проверку платежной системы (Мастеркард) и проверку статуса "Отклонено"
    @Test
    public void testMasterCard() {
        driver.get(baseUrl);
        WebElement cardNumber = driver.findElement(By.id("input-card-number"));
        WebElement cardHolder = driver.findElement(By.id("input-card-holder"));
        WebElement cardExpiresMonth = driver.findElement(By.id("card-expires-month"));
        WebElement cardYear = driver.findElement(By.id("card-expires-year"));
        WebElement cardCVC = driver.findElement(By.id("input-card-cvc"));
        cardNumber.clear();
        cardNumber.sendKeys("5555555555554477");
        cardHolder.clear();
        cardHolder.sendKeys("DMITRII IVANOV");
        cardExpiresMonth.click();
        new Select(cardExpiresMonth).selectByVisibleText("04");
        cardYear.click();
        new Select(cardYear).selectByVisibleText("2025");
        cardCVC.clear();
        cardCVC.sendKeys("247");
        driver.findElement(By.id("action-submit")).click();

        //Проверка статуса "Отказано" и платежной системы "Мастеркард"
        assertEquals(driver.findElement(By.xpath("//*[@class = 'payment-info-item-data']")).getText(), "Declined by issuing bank");
        assertEquals(driver.findElement(By.xpath("//div[@id='payment-item-cardtype']/div[2]")).getText(), "MASTERCARD");
    }

    //Проверка эмуляции 3д безопасности на статус "Отказ", платежная система "Мастеркард".

    @Test
    public void secureDeclinedMasterCard() {
        driver.get(baseUrl);
        WebElement cardNumber = driver.findElement(By.id("input-card-number"));
        WebElement cardHolder = driver.findElement(By.id("input-card-holder"));
        WebElement cardExpiresMonth = driver.findElement(By.id("card-expires-month"));
        WebElement cardYear = driver.findElement(By.id("card-expires-year"));
        WebElement cardCVC = driver.findElement(By.id("input-card-cvc"));
        cardNumber.clear();
        cardNumber.sendKeys("5555555555554444");
        cardHolder.clear();
        cardHolder.sendKeys("DMITRII IVANOV");
        cardExpiresMonth.click();
        new Select(cardExpiresMonth).selectByVisibleText("04");
        cardYear.click();
        new Select(cardYear).selectByVisibleText("2025");
        cardCVC.clear();
        cardCVC.sendKeys("247");
        driver.findElement(By.id("action-submit")).click();
        driver.findElement(By.xpath("//button[@id='failure']")).click();

        // Проверка статуса "Отказ" и платежной системы "Мастеркард"


        assertEquals(driver.findElement(By.xpath("//*[@class = 'payment-info-item-data']")).getText(), "Declined by issuing bank");
        assertEquals(driver.findElement(By.xpath("//div[@id='payment-item-cardtype']/div[2]")).getText(), "MASTERCARD");
    }

    // Проверка эмуляции 3д безопасности с платежной системой "Виза" и статусом "Успешно"

    @Test
    public void secureConfirmedVisa() {
        driver.get(baseUrl);
        WebElement cardNumber = driver.findElement(By.id("input-card-number"));
        WebElement cardHolder = driver.findElement(By.id("input-card-holder"));
        WebElement cardExpiresMonth = driver.findElement(By.id("card-expires-month"));
        WebElement cardYear = driver.findElement(By.id("card-expires-year"));
        WebElement cardCVC = driver.findElement(By.id("input-card-cvc"));
        cardNumber.clear();
        cardNumber.sendKeys("4000000000000002");
        cardHolder.clear();
        cardHolder.sendKeys("DMITRII IVANOV");
        cardExpiresMonth.click();
        new Select(cardExpiresMonth).selectByVisibleText("04");
        cardYear.click();
        new Select(cardYear).selectByVisibleText("2025");
        cardCVC.clear();
        cardCVC.sendKeys("247");
        driver.findElement(By.id("action-submit")).click();
        driver.findElement(By.xpath("//button[@id='success']")).click();

        //Проверка статуса "Успешно" и платежной системы "Виза"
        assertEquals(driver.findElement(By.xpath("//div[@id='payment-status-title']/span")).getText(), "Success");
        assertEquals(driver.findElement(By.xpath("//div[@id='payment-item-cardtype']/div[2]")).getText(), "VISA");
    }

    //Проверка имени пользователя, номера заказа и статуса после операции "Инфо"

    @Test
    public void cardHolderName() {
        driver.get(baseUrl);
        WebElement cardNumber = driver.findElement(By.id("input-card-number"));
        WebElement cardHolder = driver.findElement(By.id("input-card-holder"));
        WebElement cardExpiresMonth = driver.findElement(By.id("card-expires-month"));
        WebElement cardYear = driver.findElement(By.id("card-expires-year"));
        WebElement cardCVC = driver.findElement(By.id("input-card-cvc"));
        cardNumber.clear();
        cardNumber.sendKeys("4000000000000051");
        cardHolder.clear();
        cardHolder.sendKeys("DMITRII IVANOV");
        cardExpiresMonth.click();
        new Select(cardExpiresMonth).selectByVisibleText("04");
        cardYear.click();
        new Select(cardYear).selectByVisibleText("2025");
        cardCVC.clear();
        cardCVC.sendKeys("247");
        driver.findElement(By.id("action-submit")).click();


        //Проверка имени пользователя, номера заказа и статуса "Инфо"
        assertEquals(driver.findElement(By.xpath("//div[@id='payment-item-cardholder']/div[2]")).getText(), "DMITRII IVANOV");
        assertEquals(driver.findElement(By.xpath("//div[@id='payment-item-ordernumber']/div[2]")).getText(), "458211");
        assertEquals(driver.findElement(By.xpath("//div[@id='payment-status-title']/span")).getText(), "Info");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() throws Exception {
        driver.quit();
    }
}
