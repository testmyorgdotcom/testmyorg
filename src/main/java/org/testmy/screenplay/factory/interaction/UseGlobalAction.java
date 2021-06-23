package org.testmy.screenplay.factory.interaction;

import org.testmy.screenplay.act.interaction.global.GlobalAction;

import net.serenitybdd.core.steps.Instrumented;

public class UseGlobalAction {
    public static GlobalAction called(final String actionName) {
        return Instrumented.instanceOf(GlobalAction.class).withProperties(actionName);
    }
}
