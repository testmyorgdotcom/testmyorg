package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.testmy.data.PortalUrlProvider.DOMAIN_FIELD_NAME;
import static org.testmy.data.PortalUrlProvider.MASTER_LABEL_FIELD_NAME;
import static org.testmy.data.PortalUrlProvider.URL_PREFIX_FIELD_NAME;

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

    private SObject portalDomainSite(final String portalName,
            final String domain,
            final String urlPathPrefix) {
        final SObject result = new SObject();
        result.setField(MASTER_LABEL_FIELD_NAME, portalName);
        result.setField(DOMAIN_FIELD_NAME, domain);
        result.setField(URL_PREFIX_FIELD_NAME, urlPathPrefix);
        return result;
    }
}
