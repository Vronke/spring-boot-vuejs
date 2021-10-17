package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class Quantity {
    public int id;
    public String text;
    public boolean hover = false;

    public Quantity(){
    }

    static public ArrayList<Quantity> selectQuantityList(String filter, int flag){
        ArrayList<Quantity> quantityList = new ArrayList<Quantity>();

        Quantity first = new Quantity();
        first.id = 0;
        first.text = "";
        quantityList.add(first);


        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            String select = "SELECT TOP 100 ID, Name\n" +
                    "  FROM [WEDGE2].[dbo].[ProductQuantity] WHERE Name LIKE ?\n" +
                    "AND (Flag & ?) = ?\n" +
                    "order by Name";
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1, filter + "%");
            statement.setInt(2, flag);
            statement.setInt(3, flag);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Quantity quantity = new Quantity();
                quantity.id = Integer.parseInt(resultSet.getString(1));
                quantity.text = resultSet.getString(2);
                quantityList.add(quantity);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("Quantity");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        if (quantityList.size() > 1)
            quantityList.get(1).hover = true;
        else if(quantityList.size() == 1)
            quantityList.get(0).hover = true;
        return quantityList;
    }
}
