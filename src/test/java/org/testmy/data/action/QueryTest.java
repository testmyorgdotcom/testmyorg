package org.testmy.data.action;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.testmy.error.TestRuntimeException;

@RunWith(MockitoJUnitRunner.class)
public class QueryTest {
    @Mock
    PartnerConnection connection;
    @Mock
    Logger logger;
    @InjectMocks
    Query dataAction;

    @Before
    public void before() {
        mockConnectionQueryResult(new SObject[0]);
    }

    @Test
    public void query_usesPartnerConnectionToQueryData() throws ConnectionException {
        final String soql = "SELECT Id FROM Contact LIMIT 1";

        dataAction.query(soql);

        verify(connection).query(soql);
    }

    @Test
    public void query_logsQuery() throws ConnectionException {
        final String soql = "SELECT Id FROM Contact LIMIT 1";

        dataAction.query(soql);

        verify(logger).info("SOQL: " + soql);
    }

    @Test
    public void query_throwsRuntimeIfConnectionException() {
        final String soql = "SELECT Id FROM Contact LIMIT 1";
        mockConnectionFailedQuery();

        final TestRuntimeException tre = assertThrows(TestRuntimeException.class, () -> {
            dataAction.query(soql);
        });

        assertThat(tre.getMessage(), equalTo("Failed to execute soql: " + soql));
        assertThat(tre.getCause(), instanceOf(ConnectionException.class));
    }

    @Test
    public void query_returnsSObjectsProvidedByPartnerConnection() {
        final SObject[] expectedObjects = new SObject[] {
                new SObject(), new SObject()
        };
        mockConnectionQueryResult(expectedObjects);

        final List<SObject> actualObjects = dataAction.query("any soql");

        assertThat(actualObjects, hasItems(expectedObjects));
        assertThat(actualObjects, hasSize(expectedObjects.length));
    }

    private void mockConnectionFailedQuery() {
        try {
            when(connection.query(anyString())).thenThrow(new ConnectionException());
        } catch (final ConnectionException e) {
            throw new TestRuntimeException("Unexpected exception");
        }
    }

    private void mockConnectionQueryResult(final SObject[] expectedObjects) {
        final QueryResult qr = mock(QueryResult.class);
        when(qr.getRecords()).thenReturn(expectedObjects);
        try {
            when(connection.query(anyString())).thenReturn(qr);
        } catch (final ConnectionException e) {
            throw new TestRuntimeException("Unexpected exception");
        }
    }
}
