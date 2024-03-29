package me.jounhee.refactoring._11_primitive_obsession._32_replace_conditional_with_polymorphism.swtiches;

import java.util.List;

public abstract class Employee {

    protected List<String> availableProjects;

    public Employee(List<String> availableProjects) {
        this.availableProjects = availableProjects;
    }

    public Employee() {
    }

    public int vacationHours() {
        return 0;
    }

    public boolean canAccessTo(String project) {
        return this.availableProjects.contains(project);
    }
}
