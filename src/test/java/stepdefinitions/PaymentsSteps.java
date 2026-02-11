package stepdefinitions;

import io.cucumber.java.en.*;
import io.github.cdimascio.dotenv.Dotenv;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import pages.AccountsPage;
import pages.LoginPage;
import pages.PaymentsPage;
import pages.TransactionsPage;
import utils.Hooks;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Map;

public class PaymentsSteps {

    private WebDriver driver;
    private LoginPage loginPage;
    private AccountsPage accountsPage;
    private PaymentsPage paymentsPage;
    private TransactionsPage transactionsPage;

    private BigDecimal requestedAmount;
    private String selectedAccount;
    private String selectedCategory;

    // ============================================================
    // LOGIN + IR PARA PAYMENTS
    // ============================================================
    @Given("login and access payments page")
    public void login_and_access_payments_page() {
        driver = Hooks.getDriver();
        loginPage = new LoginPage(driver);
        accountsPage = new AccountsPage(driver);
        paymentsPage = new PaymentsPage(driver);

        loginPage.loginUsingEnvCredentials();

        accountsPage.clickPaymentsNav();
        paymentsPage.waitLoaded();
    }

    // ============================================================
    // SCENARIO 5 — DataTable
    // ============================================================
    @When("I make a payment with the following data")
    public void i_make_a_payment_with_the_following_data(io.cucumber.datatable.DataTable table) {

        Map<String,String> row = table.asMaps(String.class, String.class).get(0);

        this.selectedAccount   = row.get("ACCOUNT").trim();
        this.selectedCategory  = row.get("CATEGORY").trim();
        this.requestedAmount   = new BigDecimal(row.get("AMOUNT").trim());

        paymentsPage.waitLoaded();
        paymentsPage.selectAccount(selectedAccount);
        paymentsPage.enterReference(row.get("REFERENCE").trim());
        paymentsPage.enterEntity(row.get("ENTITY").trim());
        paymentsPage.enterAmount(requestedAmount);
        paymentsPage.enterCategory(selectedCategory);
        paymentsPage.goNextToConfirmation();
    }

    // ============================================================
    // SUPORTE CENÁRIOS ANTIGOS
    // ============================================================
    @When("I make a payment from {string} with reference {string}, entity {string}, amount {int} and category {string}")
    public void i_make_a_payment(String account, String reference, String entity, Integer amount, String category) {

        this.selectedAccount = account;
        this.selectedCategory = category;
        this.requestedAmount = new BigDecimal(amount);

        paymentsPage.waitLoaded();
        paymentsPage.selectAccount(account);
        paymentsPage.enterReference(reference);
        paymentsPage.enterEntity(entity);
        paymentsPage.enterAmount(requestedAmount);
        paymentsPage.enterCategory(category);
        paymentsPage.goNextToConfirmation();
    }

    // ============================================================
    // CONFIRMATION / SUCCESS
    // ============================================================
    @Then("Verify confirmation window appears with payment details")
    public void verify_confirmation_window() {
        Assert.assertTrue("Confirmation NOT visible", paymentsPage.isConfirmationVisible());
    }

    @When("I click to proceed with payment")
    public void i_click_to_proceed_with_payment() {
        paymentsPage.confirmPayment();
    }

    @Then("Verify success payment page appears")
    public void verify_success_payment_page() {
        Assert.assertTrue("Success NOT visible", paymentsPage.isSuccessVisible());
        paymentsPage.closeSuccessScreen();
    }

    // ============================================================
    // VERIFICAR NOVA TRANSAÇÃO
    // ============================================================
    @Then("Verify new transaction appears with {string} category and {int} amount")
    public void verify_new_transaction(String category, Integer amount) {

        String expectedAmount = "-" + new BigDecimal(amount).toPlainString() + "€";

        boolean found =
                transactionsPage.waitUntilTransactionAmountAndCategoryAppear(
                        expectedAmount,
                        category,
                        Duration.ofSeconds(20)
                );

        Assert.assertTrue(
                "Transaction NOT found → category: " + category + " / amount: " + expectedAmount,
                found
        );
    }
}
