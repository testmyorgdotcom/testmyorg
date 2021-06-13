package org.testmy.screenplay.factory.ability;

import org.testmy.screenplay.act.interaction.navigate.NavigateToApp;

import net.serenitybdd.core.steps.Instrumented;

public class NavigateTo {
    public static NavigateToApp appCalled(String appName) {
        return Instrumented.instanceOf(NavigateToApp.class).withProperties(appName);
    }
}
