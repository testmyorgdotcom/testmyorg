package org.testmy.data;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;

import org.hamcrest.Matcher;
import org.testmy.data.matchers.ConstructingMatcher;

public interface ITestDataManager {

    SObject ensureObject(ConstructingMatcher sObjectShape,
            Function<SObject[], SaveResult[]> storeFunction);

    Optional<SObject> findObject(Matcher<SObject> sObjectShape);

    List<SObject> findObjects(Matcher<SObject> sObjectShape);
}
