package jp.kusumotolab.kgenprog.project;

import java.util.Objects;

public class TargetFullyQualifiedMethodName {

  private final TargetFullyQualifiedName className;
  private final String methodName;

  public TargetFullyQualifiedMethodName(String value) {
    final int hashIndex = value.lastIndexOf("#");

    if (hashIndex == -1) {
      throw new IllegalArgumentException("illegal format");
    }

    className = new TargetFullyQualifiedName(value.substring(0, hashIndex));
    methodName = value.substring(hashIndex + 1);
  }

  public TargetFullyQualifiedName getClassName() {
    return className;
  }

  public String getMethodName() {
    return methodName;
  }

  @Override
  public String toString() {
    return getClassName().toString() + "#" + getMethodName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    TargetFullyQualifiedMethodName that = (TargetFullyQualifiedMethodName) o;
    return getClassName().equals(that.getClassName()) &&
        getMethodName().equals(that.getMethodName());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getClassName(), getMethodName());
  }
}
