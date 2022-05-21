package cn.ltxhhz.vote.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class Utils {
  public static String getRequestBodyText(HttpServletRequest request) throws IOException {
    BufferedReader streamReader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
    StringBuilder responseStrBuilder = new StringBuilder();
    String inputStr;
    while ((inputStr = streamReader.readLine()) != null) responseStrBuilder.append(inputStr);
    return responseStrBuilder.toString();
  }
  public static boolean requestCheck(HttpServletRequest request, HttpServletResponse response) throws UnsupportedEncodingException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json");
    if (!request.getContentType().equals("application/json")) {
      response.setStatus(400);
      return false;
    }
    return true;
  }
}
