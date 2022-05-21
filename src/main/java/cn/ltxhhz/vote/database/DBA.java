package cn.ltxhhz.vote.database;

import java.sql.*;

public class DBA {
  private final String drv = "com.mysql.jdbc.Driver";//数据库驱动类
  private final String url = "jdbc:mysql://localhost:3306/web?characterEncoding=utf8";//数据库地址
  private final String usr = "root";
  private final String pwd = "123456";

  private Connection conn = null;
  private Statement stm = null;
  private ResultSet rs = null;

  /**
   * 用于链接Mysql数据库
   * 构造方法
   * 无
   */
  public void DBA() {
    try {
      Class.forName(drv);             //加载数据库驱动程序
      conn = DriverManager.getConnection(url, usr, pwd);    //链接数据库
      stm = conn.createStatement();
    } catch (Exception e) {
      System.out.println(e.toString());
    }
  }

  /**
   * 用于更新数据库中的数据
   * String类型
   * boolean类型
   */
  public boolean update(String sql) {
    boolean b = false;
    try {
      stm.executeUpdate(sql);
      b = true;
    } catch (Exception e) {
      System.out.println(e.toString());
    }
    return b;
  }

  /**
   * 用于查询数据库中的数据
   * String类型
   * ResultSet类型
   */
  public ResultSet query(String sql) {
    try {
      rs = stm.executeQuery(sql);
    } catch (Exception e) {
      System.out.println(e.toString());
    }
    return rs;
  }

  public void close() {
    try {
      if (rs != null) {
        conn.close();
      }
      if (stm != null) {
        conn.close();
      }
      if (conn != null) {
        conn.close();
      }
    } catch (Exception ex) {
      System.out.println(ex);
    }
  }
}
