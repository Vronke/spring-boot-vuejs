package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class Goods {
    public int id;
    public String text;
    public boolean hover = false;

    public Goods(){
    }

    static public ArrayList<Goods> selectGoodsList(String filter, int flag){
        ArrayList<Goods> goodsList = new ArrayList<Goods>();

        Goods first = new Goods();
        first.id = 0;
        first.text = "";
        goodsList.add(first);


        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            String select = "SELECT ID, Name\n" +
                    "  FROM [WEDGE2].[dbo].[ProductGood] WHERE Name LIKE ?\n" +
                    "AND (Flag & ?) = ?\n" +
                    "order by Name";
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1, filter + "%");
            statement.setInt(2, flag);
            statement.setInt(3, flag);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Goods goods = new Goods();
                goods.id = Integer.parseInt(resultSet.getString(1));
                goods.text = resultSet.getString(2);
                goodsList.add(goods);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("Goods");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        if (goodsList.size() > 1)
            goodsList.get(1).hover = true;
        else if(goodsList.size() == 1)
            goodsList.get(0).hover = true;
        return goodsList;
    }
}
