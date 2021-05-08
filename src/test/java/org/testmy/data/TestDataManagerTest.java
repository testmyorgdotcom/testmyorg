package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testmy.data.matchers.Matchers.account;
import static org.testmy.data.matchers.Matchers.hasField;
import static org.testmy.data.matchers.Matchers.hasId;
import static org.testmy.data.matchers.Matchers.hasName;
import static org.testmy.data.matchers.Matchers.ofShape;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.IntStream;

import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.data.matchers.ConstructingMatcher;

@RunWith(MockitoJUnitRunner.class)
public class TestDataManagerTest {
    @Mock
    private Function<SObject[], SaveResult[]> storeToSalesforce;

    @InjectMocks
    private TestDataManager dataManagerUnderTest;

    @Before
    public void before() {
        when(storeToSalesforce.apply(any())).thenReturn(new SaveResult[] {
                new SaveResult()
        });
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
    public void findsObjectByMathcer() {
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
    public void createsObjectIfNotExistsWithASimilarShape() {
        final String fieldName = "field", fieldValue = "value";
        final ConstructingMatcher sObjectShape = hasField(fieldName, fieldValue);

        final SObject sObject = dataManagerUnderTest.ensureObject(sObjectShape, storeToSalesforce);

        assertThat(sObject, is(sObjectShape));
    }

    @Test
    public void storesCreatedObjectInCache() {
        final String fieldName = "field", fieldValue = "value";
        final ConstructingMatcher sObjectShape = hasField(fieldName, fieldValue);

        final SObject sObject = dataManagerUnderTest.ensureObject(sObjectShape, storeToSalesforce);

        assertThat(dataManagerUnderTest.getData().contains(sObject), is(true));
    }

    @Test
    public void createsObjectInSalesforce() {
        final String sfId = "Salesforce Id";
        final SaveResult sr = new SaveResult();
        sr.setId(sfId);
        final String clientName = "Test Client";
        final ConstructingMatcher clientShape = ofShape(
                account(),
                hasName(clientName));
        when(storeToSalesforce.apply(any())).thenReturn(new SaveResult[] {
                sr
        });

        final SObject sObject = dataManagerUnderTest.ensureObject(clientShape, storeToSalesforce);

        assertThat(sObject, hasId());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void notYetSupportedFieldValuesShapes() {
        final String fieldName = "field", fieldValue = "value";
        final Matcher<String> fieldValueShape = is(equalTo(fieldValue));
        final ConstructingMatcher sObjectShape = hasField(fieldName, fieldValueShape);

        dataManagerUnderTest.ensureObject(sObjectShape, storeToSalesforce);
    }

    @Test
    public void addDataToCacheIfWithIdAndType() {
        final ConstructingMatcher ofShape = ofShape(
                account(),
                hasId("003xyz..."),
                hasName("Test Client"));

        dataManagerUnderTest.cacheExistingShape(ofShape);

        assertThat(dataManagerUnderTest.findObject(ofShape).isPresent(), is(true));
        assertThat(dataManagerUnderTest.findObject(ofShape).get(), ofShape);

        verify(storeToSalesforce, never()).apply(any());
    }

    @Test(expected = IllegalArgumentException.class)
    public void failAddIfIdIsMissing() {
        final ConstructingMatcher ofShape = ofShape(
                account(),
                hasName("Test Client"));

        dataManagerUnderTest.cacheExistingShape(ofShape);
    }

    @Test(expected = IllegalArgumentException.class)
    public void failAddIfTypeIsMissing() {
        final ConstructingMatcher ofShape = ofShape(
                hasId("003xyz..."),
                hasName("Test Client"));

        dataManagerUnderTest.cacheExistingShape(ofShape);
    }
}
