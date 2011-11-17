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

        if (commandType == null) {
            return null;
        }

        switch (commandType) {
            case BREAK:
                return getBreakCommand(cmd, commandType);
            case DELETE:
                return getDeleteCommand(cmd, commandType);
            case PRINT:
                return getPrintCommand(cmd, commandType);
            default:
                return new InputCommand(commandType);
        }
    }

    private InputCommand.Type getCommandType(String cmd) {

        if (cmd.startsWith("break")) { // break org.jmesaweb.controller.BasicPresidentController:63
            return InputCommand.Type.BREAK;
        } else if (cmd.startsWith("delete")) { // delete org.jmesaweb.controller.BasicPresidentController:63
            return InputCommand.Type.DELETE;
        } else if (cmd.startsWith("p")) {
            return InputCommand.Type.PRINT;
        }

        return InputCommand.Type.getTypeByKey(cmd);
    }

    private InputCommand getBreakCommand(String cmd, InputCommand.Type commandType) {

        int length = cmd.length();
        if (length <= 6) {
            return null;
        }

        String substring = cmd.substring("break ".length(), length);
        String[] split = substring.split(":");
        if (split == null || split.length != 2) {
            return null;
        }

        try {
            String className = split[0];
            String lineStr = split[1];
            Integer lineNumber = Integer.valueOf(lineStr);
            return new InputCommand(commandType, className, lineNumber);
        } catch (NumberFormatException e) {
            return null;
        }
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

    private InputCommand getPrintCommand(String cmd, InputCommand.Type commandType) {

        String variableName = cmd.substring("p ".length(), cmd.length());
        InputCommand inputCommand = new InputCommand(commandType);
        inputCommand.setVariableName(variableName);
        return inputCommand;
    }
}
