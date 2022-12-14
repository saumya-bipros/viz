package com.vizzionnaire.server.config;

import com.fasterxml.classmate.TypeResolver;
import com.vizzionnaire.server.common.data.StringUtils;
import com.vizzionnaire.server.common.data.exception.VizzionnaireErrorCode;
import com.vizzionnaire.server.common.data.security.Authority;
import com.vizzionnaire.server.exception.VizzionnaireCredentialsExpiredResponse;
import com.vizzionnaire.server.exception.VizzionnaireErrorResponse;
import com.vizzionnaire.server.queue.util.TbCoreComponent;
import com.vizzionnaire.server.service.security.auth.rest.LoginRequest;
import com.vizzionnaire.server.service.security.auth.rest.LoginResponse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ExampleBuilder;
import springfox.documentation.builders.OperationBuilder;
import springfox.documentation.builders.RepresentationBuilder;
import springfox.documentation.builders.RequestParameterBuilder;
import springfox.documentation.builders.ResponseBuilder;
import springfox.documentation.schema.Example;
import springfox.documentation.service.ApiDescription;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiListing;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.HttpLoginPasswordScheme;
import springfox.documentation.service.ParameterType;
import springfox.documentation.service.Response;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.service.SecurityScheme;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.ApiListingBuilderPlugin;
import springfox.documentation.spi.service.ApiListingScannerPlugin;
import springfox.documentation.spi.service.contexts.ApiListingContext;
import springfox.documentation.spi.service.contexts.DocumentationContext;
import springfox.documentation.spi.service.contexts.OperationContext;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.spring.web.readers.operation.CachingOperationNameGenerator;
import springfox.documentation.swagger.common.SwaggerPluginSupport;
import springfox.documentation.swagger.web.DocExpansion;
import springfox.documentation.swagger.web.ModelRendering;
import springfox.documentation.swagger.web.OperationsSorter;
import springfox.documentation.swagger.web.UiConfiguration;
import springfox.documentation.swagger.web.UiConfigurationBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.function.Predicate.not;
import static springfox.documentation.builders.PathSelectors.any;
import static springfox.documentation.builders.PathSelectors.regex;

@Slf4j
@Configuration
@TbCoreComponent
@Profile("!test")
public class SwaggerConfiguration {

    @Value("${swagger.api_path_regex}")
    private String apiPathRegex;
    @Value("${swagger.security_path_regex}")
    private String securityPathRegex;
    @Value("${swagger.non_security_path_regex}")
    private String nonSecurityPathRegex;
    @Value("${swagger.title}")
    private String title;
    @Value("${swagger.description}")
    private String description;
    @Value("${swagger.contact.name}")
    private String contactName;
    @Value("${swagger.contact.url}")
    private String contactUrl;
    @Value("${swagger.contact.email}")
    private String contactEmail;
    @Value("${swagger.license.title}")
    private String licenseTitle;
    @Value("${swagger.license.url}")
    private String licenseUrl;
    @Value("${swagger.version}")
    private String version;
    @Value("${app.version:unknown}")
    private String appVersion;

    @Bean
    public Docket vizzionnaireApi() {
        TypeResolver typeResolver = new TypeResolver();
        return new Docket(DocumentationType.OAS_30)
                .groupName("vizzionnaire")
                .apiInfo(apiInfo())
                .additionalModels(
                        typeResolver.resolve(VizzionnaireErrorResponse.class),
                        typeResolver.resolve(VizzionnaireCredentialsExpiredResponse.class),
                        typeResolver.resolve(LoginRequest.class),
                        typeResolver.resolve(LoginResponse.class)
                )
                .select()
                .paths(apiPaths())
                .paths(any())
                .build()
                .globalResponses(HttpMethod.GET,
                        defaultErrorResponses(false)
                )
                .globalResponses(HttpMethod.POST,
                        defaultErrorResponses(true)
                )
                .globalResponses(HttpMethod.DELETE,
                        defaultErrorResponses(false)
                )
                .securitySchemes(newArrayList(httpLogin()))
                .securityContexts(newArrayList(securityContext()))
                .enableUrlTemplating(true);
    }

    @Bean
    @Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
    ApiListingScannerPlugin loginEndpointListingScanner(final CachingOperationNameGenerator operationNames) {
        return new ApiListingScannerPlugin() {
            @Override
            public List<ApiDescription> apply(DocumentationContext context) {
                return List.of(loginEndpointApiDescription(operationNames));
            }

            @Override
            public boolean supports(DocumentationType delimiter) {
                return DocumentationType.SWAGGER_2.equals(delimiter) || DocumentationType.OAS_30.equals(delimiter);
            }
        };
    }

