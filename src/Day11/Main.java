package Day11;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;
import java.util.regex.*;

/**
 * Advent of Code 2022 challenge, Day 11.
 * Link: <a href="https://adventofcode.com/2022/day/11">...</a>
 * <p>
 * Challenge: Calculate monkey "worry level" based on optimizing monkey chasing strategy.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    void dumpVec(ArrayList<String> vec) {
        for (String t : vec) {
            System.out.print("'" + t + "', ");
        }
        System.out.println();
    }

    void dumpVec(String[] vec) {
        for (String t : vec) {
            System.out.print("'" + t + "', ");
        }
        System.out.println();
    }

    // Mimic C++ stoi, returning integer even if invalid characters follow.
    public static int stoi(String str) {
        // Regular expression to match a valid integer at the beginning of the string
        Pattern pattern = Pattern.compile("^[+-]?(\\d+)");
        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {
            String numberStr = matcher.group();
            return Integer.parseInt(numberStr);
        }
        throw new NumberFormatException();
    }

    static class Monkey {
        ArrayList<Long> items = new ArrayList<>();
        String[] op;
        long div = 0;
        int falseNum = 0;
        int trueNum = 0;
        int inspCount = 0;
    }

    void dumpMonkey(Monkey m) {
        System.out.print("    Items: ");
        for (Long item : m.items) {
            System.out.print(item + ", ");
        }
        System.out.println();
        System.out.println("    inspCount = " + m.inspCount);
    }

    /**
     * Part 1: Simulate the behavior of monkeys throwing items for 20 rounds. Each monkey
     * performs operations on items, checks divisibility, and decides the next monkey to throw to.
     * The method calculates and outputs the two monkeys with the highest inspection counts
     * multiplied together to determine the level of monkey business.
     */
    public void part1() {
        String filePath = "src/Day11/monkey.txt";

        int numRounds = 20;
        ArrayList<Monkey> monkeys = new ArrayList<>();

        int numMonkeys = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                String[] tokens;

                System.out.println(s.substring(7));
                int monkeyNum = stoi(s.substring(7));
                System.out.println("Monkey #" + monkeyNum);
                ++numMonkeys;

                Monkey m = new Monkey();
                monkeys.add(m);
                m.inspCount = 0;

                // Starting items: #, #
                s = br.readLine();
                System.out.println(s);
                tokens = s.substring(18).split(", ");
                dumpVec(tokens);
                ArrayList<Long> items = new ArrayList<>();
                for (String t : tokens) {
                    items.add((long)stoi(t));
                }
                m.items = items;

                // Operation: new = [old|#] [+*] [old|#}
                s = br.readLine();
                System.out.println(s);
                tokens = s.substring(19).split(" ");
                dumpVec(tokens);
                m.op = tokens;

                // Test: divisible by [#]
                s = br.readLine();
                System.out.println(s);
                long div = stoi(s.substring(21));
                System.out.println("div: " + div);
                m.div = div;

                // If true: throw to monkey [#]
                s = br.readLine();
                System.out.println(s);
                tokens = s.split(" ");
                int trueNum = stoi(s.substring(29));
                System.out.println("true: " + trueNum);
                m.trueNum = trueNum;

                // If false: throw to monkey [#]
                s = br.readLine();
                System.out.println(s);
                tokens = s.split(" ");
                int falseNum = stoi(s.substring(30));
                System.out.println("false: " + falseNum);
                m.falseNum = falseNum;

                // blank line
                s = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loop through each round of the monkey's actions
        for (int round = 0; round < numRounds; ++round) {
            for (int i = 0; i < numMonkeys; ++i) {
                Monkey m = monkeys.get(i);
                System.out.println("Monkey #" + i);

                // Process each item held by the monkey
                for (long item : m.items) {
                    System.out.println("  Item " + item);
                    long val;
                    if (m.op[2].equals("old")) {
                        val = item;
                    } else {
                        val = stoi(m.op[2]);
                    }

                    switch (m.op[1].charAt(0)) {
                        case '+':
                            item += val;
                            break;
                        case '*':
                            item *= val;
                            break;
                    }
                    System.out.println("    Item is now " + item + " (" + m.op[1] + " / " + m.op[2] + " / " + val + ")");

                    item /= 3;
                    System.out.println("    Item is now " + item + " (div by 3)");

                    boolean test = (item % m.div) == 0;
                    System.out.println("    test (" + m.div + ") is " + test + " [" + (item % m.div) + "]");

                    if (test) {
                        monkeys.get(m.trueNum).items.add(item);
                    } else {
                        monkeys.get(m.falseNum).items.add(item);
                    }

                    ++m.inspCount;
                }

                m.items.clear();
            }
        }

        class PairInt {
            int first;
            int second;
            public PairInt(int f, int s) { this.first = f; this.second = s; }
        }

        ArrayList<PairInt> counts = new ArrayList<>();
        for (int i = 0; i < numMonkeys; ++i) {
            Monkey m = monkeys.get(i);
            System.out.println();
            System.out.println("Monkey #" + i);
            dumpMonkey(m);
            counts.add(new PairInt(i, m.inspCount));
        }

        // Sort monkeys based on their inspection counts
        counts.sort(new Comparator<PairInt>() {
            @Override
            public int compare(PairInt a, PairInt b) {
                return b.second - a.second;
            }
        });

        System.out.println("end");
        for (PairInt c : counts) {
            System.out.println(c.first + ": " + c.second);
        }
        System.out.println("done");

        int answer = counts.get(0).second * counts.get(1).second;
        System.out.println("answer is " + answer);
    }

    /**
     * Part 2: Similar to part1, but with 10,000 rounds and without dividing the worry level by three
     * after each inspection. It computes and outputs the product of the inspection counts
     * of the two most active monkeys after 10,000 rounds to determine the level of monkey business.
     */
    public void part2() {
        String filePath = "src/Day11/monkey.txt";

        int numRounds = 10000;
        int lcmFact = 1;
        ArrayList<Monkey> monkeys = new ArrayList<>();
        ArrayList<Long> factors = new ArrayList<>();

        int numMonkeys = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                String[] tokens;

                System.out.println(s.substring(7));
                int monkeyNum = stoi(s.substring(7));
                System.out.println("Monkey #" + monkeyNum);
                ++numMonkeys;

                Monkey m = new Monkey();
                monkeys.add(m);
                m.inspCount = 0;

                // Starting items: #, #
                s = br.readLine();
                System.out.println(s);
                tokens = s.substring(18).split(", ");
                dumpVec(tokens);
                ArrayList<Long> items = new ArrayList<>();
                for (String t : tokens) {
                    items.add((long)stoi(t));
                }
                m.items = items;

                // Operation: new = [old|#] [+*] [old|#}
                s = br.readLine();
                System.out.println(s);
                tokens = s.substring(19).split(" ");
                dumpVec(tokens);
                m.op = tokens;

                // Test: divisible by [#]
                s = br.readLine();
                System.out.println(s);
                long div = stoi(s.substring(21));
                factors.add(div);
                lcmFact *= div;
                System.out.println("div: " + div);
                m.div = div;

                // If true: throw to monkey [#]
                s = br.readLine();
                System.out.println(s);
                tokens = s.split(" ");
                int trueNum = stoi(s.substring(29));
                System.out.println("true: " + trueNum);
                m.trueNum = trueNum;

                // If false: throw to monkey [#]
                s = br.readLine();
                System.out.println(s);
                tokens = s.split(" ");
                int falseNum = stoi(s.substring(30));
                System.out.println("false: " + falseNum);
                m.falseNum = falseNum;

                // blank line
                s = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loop through each round of the monkey's actions
        for (int round = 0; round < numRounds; ++round) {
            for (int i = 0; i < numMonkeys; ++i) {
                Monkey m = monkeys.get(i);
                System.out.println("Monkey #" + i);

                // Process each item held by the monkey
                for (long item : m.items) {
                    System.out.println("  Item " + item);
                    long val;
                    if (m.op[2].equals("old")) {
                        val = item;
                    } else {
                        val = stoi(m.op[2]);
                    }

                    switch (m.op[1].charAt(0)) {
                        case '+':
                            item += val;
                            break;
                        case '*':
                            item *= val;
                            break;
                    }

                    item = item % lcmFact;

                    boolean test = (item % m.div) == 0;

                    if (test) {
                        monkeys.get(m.trueNum).items.add(item);
                    } else {
                        monkeys.get(m.falseNum).items.add(item);
                    }

                    ++m.inspCount;
                }

                m.items.clear();
            }
        }

        class PairNum {
            long first;
            long second;
            public PairNum(long f, long s) { this.first = f; this.second = s; }
        }

        ArrayList<PairNum> counts = new ArrayList<>();
        for (int i = 0; i < numMonkeys; ++i) {
            Monkey m = monkeys.get(i);
            System.out.println();
            System.out.println("Monkey #" + i);
            dumpMonkey(m);
            counts.add(new PairNum(i, m.inspCount));
        }

        // Sort monkeys based on their inspection counts
        counts.sort(new Comparator<PairNum>() {
            @Override
            public int compare(PairNum a, PairNum b) {
                return (int)(b.second - a.second);
            }
        });

        System.out.println("end");
        for (PairNum c : counts) {
            System.out.println(c.first + ": " + c.second);
        }
        System.out.println("done");

        long answer = counts.get(0).second * counts.get(1).second;
        System.out.println("answer is " + answer);
    }
}