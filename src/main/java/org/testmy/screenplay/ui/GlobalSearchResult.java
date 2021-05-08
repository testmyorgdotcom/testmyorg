package org.testmy.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;
// $x("//a[contains(@class, 'globalCreateTrigger')]").shouldBe(visible).click();
// $x("//li[contains(@class, 'oneGlobalCreateItem')]//a[@title='" + actionDisplayName + "']").shouldBe(visible).click();

public class GlobalSearchResult {

    public static Target withName(String searchableItem) {
        return Target.the("Global Search Result")
                .locatedBy("//tr//a[@title='" + searchableItem + "']");
    }

}
