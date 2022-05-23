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

@WebServlet(name = "content", value = "/api/content")
public class content extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (Utils.requestCheck(request, response)) return;
    JSONObject reqJson = JSONObject.parseObject(Utils.getRequestBodyText(request));
    JSONObject resJson = new JSONObject();
    Connection conn = DB.getConn();
    // 0 管理员，1 普通用户，2 游客
    int level = 2;
    if (reqJson.containsKey("account") && reqJson.containsKey("skey")) {
      level = JwtToken.verifyToken(reqJson.getString("skey"), reqJson.getString("account")) ? 1 : 2;
      if (level == 1) {
        String sql = "select permission from users where account=" + reqJson.getString("account");
        try {
          Statement stm = conn.createStatement();
          ResultSet rs = stm.executeQuery(sql);
          if (rs.next()) level = rs.getInt(0);
        } catch (SQLException e) {
          resJson.put("status", 0);
          response.getWriter().print(resJson.toJSONString());
          e.printStackTrace();
          return;
        }
      }
    }
    String sql = "select * from list where uuid=?";
    try {
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setString(1, reqJson.getString("data"));
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        resJson.put("account", rs.getString("account"));
        resJson.put("title", rs.getString("title"));
        resJson.put("start", rs.getTimestamp("start").getTime());
        resJson.put("end", rs.getTimestamp("end").getTime());
        resJson.put("single", rs.getInt("single") == 1);
        resJson.put("description", rs.getString("description"));
        if (!resJson.getBooleanValue("single")) {
          resJson.put("min", rs.getInt("min"));
          resJson.put("max", rs.getInt("max"));
        }
        int on = rs.getInt("optionsNum");
        if (on > 0) {
          sql = "select optionId,content from options where uuid=" + rs.getString("uuid");
          ps = conn.prepareStatement(sql);
          rs = ps.executeQuery();
          JSONObject opts = new JSONObject();
          while (rs.next()) {
            opts.put(rs.getString("optionId"), rs.getString("content"));
          }
          resJson.put("options", opts);
        }
      }
    } catch (SQLException e) {
      resJson.clear();
      resJson.put("status", 0);
      e.printStackTrace();
    }
    response.getWriter().print(resJson.toJSONString());
  }
}
