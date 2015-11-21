package repackaged.com.mangofactory.swagger.readers.operation;

import com.fasterxml.classmate.ResolvedType;
import repackaged.com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import repackaged.com.mangofactory.swagger.core.ModelUtils;
import repackaged.com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;

public class OperationResponseClassReader implements RequestMappingReader {
  private static Logger log = LoggerFactory.getLogger(OperationResponseClassReader.class);

  @Override
  public void execute(RequestMappingContext context) {
    SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
    HandlerMethod handlerMethod = context.getHandlerMethod();
    ApiOperation methodAnnotation = AnnotationUtils.findAnnotation(handlerMethod.getMethod(), ApiOperation.class);
    ResolvedType returnType;
    if (null != methodAnnotation && Void.class != methodAnnotation.response()) {
      log.debug("Overriding response class with annotated response class");
      returnType = swaggerGlobalSettings.getTypeResolver().resolve(methodAnnotation.response());
    } else {
      returnType = ModelUtils.handlerReturnType(swaggerGlobalSettings.getTypeResolver(), handlerMethod);
      returnType = swaggerGlobalSettings.getAlternateTypeProvider().alternateFor(returnType);
    }
    if (Void.class.equals(returnType.getErasedType()) || Void.TYPE.equals(returnType.getErasedType())) {
      context.put("responseClass", "void");
      return;
    }
    String responseTypeName = ModelUtils.getResponseClassName(returnType);
    log.debug("Setting response class to:" + responseTypeName);
    context.put("responseClass", responseTypeName);
  }
}
