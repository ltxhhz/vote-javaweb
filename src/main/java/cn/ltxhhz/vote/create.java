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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Map;


@WebServlet(name = "create", value = "/api/create")
public class create extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (Utils.requestCheck(request, response)) return;
    JSONObject reqJson = JSONObject.parseObject(Utils.getRequestBodyText(request));
    JSONObject resJson = new JSONObject();
    Map<String, String> user = Utils.getAccountAndSkey(request);
    if (!JwtToken.verifyToken(user.get("skey"), user.get("account"))) {
      resJson.clear();
      resJson.put("status", "-1");
      response.getWriter().print(resJson.toJSONString());
      return;
    }
    JSONObject data = reqJson.getJSONObject("data");
    Connection conn = DB.getConn();
    String sql = "insert into list(uuid,account,title,start,end,single,description,min,max) values (?,?,?,?,?,?,?,?,?)";
    try {
      PreparedStatement ps = conn.prepareStatement(sql);
      String uuid = Utils.uuid();
      ps.setString(1, uuid);
      ps.setString(2, user.get("account"));
      ps.setString(3, data.getString("title"));
      ps.setTimestamp(4, new Timestamp(data.getLongValue("start")));
      ps.setTimestamp(5, new Timestamp(data.getLongValue("end")));
      boolean single = data.getBooleanValue("single");
      ps.setInt(6, single ? 1 : 0);
      ps.setString(7, data.getString("description"));
      ps.setInt(8, single ? 0 : data.getIntValue("min"));
      ps.setInt(9, single ? 0 : data.getIntValue("max"));
      int rs = ps.executeUpdate();
      if (rs > 0) {
        resJson.put("data",uuid);
        resJson.put("status", 1);
      }
    } catch (SQLException e) {
      resJson.put("status", 0);
      e.printStackTrace();
    }
    response.getWriter().print(resJson.toJSONString());
  }
}
