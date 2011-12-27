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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import org.ejdb.command.SourceLine;

public class SourceHandler {

    private final List<String> sourcePaths;

    public SourceHandler(List<String> sourcePaths) {

        this.sourcePaths = sourcePaths;
    }

    public SourceLine getSourceLine(String className, int lineNumber) {

        SourceLine sourceLine = new SourceLine();
        sourceLine.setClassName(className);
        sourceLine.setLineNumber(lineNumber);

        File file = getFile(className);
        if (file == null) {
            throw new UndefinedSourceException(className);
        }

        try {
            FileReader input = new FileReader(file);
            BufferedReader reader = new BufferedReader(input);

            int count = 1;
            String source = reader.readLine();
            while (source != null) {
                if (count == lineNumber) {
                    sourceLine.setFilePath(file.getAbsolutePath());
                    sourceLine.setSourceCode(source);
                    break;
                }
                source = reader.readLine();
                count++;
            }

            reader.close();
        }
        catch (IOException e) {
            System.err.printf("Not able to get the source for class %s.\n", className);
        }

        return sourceLine;
    }

    private File getFile(String className) {

        for (String sourcePath : sourcePaths) {
            File file = new File(sourcePath, className);
            if (file != null && file.exists()) {
                return file;
            }
        }

        return null;
    }
}
