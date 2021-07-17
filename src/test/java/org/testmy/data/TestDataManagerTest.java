package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testmy.data.matchers.Matchers.hasField;
import static org.testmy.data.matchers.Matchers.hasName;
import static org.testmy.data.matchers.Matchers.ofShape;
import static org.testmy.data.matchers.ObjectMatchers.account;
import static org.testmy.data.matchers.ObjectMatchers.contact;
import static org.testmy.data.matchers.ObjectMatchers.opportunity;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.sforce.soap.partner.sobject.SObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.data.action.Clean;
import org.testmy.data.action.Insert;
import org.testmy.data.matchers.ConstructingMatcher;
import org.testmy.data.matchers.HasFields;
import org.testmy.data.matchers.Matchers;

@RunWith(MockitoJUnitRunner.class)
public class TestDataManagerTest {
    @Mock
    private RecordTypeIdProvider recordTypeIdProvider;
    @Mock
    private ReferenceAttributeTypeProvider referenceAttributeTypeProvider;
    @Mock
    private Insert salesforceAction;
    @Mock
    private Clean salesforceCleanAction;
    @Mock
    private SalesforceDataCache dataCache;
    @InjectMocks
    private TestDataManager dataManagerUnderTest;

    @Before
    public void before() {
        when(salesforceAction.insertObjects(any())).thenReturn(
                Collections.singletonList(UUID.randomUUID().toString()));
    }

    @Test
    public void ensureObject_guaranteesSObjectOfTheShape() {
        final ConstructingMatcher sObjectShape = ofShape(hasField("field", "value"), contact());

        final SObject sObject = dataManagerUnderTest.ensureObject(sObjectShape, salesforceAction);

        assertThat(sObject, is(sObjectShape));
    }

