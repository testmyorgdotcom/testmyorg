package org.testmy.screenplay.act.task;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;
import static org.testmy.data.matchers.Matchers.account;
import static org.testmy.data.matchers.Matchers.hasName;
import static org.testmy.data.matchers.Matchers.ofShape;

import java.net.MalformedURLException;
import java.net.URL;

import com.sforce.soap.partner.sobject.SObject;

import org.testmy.data.TestDataManager;
import org.testmy.screenplay.ui.WebPage;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.actions.Open;
import net.serenitybdd.screenplay.waits.WaitUntil;
import net.thucydides.core.annotations.Shared;

public class SearchForAccount implements Task {
    @Shared
    private TestDataManager testDataManager;
    private String accountName;

    public SearchForAccount(final String accountName) {
        this.accountName = accountName;
    }

    public static Task witnName(String accountName) {
        return Instrumented.instanceOf(SearchForAccount.class).withProperties(accountName);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        final SObject createdAccount = testDataManager.findObject(
                ofShape(
                        account(),
                        hasName(accountName)))
                .orElseThrow(
                        () -> new IllegalStateException("Trying to search for account which was not created before"));
        final String currentUrl = actor.abilityTo(BrowseTheWeb.class).getDriver().getCurrentUrl();
        final String id = createdAccount.getId();
        final String type = createdAccount.getType();
        actor.attemptsTo(
                Open.url(constructUrl(currentUrl, type, id)),
                // Click.on(AppLauncher.icon()),
                // SendKeys.of("accounts").into(AppLauncher.searchInput()),
                // WaitUntil.the(AppLauncher.itemCalled("Accounts"), isVisible()),
                // JavaScriptClick.on(AppLauncher.itemCalled("Accounts")),
                WaitUntil.the(WebPage.loadingLogo(), isNotVisible())
        // WaitUntil.the(WebPage.spinner(), isNotVisible()),
        // GlobalSearch.forType("Accounts", accountName)
        // SendKeys.of(accountName).into(GlobalSearch.input()),
        // JavaScriptClick.on(GlobalSearch.resultCalled(accountName))
        );
    }

    private String constructUrl(String currentUrl,
            String type,
            String id) {

        try {
            final URL url = new URL(currentUrl);
            return String.format("%s://%s/lightning/r/%s/%s/view", url.getProtocol(), url.getHost(), type, id);
        }
        catch (MalformedURLException e) {
            throw new IllegalArgumentException("Incorrect input url: " + currentUrl);
        }
    }
}
