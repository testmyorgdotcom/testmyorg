package org.testmy.screenplay.factory;

import org.testmy.screenplay.act.Ensure;
import org.testmy.screenplay.act.interaction.login.ViaForm;
import org.testmy.screenplay.act.interaction.login.ViaFrontDoorUrl;
import org.testmy.screenplay.act.interaction.login.WithCredentialsInUrl;
import org.testmy.screenplay.act.interaction.login.WithSessionIdAsCookie;

import net.serenitybdd.core.steps.Instrumented;
import net.serenitybdd.screenplay.Performable;

public class Login {
    public static ViaForm viaForm() {
        return Instrumented.instanceOf(ViaForm.class).newInstance();
    }

    public static WithCredentialsInUrl withCredentialsInUrl() {
        return Instrumented.instanceOf(WithCredentialsInUrl.class).newInstance();
    }

    public static ViaFrontDoorUrl withSessionIdInFrontDoorUrl() {
        return Instrumented.instanceOf(ViaFrontDoorUrl.class).newInstance();
    }

    public static WithSessionIdAsCookie withSessionIdAsCookies() {
        return Instrumented.instanceOf(WithSessionIdAsCookie.class).newInstance();
    }

    public static Performable viaAPI() {
        return Ensure.partnerConnection();
    }
}
