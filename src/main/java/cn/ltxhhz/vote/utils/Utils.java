package cn.ltxhhz.vote.utils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
      return true;
    }
    return false;
  }

  public static boolean requestCheck(HttpServletRequest request, HttpServletResponse response, String ct) throws UnsupportedEncodingException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json");
    if (!request.getContentType().startsWith(ct)) {
      response.setStatus(400);
      return true;
    }
    return false;
  }

  public static String uuid() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  public static String getFileExtension(String fileName) {
    if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
      return fileName.substring(fileName.lastIndexOf(".") + 1);
    else return "";
  }

  public static Map<String, Cookie> getCookieMap(HttpServletRequest request) {
    Cookie[] cookies = request.getCookies();
    Map<String, Cookie> map = new HashMap<>();
    for (Cookie cookie : cookies) {
      map.put(cookie.getName(), cookie);
    }
    return map;
  }

  public static Map<String, String> getAccountAndSkey(HttpServletRequest request) {
    Map<String, String> map = new HashMap<>();
    Map<String, Cookie> cm = getCookieMap(request);
    map.put("account", cm.get("account") == null ? null : cm.get("account").getValue());
    map.put("skey", cm.get("skey") == null ? null : cm.get("skey").getValue());
    return map;
  }
}
