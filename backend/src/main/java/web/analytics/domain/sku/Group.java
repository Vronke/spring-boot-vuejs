package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class Group {
    public int id;
    public String text;
    public boolean hover = false;

    public Group(){
    }

    static public ArrayList<Group> selectGroupList(String filter, int flag){
        ArrayList<Group> groupList = new ArrayList<Group>();

        Group first = new Group();
        first.id = 0;
        first.text = "";
        groupList.add(first);

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            String select = "SELECT TOP 100 ID, Name\n" +
                    "  FROM [WEDGE2].[dbo].[ProductGroup] WHERE Name LIKE ?\n" +
                    "AND (Flag & ?) = ?\n" +
                    "order by Name";
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1, filter + "%");
            statement.setInt(2, flag);
            statement.setInt(3, flag);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Group group = new Group();
                group.id = Integer.parseInt(resultSet.getString(1));
                group.text = resultSet.getString(2);
                groupList.add(group);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("Group");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        if (groupList.size() > 1)
            groupList.get(1).hover = true;
        else if(groupList.size() == 1)
            groupList.get(0).hover = true;
        return groupList;
    }
}
