package cn.ltxhhz.vote;

import cn.ltxhhz.vote.database.DB;
import cn.ltxhhz.vote.utils.JwtToken;
import cn.ltxhhz.vote.utils.Utils;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.*;
import java.util.Map;

@WebServlet(name = "content", value = "/api/content")
public class content extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (Utils.requestCheck(request, response)) return;
    Map<String, String> user = Utils.getAccountAndSkey(request);
    JSONObject reqJson = JSONObject.parseObject(Utils.getRequestBodyText(request));
    JSONObject resJson = new JSONObject();
    Connection conn = DB.getConn();
    // 0 管理员，1 普通用户，2 游客
    int level = 2;
    String account = user.get("account"), skey = user.get("skey");
    if (account != null && skey != null) {
      level = JwtToken.verifyToken(skey, account) ? 1 : 2;
      if (level == 1) {
        String sql = "select permission from users where account=?";
        try {
          PreparedStatement ps = conn.prepareStatement(sql);
          ps.setString(1, account);
          ResultSet rs = ps.executeQuery();
          if (rs.next()) level = rs.getInt(1);
        } catch (SQLException e) {
          resJson.put("status", 0);
          response.getWriter().print(resJson.toJSONString());
          e.printStackTrace();
          return;
        }
      }
    }
    String sql = "select *,visit,part from list,num where list.uuid=? and list.uuid=num.uuid";
    try {
      PreparedStatement ps = conn.prepareStatement(sql);
      ps.setString(1, reqJson.getString("data"));
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        JSONObject data = new JSONObject();
        data.put("account", rs.getString("account"));
        data.put("title", rs.getString("title"));
        data.put("start", rs.getTimestamp("start").getTime());
        data.put("end", rs.getTimestamp("end").getTime());
        data.put("single", rs.getInt("single") == 1);
        data.put("description", rs.getString("description"));
        data.put("visit", rs.getInt("visit"));
        data.put("part", rs.getInt("part"));
        if (!data.getBooleanValue("single")) {
          data.put("min", rs.getInt("min"));
          data.put("max", rs.getInt("max"));
        }
        int on = rs.getInt("optionsNum");
        if (on > 0) {
          sql = "select optionId,content from options where uuid=?";
          ps = conn.prepareStatement(sql);
          ps.setString(1, rs.getString("uuid"));
          rs = ps.executeQuery();
          JSONObject opts = new JSONObject();
          while (rs.next()) {
            opts.put(rs.getString("optionId"), rs.getString("content"));
          }
          data.put("options", opts);
        }
        resJson.put("data", data);
        resJson.put("status", 1);
      } else {
        resJson.put("data", "");
        resJson.put("status", 1);
      }
    } catch (SQLException e) {
      resJson.clear();
      resJson.put("status", 0);
      e.printStackTrace();
    }
    response.getWriter().print(resJson.toJSONString());
  }
}
