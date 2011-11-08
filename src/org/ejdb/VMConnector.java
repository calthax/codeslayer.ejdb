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

import com.sun.jdi.Bootstrap;
import java.io.IOException;
import java.util.Map;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;

public class VMConnector {

    public VirtualMachine connect(int port)
            throws IOException {

        String strPort = Integer.toString(port);
        AttachingConnector connector = getConnector();
        try {
            VirtualMachine vm = connect(connector, strPort);
            return vm;
        } catch (IllegalConnectorArgumentsException e) {
            throw new IllegalStateException(e);
        }
    }

    private AttachingConnector getConnector() {

        VirtualMachineManager virtualMachineManager = Bootstrap.virtualMachineManager();
        for (Connector connector : virtualMachineManager.attachingConnectors()) {
            if ("com.sun.jdi.SocketAttach".equals(connector.name())) {
                return (AttachingConnector) connector;
            }
        }

        throw new IllegalStateException();
    }

    private VirtualMachine connect(AttachingConnector connector, String port)
        throws IllegalConnectorArgumentsException, IOException {
        
        Map<String, Connector.Argument> defaultArguments = connector.defaultArguments();
        Connector.Argument pidArgument = defaultArguments.get("port");
        if (pidArgument == null) {
            throw new IllegalStateException();
        }
        pidArgument.setValue(port);

        return connector.attach(defaultArguments);
    }
}
