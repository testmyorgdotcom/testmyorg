package org.testmy.screenplay.act.performable;

import java.util.List;

import com.sforce.soap.partner.sobject.SObject;

import org.testmy.data.PortalUrlProvider;
import org.testmy.screenplay.factory.question.SObjects;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Performable;
import net.thucydides.core.annotations.Shared;

public class LoadDigitalExperienceSites implements Performable {
    @Shared
    private PortalUrlProvider portalUrlProvider;

    @Override
    public <T extends Actor> void performAs(T actor) {
        final String communityPortalQuery = "SELECT Site.URLpathPrefix, Site.MasterLabel, Domain.Domain"
                + " FROM DomainSite"
                + " WHERE Domain.HttpsOption = 'Community'"
                + " AND Site.Status = 'Active'";
        final List<SObject> portalInfos = actor.asksFor(SObjects.usingQuery(communityPortalQuery));
        portalUrlProvider.init(portalInfos);
    }
}
