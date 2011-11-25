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
import java.util.Map;
import org.ejdb.InputCommand.Modifier;
import com.sun.jdi.Type;
import com.sun.jdi.ArrayReference;
import com.sun.jdi.Field;
import com.sun.jdi.LocalVariable;
import com.sun.jdi.ObjectReference;
import com.sun.jdi.ReferenceType;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.Value;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class PrintHandlerTest {

    @Mock
    private ThreadReference threadReference;

    private MockCommandHandler commandHandler;

    private PrintHandler printHandler;

    @Before
    public void setUp() {

        commandHandler = new MockCommandHandler();
        printHandler = new PrintHandler(commandHandler, new MockPrintFormatter());
    }

    @Test
    public void testPrintSingleVariable()
            throws Exception {

        StackFrame stackFrame = mock(StackFrame.class);
        LocalVariable localVariable = mock(LocalVariable.class);
        Value value = mock(Value.class);

        InputCommand inputCommand = new InputCommand(InputCommand.Type.PRINT);
        inputCommand.setVariableName("id");

        when(threadReference.frame(0)).thenReturn(stackFrame);
        when(stackFrame.visibleVariableByName("id")).thenReturn(localVariable);
        when(stackFrame.getValue(localVariable)).thenReturn(value);
        when(value.toString()).thenReturn("foo");

        printHandler.value(threadReference, inputCommand);

        assertTrue(commandHandler.command.getText().equals("foo"));
    }

    @Test
    public void testPrintMultipleVariable()
            throws Exception {

        StackFrame stackFrame = mock(StackFrame.class);
        LocalVariable localVariable = mock(LocalVariable.class);
        ObjectReference objectReference = mock(ObjectReference.class);
        ReferenceType referenceType = mock(ReferenceType.class);
        Field field = mock (Field.class);
        Value value = mock(Value.class);

        InputCommand inputCommand = new InputCommand(InputCommand.Type.PRINT);
        inputCommand.setVariableName("myObject.myVar");

        when(threadReference.frame(0)).thenReturn(stackFrame);
        when(stackFrame.visibleVariableByName("myObject")).thenReturn(localVariable);
        when(stackFrame.getValue(localVariable)).thenReturn(objectReference);
        when(objectReference.referenceType()).thenReturn(referenceType);
        when(referenceType.fieldByName("myVar")).thenReturn(field);
        when(objectReference.getValue(field)).thenReturn(value);
        when(value.toString()).thenReturn("inner");

        printHandler.value(threadReference, inputCommand);

        assertTrue(commandHandler.command.getText().equals("inner"));
    }

    @Test
    public void testPrintArrayVariable()
            throws Exception {

        StackFrame stackFrame = mock(StackFrame.class);
        LocalVariable localVariable = mock(LocalVariable.class);

        ObjectReference objectReference = mock(ObjectReference.class);
        Type type = mock(Type.class);
        ReferenceType referenceType = mock(ReferenceType.class);
        Field field = mock (Field.class);

        ObjectReference arrayObjectReference = mock(ObjectReference.class);
        ReferenceType arrayReferenceType = mock(ReferenceType.class);
        Field arrayField = mock(Field.class);
        ArrayReference arrayValue = mock(ArrayReference.class);
        Value arrayReturnValue = mock(Value.class);

        InputCommand inputCommand = new InputCommand(InputCommand.Type.PRINT);
        inputCommand.setVariableName("myObject.myVar[1]");

        when(threadReference.frame(0)).thenReturn(stackFrame);
        when(stackFrame.visibleVariableByName("myObject")).thenReturn(localVariable);
        when(stackFrame.getValue(localVariable)).thenReturn(objectReference);
        when(objectReference.referenceType()).thenReturn(referenceType);
        when(referenceType.fieldByName("myVar")).thenReturn(field);
        when(arrayObjectReference.type()).thenReturn(type);
        when(type.name()).thenReturn("java.util.ArrayList");

        when(objectReference.getValue(field)).thenReturn(arrayObjectReference);
        when(arrayObjectReference.referenceType()).thenReturn(arrayReferenceType);
        when(arrayReferenceType.fieldByName("elementData")).thenReturn(arrayField);
        when(arrayObjectReference.getValue(arrayField)).thenReturn(arrayValue);
        when(arrayValue.getValue(1)).thenReturn(arrayReturnValue);
        when(arrayReturnValue.toString()).thenReturn("arrayVal");
        
        printHandler.value(threadReference, inputCommand);

        assertTrue(commandHandler.command.getText().equals("arrayVal"));
    }

    private class MockCommandHandler implements CommandHandler {

        public OutputCommand command;

        public void sendCommand(OutputCommand command) {

            this.command = command;
        }

        public InputCommand retrieveCommand() {throw new UnsupportedOperationException("Not supported");}
        public void run() {throw new UnsupportedOperationException("Not supported");}
    }

    private class MockPrintFormatter extends PrintFormatter {

        @Override
        public String format(Value value, Map<Modifier, List<String>> modifiers) {

            return String.valueOf(value);
        }
    }
}