package cn.ltxhhz.vote;

import cn.ltxhhz.vote.database.DB;
import cn.ltxhhz.vote.utils.JwtToken;
import cn.ltxhhz.vote.utils.Utils;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@WebServlet(name = "addOption", value = "/api/addOption")
@MultipartConfig
public class addOption extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (Utils.requestCheck(request, response, "multipart/form-data")) return;
    JSONObject reqJSON = new JSONObject();
    JSONObject data = new JSONObject();
    Map<String, String> user = Utils.getAccountAndSkey(request);
    // 创建上传所需要的两个对象
    DiskFileItemFactory factory = new DiskFileItemFactory();  // 磁盘文件对象
    ServletFileUpload upload = new ServletFileUpload(factory);   // 文件上传对象
    upload.setHeaderEncoding("UTF-8");
    upload.setSizeMax(1024 * 1024 * 10);
    // 创建 list容器用来保存 表单中的所有数据信息
    List<FileItem> items;
    System.out.println(ServletFileUpload.isMultipartContent(request));
    // 将表单中的所有数据信息放入 list容器中
    try {
      items = upload.parseRequest(new ServletRequestContext(request));
    } catch (FileUploadException e) {
      e.printStackTrace();
      Utils.returnFail(reqJSON, response);
      return;
    }
    String skey = user.get("skey"), account = user.get("account"), title = "", uuid = "", n = "";
    String type = "add", optionId = "";
    boolean noImage = false;
    FileItem fi = null;
    // 遍历 list容器，处理 每个数据项 中的信息
    for (FileItem item : items) {
      String name = item.getFieldName();
      // 判断是否是普通项
      if (item.isFormField()) { // 处理 普通数据项 信息
        switch (name) {
          case "title":
            title = item.getString("utf8");
            break;
          case "uuid":
            uuid = item.getString("utf8");
            break;
          case "type":
            type = item.getString("utf8");
            break;
          case "optionId":
            optionId = item.getString("utf8");
            break;
          case "noImage":
            noImage = true;
            break;
        }
      } else { // 处理 文件数据项 信息
        fi = item;
      }
    }

    if (!JwtToken.verifyToken(skey, account)) {
      Utils.returnFail(reqJSON, response, -1);
      return;
    }
    Connection conn = DB.getConn();
    int optNum;
    try {
      PreparedStatement ps = conn.prepareStatement("select optionsNum from list where uuid=?");
      ps.setString(1, uuid);
      ResultSet rs = ps.executeQuery();
      if (rs.next()) {
        optNum = rs.getInt(1);
      } else {
        Utils.returnFail(reqJSON, response);
        conn.close();
        return;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      Utils.returnFail(reqJSON, response);
      try {
        conn.close();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
      return;
    }
    if (fi != null) {
      try {
        n = handleFileField(fi);
        if (n.equals("")) {
          Utils.returnFail(reqJSON, response);
          conn.close();
          return;
        }
      } catch (Exception e) {
        e.printStackTrace();
        Utils.returnFail(reqJSON, response);
        try {
          conn.close();
        } catch (SQLException ex) {
          ex.printStackTrace();
        }
        return;
      }
    }

    try {
      PreparedStatement ps;
      if (type.equals("add")) {
        ps = conn.prepareStatement("insert into options (uuid, optionId, content, image) values (?,?,?,?);");
        ps.setString(1, uuid);
        ps.setString(2, uuid + "-" + (optNum + 1));
        data.put("optionId", uuid + "-" + (optNum + 1));
        data.put("content", title);
        data.put("image", n);
        ps.setString(3, title);
        ps.setString(4, n);
      } else {
        if (fi == null) {
          if (noImage) {
            ps = conn.prepareStatement("update options set content =?,image='' where optionId=?;");
          } else {
            ps = conn.prepareStatement("update options set content =? where optionId=?;");
          }
          ps.setString(1, title);
          ps.setString(2, optionId);
        } else {
          ps = conn.prepareStatement("update options set content =?,image=? where optionId=?;");
          ps.setString(1, title);
          ps.setString(2, n);
          ps.setString(3, optionId);
          reqJSON.put("data",n);
        }
      }
      if (ps.executeUpdate() == 0) {
        Utils.returnFail(reqJSON, response);
        conn.close();
        return;
      }
      if (type.equals("add")) {
        ps = conn.prepareStatement("update list set optionsNum=? where uuid=?");
        ps.setInt(1, optNum + 1);
        ps.setString(2, uuid);
        if (ps.executeUpdate() == 0) {
          Utils.returnFail(reqJSON, response);
          conn.close();
          return;
        }
      }
    } catch (SQLException e) {
      e.printStackTrace();
      Utils.returnFail(reqJSON, response);
      try {
        conn.close();
      } catch (SQLException ex) {
        ex.printStackTrace();
      }
      return;
    }
    if (type.equals("add")) Utils.returnSuccess(reqJSON, response, data);
    else Utils.returnSuccess(reqJSON, response);
  }

  /**
   * 处理 文件数据项
   */
  private String handleFileField(FileItem item) throws Exception {
    // 获取 文件数据项中的 文件名
    String fileName = item.getName();

    // 判断 此文件的文件名是否合法
    if (fileName == null || "".equals(fileName)) {
      return "";
    }

    // 控制只能上传图片
    if (!item.getContentType().startsWith("image")) {
      return "";
    }

    String path = this.getServletContext().getRealPath("/images");
    File file = new File(path);   // 创建 file对象

    // 创建 /files 目录
    if (!file.exists()) {
      file.mkdir();
    }
    String n = Utils.uuid() + "." + Utils.getFileExtension(fileName);
    // 将文件保存到服务器上（UUID是通用唯一标识码，不用担心会有重复的名字出现）
    item.write(new File(file.toString(), n));
    return "/images/" + n.replaceAll("\\\\", "/");
  }

}
