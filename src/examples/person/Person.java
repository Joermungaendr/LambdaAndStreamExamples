package examples.person;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import lombok.Data;

@Data
public class Person {
    private String name;
    private int age;
    private String street;
    private boolean married;
    private String occupancy;
    private double salary;

    public Person(String name, int age, String street, boolean married, String occupancy, double salary) {
        this.name = name;
        this.age = age;
        this.street = street;
        this.married = married;
        this.occupancy = occupancy;
        this.salary = salary;
    }

    public static List<Person> generatePersonList(int size, List<String> names, List<String> streets, List<String> occupations) {
        List<Person> persons = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < size; i++) {
            String name = names.get(r.nextInt(names.size()));
            int age = r.nextInt(100);
            String street = streets.get(r.nextInt(streets.size()));
            boolean married = r.nextBoolean();
            String occupancy = occupations.get(r.nextInt(occupations.size()));
            int salary = r.nextInt(6000);
            Person p = new Person(name, age, street, married, occupancy, salary);
            persons.add(p);
        }

        return persons;
    }

    @Override
    public String toString() {
        // @formatter:off
        return "\n"
                + "{\n"
                + "    name: " + this.getName() + "\n"
                + "    age: " + this.getAge() + "\n"
                + "    street: " + this.getStreet() + "\n"
                + "    married: " + this.isMarried() + "\n"
                + "    occupancy: " + this.getOccupancy() + "\n"
                + "    salary: " + this.getSalary() + "\n"
                + "}\n";
        // @formatter:on
    }

}
