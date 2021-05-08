package org.testmy.screenplay.act.task;

import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isClickable;
import static net.serenitybdd.screenplay.matchers.WebElementStateMatchers.isNotVisible;

import org.openqa.selenium.Keys;
import org.testmy.screenplay.ui.GlobalSearchResult;
import org.testmy.screenplay.ui.WebPage;

import lombok.AllArgsConstructor;
import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.actions.Click;
import net.serenitybdd.screenplay.actions.SendKeys;
import net.serenitybdd.screenplay.targets.Target;
import net.serenitybdd.screenplay.waits.WaitUntil;

@AllArgsConstructor
public class GlobalSearch implements Performable {
    private String searchableItemType;
    private String itemToSearch;

    public static Performable forType(final String searchableItemType,
            final String searchableItem) {
        return Instrumented.instanceOf(GlobalSearch.class).withProperties(searchableItemType, searchableItem);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Click.on(GlobalSearch.entitySelector()),
                Click.on(GlobalSearch.entityType(searchableItemType)),
                Click.on(GlobalSearch.input()),
                SendKeys.of(itemToSearch).into(GlobalSearch.input()).thenHit(Keys.ENTER),
                WaitUntil.the(WebPage.loadingLogo(), isNotVisible()),
                WaitUntil.the(WebPage.spinner(), isNotVisible()),
                WaitUntil.the(GlobalSearchResult.withName(itemToSearch), isClickable()),
                Click.on(GlobalSearchResult.withName(itemToSearch)),
                WaitUntil.the(WebPage.loadingLogo(), isNotVisible()));

    }

    public static Target input() {
        return Target.the("Global Search Input").locatedBy("//input[@placeholder='Search...']");
    }

    public static Target entityType(String searchableItem) {
        return Target.the("Global Search Entity Search Type")
                .locatedBy("//div[@data-aura-class='forceSearchInputEntitySelector']"
                        + "//ul[@aria-label='Suggested For You']//span[@title='" + searchableItem + "']");
    }

    public static Target entitySelector() {
        return Target.the("Global Search Entity Selector")
                .locatedBy("//div[@data-aura-class='forceSearchInputEntitySelector']");
    }
}
