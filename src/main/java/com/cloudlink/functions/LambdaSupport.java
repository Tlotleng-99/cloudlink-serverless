
package com.cloudlink.functions;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cloudlink.repository.DynamoDbUrlRepository;
import com.cloudlink.service.UrlService;
import com.cloudlink.util.ShortCodeGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Map;

final class LambdaSupport {

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final DynamoDbClient DYNAMO_DB =
            DynamoDbClient.builder().build();

    private LambdaSupport() {
    }

    static UrlService createUrlService() {
        String tableName = System.getenv("TABLE_NAME");

        if (tableName == null || tableName.isBlank()) {
            throw new IllegalStateException(
                    "The TABLE_NAME environment variable is not configured."
            );
        }

        return new UrlService(
                new DynamoDbUrlRepository(DYNAMO_DB, tableName),
                new ShortCodeGenerator()
        );
    }

    static String getShortCode(APIGatewayProxyRequestEvent request) {
        if (request == null) {
            return null;
        }

        // Prefer the shortCode supplied by API Gateway.
        if (request.getPathParameters() != null) {
            String code = request.getPathParameters().get("shortCode");

            if (code != null && !code.isBlank()) {
                return code;
            }
        }

        // Fall back to extracting the code from the request path.
        String path = request.getPath();

        if (path == null || path.isBlank()) {
            return null;
        }

        String[] parts = path.split("/");

        // Work backwards so trailing slashes are handled correctly.
        for (int i = parts.length - 1; i >= 0; i--) {
            if (!parts[i].isBlank()) {
                return parts[i];
            }
        }

        return null;
    }

    static String toJson(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Could not serialize JSON response.", e
            );
        }
    }

    static APIGatewayProxyResponseEvent jsonResponse(
            int status, Object body) {

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(status)
                .withHeaders(Map.of(
                        "Content-Type", "application/json"
                ))
                .withBody(toJson(body));
    }

    static APIGatewayProxyResponseEvent errorResponse(
            int status, String message) {

        return jsonResponse(status, Map.of("error", message));
    }

    static APIGatewayProxyResponseEvent redirectResponse(
            String originalUrl) {

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(302)
                .withHeaders(Map.of("Location", originalUrl))
                .withBody("");
    }
}
