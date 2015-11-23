package repackaged.com.mangofactory.swagger.readers.operation.parameter;

import repackaged.com.mangofactory.swagger.readers.Command;
import repackaged.com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.core.MethodParameter;

public class ParameterAccessReader implements Command<RequestMappingContext> {
  @Override
  public void execute(RequestMappingContext context) {
    MethodParameter methodParameter = (MethodParameter) context.get("methodParameter");
    ApiParam apiParam = methodParameter.getParameterAnnotation(ApiParam.class);
    String access = "";
    if (null != apiParam) {
      access = apiParam.access();
    }
    context.put("paramAccess", access);
  }
}
