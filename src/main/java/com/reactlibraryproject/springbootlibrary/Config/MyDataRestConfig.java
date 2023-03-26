package com.reactlibraryproject.springbootlibrary.Config;

import com.reactlibraryproject.springbootlibrary.Entity.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

@Configuration
public class MyDataRestConfig implements RepositoryRestConfigurer {
    private String theAllowedOrigins = "https://localhost:3000";

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config, CorsRegistry cors) {
        HttpMethod[] theUnsupportedActions = {
         HttpMethod.POST,
         HttpMethod.DELETE,
         HttpMethod.PUT,
         HttpMethod.PATCH};

        config.exposeIdsFor(Book.class);
        config.exposeIdsFor(Review.class);
        config.exposeIdsFor(Message.class);
        config.exposeIdsFor(CartItem.class);
        config.exposeIdsFor(Checkout.class);
        config.exposeIdsFor(CheckoutHistory.class);
        config.exposeIdsFor(Coin.class);
        config.exposeIdsFor(CoinChargingHistory.class);
        config.exposeIdsFor(CoinUsingHistory.class);
        config.exposeIdsFor(PaymentHistory.class);

        disableHttpMethods(Book.class, config, theUnsupportedActions);
        disableHttpMethods(Review.class, config, theUnsupportedActions);
        disableHttpMethods(Message.class, config, theUnsupportedActions);
        disableHttpMethods(CartItem.class, config, theUnsupportedActions);
        disableHttpMethods(Checkout.class, config, theUnsupportedActions);
        disableHttpMethods(CheckoutHistory.class, config, theUnsupportedActions);
        disableHttpMethods(Coin.class, config, theUnsupportedActions);
        disableHttpMethods(CoinChargingHistory.class, config, theUnsupportedActions);
        disableHttpMethods(CoinUsingHistory.class, config, theUnsupportedActions);
        disableHttpMethods(PaymentHistory.class, config, theUnsupportedActions);

        /*
        Configure CORS Mapping
        */
        cors.addMapping(config.getBasePath() + "/**")
         .allowedOrigins(theAllowedOrigins);
    }

    private void disableHttpMethods(Class theClass, RepositoryRestConfiguration config, HttpMethod[] theUnsupportedActions) {
        config.getExposureConfiguration()
         .forDomainType(theClass)
         .withItemExposure((metadata, httpMethods) -> httpMethods.disable(theUnsupportedActions))
         .withCollectionExposure((metadata, httpMethods) -> httpMethods.disable(theUnsupportedActions));
    }

}
