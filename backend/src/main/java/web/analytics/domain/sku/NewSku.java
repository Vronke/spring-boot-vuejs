package web.analytics.domain.sku;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NewSku {
    @JsonProperty("Общий ID")
    public int GlobalID;
    @JsonProperty("Сырая номенклатура")
    public String Name;
    @JsonProperty("Том")
    public String Tom;
    @JsonProperty("Работник")
    public String Assignee;
    public int productId;
    public String goodSku;

    public String fileName;
    public String author;


    public NewSku() {
        Assignee = "";
        productId = 0;
    }

    static public String loadSku(NewSku[] skuList){
        for (NewSku s :
                skuList) {
            try {
                boolean isCreated = false;
                Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
                Statement statement = connection.createStatement();
                String select = "SELECT * FROM [WEDGE2].[dbo].[RawProduct] " +
                        "WHERE [Name] = '" + s.Name + "' AND [GlobalID] = " + s.GlobalID;
                ResultSet resultSet = statement.executeQuery(select);
                while (resultSet.next()){
                    isCreated = true;
                    break;
                }
                if (isCreated)
                    continue;
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                return "1: " + e.getMessage();
            }

            try {
                Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
                Statement statement = connection.createStatement();
                String select = "SELECT r.ProductID, p.Name FROM [WEDGE2].[dbo].[RawProduct] r " +
                        "inner join Product as p on r.ProductID = p.ID " +
                        "WHERE r.Name = '" + s.Name + "'";
                ResultSet resultSet = statement.executeQuery(select);
                while (resultSet.next()){
                    s.productId = Integer.parseInt(resultSet.getString(1));
                    s.goodSku = resultSet.getString(2);
                    break;
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                return "2: " + e.getMessage();
            }

            try {
                Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(true);
                Statement statement = connection.createStatement();
                String insert = "INSERT INTO [WEDGE2].[dbo].[RawProduct] ([ID], [Name], [ProductID], [ProductClassID], [GlobalID], [Assignee])" +
                        "VALUES ((SELECT MAX(ID)+1 from [WEDGE2].[dbo].[RawProduct]), '" + s.Name + "', " + s.productId +
                        ", (SELECT TOP 1 ID FROM [WEDGE2].[dbo].[ProductClass] WHERE Name = '" + s.Tom + "'), " + s.GlobalID + ", '" + s.Assignee + "')";
                statement.executeUpdate(insert);
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                return "3: " + e.getMessage();
            }
        }
        return "Успешно загружено";
    }

    public static List<RawProduct> getLoadedSku(NewSku[] skuList){
        List<RawProduct> products = new ArrayList<>();
        for (NewSku s:
                skuList) {
            RawProduct r = new RawProduct();
            try {
                boolean isCreated = false;
                Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
                Statement statement = connection.createStatement();
                String select = "SELECT r.ID, r.Name, p.Name, r.ProductClassID, r.Assignee FROM [WEDGE2].[dbo].[RawProduct] r " +
                        "INNER JOIN [WEDGE2].[dbo].[Product] as p on r.ProductID = p.ID " +
                        "WHERE r.Name = '" + s.Name + "' AND [GlobalID] = " + s.GlobalID + " " +
                        "ORDER BY ID desc";
                ResultSet resultSet = statement.executeQuery(select);
                while (resultSet.next()){
                    r.id = Integer.parseInt(resultSet.getString(1));
                    r.sku = resultSet.getString(2);
                    r.goodSku = resultSet.getString(3);
                    r.classId = Integer.parseInt(resultSet.getString(4));
                    r.assignee = resultSet.getString(5);
                    products.add(r);
                    isCreated = true;
                    break;
                }
                if (isCreated)
                    continue;
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                String exc = e.getMessage();
            }
        }
        return products;
    }
    public static synchronized String addRawSku(NewSku[] products){
        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);

            for (NewSku s :
                    products) {
                try {
                    connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    connection.setAutoCommit(false);
                    String insert = "INSERT INTO [WEDGE2].[dbo].[TempRawProduct] ([Name], [ProductID], [ProductClassID], [GlobalID], [Assignee])" +
                            "VALUES (?, ?, (SELECT TOP 1 ID FROM [WEDGE2].[dbo].[ProductClass] WHERE Name = ?), ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(insert);
                    statement.setString(1, s.Name);
                    statement.setInt(2, s.productId);
                    statement.setString(3, s.Tom);
                    statement.setInt(4, s.GlobalID);
                    statement.setString(5, s.Assignee);

                    statement.executeUpdate();
                }
                // Handle any errors that may have occurred.
                catch (SQLException e) {
                    connection.rollback();
                    ProductController.LOG.info("NewSkuAddRawSku1");
                    ProductController.LOG.info(e.getMessage());
                    return "1: " + e.getMessage();
                }

            }
            try {
                Statement statement = connection.createStatement();
                String exec = "EXEC [WEDGE2].[dbo].[sp_add_new_sku]";
                statement.execute(exec);
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                connection.rollback();
                ProductController.LOG.info("NewSkuAddRawSku2");
                ProductController.LOG.info(e.getMessage());
                return "2: " + e.getMessage();
            }
            try{
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                ProductController.LOG.info("NewSkuAddRawSku3");
                ProductController.LOG.info(e.getMessage());
                return "3: " + e.getMessage();
            }
        } catch (SQLException e) {
            ProductController.LOG.info("NewSkuAddRawSku");
            ProductController.LOG.info(e.getMessage());
            return e.getMessage();
        }

        return "yay";
    }
}
