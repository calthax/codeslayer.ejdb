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

import java.io.InputStreamReader;

public class InteractiveCommandHandler extends AbstractCommandHandler {

    public InteractiveCommandHandler(BreakpointManager breakpointManager) {
        
        super(breakpointManager);
    }

    public void sendCommand(OutputCommand command) {

    }

    public void run() {

        while (true) {
            try {
                StringBuilder sb = new StringBuilder();
                InputStreamReader reader = new InputStreamReader(System.in);

                if (reader.ready()) {
                    int data = reader.read();

                    while (reader.ready()) {
                        sb.append((char) data);
                        data = reader.read();
                    }

                    String cmd = sb.toString();

                    InputCommand command = commandFactory.create(cmd);

                    switch (command.getType()) {
                        case QUIT:
                            return;
                        case BREAK:
                            breakpointManager.addBreakpoint(command);
                            break;
                    }
                }
            } catch (Exception ex) {
                System.err.println("The interactive command handler is unable to carry out the command.\n");
                System.exit(1);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                System.err.println("The interactive command handler quit unexpectedly.");
            }
        }
    }
}
