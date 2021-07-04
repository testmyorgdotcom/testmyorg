package org.testmy.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sforce.soap.partner.sobject.SObject;

import org.testmy.error.TestRuntimeException;

public class PortalUrlProvider {
    public final static String DOMAIN_FIELD_NAME = "Domain";
    public final static String URL_PREFIX_FIELD_NAME = "UrlPathPrefix";
    public final static String MASTER_LABEL_FIELD_NAME = "MasterLabel";
    private final Map<String, String> portalUrlByName = new HashMap<>();

    public String getPortalFor(String portalName) {
        if (!portalUrlByName.containsKey(portalName)) {
            throw new TestRuntimeException("No url data for portal: " + portalName
                    + ". Please check portal exists in the target sandbox,"
                    + " is active or Admin Actor has initialized framework");
        }
        return portalUrlByName.get(portalName);
    }

    public void init(List<SObject> portalDomainSites) {
        portalDomainSites.forEach(portalDomain -> {
            final SObject domainObject = (SObject) portalDomain.getSObjectField(DOMAIN_FIELD_NAME);
            final String domain = domainObject.getField(DOMAIN_FIELD_NAME).toString();
            final SObject siteObject = (SObject) portalDomain.getSObjectField("Site");
            final String urlPathPrefix = siteObject.getField(URL_PREFIX_FIELD_NAME).toString();
            final String portalName = siteObject.getField(MASTER_LABEL_FIELD_NAME).toString();
            portalUrlByName.put(portalName, domain + urlPathPrefix);
        });
    }
}
