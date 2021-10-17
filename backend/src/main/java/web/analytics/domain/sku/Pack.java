package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class Pack {
    public int id;
    public String text;
    public boolean hover = false;

    public Pack(){
    }

    static public ArrayList<Pack> selectPackList(String filter, int flag){
        ArrayList<Pack> packList = new ArrayList<Pack>();

        Pack first = new Pack();
        first.id = 0;
        first.text = "";
        packList.add(first);


        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            String select = "SELECT TOP 100 ID, Name\n" +
                    "  FROM [WEDGE2].[dbo].[ProductPackage] WHERE Name LIKE ?\n" +
                    "AND (Flag & ?) = ?\n" +
                    "order by Name";
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1, filter + "%");
            statement.setInt(2, flag);
            statement.setInt(3, flag);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Pack pack = new Pack();
                pack.id = Integer.parseInt(resultSet.getString(1));
                pack.text = resultSet.getString(2);
                packList.add(pack);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("Pack");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        if (packList.size() > 1)
            packList.get(1).hover = true;
        else if(packList.size() == 1)
            packList.get(0).hover = true;
        return packList;
    }
}
