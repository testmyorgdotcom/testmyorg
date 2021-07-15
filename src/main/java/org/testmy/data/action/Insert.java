package org.testmy.data.action;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.sforce.soap.partner.AllOrNoneHeader_element;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testmy.error.TestRuntimeException;

public class Insert {
    private Logger logger = LoggerFactory.getLogger(getClass());
    private final PartnerConnection connection;
    private final Query queryAction;

    private Insert(final PartnerConnection connection,
            final Query queryAction,
            final Logger logger) {
        this.connection = connection;
        this.queryAction = queryAction;
        this.logger = logger;
    }

    private Insert(final PartnerConnection connection,
            final Query queryAction) {
        this(connection, queryAction, LoggerFactory.getLogger(Insert.class));
    }

    public Insert(PartnerConnection connection) {
        this(connection, new Query(connection));
    }

    public String insert(final SObject sObject) {
        return insertObjects(Collections.singletonList(sObject)).get(0);
    }

    public List<String> insertObjects(final List<SObject> sObjects) {
        final AllOrNoneHeader_element initialState = connection.getAllOrNoneHeader();
        try {
            if (null == initialState || !initialState.isAllOrNone()) {
                connection.setAllOrNoneHeader(true);
            }
            final SaveResult[] results = connection.create(sObjects.toArray(new SObject[0]));
            final List<SaveResult> resultsAsList = Arrays.asList(results);
            final List<SaveResult> failedResultsIfAny = resultsAsList.stream()
                    .filter(s -> !s.getSuccess())
                    .collect(Collectors.toList());
            if (!failedResultsIfAny.isEmpty()) {
                logger.error("Could not save some objects, so rolling back whole transaction: {}", failedResultsIfAny);
                throw new TestRuntimeException("Could not create some records: " + failedResultsIfAny);
            }
            return Arrays.asList(results).stream().map(sr -> sr.getId()).collect(Collectors.toList());
        } catch (final ConnectionException e) {
            throw new TestRuntimeException("Failed to create object:", e);
        } finally {
            if (null != initialState && !initialState.isAllOrNone()) {
                connection.setAllOrNoneHeader(initialState.isAllOrNone());
            }
            else if (null == initialState) {
                connection.clearAllOrNoneHeader();
            }
        }
    }

    public List<SObject> query(final String soql) {
        return queryAction.query(soql);
    }
}
