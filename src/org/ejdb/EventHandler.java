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
import com.sun.jdi.event.BreakpointEvent;
import com.sun.jdi.event.Event;
import com.sun.jdi.event.EventQueue;
import com.sun.jdi.event.EventSet;
import com.sun.jdi.event.VMDeathEvent;
import com.sun.jdi.event.VMDisconnectEvent;

public class EventHandler implements Runnable {

    private final CommandHandler commandHandler;
    private final EventQueue eventQueue;

    public EventHandler(CommandHandler commandHandler, EventQueue eventQueue) {

        this.commandHandler = commandHandler;
        this.eventQueue = eventQueue;
    }

    public void run() {

        System.out.println("start running\n");
        System.out.flush();

        while (true) {
            try {
                System.out.println("try to read\n");
                System.out.flush();

                EventSet eventSet = eventQueue.remove();
                for (Event event : eventSet) {
                    if (event instanceof VMDeathEvent || event instanceof VMDisconnectEvent) {
                        System.exit(1);
                    } else if (event instanceof BreakpointEvent) {
                        System.out.println ("hitting the breakpoint\n");
                        System.out.flush();
                        BreakpointEvent breakpointEvent = (BreakpointEvent)event;

                        Location bpr1 = breakpointEvent.location();
                        int ln1 = bpr1.lineNumber();
                        System.out.println ("bpr1 " + ln1 + " \n");
                        System.out.flush();
                    }
                }
                eventSet.resume();
            } catch (Exception ex) {
                System.out.printf("Not able to carry out the command.\n");
                System.exit(1);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.out.println("The thread was interrupted.");
            }
        }
    }
}
