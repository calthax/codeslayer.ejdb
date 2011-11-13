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

public class InputCommandFactory {

    public InputCommand create(String cmd) {

        InputCommand.Type commandType = getCommandType(cmd);

        switch (commandType) {
            case QUIT:
            case NEXT:
            case STEP:
            case FINISH:
            case CONTINUE:
                return new InputCommand(commandType);
            case BREAK:
                return getBreakCommand(cmd, commandType);
            case DELETE:
                return getDeleteCommand(cmd, commandType);
        }

        return null;
    }

    private InputCommand.Type getCommandType(String cmd) {

        if (cmd.startsWith("break")) { // break org.jmesaweb.controller.BasicPresidentController:63
            return InputCommand.Type.BREAK;
        } else if (cmd.startsWith("delete")) { // delete org.jmesaweb.controller.BasicPresidentController:63
            return InputCommand.Type.DELETE;
        }

        return InputCommand.Type.getTypeByKey(cmd);
    }

    private InputCommand getBreakCommand(String cmd, InputCommand.Type commandType) {

        String substring = cmd.substring("break ".length(), cmd.length());
        String[] split = substring.split(":");
        String className = split[0];
        String lineStr = split[1];
        return new InputCommand(commandType, className, Integer.valueOf(lineStr));
    }

    private InputCommand getDeleteCommand(String cmd, InputCommand.Type commandType) {

        String trimCommand = cmd.trim();
        if (trimCommand.equals("delete")) {
            return new InputCommand(commandType);
        }

        String substring = cmd.substring("delete ".length(), cmd.length());
        String[] split = substring.split(":");
        String className = split[0];
        String lineStr = split[1];
        return new InputCommand(commandType, className, Integer.valueOf(lineStr));
    }
}
