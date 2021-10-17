package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class Flavour {
    public int id;
    public String text;
    public boolean hover = false;

    public Flavour() {
    }

    static public ArrayList<Flavour> selectFlavourList(String filter, int flag, boolean isFirstSelect) {
        ArrayList<Flavour> flavourList = new ArrayList<Flavour>();

        Flavour first = new Flavour();
        first.id = 0;
        first.text = "";
        flavourList.add(first);

        if (isFirstSelect && !filter.equals(""))
            try {
                Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
                String select = "begin\n" +
                        "declare @position int\n" +
                        "declare @start int\n" +
                        "declare @max int\n" +
                        "declare @count int\n" +
                        "declare @extra int\n" +
                        "SET @extra = 0\n" +
                        "set @count = 0\n" +
                        "SET @position = (SELECT top 1 s.Row# FROM (SELECT ROW_NUMBER() OVER(ORDER BY Name ASC) AS Row#, Name, Flag from [WEDGE2].[dbo].[ProductFlavour] " +
                        "where (Flag & ?) = ?)  as s\n" +
                        "where s.Name = ?\n" +
                        "    order by s.Name) \n" +
                        "SET @max = (SELECT COUNT(ID) from [WEDGE2].[dbo].[ProductFlavour] where (Flag & ?) = ?)\n" +
                        "IF @position <= 100 BEGIN\n" +
                        "\tSET @start = 0\n" +
                        "\tSET @count = @position\n" +
                        "\tSET @extra = 0\n" +
                        "END\n" +
                        "else begin\n" +
                        "\tSET @start = @position - 100\n" +
                        "\tSET @count = 100\n" +
                        "\tSET @extra = @position - 100\n" +
                        "end\n" +
                        "IF @max - @position <= 100\n" +
                        "BEGIN\n" +
                        "\tSET @start = @start - 100 + (@max - @position)\n" +
                        "END " +
                        " IF @start > 0 " +
                        " BEGIN" +
                        " SELECT [ID]\n" +
                        "      ,[Name]\n" +
                        "      ,[Flag]\n" +
                        "  FROM [WEDGE2].[dbo].[ProductFlavour]\n" +
                        "  WHERE (Flag & ?) = ?  order by Name\n" +
                        "  OFFSET @start ROWS\n" +
                        "  FETCH NEXT 200 ROWS ONLY\n" +
                        " END " +
                        " ELSE BEGIN " +
                        " SELECT TOP 400 ID, Name\n" +
                        "  FROM [WEDGE2].[dbo].[ProductFlavour] WHERE (Flag & ?) = ?\n" +
                        " order by Name" +
                        " END" +
                        " END;";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setInt(1, flag);
                statement.setInt(2, flag);
                statement.setString(3, filter);
                statement.setInt(4, flag);
                statement.setInt(5, flag);
                statement.setInt(6, flag);
                statement.setInt(7, flag);
                statement.setInt(8, flag);
                statement.setInt(9, flag);

                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Flavour flavour = new Flavour();
                    flavour.id = Integer.parseInt(resultSet.getString(1));
                    flavour.text = resultSet.getString(2);
                    flavourList.add(flavour);
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                ProductController.LOG.info("ProductFlavour");
                ProductController.LOG.info(e.getMessage());
                return null;
            }
        else
            try {
                Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
                String select = "SELECT TOP 100 ID, Name\n" +
                        "  FROM [WEDGE2].[dbo].[ProductFlavour] WHERE Name LIKE ?\n" +
                        "AND (Flag & ?) = ?\n" +
                        "order by Name";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setString(1, filter + "%");
                statement.setInt(2, flag);
                statement.setInt(3, flag);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Flavour flavour = new Flavour();
                    flavour.id = Integer.parseInt(resultSet.getString(1));
                    flavour.text = resultSet.getString(2);
                    flavourList.add(flavour);
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                ProductController.LOG.info("Flavour");
                ProductController.LOG.info(e.getMessage());
                return null;
            }
        if (flavourList.size() > 1)
            flavourList.get(1).hover = true;
        else if (flavourList.size() == 1)
            flavourList.get(0).hover = true;
        return flavourList;
    }
}
