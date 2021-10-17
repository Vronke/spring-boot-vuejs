package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class ProductClass {
    public int id;
    public String text;
    public int flag;

    public ProductClass(){
    }

    static public ArrayList<ProductClass> selectClassList(){
        ArrayList<ProductClass> productClassList = new ArrayList<ProductClass>();

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            Statement statement = connection.createStatement();
            String select = "SELECT [ID]\n" +
                    "      ,[Name]\n" +
                    "\t  ,POWER(2,ID)\n" +
                    "  FROM [WEDGE2].[dbo].[ProductClass]";
            ResultSet resultSet = statement.executeQuery(select);

            while (resultSet.next()){
                ProductClass productClass = new ProductClass();
                productClass.id = Integer.parseInt(resultSet.getString(1));
                productClass.text = resultSet.getString(2);
                productClass.flag = Integer.parseInt(resultSet.getString(3));
                productClassList.add(productClass);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("ProductClass");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        return productClassList;
    }
}
