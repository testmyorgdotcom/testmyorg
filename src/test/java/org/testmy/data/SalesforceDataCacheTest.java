package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThrows;
import static org.testmy.data.matchers.Matchers.hasId;
import static org.testmy.data.matchers.ObjectMatchers.account;
import static org.testmy.data.matchers.ObjectMatchers.contact;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import com.sforce.soap.partner.sobject.SObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.error.TestRuntimeException;

@RunWith(MockitoJUnitRunner.class)
public class SalesforceDataCacheTest {
    @InjectMocks
    SalesforceDataCache sfDataCache;

    @Test
    public void findObject_emptyOptionalIfNoData() {
        assertThat(sfDataCache.findObject(account()), is(Optional.empty()));
    }

    @Test
    public void addObjects_makesAddedObjectsAvailableForSearch() {
        final SObject account = getObjectWithId("Account");
        final SObject contact = getObjectWithId("Contact");

        sfDataCache.addOjbects(account, contact);

        assertThat(sfDataCache.findObject(account()).get(), is(account));
        assertThat(sfDataCache.findObject(contact()).get(), is(contact));
    }

    @Test
    public void addObjects_cannotAddObjectsWithoutId() {
        final SObject withoutId1 = getObjectWithoutId("Contact");
        final SObject withoutId2 = getObjectWithoutId("Contact");

        final TestRuntimeException tre = assertThrows(TestRuntimeException.class, () -> {
            sfDataCache.addOjbects(withoutId1, withoutId2);
        });

        assertThat(tre.getMessage(), is("Cannot add objects without Id: " + Arrays.asList(withoutId1, withoutId2)));
    }

    @Test
    public void addObjects_cannotAddObjectsWithoutType() {
        final SObject withoutType = new SObject();
        withoutType.setId("non-empty id");

        final TestRuntimeException tre = assertThrows(TestRuntimeException.class, () -> {
            sfDataCache.addOjbects(withoutType);
        });

        assertThat(tre.getMessage(), is("Cannot add objects without Type: " + Arrays.asList(withoutType)));
    }

    @Test
    public void addObjects_objectsWithIdsWillBeAddedToCache() {
        final SObject contactWithoutId = getObjectWithoutId("Contact");
        final SObject contactWithId = getObjectWithId("Contact", "123");

        assertThrows(TestRuntimeException.class, () -> {
            sfDataCache.addOjbects(contactWithoutId, contactWithId);
        });

        assertThat(sfDataCache.findObject(hasId("123")).get(), is(contactWithId));
    }

    @Test
    public void findObjects_findsAllMatchingObjectsAvaialbleInCache() {
        final SObject account1 = getObjectWithId("Account");
        final SObject account2 = getObjectWithId("Account");
        final SObject contact = getObjectWithId("Contact");

        sfDataCache.addOjbects(account1, account2, contact);

        assertThat(sfDataCache.findObjects(account()), hasSize(2));
        assertThat(sfDataCache.findObjects(account()), contains(account1, account2));
    }

    @Test
    public void getIds_providesSalesforceIdsOfAllObjectsInTheCache() {
        final SObject account1 = getObjectWithId("Account");
        final SObject account2 = getObjectWithId("Account");
        final SObject contact1 = getObjectWithId("Contact");
        final SObject contact2 = getObjectWithId("Contact");

        sfDataCache.addOjbects(account1, account2, contact1, contact2);

        assertThat(sfDataCache.getIds(), hasSize(4));
    }

    private SObject getObjectWithId(final String type) {
        return getObjectWithId(type, UUID.randomUUID().toString());
    }

    private SObject getObjectWithId(final String type,
            final String sfId) {
        final SObject result = new SObject(type);
        result.setId(sfId);
        return result;
    }

    private SObject getObjectWithoutId(final String type) {
        final SObject result = new SObject(type);
        return result;
    }
}
