package org.testmy.screenplay.act;

import static net.serenitybdd.screenplay.GivenWhenThen.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.testmy.data.matchers.Matchers.hasName;
import static org.testmy.data.matchers.Matchers.ofShape;
import static org.testmy.data.matchers.ObjectMatchers.account;

import java.util.List;
import java.util.function.Function;

import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.data.TestDataManager;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class CreateDataTest {
    @Mock
    Function<SObject[], SaveResult[]> storeFunction;
    TestDataManager testDataManager = new TestDataManager();

    Actor mike = Actor.named("Mike");

    @Before
    public void before() {
        final SaveResult saveResult = new SaveResult();
        saveResult.setId("003XYZ...");
        when(storeFunction.apply(any())).thenReturn(new SaveResult[] {
                saveResult
        });
    }

    @After
    public void after() {

    }

    @Test
    public void testPerform() {

        when(mike).attemptsTo(
                CreateData.record(ofShape(account(), hasName("accountName")))
                        .withStoreFunction(storeFunction)
                        .withTestDataManager(testDataManager));

        final List<SObject> testData = testDataManager.getData();
        assertThat(testData, hasSize(1));

        verify(storeFunction).apply(any());
    }
}
