package com.jetbrains.python.codeInsight.controlflow;

import com.intellij.codeInsight.controlflow.ControlFlowBuilder;
import com.intellij.codeInsight.controlflow.impl.InstructionImpl;
import com.jetbrains.python.psi.PyElement;
import org.jetbrains.annotations.NonNls;

public class ReadWriteInstruction extends InstructionImpl {

  public enum ACCESS {
    READ(true, false),
    WRITE(false, true),
    READWRITE(true, true);

    private final boolean isWrite;
    private final boolean isRead;

    ACCESS(boolean read, boolean write) {
      isWrite = write;
      isRead = read;
    }
    public boolean isWriteAccess(){
      return isWrite;
    }
    public boolean isReadAccess(){
      return isRead;
    }
  }

  public String myName;
  private final ACCESS myAccess;

  public ReadWriteInstruction(final ControlFlowBuilder builder,
                              final PyElement element,
                              final String name,
                              final ACCESS access) {
    super(builder, element);
    myName = name;
    myAccess = access;
  }

  public String getName() {
    return myName;
  }

  public ACCESS getAccess() {
    return myAccess;
  }

  @NonNls
  public String getElementPresentation() {
    return myAccess + " ACCESS: " + myName;
  }
}
