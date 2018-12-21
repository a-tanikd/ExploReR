package jp.kusumotolab.kgenprog.ga.mutation;

import jp.kusumotolab.kgenprog.ga.UnknownMethodName;
import jp.kusumotolab.kgenprog.project.FullyQualifiedName;

public class Scope {

  public enum Type {
    PROJECT, PACKAGE, FILE, METHOD
  }

  private final Type type;
  private final FullyQualifiedName fqn;
  private final MethodName methodName;

  public Scope(final Type type, final FullyQualifiedName fqn) {
    this(type, new UnknownMethodName(fqn));
  }

  public Scope(final Type type, final FullyQualifiedName fqn, final String methodName) {
    this(type, new MethodName(fqn, methodName));
  }

  public Scope(final Type type, final MethodName methodName) {
    this.type = type;
    this.fqn = methodName.getFqn();
    this.methodName = methodName;
  }

  public Type getType() {
    return type;
  }

  public FullyQualifiedName getFqn() {
    return fqn;
  }

  public MethodName getMethodName() {
    return methodName;
  }
}