    @Bean
    @Order(SwaggerPluginSupport.SWAGGER_PLUGIN_ORDER)
    ApiListingBuilderPlugin loginEndpointListingBuilder() {
        return new ApiListingBuilderPlugin() {
            @Override
            public void apply(ApiListingContext apiListingContext) {
                if (apiListingContext.getResourceGroup().getGroupName().equals("default")) {
                    ApiListing apiListing = apiListingContext.apiListingBuilder().build();
                    if (apiListing.getResourcePath().equals("/api/auth/login")) {
                        apiListingContext.apiListingBuilder().tags(Set.of(new Tag("login-endpoint", "Login Endpoint")));
                        apiListingContext.apiListingBuilder().description("Login Endpoint");
                    }
                }
            }

            @Override
            public boolean supports(DocumentationType delimiter) {
                return DocumentationType.SWAGGER_2.equals(delimiter) || DocumentationType.OAS_30.equals(delimiter);
            }
        };
    }

    @Bean
    UiConfiguration uiConfig() {
        return UiConfigurationBuilder.builder()
                .deepLinking(true)
                .displayOperationId(false)
                .defaultModelsExpandDepth(1)
                .defaultModelExpandDepth(1)
                .defaultModelRendering(ModelRendering.EXAMPLE)
                .displayRequestDuration(false)
                .docExpansion(DocExpansion.NONE)
                .filter(false)
                .maxDisplayedTags(null)
                .operationsSorter(OperationsSorter.ALPHA)
                .showExtensions(false)
                .showCommonExtensions(false)
                .supportedSubmitMethods(UiConfiguration.Constants.DEFAULT_SUBMIT_METHODS)
                .validatorUrl(null)
                .persistAuthorization(true)
                .syntaxHighlightActivate(true)
                .syntaxHighlightTheme("agate")
                .build();
    }

    private SecurityScheme httpLogin() {
        return HttpLoginPasswordScheme
                .X_AUTHORIZATION_BUILDER
                .loginEndpoint("/api/auth/login")
                .name("HTTP login form")
                .description("Enter Username / Password")
                .build();
    }

    private SecurityContext securityContext() {
        return SecurityContext.builder()
                .securityReferences(defaultAuth())
                .operationSelector(securityPathOperationSelector())
                .build();
    }

    private Predicate<String> apiPaths() {
        return regex(apiPathRegex);
    }

    private Predicate<OperationContext> securityPathOperationSelector() {
        return new SecurityPathOperationSelector(securityPathRegex, nonSecurityPathRegex);
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[3];
        authorizationScopes[0] = new AuthorizationScope(Authority.SYS_ADMIN.name(), "System administrator");
        authorizationScopes[1] = new AuthorizationScope(Authority.TENANT_ADMIN.name(), "Tenant administrator");
        authorizationScopes[2] = new AuthorizationScope(Authority.CUSTOMER_USER.name(), "Customer");
        return newArrayList(
                new SecurityReference("HTTP login form", authorizationScopes));
    }

    private ApiInfo apiInfo() {
        String apiVersion = version;
        if (StringUtils.isEmpty(apiVersion)) {
            apiVersion = appVersion;
        }
        return new ApiInfoBuilder()
                .title(title)
                .description(description)
                .contact(new Contact(contactName, contactUrl, contactEmail))
                .license(licenseTitle)
                .licenseUrl(licenseUrl)
                .version(apiVersion)
                .build();
    }

    private ApiDescription loginEndpointApiDescription(final CachingOperationNameGenerator operationNames) {
        return new ApiDescription(null, "/api/auth/login", "Login method to get user JWT token data", "Login endpoint", Collections.singletonList(
                new OperationBuilder(operationNames)
                        .summary("Login method to get user JWT token data")
                        .tags(Set.of("login-endpoint"))
                        .authorizations(new ArrayList<>())
                        .position(0)
                        .codegenMethodNameStem("loginPost")
                        .method(HttpMethod.POST)
                        .notes("Login method used to authenticate user and get JWT token data.\n\nValue of the response **token** " +
                                "field can be used as **X-Authorization** header value:\n\n`X-Authorization: Bearer $JWT_TOKEN_VALUE`.")
                        .requestParameters(
                                List.of(
                                        new RequestParameterBuilder()
                                                .in(ParameterType.BODY)
                                                .required(true)
                                                .description("Login request")
                                                .content(c ->
                                                         c.requestBody(true)
                                                          .representation(MediaType.APPLICATION_JSON)
                                                          .apply(classRepresentation(LoginRequest.class, false))
                                                )
                                                .build()
                                )
                        )
                        .responses(loginResponses())
                        .build()
        ), false);
    }

    private Collection<Response> loginResponses() {
        List<Response> responses = new ArrayList<>();
        responses.add(
                new ResponseBuilder()
                        .code("200")
                        .description("OK")
                        .representation(MediaType.APPLICATION_JSON)
                        .apply(classRepresentation(LoginResponse.class, true)).
                        build()
        );
        responses.addAll(loginErrorResponses());
        return responses;
    }

