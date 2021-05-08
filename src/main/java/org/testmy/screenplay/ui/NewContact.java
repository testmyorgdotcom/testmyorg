package org.testmy.screenplay.ui;

import net.serenitybdd.screenplay.targets.Target;

public class NewContact {

    public static Target quickActionButton() {
        return Target.the("New Contact - quick action").locatedBy("//button[@name='Global.NewContact']");
    }

    public static Target quickActionLayout() {
        return Target.the("New Contact - form").locatedBy("//div[@data-aura-class='forceQuickActionLayout']");
    }

    public static class LastName {
        public static Target input() {
            return Target.the("Form - LastName").locatedBy("//input[@placeholder='Last Name']");
        }
    }

    public static class Save {
        public static Target button() {
            return Target.the("Form - Save Button")
                    .locatedBy("//div[contains(@class, 'modal-footer')]//button[span[text()='Save']]");
        }
    }
}
