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
import com.sun.jdi.Location;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequestManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BreakpointHandler {

    private final VirtualMachine virtualMachine;
    private final EventRequestManager eventRequestManager;
    private final Map<String, List<InputCommand>> unresolvedBreakpoints = new HashMap<String, List<InputCommand>>();

    public BreakpointHandler(VirtualMachine virtualMachine) {

        this.virtualMachine = virtualMachine;
        this.eventRequestManager = virtualMachine.eventRequestManager();
    }

    public void addBreakpoint(InputCommand inputCommand)
            throws Exception {

        String className = inputCommand.getClassName();
        Integer lineNumber = inputCommand.getLineNumber();

        System.out.printf("add breakpoint %s:%d\n", className, lineNumber);
        
        List<ReferenceType> referenceTypes = virtualMachine.classesByName(className);
        if (referenceTypes == null || referenceTypes.isEmpty()) {
            addUnresolvedBreakpoint(inputCommand);
            return;
        }

        ReferenceType referenceType = referenceTypes.get(0);
        List<Location> locations = referenceType.locationsOfLine(lineNumber);
        if (locations == null || locations.isEmpty()) {
            throw new InvalidBreakpointException(className, lineNumber);
        }

        Location location = locations.get(0);
        BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(location);
        breakpointRequest.setEnabled(true);

        clearUnresolvedBreakpoints(className, lineNumber);
    }
    
    public void deleteBreakpoint(InputCommand inputCommand)
            throws Exception {

        if (inputCommand.getClassName() == null || inputCommand.getLineNumber() == null) {
            eventRequestManager.deleteAllBreakpoints();
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
                    return;
                }
            }
        }
    }

    public List<InputCommand> getUnresolvedBreakpoints(String className) {

        return unresolvedBreakpoints.get(className);
    }

    private void addUnresolvedBreakpoint(InputCommand inputCommand) {

        String className = inputCommand.getClassName();
        List<InputCommand> inputCommands = unresolvedBreakpoints.get(className);
        if (inputCommands == null) {
            inputCommands = new ArrayList<InputCommand>();
            unresolvedBreakpoints.put(className, inputCommands);
        }
        inputCommands.add(inputCommand);
    }

    private void clearUnresolvedBreakpoints(String className, Integer lineNumber) {
        
        List<InputCommand> inputCommands = unresolvedBreakpoints.get(className);
        if (inputCommands != null) {
            Iterator<InputCommand> iterator = inputCommands.iterator();
            while (iterator.hasNext()) {
                InputCommand inputCommand = iterator.next();
                if (inputCommand.getClassName().equals(className) && 
                        inputCommand.getLineNumber().equals(lineNumber)) {
                    iterator.remove();
                }
            }
        }
    }
}
