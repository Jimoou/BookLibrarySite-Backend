package com.reactlibraryproject.springbootlibrary.CustomExceptions;

import org.json.JSONObject;
import org.springframework.http.ResponseEntity;

public class TossPayResponseException extends RuntimeException {

    public TossPayResponseException(ResponseEntity<String> response) {
        super(getException(response));
    }

    private static String getException(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        String code = jsonObject.getString("code");
        String message = jsonObject.getString("message");
        return response.getStatusCode() + " - " + code + ": " + message;
    }
}
