package com.example.userservice.jpa;

import com.example.userservice.controller.UserController;
import com.example.userservice.dto.ReportDto;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Repository
public class UserRepository {

    private Connection conn;
    boolean isConnected = false;

    public void connect(String url, String user, String password) {
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(url, user, password);
            isConnected = true;
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean insertUser(String email, String pwd, String name, int reportedCount) {
        try {
            if (!executeQuery("SELECT * FROM user_table WHERE email=\'" + email + "\'").isAfterLast())
                return false;
            ResultSet resultSet = executeQuery("INSERT INTO user_table " +
                    "VALUES(\'" + email + "\',\'" + pwd + "\',\'" + name + "\'," + reportedCount + ")");
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ArrayList<UserEntity> getUsers() {
        try {
            ArrayList<UserEntity> userList = new ArrayList<>();
            ResultSet resulstSet = executeQuery("SELECT * FROM user_table");
            while (!resulstSet.isAfterLast()) {
                String email = resulstSet.getString("email");
                String pwd = resulstSet.getString("pwd");
                String name = resulstSet.getString("user_name");
                int reportedCount = resulstSet.getInt("reported_count");
                userList.add(new UserEntity(email, pwd, name, reportedCount));
                resulstSet.next();
            }
            return userList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public UserEntity getUser(String email) {
        try {
            ResultSet resulstSet = executeQuery("SELECT * FROM user_table WHERE email=\'" + email + "\'");
            if (!resulstSet.isAfterLast()) {
                String pwd = resulstSet.getString("pwd");
                String name = resulstSet.getString("user_name");
                int reportedCount = resulstSet.getInt("reported_count");
                return new UserEntity(email, pwd, name, reportedCount);
            } else return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateUser(String email, String name) {
        executeQuery("UPDATE user_table SET user_name = \'" + name + "\' WHERE email= \'" + email + "\'");
    }

    public void deleteUser(String email) {
        executeQuery("DELETE user_table WHERE email = \'" + email + "\'");
    }

    public void insertReport(String reporter, String reportee) {
        try {
            executeQuery("INSERT INTO report VALUES(\'" + reporter + "\',\'" + reportee + "\')");
            int reportedCount = executeQuery("SELECT * FROM user_table WHERE email=\'" + reportee + "\'").getInt("reported_count");
            executeQuery("UPDATE user_table SET reported_count = " + (reportedCount + 1) + " WHERE email= \'" + reportee + "\'");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public boolean hasReport(String reporter, String reportee) {
        ResultSet resulstSet = executeQuery("SELECT * FROM report WHERE reporter=\'" + reporter + "\' and reportee=\'" + reportee + "\'");
        try {
            return !resulstSet.isAfterLast();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public ResultSet executeQuery(String sql) {
        try {
            if (!isConnected) connect(UserController.db_url, UserController.db_user, UserController.db_password);
            ResultSet resulstSet = conn.createStatement().executeQuery(sql);
            resulstSet.next();
            return resulstSet;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


}
