package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testmy.data.matchers.Matchers.hasField;
import static org.testmy.data.matchers.Matchers.hasId;
import static org.testmy.data.matchers.Matchers.hasName;
import static org.testmy.data.matchers.Matchers.ofShape;
import static org.testmy.data.matchers.ObjectMatchers.account;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

import com.sforce.soap.partner.sobject.SObject;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.config.Config;
import org.testmy.data.matchers.ConstructingMatcher;

@RunWith(MockitoJUnitRunner.class)
public class TestDataManagerTest {
    @Mock
    private RecordTypeIdProvider recordTypeIdProvider;
    @Mock
    private SalesforceDataAction salesforceAction;
    @InjectMocks
    private TestDataManager dataManagerUnderTest;

    @Before
    public void before() {
        when(salesforceAction.insert(any())).thenReturn(UUID.randomUUID().toString());
    }

    @Test
    public void hasDataCache() {
        assertThat(dataManagerUnderTest.getData(), is(emptyCollectionOf(SObject.class)));
    }

    @Test
    public void canAddDataIntoCache() {
        final int elementsInCache = 3;
        IntStream.range(0, elementsInCache).forEach(i -> dataManagerUnderTest.addToCache(new SObject()));
        assertThat(dataManagerUnderTest.getData(), hasSize(elementsInCache));
    }

    @Test
    public void findObject_usesMatcher() {
        final String fieldName = "field", fieldValue = "value";
        final Matcher<SObject> objectShape = hasField(fieldName, fieldValue);
        final SObject objInCache = new SObject();
        objInCache.setField(fieldName, fieldValue);
        dataManagerUnderTest.addToCache(objInCache);
        final Optional<SObject> foundObject = dataManagerUnderTest.findObject(objectShape);
        assertThat(foundObject.isPresent(), is(true));
        assertThat(foundObject.get(), is(objectShape));
    }

    @Test
    public void ensureObject_createsObjectIfNotExistsWithASimilarShape() {
        final String fieldName = "field", fieldValue = "value";
        final ConstructingMatcher sObjectShape = hasField(fieldName, fieldValue);
        final SObject sObject = dataManagerUnderTest.ensureObject(sObjectShape, salesforceAction);
        assertThat(sObject, is(sObjectShape));
    }

    @Test
    public void ensureObject_storesCreatedObjectInCache() {
        final String fieldName = "field", fieldValue = "value";
        final ConstructingMatcher sObjectShape = hasField(fieldName, fieldValue);
        final SObject sObject = dataManagerUnderTest.ensureObject(sObjectShape, salesforceAction);
        assertThat(dataManagerUnderTest.getData().contains(sObject), is(true));
    }

    @Test
    public void ensureObject_createsObjectInSalesforce() {
        final String sfId = "Salesforce Id";
        final String clientName = "Test Client";
        final ConstructingMatcher clientShape = ofShape(account(), hasName(clientName));
        when(salesforceAction.insert(any())).thenReturn(sfId);
        final SObject sObject = dataManagerUnderTest.ensureObject(clientShape, salesforceAction);
        assertThat(sObject, hasId(sfId));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void ensureObject_notYetSupportedFieldValuesShapes() {
        final String fieldName = "field", fieldValue = "value";
        final Matcher<String> fieldValueShape = is(equalTo(fieldValue));
        final ConstructingMatcher sObjectShape = hasField(fieldName, fieldValueShape);
        dataManagerUnderTest.ensureObject(sObjectShape, salesforceAction);
    }

    @Test
    public void cacheExistingShape_addDataToCacheDirectlySKippingStoreInSalesforce() {
        final ConstructingMatcher ofShape = ofShape(account(), hasId("003xyz..."), hasName("Test Client"));
        dataManagerUnderTest.cacheExistingShape(ofShape);
        assertThat(dataManagerUnderTest.findObject(ofShape).isPresent(), is(true));
        assertThat(dataManagerUnderTest.findObject(ofShape).get(), ofShape);
        verify(salesforceAction, never()).insert(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cacheExistingShape_failIfTypeIsMissing() {
        final ConstructingMatcher ofShape = ofShape(account(), hasName("Test Client"));
        dataManagerUnderTest.cacheExistingShape(ofShape);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cacheExistingShape_failIfIdIsMissing() {
        final ConstructingMatcher ofShape = ofShape(account(), hasName("Test Client"));
        dataManagerUnderTest.cacheExistingShape(ofShape);
    }

    @Test
    public void constructSObject_replaceRecordTypeNameWithRecordTypeId() {
        final String recordTypeId = "0x123...";
        final String recordTypeName = "Sales Opportunity";
        final ConstructingMatcher ofShape = ofShape(
                hasField("type", Config.OBJECT_OPPORTUNITY),
                hasField(Config.FIELD_RECORDTYPE_DEVELOPERNAME, recordTypeName));
        when(recordTypeIdProvider.getIdFor(Config.OBJECT_OPPORTUNITY, recordTypeName)).thenReturn(recordTypeId);
        final SObject sObject = dataManagerUnderTest.constructSObject(ofShape);
        assertThat(sObject.getField(Config.FIELD_RECORDTYPEID), is(recordTypeId));
        assertThat(sObject.getField(Config.FIELD_RECORDTYPE_DEVELOPERNAME), is(nullValue()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructSObject_faileIfTypeIsMissing() {
        final ConstructingMatcher ofShape = ofShape(hasId("003xyz..."), hasName("Test Client"));
        dataManagerUnderTest.constructSObject(ofShape);
    }
}
