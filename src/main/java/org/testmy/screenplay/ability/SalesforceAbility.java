package org.testmy.screenplay.ability;

import net.serenitybdd.screenplay.Ability;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.RefersToActor;

public interface SalesforceAbility extends Ability, RefersToActor {
    @Override
    @SuppressWarnings("unchecked")
    default <T extends Ability> T asActor(Actor actor) {
        setActor(actor);
        return (T) this;
    }

    // this method should be called from within RefersToActor.asActor only
    void setActor(Actor actor);
}
