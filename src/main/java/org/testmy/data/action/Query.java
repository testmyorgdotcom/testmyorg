package org.testmy.data.action;

import java.util.Arrays;
import java.util.List;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testmy.error.TestRuntimeException;

public class Query {
    private Logger logger;
    private PartnerConnection connection;

    private Query(final PartnerConnection connection,
            final Logger logger) {
        this.connection = connection;
        this.logger = logger;
    }

    public Query(final PartnerConnection connection) {
        this(connection, LoggerFactory.getLogger(Query.class));
    }

    public List<SObject> query(final String soql) {
        try {
            logger.info("SOQL: " + soql);
            final QueryResult queryResult = connection.query(soql);
            return Arrays.asList(queryResult.getRecords());
        } catch (final ConnectionException e) {
            throw new TestRuntimeException("Failed to execute soql: " + soql, e);
        }
    }

}
