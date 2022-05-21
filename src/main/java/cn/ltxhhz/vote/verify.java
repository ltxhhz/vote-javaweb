package cn.ltxhhz.vote;

import cn.ltxhhz.vote.utils.JwtToken;
import cn.ltxhhz.vote.utils.Utils;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@WebServlet(name = "verify", value = "/api/verify")
public class verify extends HttpServlet {
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if(!Utils.requestCheck(request,response)) return;
    JSONObject reqJson = JSONObject.parseObject(Utils.getRequestBodyText(request));
    JSONObject resJson = new JSONObject();
    resJson.put("data",JwtToken.verifyToken(reqJson.getString("skey"),reqJson.getString("account")));
    resJson.put("status",1);
    response.getWriter().print(resJson.toJSONString());
  }
}
