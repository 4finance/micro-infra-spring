package repackaged.com.mangofactory.swagger.readers.operation;

import com.mangofactory.swagger.models.dto.builder.AuthorizationBuilder;
import com.mangofactory.swagger.models.dto.builder.AuthorizationScopeBuilder;
import repackaged.com.mangofactory.swagger.authorization.AuthorizationContext;
import repackaged.com.mangofactory.swagger.scanners.RequestMappingContext;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.Authorization;
import com.wordnik.swagger.annotations.AuthorizationScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import java.util.List;

import static com.google.common.collect.Lists.*;

public class OperationAuthReader implements RequestMappingReader {

  private static final Logger LOG = LoggerFactory.getLogger(OperationAuthReader.class);
  @Override
  public void execute(RequestMappingContext context) {
    AuthorizationContext authorizationContext = (AuthorizationContext) context.get("authorizationContext");

    HandlerMethod handlerMethod = context.getHandlerMethod();
    String requestMappingPattern = (String) context.get("requestMappingPattern");
    List<com.mangofactory.swagger.models.dto.Authorization> authorizations = newArrayList();

    if (null != authorizationContext) {
      authorizations = authorizationContext.getAuthorizationsForPath(requestMappingPattern);
    }

    ApiOperation apiOperationAnnotation = handlerMethod.getMethodAnnotation(ApiOperation.class);

    if (null != apiOperationAnnotation && null != apiOperationAnnotation.authorizations()) {
      Authorization[] authorizationAnnotations = apiOperationAnnotation.authorizations();
      if (authorizationAnnotations != null
              && authorizationAnnotations.length > 0
              && StringUtils.hasText(authorizationAnnotations[0].value())) {

        authorizations = newArrayList();
        for (Authorization authorization : authorizationAnnotations) {
          String value = authorization.value();
          AuthorizationScope[] scopes = authorization.scopes();
          List<com.mangofactory.swagger.models.dto.AuthorizationScope> authorizationScopeList = newArrayList();
          for (AuthorizationScope authorizationScope : scopes) {
            String description = authorizationScope.description();
            String scope = authorizationScope.scope();
            authorizationScopeList.add(new AuthorizationScopeBuilder().scope(scope).description(description)
                    .build());
          }
          com.mangofactory.swagger.models.dto.AuthorizationScope[] authorizationScopes = authorizationScopeList
                  .toArray(new com.mangofactory.swagger.models.dto.AuthorizationScope[authorizationScopeList.size()]);
          com.mangofactory.swagger.models.dto.Authorization authorizationModel =
                  new AuthorizationBuilder()
                          .type(value)
                          .scopes(authorizationScopes)
                          .build();
          authorizations.add(authorizationModel);
        }
      }
    }
    LOG.debug("Authorization count {} for method {}", authorizations.size(), handlerMethod.getMethod().getName());
    context.put("authorizations", authorizations);
  }
}
