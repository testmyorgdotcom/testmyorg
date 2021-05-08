package org.testmy.screenplay.act.task;

import static org.testmy.data.matchers.Matchers.hasField;
import static org.testmy.data.matchers.Matchers.hasId;
import static org.testmy.data.matchers.Matchers.ofShape;

import org.openqa.selenium.WebDriver;
import org.testmy.URLHelper;
import org.testmy.data.TestDataManager;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.thucydides.core.annotations.Shared;

public class StoreObjectAtScene implements Performable {
    @Shared
    private TestDataManager testDataManager;

    public static Performable intoDataCache() {
        return Instrumented.instanceOf(StoreObjectAtScene.class).newInstance();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        final WebDriver driver = actor.abilityTo(BrowseTheWeb.class).getDriver();
        final String currentObjectUrl = driver.getCurrentUrl();
        final String id = URLHelper.parseObjectId(currentObjectUrl);
        final String type = URLHelper.parseObjectType(currentObjectUrl);
        testDataManager.cacheExistingShape(ofShape(
                hasId(id),
                hasField("type", type)));
    }
}
