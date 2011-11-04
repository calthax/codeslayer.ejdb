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

import java.io.Console;

public class ConsoleCommandHandler extends AbstractCommandHandler {

    private final Console console;

    public ConsoleCommandHandler(BreakpointManager breakpointManager) {
        
        super(breakpointManager);
        this.console = System.console();
        if (console == null) {
            System.err.println("no console.");
            System.exit(1);
        }
    }

    public void sendCommand(Command command) {

    }

    public void run() {

        while (true) {
            try {
                String command = console.readLine("(cmd)");

                if (command != null && !command.isEmpty()) {
                    if (command.equals("quit")) {
                        return;
                    }

                    if (command.startsWith("break ")) { // break org.jmesaweb.controller.BasicPresidentController:68
                        String substring = command.substring("break ".length(), command.length());
                        String[] split = substring.split(":");
                        String className = split[0];
                        String lineStr = split[1];
                        breakpointManager.addBreakpoint(className, Integer.parseInt(lineStr));
                    }
                }
            } catch (Exception ex) {
                System.err.println("The console command handler is unable to carry out the command.\n");
                System.exit(1);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.err.println("The console command handler quit unexpectedly.");
            }
        }
    }
}
