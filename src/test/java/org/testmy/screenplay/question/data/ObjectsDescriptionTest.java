package org.testmy.screenplay.question.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.error.TestRuntimeException;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class ObjectsDescriptionTest {
    @Mock
    Actor actor;
    @Mock
    PartnerConnection connection;
    @Mock
    DescribeSObjectResult objectDescription;

    @Before
    public void before(){
        when(actor.asksFor(any())).thenReturn(connection);
    }

    @Test
    public void usesPartnerConnectionToGetObjectsDescriptionsByType() throws ConnectionException{
        final String objectType = "Account";
        final Set<String> objectTypes = Collections.singleton(objectType);
        final ObjectsDescription objectsDescriptionQuestion = new ObjectsDescription(objectTypes);
        when(connection.describeSObjects(new String[]{objectType}))
            .thenReturn(new DescribeSObjectResult[]{objectDescription});
        
        final List<DescribeSObjectResult> objectsDescriptionResult = objectsDescriptionQuestion.answeredBy(actor);

        assertThat(objectsDescriptionResult, hasSize(objectTypes.size()));
        assertThat(objectsDescriptionResult.get(0), is(objectDescription));
    }

    @Test(expected = TestRuntimeException.class)
    public void throwsExceptionIfConnectionExceptionHappened() throws ConnectionException{
        final ObjectsDescription objectsDescriptionQuestion = new ObjectsDescription(Collections.emptySet());
        when(connection.describeSObjects(any())).thenThrow(new ConnectionException());
        
        objectsDescriptionQuestion.answeredBy(actor);
    }
}
