package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testmy.data.matchers.Matchers.hasField;
import static org.testmy.data.matchers.Matchers.hasId;
import static org.testmy.data.matchers.Matchers.hasName;
import static org.testmy.data.matchers.Matchers.ofShape;
import static org.testmy.data.matchers.ObjectMatchers.account;
import static org.testmy.data.matchers.ObjectMatchers.contact;
import static org.testmy.data.matchers.ObjectMatchers.opportunity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;

import com.sforce.soap.partner.sobject.SObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.data.matchers.ConstructingMatcher;
import org.testmy.data.matchers.HasFields;

@RunWith(MockitoJUnitRunner.class)
public class TestDataManagerTest {
    @Mock
    private RecordTypeIdProvider recordTypeIdProvider;
    @Mock
    private ReferenceAttributeTypeProvider referenceAttributeTypeProvider;
    @Mock
    private SalesforceInsertDataAction salesforceAction;
    @Mock
    private SalesforceCleanDataAction salesforceCleanAction;
    @InjectMocks
    private TestDataManager dataManagerUnderTest;

    @Before
    public void before() {
        when(salesforceAction.insert(any())).thenReturn(UUID.randomUUID().toString());
    }

    @Test
    public void ensureObject_guaranteesSObjectOfTheProvidedShape() {
        final ConstructingMatcher sObjectShape = ofShape(hasField("field", "value"), contact());

        final SObject sObject = dataManagerUnderTest.ensureObject(sObjectShape, salesforceAction);

        assertThat(sObject, is(sObjectShape));
    }

    @Test
    public void ensureObject_createsObjectInSalesforce() {
        final HasFields clientShape = ofShape(account());

        dataManagerUnderTest.ensureObject(clientShape, salesforceAction);

        verify(salesforceAction).insert(any());
    }

    @Test
    public void ensureObject_createsObjectInSalesforceOnlyOnce() {
        final int amountOfCallsToEnsure = 10;
        final HasFields clientShape = ofShape(account());

        for (int i = 0; i < amountOfCallsToEnsure; i++) {
            dataManagerUnderTest.ensureObject(clientShape, salesforceAction);
        }

        verify(salesforceAction, times(1)).insert(any());
    }

    @Test
    public void findObject_canFindEnsuredObjectsOnly() {
        final HasFields objectShape = ofShape(account());

        assertThat(dataManagerUnderTest.findObject(objectShape), is(Optional.empty()));

        dataManagerUnderTest.ensureObject(objectShape, salesforceAction);

        assertThat(dataManagerUnderTest.findObject(objectShape).get(), objectShape);
    }

    @Test
    public void findObjects_canFindEnsuredObjectsOnly() {
        final HasFields objectShape = ofShape(account());

        assertThat(dataManagerUnderTest.findObjects(objectShape), is(empty()));

        dataManagerUnderTest.ensureObject(objectShape, salesforceAction);

        assertThat(dataManagerUnderTest.findObjects(objectShape), contains(objectShape));
    }

    @Test
    public void findObjects_findsAllMatchingObjects() {
        final HasFields objectShape1 = ofShape(account(), hasName("Client 1"));
        final HasFields objectShape2 = ofShape(account(), hasName("Client 2"));
        final HasFields objectShape3 = ofShape(contact());

        dataManagerUnderTest.ensureObject(objectShape1, salesforceAction);
        dataManagerUnderTest.ensureObject(objectShape2, salesforceAction);
        dataManagerUnderTest.ensureObject(objectShape3, salesforceAction);

        assertThat(dataManagerUnderTest.findObjects(account()), hasSize(2));
        assertThat(dataManagerUnderTest.findObjects(account()), containsInAnyOrder(objectShape1, objectShape2));
    }

    @Test
    public void cacheExistingShape_makesItSearchableWithoutStoringToSalesforce() {
        final ConstructingMatcher ofShape = ofShape(account(), hasId("003xyz..."), hasName("Test Client"));

        assertThat(dataManagerUnderTest.findObject(ofShape), is(Optional.empty()));

        dataManagerUnderTest.cacheExistingShape(ofShape);

        assertThat(dataManagerUnderTest.findObject(ofShape).get(), ofShape);
        verify(salesforceAction, never()).insert(any());
    }

    @Test
    public void cacheExistingShape_failIfShapeWithoutType() {
        final ConstructingMatcher ofShape = ofShape(hasName("Test Client"));

        final IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> {
            dataManagerUnderTest.cacheExistingShape(ofShape);
        });

