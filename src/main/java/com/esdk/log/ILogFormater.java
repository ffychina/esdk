/**
 * to format the log string, as if time format, control field output.
 *
 */

package com.esdk.log;

public interface ILogFormater{
  ILogItem format(ILogItem item);

}
