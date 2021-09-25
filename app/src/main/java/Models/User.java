package Models;

public class User {
    String name = "";
    String pPic = "";
    String phone = "";
    String email = "";
    String id = "";
    String pass = "";
    String active = "true";
    String type = "User";
    String date = "";
    String online = "false";
    String trackId = "";

    public User() {}

    public User(String name, String pPic, String phone, String email, String id, String pass, String type, String date) {
        this.name = name;
        this.pPic = pPic;
        this.phone = phone;
        this.email = email;
        this.id = id;
        this.pass = pass;
        this.type = type;
        this.date = date;
    }

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
    }

    public String getOnline() {
        return online;
    }

    public void setOnline(String online) {
        this.online = online;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getpPic() {
        return pPic;
    }

    public void setpPic(String pPic) {
        this.pPic = pPic;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
