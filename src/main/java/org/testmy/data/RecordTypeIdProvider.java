package org.testmy.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;

public class RecordTypeIdProvider {
    private Map<String, List<RecordType>> recordTypesByObjectType = new HashMap<>();

    public Optional<String> getIdFor(final String objectType,
            final String recordTypeName) {
        if (!recordTypesByObjectType.containsKey(objectType)) {
            return Optional.empty();
        }
        final List<RecordType> recordTypesForObject = recordTypesByObjectType.get(objectType);
        return recordTypesForObject.stream()
                .filter(rt -> rt.getName().equals(recordTypeName))
                .findFirst()
                .map(rt -> rt.getId());
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
