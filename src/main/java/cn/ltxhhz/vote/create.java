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
      Utils.returnFail(resJson,response,-1);
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
      ps.setBoolean(6, single);
      ps.setString(7, data.getString("description"));
      ps.setInt(8, single ? 0 : data.getIntValue("min"));
      ps.setInt(9, single ? 0 : data.getIntValue("max"));
      int rs = ps.executeUpdate();
      if (rs > 0) {
        Utils.returnSuccess(resJson,response,uuid);
        ps=conn.prepareStatement("insert into num (uuid) values (?);");
        ps.setString(1,uuid);
        ps.executeUpdate();
      }else{
        Utils.returnFail(resJson,response);
      }
    } catch (SQLException e) {
      Utils.returnFail(resJson,response);
      e.printStackTrace();
    }
  }
}
