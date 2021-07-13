package org.testmy.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.sforce.soap.partner.DeleteResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testmy.error.TestRuntimeException;

public class DeleteResultProcessor {
    private Logger logger = LoggerFactory.getLogger(getClass());

    public void processResults(DeleteResult[] deleteResults) {
        final List<DeleteResult> nonDeleted = new ArrayList<>();
        for (final DeleteResult dr : deleteResults) {
            if (!dr.isSuccess()) {
                nonDeleted.add(dr);
            }
        }
        if (!nonDeleted.isEmpty()) {
            final StringBuilder sb = new StringBuilder("Set<Id> ids = new Set<Id>{");
            sb
                    .append(String.join(",",
                            nonDeleted.stream().map(dr -> "'" + dr.getId() + "'").collect(Collectors.toList())))
                    .append("}; delete ids;");
            logger.info("Failed ids can be deleted via Apex: {}", sb.toString());
            throw new TestRuntimeException(String.format("Could not delete some test data: %s", nonDeleted));
        }
    }

}
