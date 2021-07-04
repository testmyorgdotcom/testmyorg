package org.testmy.data;

import static org.testmy.data.matchers.Matchers.hasId;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.bind.XmlObject;

import org.hamcrest.Matcher;
import org.testmy.config.Config;
import org.testmy.data.matchers.ConstructingMatcher;

import net.thucydides.core.annotations.Shared;

public class TestDataManager implements Config {
    @Shared
    private RecordTypeIdProvider recordTypeIdProvider;
    @Shared
    private ReferenceAttributeTypeProvider referenceAttributeTypeProvider;
    private MatcherToSOjbectConstructor sObjectConstructor = new MatcherToSOjbectConstructor();
    private List<SObject> sObjects = new LinkedList<>();

    public List<SObject> getData() {
        return Collections.unmodifiableList(sObjects);
    }

    void addToCache(SObject object) {
        sObjects.add(object);
    }

    public SObject ensureObject(final ConstructingMatcher sObjectShape,
            final SalesforceDataAction salesforceAction) {
        return findObject(sObjectShape).orElseGet(() -> {
            final SObject result = constructSObject(sObjectShape);
            final String sfId = salesforceAction.insert(result);
            result.setId(sfId);
            addToCache(result);
            return result;
        });
    }

    SObject constructSObject(final ConstructingMatcher sObjectShape) {
        final SObject result = sObjectConstructor.constructSObject(sObjectShape);
        replaceWithRecordTypeIdIfPresent(result);
        replaceReferenceAttributesIfPresent(result);
        return result;
    }

    private void replaceReferenceAttributesIfPresent(final SObject result) {
        final Map<String, SObject> referenceAttributesWithNewValues = constructReferenceAttributes(result);
        for (final String complexReferenceFieldName : referenceAttributesWithNewValues.keySet()) {
            result.removeField(complexReferenceFieldName);
            final String simpleReferenceFieldName = complexReferenceFieldName.split("\\.")[0];
            result.setSObjectField(simpleReferenceFieldName,
                    referenceAttributesWithNewValues.get(complexReferenceFieldName));
        }
    }

    private Map<String, SObject> constructReferenceAttributes(final SObject result) {
        final Iterator<XmlObject> xmlObjectIt = result.getChildren();
        final Map<String, SObject> referenceAttributesWithNewValues = new HashMap<>();
        while (xmlObjectIt.hasNext()) {
            final XmlObject xmlObject = xmlObjectIt.next();
            if (isReferenceAttribute(xmlObject)) {
                final SObject referenceObject = constructReferenceObject(result, xmlObject);
                final String complexReferenceFieldName = xmlObject.getName().getLocalPart();
                referenceAttributesWithNewValues.put(complexReferenceFieldName, referenceObject);
            }
        }
        return referenceAttributesWithNewValues;
    }

    private SObject constructReferenceObject(SObject mainObject,
            XmlObject xmlObject) {
        final SObject referenceObject = new SObject("Any Type to be replaced");
        final String[] fieldParts = xmlObject.getName().getLocalPart().split("\\.");
        final String fieldName = fieldParts[0];
        final String referenceObjectExternalIdFieldName = fieldParts[1];
        referenceObject.setField(referenceObjectExternalIdFieldName, xmlObject.getValue());
        final String referenceObjectType = referenceAttributeTypeProvider.getTypeFor(mainObject.getType(), fieldName);
        referenceObject.setType(referenceObjectType);
        return referenceObject;
    }

    private boolean isReferenceAttribute(XmlObject xmlObject) {
        return xmlObject.getName().getLocalPart().contains(".");
    }

    private void replaceWithRecordTypeIdIfPresent(final SObject sObject) {
        final Object recordTypeName = sObject.getField(FIELD_RECORDTYPE_DEVELOPERNAME);
        if (null != recordTypeName) {
            final String recordTypeId = recordTypeIdProvider.getIdFor(sObject.getType(), recordTypeName.toString());
            sObject.setField(FIELD_RECORDTYPEID, recordTypeId);
            sObject.removeField(FIELD_RECORDTYPE_DEVELOPERNAME);
        }
    }

    public Optional<SObject> findObject(Matcher<SObject> sObjectShape) {
        return sObjects.stream().filter(sObjectShape::matches).findFirst();
    }

    public List<SObject> findObjects(final Matcher<SObject> sObjectShape) {
        return sObjects.stream().filter(sObjectShape::matches).collect(Collectors.toList());
    }

    public void cacheExistingShape(final ConstructingMatcher ofShape) {
        final SObject sObject = constructSObject(ofShape);
        if (!hasId().matches(sObject)) {
            throw new IllegalArgumentException("Cannot add objects without Id: " + sObject);
        }
        addToCache(sObject);
    }
}
