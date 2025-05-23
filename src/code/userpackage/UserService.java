package code.userpackage;

import java.sql.SQLException;
import java.util.ArrayList;

public class UserService {
    private final UserDataHandling userDataHandling;

    public UserService() {
        this.userDataHandling = new UserDataHandling();
    }

    public void createUserTable() {
        userDataHandling.createUserTable();
    }

    public boolean saveUser(User user) throws SQLException {
        return userDataHandling.saveUser(user);
    }

    public ArrayList<User> getAllUsers() {
        return userDataHandling.getUsers();
    }

    public User getUserByUsername(String username) {
        return userDataHandling.getUserByUsername(username);
    }

//    public boolean updateUser(String username, String email, String firstName, String lastName) {
//        return userDataHandling.updateUserInfo(username, email, firstName, lastName);
//    }

    public boolean deleteUser(String username) {
        return userDataHandling.deleteUser(username);
    }
}
