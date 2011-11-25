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
import com.sun.jdi.ObjectReference;
import com.sun.jdi.Value;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.ejdb.InputCommand.Modifier;

public class PrintFormatter {

    public String format(Value value, Map<Modifier, List<String>> modifiers) 
            throws Exception {

        List<String> fieldNames = modifiers.get(Modifier.FIELD);
        if (fieldNames != null) {
            Class<?> klass = Class.forName(value.type().name());
            if (List.class.isAssignableFrom(klass)) {
                return formatList(value, fieldNames);
            } else {
                return formatObject(value, fieldNames);
            }
        }
        
        return String.valueOf(value);
    }

    private String formatObject(Value value, List<String> fieldNames) 
            throws Exception {

        StringBuilder sb = new StringBuilder();

        Iterator<String> iterator = fieldNames.iterator();
        while (iterator.hasNext()) {
            String fieldName = iterator.next();
            Iterator<String> variableNames = PrintUtils.getVariableNames(fieldName);
            Value findValue = PrintUtils.findValue(value, variableNames);
            if (findValue == null) {
                sb.append(fieldName).append(" -> ").append("value not found");
            } else {
                sb.append(fieldName).append(" -> ").append(getText(findValue));
            }
            if (iterator.hasNext()) {
                sb.append("\n");
            }
        }

        return sb.toString();
    }

    private String formatList(Value value, List<String> fieldNames) 
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
                        List<Value> values = arrayReference.getValues(0, 10);
                        Iterator<Value> iterator = values.iterator();
                        while (iterator.hasNext()) {
                            Value arrayValue = iterator.next();
                            sb.append(formatObject(arrayValue, fieldNames));
                            if (iterator.hasNext()) {
                                sb.append("\n\n");
                            }
                        }
                    } catch (IndexOutOfBoundsException e) {
                        // just return null
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
}
