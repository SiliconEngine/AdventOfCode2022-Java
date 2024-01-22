package Day21;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Advent of Code 2022 challenge, Day 21.
 * Link: <a href="https://adventofcode.com/2022/day/21">...</a>
 * <p>
 * Challenge: Interpret monkey math operations and pass monkey root's "equality test".
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    // Represents a monkey with a name, a math operation or number, its computed value, and a flag to check if the value is computed
    static class Monkey {
        String name;
        String math;
        long value;
        boolean have;

        // Clone method
        public Monkey clone() {
            Monkey newMonkey = new Monkey();
            newMonkey.name = this.name;
            newMonkey.math = this.math;
            newMonkey.value = this.value;
            newMonkey.have = this.have;
            return newMonkey;
        }
    }

    Map<String, Monkey> monkeys = new HashMap<>();

    // Clones the current state of all monkeys in the map to allow for different scenarios without altering the original state
    Map<String, Monkey> cloneMonkeyMap(Map<String, Monkey> original) {
        Map<String, Monkey> clonedMap = new HashMap<>();
        for (Map.Entry<String, Monkey> entry : original.entrySet()) {
            clonedMap.put(entry.getKey(), entry.getValue().clone());
        }
        return clonedMap;
    }

    // Recursively computes the value of a monkey based on its math operation and the values of other referenced monkeys
    long getValue(String name)
    {
        Monkey m = monkeys.get(name);

        //System.out.println("GET: " + name);
        if (m.have)
            return m.value;

        String[] list = m.math.split(" ");
        String param1 = list[0];
        String op = list[1];
        String param2 = list[2];

        // Perform the appropriate mathematical operation based on the operator: +, -, *, /
        long value1 = getValue(param1);
        long value2 = getValue(param2);
        long newval = switch (op.charAt(0)) {
            case '+' -> value1 + value2;
            case '-' -> value1 - value2;
            case '*' -> value1 * value2;
            case '/' -> value1 / value2;
            default -> 0;
        };

        m.value = newval;
        m.have = true;

        return newval;
    }

    // Compares the computed values of two monkeys for a given human's yelled number, and returns the difference
    long comp(String monkey1, String monkey2, Map<String, Monkey> saveMonkeys, long human) {
        monkeys = cloneMonkeyMap(saveMonkeys);
        Monkey humn = monkeys.get("humn");
        humn.value = human;
        humn.have = true;

        long m1Val = getValue(monkey1);
        long m2Val = getValue(monkey2);

        System.out.println(human + ": numbers are " + m1Val + " - " + m2Val + ", diff = " + Math.abs(m1Val - m2Val));

        //if (m1Val == m2Val) {
        //    System.out.println("FOUND IT: " + human + " num is " + m1Val);
        //    exit(0);
        //}

        return Math.abs(m1Val - m2Val);
    }

    /**
     * Part 2: Uses a gradient ascent approach.
     * This method reads the monkey data from a file and then iteratively adjusts
     * the 'human' monkey's yelled number to minimize the difference between the
     * values yelled by two specified monkeys. The goal is to find the human's number
     * that makes these values equal (or as close as possible).
     * It starts from an initial guess and incrementally adjusts this number in a small range
     * to find the optimal solution.
     */
    public void part2() {
        String filePath = "src/Day21/monkeys.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);

                Monkey m = new Monkey();
                String[] list = s.split(": ");
                m.name = list[0];
                m.math = list[1];
                m.have = false;
                if (m.math.charAt(0) >= '0' && m.math.charAt(0) <= '9') {
                    //System.out.println("math = " + m.math);
                    m.value = Integer.parseInt(m.math);
                    m.have = true;
                }

                monkeys.put(list[0], m);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save the original state of monkeys before modifying them in the gradient ascent algorithm
        Map<String, Monkey> saveMonkeys = cloneMonkeyMap(monkeys);

        Monkey root = monkeys.get("root");
        String[] list = root.math.split(" ");
        String monkey1 = list[0];
        String monkey2 = list[2];

        long current = 3759569926192L;
        long inc = 5;
        long low = current-inc;
        long high = current+inc;

        for (long i = low; i < high; i += 1) {
            //System.out.println(i + ": numbers are " + m1Val + " - " + m2Val + ", diff = " + abs(m1Val - m2Val));

            long diff = comp(monkey1, monkey2, saveMonkeys, i);
            System.out.println(i + ": " + diff);
        }
    }
}