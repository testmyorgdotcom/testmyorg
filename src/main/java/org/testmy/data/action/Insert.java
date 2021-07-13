package org.testmy.data.action;

import java.util.List;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.testmy.error.TestRuntimeException;

public class Insert {
    private final PartnerConnection connection;
    private final Query queryAction;

    private Insert(final PartnerConnection connection,
            final Query queryAction) {
        this.connection = connection;
        this.queryAction = queryAction;
    }

    public Insert(PartnerConnection connection) {
        this(connection, new Query(connection));
    }

    public String insert(final SObject sObject) {
        try {
            final SaveResult[] results = connection.create(new SObject[] {
                    sObject
            });
            final SaveResult result = results[0];
            if (result.isSuccess()) {
                return results[0].getId();
            }
            throw new TestRuntimeException("Failed to create object: " + result);
        } catch (ConnectionException e) {
            throw new TestRuntimeException("Failed to create object: " + sObject, e);
        }
    }

    public List<SObject> query(final String soql) {
        return queryAction.query(soql);
    }
}