    @Test
    public void ensureObject_createsObjectInSalesforce() {
        final HasFields clientShape = ofShape(account());

        dataManagerUnderTest.ensureObject(clientShape, salesforceAction);

        verify(salesforceAction).insertObjects(anyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void ensureObject_createsObjectInSalesforceOnlyOnce() {
        final int amountOfCallsToEnsure = 10;
        final HasFields clientShape = ofShape(account());
        when(dataCache.findObject(clientShape)).thenReturn(
                Optional.empty(),
                Optional.of(new SObject("Account")));

        for (int i = 0; i < amountOfCallsToEnsure; i++) {
            dataManagerUnderTest.ensureObject(clientShape, salesforceAction);
        }

        verify(salesforceAction, times(1)).insertObjects(anyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void ensureObject_addsCreatedObjectIntoDataCache() {
        final HasFields clientShape = ofShape(account());
        final SObject sObject = new SObject("Account");
        when(dataCache.findObject(clientShape)).thenReturn(Optional.empty(), Optional.of(sObject));

        dataManagerUnderTest.ensureObject(clientShape, salesforceAction);

        verify(dataCache).addOjbects(any());;
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
        final Set<String> testIds = new HashSet<>();
        testIds.add(sfId1);
        testIds.add(sfId2);
        testIds.add(sfId3);
        when(dataCache.getIds()).thenReturn(testIds);

        dataManagerUnderTest.cleanData(salesforceCleanAction);

        verify(salesforceCleanAction).cleanData(testIds);
    }

    @Test
    public void ensureObjectIfAbsent_checksIfRecordOfTheSameShapeExistsInSalesforce() {
        final HasFields sObjectShape = ofShape(account());

        dataManagerUnderTest.ensureObjectIfAbsent(sObjectShape, salesforceAction);

        verify(salesforceAction).query(sObjectShape.toSoql());
    }

    @Test
    public void ensureObjectIfAbsent_usesObjectsFoundBefore() {
        final SObject objectFoundBefore = mock(SObject.class);
        final HasFields sObjectShape = ofShape(account());
        when(dataCache.findObject(sObjectShape)).thenReturn(Optional.of(objectFoundBefore));

        final SObject ensuredObjectWithIfAbsentCheck = dataManagerUnderTest.ensureObjectIfAbsent(sObjectShape,
                salesforceAction);

        assertThat(ensuredObjectWithIfAbsentCheck, is(objectFoundBefore));

        verify(salesforceAction, never()).query(sObjectShape.toSoql());
    }

    @Test
    public void ensureObjectIfAbsent_usesObjectFoundInSalesforce() {
        final HasFields sObjectShape = ofShape(account());
        final SObject objectFromSalesforce = new SObject("Account");
        when(salesforceAction.query(sObjectShape.toSoql())).thenReturn(Collections.singletonList(objectFromSalesforce));

        final SObject foundObject = dataManagerUnderTest.ensureObjectIfAbsent(sObjectShape, salesforceAction);

        assertThat(foundObject, is(objectFromSalesforce));
    }

    @Test
    public void ensureObjectIfAbsent_foundObjectsAreAddedIntoCache() {
        final HasFields sObjectShape = ofShape(account());
        final SObject objectFromSalesforce = new SObject("Account");
        when(salesforceAction.query(sObjectShape.toSoql())).thenReturn(Collections.singletonList(objectFromSalesforce));

        dataManagerUnderTest.ensureObjectIfAbsent(sObjectShape, salesforceAction);

        verify(dataCache).addOjbects(objectFromSalesforce);
    }

    @Test
    public void ensureObjectIfAbsent_createsObjectIfNotFound() {
        final HasFields sObjectShape = ofShape(account());
        when(salesforceAction.query(sObjectShape.toSoql())).thenReturn(Collections.emptyList());

        final SObject createdObject = dataManagerUnderTest.ensureObjectIfAbsent(sObjectShape, salesforceAction);

        verify(salesforceAction).insertObjects(anyList());
        verify(dataCache).addOjbects(createdObject);;
    }

    @Test
    public void ensureObjectIfAbsent_foundObjectsAreNotCleaned() {
        final HasFields sObjectShape = ofShape(account());
        final String existingSfId = "123";
        final SObject objectFromSalesforce = new SObject("Account");
        objectFromSalesforce.setId(existingSfId);
        when(dataCache.getIds()).thenReturn(Collections.singleton(existingSfId));
        when(salesforceAction.query(sObjectShape.toSoql())).thenReturn(Collections.singletonList(objectFromSalesforce));

        dataManagerUnderTest.ensureObjectIfAbsent(sObjectShape, salesforceAction);
        dataManagerUnderTest.cleanData(salesforceCleanAction);

        verify(salesforceCleanAction, never()).cleanData(Collections.singleton(existingSfId));
    }

    @Test
    public void ensureObjects_doesNotGuaranteeOrderOfReturnedObjects() {
        final HasFields existingShape = ofShape(account(), hasName("Test Client 1"));
        final HasFields shapeWithoutRecord = ofShape(account(), hasName("Test Client 2"));
        final List<HasFields> shapesToCreateInBulk = Arrays.asList(
                existingShape,
                shapeWithoutRecord);
        when(dataCache.findObject(existingShape)).thenReturn(Optional.of(new SObject()));

        final List<SObject> sObjects = dataManagerUnderTest.ensureObjects(shapesToCreateInBulk, salesforceAction);

        for (int i = 0; i < shapesToCreateInBulk.size(); i++) {
            assertThat(sObjects.get(i), not(shapesToCreateInBulk.get(i)));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void ensureObjects_createsObjectsInBulk() {
        final ArgumentCaptor<List<SObject>> captureInsertedObject = ArgumentCaptor.forClass(List.class);
        final List<HasFields> shapesToCreateInBulk = Arrays.asList(
                ofShape(account(), hasName("Test Client 1")),
                ofShape(account(), hasName("Test Client 2")),
                ofShape(account(), hasName("Test Client 3")));
        when(salesforceAction.insertObjects(any())).thenReturn(
                IntStream.range(0, shapesToCreateInBulk.size()).mapToObj(i -> UUID.randomUUID().toString())
                        .collect(Collectors.toList()));

        dataManagerUnderTest.ensureObjects(shapesToCreateInBulk, salesforceAction);

        verify(salesforceAction).insertObjects(captureInsertedObject.capture());
        assertThat(captureInsertedObject.getValue(), hasItems(shapesToCreateInBulk.toArray(new HasFields[0])));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void ensureObjects_doesNotCreateObjectsForDataAlreadyInCache() {
        final ArgumentCaptor<List<SObject>> captureInsertedObject = ArgumentCaptor.forClass(List.class);
        final HasFields existingShape = ofShape(account(), hasName("Test Client 1"));
        final HasFields missingShape = ofShape(account(), hasName("Test Client 2"));
        final List<HasFields> shapesToCreateInBulk = Arrays.asList(existingShape, missingShape);
        when(dataCache.findObject(existingShape)).thenReturn(Optional.of(new SObject("Account")));

        dataManagerUnderTest.ensureObjects(shapesToCreateInBulk, salesforceAction);

        verify(salesforceAction).insertObjects(captureInsertedObject.capture());
        assertThat(captureInsertedObject.getValue(), contains(missingShape));
        assertThat(captureInsertedObject.getValue(), not(contains(existingShape)));
    }
}
