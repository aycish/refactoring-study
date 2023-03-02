package me.jounhee.refactoring._07_divergent_change._26_extract_class;

public class Person {

    private final TelephoneNumber telephoneNumber = new TelephoneNumber();
    private String name;

    public String telephoneNumber() {
        return this.telephoneNumber.getAreaCode() + " " + this.telephoneNumber.getNumber();
    }

    public TelephoneNumber getTelephoneNumber() {
        return telephoneNumber;
    }

    public String name() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
