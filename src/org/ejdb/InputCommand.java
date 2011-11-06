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

public class InputCommand extends Command {

    private final Type type;

    public InputCommand(Type type) {

        this.type = type;
    }

    public InputCommand(Type type, String className, Integer lineNumber) {

        super(className, lineNumber);
        this.type = type;
    }

    public Type getType() {

        return type;
    }

    public enum Type {

        BREAK("break"),
        NEXT("n"),
        CONTINUE("c"),
        QUIT("q");

        private final String key;

        private Type(String shortName) {

            this.key = shortName;
        }

        public static Type getTypeByKey(String key) {

            for (Type type : values()) {
                if (type.key.equals(key)) {
                    return type;
                }
            }
            
            throw new IllegalStateException(key + " is not a valid input key");
        }
    }
}
