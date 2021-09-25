package com.armjld.busaty.Utill;

public class Validity {
    public boolean isNumb(String str) { return !str.contains("[a-zA-Z]+"); }

    public boolean isWebSite(String str) { return str.trim().contains("."); }

    public boolean isEmail(String str) { return str.trim().contains("@") && str.trim().contains("."); }

    public boolean isPass(String str) {
        return str.trim().length() > 5;
    }

    public boolean isPhone(String str) {
        return str.trim().length() == 11 && isNumb(str);
    }
}
