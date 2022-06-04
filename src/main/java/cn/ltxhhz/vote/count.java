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

@WebServlet(name = "count", value = "/api/count")
public class count extends HttpServlet {
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (Utils.requestCheck(request, response)) return;
    Map<String, String> user = Utils.getAccountAndSkey(request);
    JSONObject reqJson = JSONObject.parseObject(Utils.getRequestBodyText(request));
    JSONObject resJson = new JSONObject();
//    if (user.get("account") != null && user.get("skey") != null) {
//      JwtToken.verifyToken(user.get("skey"), user.get("account"));
//    }else{
    try {
      visitAdd(reqJson.getString("uuid"));
      Utils.returnSuccess(resJson, response);

    } catch (SQLException e) {
      e.printStackTrace();
      Utils.returnFail(resJson, response);

    }
//    }
  }

  void visitAdd(String uuid) throws SQLException {
    if (uuid.equals("")) return;
    Connection conn = DB.getConn();
    PreparedStatement ps = conn.prepareStatement("update num set visit=visit+1 where uuid=?");
    ps.setString(1, uuid);
    ps.executeUpdate();
    conn.close();
  }
}
