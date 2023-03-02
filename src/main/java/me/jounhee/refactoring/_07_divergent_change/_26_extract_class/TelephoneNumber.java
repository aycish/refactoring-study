package me.jounhee.refactoring._07_divergent_change._26_extract_class;

public class TelephoneNumber {
    String areaCode;
    String number;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}