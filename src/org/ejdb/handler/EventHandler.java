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
package org.ejdb.handler;

import org.ejdb.command.InputCommand;
import org.ejdb.command.OutputCommand;
import org.ejdb.print.PrintHandler;
import org.ejdb.print.PrintFormatter;
import com.sun.jdi.Location;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.ClassPrepareEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.StepEvent;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.event.VMStartEvent;
import com.sun.jdi.request.StepRequest;

public class EventHandler implements Runnable {

    private final VirtualMachine virtualMachine;
    private final CommandHandler commandHandler;
    private final SourceHandler sourceHandler;
    private final BreakpointHandler breakpointHandler;
    private final StepHandler stepHandler;
    private final PrintHandler printHandler;

    public EventHandler(VirtualMachine virtualMachine, CommandHandler commandHandler, SourceHandler sourceHandler) {

        this.virtualMachine = virtualMachine;
        this.commandHandler = commandHandler;
        this.sourceHandler = sourceHandler;
        this.breakpointHandler = new BreakpointHandler(virtualMachine, commandHandler);
        this.stepHandler = new StepHandler(virtualMachine);
        this.printHandler = new PrintHandler(commandHandler, new PrintFormatter());
    }

    public void run() {

        EventQueue eventQueue = virtualMachine.eventQueue();

        while (true) {
            try {
                EventSet eventSet = eventQueue.remove();
                for (Event event : eventSet) {
                    if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
                        return;
                    } else if (event instanceof VMStartEvent) {
                        VMStartEvent startEvent = (VMStartEvent)event;
                        startRequest(startEvent.thread());
                    } else if (event instanceof ClassPrepareEvent) {
                        ClassPrepareEvent classPrepareEvent = (ClassPrepareEvent)event;
//                        System.out.println(classPrepareEvent.referenceType().name());

                        if ("org.jmesa.core.CoreContextTest".equals(classPrepareEvent.referenceType().name())) {
                            virtualMachine.suspend();
                            InputCommand inputCommand = new InputCommand(InputCommand.Type.BREAK, "org.jmesa.core.CoreContextTest", 53);
                            breakpointHandler.addBreakpoint(inputCommand);
                            virtualMachine.resume();
                        }

                    } else if (event instanceof BreakpointEvent) {
                        BreakpointEvent breakpointEvent = (BreakpointEvent)event;
                        virtualMachine.suspend();
                        Location location = breakpointEvent.location();
                        
                        sendCommand(OutputCommand.Type.HIT_BREAKPOINT, location);
                        stepRequest(breakpointEvent.thread());
                    } else if (event instanceof StepEvent) {
                        StepEvent stepEvent = (StepEvent)event;
                        virtualMachine.suspend();
                        Location location = stepEvent.location();

                        Integer stepLine = (Integer)stepEvent.request().getProperty("STEP_LINE");
                        switch (stepLine) {
                            case StepRequest.STEP_OVER :
                                sendCommand(OutputCommand.Type.STEP_OVER_LINE, location);
                                break;
                            case StepRequest.STEP_INTO :
                                sendCommand(OutputCommand.Type.STEP_INTO_LINE, location);
                                break;
                            case StepRequest.STEP_OUT :
                                sendCommand(OutputCommand.Type.STEP_OUT_LINE, location);
                                break;
                        }

                        stepRequest(stepEvent.thread());
                    }
                }
                eventSet.resume();
            } catch (Exception e) {
                System.err.println("Not able to carry out the event.");
                e.printStackTrace();
                return;
            }
        }
    }

    private void sendCommand(OutputCommand.Type type, Location location)
            throws Exception {

        OutputCommand outputCommand = new OutputCommand(type, location.sourcePath(), location.lineNumber());
        String text = sourceHandler.getLine(location.sourcePath(), location.lineNumber());
        outputCommand.setText(text);
        commandHandler.sendCommand(outputCommand);
    }

    private void startRequest(ThreadReference threadReference)
            throws Exception {

        InputCommand inputCommand = commandHandler.retrieveCommand();
        switch (inputCommand.getType()) {
            case BREAK:
                breakpointHandler.addBreakpoint(inputCommand);
                break;
            case CONTINUE:
                virtualMachine.resume();
                break;
        }
    }

    private void stepRequest(ThreadReference threadReference)
            throws Exception {

        InputCommand inputCommand = commandHandler.retrieveCommand();
        switch (inputCommand.getType()) {
            case PRINT:
                printHandler.value(threadReference, inputCommand);
                stepRequest(threadReference);
                break;
            case NEXT:
                stepHandler.next(threadReference);
                break;
            case STEP:
                stepHandler.step(threadReference);
                break;
            case FINISH:
                stepHandler.finish(threadReference);
                break;
            case CONTINUE:
                stepHandler.cont(threadReference);
                break;
        }
    }
}
