package cn.ltxhhz.vote;

import cn.ltxhhz.vote.database.DB;
import cn.ltxhhz.vote.utils.JwtToken;
import cn.ltxhhz.vote.utils.Utils;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

@WebServlet(name = "delete", value = "/api/delete")
public class delete extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
      PreparedStatement ps = conn.prepareStatement("select * from list where uuid=?");
      ps.setString(1, reqJson.getString("uuid"));
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        if (rs.getString("account").equals(account)) {
          ps = conn.prepareStatement("delete from list where uuid=?;");
          ps.setString(1, reqJson.getString("uuid"));
          if (ps.executeUpdate() > 0) {
            Utils.returnSuccess(resJson, response);
          } else {
            Utils.returnFail(resJson, response);
            conn.close();
          }
        } else {
          Utils.returnFail(resJson, response);
          conn.close();
        }
      } else {
        Utils.returnFail(resJson, response, "投票不存在");
        conn.close();
      }
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
