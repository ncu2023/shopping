package com;


import java.sql.Statement;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

// PO
class ProductEntity {
    int id;
    String name;
    String imageUrl; // Field (欄位)
    String description;
    String category;
    int price;
    String storeName;
}

class ReponseProductEntity {
    int code;
    String message;
    ArrayList<ProductEntity> data;

    public ReponseProductEntity(int code, String message, 
        ArrayList<ProductEntity> data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }
}

public class Product extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws IOException {
        // 取得所有商品
        ReponseProductEntity respEntity = getProductList();
        // 將ResponseProductEntity物件轉換成JSON字串
        String responseString = new Gson().toJson(respEntity);
        System.out.println(responseString);

        resp.setContentType("application/json;charset=UTF-8");

        // 回應給前端
        PrintWriter out = resp.getWriter();
        out.print(responseString);
        out.flush();

    }

    // 取得產品清單並回傳ResponseProductEntity物件
    private ReponseProductEntity getProductList() {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 連接資料庫
            conn = DriverManager.getConnection("jdbc:mysql://localhost/shopping?" + 
                "user=root&password=0000");

            // 取得Statement物件
            stmt = conn.createStatement();

            // 查詢全部商品
            rs = stmt.executeQuery("select * from product");

            // 建立陣列存放所有商品用
            ArrayList<ProductEntity> products = new ArrayList<>();

            // 將每一筆商品資料讀出來存放到ArrayList內
            while(rs.next()) {
                ProductEntity productEntity = new ProductEntity();
                productEntity.id = rs.getInt("id");
                productEntity.name = rs.getString("name");
                productEntity.description = rs.getString("description");
                productEntity.price = rs.getInt("price");
                productEntity.imageUrl = rs.getString("image_url");
                productEntity.storeName = rs.getString("store_name");
                productEntity.category = rs.getString("category");

                // 將取的商品資料存到ArrayList
                products.add(productEntity);
            }

            // 關資料庫相關資源
            rs.close();
            stmt.close();
            conn.close();

            return new ReponseProductEntity(0, "sucess", products);
        } catch(ClassNotFoundException e) {
            // 無法註冊
            return new ReponseProductEntity(1, "無法註冊", null);
        } catch(SQLException e) {
            return new ReponseProductEntity(e.getErrorCode(), 
                    e.getMessage(), null);
        }
        
    }
}
