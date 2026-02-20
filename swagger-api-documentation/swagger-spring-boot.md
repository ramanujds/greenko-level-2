## 1) Add the dependency

### Spring Boot 3.x (Spring MVC)
**Maven**
```xml
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version><!-- latest release --></version>
</dependency>
```
Springdoc’s starter is documented in the project README. [github](https://github.com/springdoc/springdoc-openapi)

**Gradle**
```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:latest'
```
(Shown in the same springdoc docs/readme.) [github](https://github.com/springdoc/springdoc-openapi)

After you start the app, Swagger UI is typically available at `/swagger-ui.html`, and the OpenAPI JSON at `/v3/api-docs` (YAML at `/v3/api-docs.yaml`). [springdoc](http://springdoc.org)

## 2) (Optional) Customize paths
In `application.properties`:
```properties
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```
These properties are explicitly supported and commonly used for custom endpoints. [dev](https://dev.to/olymahmud/integrating-openapi-documentation-and-swagger-ui-in-spring-boot-38p6)

## 3) Add metadata and endpoint docs
Add a basic OpenAPI bean to set title/version/etc. (this drives what Swagger UI displays). [github](https://github.com/springdoc/springdoc-openapi)

```java
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  OpenAPI api() {
    return new OpenAPI().info(new Info()
      .title("My Service API")
      .version("v1"));
  }
}
```

Then annotate controllers/DTOs as needed using OpenAPI annotations (springdoc supports complementing the generated spec with swagger-annotations). [springdoc](https://springdoc.org)

## 4) If you use Spring Security (common gotcha)
Swagger UI and `/v3/api-docs` are just endpoints—if Security blocks them, you must permit them (e.g., allow `/swagger-ui/**`, `/swagger-ui.html`, `/v3/api-docs/**`). The fact that springdoc serves these endpoints at those paths is documented by springdoc. [springdoc](http://springdoc.org)

## 5) Quick verification checklist
- OpenAPI JSON: `http://localhost:8080/v3/api-docs` (or your custom `springdoc.api-docs.path`). [springdoc](http://springdoc.org)
- Swagger UI: `http://localhost:8080/swagger-ui.html` (or your custom `springdoc.swagger-ui.path`). [github](https://github.com/springdoc/springdoc-openapi)

