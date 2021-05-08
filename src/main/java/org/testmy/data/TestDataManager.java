package org.testmy.data;

import static org.testmy.data.matchers.Matchers.hasId;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;

import org.hamcrest.Matcher;
import org.testmy.data.matchers.ConstructingMatcher;

public class TestDataManager implements ITestDataManager {
    private List<SObject> sObjects = new LinkedList<>();

    public List<SObject> getData() {
        return Collections.unmodifiableList(sObjects);
    }

    void addToCache(SObject object) {
        sObjects.add(object);
    }

    @Override
    public SObject ensureObject(
            final ConstructingMatcher sObjectShape,
            final Function<SObject[], SaveResult[]> storeFunction) {
        return findObject(sObjectShape).orElseGet(() -> {
            final SObject result = constructSObject(sObjectShape);
            final SaveResult[] saveResults = storeFunction.apply(new SObject[] {
                    result
            });
            result.setId(saveResults[0].getId());
            addToCache(result);
            return result;
        });
    }

    private SObject constructSObject(final ConstructingMatcher sObjectShape) {
        final SObject result = new SObject("Type must be initialized 1st, but can be changed later");
        sObjectShape.visitForUpdate(result);
        return result;
    }

    @Override
    public Optional<SObject> findObject(Matcher<SObject> sObjectShape) {
        return sObjects.stream().filter(sObjectShape::matches).findFirst();
    }

    @Override
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
