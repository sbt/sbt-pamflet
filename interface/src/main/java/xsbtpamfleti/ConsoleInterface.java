package xsbtpamfleti;

public interface ConsoleInterface {
  void reset();
  ConsoleResponse interpret(String line, boolean synthetic);
}
