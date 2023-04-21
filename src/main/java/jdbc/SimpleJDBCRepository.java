package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "CREATE TABLE myusers (id bigint PRIMARY KEY AUTO-INCREMENT, firstname varchar(63), lastname varchar(63), age int);";
    private static final String updateUserSQL = "";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?;";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE firstname LIKE CONCAT(?, '%');";
    private static final String findAllUserSQL = "SELECT * FROM myusers;";

    public Long createUser() {
        try (Connection conn = CustomDataSource.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            return stmt.executeLargeUpdate(createUserSQL);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserById(Long userId) {
        try (Connection conn = CustomDataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(findUserByIdSQL)) {
            stmt.setLong(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildUserFromResultSet(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User findUserByName(String userName) {
        try (Connection conn = CustomDataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(findUserByNameSQL)) {
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return buildUserFromResultSet(rs);
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<User> findAllUser() {
        try (Connection conn = CustomDataSource.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery(findAllUserSQL);
            List<User> users = new ArrayList<>();
            while (rs.next()) {
                users.add(buildUserFromResultSet(rs));
            }
            return users;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public User updateUser() {
        return new User();
    }

    private void deleteUser(Long userId) {
        try (Connection conn = CustomDataSource.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(deleteUser)) {
            stmt.setLong(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private User buildUserFromResultSet(ResultSet rs) throws SQLException{
        return User.builder()
                .id(rs.getLong("id"))
                .firstName(rs.getString("firstname"))
                .lastName(rs.getString("lastname"))
                .age(rs.getInt("age"))
                .build();
    }
}