    /** Helper methods **/

    private List<Response> defaultErrorResponses(boolean isPost) {
        return List.of(
                errorResponse("400", "Bad Request",
                        VizzionnaireErrorResponse.of(isPost ? "Invalid request body" : "Invalid UUID string: 123", VizzionnaireErrorCode.BAD_REQUEST_PARAMS, HttpStatus.BAD_REQUEST)),
                errorResponse("401", "Unauthorized",
                        VizzionnaireErrorResponse.of("Authentication failed", VizzionnaireErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED)),
                errorResponse("403", "Forbidden",
                        VizzionnaireErrorResponse.of("You don't have permission to perform this operation!",
                        VizzionnaireErrorCode.PERMISSION_DENIED, HttpStatus.FORBIDDEN)),
                errorResponse("404", "Not Found",
                        VizzionnaireErrorResponse.of("Requested item wasn't found!", VizzionnaireErrorCode.ITEM_NOT_FOUND, HttpStatus.NOT_FOUND)),
                errorResponse("429", "Too Many Requests",
                        VizzionnaireErrorResponse.of("Too many requests for current tenant!",
                        VizzionnaireErrorCode.TOO_MANY_REQUESTS, HttpStatus.TOO_MANY_REQUESTS))
        );
    }

    private List<Response> loginErrorResponses() {
        return List.of(
                errorResponse("401", "Unauthorized",
                        List.of(
                                errorExample("bad-credentials", "Bad credentials",
                                    VizzionnaireErrorResponse.of("Invalid username or password", VizzionnaireErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED)),
                                 errorExample("token-expired", "JWT token expired",
                                    VizzionnaireErrorResponse.of("Token has expired", VizzionnaireErrorCode.JWT_TOKEN_EXPIRED, HttpStatus.UNAUTHORIZED)),
                                errorExample("account-disabled", "Disabled account",
                                    VizzionnaireErrorResponse.of("User account is not active", VizzionnaireErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED)),
                                errorExample("account-locked", "Locked account",
                                    VizzionnaireErrorResponse.of("User account is locked due to security policy", VizzionnaireErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED)),
                                errorExample("authentication-failed", "General authentication error",
                                    VizzionnaireErrorResponse.of("Authentication failed", VizzionnaireErrorCode.AUTHENTICATION, HttpStatus.UNAUTHORIZED))
                        )
                ),
                errorResponse("401 ", "Unauthorized (**Expired credentials**)",
                        List.of(
                                errorExample("credentials-expired", "Expired credentials",
                                        VizzionnaireCredentialsExpiredResponse.of("User password expired!", StringUtils.randomAlphanumeric(30)))
                        ), VizzionnaireCredentialsExpiredResponse.class
                )
        );
    }

    private Response errorResponse(String code, String description, VizzionnaireErrorResponse example) {
        return errorResponse(code, description,  List.of(errorExample("error-code-" + code, description, example)));
    }

    private Response errorResponse(String code, String description, List<Example> examples) {
        return errorResponse(code, description, examples, VizzionnaireErrorResponse.class);
    }

    private Response errorResponse(String code, String description, List<Example> examples,
                                   Class<? extends VizzionnaireErrorResponse> errorResponseClass) {
        return new ResponseBuilder()
                .code(code)
                .description(description)
                .examples(examples)
                .representation(MediaType.APPLICATION_JSON)
                .apply(classRepresentation(errorResponseClass, true))
                .build();
    }

    private Example errorExample(String id, String summary, VizzionnaireErrorResponse example) {
        return new ExampleBuilder()
                .mediaType(MediaType.APPLICATION_JSON_VALUE)
                .summary(summary)
                .id(id)
                .value(example).build();
    }

    private Consumer<RepresentationBuilder> classRepresentation(Class<?> clazz, boolean isResponse) {
        return r -> r.model(
                m ->
                        m.referenceModel(ref ->
                                ref.key(k ->
                                        k.qualifiedModelName(q ->
                                                q.namespace(clazz.getPackageName())
                                                        .name(clazz.getSimpleName())).isResponse(isResponse)))
        );
    }

    private static class SecurityPathOperationSelector implements Predicate<OperationContext> {

        private final Predicate<String> securityPathSelector;

        SecurityPathOperationSelector(String securityPathRegex, String nonSecurityPathRegex) {
            this.securityPathSelector = regex(securityPathRegex).and(
                not(
                    regex(nonSecurityPathRegex)
            ));
        }

        @Override
        public boolean test(OperationContext operationContext) {
            return this.securityPathSelector.test(operationContext.requestMappingPattern());
        }
    }


}
