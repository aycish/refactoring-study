package me.jounhee.refactoring._11_primitive_obsession._32_replace_conditional_with_polymorphism.swtiches;

import java.util.List;

public class FullTimeEmployee extends Employee {
    public FullTimeEmployee(String type, List<String> availableProjects) {
        super(availableProjects);
    }

    @Override
    public int vacationHours() {
        return 120;
    }

    @Override
    public boolean canAccessTo(String project) {
        return true;
    }
}
