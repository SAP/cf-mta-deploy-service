package org.cloudfoundry.multiapps.controller.process.jobs;

import static org.mockito.Mockito.verify;

import java.util.Date;

import org.cloudfoundry.multiapps.controller.persistence.services.FileStorageException;
import org.cloudfoundry.multiapps.controller.persistence.services.ProcessLogsPersistenceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ProcessLogsCleanerTest {

    private static final Date EXPIRATION_TIME = new Date(5000);

    @Mock
    private ProcessLogsPersistenceService processLogsPersistenceService;
    @InjectMocks
    private ProcessLogsCleaner cleaner;

    @BeforeEach
    void initMocks() throws Exception {
        MockitoAnnotations.openMocks(this)
                          .close();
    }

    @Test
    void testExecute() throws FileStorageException {
        cleaner.execute(EXPIRATION_TIME);
        verify(processLogsPersistenceService).deleteModifiedBefore(EXPIRATION_TIME);
    }

}
