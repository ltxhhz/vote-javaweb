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
import java.sql.Timestamp;
import java.util.Map;

@WebServlet(name = "update", value = "/api/update")
public class update extends HttpServlet {

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
    JSONObject data = reqJson.getJSONObject("data");
    Connection conn = DB.getConn();
    String type = reqJson.getString("type");
    try {
      PreparedStatement ps;
      if (type.equals("edit")) {
        ps = conn.prepareStatement("update list set title = ?,start=?,end=?,single=?,description=?,min=?,max=? where uuid=?;");
        ps.setString(1, data.getString("title"));
        ps.setTimestamp(2, new Timestamp(data.getLongValue("start")));
        ps.setTimestamp(3, new Timestamp(data.getLongValue("end")));
        ps.setBoolean(4, data.getBooleanValue("single"));
        ps.setString(5, data.getString("description"));
        ps.setInt(6, data.getIntValue("min"));
        ps.setInt(7, data.getIntValue("max"));
        ps.setString(8, reqJson.getString("uuid"));
      }else{
        ps = conn.prepareStatement("update list set everyday = ?,hideResult=? where uuid=?;");
        ps.setBoolean(1,data.getBooleanValue("everyday"));
        ps.setBoolean(2,data.getBooleanValue("hideResult"));
        ps.setString(3, reqJson.getString("uuid"));
      }
      if (ps.executeUpdate() == 1) {
        Utils.returnSuccess(resJson, response);
        conn.close();
      } else {
        Utils.returnFail(resJson, response);
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
