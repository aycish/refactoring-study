package me.jounhee.refactoring._06_mutable_data._18_split_variable;

public class Rectangle {

    private double perimeter;
    private double area;

    public void updateGeometry(double height, double width) {
        double temp = 2 * (height + width);
        System.out.println("Perimeter: " + temp);
        perimeter = temp;

        temp = height * width;
        System.out.println("Area: " + temp);
        area = temp;
    }

    public void updateGeometryRefactored(double height, double width) {
        double perimeter = 2 * (height + width);
        System.out.println("Perimeter: " + perimeter);
        this.perimeter = perimeter;

        double area = height * width;
        System.out.println("Area: " + area);
        this.area = area;

    }

    public double getPerimeter() {
        return perimeter;
    }

    public double getArea() {
        return area;
    }
}
