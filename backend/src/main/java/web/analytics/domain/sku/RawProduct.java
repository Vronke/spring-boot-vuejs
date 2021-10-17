package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class RawProduct {
    public int id;
    public String sku;
    public String goodSku;
    public boolean hover;
    public int classId;
    public String assignee;
    public int goodId;

    public int getId() {
        return id;
    }

    public RawProduct(){
        hover = false;
    }

    static public ArrayList<RawProduct> loadSku(){
        ArrayList<RawProduct> products = new ArrayList<RawProduct>();

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            Statement statement = connection.createStatement();
            String select = "SELECT r.ID, r.Name, p.Name, r.ProductClassID, r.Assignee, p.ID\n" +
                    "  FROM [WEDGE2].[dbo].[RawProduct] as r\n" +
                    "  left join Product as p on r.ProductID = p.ID" +
                    "  ORDER BY r.Name";
            ResultSet resultSet = statement.executeQuery(select);

            while (resultSet.next()){
                RawProduct product = new RawProduct();
                product.id = resultSet.getInt(1);
                product.sku = resultSet.getString(2);
                product.goodSku = resultSet.getString(3);
                product.classId = resultSet.getInt(4);
                product.assignee = resultSet.getString(5);
                product.goodId = resultSet.getInt(6);

                products.add(product);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("RawProductLoadSku");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        for (RawProduct p :
                products) {
            if (p.goodSku == null)
                p.goodSku = "";
        }
        products.get(0).hover = true;
        return products;
    }
    public Confirm toConfirm(){
        Confirm confirm = new Confirm();
        confirm.rawId = this.id;
        confirm.goodId = this.goodId;
        confirm.goodSku = this.goodSku;
        return confirm;
    }
    static public ArrayList<RawProduct> addSkuFromTemp() {
        ArrayList<RawProduct> products = new ArrayList<RawProduct>();

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            Statement statement = connection.createStatement();
            String select = "SELECT r.ID, r.Name, p.Name, r.ProductClassID, r.Assignee\n" +
                    "  FROM [WEDGE2].[dbo].[TriggerRawProduct] as r\n" +
                    "  left join Product as p on r.ProductID = p.ID" +
                    "  ORDER BY r.Name";
            ResultSet resultSet = statement.executeQuery(select);

            while (resultSet.next()){
                RawProduct product = new RawProduct();
                product.id = Integer.parseInt(resultSet.getString(1));
                product.sku = resultSet.getString(2);
                product.goodSku = resultSet.getString(3);
                product.classId = Integer.parseInt(resultSet.getString(4));
                product.assignee = resultSet.getString(5);

                products.add(product);
            }
            for (RawProduct p :
                    products) {
                deleteTempRawProduct(p, connection);
            }
            try {
                connection.commit();
            } catch (SQLException e){
                connection.rollback();
                throw e;
            }
            finally {
                connection.close();
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("RawProductAddSkuFromTemp");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        for (RawProduct p :
                products) {
            if (p.goodSku == null)
                p.goodSku = "";
        }
        return products;
    }
    static public long getCounter(int flag){
        long counter = 0;

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            Statement statement = connection.createStatement();
            String select = "select COUNT(p.ID) \n" +
                    "  FROM [WEDGE2].[dbo].Product p\n" +
                    "  inner join [WEDGE2].[dbo].RawProduct rp on p.ID = rp.ProductID\n" +
                    "  where p.Name = ''" +
                    "  AND  p.ProductClassID = " + flag;
            ResultSet resultSet = statement.executeQuery(select);

            while (resultSet.next()){
                counter = resultSet.getLong(1);
            }
        } catch (SQLException e){
            ProductController.LOG.info("RawProductGetCounter");
            ProductController.LOG.info(e.getMessage());
            return 0;
        }
        return counter;
    }
    static public long getFilteredCounter(int flag, String assignee, String filter, boolean isWithoutConfirm){
        long counter = 0;

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            Statement statement = connection.createStatement();
            String select = "";
            if (isWithoutConfirm){
                select = "select COUNT(p.ID) \n" +
                        "  FROM [WEDGE2].[dbo].Product p\n" +
                        "  inner join [WEDGE2].[dbo].RawProduct rp on p.ID = rp.ProductID\n" +
                        "  where p.Name = ''" +
                        "  AND  p.ProductClassID = " + flag +
                        "  AND rp.Assignee like '%" + assignee + "%'\n" +
                        "  AND rp.Name like '%" + filter + "%'";
            } else{
                 select = "select COUNT(p.ID) \n" +
                        "  FROM [WEDGE2].[dbo].Product p\n" +
                        "  inner join [WEDGE2].[dbo].RawProduct rp on p.ID = rp.ProductID\n" +
                        "  where p.ProductClassID = " + flag +
                        "  AND rp.Assignee like '%" + assignee + "%'\n" +
                        "  AND rp.Name like '%" + filter + "%'";
            }

            ResultSet resultSet = statement.executeQuery(select);

            while (resultSet.next()){
                counter = resultSet.getLong(1);
            }
        } catch (SQLException e){
            ProductController.LOG.info("RawProductGetFilteredCounter");
            ProductController.LOG.info(e.getMessage());
            return 0;
        }
        return counter;
    }
    static public void deleteTempRawProduct(RawProduct p, Connection connection){
        try{
            Statement statement = connection.createStatement();
            String delete = "DELETE FROM [WEDGE2].[dbo].[TriggerRawProduct] WHERE ID = " + p.id;
            statement.executeUpdate(delete);
        } catch (SQLException e){
            ProductController.LOG.info("RawProductDeleteTempRawProduct");
            ProductController.LOG.info(e.getMessage());
        }
    }

    public String getSku() {
        return sku;
    }
}
