package com.sap.cloud.lm.sl.cf.process.steps;

import com.sap.cloud.lm.sl.cf.core.model.HookPhase;

import java.util.List;

public interface AfterStepHookPhaseProvider {

    List<HookPhase> getHookPhasesAfterStep(ProcessContext context);
}
