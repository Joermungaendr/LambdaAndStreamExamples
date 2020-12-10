package examples;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Consumer;

public class LambdaDemo {
    // https://www.baeldung.com/java-8-lambda-expressions-tips
    public static void main(String[] args) {

        ArrayList<Integer> ints = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            ints.add(i);
        }
        Consumer<Integer> method1 = (n) -> {
            System.out.println(n);
        };
        Consumer<Integer> method2 = (n) -> System.out.println(n);
        Consumer<Integer> method3 = n -> System.out.println(n);

        // method 1
        System.out.println("method 1:");
        ints.forEach(method1);
        System.out.println();

        // method 2
        System.out.println("method 2:");
        ints.forEach(method2);
        System.out.println();

        // method 3
        System.out.println("method 3:");
        ints.forEach(method3);
        System.out.println();

        // sort descending
        Comparator<Integer> sortingFunction = (i1, i2) -> i2 - i1;
        ints.sort(sortingFunction);
        System.out.println("sorted descending:");
        ints.forEach(method3);
        System.out.println();

        // provide method as parameter
        MethodInterface print = i -> "element " + i;
        System.out.println("method as parameter:");
        printElement("x", print);
        printElement("y", print);
        System.out.println();
    }

    private static void printElement(String str, MethodInterface method) {
        String result = method.run(str);
        System.out.println(result);
    }

    private interface MethodInterface {
        String run(String i);
    }
}
