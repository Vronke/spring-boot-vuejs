package web.analytics.domain.sku;

import web.analytics.controller.ProductController;
import web.analytics.domain.SQL;

import java.sql.*;
import java.util.ArrayList;

public class ClientGroup {
    public int id;
    public String text;
    public boolean hover = false;

    public ClientGroup(){
    }

    static public ArrayList<ClientGroup> selectClientGroupList(String filter, int flag){
        ArrayList<ClientGroup> clientGroupList = new ArrayList<ClientGroup>();

        ClientGroup first = new ClientGroup();
        first.id = 0;
        first.text = "";
        clientGroupList.add(first);

        try {
            Connection connection = DriverManager.getConnection(SQL.CONNECTION_STRING);
            String select = "SELECT TOP 100 ID, Name\n" +
                    "  FROM [WEDGE2].[dbo].[ProductClientGroup] WHERE Name LIKE ?\n" +
                    "AND (Flag & ?) = ?\n" +
                    "order by Name";
            PreparedStatement statement = connection.prepareStatement(select);
            statement.setString(1, filter + "%");
            statement.setInt(2, flag);
            statement.setInt(3, flag);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()){
                ClientGroup clientGroup = new ClientGroup();
                clientGroup.id = Integer.parseInt(resultSet.getString(1));
                clientGroup.text = resultSet.getString(2);
                clientGroupList.add(clientGroup);
            }
        }
        // Handle any errors that may have occurred.
        catch (SQLException e) {
            ProductController.LOG.info("ClientGroup");
            ProductController.LOG.info(e.getMessage());
            return null;
        }
        if (clientGroupList.size() > 1)
            clientGroupList.get(1).hover = true;
        else if(clientGroupList.size() == 1)
            clientGroupList.get(0).hover = true;
        return clientGroupList;
    }
}
