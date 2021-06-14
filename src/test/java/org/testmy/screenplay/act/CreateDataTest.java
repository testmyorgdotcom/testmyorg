package org.testmy.screenplay.act;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.testmy.data.matchers.Matchers.hasId;
import static org.testmy.data.matchers.Matchers.hasName;
import static org.testmy.data.matchers.Matchers.ofShape;
import static org.testmy.data.matchers.ObjectMatchers.account;

import java.util.List;
import java.util.UUID;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.data.TestDataManager;
import org.testmy.data.matchers.ConstructingMatcher;
import org.testmy.screenplay.ability.AbilityProvider;
import org.testmy.screenplay.ability.CallPartnerSoapApi;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class CreateDataTest {
    @Mock
    AbilityProvider abilityProvider;
    @Mock
    CallPartnerSoapApi mockedPartnerApiAbility;
    @Mock
    PartnerConnection partnerConnection;
    TestDataManager testDataManager = new TestDataManager();
    Actor mike = Actor.named("Mike");

    @Before
    public void before() throws ConnectionException {
        when(abilityProvider.as(any(), eq(CallPartnerSoapApi.class))).thenReturn(mockedPartnerApiAbility);
        when(mockedPartnerApiAbility.ensureConnection()).thenReturn(partnerConnection);
        final SaveResult saveResult = new SaveResult();
        saveResult.setId(UUID.randomUUID().toString());
        saveResult.setSuccess(true);
        when(partnerConnection.create(any())).thenReturn(new SaveResult[] {
                saveResult
        });
    }

    @Test
    public void createRecord_storesDataIntoCacheAndSalesforce() {
        final ConstructingMatcher ofShape = ofShape(account(), hasName("accountName"));
        final CreateData createDataRecord = createRecordForWithIjectedMocks(ofShape);
        createDataRecord.performAs(mike);
        final List<SObject> testData = testDataManager.getData();
        assertThat(testData, hasSize(1));
        assertThat(testData.get(0), hasId());
    }

    private CreateData createRecordForWithIjectedMocks(final ConstructingMatcher ofShape) {
        final CreateData createDataRecord = new CreateData(ofShape);
        createDataRecord.testDataManager = testDataManager;
        createDataRecord.abilityProvider = abilityProvider;
        return createDataRecord;
    }
}
