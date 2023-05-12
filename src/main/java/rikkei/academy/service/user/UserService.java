package rikkei.academy.service.user;

import com.mysql.cj.Session;
import rikkei.academy.config.ConnectMySQL;
import rikkei.academy.model.Role;
import rikkei.academy.model.RoleName;
import rikkei.academy.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserService implements IUserService{
    private Connection connection = ConnectMySQL.getConnection();
    private final String SELECT_ALL_USERNAME = "SELECT username FROM user;";
    private final String SELECT_ALL_EMAIL = "SELECT email FROM user;";
    private final String INSERT_INTO_USER = "INSERT INTO user (name, username,email,password, avatar) VALUES (?,?,?,?,?);";
    private final String INSERT_INTO_USER_ROLE = "INSERT INTO user_role (user_id, role_id) VALUES (?,?);";
    private final String SELECT_USER_LOGIN = "SELECT * FROM user WHERE username = ? AND password = ?;";
    private final String SELECT_ROLE_BY_ID = "select role_id, role.name from role join user_role ur on role.id = ur.role_id where user_id = ?";
    private final String UPDATE_AVATAR = "UPDATE user set avatar=? where id=?;";

    @Override
    public boolean existedByUsername(String username) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_USERNAME);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> usernameList = new ArrayList<>();
            while (resultSet.next()){
                usernameList.add(resultSet.getString("username"));
            }
            for (int i = 0; i < usernameList.size(); i++) {
                if(username.equals(usernameList.get(i))){
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public boolean existedByEmail(String email) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_EMAIL);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<String> emailList = new ArrayList<>();
            while (resultSet.next()){
                emailList.add(resultSet.getString("email"));
            }
            for (int i = 0; i < emailList.size(); i++) {
                if(email.equals(emailList.get(i))){
                    return true;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public void save(User user) {
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_INTO_USER, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getUsername());
            preparedStatement.setString(3, user.getEmail());
            preparedStatement.setString(4, user.getPassword());
            preparedStatement.setString(5, user.getAvatar());
            preparedStatement.executeUpdate();
            int user_id = 0;
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()){
                user_id = resultSet.getInt(1);
            }
            PreparedStatement preparedStatement1 = connection.prepareStatement(INSERT_INTO_USER_ROLE);
            Set<Role> roleSet = user.getRoleSet();
            List<Role> roleList = new ArrayList<>(roleSet);
            List<Integer> listRoleId = new ArrayList<>();
            for (int i = 0; i < roleList.size(); i++) {
                listRoleId.add(roleList.get(i).getId());
            }
            for (int i = 0; i < listRoleId.size(); i++) {
                preparedStatement1.setInt(1, user_id);
                preparedStatement1.setInt(2, listRoleId.get(i));
                preparedStatement1.executeUpdate();
            }
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public User userLogin(String username, String password) {
        User user = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_LOGIN);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String avatar = resultSet.getString("avatar");
                Set<Role> roleSet = findRoleByUserId(id);
                user = new User(id, name, avatar, roleSet);

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return user;
    }

    @Override
    public Set<Role> findRoleByUserId(int user_id) {
        Set<Role> roleSet = new HashSet<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ROLE_BY_ID);
            preparedStatement.setInt(1, user_id);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                int id = resultSet.getInt("role_id");
                RoleName name = RoleName.valueOf(resultSet.getString("name"));
                roleSet.add(new Role(id, name));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return roleSet;
    }

    @Override
    public void updateAvatar(String avatar, int id) {
        try {
            connection.setAutoCommit(false);
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_AVATAR);
            preparedStatement.setString(1, avatar);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
