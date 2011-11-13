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

import com.sun.jdi.VirtualMachine;
import java.io.IOException;

public class Main {

    public static void main(String args[]) {

        VirtualMachine virtualMachine = null;
        try {
            VMConnector connector = new VMConnector();
            virtualMachine = connector.connect(8000);
        } catch (IOException e) {
            System.err.println("Not able to connect to the VM.");
            System.exit(1);
        }

        CommandHandler commandHandler = createCommandHandler(virtualMachine, args);
        Thread commandHandlerThread = new Thread(commandHandler);

        SourceHandler sourceHandler = new SourceHandler(
                new String[]{"/home/jeff/workspace/jmesaWeb/src/", "/home/jeff/workspace/jmesa/src/"});

        EventHandler eventHandler = new EventHandler(virtualMachine, commandHandler, sourceHandler);
        Thread eventHandlerThread = new Thread(eventHandler);

        commandHandlerThread.start();
        eventHandlerThread.start();

        do {
            // keep running while threads are still alive
        } while (commandHandlerThread.isAlive() && eventHandlerThread.isAlive());

        virtualMachine.dispose();
        System.exit(1);
    }

    private static CommandHandler createCommandHandler(VirtualMachine virtualMachine, String args[]) {

        if (args != null && args.length > 0) {
            String type = args[0];
            if (type.equals("--interpreter=mi")) {
                return new InteractiveCommandHandler(virtualMachine);
            }
        }

        //-sourcepath=<dir1:dir2:...>

        return new ConsoleCommandHandler(virtualMachine);
    }
}
