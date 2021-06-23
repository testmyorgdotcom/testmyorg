package org.testmy.screenplay.ability;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testmy.data.matchers.Matchers.hasField;
import static org.testmy.data.matchers.Matchers.ofShape;

import java.util.Collections;

import com.sforce.ws.ConnectionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.data.MatcherToSOjbectConstructor;
import org.testmy.data.PortalUrlProvider;
import org.testmy.data.RecordTypeIdProvider;
import org.testmy.data.matchers.HasFields;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class InitializeFrameworkTest {
    @Mock
    Actor mike;
    RecordTypeIdProvider recordTypeIdProvider = new RecordTypeIdProvider();
    PortalUrlProvider portalUrlProvider = new PortalUrlProvider();
    @InjectMocks
    InitializeFramework initializeFramework;
    MatcherToSOjbectConstructor sObjectConstructor = new MatcherToSOjbectConstructor();

    @Test
    public void loadRecordTypes_initializesRecordTypeProvider() throws ConnectionException {
        initializeFramework.setRecordTypeIdProvider(recordTypeIdProvider);
        final HasFields recordTypeShape = ofShape(
                hasField("type", "RecordType"),
                hasField("SobjectType", "Account"),
                hasField("DeveloperName", "Customer"),
                hasField("Id", "0x123..."));
        when(mike.asksFor(any()))
                .thenReturn(Collections.singletonList(sObjectConstructor.constructSObject(recordTypeShape)));

        initializeFramework.loadRecordTypes();

        assertThat("0x123...", is(recordTypeIdProvider.getIdFor("Account", "Customer")));
    }

    @Test
    public void loadPortalInfo_initializesPortalUrlProvider() {
        final String portalName = "My Portal";
        final String domain = "abc.com";
        final String urlPrefx = "/xyz";
        initializeFramework.setPortalUrlProvider(portalUrlProvider);
        final HasFields domainInfoShape = ofShape(
                hasField("type", "Domain"),
                hasField(PortalUrlProvider.MASTER_LABEL_FIELD_NAME, portalName),
                hasField(PortalUrlProvider.DOMAIN_FIELD_NAME, domain),
                hasField(PortalUrlProvider.URL_PREFIX_FIELD_NAME, urlPrefx));
        when(mike.asksFor(any()))
                .thenReturn(Collections.singletonList(sObjectConstructor.constructSObject(domainInfoShape)));

        initializeFramework.loadPortalInfo();

        assertThat(domain + urlPrefx, is(portalUrlProvider.getPortalFor(portalName)));
    }
}
