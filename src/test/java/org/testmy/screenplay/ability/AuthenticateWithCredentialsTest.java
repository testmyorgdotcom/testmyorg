package org.testmy.screenplay.ability;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testmy.screenplay.fact.PersonaBehaviour;
import org.testmy.screenplay.factory.ability.Authenticate;

import net.serenitybdd.screenplay.Actor;

public class AuthenticateWithCredentialsTest {
    Actor mike;

    @Before
    public void before() {
        mike = Actor.named("Mike");
        mike
                .can(Authenticate.withCredentials())
                .has(PersonaBehaviour.of("Sales"));
    }

    @After
    public void after() {
        PersonaBehaviour.of("Sales").teardown(mike);
    }

    @Test
    public void testAuthenticateAs() {

        final AuthenticateWithCredentials authenticated = AuthenticateWithCredentials.as(mike);

        assertThat(authenticated.getUsername(), is(notNullValue()));
        assertThat(authenticated.getPassword(), is(notNullValue()));
    }
}
