package org.testmy.screenplay.act;

import org.testmy.screenplay.ability.AbilityProvider;
import org.testmy.screenplay.ability.InitializeFramework;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;

public class Initialize implements Performable {
    private AbilityProvider abilityProvider = AbilityProvider.getInstance();

    public static Initialize framework() {
        return Instrumented.instanceOf(Initialize.class).newInstance();
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        final InitializeFramework initializeFrameworkAbility = abilityProvider.as(actor, InitializeFramework.class);
        initializeFrameworkAbility.initialize();
    }
}
