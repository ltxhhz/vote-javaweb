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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "vote", value = "/api/vote")
public class vote extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (Utils.requestCheck(request, response)) return;
    JSONObject reqJson = JSONObject.parseObject(Utils.getRequestBodyText(request));
    JSONObject resJson = new JSONObject();
    Map<String, String> user = Utils.getAccountAndSkey(request);
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
        if (!(System.currentTimeMillis() > rs.getTimestamp("start").getTime() && System.currentTimeMillis() < rs.getTimestamp("end").getTime())) {
          Utils.returnFail(resJson, response, "投票已过期");
          return;
        }
        boolean single = rs.getInt("single") == 1;
        JSONArray checked = reqJson.getJSONArray("checked");
        int max = rs.getInt("max"),
          min = rs.getInt("min"),
          optNum = rs.getInt("optionsNum");
        if (single && checked.size() > 1 ||
          (!single && checked.size() > max && checked.size() < min) ||
          optNum == 0) {
          Utils.returnFail(resJson, response);
          return;
        }
        ps = conn.prepareStatement("insert into history (account, uuid, choise) values (?,?,?);");
        ps.setString(1, account);
        ps.setString(2, reqJson.getString("uuid"));
        Pattern p = Pattern.compile("-(\\d+)$");
        String opts = "";
        for (int i = 0; i < checked.size(); i++) {
          String str = checked.getString(i);
          Matcher m = p.matcher(str);
          if (m.find()) {
            if (opts.equals("")) opts = opts.concat(",").concat(m.group(0));
            else opts = opts.concat(m.group(0));
          } else {
            Utils.returnFail(resJson, response);
            return;
          }
        }
        ps.setString(3, opts);

        if (ps.executeUpdate()==0){
          Utils.returnFail(resJson, response);
        }else{
          Utils.returnSuccess(resJson,response);
        }
      } else {
        Utils.returnFail(resJson, response);
      }
    } catch (SQLException e) {
      e.printStackTrace();
      Utils.returnFail(resJson, response);
    }

  }
}
