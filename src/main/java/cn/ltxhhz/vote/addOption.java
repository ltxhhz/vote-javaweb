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

@WebServlet(name = "addOption", value = "/api/addOption")
@MultipartConfig
public class addOption extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    if (Utils.requestCheck(request, response, "multipart/form-data")) return;
    JSONObject resJson = new JSONObject();
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
      resJson.clear();
      resJson.put("status", 0);
      response.getWriter().print(resJson.toJSONString());
      return;
    }
    System.out.println(System.getProperty("file.encoding"));
    String skey = "", account = "", title = "", uuid = "", n = "";
    FileItem fi = null;
    // 遍历 list容器，处理 每个数据项 中的信息
    for (FileItem item : items) {
      String name = item.getFieldName();
      // 判断是否是普通项
      if (item.isFormField()) { // 处理 普通数据项 信息
        switch (name) {
          case "skey":
            skey = item.getString("utf8");
            break;
          case "account":
            account = item.getString("utf8");
            break;
          case "title":
            title = item.getString("utf8");
            break;
          case "uuid":
            uuid = item.getString("utf8");
            break;
        }
      } else { // 处理 文件数据项 信息
        fi = item;
      }
    }

    if (!JwtToken.verifyToken(skey, account)) {
      resJson.clear();
      resJson.put("status", -1);
      response.getWriter().print(resJson.toJSONString());
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
        resJson.clear();
        resJson.put("status", 0);
        response.getWriter().print(resJson.toJSONString());
        return;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      resJson.clear();
      resJson.put("status", 0);
      response.getWriter().print(resJson.toJSONString());
      return;
    }
    if (fi != null) {
      try {
        n = handleFileField(fi);
        if (n.equals("")) {
          resJson.clear();
          resJson.put("status", 0);
          response.getWriter().print(resJson.toJSONString());
          return;
        }
      } catch (Exception e) {
        e.printStackTrace();
        resJson.clear();
        resJson.put("status", 0);
        response.getWriter().print(resJson.toJSONString());
        return;
      }
    }

    try {
      PreparedStatement ps = conn.prepareStatement("insert into options values (?,?,?,?);");
      ps.setString(1, uuid);
      ps.setString(2, uuid + "-" + (optNum + 1));
      ps.setString(3, title);
      ps.setString(4, n);
      if (ps.executeUpdate() == 0) {
        resJson.clear();
        resJson.put("status", 0);
        response.getWriter().print(resJson.toJSONString());
        return;
      }
      ps = conn.prepareStatement("update list set optionsNum=? where uuid=?");
      ps.setInt(1, optNum + 1);
      ps.setString(2, uuid);
      if (ps.executeUpdate() == 0) {
        resJson.clear();
        resJson.put("status", 0);
        response.getWriter().print(resJson.toJSONString());
        return;
      }
    } catch (SQLException e) {
      e.printStackTrace();
      resJson.clear();
      resJson.put("status", 0);
      response.getWriter().print(resJson.toJSONString());
      return;
    }
    resJson.put("status", 1);
    response.getWriter().print(resJson.toJSONString());
  }

  /**
   * 处理 文件数据项
   *
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

    // 将文件信息 输出到控制台
    System.out.println("fileName:" + fileName);         // 文件名
    System.out.println("fileSize:" + item.getSize());   // 文件大小

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
