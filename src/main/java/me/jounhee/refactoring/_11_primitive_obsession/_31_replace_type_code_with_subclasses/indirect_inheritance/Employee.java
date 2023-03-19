package me.jounhee.refactoring._11_primitive_obsession._31_replace_type_code_with_subclasses.indirect_inheritance;


public class Employee {

    private String name;
    private EmployeeType type;

    public Employee(String name, String typeValue) {
        this.name = name;
        this.type = this.employeeType(typeValue);
    }

    private EmployeeType employeeType(String type) {
        return switch (type) {
            case "engineer" -> new Engineer();
            case "manager" -> new Manager();
            case "salesman" -> new Salesman();
            default -> throw new IllegalArgumentException(type);
        };
    }

    public String capitalizedType() {
        return this.type.capitalizedType();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + this.type.toString() + '\'' +
                '}';
    }
}
