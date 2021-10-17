package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class ABV {
    public int id;
    public String text;
    public boolean hover = false;

    public ABV(){
    }

    static public ArrayList<ABV> selectABVList(String filter, int flag, boolean isFirstSelect) {
        ArrayList<ABV> abvList = new ArrayList<ABV>();
        ABV first = new ABV();
        first.id = 0;
        first.text = "";
        abvList.add(first);

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            String select = "SELECT TOP 100 ID, Name\n" +
                    "  FROM [WEDGE2].[dbo].[ProductABV] WHERE Name LIKE ?\n" +
                    "AND (Flag & ?) = ?\n" +
                    "order by Name";
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1, filter + "%");
            statement.setInt(2, flag);
            statement.setInt(3, flag);


            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                ABV abv = new ABV();
                abv.id = Integer.parseInt(resultSet.getString(1));
                abv.text = resultSet.getString(2);
                abvList.add(abv);
            }
        } catch (SQLException e){
            ProductController.LOG.info("ABV");
            ProductController.LOG.info(e.getMessage());
            return null;
        }


        if (abvList.size() > 1)
            abvList.get(1).hover = true;
        else if (abvList.size() == 1)
            abvList.get(0).hover = true;
        return abvList;
    }
}
