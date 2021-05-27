package org.testmy.screenplay.factory;

import org.junit.Test;
import org.testmy.config.Config;
import org.testmy.screenplay.act.task.CreateRecord;

public class CreateRecordTest {
    @Test
    public void resolvesComposerPopulationTaskBasedOnAction() {
        final CreateRecord globalActionTask = CreateRecord.viaGlobalAction(Config.GLOBAL_ACTION_NEW_CONTACT);
        // assertThat(globalActionTask.getClass(), is(CreateContact.class));
        // globalActionTask.
    }
}
