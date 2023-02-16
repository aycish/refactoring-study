package me.jounhee.refactoring._06_mutable_data._23_change_reference_to_value;

public class TelephoneNumber {

    private final String areaCode;
    private final String number;

    public TelephoneNumber(String areaCode, String number) {
        this.areaCode = areaCode;
        this.number = number;
    }

    public String areaCode() {
        return areaCode;
    }
    public String number() {
        return number;
    }
}
