package cn.ltxhhz.vote;

import cn.ltxhhz.vote.database.DB;
import cn.ltxhhz.vote.utils.JwtToken;
import cn.ltxhhz.vote.utils.Utils;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(name = "login", value = "/api/login")
public class login extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.getWriter().println("hello");
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (Utils.requestCheck(request, response)) return;
    JSONObject reqJson = JSONObject.parseObject(Utils.getRequestBodyText(request));
    JSONObject resJson = new JSONObject();
    Connection conn = DB.getConn();
    String sql = "select permission from users where account=? and password=?";
    try {
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setString(1, reqJson.getString("account"));
      ps.setString(2, reqJson.getString("password"));
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        JSONObject data = new JSONObject();
        data.put("skey", JwtToken.createToken(reqJson.getString("account")));
        data.put("permission", rs.getInt("permission"));
        Utils.returnSuccess(resJson, response, data);
      } else {
        Utils.returnFail(resJson, response);
      }
    } catch (SQLException e) {
      Utils.returnFail(resJson, response);
      e.printStackTrace();
    }
    try {
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
