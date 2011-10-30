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
import com.sun.jdi.ReferenceType;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequestManager;
import java.util.List;

public class BreakpointManager {

    private final VirtualMachine virtualMachine;
    private final EventRequestManager eventRequestManager;

    public BreakpointManager(VirtualMachine virtualMachine, EventRequestManager eventRequestManager) {

        this.virtualMachine = virtualMachine;
        this.eventRequestManager = eventRequestManager;
    }

    public void addBreakpoint(String className, int lineNumber)
            throws Exception {

        List<ReferenceType> referenceTypes = virtualMachine.classesByName(className);
        if (referenceTypes == null || referenceTypes.isEmpty()) {
            System.out.println("not a valid breakpoint reference type");
            return;
        }

        ReferenceType referenceType = referenceTypes.get(0);
        List<Location> locations = referenceType.locationsOfLine(lineNumber);
        if (locations == null || locations.isEmpty()) {
            System.out.println("not a valid breakpoint location");
            return;
        }

        Location location = locations.get(0);
        BreakpointRequest breakpointRequest = eventRequestManager.createBreakpointRequest(location);
        breakpointRequest.setEnabled(true);
    }
}
