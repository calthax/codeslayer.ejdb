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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Modifiers {

    private Map<Modifier, String> modifiers = new HashMap<Modifier, String>();

    public Modifiers(String args[]) {

        Modifier lastModifier = null;

        Iterator<String> iterator = Arrays.asList(args).iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();

            Modifier modifier = Modifier.getModifierByKey(arg);
            if (modifier != null) {
                lastModifier = modifier;
            }

            String text = modifiers.get(lastModifier);
            if (text == null) {
                modifiers.put(lastModifier, "");
            } else {
                String value = text + " " + arg;
                modifiers.put(lastModifier, value.trim());
            }
        }
    }

    public List<String> getSourcepath() {

        String sourcePath = modifiers.get(Modifier.SOURCEPATH);
        if (sourcePath != null) {
            String[] split = sourcePath.split(":");
            return Arrays.asList(split);
        }

        return null;
    }

    public String getClasspath() {

        return modifiers.get(Modifier.CLASSPATH);
    }

    public String getLaunch() {

        return modifiers.get(Modifier.LAUNCH);
    }

    public Integer getPort() {

        String port = modifiers.get(Modifier.PORT);
        if (port != null) {
            return Integer.parseInt(port.trim());
        }

        return null;
    }

    public List<String> getPrintFields() {

        String printFields = modifiers.get(Modifier.PRINT_FIELD);
        if (printFields != null) {
            String[] split = printFields.split("\\s");
            return Arrays.asList(split);
        }

        return null;
    }

    public String getPrintNumber() {

        return modifiers.get(Modifier.PRINT_NUMBER);
    }

    public boolean hasPrintKey() {

        return modifiers.get(Modifier.PRINT_KEY) != null;
    }

    private enum Modifier {

        SOURCEPATH("-sourcepath"),
        CLASSPATH("-classpath"),
        LAUNCH("-launch"),
        PORT("-port"),
        PRINT_KEY("-k"),
        PRINT_NUMBER("-n"),
        PRINT_FIELD("-f");

        private final String key;

        private Modifier(String key) {

            this.key = key;
        }

        public static Modifier getModifierByKey(String key) {

            for (Modifier value : values()) {
                if (value.key.equals(key)) {
                    return value;
                }
            }

            return null;
        }
    }
}
