package org.testmy.persona;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.util.List;

import org.junit.Test;

public class PersonaManagerTest {
    final PersonaManager managerUnderTest = new PersonaManager("empty-personas");

    @Test
    public void hasPersonas() {
        final List<Persona> personas = managerUnderTest.getAllPersonas();

        assertThat(personas, notNullValue());
    }

    @Test
    public void reservesPersonaForActor() {
        final Persona expectedPersona = new Persona();
        managerUnderTest.addPersona(expectedPersona);

        final Persona persona = managerUnderTest.reservePersonaFor("any actor");

        assertThat(persona, is(equalTo(expectedPersona)));
    }

    @Test
    public void findNextAvailablePersona() {
        final Persona expectedPersona1 = new Persona("1");
        managerUnderTest.addPersona(expectedPersona1);
        final Persona expectedPersona2 = new Persona("2");
        managerUnderTest.addPersona(expectedPersona2);

        managerUnderTest.reservePersonaFor("1st actor");
        final Persona persona = managerUnderTest.reservePersonaFor("2nd actor");

        assertThat(persona, is(equalTo(expectedPersona2)));
    }

    @Test(expected = IllegalStateException.class)
    public void exceptionIfNoAvailablePersonas() {
        final Persona expectedPersona = new Persona();
        managerUnderTest.addPersona(expectedPersona);

        managerUnderTest.reservePersonaFor("1st actor");
        managerUnderTest.reservePersonaFor("2nd actor");
    }

    @Test
    public void reusePersonaForActor() {
        final String actorName = "Mike";
        final Persona expectedPersona = new Persona();
        managerUnderTest.addPersona(expectedPersona);

        managerUnderTest.reservePersonaFor(actorName);
        final Persona persona = managerUnderTest.reservePersonaFor(actorName);

        assertThat(persona, is(equalTo(expectedPersona)));
    }

    @Test
    public void reserveSpecificPersona() {
        final String actorName = "Mike";
        final String personaName = "Sales";
        final Persona persona = new Persona(personaName);
        managerUnderTest.addPersona(persona);

        final Persona salesPersona = managerUnderTest.reservePersonaFor(actorName, personaName);

        assertThat(salesPersona, is(equalTo(persona)));
    }

    @Test
    public void loadsPersonaDefinitionsFromConfigFile() {
        final PersonaManager managerWithConfigOverride = new PersonaManager("test-personas"); // TODO: move to varaible

        assertThat(managerWithConfigOverride.getAllPersonas(), hasSize(2));

        final String personaName = "Sales";
        final Persona persona = managerWithConfigOverride.reservePersonaFor("any actor", personaName);

        assertThat(persona, hasProperty("name", equalTo(personaName)));
    }

    @Test
    public void returnsBackPersonasForReUse() {
        final Persona expectedPersona = new Persona();
        managerUnderTest.addPersona(expectedPersona);

        Persona reservedPersona = managerUnderTest.reservePersonaFor("1st actor");
        managerUnderTest.tearDown("1st actor", reservedPersona);
        reservedPersona = managerUnderTest.reservePersonaFor("2nd actor");

        assertThat(reservedPersona, is(expectedPersona));
    }
}
