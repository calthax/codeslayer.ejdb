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

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class AbstractCommandHandler implements CommandHandler {

    BlockingQueue<InputCommand> commands = new LinkedBlockingQueue<InputCommand>();

    final BreakpointManager breakpointManager;
    final InputCommandFactory commandFactory = new InputCommandFactory();

    public AbstractCommandHandler(BreakpointManager breakpointManager) {

        this.breakpointManager = breakpointManager;
    }

    public InputCommand retrieveCommand() {
        try {
            return commands.take();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        throw new IllegalStateException("Not able to retrieve the command.");
    }
}
