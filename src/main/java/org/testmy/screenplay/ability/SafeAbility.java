package org.testmy.screenplay.ability;

import org.testmy.config.Config;
import org.testmy.error.AbilityIsAbsentException;

import net.serenitybdd.screenplay.Actor;

public class SafeAbility implements Config {
    public static <T extends SalesforceAbility> T as(final Actor actorWithAbility,
            final Class<T> abilityClass) {
        final T ability = actorWithAbility.abilityTo(abilityClass);
        if (null == ability) {
            throw new AbilityIsAbsentException(String.format(PATTERN_MESSAGE_ERROR_ABILITY_IS_MISSING_FOR_ACTOR,
                    actorWithAbility.getName(), abilityClass.getSimpleName()));
        }
        ability.asActor(actorWithAbility);
        return ability;
    }
}
