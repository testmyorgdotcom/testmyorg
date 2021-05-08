package org.testmy.screenplay.ability;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.testmy.error.AbilityIsAbsentException;

import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.RefersToActor;

public class SafeAbilityTest {
    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

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
        exceptionRule.expect(AbilityIsAbsentException.class);
        exceptionRule.expectMessage(errorMessage);
        SafeAbility.as(actorWithoutAbility, TestAbility.class);
    }

    public static class TestAbility implements Ability, RefersToActor {
        public static TestAbility obtain() {
            return new TestAbility();
        }

        @Override
        public <T extends Ability> T asActor(Actor actor) {
            return (T) this;
        }
    }

}
