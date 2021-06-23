package org.testmy.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testmy.error.TestRuntimeException;

import lombok.AllArgsConstructor;
import lombok.Data;

public class RecordTypeIdProvider {
    private Map<String, List<RecordType>> recordTypesByObjectType = new HashMap<>();

    public String getIdFor(final String objectType,
            final String recordTypeName) {
        if (!recordTypesByObjectType.containsKey(objectType)) {
            throw new TestRuntimeException("No record types loaded for object type: " + objectType);
        }
        final List<RecordType> recordTypesForObject = recordTypesByObjectType.get(objectType);
        return recordTypesForObject.stream()
                .filter(rt -> rt.getName().equals(recordTypeName))
                .findFirst()
                .map(rt -> rt.getId())
                .orElseThrow(() -> new TestRuntimeException(
                        "No '" + recordTypeName
                                + "'' record type found for object type: " + objectType + ", "));
    }

    public void init(final Map<String, List<RecordType>> recordTypesByObjectType) {
        this.recordTypesByObjectType.putAll(recordTypesByObjectType);
    }

    @Data
    @AllArgsConstructor
    public static class RecordType {
        private String name;
        private String id;
    }
}
