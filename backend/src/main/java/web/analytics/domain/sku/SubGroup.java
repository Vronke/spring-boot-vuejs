package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class SubGroup {
    public int id;
    public String text;
    public boolean hover = false;

    public SubGroup(){
    }

    static public ArrayList<SubGroup> selectSubGroupList(String filter, int flag){
        ArrayList<SubGroup> subGroupList = new ArrayList<SubGroup>();

        SubGroup first = new SubGroup();
        first.id = 0;
        first.text = "";
        subGroupList.add(first);

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            String select = "SELECT TOP 100 ID, Name\n" +
                    "  FROM [WEDGE2].[dbo].[ProductSubGroup] WHERE Name LIKE ?\n" +
                    "AND (Flag & ?) = ?\n" +
                    "order by Name";
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1, filter + "%");
            statement.setInt(2, flag);
            statement.setInt(3, flag);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                SubGroup subgroup = new SubGroup();
                subgroup.id = Integer.parseInt(resultSet.getString(1));
                subgroup.text = resultSet.getString(2);
                subGroupList.add(subgroup);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("SubGroup");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        if (subGroupList.size() > 1)
            subGroupList.get(1).hover = true;
        else if(subGroupList.size() == 1)
            subGroupList.get(0).hover = true;
        return subGroupList;
    }
}
