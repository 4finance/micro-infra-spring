package repackaged.com.mangofactory.swagger.readers.operation;

import repackaged.com.mangofactory.swagger.scanners.RequestMappingContext;

public class OperationNicknameReader implements RequestMappingReader {
  @Override
  public void execute(RequestMappingContext context) {
    context.put("nickname", context.getHandlerMethod().getMethod().getName());
  }
}
