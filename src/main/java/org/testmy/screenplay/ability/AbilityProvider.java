package org.testmy.screenplay.ability;

import net.serenitybdd.screenplay.Actor;

/**
 * This is a dummy wrapper class to overcome serenity framework limitation to
 * get abilities inside unit tests. Without it TDD is hard, code for unit tests
 * becomes ugly.
 */
public class AbilityProvider {
    private final static AbilityProvider instance = new AbilityProvider();

    public static AbilityProvider getInstance() {
        return instance;
    }

    private AbilityProvider() {
    }

    public <T extends SalesforceAbility> T as(Actor actorWithAbility,
            Class<T> abilityClass) {
        return SafeAbility.as(actorWithAbility, abilityClass);
    }
}
