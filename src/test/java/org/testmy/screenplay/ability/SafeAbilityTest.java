package org.testmy.screenplay.ability;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.testmy.error.AbilityIsAbsentException;

import net.serenitybdd.screenplay.Actor;

public class SafeAbilityTest {
    Actor actorWithAbility = Actor.named("Creator");
    Actor actorWithoutAbility = Actor.named("Heart-Breaker");

    @Before
    public void before() {
        actorWithAbility.can(TestAbility.obtain());
    }

    @Test
    public void yieldsAbilityForActor() {
        final TestAbility ability = SafeAbility.as(actorWithAbility, TestAbility.class);
        assertThat(ability, is(notNullValue()));
    }

    @Test
    public void failWithExceptionIfActorHasNoAbility() {
        final String errorMessage = String.format("Actor: %s has no requested ability: %s",
                actorWithoutAbility.getName(),
                TestAbility.class.getSimpleName());

        final AbilityIsAbsentException expected = Assert.assertThrows(AbilityIsAbsentException.class, () -> {
            SafeAbility.as(actorWithoutAbility, TestAbility.class);
        });

        assertThat(expected.getMessage(), is(errorMessage));
    }

    public static class TestAbility implements SalesforceAbility {
        public static TestAbility obtain() {
            return new TestAbility();
        }

        @Override
        public void setActor(Actor actor) {
        }
    }
}
