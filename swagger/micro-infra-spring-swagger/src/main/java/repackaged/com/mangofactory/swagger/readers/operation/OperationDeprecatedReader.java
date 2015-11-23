package repackaged.com.mangofactory.swagger.readers.operation;

import repackaged.com.mangofactory.swagger.scanners.RequestMappingContext;

/**
 * Terrible hack until they fix https://github.com/springfox/springfox/issues/1055
 *
 * TODO: Remove when issue fixed
 *
 */
public class OperationDeprecatedReader implements RequestMappingReader {
  @Override
  public void execute(RequestMappingContext context) {
    //boolean isDeprecated = context.getHandlerMethod().getMethodAnnotation(Deprecated.class) != null;
    //context.put("deprecated", String.valueOf(isDeprecated));
    context.put("deprecated", String.valueOf(false));
  }
}
