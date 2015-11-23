package repackaged.com.mangofactory.swagger.readers.operation.parameter;

import com.fasterxml.classmate.ResolvedType;
import repackaged.com.mangofactory.swagger.configuration.SwaggerGlobalSettings;
import repackaged.com.mangofactory.swagger.readers.Command;
import repackaged.com.mangofactory.swagger.readers.operation.ResolvedMethodParameter;
import repackaged.com.mangofactory.swagger.scanners.RequestMappingContext;
import org.springframework.web.multipart.MultipartFile;

import static com.mangofactory.swagger.models.ResolvedTypes.*;

public class ParameterDataTypeReader implements Command<RequestMappingContext> {

  @Override
  public void execute(RequestMappingContext context) {
    ResolvedMethodParameter methodParameter = (ResolvedMethodParameter) context.get("resolvedMethodParameter");
    SwaggerGlobalSettings swaggerGlobalSettings = (SwaggerGlobalSettings) context.get("swaggerGlobalSettings");
    ResolvedType parameterType = methodParameter.getResolvedParameterType();
    parameterType = swaggerGlobalSettings.getAlternateTypeProvider().alternateFor(parameterType);
    //Multi-part file trumps any other annotations
    if (MultipartFile.class.isAssignableFrom(parameterType.getErasedType())) {
      context.put("dataType", "File");
    } else {
      context.put("dataType", responseTypeName(parameterType));
    }
  }

}
