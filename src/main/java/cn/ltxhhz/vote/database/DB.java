package cn.ltxhhz.vote.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class DB {

  public static Connection getConn() {
    Connection conn = null;
    try {
      Class.forName("com.mysql.cj.jdbc.Driver");
      conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/vote?user=root&useUnicode=true&characterEncoding=UTF8");
    } catch (ClassNotFoundException | SQLException e) {
      e.printStackTrace();
    }
    return conn;
  }

  public static Statement getStatement(Connection conn) {
    Statement stmt = null;
    if (conn != null) {
      try {
        stmt = conn.createStatement();
      } catch (SQLException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    return stmt;
  }

  public static ResultSet getResultSet(Statement stmt, String sql) {
    ResultSet rs = null;
    if (stmt != null && !Objects.equals(sql, "")) {
      try {
        rs = stmt.executeQuery(sql);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return rs;
  }

  public static void closeStmt(Statement stmt) {
    if (stmt != null) {
      try {
        stmt.close();
        stmt = null;
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static void closeConn(Connection conn) {
    if (conn != null) {
      try {
        conn.close();
        conn = null;
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }

  public static void closeRs(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
        rs = null;
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
  }
}