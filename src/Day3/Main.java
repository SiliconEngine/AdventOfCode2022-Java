package Day3;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Advent of Code 2022 challenge, Day 3.
 * Link: <a href="https://adventofcode.com/2022/day/3">...</a>
 * <p>
 * Challenge: Analyze elf "ruck sacks" and determine badge priorities.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        part2();
    }

    static int cvtToPriority(char c) {
        if (c >= 'a' && c <= 'z') {
            return 1 + c - 'a';
        }
        if (c >= 'A' && c <= 'Z') {
            return 27 + c - 'A';
        }
        return 0;
    }

    /**
     * Part 1: This method reads the content of rucksacks from a file and identifies the item type that
     * appears in both compartments of each rucksack. It calculates the sum of the priorities of
     * these item types.
     */
    public static void part1() {
        String filePath = "src/Day3/rucksack.txt"; // Replace with the path to your file

        int totalPriority = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                int compLen = s.length() / 2;
                String comp1 = s.substring(0, compLen);
                String comp2 = s.substring(compLen);

                for (int i = 0; i < compLen; ++i) {
                    char c = comp1.charAt(i);
                    int pos = comp2.indexOf(c);
                    if (pos >= 0) {
                        int priority = cvtToPriority(c);
                        totalPriority += priority;
                        break;
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Total priority is " + totalPriority);
    }

    /**
     * Part 2: This method reads groups of three lines (representing three Elves' rucksacks) from a file
     * and identifies the badge item type common to all three rucksacks in each group. It calculates the
     * sum of the priorities of these badge item types.
     */
    public static void part2() {
        String filePath = "src/Day3/rucksack.txt"; // Replace with the path to your file
        String s1, s2, s3;

        int totalPriority = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s1 = br.readLine()) != null) {
                s2 = br.readLine();
                s3 = br.readLine();

                int len = s1.length();

                boolean flag = false;
                for (int i = 0; i < len; ++i) {
                    char c = s1.charAt(i);
                    int pos2 = s2.indexOf(c);
                    int pos3 = s3.indexOf(c);
                    if (pos2 >= 0 && pos3 >= 0) {
                        int priority = cvtToPriority(c);
                        totalPriority += priority;
                        flag = true;
                        break;
                    }
                }

                if (! flag) {
                    System.out.println("NOT FOUND");
                    System.exit(0);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Total priority is " + totalPriority);
    }
}