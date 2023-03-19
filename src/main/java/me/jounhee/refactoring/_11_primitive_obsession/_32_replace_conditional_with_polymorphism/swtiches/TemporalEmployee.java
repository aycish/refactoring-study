package me.jounhee.refactoring._11_primitive_obsession._32_replace_conditional_with_polymorphism.swtiches;

import java.util.List;

public class TemporalEmployee extends Employee {
    public TemporalEmployee(String type, List<String> availableProjects) {
        super(type, availableProjects);
    }

    @Override
    public int vacationHours() {
        return 32;
    }
}
