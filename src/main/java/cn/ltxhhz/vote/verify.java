package cn.ltxhhz.vote;

import cn.ltxhhz.vote.utils.JwtToken;
import cn.ltxhhz.vote.utils.Utils;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "verify", value = "/api/verify")
public class verify extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json");
    JSONObject resJson = new JSONObject();
    Map<String, String> user = Utils.getAccountAndSkey(request);
    Utils.returnSuccess(resJson,response,JwtToken.verifyToken(user.get("skey"),user.get("account")));
  }
}
