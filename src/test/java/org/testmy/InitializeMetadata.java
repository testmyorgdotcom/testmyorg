package org.testmy;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.testmy.screenplay.ability.AbilityProvider;
import org.testmy.screenplay.ability.InitializeFramework;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Interaction;

public class InitializeMetadata implements Interaction {
    private AbilityProvider abilityProvider = AbilityProvider.getInstance();
    private Set<String> objectTypes;

    protected InitializeMetadata(final String[] objectTypesArray){
        this.objectTypes = new HashSet<>();
        this.objectTypes.addAll(Arrays.asList(objectTypesArray));
    }
    public static InitializeMetadata forObjects(String ...objectTypes) {
        final String[] objectTypesArray = objectTypes;
        return new InitializeMetadata(objectTypesArray);
    }

    @Override
    public <T extends Actor> void performAs(final T actor) {
        final InitializeFramework initializeFramework = abilityProvider.as(actor, InitializeFramework.class);
        initializeFramework.loadReferenceAttributes(objectTypes);
    }

}
