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
package org.ejdb.print;

import com.sun.jdi.ArrayReference;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.ejdb.Modifiers;

public class PrintFormatter {

    public String format(Value value, Modifiers modifiers)
            throws Exception {

        List<String> fieldNames = modifiers.getPrintFields();

        try {
            Class<?> klass = Class.forName(value.type().name());

            if (List.class.isAssignableFrom(klass)) {
                int lineNumbers = getLineNumbers(modifiers);
                return formatList(value, fieldNames, lineNumbers);
            } else if (Map.class.isAssignableFrom(klass)) {
                int lineNumbers = getLineNumbers(modifiers);
                if (modifiers.hasPrintKey()) {
                    return formatMap(value, fieldNames, true, lineNumbers);
                } else {
                    return formatMap(value, fieldNames, false, lineNumbers);
                }
            } else {
                return formatObject(value, fieldNames);
            }
        } catch (ClassNotFoundException e) {            
            return formatObject(value, fieldNames);
        }
    }

    private String formatObject(Value value, List<String> fieldNames) 
            throws Exception {

        StringBuilder sb = new StringBuilder();

        if (fieldNames != null) {
            Iterator<String> iterator = fieldNames.iterator();
            while (iterator.hasNext()) {
                String fieldName = iterator.next();
                Iterator<String> variableNames = PrintUtils.getVariableNames(fieldName);
                Value findValue = PrintUtils.findValue(value, variableNames);
                if (findValue == null) {
                    sb.append(fieldName).append(" -> ").append("\"value not found\"");
                } else {
                    sb.append(fieldName).append(" -> ").append(getText(findValue));
                }
                if (iterator.hasNext()) {
                    sb.append("\n");
                }
            }
        } else {
            sb.append(String.valueOf(value));
        }

        return sb.toString();
    }

    private String formatList(Value value, List<String> fieldNames, int lineNumbers)
            throws Exception {

        StringBuilder sb = new StringBuilder();

        if (value instanceof ObjectReference) {
            ObjectReference objectReference = (ObjectReference)value;
            Field field = objectReference.referenceType().fieldByName("elementData");
            if (field != null) {
                Value result = objectReference.getValue(field);
                if (result != null && result instanceof ArrayReference) {
                    ArrayReference arrayReference = (ArrayReference)result;
                    try {
                        List<Value> values = arrayReference.getValues();
                        Iterator<Value> iterator = values.iterator();
                        int count = 1;
                        while (iterator.hasNext() && count <= lineNumbers) {
                            Value arrayValue = iterator.next();
                            if (count > 1) {
                                sb.append("\n\n");
                            }
                            sb.append(formatObject(arrayValue, fieldNames));
                            count++;
                        }
                    } catch (IndexOutOfBoundsException e) {
                        // just return null
                    }
                }
            }
        }

        return sb.toString();
    }

    private String formatMap(Value value, List<String> fieldNames, boolean printKeys, int lineNumbers)
            throws Exception {

        StringBuilder sb = new StringBuilder();

        if (value instanceof ObjectReference) {
            ObjectReference objectReference = (ObjectReference)value;

            Field tableField = objectReference.referenceType().fieldByName("table");
            if (tableField != null) {
                Value tableValue = objectReference.getValue(tableField);
                if (tableValue != null && tableValue instanceof ArrayReference) {
                    ArrayReference tableReference = (ArrayReference)tableValue;
                    List<Value> entryValues = tableReference.getValues();
                    Iterator<Value> iterator = entryValues.iterator();
                    int count = 1;
                    while (iterator.hasNext() && count <= lineNumbers) {
                        Value entryValue = iterator.next();
                        if (entryValue != null && entryValue instanceof ObjectReference) {
                            ObjectReference entryReference = (ObjectReference)entryValue;
                            Field keyField = entryReference.referenceType().fieldByName("key");
                            Value keyValue = entryReference.getValue(keyField);
                            if (keyValue != null && keyValue instanceof StringReference) {
                                if (count > 1) {
                                    sb.append("\n\n");
                                }
                                if (printKeys) {
                                    sb.append(formatObject(keyValue, fieldNames));
                                } else {
                                    Field valueField = entryReference.referenceType().fieldByName("value");
                                    Value valueValue = entryReference.getValue(valueField);
                                    sb.append(formatObject(valueValue, fieldNames));
                                }
                                count++;
                            }
                        }
                    }
                }
            }
        }

        return sb.toString();
    }

    private String getText(Value value) {

        if (value instanceof ObjectReference) {
            return String.valueOf(value);
        }

        return "";
    }

    private int getLineNumbers(Modifiers modifiers) {
        
        String number = modifiers.getPrintNumber();
        if (number != null) {
            return Integer.valueOf(number);
        }

        return 10;
    }
}
