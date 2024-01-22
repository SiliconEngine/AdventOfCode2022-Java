package Day4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Advent of Code 2022 challenge, Day 4.
 * Link: <a href="https://adventofcode.com/2022/day/4">...</a>
 * <p>
 * Challenge: Simulate elf "cleaning sections" and determine overlapping assignment pairs.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        part2();
    }

    /**
     * Part 1: Determines how many assignment pairs have one range that fully contains the other.
     * It reads each line from the file, splits it into two ranges for each elf, and checks if one range is fully
     * contained within the other.
     */
    public static void part1() {
        String filePath = "src/Day4/elf_ranges.txt"; // Replace with the path to your file

        int isContained = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                String[] list = s.split("[,-]");
                System.out.println(s);

                int elf1_r1 = Integer.parseInt(list[0]);
                int elf1_r2 = Integer.parseInt(list[1]);
                int elf2_r1 = Integer.parseInt(list[2]);
                int elf2_r2 = Integer.parseInt(list[3]);

                if ((elf1_r1 >= elf2_r1 && elf1_r2 <= elf2_r2) || (elf2_r1 >= elf1_r1 && elf2_r2 <= elf1_r2)) {
                    ++isContained;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Total contained is " + isContained);
    }

    /**
     * Part 2: * Determines the number of pairs that overlap at all.
     * This method reads each line from the file and checks for any overlap between the ranges of the two elves.
     * It uses a more detailed approach to check for any overlap, as opposed to full containment.
     */
    public static void part2() {
        String filePath = "src/Day4/elf_ranges.txt"; // Replace with the path to your file
        String s1, s2, s3;

        int noOverlapCount = 0;
        int overlapCount = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                String[] list = s.split("[,-]");
                System.out.println(s);

                int elf1_r1 = Integer.parseInt(list[0]);
                int elf1_r2 = Integer.parseInt(list[1]);
                int elf2_r1 = Integer.parseInt(list[2]);
                int elf2_r2 = Integer.parseInt(list[3]);

                boolean overlap = false;
                for (int i = elf1_r1; i <= elf1_r2; ++i) {
                    if (i >= elf2_r1 && i <= elf2_r2) {
                        overlap = true;
                    }
                }
                for (int i = elf2_r1; i <= elf2_r2; ++i) {
                    if (i >= elf1_r1 && i <= elf1_r2) {
                        overlap = true;
                    }
                }

                if ((elf1_r2 < elf2_r1 || elf1_r1 > elf2_r2) || (elf2_r2 < elf1_r1 || elf2_r1 > elf1_r2)) {
                    ++noOverlapCount;
                    System.out.println(s + " : " + elf1_r1 + " : " + elf1_r2 + " : " + elf2_r1 + " : " + elf2_r2);
                    if (overlap) {
                        System.out.println(overlap + " BUG, should be overlap");
                        System.exit(0);
                    }
                } else {
                    ++overlapCount;
                    if (! overlap) {
                        System.out.println(overlap + " BUG, should be no overlap");
                        System.exit(0);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("total overlap is " + overlapCount);
        System.out.println("total no overlap is " + noOverlapCount);
    }
}