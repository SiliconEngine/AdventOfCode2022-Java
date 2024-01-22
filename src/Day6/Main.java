package Day6;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Advent of Code 2022 challenge, Day 6.
 * Link: <a href="https://adventofcode.com/2022/day/6">...</a>
 * <p>
 * Challenge: Decode communication stream and find start-of-message markers.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        part2();
    }

    static boolean checkDup(String q) {
        Map<Character, Boolean> chk = new HashMap<>();
        System.out.println("Checking: " + q);
        for (char c : q.toCharArray()) {
            if (chk.get(c) != null) {
                System.out.println("true");
                return true;
            }

            chk.put(c, true);
        }

        System.out.println("false");
        return false;
    }

    /**
     * Part 1: Process the data stream to find the first start-of-packet marker, identified
     * as a sequence of 4 characters where all characters are distinct.
     * This method reads from a file character by character, maintaining a rolling sequence of 4 characters,
     * and checks for uniqueness within this sequence. The count of characters processed is returned when
     * a valid start-of-packet marker is found.
     */
    public static void part1() {
        String filePath = "src/Day6/stream.txt"; // Replace with the path to your file

        String s = "", q = "";
        int count = 0;
        try (FileReader reader = new FileReader(filePath)) {
            int inp;
            while ((inp = reader.read()) != -1) {
                char c = (char)inp;
                System.out.print(c + " ");
                ++count;

                q += c;
                if (q.length() > 4) {
                    q = q.substring(1);
                }

                System.out.println("queue: " + q);
                if (q.length() > 3 && q.charAt(0) != q.charAt(1) && q.charAt(0) != q.charAt(2) && q.charAt(0) != q.charAt(3)
                        && q.charAt(1) != q.charAt(2) && q.charAt(1) != q.charAt(3)
                        && q.charAt(2) != q.charAt(3))
                {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("count is " + count);
    }

    /**
     * Part 2: Process the data stream to find the first start-of-message marker, which
     * is identified as a sequence of 14 distinct characters.
     * This method reads from a file character by character, maintaining a rolling sequence of 14 characters,
     * and checks for uniqueness using the checkDup method. The count of characters processed is returned when
     * a valid start-of-message marker is found.
     */
    public static void part2() {
        String filePath = "src/Day6/stream.txt"; // Replace with the path to your file
        String s = "", q = "";
        int count = 0;
        try (FileReader reader = new FileReader(filePath)) {
            int inp;
            while ((inp = reader.read()) != -1) {
                char c = (char)inp;
                System.out.print(c + " ");
                ++count;

                q += c;
                if (q.length() > 14) {
                    q = q.substring(1);
                }

                System.out.println("queue: " + q);
                if (q.length() >= 14 && !checkDup(q)) {
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("count is " + count);
    }
}