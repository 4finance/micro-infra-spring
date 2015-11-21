package repackaged.com.mangofactory.swagger.core;

import repackaged.com.mangofactory.swagger.readers.Command;

import java.util.List;

//T - result type, typically Map<String, Object>
public class CommandExecutor<T, C> {

  public T execute(List<? extends Command<C>> commands, CommandContext<T> context) {
    if (null != commands) {
      for (Command command : commands) {
        command.execute(context);
      }
      return context.getResult();
    }
    return null;
  }
}
