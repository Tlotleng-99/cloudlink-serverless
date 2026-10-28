
package com.cloudlink.functions;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.cloudlink.exception.UrlNotFoundException;
import com.cloudlink.service.UrlService;

public class RedirectUrlLambda implements
        RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    @Override
    public APIGatewayProxyResponseEvent handleRequest(
            APIGatewayProxyRequestEvent request, Context context) {

        String shortCode = LambdaSupport.getShortCode(request);

        if (shortCode == null || shortCode.isBlank()) {
            return LambdaSupport.errorResponse(
                    400, "Short code is required."
            );
        }

        try {
            UrlService service = LambdaSupport.createUrlService();
            String originalUrl = service.redirect(shortCode);

            return LambdaSupport.redirectResponse(originalUrl);

        } catch (UrlNotFoundException e) {
            return LambdaSupport.errorResponse(404, e.getMessage());

        } catch (Exception e) {
            if (context != null) {
                context.getLogger().log(
                        "Redirect failed: " + e.getMessage()
                );
            }

            return LambdaSupport.errorResponse(
                    500, "Unable to redirect."
            );
        }
    }
}
