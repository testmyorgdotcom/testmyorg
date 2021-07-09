package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.testmy.error.TestRuntimeException;

@RunWith(MockitoJUnitRunner.class)
public class SalesforceCleanDataActionTest {
    @Mock
    DeleteResultProcessor deleteResultProcessor;
    @Mock
    PartnerConnection connection;
    @InjectMocks
    SalesforceCleanDataAction cleanAction;

    @Test
    public void cleanData_deletesByIdsWithThePartnerConnectionProvided() throws ConnectionException {
        final Set<String> sfIdsToDelete = new HashSet<>(Arrays.asList("123", "ABC"));

        cleanAction.cleanData(sfIdsToDelete);

        verify(connection).delete(new String[] {
                "123", "ABC"
        });
    }

    @Test
    public void cleanData_throwsRuntimeIfConnectionException() {
        final String sfIdToDelete = "UNIQUESALEFORCEID";
        mockExceptionOnDeleteAny();

        TestRuntimeException tre = assertThrows(TestRuntimeException.class, () -> {
            cleanAction.cleanData(Collections.singleton(sfIdToDelete));
        });

        assertThat(tre.getMessage(), containsString(sfIdToDelete));
    }

    @Test
    public void cleanData_delegatesResultsProcessing() {
        final String sfIdToDelete = "UNIQUESALEFORCEID";

        cleanAction.cleanData(Collections.singleton(sfIdToDelete));

        verify(deleteResultProcessor).processResults(any());
    }

    private void mockExceptionOnDeleteAny() {
        try {
            when(connection.delete(any())).thenThrow(new ConnectionException());
        } catch (final ConnectionException e) {
            throw new IllegalStateException("should not reach here");
        }
    }

}
