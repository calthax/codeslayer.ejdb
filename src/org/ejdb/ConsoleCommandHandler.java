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

public class ConsoleCommandHandler extends AbstractCommandHandler {

    public ConsoleCommandHandler(VirtualMachine virtualMachine) {
        
        super(virtualMachine);
    }

    public void sendCommand(OutputCommand command) {

        switch (command.getType()) {
            case ADD_BREAKPOINT:
                System.out.printf("Add breakpoint at %s:%d\n", command.getClassName(), command.getLineNumber());
                break;
            case HIT_BREAKPOINT:
                System.out.printf("Hit breakpoint at %s:%d\n", command.getClassName(), command.getLineNumber());
                break;
            case STEP_OVER:
                System.out.printf("%d %s\n", command.getLineNumber(), command.getText());
                break;
        }
    }
}
