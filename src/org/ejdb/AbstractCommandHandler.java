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
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractCommandHandler implements CommandHandler {

    /*Use a blocking queue to handle the input command*/
    private BlockingQueue<InputCommand> inputCommands = new LinkedBlockingQueue<InputCommand>();

    private final BreakpointHandler breakpointHandler;
    private final InputCommandFactory inputCommandFactory = new InputCommandFactory();

    public AbstractCommandHandler(VirtualMachine virtualMachine) {

        breakpointHandler = new BreakpointHandler(virtualMachine, this);
    }

    private void setCommand(InputCommand inputCommand) {

        inputCommands.clear();
        inputCommands.add(inputCommand);
    }

    public InputCommand retrieveCommand() {
        
        try {
            inputCommands.clear();
            return inputCommands.take();
        } catch (InterruptedException ex) {}

        throw new IllegalStateException("Not able to retrieve the command.");
    }

    public void run() {

        String lastCmd = null;

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

                    if (cmd != null) {
                        cmd = cmd.trim();
                    }

                    if (cmd == null || cmd.isEmpty()) {
                        if (lastCmd != null) {
                            cmd = lastCmd;
                        } else {
                            continue;
                        }
                    }

                    lastCmd = cmd;

                    InputCommand inputCommand = inputCommandFactory.create(cmd);

                    if (inputCommand == null) {
                        OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.INVALID_COMMAND);
                        outputCommand.setText(cmd);
                        sendCommand(outputCommand);
                        continue;
                    }

                    switch (inputCommand.getType()) {
                        case QUIT:
                            return;
                        case BREAK:
                            breakpointHandler.addBreakpoint(inputCommand);
                            break;
                        case DELETE:
                            breakpointHandler.deleteBreakpoint(inputCommand);
                            break;
                        default:
                            setCommand(inputCommand);
                            break;
                    }
                }
            } catch (Exception e) {
                System.err.println("The console command handler is unable to carry out the command.");
                e.printStackTrace();
                return;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                System.err.println("The console command handler quit unexpectedly.");
                return;
            }
        }
    }
}
