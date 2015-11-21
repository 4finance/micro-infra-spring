package repackaged.com.mangofactory.swagger.readers.operation;

import repackaged.com.mangofactory.swagger.scanners.RequestMappingContext;
import com.mangofactory.swagger.models.dto.Parameter;

import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.*;

public abstract class SwaggerParameterReader implements RequestMappingReader {

  @Override
  public final void execute(RequestMappingContext context) {
    List<Parameter> parameters = (List<Parameter>) context.get("parameters");
    if (parameters == null) {
      parameters = newArrayList();
    }
    parameters.addAll(this.readParameters(context));
    context.put("parameters", parameters);
  }

  abstract protected Collection<? extends Parameter> readParameters(RequestMappingContext context);
}
