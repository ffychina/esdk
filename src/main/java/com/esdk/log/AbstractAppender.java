package com.esdk.log;
abstract public class AbstractAppender implements ILogExporter{
  private ILevelEffective _levelEffective;
  protected ILogExporter _nextExporter;

  public AbstractAppender(){}

  public void setLevelEffective(ILevelEffective levelEffective){
    _levelEffective=levelEffective;
  }

  public void write(ILogItem item){
    if(_levelEffective==null)
      new NullPointerException("levelEffective is null").printStackTrace();
    else{
      if(_levelEffective.containLevel(item.getLevel())){
        append(item);
      }
    }
    if(nextExporter()!=null)
      nextExporter().write(item);
  }

  abstract public void append(ILogItem item);

  public void addNextExporter(ILogExporter exporter){
    if(_nextExporter!=null)
      _nextExporter.addNextExporter(exporter);
    else
      _nextExporter=exporter;
  }

  public void appendExporter(ILogExporter exporter) {
    ILogExporter appender=this;
    while(appender.nextExporter()!=null) {
      appender=_nextExporter.nextExporter();
    }
    appender.addNextExporter(exporter);
  }
  
  public ILogExporter nextExporter(){
    return _nextExporter;
  }
  
  public void flush() {
    if(_nextExporter!=null)
      _nextExporter.flush();
  }
}
