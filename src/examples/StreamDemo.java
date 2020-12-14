package examples;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.openjdk.jmh.runner.RunnerException;

import examples.person.Person;

public class StreamDemo {

    private static List<String> names = getFileContent("resources/names.txt");
    private static List<String> streets = getFileContent("resources/streets.txt");
    private static List<String> occupations = getFileContent("resources/occupations.txt");
    private static List<Person> persons1000000 = Person.generatePersonList(1000000, names, streets, occupations);

    // https://www.baeldung.com/java-8-collectors
    // https://www.baeldung.com/java-8-streams
    public static void main(String[] args) throws RunnerException, IOException {
        System.out.println("######################");
        System.out.println("step-by-step example:");
        List<Integer> randomInts = Stream.iterate(1, n -> n + 1).limit(1000).collect(Collectors.toList());
        Stream<Integer> evenInts = randomInts.stream().filter(n -> n % 2 == 0); // gets executed in the next line before collect()
        List<Integer> evenIntList = evenInts.collect(Collectors.toList());
        System.out.println(evenIntList);

        System.out.println("######################");
        System.out.println("This can also be chained:");
        // @formatter:off
        List<Integer> evenIntList2 = Stream
                .iterate(1, n -> n+1)
                .limit(1000)
                .filter(n -> n % 2 == 0)
                .collect(Collectors.toList());
        System.out.println(evenIntList2);

        System.out.println("######################");
        System.out.println("married persons between 30 and 60 who work as a teacher and whose names start with 'J':");
        List<Person> marriedPersonsBetween30and60WhoWorkAsTeachersWhoseNamesStartWithJ = persons1000000
                .parallelStream()
                .filter(person -> person.isMarried() && person.getAge() >= 30 && person.getAge() <= 60 && person.getOccupancy().equals("Teacher") && person.getName().startsWith("J"))
                .collect(Collectors.toList());
        System.out.println(marriedPersonsBetween30and60WhoWorkAsTeachersWhoseNamesStartWithJ);
        System.out.println("persons1000000 still has the size: " + persons1000000.size());
        // @formatter:on

        System.out.println("######################");
        System.out.println("detect salaries with parallel stream: ");
        parallelDetectSalariesWithStream();

        System.out.println("######################");
        System.out.println("detect salaries without stream but with lamdas: ");
        parallelDetectSalariesWithoutStreamWithLamdas();

        System.out.println("######################");
        System.out.println("detect salaries without stream and without lamdas: ");
        parallelDetectSalariesWithoutStreamWithoutLamdas();
    }

    private static void parallelDetectSalariesWithStream() {

        // @formatter:off
        List<Double> salaries = persons1000000
                .parallelStream()
                .filter(person -> person.getName().startsWith("H") && person.getSalary() > 5000 && person.getSalary() < 5500)
                .map(person -> new Double(person.getSalary()))
                .sorted((s1, s2) -> s1.compareTo(s2))
                .collect(Collectors.toList());
        System.out.println("All salaries of all persons whose names start with the letter 'H' ordered ascending: \n" + salaries);
        // @formatter:on
    }

    private static void parallelDetectSalariesWithoutStreamWithLamdas() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        BlockingQueue<Double> blockingSalaries = new LinkedBlockingQueue<>();
        List<Future<?>> futures = new LinkedList<>();
        System.out.println(persons1000000.size());
        futures.add(executor.submit(() -> blockingSalaries.addAll(retrieveSalaries(persons1000000.subList(0, 249999)))));
        futures.add(executor.submit(() -> blockingSalaries.addAll(retrieveSalaries(persons1000000.subList(250000, 499999)))));
        futures.add(executor.submit(() -> blockingSalaries.addAll(retrieveSalaries(persons1000000.subList(500000, 749999)))));
        futures.add(executor.submit(() -> blockingSalaries.addAll(retrieveSalaries(persons1000000.subList(750000, 1000000)))));
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        List<Double> salaries = new ArrayList<>(blockingSalaries);

        salaries.sort((s1, s2) -> s1.compareTo(s2));
        System.out.println("All salaries of all persons whose names start with the letter 'H' ordered ascending: \n" + salaries);
        executor.shutdown();
    }

    private static void parallelDetectSalariesWithoutStreamWithoutLamdas() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        List<Future<?>> futures = new LinkedList<>();
        BlockingQueue<Double> blockingSalaries = new LinkedBlockingQueue<>();
        futures.add(executor.submit(new Runnable() {
            @Override
            public void run() {
                blockingSalaries.addAll(retrieveSalaries(persons1000000.subList(0, 249999)));
            }
        }));
        futures.add(executor.submit(new Runnable() {
            @Override
            public void run() {
                blockingSalaries.addAll(retrieveSalaries(persons1000000.subList(250000, 499999)));
            }
        }));
        futures.add(executor.submit(new Runnable() {
            @Override
            public void run() {
                blockingSalaries.addAll(retrieveSalaries(persons1000000.subList(500000, 749999)));
            }
        }));
        futures.add(executor.submit(new Runnable() {
            @Override
            public void run() {
                blockingSalaries.addAll(retrieveSalaries(persons1000000.subList(750000, 1000000)));
            }
        }));
        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        List<Double> salaries = new ArrayList<>(blockingSalaries);

        salaries.sort((s1, s2) -> s1.compareTo(s2));
        System.out.println("All salaries of all persons whose names start with the letter 'H' ordered ascending: \n" + salaries);
        executor.shutdown();
    }

    private static List<Double> retrieveSalaries(List<Person> persons) {
        List<Double> salaries = new ArrayList<>();
        for (Person person : persons) {
            Double salary = person.getSalary();
            if (person.getName().startsWith("H") && salary > 5000 && salary < 5500) {
                salaries.add(salary);
            }
        }

        return salaries;
    }

    public static List<Person> filterBySalaryWithLoop(List<Person> persons, int threshold) {
        List<Person> result = new ArrayList<>();
        for (Person person : persons) {
            if (person.getSalary() < threshold) {
                result.add(person);
            }
        }
        return result;
    }

    public static List<Person> filterBySalarySequential(List<Person> persons, int threshold) {
        return persons.stream().filter(person -> person.getSalary() < threshold).collect(Collectors.toList());
    }

    public static List<Person> filterBySalaryParallel(List<Person> persons, int threshold) {
        return persons.parallelStream().filter(person -> person.getSalary() < threshold).parallel().collect(Collectors.toList());
    }

    public static HashMap<Integer, List<Person>> mapToAgeWithLoop(List<Person> persons) {
        HashMap<Integer, List<Person>> result = new HashMap<>();
        for (Person person : persons) {
            int age = person.getAge();
            if (result.containsKey(age)) {
                result.get(age).add(person);
            } else {
                result.put(age, new ArrayList<Person>());
                result.get(age).add(person);
            }
        }
        return result;
    }

    public static Map<Integer, List<Person>> mapToAgeSequential(List<Person> persons) {

        return persons.stream().collect(Collectors.groupingBy(person -> person.getAge(), HashMap::new, Collectors.toList()));
    }

    public static Map<Integer, List<Person>> mapToAgeParallel(List<Person> persons) {
        return persons.parallelStream().collect(Collectors.groupingBy(person -> person.getAge(), HashMap::new, Collectors.toList()));
    }

    /**
     * Retrieves file content using java 8 Streams and returns a list of String using Stream Collectors. Each list entry is corresponding to a line in the file.
     * @param fileName
     * @return
     */
    private static List<String> getFileContent(String fileName) {
        try {
            return Files.lines(Paths.get(fileName)).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
