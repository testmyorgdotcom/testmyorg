package org.testmy.persona.auth;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.apache.http.auth.Credentials;
import org.junit.Test;
import org.testmy.persona.Persona;
import org.testmy.persona.PersonaManager;

public class LightCredentialsProviderTest {
    @Test
    public void testGetCredentialsFromDefaultFile() {
        final String testProperty = "dummyProperty";
        final String testPassword = "dummyPassword";
        LightCredentialsProvider.passwordPropertyName = testProperty;
        System.setProperty(testProperty, testPassword);
        final PersonaManager personaManager = new PersonaManager("test-personas");
        final Persona testPersona = personaManager.getAllPersonas().get(0);

        final Credentials credentials = new LightCredentialsProvider().getCredentialsFor(testPersona);

        assertThat(credentials.getUserPrincipal().getName(), is(equalTo(testPersona.getUsername())));
        assertThat(credentials.getPassword(), is(equalTo(testPassword)));
    }
}
