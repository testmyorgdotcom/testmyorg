package org.testmy.data;

import java.util.Set;

import com.sforce.soap.partner.DeleteResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

import org.testmy.error.TestRuntimeException;

public class SalesforceCleanDataAction {
    private DeleteResultProcessor deleteResultProcessor;
    private PartnerConnection connection;

    private SalesforceCleanDataAction(final PartnerConnection partnerConnection,
            final DeleteResultProcessor deleteResultProcessor) {
        this.connection = partnerConnection;
        this.deleteResultProcessor = deleteResultProcessor;
    }

    public SalesforceCleanDataAction(final PartnerConnection partnerConnection) {
        this(partnerConnection, new DeleteResultProcessor());
    }

    public void cleanData(final Set<String> sfIds) {
        try {
            final String[] ids = sfIds.toArray(new String[0]);
            final DeleteResult[] deleteResults = connection.delete(ids);
            deleteResultProcessor.processResults(deleteResults);
        } catch (final ConnectionException e) {
            throw new TestRuntimeException("exception while deleting: " + sfIds, e);
        }
    }
}
