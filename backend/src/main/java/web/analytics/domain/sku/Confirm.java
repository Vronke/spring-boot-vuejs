package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class Confirm {
    public int rawId;
    public int goodId;
    public String goodSku;

    @Override
    public String toString() {
        return "Confirm{" +
                "rawId=" + rawId +
                ", goodId=" + goodId +
                ", goodSku='" + goodSku + '\'' +
                '}';
    }


    public static Confirm confirmSku(Confirm confirm){
        String update = "";
        ProductController.LOG.info("ConfirmingSku1");
        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            connection.setAutoCommit(false);
            update = "UPDATE RawProduct " +
                    " SET ProductID = ?" +
                    " WHERE ID = ?";
            PreparedStatement statement = connection.prepareStatement(update);
            statement.setQueryTimeout(3);
            statement.setInt(1, confirm.goodId);
            statement.setInt(2, confirm.rawId);
            try {
                statement.executeUpdate();
                connection.commit();
            } catch (SQLException e){
                connection.rollback();
                throw e;
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {

            ProductController.LOG.info("Confirm |||" + update);
            ProductController.LOG.info(e.getMessage());
            confirm.goodId = -1;
            confirm.goodSku = e.getMessage();
            return confirm;
        }
        ProductController.LOG.info("skuConfirmed");
        return confirm;
    }
    public static ArrayList<Confirm> getLastConfirmedProduct(){
        ArrayList<Confirm> products = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            Statement statement = connection.createStatement();
            String select = " SELECT r.ID, p.ID, p.Name FROM [WEDGE2].[dbo].[LogRawProduct] as r" +
                    " INNER JOIN [WEDGE2].[dbo].[Product] as p on p.ID = r.ProductID" +
                    " WHERE r.Date > DATEADD(n, -10, GETDATE()) " +
                    " ORDER BY r.Date";
            ResultSet resultSet = statement.executeQuery(select);

            while (resultSet.next()){
                Confirm product = new Confirm();
                product.rawId = resultSet.getInt(1);
                product.goodId = resultSet.getInt(2);
                product.goodSku = resultSet.getString(3);
                products.add(product);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("getLastGoodProduct");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        return products;
    }
}
