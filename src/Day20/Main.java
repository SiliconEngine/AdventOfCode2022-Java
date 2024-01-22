package Day20;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Advent of Code 2022 challenge, Day 20.
 * Link: <a href="https://adventofcode.com/2022/day/20">...</a>
 * <p>
 * Challenge: Decrypt "grove positioning system" that produces grove coordinates.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    static class Element {
        Element next;
        Element prev;
        long pos;
        long value;
    };

    void dsp(Element head) {
        Element current = head;
        boolean first = true;
        //int badcount = 20;
        do {
            if (! first) {
                System.out.print(", ");
            }
            first = false;
            //System.out.println(current.pos + "/" + current.value;
            System.out.print(current.value);

            current = current.next;

            //if (--badcount == 0) {
            //System.out.println("BAD");
            //exit(0);
            //}

        } while (current != head);
        System.out.println();
    }

    void dsprev(Element head) {
        Element current = head;
        boolean first = true;
        do {
            if (! first) {
                System.out.print(", ");
            }
            first = false;
            //System.out.println(current.pos + "/" + current.value);
            System.out.print(current.value);

            current = current.prev;
        } while (current != head);
        System.out.println();
    }

    // Method to find an element by its position in the circular list.
    // Iterates through the list until it finds the element with the given index.
    // If the element is not found, the program exits with an error message. *
    Element find(Element head, int idx) {
        Element current = head;
        do {
            if (current.pos == idx)
                return current;
            current = current.next;
        } while (current != head);

        System.out.println("Didn't find index " + idx);
        System.exit(0);
        return null;
    }

    /**
     * Part 1: Decode the list of numbers representing encrypted coordinates.
     * The decoding is done by 'mixing' the numbers, moving each number forward
     * or backward in the list a number of positions equal to its value. The list
     * is treated as circular. After the mixing, coordinates are obtained by
     * checking values at specific positions relative to the value 0.
     * <p>
     * The sum of the coordinates at positions 1000th, 2000th, and 3000th after
     * the value 0 is calculated and outputted as the final result.
     */
    public void part1() {
        String filePath = "src/Day20/numbers.txt";
        Element head = null;

        int count = 0;
        Element current = null;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                int n = Integer.parseInt(s);

                Element e = new Element();
                e.pos = count++;
                e.value = n;
                if (head == null) {
                    head = e;
                    e.next = e;
                    e.prev = e;
                    current = head;
                }
                else {
                    e.prev = current;
                    e.next = current.next;
                    current.next.prev = e;
                    current.next = e;

                    current = e;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Loop through each element in the list based on its original order.
        // For each element, calculate the number of positions to move based on its value.
        // Move the element forward or backward in the list accordingly.
        for (int idx = 0; idx < count; ++idx) {
            current = find(head, idx);
            long n = current.value;
            System.out.println(System.lineSeparator() + "Move value " + n);;
            n = n % (count-1);

            if (n == 0) {
                System.out.println("SKIP ZERO");
                continue;
            }

            Element move = current;
            if (n < 0) {
                while (n++ != 0) {
                    move = move.prev;
                }
                System.out.println("    NEG: Move to node value " + move.value);

                // Disconnect
                current.prev.next = current.next;
                current.next.prev = current.prev;

                // current . move
                current.next = move;
                current.prev = move.prev;
                move.prev.next = current;
                move.prev = current;

            }
            else {
                while (n-- != 0) {
                    move = move.next;
                }
                System.out.println("    POS: Move to node value " + move.value);

                // Disconnect
                current.prev.next = current.next;
                current.next.prev = current.prev;

                // move . current
                current.prev = move;
                current.next = move.next;
                move.next.prev = current;
                move.next = current;
            }
            //dsp(head);
        }

        Element zeroent = head;
        do {
            if (zeroent.value == 0)
                break;
            zeroent = zeroent.next;
        } while (zeroent != head);

        int p1000 = 1000 % count;
        int p2000 = 2000 % count;
        int p3000 = 3000 % count;

        BiFunction<Element, Integer, Element> find = (e, counter) -> {
            while (counter-- > 0) {
                e = e.next;
            }
            return e;
        };

        Element e;
        e = find.apply(zeroent, p1000);
        int n1000 = (int)e.value;

        e = find.apply(zeroent, p2000);
        int n2000 = (int)e.value;

        e = find.apply(zeroent, p3000);
        int n3000 = (int)e.value;

        System.out.println("n1000: " + n1000);
        System.out.println("n2000: " + n2000);
        System.out.println("n3000: " + n3000);

        int sum = n1000 + n2000 + n3000;
        System.out.println("Sum is " + sum);
    }

    /**
     * Part 2: Similar to part1, but with additional steps in the decryption process.
     * Each number is first multiplied by a decryption key before the mixing
     * process. The mixing is then performed ten times instead of once. The
     * coordinates are again obtained by checking values at specific positions
     * relative to the value 0, and the sum of these coordinates is outputted
     * as the final result.
     */
    public void part2() {
        String filePath = "src/Day20/numbers.txt";
        Element head = null;

        int count = 0;
        Element current = null;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                long n = Integer.parseInt(s);
                n *= 811589153L;

                Element e = new Element();
                e.pos = count++;
                e.value = n;
                if (head == null) {
                    head = e;
                    e.next = e;
                    e.prev = e;
                    current = head;
                }
                else {
                    e.prev = current;
                    e.next = current.next;
                    current.next.prev = e;
                    current.next = e;

                    current = e;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // For each round of mixing, apply the decryption key to each element,
        // then perform the mixing process similar to part1.
        // This process is repeated for a total of 10 rounds.
        for (int round = 0; round < 10; ++round) {
            System.out.println("=================================" + System.lineSeparator() + "ROUND " + round);

            for (int idx = 0; idx < count; ++idx) {
                current = find(head, idx);
                long n = current.value;
                //System.out.println(System.lineSeparator() + "Move value " + n);;
                n = n % (count - 1);

                if (n == 0) {
                    //System.out.println("SKIP ZERO");
                    continue;
                }

                Element move = current;
                if (n < 0) {
                    while (n++ != 0) {
                        move = move.prev;
                    }
                    //System.out.println("    NEG: Move to node value " + move.value);

                    // Disconnect
                    current.prev.next = current.next;
                    current.next.prev = current.prev;

                    // current . move
                    current.next = move;
                    current.prev = move.prev;
                    move.prev.next = current;
                    move.prev = current;

                }
                else {
                    while (n-- != 0) {
                        move = move.next;
                    }
                    //System.out.println("    POS: Move to node value " + move.value);

                    // Disconnect
                    current.prev.next = current.next;
                    current.next.prev = current.prev;

                    // move . current
                    current.prev = move;
                    current.next = move.next;
                    move.next.prev = current;
                    move.next = current;
                }
                //dsp(head);
            }
        }

        Element zeroent = head;
        do {
            if (zeroent.value == 0)
                break;
            zeroent = zeroent.next;
        } while (zeroent != head);

        long p1000 = 1000 % count;
        long p2000 = 2000 % count;
        long p3000 = 3000 % count;

        BiFunction<Element, Integer, Element> finder = (e, counter) -> {
            while (counter-- != 0) {
                e = e.next;
            }
            return e;
        };

        Element e;
        e = finder.apply(zeroent, (int) p1000);
        long n1000 = e.value;

        e = finder.apply(zeroent, (int)p2000);
        long n2000 = e.value;

        e = finder.apply(zeroent, (int)p3000);
        long n3000 = e.value;

        System.out.println("n1000: " + n1000);
        System.out.println("n2000: " + n2000);
        System.out.println("n3000: " + n3000);

        long sum = n1000 + n2000 + n3000;
        System.out.println("Sum is " + sum);
    }
}