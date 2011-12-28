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

import org.junit.Test;
import static org.junit.Assert.*;

public class XmlOutputFormatterTest {

    private final XmlOutputFormatter formatter = new XmlOutputFormatter();

    @Test
    public void testFormatReady() {

        OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.HIT_BREAKPOINT);
        outputCommand.setText("ready");
        String xml = "<ready/>";
        String result = formatter.formatReady(outputCommand);
        assertEquals(result, xml);
    }

    @Test
    public void testFormatHitBreakpoint() {

        OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.HIT_BREAKPOINT);
        SourceLine sourceLine = new SourceLine();
        sourceLine.setFilePath("/home/jeff/jmesa/src/org/jmesa/core/CoreContextTest.java");
        sourceLine.setLineNumber(60);
        outputCommand.setSourceLine(sourceLine);
        String xml = "<hit-breakpoint file_path=\"/home/jeff/jmesa/src/org/jmesa/core/CoreContextTest.java\" line_number=\"60\"/>";
        String result = formatter.formatHitBreakpoint(outputCommand);
        assertEquals(result, xml);
    }

    @Test
    public void testFormatStep() {

        OutputCommand outputCommand = new OutputCommand(OutputCommand.Type.STEP_INTO_LINE);
        SourceLine sourceLine = new SourceLine();
        sourceLine.setFilePath("/home/jeff/jmesa/src/org/jmesa/core/CoreContextTest.java");
        sourceLine.setLineNumber(60);
        outputCommand.setSourceLine(sourceLine);
        String xml = "<step file_path=\"/home/jeff/jmesa/src/org/jmesa/core/CoreContextTest.java\" line_number=\"60\"/>";
        String result = formatter.formatStep(outputCommand);
        assertEquals(result, xml);
    }
}
