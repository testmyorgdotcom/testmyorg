package org.testmy.data;

import static org.testmy.data.matchers.Matchers.hasId;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.sforce.soap.partner.sobject.SObject;

import org.hamcrest.Matcher;
import org.testmy.config.Config;
import org.testmy.data.matchers.ConstructingMatcher;

import net.thucydides.core.annotations.Shared;

public class TestDataManager implements Config {
    @Shared
    private RecordTypeIdProvider recordTypeIdProvider;
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

    public SObject constructSObject(final ConstructingMatcher sObjectShape) {
        final SObject result = new SObject("Type must be initialized 1st, but can be changed later");
        sObjectShape.visitForUpdate(result);
        replaceWithRecordTypeIdIfPresent(result);
        return result;
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
