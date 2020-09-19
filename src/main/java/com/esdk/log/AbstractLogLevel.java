package com.esdk.log;

abstract public class AbstractLogLevel implements ILogLevel{
  protected String[] _levelCode;
  private int _intLevel=-1;
  AbstractLogLevel(int iLevel)throws IndexOutOfBoundsException{
    checkLevel(iLevel);
    _intLevel=iLevel;
  }
  
  abstract public ILogLevel getInstance(int iLevel);
  
  protected void checkLevel(int iLevel)throws IndexOutOfBoundsException{
    if(iLevel<0&&iLevel>=getLevelArray().length)
      throw new IndexOutOfBoundsException("Index: "+iLevel+", Size: "+getLevelArray().length);
  }
  
  public String getProperty(){
    return _levelCode[_intLevel];
  }
  public ILogLevel getNextlowerLevel(){
    if(_intLevel==0)
      return null;
    return getInstance(_intLevel-1);
  }
  public ILogLevel getNextHigherLevel(){
    if(_intLevel>=getLevelArray().length-1)
      return null;
    return getInstance(_intLevel+1);
  }

  public boolean equals(Object obj) {
  	if(obj==null)
  		return false;
  	if (this == obj)
      return true;
    if(obj.getClass().equals(this.getClass()))
        if(this._intLevel==((AbstractLogLevel)obj).getLevel())
          return true;
    return false;
  }
  
  public String[] getLevelArray(){
    return _levelCode;
  }

  public String toString(){
    return getProperty();
  }
  
  public int getLevel(){
    return _intLevel;
  }
  
  
}
