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
package org.ejdb.connector;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.connect.Connector.Argument;
import com.sun.jdi.connect.VMStartException;
import java.io.IOException;
import java.util.Map;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.connect.IllegalConnectorArgumentsException;
import com.sun.jdi.connect.LaunchingConnector;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;

public class LaunchConnector implements VirtualMachineConnector {

    private final String exec;
    private final String classpath;

    public LaunchConnector(String exec, String classpath) {

        this.exec = exec;
        this.classpath = classpath;
    }

    public VirtualMachine connect()
            throws Exception {

        LaunchingConnector connector = getConnector();
        try {
            VirtualMachine virtualMachine = connect(connector);
            Process process = virtualMachine.process();
            displayRemoteOutput(process.getErrorStream());
            return virtualMachine;
        } catch (IllegalConnectorArgumentsException e) {
            throw new IllegalStateException(e);
        }
    }

    private LaunchingConnector getConnector() {

        List connectors = Bootstrap.virtualMachineManager().allConnectors();
        Iterator iter = connectors.iterator();
        while (iter.hasNext()) {
            Connector connector = (Connector) iter.next();
            if (connector.name().equals("com.sun.jdi.CommandLineLaunch")) {
                return (LaunchingConnector) connector;
            }
        }

        throw new Error("No launching connector");
    }

    private VirtualMachine connect(LaunchingConnector connector)
            throws IllegalConnectorArgumentsException, IOException, VMStartException {

        Map<String, Connector.Argument> defaultArguments = connector.defaultArguments();
        Argument main = defaultArguments.get("main");
        main.setValue(exec);

        if (classpath != null && classpath.length() > 0) {
            Argument options = defaultArguments.get("options");
            options.setValue("-cp " + classpath);
        }

        return connector.launch(defaultArguments);
    }

    private void dumpStream(InputStream stream) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(stream));
        int i;
        try {
            while ((i = in.read()) != -1) {
                System.out.print((char) i);
            }
        } catch (IOException ex) {
            String s = ex.getMessage();
            if (!s.startsWith("Bad file number")) {
                throw ex;
            }
        }
    }

    private void displayRemoteOutput(final InputStream stream) {

        Thread thr = new Thread("output reader") {

            public void run() {
                try {
                    dumpStream(stream);
                } catch (IOException ex) {
                    //MessageOutput.fatalError("Failed reading output");
                } finally {
                    //notifyOutputComplete();
                }
            }
        };
        thr.setPriority(Thread.MAX_PRIORITY - 1);
        thr.start();
    }
}
