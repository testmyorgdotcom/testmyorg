package org.testmy.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;

public class Toast {
    private final static String BASE_XPATH = "//div[@data-aura-class='forceVisualMessageQueue']";
    // private final static String BASE_XPATH = "//span[contains(@class,
    // 'toastMessage')]";

    public static Target success() {
        return Target.the("Toast Success").locatedBy(BASE_XPATH);
    }

    public static Target objectName() {
        return Target.the("Toast Created Object Link").locatedBy(BASE_XPATH + "//a");
    }

}