        assertThat(iae.getMessage(), containsString("shape without 'type' property cannot construct SObject"));
    }

    @Test
    public void cacheExistingShape_failIfShapeWithoutId() {
        final ConstructingMatcher ofShape = ofShape(account(), hasName("Test Client"));

        final IllegalArgumentException iae = assertThrows(IllegalArgumentException.class, () -> {
            dataManagerUnderTest.cacheExistingShape(ofShape);
        });

        assertThat(iae.getMessage(), containsString("Cannot add objects without Id"));
    }

    @Test
    public void constructSObjectToStore_replacesRecordTypeNameWithRecordTypeId() {
        final String recordTypeId = "0x123...";
        final ConstructingMatcher ofShape = ofShape(
                opportunity(),
                hasField("RecordType.DeveloperName", "Sales_Opportunity"));
        when(recordTypeIdProvider.getIdFor("Opportunity", "Sales_Opportunity")).thenReturn(recordTypeId);

        final SObject sObject = dataManagerUnderTest.constructSObjectToStore(ofShape);

        assertThat(sObject.getField("RecordTypeId"), is(recordTypeId));
        assertThat(sObject.getField("RecordType.DeveloperName"), is(nullValue()));
    }

    @Test
    public void constructSObjectToStore_replacesComplexFieldsWithReference() {
        final String externalId = "123";
        final ConstructingMatcher ofShape = ofShape(
                contact(),
                hasField("CustomReference__r.ExternalId", externalId));
        when(referenceAttributeTypeProvider.getTypeFor("Contact", "CustomReference__r"))
                .thenReturn("Account");

        final SObject sObject = dataManagerUnderTest.constructSObjectToStore(ofShape);

        assertThat(sObject.getField("CustomReference__r.ExternalId"), is(nullValue()));
        assertThat(sObject.getSObjectField("CustomReference__r"), instanceOf(SObject.class));

        final SObject refObject = (SObject) sObject.getSObjectField("CustomReference__r");
        assertThat(refObject, is(account()));
        assertThat(refObject.getField("ExternalId"), is(externalId));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructSObjectToStore_faileIfTypeIsMissing() {
        dataManagerUnderTest.constructSObjectToStore(hasName("Without Type"));
    }

    @Test
    public void cleanData_deletesCreatedRecords() {
        final String sfId1 = "1", sfId2 = "2", sfId3 = "3";
        when(salesforceAction.insert(any())).thenReturn(sfId1, sfId2, sfId3);
        dataManagerUnderTest.ensureObject(account(), salesforceAction);
        dataManagerUnderTest.ensureObject(contact(), salesforceAction);
        dataManagerUnderTest.ensureObject(opportunity(), salesforceAction);

        dataManagerUnderTest.cleanData(salesforceCleanAction);

        verify(salesforceCleanAction).cleanData(new HashSet<String>(Arrays.asList(sfId1, sfId2, sfId3)));
    }

    @Test
    public void ensureObjectIfAbsent_checksIfRecordOfTheSameShapeExistsInSalesforce() {
        final HasFields sObjectShape = ofShape(account());

        dataManagerUnderTest.ensureObjectIfAbsent(sObjectShape, salesforceAction);

        verify(salesforceAction).query(sObjectShape.toSoql());
    }

    @Test
    public void ensureObjectIfAbsent_usesEnsuredObjects() {
        final HasFields sObjectShape = ofShape(account());

        final SObject ensuredObject = dataManagerUnderTest.ensureObject(sObjectShape, salesforceAction);

        final SObject ensuredObjectWithIfAbsentCheck = dataManagerUnderTest.ensureObjectIfAbsent(sObjectShape,
                salesforceAction);

        assertThat(ensuredObjectWithIfAbsentCheck, is(ensuredObject));

        verify(salesforceAction, never()).query(sObjectShape.toSoql());
    }

    @Test
    public void ensureObjectIfAbsent_usesFoundObject() {
        final HasFields sObjectShape = ofShape(account());
        final SObject objectFromSalesforce = new SObject("Account");
        when(salesforceAction.query(sObjectShape.toSoql())).thenReturn(Collections.singletonList(objectFromSalesforce));

        final SObject foundObject = dataManagerUnderTest.ensureObjectIfAbsent(sObjectShape, salesforceAction);

        assertThat(foundObject, is(objectFromSalesforce));
    }

    @Test
    public void ensureObjectIfAbsent_foundObjectsAreAlsoUsedInEnsure() {
        final HasFields sObjectShape = ofShape(account());
        final SObject objectFromSalesforce = new SObject("Account");
        when(salesforceAction.query(sObjectShape.toSoql())).thenReturn(Collections.singletonList(objectFromSalesforce));

        dataManagerUnderTest.ensureObjectIfAbsent(sObjectShape, salesforceAction);

        final SObject ensuredObject = dataManagerUnderTest.ensureObject(sObjectShape, salesforceAction);

        assertThat(ensuredObject, is(objectFromSalesforce));
        verify(salesforceAction, never()).insert(any());
    }

    @Test
    public void ensureObjectIfAbsent_createsObjectIfNotFound() {
        final HasFields sObjectShape = ofShape(account());
        when(salesforceAction.query(sObjectShape.toSoql())).thenReturn(Collections.emptyList());

        final SObject createdObject = dataManagerUnderTest.ensureObjectIfAbsent(sObjectShape, salesforceAction);

        verify(salesforceAction).insert(any());

        assertThat(dataManagerUnderTest.ensureObject(sObjectShape, salesforceAction), is(createdObject));
    }

    @Test
    public void ensureObjectIfAbsent_foundObjectsAreNotCleaned() {
        final HasFields sObjectShape = ofShape(account());
        final String existingSfId = "123";
        final SObject objectFromSalesforce = new SObject("Account");
        objectFromSalesforce.setId(existingSfId);

        when(salesforceAction.query(sObjectShape.toSoql())).thenReturn(Collections.singletonList(objectFromSalesforce));

        dataManagerUnderTest.ensureObjectIfAbsent(sObjectShape, salesforceAction);

        dataManagerUnderTest.cleanData(salesforceCleanAction);

        verify(salesforceCleanAction, never()).cleanData(Collections.singleton(existingSfId));
    }
}
