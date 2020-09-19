package com.esdk.sql;

public class OnCondition extends Condition{
  Field leftField,rightField;
  
  public OnCondition(Field leftfield,Field rightfield) {
    leftField=leftfield;
    rightField=rightfield;
  }
  
  public String toString() {
    return leftField.toString().concat("=").concat(rightField.toString());
  }
  
}
