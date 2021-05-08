package org.testmy.persona;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import javax.annotation.concurrent.NotThreadSafe;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigBeanFactory;
import com.typesafe.config.ConfigFactory;

import lombok.Data;
import lombok.NoArgsConstructor;

@NotThreadSafe
public class PersonaManager {
    private List<Persona> personas = new ArrayList<>();
    private Map<String, List<Persona>> actorsPersonas = new HashMap<>();
    private Set<Persona> reservedPersonas = new HashSet<>();

    public PersonaManager() {
        final Config config = ConfigFactory.load();
        this.personas = ConfigBeanFactory.create(config, PersonaList.class).personas;
    }

    // TODO: change approach to avoud such constructor, read more about com.typesafe
    // config
    public PersonaManager(final String configFile) {
        final Config config = ConfigFactory.load(configFile);
        this.personas = ConfigBeanFactory.create(config, PersonaList.class).personas;
    }

    public List<Persona> getAllPersonas() {
        return Collections.unmodifiableList(personas);
    }

    void addPersona(final Persona persona) {
        this.personas.add(persona);
    }

    public void tearDown(String actorName,
            Persona reservedPersona) {
        actorsPersonas.get(actorName).remove(reservedPersona);
        reservedPersonas.remove(reservedPersona);
    }

    private Persona reservePersona(final Predicate<Persona> criteria) {
        final Persona result = personas.stream()
                .filter(p -> !reservedPersonas.contains(p) && criteria.test(p))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No free personas")); // TODO: better description
        reservedPersonas.add(result);
        return result;
    }

    public Persona reservePersonaFor(final String actorName) {
        return reservePersonaFor(actorName, p -> true);
    }

    public Persona reservePersonaFor(final String actorName,
            final String personaName) {
        return reservePersonaFor(actorName, p -> p.getName().equals(personaName));
    }

    public Persona reservePersonaFor(final String actorName,
            final Predicate<Persona> criteria) {
        final List<Persona> actorPersonas = actorsPersonas.computeIfAbsent(actorName, actor -> new ArrayList<>());
        final Persona result = actorPersonas
                .stream()
                .filter(criteria)
                .findFirst()
                .orElseGet(() -> {
                    final Persona tempVariableForPersona = reservePersona(criteria);
                    actorPersonas.add(tempVariableForPersona);
                    return tempVariableForPersona;
                });
        return result;
    }

    @NoArgsConstructor
    @Data
    protected static class PersonaList {
        private List<Persona> personas;
    }
}
