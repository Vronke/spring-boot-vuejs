package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class Color {
    public int id;
    public String text;
    public boolean hover = false;

    public Color(){
    }

    static public ArrayList<Color> selectColorList(String filter, int flag){
        ArrayList<Color> colorList = new ArrayList<Color>();

        Color first = new Color();
        first.id = 0;
        first.text = "";
        colorList.add(first);

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            String select = "SELECT TOP 100 ID, Name\n" +
                    "  FROM [WEDGE2].[dbo].[ProductColour] WHERE Name LIKE ?\n" +
                    "AND (Flag & ?) = ?\n" +
                    "order by Name";
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1, filter + "%");
            statement.setInt(2, flag);
            statement.setInt(3, flag);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                Color color = new Color();
                color.id = Integer.parseInt(resultSet.getString(1));
                color.text = resultSet.getString(2);
                colorList.add(color);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("Color");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        if (colorList.size() > 1)
            colorList.get(1).hover = true;
        else if(colorList.size() == 1)
            colorList.get(0).hover = true;
        return colorList;
    }
}
