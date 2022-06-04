package cn.ltxhhz.vote;

import cn.ltxhhz.vote.database.DB;
import cn.ltxhhz.vote.utils.JwtToken;
import cn.ltxhhz.vote.utils.Utils;
import com.alibaba.fastjson.JSONArray;
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
          e.printStackTrace();
          Utils.returnFail(resJson,response);
          try {
            conn.close();
          } catch (SQLException ex) {
            ex.printStackTrace();
          }
          return;
        }
      }
    }
    try {
      PreparedStatement ps = conn.prepareStatement("select *,visit,part from list,num where list.uuid=? and list.uuid=num.uuid");
      ps.setString(1, reqJson.getString("uuid"));
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        JSONObject data = new JSONObject();
        data.put("account", rs.getString("account"));
        data.put("title", rs.getString("title"));
        data.put("start", rs.getTimestamp("start").getTime());
        data.put("end", rs.getTimestamp("end").getTime());
        data.put("single", rs.getBoolean("single"));
        data.put("description", rs.getString("description"));
        data.put("visit", rs.getInt("visit"));
        data.put("part", rs.getInt("part"));
        data.put("hideResult", rs.getBoolean("hideResult"));
        data.put("everyday", rs.getBoolean("everyday"));
        if (!data.getBooleanValue("single")) {
          data.put("min", rs.getInt("min"));
          data.put("max", rs.getInt("max"));
        }
        int on = rs.getInt("optionsNum");
        if (on > 0) {
          ps = conn.prepareStatement("select optionId,content,image from options where uuid=?");
          ps.setString(1, rs.getString("uuid"));
          rs = ps.executeQuery();
          JSONArray arr = new JSONArray();
          while (rs.next()) {
            JSONObject obj = new JSONObject();
            obj.put("optionId",rs.getString("optionId"));
            obj.put("content",rs.getString("content"));
            obj.put("image",rs.getString("image"));
            arr.add(obj);
          }
          data.put("options", arr);
        }
        ps = conn.prepareStatement("select * from history where uuid=? and account=?");
        ps.setString(1,reqJson.getString("uuid"));
        ps.setString(2,account);
        rs=ps.executeQuery();
        if (rs.next()){
          data.put("haveVoted",1);
          data.put("choice",rs.getString("choice"));
        }
        Utils.returnSuccess(resJson,response,data);
      } else {
        Utils.returnSuccess(resJson,response,"");
      }
    } catch (SQLException e) {
      Utils.returnFail(resJson,response);
      e.printStackTrace();
    }
    try {
      conn.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
}
