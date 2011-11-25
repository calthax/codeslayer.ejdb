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

import java.util.ArrayList;
import java.util.Arrays;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.Field;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StringReference;
import com.sun.jdi.Value;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PrintUtils {

    private final static String REGEX = "([a-zA-Z\\d_]+)\\[?([a-zA-Z\\d_]*)\\]?";
    public final static Pattern PATTERN = Pattern.compile(REGEX);

    private PrintUtils() {}
    
    public static Iterator<String> getVariableNames(String variableName) {

        List<String> fieldNames = new ArrayList<String>();
        String[] split = variableName.split("\\.");
        if (split != null && split.length > 0) {
            fieldNames.addAll(Arrays.asList(split));
        } else {
            fieldNames.add(variableName);
        }

        return fieldNames.iterator();
    }

    public static Value findValue(Value value, Iterator<String> variableNames)
            throws Exception {

        if (value == null) {
            return null;
        }

        if (variableNames.hasNext() && value instanceof ObjectReference) {
            ObjectReference objectReference = (ObjectReference)value;
            String variableName = variableNames.next();
            Value valueByObjectReference = getValueByObjectReference(objectReference, variableName);
            return findValue(valueByObjectReference, variableNames);
        }

        return value;
    }

    public static Value getValueByObjectReference(ObjectReference objectReference, String variableName)
            throws Exception {

        ReferenceType referenceType = objectReference.referenceType();

        Matcher matcher = PATTERN.matcher(variableName);
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

     public static Value getValueByType(Matcher matcher, Value value)
            throws Exception {

        String args = matcher.group(2);

        if (args == null || args.length() == 0) {
            return value;
        }

        Class<?> klass = Class.forName(value.type().name());

        if (List.class.isAssignableFrom(klass)) {
            return getListValueType(value, args);
        } else if (Map.class.isAssignableFrom(klass)) {
            return getMapValueType(value, args);
        }

        return null;
    }

    private static Value getListValueType(Value value, String args)
            throws Exception {

        if (value instanceof ObjectReference) {
            ObjectReference objectReference = (ObjectReference)value;

            Field field = objectReference.referenceType().fieldByName("elementData");
            if (field != null) {
                Value result = objectReference.getValue(field);
                if (result != null && result instanceof ArrayReference) {
                    ArrayReference arrayReference = (ArrayReference)result;
                    try {
                        return arrayReference.getValue(Integer.parseInt(args));
                    } catch (IndexOutOfBoundsException e) {
                        // just return null
                    }
                }
            }
        }

        return null;
    }

    private static Value getMapValueType(Value value, String args)
            throws Exception {

        if (value instanceof ObjectReference) {
            ObjectReference objectReference = (ObjectReference)value;

            Field tableField = objectReference.referenceType().fieldByName("table");
            if (tableField != null) {
                Value tableValue = objectReference.getValue(tableField);
                if (tableValue != null && tableValue instanceof ArrayReference) {
                    ArrayReference tableReference = (ArrayReference)tableValue;
                    List<Value> entryValues = tableReference.getValues();
                    for (Value entryValue : entryValues) {
                        if (entryValue != null && entryValue instanceof ObjectReference) {
                            ObjectReference entryReference = (ObjectReference)entryValue;
                            Field keyField = entryReference.referenceType().fieldByName("key");
                            Value keyValue = entryReference.getValue(keyField);
                            if (keyValue != null && keyValue instanceof StringReference) {
                                StringReference keyReference = (StringReference)keyValue;
                                String key = keyReference.value();
                                if (key.equals(args)) {
                                    Field valueField = entryReference.referenceType().fieldByName("value");
                                    return entryReference.getValue(valueField);
                                }
                            }
                        }
                    }
                }
            }
        }

        return null;
    }
}
