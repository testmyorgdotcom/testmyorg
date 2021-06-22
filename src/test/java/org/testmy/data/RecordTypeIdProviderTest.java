package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.testmy.data.RecordTypeIdProvider.RecordType;
import org.testmy.error.TestRuntimeException;

public class RecordTypeIdProviderTest {
    RecordTypeIdProvider rtIdProvider = new RecordTypeIdProvider();

    @Test
    public void getIdFor_takesDataFromCache() {
        final String recordTypeName = "Sales Opportunity";
        final String recordTypeId = "ABC";
        final String objectType = "Account";
        initRecordType(objectType, recordTypeName, recordTypeId);
        assertThat(rtIdProvider.getIdFor(objectType, recordTypeName), is(recordTypeId));
    }

    @Test(expected = TestRuntimeException.class)
    public void getIdFor_ExceptionIfNoRecordTypeWithName() {
        final String recordTypeName = "Sales Opportunity";
        final String recordTypeId = "ABC";
        final String objectType = "Account";
        initRecordType(objectType, recordTypeName, recordTypeId);
        rtIdProvider.getIdFor(objectType, "Non Existing Record Type");
    }

    @Test(expected = TestRuntimeException.class)
    public void getIdFor_OptionalEmptyIfNoDataForObjectType() {
        rtIdProvider.getIdFor("Object Type with no data", "Any Record Type");
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
