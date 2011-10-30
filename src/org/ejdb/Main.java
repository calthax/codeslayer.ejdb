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
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;
import com.sun.jdi.request.EventRequestManager;
import java.io.Console;
import java.io.IOException;

public class Main {

    public static void main(String args[]) {

        Console console = System.console();
        if (console == null) {
            System.err.println("no console.");
            System.exit(1);
        }

        VirtualMachine virtualMachine = null;
        try {
            VMConnector connector = new VMConnector();
            virtualMachine = connector.connect(8000);
        } catch (IOException e) {
            System.err.println("Not able to connect to the VM.");
            System.exit(1);
        }

        EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();
        EventQueue eventQueue = virtualMachine.eventQueue();

        BreakpointManager breakpointManager = new BreakpointManager(virtualMachine, eventRequestManager);

        while (true) {
            
            String command = console.readLine("(cmd)");

            if (command == null || command.length() == 0) {
                continue;
            }

            if (command.equals("quit")) {
                System.exit(1);
            }

            try {
                if (command.startsWith("break ")) { // break org.jmesaweb.controller.BasicPresidentController:68
                    String substring = command.substring("break ".length(), command.length());
                    String[] split = substring.split(":");
                    String className = split[0];
                    String lineStr = split[1];
                    breakpointManager.addBreakpoint(className, Integer.parseInt(lineStr));
                }
            
                EventSet eventSet = eventQueue.remove();
                for (Event event : eventSet) {
                    if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
                        System.exit(1);
                    } else if (event instanceof BreakpointEvent) {
                        System.out.printf ("hitting the breakpoint\n");
                        BreakpointEvent breakpointEvent = (BreakpointEvent)event;

                        Location bpr1 = breakpointEvent.location();
                        int ln1 = bpr1.lineNumber();
                        System.out.printf ("bpr1 %d \n", ln1);
                    }
                }
                eventSet.resume();
            } catch (Exception ex) {
                System.err.printf("Not able to carry out the command %s.\n", command);
                System.exit(1);
            }
        }
    }
}
