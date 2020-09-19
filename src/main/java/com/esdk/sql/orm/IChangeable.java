package com.esdk.sql.orm;

import java.io.Serializable;

public interface IChangeable extends Serializable{
  boolean isChanged();
  
  /*void notifyChanged(EventObject event);*/
}
