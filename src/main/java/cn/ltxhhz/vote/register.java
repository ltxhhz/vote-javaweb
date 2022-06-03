package cn.ltxhhz.vote;

import cn.ltxhhz.vote.database.DB;
import cn.ltxhhz.vote.utils.Utils;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "register", value = "/api/register")
public class register extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (Utils.requestCheck(request, response)) return;
    JSONObject reqJson = JSONObject.parseObject(Utils.getRequestBodyText(request));
    JSONObject resJson = new JSONObject();
    if (reqJson.getString("account").equals("") || reqJson.getString("password").equals("")) {
      Utils.returnFail(resJson, response);
      return;
    }
    Connection conn = DB.getConn();
    PreparedStatement ps = null;
    try {
      ps = conn.prepareStatement("select * from users where account=?;");
      ps.setString(1, reqJson.getString("account"));
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        Utils.returnFail(resJson, response, "用户名已注册");
        conn.close();
        return;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      Utils.returnFail(resJson, response);
      try {
        conn.close();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
      return;
    }

    try {
      ps = conn.prepareStatement("insert into users (account, password, permission) values (?,?,?);");
      ps.setString(1, reqJson.getString("account"));
      ps.setString(2, reqJson.getString("password"));
      ps.setInt(3, 1);
      if (ps.executeUpdate() == 1) {
        Utils.returnSuccess(resJson, response);
      } else {
        Utils.returnFail(resJson, response);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      Utils.returnFail(resJson, response);
    }
    try {
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
