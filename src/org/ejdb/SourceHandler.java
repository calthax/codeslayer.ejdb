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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class SourceHandler {

    private final String[] sourcePaths;

    public SourceHandler(String[] sourcePaths) {

        this.sourcePaths = sourcePaths;
    }

    public String getLine(String sourcePath, int lineNumber) {

        String result = "";

        try {
            String filePath = sourcePaths[0] + sourcePath;

            FileReader input = new FileReader(filePath);
            BufferedReader reader = new BufferedReader(input);

            int count = 1;
            String line = reader.readLine();
            while (line != null){
                if (count == lineNumber) {
                    result = line;
                    break;
                }
                line = reader.readLine();
                count++;
            }

            reader.close();
        } catch (IOException e){
            e.printStackTrace();
        }

        return result;
    }
}
