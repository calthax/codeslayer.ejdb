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

public class OutputCommand extends Command {

    private final Type type;
    private String text;

    public OutputCommand(Type type) {

        this.type = type;
    }

    public OutputCommand(Type type, String className, Integer lineNumber) {

        super(className, lineNumber);
        this.type = type;
    }

    public Type getType() {

        return type;
    }

    public String getText() {

        return text;
    }

    public void setText(String text) {

        this.text = text;
    }

    public enum Type {

        INVALID_COMMAND,
        ADD_BREAKPOINT,
        DELETE_BREAKPOINT,
        DELETE_ALL_BREAKPOINTS,
        HIT_BREAKPOINT,
        STEP_OVER_LINE,
        STEP_INTO_LINE,
        STEP_OUT_LINE,
        INVALID_VARIABLE,
        PRINT_VALUE;
    }
}