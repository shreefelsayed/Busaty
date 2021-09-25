package Models;

public class Rides {
    String id = "";
    String uId = "";
    String dId = "";
    String routeId = "";
    String fees = "";
    String date = "";
    String fromId = "";
    String toId = "";

    public Rides() {}

    public Rides(String id, String uId, String dId, String routeId, String fees, String date, String fromId, String toId) {
        this.id = id;
        this.uId = uId;
        this.dId = dId;
        this.routeId = routeId;
        this.fees = fees;
        this.date = date;
        this.fromId = fromId;
        this.toId = toId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getdId() {
        return dId;
    }

    public void setdId(String dId) {
        this.dId = dId;
    }

    public String getRouteId() {
        return routeId;
    }

    public void setRouteId(String routeId) {
        this.routeId = routeId;
    }

    public String getFees() {
        return fees;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFromId() {
        return fromId;
    }

    public void setFromId(String fromId) {
        this.fromId = fromId;
    }

    public String getToId() {
        return toId;
    }

    public void setToId(String toId) {
        this.toId = toId;
    }
}
