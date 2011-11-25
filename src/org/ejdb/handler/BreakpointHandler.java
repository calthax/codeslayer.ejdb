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
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequestManager;
import com.sun.jdi.request.StepRequest;
import java.util.Iterator;
import java.util.List;

public class BreakpointHandler {

    private final VirtualMachine virtualMachine;
    private final CommandHandler commandHandler;
    private final EventRequestManager eventRequestManager;

    public BreakpointHandler(VirtualMachine virtualMachine, CommandHandler commandHandler) {

        this.virtualMachine = virtualMachine;
        this.commandHandler = commandHandler;
        this.eventRequestManager = virtualMachine.eventRequestManager();
    }

    public void addBreakpoint(InputCommand inputCommand)
            throws Exception {

        List<ReferenceType> referenceTypes = virtualMachine.classesByName(inputCommand.getClassName());
        if (referenceTypes == null || referenceTypes.isEmpty()) {
            System.err.println("Not a valid breakpoint reference type.");
            return;
        }

        ReferenceType referenceType = referenceTypes.get(0);
        List<Location> locations = referenceType.locationsOfLine(inputCommand.getLineNumber());
        if (locations == null || locations.isEmpty()) {
            System.err.println("Not a valid breakpoint location.");
            return;
        }

        Location location = locations.get(0);
        BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(location);
        breakpointRequest.setEnabled(true);

        OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.ADD_BREAKPOINT, inputCommand.getClassName(), inputCommand.getLineNumber());
        commandHandler.sendCommand(outputCommand);
    }
    
    public void deleteBreakpoint(InputCommand inputCommand)
            throws Exception {

        if (inputCommand.getClassName() == null || inputCommand.getLineNumber() == null) {
            eventRequestManager.deleteAllBreakpoints();
            OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.DELETE_ALL_BREAKPOINTS);
            commandHandler.sendCommand(outputCommand);
        } else {
            List<BreakpointRequest> breakpointRequests = eventRequestManager.breakpointRequests();
            Iterator iter = breakpointRequests.iterator();
            while (iter.hasNext()) {
                BreakpointRequest breakpointRequest = (BreakpointRequest)iter.next();
                Location location = breakpointRequest.location();

                String sourcePath = location.sourcePath();
                sourcePath = sourcePath.replace("/", ".");
                int lineNumber = location.lineNumber();

                if (sourcePath.startsWith(inputCommand.getClassName()) && inputCommand.getLineNumber().equals(lineNumber)) {
                    eventRequestManager.deleteEventRequest(breakpointRequest);
                    OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.DELETE_BREAKPOINT, inputCommand.getClassName(), inputCommand.getLineNumber());
                    commandHandler.sendCommand(outputCommand);
                    return;
                }
            }
        }
    }
}
