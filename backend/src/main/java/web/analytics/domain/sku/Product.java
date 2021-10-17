package web.analytics.domain.sku;

import org.apache.poi.hpsf.Decimal;
import org.slf4j.Logger;
import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Product {


    public int groupID;
    public String group;
    public int clientGroupID;
    public String clientGroup;
    public int subGroupID;
    public String subGroup;
    public int typeID;
    public String type;
    public int goodsID;
    public String goods;
    public int brandID;
    public String brand;
    public int subBrandID;
    public String subBrand;
    public int colorID;
    public String color;
    public int flavorID;
    public String flavor;
    public int extraID;
    public String extra;
    public int packageID;
    public String pack;
    public int quantityID;
    public String quantity;
    public int manufacturerID;
    public String manufacturer;
    public int originID;
    public String origin;
    public String weight;
    public float mass;
    public int abvID;
    public String abv;

    public Product(){

    }
    public Product(int id){
        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            String select = "SELECT TOP (300) * FROM [WEDGE2].[dbo].[GoodSKU] WHERE [ID] = ?";
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                this.groupID = Integer.parseInt(resultSet.getString(3));
                this.clientGroupID = Integer.parseInt(resultSet.getString(5));
                this.subGroupID = Integer.parseInt(resultSet.getString(7));
                this.typeID = Integer.parseInt(resultSet.getString(9));
                this.goodsID = Integer.parseInt(resultSet.getString(11));
                this.brandID = Integer.parseInt(resultSet.getString(13));
                this.subBrandID = Integer.parseInt(resultSet.getString(15));
                this.colorID = Integer.parseInt(resultSet.getString(17));
                this.flavorID = Integer.parseInt(resultSet.getString(19));
                this.extraID = Integer.parseInt(resultSet.getString(21));
                this.packageID = Integer.parseInt(resultSet.getString(23));
                this.quantityID = Integer.parseInt(resultSet.getString(25));
                this.manufacturerID = Integer.parseInt(resultSet.getString(27));
                this.originID = Integer.parseInt(resultSet.getString(29));
                this.abvID = Integer.parseInt(resultSet.getString(33));

                this.group = resultSet.getString(4);
                this.clientGroup = resultSet.getString(6);
                this.subGroup = resultSet.getString(8);
                this.type = resultSet.getString(10);
                this.goods = resultSet.getString(12);
                this.brand = resultSet.getString(14);
                this.subBrand = resultSet.getString(16);
                this.color = resultSet.getString(18);
                this.flavor = resultSet.getString(20);
                this.extra = resultSet.getString(22);
                this.pack = resultSet.getString(24);
                this.quantity = resultSet.getString(26);
                this.manufacturer = resultSet.getString(28);
                this.origin = resultSet.getString(30);
                this.weight = resultSet.getString(31);
                this.mass = Float.parseFloat(resultSet.getString(32));
                this.abv = resultSet.getString(34);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("Product");
            ProductController.LOG.info(e.getMessage());
        }
    }
}
