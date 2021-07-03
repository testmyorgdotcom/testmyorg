package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;

import com.sforce.soap.partner.sobject.SObject;

import org.junit.Test;
import org.testmy.error.TestRuntimeException;

public class PortalUrlProviderTest {
    PortalUrlProvider portalUrlProver = new PortalUrlProvider();

    @Test
    public void getPortalFor_ProvidesPortalURLForName() {
        final String domain = "http://localhost";
        final String urlPathPrefix = "/s";
        final String portalName = "Customer Portal";
        initPortaldata(portalName, domain, urlPathPrefix);

        final String resolvedPortalUrl = portalUrlProver.getPortalFor(portalName);

        assertThat(resolvedPortalUrl, is(domain + urlPathPrefix));
    }

    @Test(expected = TestRuntimeException.class)
    public void getPortalFor_throwsExceptionIfNoDataForPortalName() {
        portalUrlProver.getPortalFor("non-existing protal");
    }

    private void initPortaldata(final String portalName,
            final String domain,
            final String urlPathPrefix) {
        portalUrlProver.init(Arrays.asList(portalDomainSite(portalName, domain, urlPathPrefix)));
    }

    public static SObject portalDomainSite(final String portalName,
            final String domain,
            final String urlPathPrefix) {
        final SObject domainObject = new SObject();
        domainObject.setField("Domain", domain);
        final SObject siteObject = new SObject();
        siteObject.setField("MasterLabel", portalName);
        siteObject.setField("UrlPathPrefix", urlPathPrefix);
        final SObject result = new SObject();
        result.setSObjectField("Domain", domainObject);
        result.setSObjectField("Site", siteObject);
        return result;
    }
}
