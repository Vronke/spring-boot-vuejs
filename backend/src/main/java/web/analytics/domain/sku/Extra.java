package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class Extra {
    public int id;
    public String text;
    public boolean hover = false;

    public Extra(){
    }

    static public ArrayList<Extra> selectExtraList(String filter, int flag){
        ArrayList<Extra> extraList = new ArrayList<Extra>();

        Extra first = new Extra();
        first.id = 0;
        first.text = "";
        extraList.add(first);


        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            String select = "SELECT TOP 100 ID, Name\n" +
                    "  FROM [WEDGE2].[dbo].[ProductExtra] WHERE Name LIKE ?\n" +
                    "AND (Flag & ?) = ?\n" +
                    "order by Name";
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1, filter + "%");
            statement.setInt(2, flag);
            statement.setInt(3, flag);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Extra extra = new Extra();
                extra.id = Integer.parseInt(resultSet.getString(1));
                extra.text = resultSet.getString(2);
                extraList.add(extra);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("Extra");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        if (extraList.size() > 1)
            extraList.get(1).hover = true;
        else if(extraList.size() == 1)
            extraList.get(0).hover = true;
        return extraList;
    }
}
