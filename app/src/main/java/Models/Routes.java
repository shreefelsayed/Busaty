package Models;

import java.util.ArrayList;

public class Routes {
    String code = "";
    String date = "";
    String by = "";
    int fees = 0;
    ArrayList<String> listStopsCode = new ArrayList<>();
    ArrayList<Stops> listStops = new ArrayList<>();

    public Routes() { }

    public Routes(String code, String date, String by, int fees) {
        this.code = code;
        this.date = date;
        this.by = by;
        this.fees = fees;
    }

    public ArrayList<String> getListStopsCode() {
        return listStopsCode;
    }

    public void setListStopsCode(ArrayList<String> listStopsCode) {
        this.listStopsCode = listStopsCode;
    }

    public ArrayList<Stops> getListStops() {
        return listStops;
    }

    public void setListStops(ArrayList<Stops> listStops) {
        this.listStops = listStops;
    }

    public int getFees() {
        return fees;
    }

    public void setFees(int fees) {
        this.fees = fees;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }
}
