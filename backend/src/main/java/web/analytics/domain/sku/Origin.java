package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class Origin {
    public int id;
    public String text;
    public boolean hover = false;

    public Origin(){
    }

    static public ArrayList<Origin> selectOriginList(String filter, int flag){
        ArrayList<Origin> originList = new ArrayList<Origin>();

        Origin first = new Origin();
        first.id = 0;
        first.text = "";
        originList.add(first);

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            String select = "SELECT TOP 100 ID, Name\n" +
                    "  FROM [WEDGE2].[dbo].[ProductOrigin] WHERE Name LIKE ?\n" +
                    "AND (Flag & ?) = ?\n" +
                    "order by Name";
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1, filter + "%");
            statement.setInt(2, flag);
            statement.setInt(3, flag);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Origin origin = new Origin();
                origin.id = Integer.parseInt(resultSet.getString(1));
                origin.text = resultSet.getString(2);
                originList.add(origin);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("Origin");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        if (originList.size() > 1)
            originList.get(1).hover = true;
        else if(originList.size() == 1)
            originList.get(0).hover = true;
        return originList;
    }
}
