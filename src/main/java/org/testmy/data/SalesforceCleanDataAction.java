package org.testmy.data;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.sforce.soap.partner.DeleteResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

import org.testmy.error.TestRuntimeException;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class SalesforceCleanDataAction {
    private PartnerConnection connection;

    public void cleanData(final Set<String> sfIds) {
        try {
            System.out.println("Going to delete: " + sfIds);
            final String[] ids = sfIds.toArray(new String[0]);
            final DeleteResult[] deleteResults = connection.delete(ids);
            final List<DeleteResult> nonDeletedObjects = Arrays.asList(deleteResults).stream()
                    .filter(dr -> !dr.getSuccess())
                    .collect(Collectors.toList());
            if (!nonDeletedObjects.isEmpty()) {
                System.err.println("Could not delete: " + nonDeletedObjects); // TODO: move to loggers
            }
        } catch (final ConnectionException e) {
            throw new TestRuntimeException(e);
        }
    }
}
