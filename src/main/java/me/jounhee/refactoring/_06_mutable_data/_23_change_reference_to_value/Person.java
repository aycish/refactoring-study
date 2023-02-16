package me.jounhee.refactoring._06_mutable_data._23_change_reference_to_value;

public class Person {

    private TelephoneNumber officeTelephoneNumber;
    public String officeAreaCode() {
        return this.officeTelephoneNumber.areaCode();
    }

    public void officeAreaCode(String areaCode) {
        this.officeTelephoneNumber = new TelephoneNumber(areaCode, officeNumber());
    }

    public String officeNumber() {
        return this.officeTelephoneNumber.number();
    }

    public void officeNumber(String number) {
        this.officeTelephoneNumber = new TelephoneNumber(officeAreaCode(), number);
    }
}
