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

public class XmlOutputFormatter implements OutputFormatter {

    public String formatReady(OutputCommand outputCommand) {

        StringBuilder xml = new StringBuilder();
        xml.append("<").append(outputCommand.getText()).append("/>");
        return xml.toString();
    }

    public String formatHitBreakpoint(OutputCommand outputCommand) {

        SourceLine sourceLine = outputCommand.getSourceLine();

        StringBuilder xml = new StringBuilder();
        xml.append("<hit-breakpoint");
        xml.append(" file_path=\"").append(sourceLine.getFilePath()).append("\"");
        xml.append(" line_number=\"").append(sourceLine.getLineNumber()).append("\"");
        xml.append("/>");
        return xml.toString();
    }

    public String formatStep(OutputCommand outputCommand) {

        SourceLine sourceLine = outputCommand.getSourceLine();

        StringBuilder xml = new StringBuilder();
        xml.append("<step");
        xml.append(" file_path=\"").append(sourceLine.getFilePath()).append("\"");
        xml.append(" line_number=\"").append(sourceLine.getLineNumber()).append("\"");
        xml.append("/>");
        return xml.toString();
    }
}
