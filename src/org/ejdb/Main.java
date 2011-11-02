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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public class Main {

    public static void main(String args[]) {

        System.out.println("entered main\n");
        System.out.flush();

        VirtualMachine virtualMachine = null;
        try {
            VMConnector connector = new VMConnector();
            virtualMachine = connector.connect(8000);
        } catch (IOException e) {
            System.err.println("Not able to connect to the VM.");
            System.exit(1);
        }

        System.out.println("connected to machine\n");
        System.out.flush();

        EventRequestManager eventRequestManager = virtualMachine.eventRequestManager();
        EventQueue eventQueue = virtualMachine.eventQueue();

        BreakpointManager breakpointManager = new BreakpointManager(virtualMachine, eventRequestManager);
        
        StreamHandler handler = new StreamHandler(breakpointManager, eventQueue);
        Thread thread = new Thread(handler);

        System.out.println("start the thread\n");
        System.out.flush();

        thread.start();
        do {
            // keep running while still alive
        } while (thread.isAlive());
    }

    public static class StreamHandler implements Runnable {

        private final BreakpointManager breakpointManager;
        private final EventQueue eventQueue;

        public StreamHandler(BreakpointManager breakpointManager, EventQueue eventQueue) {

            this.breakpointManager = breakpointManager;
            this.eventQueue = eventQueue;
        }

        public void run() {

        System.out.println("start running\n");
        System.out.flush();


            while (true) {
                try {
                    System.out.println("try to read\n");
                    System.out.flush();

                    StringBuilder sb = new StringBuilder();
                    InputStreamReader reader = new InputStreamReader(System.in);

                    if (reader.ready()) {
                        int data = reader.read();

                        while (reader.ready()) {
                            sb.append((char) data);
                            data = reader.read();
                            System.out.println("data " + sb.toString());
                        }

                        System.out.println("try to find command\n");
                        System.out.flush();

                        String command = sb.toString();

                        if (command != null && !command.isEmpty()) {
                            if (command.equals("quit")) {
                                System.exit(1);
                            }

                            if (command.startsWith("break ")) { // break org.jmesaweb.controller.BasicPresidentController:68
                                String substring = command.substring("break ".length(), command.length());
                                String[] split = substring.split(":");
                                String className = split[0];
                                String lineStr = split[1];
                                breakpointManager.addBreakpoint(className, Integer.parseInt(lineStr));
                            }

                            System.out.println("wait for the breakpoint to be hit");
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
                        }         
                    }
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
}
