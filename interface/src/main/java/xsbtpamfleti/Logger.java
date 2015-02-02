// originally from sbt
package xsbtpamfleti;

public interface Logger
{
  public void error(F0<String> msg);
  public void warn(F0<String> msg);
  public void info(F0<String> msg);
  public void debug(F0<String> msg);
  public void trace(F0<Throwable> exception);
}
