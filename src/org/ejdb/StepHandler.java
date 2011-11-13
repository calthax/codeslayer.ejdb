/*
 * Copyright (C) 2010 - Jeff Johnston
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package org.ejdb;

import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;
import java.util.Iterator;
import java.util.List;

public class StepHandler {

    private final VirtualMachine virtualMachine;
    private final EventRequestManager eventRequestManager;

    public StepHandler(VirtualMachine virtualMachine) {

        this.virtualMachine = virtualMachine;
        this.eventRequestManager = virtualMachine.eventRequestManager();
    }

    public void next(ThreadReference threadReference) {

        clearPreviousStep(threadReference);
        createNextStep(threadReference, StepRequest.STEP_LINE, StepRequest.STEP_OVER);
    }

    public void step(ThreadReference threadReference) {

        clearPreviousStep(threadReference);
        createNextStep(threadReference, StepRequest.STEP_LINE, StepRequest.STEP_INTO);
    }

    public void finish(ThreadReference threadReference) {

        clearPreviousStep(threadReference);
        createNextStep(threadReference, StepRequest.STEP_LINE, StepRequest.STEP_OUT);
    }

    public void cont() {

        virtualMachine.resume();
    }

    private void clearPreviousStep(ThreadReference threadReference) {

        List requests = eventRequestManager.stepRequests();
        Iterator iter = requests.iterator();
        while (iter.hasNext()) {
            StepRequest stepRequest = (StepRequest)iter.next();
            if (stepRequest.thread().equals(threadReference)) {
                eventRequestManager.deleteEventRequest(stepRequest);
                break;
            }
        }
    }

    private void createNextStep(ThreadReference threadReference, int size, int depth) {

        StepRequest stepRequest = eventRequestManager.createStepRequest(threadReference, size, depth);
        stepRequest.addCountFilter(1);
        stepRequest.enable();
        virtualMachine.resume();
    }
}
