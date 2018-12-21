package jp.kusumotolab.kgenprog.ga;

import jp.kusumotolab.kgenprog.ga.mutation.MethodName;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

public class UnknownMethodName extends MethodName {

  public UnknownMethodName(final FullyQualifiedName fullyQualifiedName) {
    super(fullyQualifiedName, "");
  }

  @Override
  public String getMethodName() {
    throw new UnsupportedOperationException();
  }
}
