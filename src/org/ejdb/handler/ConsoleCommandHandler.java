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
package org.ejdb.handler;

import com.sun.jdi.VirtualMachine;
import org.ejdb.command.OutputCommand;

public class ConsoleCommandHandler extends AbstractCommandHandler {

    public ConsoleCommandHandler(VirtualMachine virtualMachine, BreakpointHandler breakpointHandler) {
        
        super(virtualMachine, breakpointHandler);
    }

    public void sendCommand(OutputCommand outputCommand) {

        switch (outputCommand.getType()) {
            case INVALID_COMMAND:
                System.out.printf("Invalid command: \"%s\".\n", outputCommand.getText());
                break;
            case ADD_BREAKPOINT:
                System.out.printf("Add breakpoint at %s:%d\n", outputCommand.getClassName(), outputCommand.getLineNumber());
                break;
            case HIT_BREAKPOINT:
                System.out.printf("Hit breakpoint at %s:%d\n", outputCommand.getClassName(), outputCommand.getLineNumber());
                System.out.printf("%d %s\n", outputCommand.getLineNumber(), outputCommand.getText());
                break;
            case INVALID_BREAKPOINT:
                System.out.printf("Invalid breakpoint at %s:%d\n", outputCommand.getClassName(), outputCommand.getLineNumber());
                break;
            case STEP_OVER_LINE:
                System.out.printf("%d %s\n", outputCommand.getLineNumber(), outputCommand.getText());
                break;
            case STEP_INTO_LINE:
                System.out.printf("Step to %s:%d\n", outputCommand.getClassName(), outputCommand.getLineNumber());
                System.out.printf("%d %s\n", outputCommand.getLineNumber(), outputCommand.getText());
                break;
            case STEP_OUT_LINE:
                System.out.printf("Step to %s:%d\n", outputCommand.getClassName(), outputCommand.getLineNumber());
                System.out.printf("%d %s\n", outputCommand.getLineNumber(), outputCommand.getText());
                break;
            case PRINT_VALUE:
                System.out.printf("%s\n", outputCommand.getText());
                break;
            case UNDEFINED_SOURCE:
                System.out.printf("Not able to get the source for %s.\n", outputCommand.getText());
                break;
            case INVALID_VARIABLE:
                System.out.printf("Invalid print request.\n");
                break;
        }
    }
}
