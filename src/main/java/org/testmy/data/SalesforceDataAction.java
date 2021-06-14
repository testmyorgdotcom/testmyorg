package org.testmy.data;

import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.testmy.error.TestRuntimeException;

public class SalesforceDataAction {
    private final PartnerConnection connection;

    public SalesforceDataAction(PartnerConnection connection) {
        this.connection = connection;
    }

    public String insert(SObject sObject) {
        try {
            final SaveResult[] results = connection.create(new SObject[] {
                    sObject
            });
            final SaveResult result = results[0];
            if (result.isSuccess()) {
                return results[0].getId();
            }
            throw new TestRuntimeException(extractErrors(result));
        } catch (ConnectionException e) {
            throw new TestRuntimeException(e);
        }
    }

    private String extractErrors(final SaveResult result) {
        final StringBuilder sb = new StringBuilder("Errors:[ ");
        for (final Error error : result.getErrors()) {
            sb.append(error.getMessage());
            sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
