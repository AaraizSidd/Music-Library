package code.userpackage;

//import com.google.gson.annotations.SerializedName;
public class User {

//    @SerializedName("username")
    private String user_username;

//    @SerializedName("password")
    private String user_password;

//    @SerializedName("role")
    private String role;

    private String user_firstname;
    private String user_lastname;
    private String user_email;
    private String user_full_name;
    private String user_datejoined;

    public User(String user_full_name, String user_email, String user_username, String user_datejoined) {
        this.user_full_name = user_full_name;
        this.user_email = user_email;
        this.user_username = user_username;
        this.user_datejoined = user_datejoined;
    }

    public User(String userName, String password, String firstName, String lastName, String email, String datejoined) {
        this.user_username = userName;
        this.user_password = password;
        this.user_firstname = firstName;
        this.user_lastname = lastName;
        this.user_email = email;
        this.user_datejoined = datejoined;
    }

    public User(String userName, String password, String firstName, String lastName, String full_name, String email, String datejoined) {
        this.user_username = userName;
        this.user_password = password;
        this.user_firstname = firstName;
        this.user_lastname = lastName;
        this.user_full_name = full_name;
        this.user_email = email;
        this.user_datejoined = datejoined;
    }

    public User() {
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public String getUser_datejoined() {
        return user_datejoined;
    }

    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }

    public void setUser_datejoined(String user_datejoined) {
        this.user_datejoined = user_datejoined;
    }

    public String getUser_username() {
        return user_username;
    }

    public String getUser_password() {
        return user_password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setUser_username(String user_username) {
        this.user_username = user_username;
    }

    public void setUser_password(String user_password) {
        this.user_password = user_password;
    }

    public String getUser_firstname() {
        return user_firstname;
    }

    public void setUser_firstname(String user_firstname) {
        this.user_firstname = user_firstname;
    }

    public String getUser_lastname() {
        return user_lastname;
    }

    public void setUser_lastname(String user_lastname) {
        this.user_lastname = user_lastname;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_fullname() {
        return user_firstname + " " + user_lastname;
    }

    @Override
    public String toString() {
        return "{ " + "userName = " + user_username + "  |  password = " + user_password + "  |  firstName = " + user_firstname + "  |  lastName = " + user_lastname + "  |  email = " + user_email + " }";
    }

}
