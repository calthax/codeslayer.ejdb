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
package org.ejdb.handler;

import com.sun.jdi.VirtualMachine;
import org.ejdb.command.OutputCommand;
import org.ejdb.command.OutputFormatter;
import org.ejdb.print.PrintColumn;
import org.ejdb.print.PrintRow;

public class InteractiveCommandHandler extends AbstractCommandHandler {

    private final OutputFormatter outputFormatter;

    public InteractiveCommandHandler(VirtualMachine virtualMachine, BreakpointHandler breakpointHandler, OutputFormatter outputFormatter) {
        
        super(virtualMachine, breakpointHandler);
        this.outputFormatter = outputFormatter;
    }

    public void sendCommand(OutputCommand outputCommand) {

        switch (outputCommand.getType()) {
            case READY:
                System.out.printf("<%s/>\n", outputCommand.getText());
                break;
            case INVALID_COMMAND:
                System.out.printf("Invalid command: \"%s\".\n", outputCommand.getText());
                break;
            case HIT_BREAKPOINT:
                System.out.println(outputFormatter.formatHitBreakpoint(outputCommand));
                break;
            case INVALID_BREAKPOINT:
                System.out.printf("Invalid breakpoint at %s:%d\n", outputCommand.getClassName(), outputCommand.getLineNumber());
                break;
            case STEP_OVER_LINE:
                System.out.println(outputFormatter.formatStep(outputCommand));
                break;
            case STEP_INTO_LINE:
                System.out.println(outputFormatter.formatStep(outputCommand));
                break;
            case STEP_OUT_LINE:
                System.out.println(outputFormatter.formatStep(outputCommand));
                break;
            case PRINT_VALUE:
                System.out.printf("%s\n", getPrintTable(outputCommand));
                break;
            case UNDEFINED_SOURCE:
                System.out.printf("Not able to get the source for %s.\n", outputCommand.getText());
                break;
            case INVALID_VARIABLE:
                System.out.printf("Invalid print request.\n");
                break;
        }
    }

    private String getPrintTable(OutputCommand outputCommand) {

        StringBuilder sb = new StringBuilder();

        sb.append("<print-table>");

        for (PrintRow printRow : outputCommand.getPrintRows()) {
            sb.append("<print-row>");
            for (PrintColumn printColumn : printRow.getColumns()) {
                sb.append("<print-column");
                sb.append(" name=\"").append(printColumn.getName()).append("\"");
                String value = printColumn.getValue();
                value = value.replaceAll("\"", "&quot;");
                value = value.replaceAll("'", "&apos;");
                value = value.replaceAll("\"", "&quot;");
                value = value.replaceAll("<", "&lt;");
                value = value.replaceAll(">", "&gt;");
                value = value.replaceAll("&", "&amp;");
                value = value.replaceAll("\n", ""); // strip out the new line or else the buffer gets flushed automatically
                sb.append(" value=\"").append(value).append("\"");
                sb.append("/>");
            }
            sb.append("</print-row>");
        }

        sb.append("</print-table>");

        return sb.toString();
    }
}
