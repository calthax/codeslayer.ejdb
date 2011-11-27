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

import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

public class MainUtilsTest {

    private final static String ARGS = "-port 8000 -sourcePath /home/jeff/workspace/jmesaWeb/src /home/jeff/workspace/jmesa/src -launch java org.junit.runner.JUnitCore test org.jmesa.core.CoreContextTest";

    @Test
    public void testGetSourcePath() {
        
        List<String> sourcePath = MainUtils.getSourcepath(ARGS);
        assertEquals(sourcePath.get(0), "/home/jeff/workspace/jmesaWeb/src");
        assertEquals(sourcePath.get(1), "/home/jeff/workspace/jmesa/src");
    }

    @Test
    public void testGetLaunch() {

        String launch = MainUtils.getLaunch(ARGS);
        assertEquals(launch, "java org.junit.runner.JUnitCore test org.jmesa.core.CoreContextTest");
    }

    @Test
    public void testGetPort() {

        Integer port = MainUtils.getPort(ARGS);
        assertTrue(port.intValue() == 8000);
    }
}
