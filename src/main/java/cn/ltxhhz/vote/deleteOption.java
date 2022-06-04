package cn.ltxhhz.vote;

import cn.ltxhhz.vote.database.DB;
import cn.ltxhhz.vote.utils.JwtToken;
import cn.ltxhhz.vote.utils.Utils;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

@WebServlet(name = "deleteOption", value = "/api/deleteOption")
public class deleteOption extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    if (Utils.requestCheck(request, response)) return;
    Map<String, String> user = Utils.getAccountAndSkey(request);
    JSONObject reqJson = JSONObject.parseObject(Utils.getRequestBodyText(request));
    JSONObject resJson = new JSONObject();
    String account = user.get("account");
    if (!JwtToken.verifyToken(user.get("skey"), account)) {
      Utils.returnFail(resJson, response, -1);
      return;
    }
    Connection conn = DB.getConn();
    try {
      PreparedStatement ps = conn.prepareStatement("delete from options where optionId=?;");
      ps.setString(1, reqJson.getString("optionId"));
      if (ps.executeUpdate() == 1) {
        Utils.returnSuccess(resJson, response);
      } else {
        Utils.returnFail(resJson, response);
      }
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
      Utils.returnFail(resJson, response);
      try {
        conn.close();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
    }
  }
}
