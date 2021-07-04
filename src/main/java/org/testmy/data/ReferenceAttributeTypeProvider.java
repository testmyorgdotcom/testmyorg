package org.testmy.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.FieldType;

import org.testmy.error.TestRuntimeException;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class ReferenceAttributeTypeProvider {
    private Map<String, List<ReferenceAttribute>> referenceAttributesByType = new HashMap<>();

    public String getTypeFor(final String objectType,
            final String attribute) {
        if (!referenceAttributesByType.containsKey(objectType)) {
            throw new TestRuntimeException("No reference attributes type loaded for: " + objectType);
        }
        final List<ReferenceAttribute> referenceAttributes = referenceAttributesByType.get(objectType);
        return referenceAttributes.stream()
                .filter(rt -> rt.getReferenceAttribute().equals(attribute))
                .findFirst()
                .map(rt -> rt.getReferenceAttributeType())
                .orElseThrow(() -> new TestRuntimeException(
                        "No attribute '" + attribute + "' for object type: " + objectType));
    }

    public void init(final List<DescribeSObjectResult> objectsDescription) {
        objectsDescription.forEach(objectDescription -> {
            final String objectType = objectDescription.getName();
            for (final Field field : objectDescription.getFields()) {
                if (FieldType.reference.equals(field.getType())) {
                    final List<ReferenceAttribute> referenceAttributes = referenceAttributesByType.computeIfAbsent(
                            objectType,
                            ot -> new ArrayList<>());
                    referenceAttributes.add(new ReferenceAttribute(
                            field.getRelationshipName(),
                            field.getReferenceTo()[0]));
                }
            }
        });
    }

    @AllArgsConstructor
    @Getter
    protected static class ReferenceAttribute {
        private String referenceAttribute;
        private String referenceAttributeType;
    }
}
