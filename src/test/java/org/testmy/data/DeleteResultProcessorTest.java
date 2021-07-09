package org.testmy.data;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.util.Arrays;

import com.sforce.soap.partner.DeleteResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class DeleteResultProcessorTest {
    @Mock
    Logger logger;
    @InjectMocks
    DeleteResultProcessor processor;

    @Test
    public void processResults_isSilentIfAllResultsAreGood() {
        final DeleteResult dr1 = new DeleteResult();
        dr1.setSuccess(true);
        final DeleteResult dr2 = new DeleteResult();
        dr2.setSuccess(true);
        final DeleteResult[] deleteResults = new DeleteResult[] {
                dr1, dr2
        };

        processor.processResults(deleteResults);

        verify(logger, never()).warn(any());
        verify(logger, never()).info(any());
    }

    @Test
    public void processResults_logsErrorDetailsForUnsuccessfulResults() {
        final DeleteResult failedDeletion = constructResult(false, "1");

        processor.processResults(new DeleteResult[] {
                failedDeletion
        });

        verify(logger).warn("Failed to delete some ids: {}", Arrays.asList(failedDeletion));
    }

    @Test
    public void processResults_logsApexToDeleteFailedIds() {
        final DeleteResult failedDeletion1 = constructResult(false, "1");
        final DeleteResult failedDeletion2 = constructResult(false, "2");

        final DeleteResult[] deletedResult = new DeleteResult[] {
                failedDeletion1, failedDeletion2
        };

        processor.processResults(new DeleteResult[] {
                failedDeletion1, failedDeletion2
        });

        final InOrder inOrder = inOrder(logger);
        inOrder.verify(logger).warn(anyString(), eq(Arrays.asList(deletedResult)));
        inOrder.verify(logger).info(
                "Failed ids can be deleted via Apex: {}",
                "Set<Id> ids = new Set<Id>{'1','2'}; delete ids;");
    }

    private DeleteResult constructResult(final Boolean deleted,
            final String id) {
        final DeleteResult dr = new DeleteResult();
        dr.setSuccess(deleted);
        dr.setId(id);
        return dr;
    }
}
