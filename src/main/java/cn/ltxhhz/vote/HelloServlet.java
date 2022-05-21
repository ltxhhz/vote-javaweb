package cn.ltxhhz.vote;

import cn.ltxhhz.vote.utils.JwtToken;
import cn.ltxhhz.vote.utils.Utils;
import com.alibaba.fastjson.JSONObject;

import java.io.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;

@WebServlet(name = "helloServlet", value = "/api/hello-servlet")
public class HelloServlet extends HttpServlet {
  private String message;

  public void init() {
    message = "Hello World!";
  }

  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if(!Utils.requestCheck(request,response)) return;
    JSONObject reqJson = JSONObject.parseObject(Utils.getRequestBodyText(request));
    System.out.println(JwtToken.verifyToken(reqJson.getString("skey"),reqJson.getString("account")));
  }

  public void destroy() {
  }
}