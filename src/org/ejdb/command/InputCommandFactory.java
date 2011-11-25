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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputCommandFactory {

    private static final String PRINT_REXEX = "p\\s+([a-zA-Z._\\d]+)(.*)";
    private static final Pattern PRINT_PATTERN = Pattern.compile(PRINT_REXEX);

    private static final String PRINT_FIELD_REXEX = ".*?-f\\s+([a-zA-Z._\\d\\s]+)";
    private static final Pattern PRINT_FIELD = Pattern.compile(PRINT_FIELD_REXEX);

    private static final String PRINT_KEY_REXEX = ".*?-k";
    private static final Pattern PRINT_KEY = Pattern.compile(PRINT_KEY_REXEX);

    private static final String PRINT_LINE_REXEX = ".*?-n\\s+(\\d+)";
    private static final Pattern PRINT_LINE = Pattern.compile(PRINT_LINE_REXEX);

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
        String modifiers = printMatcher.group(2);

        Matcher fieldMatcher = PRINT_FIELD.matcher(modifiers);
        if (fieldMatcher.find()) {
            String args = fieldMatcher.group(1);
            String[] split = args.split("\\s");
            for (String arg : split) {
                inputCommand.addModifier(InputCommand.Modifier.FIELD, arg);
            }
        }

        Matcher lineMatcher = PRINT_LINE.matcher(modifiers);
        if (lineMatcher.find()) {
            String args = lineMatcher.group(1);
            inputCommand.addModifier(InputCommand.Modifier.NUMBER, args);
        }

        Matcher keyMatcher = PRINT_KEY.matcher(modifiers);
        if (keyMatcher.find()) {
            inputCommand.addModifier(InputCommand.Modifier.KEY, "true");
        }
        
        inputCommand.setVariableName(variableName);
        return inputCommand;
    }
}
