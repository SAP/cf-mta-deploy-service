package com.sap.cloud.lm.sl.cf.process.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import com.sap.cloud.lm.sl.cf.core.dao.OperationDao;
import com.sap.cloud.lm.sl.cf.web.api.model.Operation;
import com.sap.cloud.lm.sl.cf.web.api.model.ProcessType;
import com.sap.cloud.lm.sl.common.SLException;

public class ProcessConflictPreventerTest {

    private String testMtaId = "test-mta-id";
    private String testSpaceId = "test-space-id";
    private String testProcessId = "test-process-id";
    private OperationDao daoMock;
    private ProcessConflictPreventer processConflictPreventerMock;

    @Before
    public void setUp() throws SLException {
        daoMock = getOngoingOperationDaoMock();
        processConflictPreventerMock = new ProcessConflictPreventerMock(daoMock);
    }

    @Test
    public void testAttemptToAcquireLock() {
        try {
            when(daoMock.findProcessWithLock(testMtaId, testSpaceId))
                .thenReturn(new Operation(testProcessId, ProcessType.DEPLOY, null, null, testSpaceId, testMtaId, "", false, null));
            processConflictPreventerMock.attemptToAcquireLock(testMtaId, testSpaceId, testProcessId);
            verify(daoMock).merge(daoMock.findRequired(testProcessId));
        } catch (SLException e) {
            assertEquals("Conflicting process \"test-process-id\" found for MTA \"test-mta-id\"", e.getMessage());
        }
    }

    @Test
    public void testAttemptToAcquireLockWithConflictProcessFound() throws SLException {
        try {
            when(daoMock.findProcessWithLock(testMtaId, testSpaceId)).thenReturn(null);
            processConflictPreventerMock.attemptToAcquireLock(testMtaId, testSpaceId, testProcessId);
        } catch (SLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testAttemptToReleaseLock() throws SLException {
        processConflictPreventerMock.attemptToReleaseLock(testProcessId);
        verify(daoMock).merge(daoMock.findRequired(testProcessId));
    }

    private OperationDao getOngoingOperationDaoMock() throws SLException {
        OperationDao daoMock = mock(OperationDao.class);
        when(daoMock.findRequired(testProcessId))
            .thenReturn(new Operation(testProcessId, ProcessType.DEPLOY, null, null, "", testMtaId, "", false, null));
        return daoMock;
    }

    private class ProcessConflictPreventerMock extends ProcessConflictPreventer {
        public ProcessConflictPreventerMock(OperationDao dao) {
            super(dao);
        }
    }
}
