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

import com.sun.jdi.VMDisconnectedException;
import org.ejdb.connector.SocketConnector;
import java.util.List;
import org.ejdb.handler.EventHandler;
import org.ejdb.handler.SourceHandler;
import org.ejdb.handler.ConsoleCommandHandler;
import org.ejdb.handler.CommandHandler;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.request.ClassPrepareRequest;
import org.ejdb.connector.LaunchConnector;
import org.ejdb.connector.VirtualMachineConnector;

public class Main {

    public static void main(String args[]) {

        String modifiers = MainUtils.getModifiers(args);

        VirtualMachine virtualMachine = null;
        try {
            virtualMachine = createVirtualMachine(modifiers);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Not able to connect to the VM. Did you define a -port or a -launch?");
            System.exit(1);
        }

        CommandHandler commandHandler = createCommandHandler(virtualMachine);
        Thread commandHandlerThread = new Thread(commandHandler);

        List<String> sourcePaths = MainUtils.getSourcepath(modifiers);
        if (sourcePaths == null) {
            System.err.println("You need to define the -sourcepath.");
            System.exit(1);
        }

        EventHandler eventHandler = new EventHandler(virtualMachine, commandHandler, new SourceHandler(sourcePaths));
        Thread eventHandlerThread = new Thread(eventHandler);

        commandHandlerThread.start();
        eventHandlerThread.start();

        ClassPrepareRequest classPrepareRequest = virtualMachine.eventRequestManager().createClassPrepareRequest();
        classPrepareRequest.setEnabled(true);

        do {
            // keep running while threads are still alive
        } while (commandHandlerThread.isAlive() && eventHandlerThread.isAlive());

        virtualMachine.resume();
        try {
            virtualMachine.dispose();
        } catch (VMDisconnectedException e) {
            // disconnected
        }

        System.exit(1);
    }

    private static VirtualMachine createVirtualMachine(String modifiers) 
            throws Exception {

        VirtualMachineConnector virtualMachineConnector = null;

        Integer port = MainUtils.getPort(modifiers);

        if (port != null) {
            virtualMachineConnector = new SocketConnector(port);
        } else {
            String exec = MainUtils.getLaunch(modifiers);
            if (exec != null) {
                String classpath = MainUtils.getClasspath(modifiers);
                virtualMachineConnector = new LaunchConnector(exec, classpath);
            }
        }
        
        return virtualMachineConnector.connect();
    }

    private static CommandHandler createCommandHandler(VirtualMachine virtualMachine) {

        return new ConsoleCommandHandler(virtualMachine);
    }
}
