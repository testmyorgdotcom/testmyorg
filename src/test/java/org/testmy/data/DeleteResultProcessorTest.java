package org.testmy.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.sforce.soap.partner.DeleteResult;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.testmy.error.TestRuntimeException;

@RunWith(MockitoJUnitRunner.class)
public class DeleteResultProcessorTest {
    @Mock
    Logger logger;
    @InjectMocks
    DeleteResultProcessor processor;

    @Test
    public void processResults_isSilentIfEverythingWasDeleted() {
        final DeleteResult[] deletedResults = new DeleteResult[] {
                constructResult(true, "1"), constructResult(true, "2")
        };

        processor.processResults(deletedResults);

        verify(logger, never()).info(any());
    }

    @Test
    public void processResults_throwsRuntimeExceptionIfCouldNotDeleteSomeTestData() {
        final DeleteResult deleted = constructResult(true, "success id");
        final DeleteResult failedToDelete = constructResult(false, "failed id");

        final DeleteResult[] deletedResult = new DeleteResult[] {
                deleted, failedToDelete
        };

        final TestRuntimeException tre = assertThrows(TestRuntimeException.class, () -> {
            processor.processResults(deletedResult);
        });

        assertThat(tre.getMessage(), containsString(failedToDelete.toString()));
        assertThat(tre.getMessage(), not(containsString(deleted.toString())));
    }

    @Test(expected = TestRuntimeException.class)
    public void processResults_logsApexToDeleteFailedIds() {
        final DeleteResult failedDeletion1 = constructResult(false, "1");
        final DeleteResult failedDeletion2 = constructResult(false, "2");
        final DeleteResult deleted = constructResult(true, "3");

        final DeleteResult[] deletedResult = new DeleteResult[] {
                failedDeletion1, failedDeletion2, deleted
        };

        processor.processResults(deletedResult);

        verify(logger).info(
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
