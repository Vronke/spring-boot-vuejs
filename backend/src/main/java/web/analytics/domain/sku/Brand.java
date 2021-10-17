package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class Brand {
    public int id;
    public String text;
    public boolean hover = false;

    public Brand(){
    }

    static public ArrayList<Brand> selectBrandList(String filter, int flag, boolean isFirstSelect){
        ArrayList<Brand> brandList = new ArrayList<Brand>();

        Brand first = new Brand();
        first.id = 0;
        first.text = "";
        brandList.add(first);

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
                        "SET @position = (SELECT top 1 s.Row# FROM (SELECT ROW_NUMBER() OVER(ORDER BY Name ASC) AS Row#, Name, Flag from [WEDGE2].[dbo].[ProductBrand] " +
                        "where (Flag & ?) = ?)  as s\n" +
                        "where s.Name = ?\n" +
                        "    order by s.Name) \n" +
                        "SET @max = (SELECT COUNT(ID) from [WEDGE2].[dbo].[ProductBrand] where (Flag & ?) = ?)\n" +
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
                        "END\n" +
                        "SELECT [ID]\n" +
                        "      ,[Name]\n" +
                        "      ,[Flag]\n" +
                        "  FROM [WEDGE2].[dbo].[ProductBrand]\n" +
                        "  WHERE (Flag & ?) = ?  order by Name\n" +
                        "  OFFSET @start ROWS\n" +
                        "  FETCH NEXT 200 ROWS ONLY\n" +
                        "end;";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setInt(1, flag);
                statement.setInt(2, flag);
                statement.setString(3, filter);
                statement.setInt(4, flag);
                statement.setInt(5, flag);
                statement.setInt(6, flag);
                statement.setInt(7, flag);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()){
                    Brand brand = new Brand();
                    brand.id = Integer.parseInt(resultSet.getString(1));
                    brand.text = resultSet.getString(2);
                    brandList.add(brand);
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                ProductController.LOG.info("Brand");
                ProductController.LOG.info(e.getMessage());
                return null;
            }
        else {
            try {
                Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
                String select = "SELECT TOP 100 ID, Name\n" +
                        "  FROM [WEDGE2].[dbo].[ProductBrand] WHERE Name LIKE ?\n" +
                        "AND (Flag & ?) = ?\n" +
                        "order by Name";
                PreparedStatement statement = connection.prepareStatement(select);
                statement.setString(1, filter + "%");
                statement.setInt(2, flag);
                statement.setInt(3, flag);
                ResultSet resultSet = statement.executeQuery();


                while (resultSet.next()) {
                    Brand brand = new Brand();
                    brand.id = Integer.parseInt(resultSet.getString(1));
                    brand.text = resultSet.getString(2);
                    brandList.add(brand);
                }
            }
            // Handle any errors that may have occurred.
            catch (SQLException e) {
                ProductController.LOG.info("Brand");
                ProductController.LOG.info(e.getMessage());
                return null;
            }
        }
        if (brandList.size() > 1)
            brandList.get(1).hover = true;
        else if(brandList.size() == 1)
            brandList.get(0).hover = true;
        return brandList;
    }
}
