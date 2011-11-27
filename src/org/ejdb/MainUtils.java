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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainUtils {

    private static final String SOURCEPATH_REGEX = ".*?-sourcepath\\s+(.*)(\\s+(-port|-launch|-classpath)\\s+)?";
    private static final Pattern SOURCEPATH = Pattern.compile(SOURCEPATH_REGEX);

    private static final String CLASSPATH_REGEX = ".*?-classpath\\s+(.*)(\\s+(-port|-launch|-sourcepath)\\s+)?";
    private static final Pattern CLASSPATH = Pattern.compile(CLASSPATH_REGEX);

    private static final String LAUNCH_REGEX = ".*?-launch\\s+(.*)(\\s+(-port|-sourcepath|-classpath)\\s+)?";
    private static final Pattern LAUNCH = Pattern.compile(LAUNCH_REGEX);

    private static final String PORT_REGEX = ".*?-port\\s+(\\d+)(\\s+(-sourcepath|-launch|-classpath)\\s+)?";
    private static final Pattern PORT = Pattern.compile(PORT_REGEX);

    public static String getModifiers(String args[]) {

        StringBuilder sb = new StringBuilder();

        for (String arg : args) {
            sb.append(arg).append(" ");
        }

        return sb.toString();
    }

    public static List<String> getSourcepath(String modifiers) {

        Matcher matcher = SOURCEPATH.matcher(modifiers);
        if (matcher.find()) {
            String args = matcher.group(1);
            String[] split = args.split("\\s");
            return Arrays.asList(split);
        }

        return null;
    }

    public static String getClasspath(String modifiers) {

        Matcher matcher = CLASSPATH.matcher(modifiers);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    public static String getLaunch(String modifiers) {

        Matcher matcher = LAUNCH.matcher(modifiers);
        if (matcher.find()) {
            return matcher.group(1);
        }

        return null;
    }

    public static Integer getPort(String modifiers) {

        Matcher matcher = PORT.matcher(modifiers);
        if (matcher.find()) {
            String args = matcher.group(1);
            return Integer.parseInt(args.trim());
        }

        return null;
    }
}
