package com.crio.xpoll.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.crio.xpoll.model.User;
import com.crio.xpoll.util.DatabaseConnection;

/**
 * Data Access Object (DAO) for managing users in the XPoll application.
 * Provides methods for creating and retrieving user information.
 */
public class UserDAO {

    private final DatabaseConnection databaseConnection;

    /**
     * Constructs a UserDAO with the specified DatabaseConnection.
     *
     * @param databaseConnection The DatabaseConnection to be used for database operations.
     */
    public UserDAO(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    /**
     * Creates a new user with the specified username and password.
     *
     * @param username The username for the new user.
     * @param password The password for the new user.
     * @return A User object representing the created user.
     * @throws SQLException If a database error occurs during user creation.
     */
    public User createUser(String username, String password) throws SQLException {
        String query="INSERT INTO `users`(`username`,`password`) VALUES(?,?)";
        Connection con=null;
        PreparedStatement pstmt=null;
        ResultSet resultSet=null;
        try{
            con=databaseConnection.getConnection();
            pstmt=con.prepareStatement(query,PreparedStatement.RETURN_GENERATED_KEYS);
            pstmt.setString(1,username);
            pstmt.setString(2,password);
            pstmt.executeUpdate();
            resultSet=pstmt.getGeneratedKeys();
            if(resultSet.next()){
                int userId=resultSet.getInt(1);
                return new User(userId,username,password);
            }else{
                 throw new SQLException("Creating user failed!");
            }
        }catch(SQLException e){
            throw e;
        }finally{
            if(resultSet!=null){
                resultSet.close();
            }if(pstmt!=null){
                pstmt.close();
            }if(con!=null){
                con.close();
            }
        }
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return A User object representing the user with the specified ID, or null if no user is found.
     * @throws SQLException If a database error occurs during the retrieval.
     */
    public User getUserById(int userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection connection = databaseConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    return new User(userId, username, password);
                }else {
                    return null;
                }
            }
        }
        // return null;
    }
}