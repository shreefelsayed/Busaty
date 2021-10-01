package Models;

import com.armjld.busaty.Utill.Converter;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class Routes {
    String code = "";
    String date = "";
    String by = "";
    int fees = 0;
    String dir = "normal";

    ArrayList<String> listStopsCode = new ArrayList<>();
    ArrayList<Stops> listStops = new ArrayList<>();

    ArrayList<LatLng> listOne = new ArrayList<>();
    ArrayList<LatLng> listTwo = new ArrayList<>();

    List<LatLng> listPoints = new ArrayList<>();

    Double disDrop;
    Double disPick;

    public Routes() { }

    public Routes(String code, String date, String by, int fees) {
        this.code = code;
        this.date = date;
        this.by = by;
        this.fees = fees;
    }

    public ArrayList<LatLng> getListOne() {
        return listOne;
    }

    public void setListOne(ArrayList<LatLng> listOne) {
        this.listOne = listOne;
    }

    public ArrayList<LatLng> getListTwo() {
        return listTwo;
    }

    public void setListTwo(ArrayList<LatLng> listTwo) {
        this.listTwo = listTwo;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public ArrayList<String> getListStopsCode() {
        return listStopsCode;
    }

    public ArrayList<LatLng> getWayPoints() {
        if(getListOne().size() != 0 && getDir().equals("normal")) {
            return getListOne();
        }

        if(getListTwo().size() != 0  && getDir().equals("reverse")) {
            return getListTwo();
        }

        Converter converter = new Converter();
        return converter.convert(getListStops());
    }

    public void setListStopsCode(ArrayList<String> listStopsCode) {
        this.listStopsCode = listStopsCode;
    }

    public Double getDisDrop() {
        return disDrop;
    }

    public void setDisDrop(Double disDrop) {
        this.disDrop = disDrop;
    }

    public Double getDisPick() {
        return disPick;
    }

    public void setDisPick(Double disPick) {
        this.disPick = disPick;
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

    public List<LatLng> getListPoints() {
        return listPoints;
    }

    public void setListPoints(List<LatLng> listPoints) {
        this.listPoints = listPoints;
    }
}
