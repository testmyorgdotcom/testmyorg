package org.testmy.data;

import java.util.Arrays;
import java.util.List;

import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testmy.error.TestRuntimeException;

public class SalesforceDataAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(SalesforceDataAction.class);

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

    public List<SObject> query(final String soql) {
        try {
            LOGGER.debug(soql);
            final QueryResult queryResult = connection.query(soql);
            return Arrays.asList(queryResult.getRecords());
        } catch (final ConnectionException e) {
            throw new TestRuntimeException(e);
        }
    }
}
