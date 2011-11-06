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

import com.sun.jdi.Location;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;

public class EventHandler implements Runnable {

    private final VirtualMachine virtualMachine;
    private final CommandHandler commandHandler;
    private final SourceHandler sourceHandler;
    private final EventRequestManager eventRequestManager;

    public EventHandler(VirtualMachine virtualMachine, CommandHandler commandHandler, SourceHandler sourceHandler) {

        this.virtualMachine = virtualMachine;
        this.commandHandler = commandHandler;
        this.sourceHandler = sourceHandler;
        this.eventRequestManager = virtualMachine.eventRequestManager();
    }

    public void run() {

        EventQueue eventQueue = virtualMachine.eventQueue();

        while (true) {
            try {
                EventSet eventSet = eventQueue.remove();
                for (Event event : eventSet) {
                    if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
                        return;
                    } else if (event instanceof BreakpointEvent) {
                        BreakpointEvent breakpointEvent = (BreakpointEvent)event;
                        Location location = breakpointEvent.location();

                        OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.BREAKPOINT, location.sourcePath(), location.lineNumber());
                        commandHandler.sendCommand(outputCommand);
                        
                        InputCommand inputCommand = commandHandler.retrieveCommand();
                        switch (inputCommand.getType()) {
                            case NEXT:
                                ThreadReference thread = breakpointEvent.thread();
                                StepRequest stepRequest = eventRequestManager.createStepRequest(thread, StepRequest.STEP_LINE, StepRequest.STEP_OVER);
                                stepRequest.addCountFilter(1);
                                stepRequest.enable();
                                virtualMachine.resume();
                                break;
                        }
                    } else if (event instanceof StepEvent) {
                        StepEvent stepEvent = (StepEvent)event;
                        Location location = stepEvent.location();

                        OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.STEP_OVER, location.sourcePath(), location.lineNumber());
                        String text = sourceHandler.getLine(location.sourcePath(),location.lineNumber());
                        outputCommand.setText(text);

                        commandHandler.sendCommand(outputCommand);

                        stepEvent.request().disable();

                        InputCommand inputCommand = commandHandler.retrieveCommand();
                        switch (inputCommand.getType()) {
                            case NEXT:
                                ThreadReference thread = stepEvent.thread();
                                StepRequest stepRequest = eventRequestManager.createStepRequest(thread, StepRequest.STEP_LINE, StepRequest.STEP_OVER);
//                                stepRequest.addCountFilter(1);
                                stepRequest.enable();
                                virtualMachine.resume();
                                break;
                        }
                    }
                }
                eventSet.resume();
            } catch (Exception ex) {
                System.out.printf("Not able to carry out the command.\n");
                ex.printStackTrace();
                break;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("The event handler quit unexpectedly.");
                ex.printStackTrace();
                break;
            }
        }
    }
}
