
package com.cloudlink.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cloudlink.dto.CreateShortUrlRequest;
import com.cloudlink.dto.CreateShortUrlResponse;
import com.cloudlink.exception.InvalidUrlException;
import com.cloudlink.service.UrlService;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CreateUrlLambda implements
        RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request, Context context) {

        try {
            if (request.getBody() == null || request.getBody().isBlank()) {
                return LambdaSupport.errorResponse(400, "Request body is required.");
            }

            CreateShortUrlRequest input =
                    MAPPER.readValue(request.getBody(), CreateShortUrlRequest.class);

            if (input.getOriginalUrl() == null || input.getOriginalUrl().isBlank()) {
                return LambdaSupport.errorResponse(400, "originalUrl is required.");
            }

            UrlService service = LambdaSupport.createUrlService();
            String code = service.shortenUrl(input.getOriginalUrl());

            return LambdaSupport.jsonResponse(
                    201, new CreateShortUrlResponse(code));

        } catch (InvalidUrlException e) {
            return LambdaSupport.errorResponse(400, e.getMessage());
        } catch (Exception e) {
            if (context != null) {
                context.getLogger().log("Create URL failed: " + e.getMessage());
            }
            return LambdaSupport.errorResponse(500, "Unable to create short URL.");
        }
    }
}
