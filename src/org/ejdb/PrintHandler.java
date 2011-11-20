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

import com.sun.jdi.ArrayReference;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrintHandler {

    private final static String regex = "([a-zA-Z\\d_]+)\\[?(\\d*)\\]?";
    private final Pattern pattern = Pattern.compile(regex);

    private final CommandHandler commandHandler;

    public PrintHandler(CommandHandler commandHandler) {

        this.commandHandler = commandHandler;
    }
    
    public void value(ThreadReference threadReference, InputCommand inputCommand)
            throws Exception {

        Iterator<String> variableNames = getVariableNames(inputCommand);
        String variableName = variableNames.next();
        
        Value value = getValueByThreadReference(threadReference, variableName);
        String text = getTextByValue(value, variableNames);

        if (text == null) {
            OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.INVALID_VARIABLE);
            commandHandler.sendCommand(outputCommand);
        } else {
            OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.PRINT_VALUE);
            outputCommand.setText(text);
            commandHandler.sendCommand(outputCommand);
        }
    }

    private Iterator<String> getVariableNames(InputCommand inputCommand) {

        String variableName = inputCommand.getVariableName();
        List<String> variableNames = new ArrayList<String>();
        String[] split = variableName.split("\\.");
        if (split != null && split.length > 0) {
            variableNames.addAll(Arrays.asList(split));
        } else {
            variableNames.add(variableName);
        }

        return variableNames.iterator();
    }

    private Value getValueByThreadReference(ThreadReference threadReference, String variableName)
            throws Exception {

        StackFrame stackFrame = threadReference.frame(0);

        Matcher matcher = pattern.matcher(variableName);
        if (!matcher.find()) {
            return null;
        }

        String name = matcher.group(1);

        LocalVariable localVariable = stackFrame.visibleVariableByName(name);
        if (localVariable != null) {
            return getValueByType(matcher, stackFrame.getValue(localVariable));
        }

        return getValueByObjectReference(stackFrame.thisObject(), variableName);
    }

    private Value getValueByObjectReference(ObjectReference objectReference, String variableName)
            throws Exception {

        ReferenceType referenceType = objectReference.referenceType();

        Matcher matcher = pattern.matcher(variableName);
        if (!matcher.find()) {
            return null;
        }

        String name = matcher.group(1);

        Field field = referenceType.fieldByName(name);
        if (field == null) {
            return null;
        }

        return getValueByType(matcher, objectReference.getValue(field));
    }

    private Value getValueByType(Matcher matcher, Value value)
            throws Exception {

        String args = matcher.group(2);

        if (args == null || args.length() == 0) {
            return value;
        }

        try {
            if (value instanceof ObjectReference) {
                ObjectReference objectReference = (ObjectReference)value;
                Field field = objectReference.referenceType().fieldByName("elementData");
                if (field != null) {
                    Value result = objectReference.getValue(field);
                    if (result != null && result instanceof ArrayReference) {
                        ArrayReference arrayReference = (ArrayReference)result;
                        return arrayReference.getValue(Integer.parseInt(args));
                    }
                }
            }
        } catch (IndexOutOfBoundsException e) {
            // just return null
        }

        return null;
    }

    private String getTextByValue(Value value, Iterator<String> variableNames) 
            throws Exception {
        
        if (value == null) {
            return null;
        }

        if (variableNames.hasNext() && value instanceof ObjectReference) {
            ObjectReference objectReference = (ObjectReference)value;
            String variableName = variableNames.next();
            Value valueByObjectReference = getValueByObjectReference(objectReference, variableName);
            return getTextByValue(valueByObjectReference, variableNames);
        }

        return String.valueOf(value);
    }
}
