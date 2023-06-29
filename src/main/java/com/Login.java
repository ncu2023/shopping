package com;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

class ActionResult {
    private int code = -1; // 動作結過的代碼
    private String message = ""; // 動作描述

    public ActionResult(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public String getMessage() {
        return this.message;
    }
}

public class Login extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
        throws IOException {
        // 接收使用者帳號，參數名稱為username
        String username = req.getParameter("username");

        // 接收使用者密碼，參數名稱為password
        String password = req.getParameter("password");

        // 設定輸出的資料格式和編碼
        // resp.setContentType("text/html;charset=UTF-8");

        // // 輸出到網頁上
        // PrintWriter out = resp.getWriter();
        // out.print("收到帳號: " + username);
        ActionResult actionResult = checkAccount(username, password);
        // 將網址列的參數值編碼，避免中文字出現亂碼
        String url = URLEncoder.encode(actionResult.getMessage(), 
                                       StandardCharsets.UTF_8.toString());
        
        // 導回前端並將結果透過參數給前端
        if(actionResult.getCode() == 0)  { // 成功, 轉導商品清單頁
            resp.sendRedirect("/login/card.html");
        } else { // 失敗:轉導失敗結果頁
            resp.sendRedirect("/login/loginResult.html?message=" + url);
        }

        
        // out.print("<div> Code = " + actionResult.getCode() + "</div>");
        // out.print("<div> Message = " + actionResult.getMessage() + "</div>");
        // out.print("<br/>");
        // out.print("收到密碼: " + password);

        // 立即輸出
        // out.flush();
    }

    // 判斷帳號是否存在
    private ActionResult checkAccount(String username, String password) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        // 註冊mySQL資料庫驅動程式
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 連線資料庫
            conn = DriverManager.getConnection("jdbc:mysql://localhost/shopping?" +
                                               "user=root&password=0000");

            // 取得Statement物件
            stmt = conn.createStatement();

            // 查詢該帳號是否存在
            rs = stmt.executeQuery("select count(*) as c from account where name='" + username + "'");

            // 判斷帳號是否存在
            rs.next(); // 跳到查詢結果的第一筆資料
            int c = rs.getInt("c"); // 查詢到的資料筆數

            // if(c == 0) {
            //     return new ActionResult(2, "帳號不存在");
            // } else {
            //     return new ActionResult(0, "成功");
            // }

            // 帳號不存在，直接返回錯誤
            if(c == 0) {
                // 把資料庫相關資源釋放
                rs.close();
                stmt.close();
                conn.close();

                return new ActionResult(2, "帳號不存在");
            }

            // 帳號存在，繼續判斷密碼
            rs = stmt.executeQuery("select count(*) as c from account " +
                "where name='" + username + "' and password='" + password + "';");

            // 移動到第一筆資料
            rs.next();
            c = rs.getInt("c"); // 查詢到的資料筆數

            // 把資料庫相關資源釋放
            rs.close();
            stmt.close();
            conn.close();

            // 密碼錯誤
            if(c == 0) {
                return new ActionResult(3, "密碼錯誤");
            }

            return new ActionResult(0, "登入成功");
            // return c == 0 ? new ActionResult(2, "帳號不存在") : new ActionResult(0, "登入成功");

        } catch (ClassNotFoundException e) {
            // 無法註冊(錯誤代碼1)
            return new ActionResult(1, "無法註冊驅動程式");
        } catch (SQLException e) {
            // SQL操作錯誤(代碼2)
            return new ActionResult(e.getErrorCode(), e.getMessage());
        }
    }
}
