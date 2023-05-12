package rikkei.academy.service.role;

import rikkei.academy.config.ConnectMySQL;
import rikkei.academy.model.Role;
import rikkei.academy.model.RoleName;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleService implements IRoleService{
    private Connection connection = ConnectMySQL.getConnection();
    private final String FIND_BY_NAME_ROLE = "SELECT * FROM role WHERE name = ?;";

    @Override
    public Role findByName(RoleName name) {
        Role role = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_NAME_ROLE);
            preparedStatement.setString(1, String.valueOf(name));
            ResultSet resultSet = preparedStatement.executeQuery();
            int role_id = 0;
            while (resultSet.next()){
                role_id = resultSet.getInt("id");
                role = new Role(role_id, name);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return role;
    }
}
