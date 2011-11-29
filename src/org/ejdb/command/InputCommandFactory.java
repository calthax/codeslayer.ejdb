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
package org.ejdb.command;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.ejdb.Modifiers;

public class InputCommandFactory {

    private static final String PRINT_REXEX = "p\\s+([a-zA-Z._\\d]+)(.*)";
    private static final Pattern PRINT_PATTERN = Pattern.compile(PRINT_REXEX);

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

        Matcher printMatcher = PRINT_PATTERN.matcher(cmd);
        if (!printMatcher.find()) {
            return null;
        }

        InputCommand inputCommand = new InputCommand(commandType);

        String variableName = printMatcher.group(1);
        String text = printMatcher.group(2);
        String[] args = text.split("\\s");
        Modifiers modifiers = new Modifiers(args);
        inputCommand.setModifiers(modifiers);
        inputCommand.setVariableName(variableName);
        return inputCommand;
    }
}
