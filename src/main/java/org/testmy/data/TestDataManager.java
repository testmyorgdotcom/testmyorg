package org.testmy.data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.bind.XmlObject;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.Matcher;
import org.testmy.config.Config;
import org.testmy.data.action.Clean;
import org.testmy.data.action.Insert;
import org.testmy.data.matchers.ConstructingMatcher;
import org.testmy.data.matchers.HasFields;

import net.thucydides.core.annotations.Shared;

public class TestDataManager implements Config {
    @Shared
    private RecordTypeIdProvider recordTypeIdProvider;
    @Shared
    private ReferenceAttributeTypeProvider referenceAttributeTypeProvider;
    private List<SObject> sObjects = new LinkedList<>();
    private Set<String> sfIdsOfObjectsFoundInSalesforce = new HashSet<>();

    public Optional<SObject> findObject(Matcher<SObject> sObjectShape) {
        return sObjects.stream().filter(sObjectShape::matches).findFirst();
    }

    public List<SObject> findObjects(final Matcher<SObject> sObjectShape) {
        return sObjects.stream().filter(sObjectShape::matches).collect(Collectors.toList());
    }

    public SObject ensureObject(final ConstructingMatcher sObjectShape,
            final Insert salesforceAction) {
        return findObject(sObjectShape).orElseGet(() -> {
            final String sfId = store(sObjectShape, salesforceAction);
            final SObject result = constructSObject(sObjectShape);
            result.setId(sfId);
            sObjects.add(result);
            return result;
        });
    }

    private String store(final ConstructingMatcher sObjectShape,
            final Insert salesforceAction) {
        final SObject sObjectToStore = constructSObjectToStore(sObjectShape);
        return salesforceAction.insert(sObjectToStore);
    }

    // consider private, access = default for testability
    SObject constructSObjectToStore(final ConstructingMatcher sObjectShape) {
        final SObject sObjectToStore = constructSObject(sObjectShape);
        replaceReferenceAttributesIfPresent(sObjectToStore);
        replaceWithRecordTypeIdIfPresent(sObjectToStore);
        return sObjectToStore;
    }

    private SObject constructSObject(final ConstructingMatcher sObjectShape) {
        final String tempType = "Type must be initialized 1st, but can be changed later";
        final SObject result = new SObject(tempType);
        sObjectShape.visitForUpdate(result);
        if (tempType.equals(result.getType())) {
            throw new IllegalArgumentException("shape without 'type' property cannot construct SObject");
        }
        return result;
    }

    private void replaceReferenceAttributesIfPresent(final SObject result) {
        final Map<String, SObject> referenceAttributesWithNewValues = constructReferenceAttributes(result);
        referenceAttributesWithNewValues.forEach((fullName,
                referenceSObject) -> {
            result.removeField(fullName);
            final String simpleReferenceFieldName = fullName.split("\\.")[0];
            result.setSObjectField(simpleReferenceFieldName, referenceAttributesWithNewValues.get(fullName));
        });
    }

    private Map<String, SObject> constructReferenceAttributes(final SObject mainObject) {
        final Iterator<XmlObject> xmlObjectIt = mainObject.getChildren();
        final Map<String, SObject> referenceAttributesWithNewValues = new HashMap<>();
        while (xmlObjectIt.hasNext()) {
            final XmlObject xmlObject = xmlObjectIt.next();
            if (isReferenceAttribute(xmlObject)
                    && !isRecordTypeReference(xmlObject)) {
                final SObject referenceObject = constructReferenceObject(mainObject, xmlObject);
                final String complexReferenceFieldName = xmlObject.getName().getLocalPart();
                referenceAttributesWithNewValues.put(complexReferenceFieldName, referenceObject);
            }
        }
        return referenceAttributesWithNewValues;
    }

    private SObject constructReferenceObject(final SObject mainObject,
            final XmlObject xmlObject) {
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

    private boolean isRecordTypeReference(XmlObject xmlObject) {
        return FIELD_RECORDTYPE_DEVELOPERNAME.equals(xmlObject.getName().getLocalPart());
    }

    private void replaceWithRecordTypeIdIfPresent(final SObject sObject) {
        final Object recordTypeName = sObject.getField(FIELD_RECORDTYPE_DEVELOPERNAME);
        if (null != recordTypeName) {
            final String recordTypeId = recordTypeIdProvider.getIdFor(sObject.getType(), recordTypeName.toString());
            sObject.setField(FIELD_RECORDTYPEID, recordTypeId);
            sObject.removeField(FIELD_RECORDTYPE_DEVELOPERNAME);
        }
    }

    public void cacheExistingShape(final ConstructingMatcher ofShape) {
        final SObject sObject = constructSObject(ofShape);
        if (StringUtils.isBlank(sObject.getId())) {
            throw new IllegalArgumentException("Cannot add objects without Id: " + sObject);
        }
        sObjects.add(sObject);
    }

    public void cleanData(final Clean salesforceCleanAction) {
        final Set<String> sfIdsToDelete = sObjects.stream()
                .map(so -> so.getId())
                .filter(id -> !sfIdsOfObjectsFoundInSalesforce.contains(id))
                .collect(Collectors.toSet());
        if (!sfIdsToDelete.isEmpty()) {
            salesforceCleanAction.cleanData(sfIdsToDelete);
        }
    }

    public SObject ensureObjectIfAbsent(final HasFields sObjectShape,
            final Insert salesforceAction) {
        return findObject(sObjectShape).orElseGet(() -> {
            final Optional<SObject> foundObject = salesforceAction.query(sObjectShape.toSoql()).stream().findFirst();
            if (foundObject.isPresent()) {
                final SObject fo = foundObject.get();
                sObjects.add(fo);
                sfIdsOfObjectsFoundInSalesforce.add(fo.getId());
            }
            return foundObject.orElse(ensureObject(sObjectShape, salesforceAction));
        });
    }

    public void ensureObjects(final List<HasFields> shapesToCreateInBulk,
            final Insert salesforceAction) {
    }
}
