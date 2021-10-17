package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;

public class Property {
    public String table;
    public String value;
    public int flag;
    public int id;


    public static String addProperty(Property property){
        boolean isCreated = false;

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
            try {
                String select = "SELECT ID, Name\n" +
                        "  FROM [WEDGE2].[dbo].[" + property.table + "] " +
                        "WHERE Name = ?\n" +
                        "AND (Flag & ?) = ?";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setString(1, property.value);
                statement.setInt(2, property.flag);
                statement.setInt(3, property.flag);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    connection.rollback();
                    return "Already Created";
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                connection.rollback();
                return e.getMessage();
            }

            try {
                String select = "SELECT ID, Name\n" +
                        " FROM [WEDGE2].[dbo].[" + property.table + "]" +
                        " WHERE Name = ?";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setString(1, property.value);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    isCreated = true;
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                connection.rollback();
                ProductController.LOG.info("PropertyAddProperty1");
                ProductController.LOG.info(e.getMessage());
                return e.getMessage();
            }

            if (isCreated){
                try {
                    String update = "UPDATE [WEDGE2].[dbo].[" + property.table + "] " +
                            " SET Flag = Flag + ?" +
                            " WHERE Name = ?";
                    PreparedStatement statement = connection.prepareStatement(update);
                    statement.setInt(1, property.flag);
                    statement.setString(2, property.value);
                    statement.executeUpdate();
                }
                catch (SQLException e){
                    connection.rollback();
                    ProductController.LOG.info("PropertyAddProperty2");
                    ProductController.LOG.info(e.getMessage());
                    return e.getMessage();
                }
            }
            else {
                try {
                    String update = "INSERT INTO [WEDGE2].[dbo].[" + property.table + "] " +
                            "(ID, Name, Flag)" +
                            "VALUES ((SELECT MAX(ID) + 1 FROM [WEDGE2].[dbo].[" + property.table + "]), ?, ?)";
                    PreparedStatement statement = connection.prepareStatement(update);
                    statement.setString(1, property.value);
                    statement.setInt(2, property.flag);
                    statement.executeUpdate();
                }
                catch (SQLException e){
                    connection.rollback();
                    ProductController.LOG.info("PropertyAddProperty3");
                    ProductController.LOG.info(e.getMessage());
                    return e.getMessage();
                }
            }


            try {
                String select = "SELECT ID, Name\n" +
                        "  FROM [WEDGE2].[dbo].[" + property.table + "] " +
                        "WHERE Name = ? AND (Flag & ?) = ?";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setString(1, property.value);
                statement.setInt(2, property.flag);
                statement.setInt(3, property.flag);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    try {
                        connection.commit();
                    } catch (SQLException e){
                        connection.rollback();
                        throw e;
                    }
//                    finally {
//                        connection.close();
//                    }
                    return resultSet.getString(1);
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                connection.rollback();
                ProductController.LOG.info("PropertyAddProperty4");
                ProductController.LOG.info(e.getMessage());
                return e.getMessage();
            }
            try {
                connection.commit();
            } catch (SQLException e){
                connection.rollback();
                throw e;
            }
//            finally {
//                connection.close();
//            }
        } catch (SQLException e){
            ProductController.LOG.info("PropertyAddProperty");
            ProductController.LOG.info(e.getMessage());
            return e.getMessage();
        }

