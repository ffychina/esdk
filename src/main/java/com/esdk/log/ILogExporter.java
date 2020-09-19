/**
 * 输出的对象,一般包括控制台,文件,数据库,邮箱,及其输出的格式
 *
 */

package com.esdk.log;

public interface ILogExporter{
  void write(ILogItem item);
  ILogExporter nextExporter();
  void addNextExporter(ILogExporter exporter);
  void setLevelEffective(ILevelEffective levelEffective);
  void flush();//submit buffer,clear buffer,current use for mailappender,send mail when perform flush.
}
