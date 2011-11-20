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

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InputCommandFactoryTest {

    @Test
    public void testBreak() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("break org.jmesaweb.controller.BasicPresidentController:63");
        
        assertEquals(inputCommand.getType(), InputCommand.Type.BREAK);
        assertEquals(inputCommand.getClassName(), "org.jmesaweb.controller.BasicPresidentController");
        assertEquals(inputCommand.getLineNumber(), Integer.valueOf("63"));
    }

    @Test
    public void testBreakInvalidNoTokens() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("break");
        assertNull(inputCommand);
    }

    @Test
    public void testBreakInvalidNumberTokens() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("break foo");
        assertNull(inputCommand);
    }

    @Test
    public void testBreakInvalidLineNumberToken() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("break org.jmesaweb.controller.BasicPresidentController:foo");
        assertNull(inputCommand);
    }
}