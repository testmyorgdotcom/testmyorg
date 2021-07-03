package org.testmy.screenplay.question.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sforce.soap.partner.DescribeGlobalResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.error.TestRuntimeException;

import net.serenitybdd.screenplay.Actor;

@RunWith(MockitoJUnitRunner.class)
public class OrgGlobalDescriptionTest {
    @Mock
    Actor actor;
    @Mock
    PartnerConnection partnerConnection;
    @Mock
    DescribeGlobalResult mockedGlobalDescription;
    @InjectMocks
    OrgGlobalDescription metadataQuestion;

    @Before
    public void before() throws ConnectionException {
        when(actor.asksFor(any())).thenReturn(partnerConnection);
        when(partnerConnection.describeGlobal()).thenReturn(mockedGlobalDescription);
    }

    @Test
    public void requiresCallPartnerApiAbilityToGetData() {
        final DescribeGlobalResult globalDescription = metadataQuestion.answeredBy(actor);

        assertThat(globalDescription, is(mockedGlobalDescription));
    }

    @Test(expected = TestRuntimeException.class)
    public void testRuntimeExceptionOnConnectionFailures() throws ConnectionException {
        when(partnerConnection.describeGlobal()).thenThrow(new ConnectionException());

        metadataQuestion.answeredBy(actor);
    }
}
