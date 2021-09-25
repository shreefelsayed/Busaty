package Models;

public class Stops {
    String code = "";
    String name = "";
    String lat = "";
    String _long = "";
    String date = "";
    String by = "";
    int numb = 0;

    public Stops() {}

    public Stops(String code, String name, String lat, String _long, String date, String by) {
        this.code = code;
        this.name = name;
        this.lat = lat;
        this._long = _long;
        this.date = date;
        this.by = by;
    }

    public int getNumb() {
        return numb;
    }

    public void setNumb(int numb) {
        this.numb = numb;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String get_long() {
        return _long;
    }

    public void set_long(String _long) {
        this._long = _long;
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
