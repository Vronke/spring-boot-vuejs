package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class Type {
    public int id;
    public String text;
    public boolean hover = false;

    public Type(){

    }

    static public ArrayList<Type> selectTypeList(String filter, int flag){
        ArrayList<Type> typeList = new ArrayList<Type>();

        Type first = new Type();
        first.id = 0;
        first.text = "";
        typeList.add(first);

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            String select = "SELECT ID, Name\n" +
                    "  FROM [WEDGE2].[dbo].[ProductType] WHERE Name LIKE ?\n" +
                    "AND (Flag & ?) = ?\n" +
                    "order by Name";
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1, filter + "%");
            statement.setInt(2, flag);
            statement.setInt(3, flag);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Type type = new Type();
                type.id = Integer.parseInt(resultSet.getString(1));
                type.text = resultSet.getString(2);
                typeList.add(type);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("Type");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        if (typeList.size() > 1)
            typeList.get(1).hover = true;
        else if(typeList.size() == 1)
            typeList.get(0).hover = true;
        return typeList;
    }
}
