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

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class InputCommandFactoryTest {

    @Test
    public void testBreak() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("break org.jmesaweb.controller.BasicPresidentController:63").get(0);
        
        assertEquals(inputCommand.getType(), InputCommand.Type.BREAK);
        assertEquals(inputCommand.getClassName(), "org.jmesaweb.controller.BasicPresidentController");
        assertEquals(inputCommand.getLineNumber(), Integer.valueOf("63"));
    }

    @Test
    public void testBreakInvalidNoTokens() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("break").get(0);
        assertNull(inputCommand);
    }

    @Test
    public void testBreakInvalidNumberTokens() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("break foo").get(0);
        assertNull(inputCommand);
    }

    @Test
    public void testBreakInvalidLineNumberToken() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("break org.jmesaweb.controller.BasicPresidentController:foo").get(0);
        assertNull(inputCommand);
    }

    @Test
    public void testPrintField() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("p tableModel.items -f name.firstName name.lastName").get(0);

        assertEquals(inputCommand.getType(), InputCommand.Type.PRINT);
        
        List<String> fieldNames = inputCommand.getModifiers().getPrintFields();
        assertNotNull(fieldNames);
        assertEquals(fieldNames.get(0), "name.firstName");
        assertEquals(fieldNames.get(1), "name.lastName");
    }

    @Test
    public void testPrintFieldMultipleArgs() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("p tableModel.items -f name.firstName name.lastName").get(0);

        assertEquals(inputCommand.getType(), InputCommand.Type.PRINT);

        List<String> fieldNames = inputCommand.getModifiers().getPrintFields();
        assertNotNull(fieldNames);
        assertEquals(fieldNames.get(0), "name.firstName");
        assertEquals(fieldNames.get(1), "name.lastName");
    }

    @Test
    public void testPrintFieldSingleArg() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("p tableModel.items -f name.firstName").get(0);

        assertEquals(inputCommand.getType(), InputCommand.Type.PRINT);

        List<String> fieldNames = inputCommand.getModifiers().getPrintFields();
        assertNotNull(fieldNames);
        assertEquals(fieldNames.get(0), "name.firstName");
    }

    @Test
    public void testPrintKey() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("p tableModel.items -f name.firstName -k").get(0);

        assertEquals(inputCommand.getType(), InputCommand.Type.PRINT);

        boolean key = inputCommand.getModifiers().hasPrintKey();
        assertTrue(key);
    }

    @Test
    public void testPrintNumber() {

        InputCommandFactory inputCommandFactory = new InputCommandFactory();
        InputCommand inputCommand = inputCommandFactory.create("p tableModel.items -f name.firstName -k -n 5").get(0);

        assertEquals(inputCommand.getType(), InputCommand.Type.PRINT);
        
        String number = inputCommand.getModifiers().getPrintNumber();

        assertNotNull(number);
        assertEquals(number, "5");
    }
}