        return "ERROR";
    }


    public static String changeProperty(Property property){
        int resultId = 0;
        if (property.id == 0)
            return "Нельзя изменить это свойство!";


        try{
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            try {
                String select = "SELECT ID, Name\n" +
                        "  FROM [WEDGE2].[dbo].[" + property.table + "] " +
                        "WHERE Name collate SQL_Latin1_General_Cp1250_CS_AS = ?\n" +
                        "AND (Flag & ?) = ?";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setString(1, property.value);
                statement.setInt(2, property.flag);
                statement.setInt(3, property.flag);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    connection.rollback();
                    return "Такое свойство уже существует!";
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                connection.rollback();
                ProductController.LOG.info("PropertyChangeProperty1");
                ProductController.LOG.info(e.getMessage());
                return e.getMessage();
            }

            try {
                String select = "SELECT ID, Name\n" +
                        " FROM [WEDGE2].[dbo].[" + property.table + "]" +
                        " WHERE Name collate SQL_Latin1_General_Cp1250_CS_AS = ?";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setString(1, property.value);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    connection.rollback();
                    return "Такое свойство уже существует!";
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                connection.rollback();
                ProductController.LOG.info("PropertyChangeProperty2");
                ProductController.LOG.info(e.getMessage());
                return e.getMessage();
            }

            try {
                String update = "UPDATE [WEDGE2].[dbo].[" + property.table + "] " +
                        "SET Name = ? " +
                        "WHERE ID = ?";
                PreparedStatement statement = connection.prepareStatement(update);
                statement.setString(1, property.value);
                statement.setInt(2, property.id);

                statement.executeUpdate();
            }
            catch (SQLException e){
                connection.rollback();
                ProductController.LOG.info("PropertyChangeProperty3");
                ProductController.LOG.info(e.getMessage());
                return e.getMessage();
            }

            try {
                String select = "SELECT ID, Name\n" +
                        "  FROM [WEDGE2].[dbo].[" + property.table + "] " +
                        "WHERE ID = ?";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setInt(1, property.id);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){

//                    finally {
//                        connection.close();
//                    }
                    resultId = resultSet.getInt(1);
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                connection.rollback();
                ProductController.LOG.info("PropertyChangeProperty4");
                ProductController.LOG.info(e.getMessage());
                return e.getMessage();
            }
            try {
                String update = "UPDATE p" +
                        "  SET p.Name = RTRIM(LTRIM(replace(replace(replace((pg.Name + ' ' + pb.Name + ' ' + psb.Name + ' ' + " +
                        "  pf.Name + ' ' + pc.Name + ' ' + pabv.Name + ' ' + p.Weight + ' ' +" +
                        "  pq.Name + ' ' + pp.Name + ' ' + po.Name + ' ' + pe.Name), ' ', ' ♦'), '♦ ', ''),' ♦',' ')))" +
                        "  FROM [WEDGE2].[dbo].[Product] p" +
                        "  left join ProductGood as pg on p.ProductGoodID = pg.ID" +
                        "  left join ProductBrand as pb on p.ProductBrandID = pb.ID" +
                        "  left join ProductSubbrand as psb on p.ProductSubbrandID = psb.ID" +
                        "  left join ProductFlavour as pf on p.ProductFlavourID = pf.ID" +
                        "  left join ProductColour as pc on p.ProductColourID = pc.ID" +
                        "  left join ProductABV as pabv on p.ProductABVID = pabv.ID" +
                        "  left join ProductQuantity as pq on p.ProductQuantityID = pq.ID" +
                        "  left join ProductPackage as pp on p.ProductPackageID = pp.ID" +
                        "  left join ProductOrigin as po on p.ProductOriginID = po.ID" +
                        "  left join ProductExtra as pe on p.ProductExtraID = pe.ID" +
                        "  where p." + property.table + "ID = " + resultId;
                PreparedStatement statement = connection.prepareStatement(update);

                statement.executeUpdate();
            }
            catch (SQLException e){
                connection.rollback();
                ProductController.LOG.info("PropertyChangeProperty5");
                ProductController.LOG.info(e.getMessage());
                return e.getMessage();
            }

            try {
                connection.commit();
                return String.valueOf(resultId);
            } catch (SQLException e){
                connection.rollback();
                throw e;
            }
//            finally {
//                connection.close();
//            }
        } catch (SQLException e) {
            ProductController.LOG.info("PropertyChangeProperty");
            ProductController.LOG.info(e.getMessage());
            return e.getMessage();
        }
    }
}
