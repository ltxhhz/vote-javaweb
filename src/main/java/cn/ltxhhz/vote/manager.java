package cn.ltxhhz.vote;

import cn.ltxhhz.vote.database.DB;
import cn.ltxhhz.vote.utils.JwtToken;
import cn.ltxhhz.vote.utils.Utils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@WebServlet(name = "manager", value = "/api/manager")
public class manager extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
    response.setContentType("application/json");
    Map<String, String> user = Utils.getAccountAndSkey(request);
    JSONObject resJson = new JSONObject();
    String account = user.get("account");
    if (!JwtToken.verifyToken(user.get("skey"), account)) {
      Utils.returnFail(resJson, response, -1);
      return;
    }
    Connection conn = DB.getConn();
    try {
      PreparedStatement ps = conn.prepareStatement("select *,visit,part from list,num where account=?;");
      ps.setString(1, account);
      ResultSet rs = ps.executeQuery();
      JSONArray data = new JSONArray();
      while (rs.next()) {
        JSONObject obj = new JSONObject();
        obj.put("title", rs.getString("title"));
        obj.put("uuid",rs.getString("uuid"));
        obj.put("start", rs.getTimestamp("start").getTime());
        obj.put("end", rs.getTimestamp("end").getTime());
        obj.put("single", rs.getBoolean("single"));
        obj.put("description", rs.getString("description"));
        obj.put("visit", rs.getInt("visit"));
        obj.put("part", rs.getInt("part"));
        obj.put("hideResult", rs.getBoolean("hideResult"));
        obj.put("everyday", rs.getBoolean("everyday"));
        if (!obj.getBooleanValue("single")) {
          obj.put("min", rs.getInt("min"));
          obj.put("max", rs.getInt("max"));
        }
        data.add(obj);
      }
      Utils.returnSuccess(resJson,response,data);
    } catch (SQLException e) {
      e.printStackTrace();
      Utils.returnFail(resJson, response);
    }

  }
}
