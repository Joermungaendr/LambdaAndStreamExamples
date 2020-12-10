package benchmark;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import examples.StreamDemo;
import examples.person.Person;

public class BenchmarkClass {

    @State(Scope.Benchmark)
    public static class ExecutionPlan {

        @Param({"0", "1", "2", "3", "4"})
        public int index;

        public List<String> names;
        public List<String> streets;
        public List<String> occupations;
        public List<List<Person>> persons = new ArrayList<>(5);

        @Setup(Level.Invocation)
        public void setUp() {
            names = getFileContent("resources/names.txt");
            streets = getFileContent("resources/streets.txt");
            occupations = getFileContent("resources/occupations.txt");
            persons.add(Person.generatePersonList(100, names, streets, occupations));
            persons.add(Person.generatePersonList(1000, names, streets, occupations));
            persons.add(Person.generatePersonList(10000, names, streets, occupations));
            persons.add(Person.generatePersonList(100000, names, streets, occupations));
            persons.add(Person.generatePersonList(1000000, names, streets, occupations));
        }

        /**
         * Retrieves file content using java 8 Streams and returns a list of String using Stream Collectors. Each list entry is corresponding to a line in the
         * file.
         * @param fileName
         * @return
         */
        private List<String> getFileContent(String fileName) {
            try {
                return Files.lines(Paths.get(fileName)).collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 5)
    public void benchmarkfilterBySalaryWithLoop(ExecutionPlan plan) {
        int threshold = 3000;
        List<Person> persons = plan.persons.get(plan.index);
        StreamDemo.filterBySalaryWithLoop(persons, threshold);
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 5)
    public void benchmarkfilterBySalarySequential(ExecutionPlan plan) {
        int threshold = 3000;
        List<Person> persons = plan.persons.get(plan.index);
        StreamDemo.filterBySalarySequential(persons, threshold);
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 5)
    public void benchmarkfilterBySalaryParallel(ExecutionPlan plan) {
        int threshold = 3000;
        List<Person> persons = plan.persons.get(plan.index);
        StreamDemo.filterBySalaryParallel(persons, threshold);
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 5)
    public void benchmarkMapToAgeWithLoop(ExecutionPlan plan) {
        List<Person> persons = plan.persons.get(plan.index);
        StreamDemo.mapToAgeWithLoop(persons);
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 5)
    public void benchmarkMapToAgeSequential(ExecutionPlan plan) {
        List<Person> persons = plan.persons.get(plan.index);
        StreamDemo.mapToAgeSequential(persons);
    }

    @Fork(value = 1, warmups = 1)
    @Benchmark
    @OutputTimeUnit(TimeUnit.NANOSECONDS)
    @BenchmarkMode(Mode.AverageTime)
    @Warmup(iterations = 5)
    public void benchmarkMapToAgeParallel(ExecutionPlan plan) {
        List<Person> persons = plan.persons.get(plan.index);
        StreamDemo.mapToAgeParallel(persons);
    }
}
