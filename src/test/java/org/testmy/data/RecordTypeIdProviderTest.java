package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.Test;
import org.testmy.data.RecordTypeIdProvider.RecordType;

public class RecordTypeIdProviderTest {
    RecordTypeIdProvider rtIdProvider = new RecordTypeIdProvider();

    @Test
    public void getIdFor_takesDataFromCache() {
        final String recordTypeName = "Sales Opportunity";
        final String recordTypeId = "ABC";
        final String objectType = "Account";
        initRecordType(objectType, recordTypeName, recordTypeId);
        assertThat(rtIdProvider.getIdFor(objectType, recordTypeName), is(Optional.of(recordTypeId)));
    }

    @Test
    public void getIdFor_OptionalEmptyIfNoRecordTypeWithName() {
        final String recordTypeName = "Sales Opportunity";
        final String recordTypeId = "ABC";
        final String objectType = "Account";
        initRecordType(objectType, recordTypeName, recordTypeId);
        assertThat(rtIdProvider.getIdFor(objectType, "Non Existing Record Type"), is(Optional.empty()));
    }

    @Test
    public void getIdFor_OptionalEmptyIfNoDataForObjectType() {
        assertThat(rtIdProvider.getIdFor("Object Type with no data", "Any Record Type"), is(Optional.empty()));
    }

    private void initRecordType(final String objectType,
            final String recordTypeName,
            final String recordTypeId) {
        final Map<String, List<RecordType>> recordTypesByObjectType = new HashMap<String, List<RecordType>>() {
            {
                put(objectType, Arrays.asList(new RecordType(recordTypeName, recordTypeId)));
            }
        };
        rtIdProvider.init(recordTypesByObjectType);
    }
}
