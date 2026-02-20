## Reuse Swagger/OpenAPI annotations
Use OpenAPI annotations directly on controller methods/params and model classes for detailed descriptions, examples, and response schemas. [docs.swagger](https://docs.swagger.io/swagger-core/v2.2.6/apidocs/io/swagger/v3/oas/annotations/media/Schema.html)

Example (controller):
```java
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@Operation(
  summary = "Get employee by id",
  description = "Returns a single employee if it exists.",
  responses = {
    @ApiResponse(responseCode = "200",
      content = @Content(mediaType = "application/json",
        schema = @Schema(implementation = EmployeeDto.class),
        examples = @ExampleObject(value = "{\"id\":1,\"name\":\"Ravi\"}")
      )
    ),
    @ApiResponse(responseCode = "404", description = "Not found")
  }
)
public EmployeeDto getById(
  @Parameter(description = "Employee id", example = "1", required = true)
  @PathVariable long id
) { ... }
```
`@Parameter` is meant for request parameters (path/query/header/cookie), while `@Schema` is meant for data model properties/structures. [baeldung](https://www.baeldung.com/swagger-parameter-vs-schema)

Example (DTO):
```java
import io.swagger.v3.oas.annotations.media.Schema;

public class EmployeeDto {
  @Schema(description = "Employee identifier", example = "1")
  public Long id;

  @Schema(description = "Full name", example = "Ravi Kumar")
  public String name;
}
```
`@Schema` supports many fields like `description`, `example`, `requiredMode`, etc. [docs.swagger](https://docs.swagger.io/swagger-core/v2.2.6/apidocs/io/swagger/v3/oas/annotations/media/Schema.html)

## Create your own annotation + customize spec (true “custom annotation”)
If you want something like `@TenantHeaderRequired` or `@StandardErrorResponses`, define your annotation and then implement an `OperationCustomizer` that detects it and edits the `Operation`. Springdoc supports customizing operations/spec via customizers (feature documented by springdoc). [stackoverflow](https://stackoverflow.com/questions/71876731/springdoc-openapi-not-including-global-headers-or-responses-in-api-docs)

1) Your annotation:
```java
import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TenantHeaderRequired {}
```

2) Register an `OperationCustomizer`:
```java
import io.swagger.v3.oas.models.parameters.HeaderParameter;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

@Configuration
class OpenApiCustomAnnotationsConfig {

  @Bean
  OperationCustomizer tenantHeaderCustomizer() {
    return (operation, handlerMethod) -> {
      if (hasTenantAnnotation(handlerMethod)) {
        operation.addParametersItem(new HeaderParameter()
          .name("X-Tenant-Id")
          .required(true)
          .description("Tenant identifier"));
      }
      return operation;
    };
  }

  private boolean hasTenantAnnotation(HandlerMethod hm) {
    return hm.hasMethodAnnotation(TenantHeaderRequired.class)
        || hm.getBeanType().isAnnotationPresent(TenantHeaderRequired.class);
  }
}
```
