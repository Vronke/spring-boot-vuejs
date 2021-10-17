package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class GoodProduct {
    public int id;
    public String sku;
    public boolean hover;
    public int classId;
    public int productGroupID;
    public int productSubGroupID;
    public int productTypeID;
    public int productBrandID;
    public int productSubBrandID;
    public int productFlavourID;
    public int productPackageID;
    public int productQuantityID;
    public int productManufacturerID;
    public int productOriginID;
    public String weight;
    public double mass;
    public int productGoodID;
    public int productColourID;
    public int productExtraID;
    public int productABVID;
    public int productClientGroupID;
    public String author;
    public String subgroup;
    public String type;
    public String manufacturer;

    public int getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    @Override
    public String toString() {
        return "GoodProduct{" +
                "id=" + id +
                ", sku='" + sku + '\'' +
                ", hover=" + hover +
                ", classId=" + classId +
                ", productGroupID=" + productGroupID +
                ", productSubGroupID=" + productSubGroupID +
                ", productTypeID=" + productTypeID +
                ", productBrandID=" + productBrandID +
                ", productSubBrandID=" + productSubBrandID +
                ", productFlavourID=" + productFlavourID +
                ", productPackageID=" + productPackageID +
                ", productQuantityID=" + productQuantityID +
                ", productManufacturerID=" + productManufacturerID +
                ", productOriginID=" + productOriginID +
                ", weight='" + weight + '\'' +
                ", mass=" + mass +
                ", productGoodID=" + productGoodID +
                ", productColourID=" + productColourID +
                ", productExtraID=" + productExtraID +
                ", productABVID=" + productABVID +
                ", productClientGroupID=" + productClientGroupID +
                ", author='" + author + '\'' +
                ", subgroup='" + subgroup + '\'' +
                ", type='" + type + '\'' +
                ", manufacturer='" + manufacturer + '\'' +
                '}';
    }

    public boolean isHover() {
        return hover;
    }

    public int getClassId() {
        return classId;
    }

    public int getProductGroupID() {
        return productGroupID;
    }

    public int getProductSubGroupID() {
        return productSubGroupID;
    }

    public int getProductTypeID() {
        return productTypeID;
    }

    public int getProductBrandID() {
        return productBrandID;
    }

    public int getProductSubBrandID() {
        return productSubBrandID;
    }

    public int getProductFlavourID() {
        return productFlavourID;
    }

    public int getProductPackageID() {
        return productPackageID;
    }

    public int getProductQuantityID() {
        return productQuantityID;
    }

    public int getProductManufacturerID() {
        return productManufacturerID;
    }

    public int getProductOriginID() {
        return productOriginID;
    }

    public String getWeight() {
        return weight;
    }

    public double getMass() {
        return mass;
    }

    public int getProductGoodID() {
        return productGoodID;
    }

    public int getProductColourID() {
        return productColourID;
    }

    public int getProductExtraID() {
        return productExtraID;
    }

    public int getProductABVID() {
        return productABVID;
    }

    public int getProductClientGroupID() {
        return productClientGroupID;
    }

    public String getAuthor() {
        return author;
    }

    public GoodProduct(){
        hover = false;
        manufacturer = "";
    }

    static public ArrayList<GoodProduct> loadSku(){
        ArrayList<GoodProduct> products = new ArrayList<GoodProduct>();


        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            Statement statement = connection.createStatement();
            String select = "SELECT p.ID, p.Name, ProductClassID, sb.Name, t.Name, m.Name FROM [WEDGE2].[dbo].[Product] as p" +
                    "  left join ProductSubgroup as sb on p.ProductSubgroupID = sb.ID" +
                    "  left join ProductType as t on p.ProductTypeID = t.ID" +
                    "  left join ProductManufacturer as m on p.ProductManufacturerID = m.ID" +
                    " ORDER BY p.Name";
            ResultSet resultSet = statement.executeQuery(select);

            while (resultSet.next()){
                GoodProduct product = new GoodProduct();
                product.id = Integer.parseInt(resultSet.getString(1));
                product.sku = resultSet.getString(2);
                product.classId = Integer.parseInt(resultSet.getString(3));
                product.subgroup = resultSet.getString(4);
                product.type = resultSet.getString(5);
                product.manufacturer = resultSet.getString(6);
                if (product.manufacturer == null)
                    product.manufacturer = "";
                products.add(product);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("GoodProductLoadSku");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        products.get(0).hover = true;
        return products;
    }

    static public ArrayList<GoodProduct> addSkuFromTemp(){
        ArrayList<GoodProduct> products = new ArrayList<GoodProduct>();


        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            Statement statement = connection.createStatement();
            String select = "SELECT p.ID, p.Name, ProductClassID, sb.Name, t.Name, m.Name FROM [WEDGE2].[dbo].[TriggerGoodProduct] as p" +
                    "  left join ProductSubgroup as sb on p.ProductSubgroupID = sb.ID" +
                    "  left join ProductType as t on p.ProductTypeID = t.ID" +
                    "  left join ProductManufacturer as m on p.ProductManufacturerID = m.ID" +
                    "  GROUP BY p.ID, p.Name, ProductClassID, sb.Name, t.Name, m.Name" +
                    "  ORDER BY p.Name";
            ResultSet resultSet = statement.executeQuery(select);

            while (resultSet.next()){
                GoodProduct product = new GoodProduct();
                product.id = Integer.parseInt(resultSet.getString(1));
                product.sku = resultSet.getString(2);
                product.classId = Integer.parseInt(resultSet.getString(3));
                product.subgroup = resultSet.getString(4);
                product.type = resultSet.getString(5);
                product.manufacturer = resultSet.getString(6);
                if (product.manufacturer == null)
                    product.manufacturer = "";
                products.add(product);
            }

            for (GoodProduct p :
                    products) {
                deleteFromTempGoodSku(p);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("GoodProductAddSkuFromTemp");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        return products;
    }
    static public void deleteFromTempGoodSku(GoodProduct product){
        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            Statement statement = connection.createStatement();
            String delete = "DELETE FROM [WEDGE2].[dbo].[TriggerGoodProduct] WHERE ID = " + product.id;
            statement.executeUpdate(delete);
        }
       catch (SQLException e){
           ProductController.LOG.info("GoodProductDeleteFromTempGoodSku");
           ProductController.LOG.info(e.getMessage());
       }
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public GoodProduct errorGoodProduct(String error) {
        this.id = -1;
        this.manufacturer = error;
        return this;
    }

    public GoodProduct(int id) {
        this.id = id;
    }

    static public GoodProduct createSku(GoodProduct product) {
        ProductController.LOG.info("creatingSku1");
        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            try {
                String select = "SELECT TOP 1 * FROM [WEDGE2].[dbo].[Product] " +
                        "WHERE ProductGroupID = ? AND ProductSubgroupID = ? AND " +
                        "ProductTypeID = ? AND ProductBrandID = ? AND " +
                        "ProductSubbrandID = ? AND ProductFlavourID = ? AND " +
                        "ProductPackageID = ? AND ProductQuantityID = ? AND " +
                        "ProductManufacturerID = ? AND ProductOriginID = ? AND " +
                        "Weight = ? AND Mass = ? AND " +
                        "ProductGoodID = ? AND ProductClassID = ? AND " +
                        "ProductColourID = ? AND ProductExtraID = ? AND " +
                        "ProductABVID = ? AND ProductClientGroupID = ?";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setInt(1, product.productGroupID);
                statement.setInt(2, product.productSubGroupID);
                statement.setInt(3, product.productTypeID);
                statement.setInt(4, product.productBrandID);
                statement.setInt(5, product.productSubBrandID);
                statement.setInt(6, product.productFlavourID);
                statement.setInt(7, product.productPackageID);
                statement.setInt(8, product.productQuantityID);
                statement.setInt(9, product.productManufacturerID);
                statement.setInt(10, product.productOriginID);
                statement.setString(11, product.weight);
                statement.setDouble(12, product.mass);
                statement.setInt(13, product.productGoodID);
                statement.setInt(14, product.classId);
                statement.setInt(15, product.productColourID);
                statement.setInt(16, product.productExtraID);
                statement.setInt(17, product.productABVID);
                statement.setInt(18, product.productClientGroupID);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    connection.rollback();
                    return product.errorGoodProduct("Already created");
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                connection.rollback();
                if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                    return createSku(product);
                ProductController.LOG.info("GoodProductCreateSku1");
                ProductController.LOG.info(e.getMessage());
                return product.errorGoodProduct("1: " + e.getMessage());
            }

            String GUID = "";
            ProductController.LOG.info("creatingSku2");


            try {
                String select = "SELECT TOP 1 [UniqueID] FROM [WEDGE2].[dbo].[Product] " +
                        "WHERE ProductTypeID = ? AND ProductBrandID = ? AND ProductSubbrandID = ? AND " +
                        "ProductPackageID = ? AND ProductManufacturerID = ? AND Weight = ? AND " +
                        "Mass = ? AND ProductColourID = ? AND ProductABVID = ?";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setInt(1, product.productTypeID);
                statement.setInt(2, product.productBrandID);
                statement.setInt(3, product.productSubBrandID);
                statement.setInt(4, product.productPackageID);
                statement.setInt(5, product.productManufacturerID);
                statement.setString(6, product.weight);
                statement.setDouble(7, product.mass);
                statement.setInt(8, product.productColourID);
                statement.setInt(9, product.productABVID);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    GUID = resultSet.getString(1);
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                connection.rollback();
                if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                    return createSku(product);
                ProductController.LOG.info("GoodProductCreateSkuCheckGuid");
                ProductController.LOG.info(e.getMessage());
                return product.errorGoodProduct("1: " + e.getMessage());
            }
            ProductController.LOG.info("creatingSku3");
            try {
                String create = "insert into [WEDGE2].[dbo].[Product] \n" +
                        "([ID], [ProductGroupID],[ProductSubgroupID],[ProductTypeID],[ProductBrandID],[ProductSubbrandID],[ProductFlavourID]\n" +
                        ",[ProductPackageID],[ProductQuantityID],[ProductManufacturerID],[ProductOriginID],[Weight],[Mass],[Name]\n" +
                        ",[ProductGoodID],[ProductClassID],[ProductColourID],[ProductExtraID],[ProductABVID],[Date],[Author],[ProductClientGroupID],[UniqueID]) \n" +
                        "VALUES ((SELECT MAX(ID) + 1 FROM [WEDGE2].[dbo].[Product]), ?, ?, ?, " +
                        "?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), ?, ?, " +
                        (GUID.equals("") ? "REPLACE(CONVERT(nchar(50), NEWID()),'-','')" : "'" + GUID + "'") + ")";
                PreparedStatement statement = connection.prepareStatement(create);
                statement.setInt(1, product.productGroupID);
                statement.setInt(2, product.productSubGroupID);
                statement.setInt(3, product.productTypeID);
                statement.setInt(4, product.productBrandID);
                statement.setInt(5, product.productSubBrandID);
                statement.setInt(6, product.productFlavourID);
                statement.setInt(7, product.productPackageID);
                statement.setInt(8, product.productQuantityID);
                statement.setInt(9, product.productManufacturerID);
                statement.setInt(10, product.productOriginID);
                statement.setString(11, product.weight);
                statement.setDouble(12, product.mass);
                statement.setString(13, product.sku);
                statement.setInt(14, product.productGoodID);
                statement.setInt(15, product.classId);
                statement.setInt(16, product.productColourID);
                statement.setInt(17, product.productExtraID);
                statement.setInt(18, product.productABVID);
                statement.setString(19, product.author);
                statement.setInt(20, product.productClientGroupID);

                statement.executeUpdate();
            }
            catch (SQLException e) {
                connection.rollback();
                if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                    return createSku(product);
                ProductController.LOG.info("GoodProductCreateSku2");
                ProductController.LOG.info(e.getMessage());
                return product.errorGoodProduct("2: " + e.getMessage());
            }
            ProductController.LOG.info("creatingSku4");

            try {
                String select = "select MAX(ID)\n" +
                        "  FROM [WEDGE2].[dbo].[Product]\n" +
                        "  where Name = ?";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setString(1, product.sku);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    try {
                        connection.commit();
                    } catch (SQLException e){
                        connection.rollback();
                        if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                            return createSku(product);
                        throw e;
                    }
                    product.id = resultSet.getInt(1);
                    return product;
                }
            } catch (SQLException e){
                connection.rollback();
                if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                    return createSku(product);
                ProductController.LOG.info("GoodProductCreateSku3");
                ProductController.LOG.info(e.getMessage());
            }
            try {
                connection.commit();
            } catch (SQLException e){
                if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                    return createSku(product);
                connection.rollback();
                ProductController.LOG.info("GoodProductCreateSKU");
                ProductController.LOG.info(e.getMessage());
                throw e;
            }
        }
        catch (SQLException e){
            if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                return createSku(product);
            ProductController.LOG.info("GoodProductCreateSKU");
            ProductController.LOG.info(e.getMessage());
            return product.errorGoodProduct(e.getMessage());
        }
        return product.errorGoodProduct("YAY");
    }

    static public ArrayList<GoodProduct> getLastGoodProduct(){
        ArrayList<GoodProduct> products = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            Statement statement = connection.createStatement();
            String select = " SELECT ID, Name FROM [WEDGE2].[dbo].[LogGoodProduct]" +
                    " WHERE Date > DATEADD(n, -10, GETDATE()) " +
                    " ORDER BY Date";
            ResultSet resultSet = statement.executeQuery(select);

            while (resultSet.next()){
                GoodProduct product = new GoodProduct();
                product.id = Integer.parseInt(resultSet.getString(1));
                product.sku = resultSet.getString(2);
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

    static public GoodProduct updateSku(GoodProduct product) {
        try{
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
            try {
                String select = "SELECT * FROM [WEDGE2].[dbo].[Product] " +
                        "WHERE ProductGroupID = ? AND ProductSubgroupID = ? AND " +
                        "ProductTypeID = ? AND ProductBrandID = ? AND " +
                        "ProductSubbrandID = ? AND ProductFlavourID = ? AND " +
                        "ProductPackageID = ? AND ProductQuantityID = ? AND " +
                        "ProductManufacturerID = ? AND ProductOriginID = ? AND " +
                        "Weight = ? AND Mass = ? AND " +
                        "ProductGoodID = ? AND ProductClassID = ? AND " +
                        "ProductColourID = ? AND ProductExtraID = ? AND " +
                        "ProductABVID = ? AND ProductClientGroupID = ?";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setInt(1, product.productGroupID);
                statement.setInt(2, product.productSubGroupID);
                statement.setInt(3, product.productTypeID);
                statement.setInt(4, product.productBrandID);
                statement.setInt(5, product.productSubBrandID);
                statement.setInt(6, product.productFlavourID);
                statement.setInt(7, product.productPackageID);
                statement.setInt(8, product.productQuantityID);
                statement.setInt(9, product.productManufacturerID);
                statement.setInt(10, product.productOriginID);
                statement.setString(11, product.weight);
                statement.setDouble(12, product.mass);
                statement.setInt(13, product.productGoodID);
                statement.setInt(14, product.classId);
                statement.setInt(15, product.productColourID);
                statement.setInt(16, product.productExtraID);
                statement.setInt(17, product.productABVID);
                statement.setInt(18, product.productClientGroupID);

                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()){
                    product.manufacturer = "Такая номенклатура уже существует!";
                    product.id = -1;
                    connection.rollback();
                    return product;
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                connection.rollback();
                if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                    return updateSku(product);
                ProductController.LOG.info("GoodProductUpdateSku1");
                ProductController.LOG.info(e.getMessage());

                product.manufacturer = "1: " + e.getMessage();
                product.id = -1;
                return product;
            }

            String GUID = "";

            try {
                String select = "SELECT [UniqueID] FROM [WEDGE2].[dbo].[Product] " +
                        "WHERE ProductTypeID = ? AND ProductBrandID = ? AND ProductSubbrandID = ? AND " +
                        "ProductPackageID = ? AND ProductManufacturerID = ? AND Weight = ? AND " +
                        "Mass = ? AND ProductColourID = ? AND ProductABVID = ?";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setInt(1, product.productTypeID);
                statement.setInt(2, product.productBrandID);
                statement.setInt(3, product.productSubBrandID);
                statement.setInt(4, product.productPackageID);
                statement.setInt(5, product.productManufacturerID);
                statement.setString(6, product.weight);
                statement.setDouble(7, product.mass);
                statement.setInt(8, product.productColourID);
                statement.setInt(9, product.productABVID);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    GUID = resultSet.getString(1);
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                connection.rollback();
                if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                    return updateSku(product);
                ProductController.LOG.info("GoodProductUpdateSkuCheckGuid");
                ProductController.LOG.info(e.getMessage());
                product.manufacturer = "1: " + e.getMessage();
                product.id = -1;
                return product;
            }

            try {
                String create = "UPDATE [WEDGE2].[dbo].[Product] SET [ProductGroupID] = ?, [ProductSubgroupID] = ?, " +
                        "[ProductTypeID] = ?,[ProductBrandID] = ?,[ProductSubbrandID] = ?,[ProductFlavourID] = ?, " +
                        "[ProductPackageID] = ?, [ProductQuantityID] = ?, [ProductManufacturerID] = ?,[ProductOriginID] = ?, " +
                        "[Weight] = ?, [Mass] = ?, [Name] = ?, [ProductGoodID] = ?, [ProductClassID] = ?, " +
                        "[ProductColourID] = ?, [ProductExtraID] = ?, [ProductABVID] = ?, [Date] = GETDATE(), [Author] = ?, " +
                        "[ProductClientGroupID] = ?, [UniqueID] = " + (GUID.equals("") ? "REPLACE(CONVERT(nchar(50), NEWID()),'-','')" : "'" + GUID + "'") +
                        " WHERE ID = ?";
                PreparedStatement statement = connection.prepareStatement(create);
                statement.setInt(1, product.productGroupID);
                statement.setInt(2, product.productSubGroupID);
                statement.setInt(3, product.productTypeID);
                statement.setInt(4, product.productBrandID);
                statement.setInt(5, product.productSubBrandID);
                statement.setInt(6, product.productFlavourID);
                statement.setInt(7, product.productPackageID);
                statement.setInt(8, product.productQuantityID);
                statement.setInt(9, product.productManufacturerID);
                statement.setInt(10, product.productOriginID);
                statement.setString(11, product.weight);
                statement.setDouble(12, product.mass);
                statement.setString(13, product.sku);
                statement.setInt(14, product.productGoodID);
                statement.setInt(15, product.classId);
                statement.setInt(16, product.productColourID);
                statement.setInt(17, product.productExtraID);
                statement.setInt(18, product.productABVID);
                statement.setString(19, product.author);
                statement.setInt(20, product.productClientGroupID);
                statement.setInt(21, product.id);

                statement.executeUpdate();
            }
            catch (SQLException e) {
                connection.rollback();
                if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                    return updateSku(product);
                ProductController.LOG.info("GoodProductUpdateSku2");
                ProductController.LOG.info(e.getMessage());
                product.manufacturer = "2: " + e.getMessage();
                product.id = -1;
                return product;
            }

            try {
                String select = "select MAX(ID)\n" +
                        "  FROM [WEDGE2].[dbo].[Product]\n" +
                        "  where Name = ?";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setString(1, product.sku);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    try {
                        connection.commit();
                    } catch (SQLException e){
                        connection.rollback();
                        if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                            return updateSku(product);
                        throw e;
                    }
                    if (resultSet.getInt(1) == product.id)
                    return product;
                }
            } catch (SQLException e){
                connection.rollback();
                if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                    return updateSku(product);
                ProductController.LOG.info("GoodProductUpdateSku3");
                ProductController.LOG.info(e.getMessage());
            }
            try {
                connection.commit();
            } catch (SQLException e){
                connection.rollback();
                if (e.getMessage().contains("was deadlocked on lock resources with another process"))
                    return updateSku(product);
                throw e;
            }
        } catch (SQLException e){
            ProductController.LOG.info("GoodProductUpdateSKU");
            ProductController.LOG.info(e.getMessage());
        }


        product.manufacturer = "Не удалось изменить номенклатуру";
        product.id = -1;
        return product;
    }
}
