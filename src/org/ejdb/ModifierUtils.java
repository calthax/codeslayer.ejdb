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

public class ModifierUtils {

    public static Map<Modifier, String> getModifiers(String args[]) {

        Map<Modifier, String> result = new HashMap<Modifier, String>();

        Modifier lastModifier = null;

        Iterator<String> iterator = Arrays.asList(args).iterator();
        while (iterator.hasNext()) {
            String arg = iterator.next();

            Modifier modifier = Modifier.getModifierByCommand(arg);
            if (modifier != null) {
                lastModifier = modifier;
            }

            String text = result.get(lastModifier);
            if (text == null) {
                result.put(lastModifier, "");
            } else {
                String value = text + " " + arg;
                result.put(lastModifier, value.trim());
            }
        }

        return result;
    }

    public static List<String> getSourcepath(Map<Modifier, String> modifiers) {
        
        String sourcePath = modifiers.get(Modifier.SOURCEPATH);
        if (sourcePath != null) {
            String[] split = sourcePath.split(":");
            return Arrays.asList(split);
        }

        return null;
    }

    public static String getClasspath(Map<Modifier, String> modifiers) {

        return modifiers.get(Modifier.CLASSPATH);
    }

    public static String getLaunch(Map<Modifier, String> modifiers) {

        return modifiers.get(Modifier.LAUNCH);
    }

    public static Integer getPort(Map<Modifier, String> modifiers) {
        
        String port = modifiers.get(Modifier.PORT);
        if (port != null) {
            return Integer.parseInt(port.trim());
        }

        return null;
    }
}
