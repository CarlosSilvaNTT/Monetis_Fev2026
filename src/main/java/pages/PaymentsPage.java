package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Página de Payments – Compatível com todos os cenários
 *
 * Fluxo:
 *  1) H1 Payments
 *  2) react-select-3 → Account
 *  3) Inputs: reference, entity, amount
 *  4) react-select-2 → Category
 *  5) Next → Confirmation → Next → Success
 */
public class PaymentsPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ---------- ROOT ----------
    private static final By PAGE_ROOT =
            By.xpath("//div[contains(@class,'content')]/h1[normalize-space()='Payments']");

    private static final By LOADING_OVERLAY =
            By.cssSelector("div.loading_screen");

    // ---------- ACCOUNT SELECT (react-select-3) ----------
    private static final By ACCOUNT_CONTROL =
            By.xpath("//h2[normalize-space()='Select account']/following::div[contains(@class,'select')][1]//div[contains(@class,'control')]");
    private static final By ACCOUNT_COMBO =
            By.cssSelector("input#react-select-3-input");

    // ---------- FORM INPUTS ----------
    private static final By ENTITY_INPUT =
            By.cssSelector("input[name='entity']");
    private static final By REFERENCE_INPUT =
            By.cssSelector("input[name='reference']");
    private static final By AMOUNT_INPUT =
            By.cssSelector("div.suffix_container input[name='amount']");

    // ---------- CATEGORY SELECT (react-select-2) ----------
    private static final By CATEGORY_CONTROL =
            By.xpath("//label[normalize-space()='Category']/following::div[contains(@class,'select')][1]//div[contains(@class,'control')]");
    private static final By CATEGORY_COMBO =
            By.cssSelector("input#react-select-2-input");

    // ---------- STEPPER ----------
    private static final By NEXT_BUTTON =
            By.xpath("//form//button[normalize-space()='Next']");
    private static final By STATUS_CONFIRM =
            By.xpath("//div[contains(@class,'status')]//span[normalize-space()='Confirmation']");
    private static final By STATUS_SUCCESS =
            By.xpath("//div[contains(@class,'status')]//span[normalize-space()='Success']");

    public PaymentsPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    // ============================================================
    // PAGE READY
    // ============================================================
    public void waitLoaded() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADING_OVERLAY));
        } catch (Exception ignored) {}

        wait.until(ExpectedConditions.visibilityOfElementLocated(PAGE_ROOT));
        wait.until(d -> "complete".equals(
                ((JavascriptExecutor)d).executeScript("return document.readyState")
        ));
        wait.until(ExpectedConditions.urlContains("/payments"));
    }

    // ============================================================
    // ACCOUNT SELECTION
    // ============================================================
    public void selectAccount(String accountLabel) {
        WebElement control = wait.until(ExpectedConditions.elementToBeClickable(ACCOUNT_CONTROL));
        scrollCenter(control);
        safeClick(control);

        WebElement combo = wait.until(ExpectedConditions.visibilityOfElementLocated(ACCOUNT_COMBO));
        combo.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        combo.sendKeys(accountLabel);
        combo.sendKeys(Keys.ENTER);

        domQuiet(Duration.ofSeconds(2));
    }

    // ============================================================
    // FORM ENTRY
    // ============================================================
    public void enterEntity(String entity) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(ENTITY_INPUT));
        el.clear();
        el.sendKeys(entity);
    }

    public void enterReference(String reference) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(REFERENCE_INPUT));
        el.clear();
        el.sendKeys(reference);
    }

    public void enterAmount(BigDecimal amount) {
        WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(AMOUNT_INPUT));
        el.clear();
        el.sendKeys(amount.toPlainString());
    }

    public void enterCategory(String category) {
        WebElement control = wait.until(ExpectedConditions.elementToBeClickable(CATEGORY_CONTROL));
        scrollCenter(control);
        safeClick(control);

        WebElement combo = wait.until(ExpectedConditions.visibilityOfElementLocated(CATEGORY_COMBO));
        combo.sendKeys(Keys.chord(Keys.CONTROL, "a"));
        combo.sendKeys(category);
        combo.sendKeys(Keys.ENTER);
    }

    // ============================================================
    // STEPPER
    // ============================================================
    public void goNextToConfirmation() {
        wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(STATUS_CONFIRM));
    }

    public boolean isConfirmationVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(STATUS_CONFIRM));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void confirmPayment() {
        wait.until(ExpectedConditions.elementToBeClickable(NEXT_BUTTON)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(STATUS_SUCCESS));
    }

    public boolean isSuccessVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(STATUS_SUCCESS));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private void safeClick(WebElement el) {
        try { el.click(); }
        catch (Exception e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
        }
    }

    private void scrollCenter(WebElement el) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block:'center'});", el);
    }

    private void domQuiet(Duration timeout) {
        try {
            new WebDriverWait(driver, timeout)
                    .until(d -> "complete".equals(
                            ((JavascriptExecutor)d).executeScript("return document.readyState")));
        } catch (TimeoutException ignored) {}
    }
}
