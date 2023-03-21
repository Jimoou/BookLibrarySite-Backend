package com.reactlibraryproject.springbootlibrary.CustomExceptions;

import org.json.JSONObject;import java.net.http.HttpResponse;

public class PaymentResponseException extends RuntimeException {

  public PaymentResponseException(HttpResponse<String> jsonResponse) {
    super(getMessageFromJson(jsonResponse));
  }
  private static String getMessageFromJson(HttpResponse<String>  jsonResponse) {
    JSONObject jsonObject = new JSONObject(jsonResponse);
    return jsonResponse.statusCode() + " - " + jsonObject.getString("message");
  }
}
