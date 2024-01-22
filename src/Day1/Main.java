package Day1;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Advent of Code 2022 challenge, Day 1.
 * Link: <a href="https://adventofcode.com/2022/day/1">...</a>
 * <p>
 * Challenge: Calculate total calories carried by elves.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        parts();
    }

    public static void parts() {
        String filePath = "src/Day1/elf_items.txt"; // Replace with the path to your file
        ArrayList<Integer> elfInv = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            boolean flag = false;
            int total = 0;
            int elfNum = 0;
            do {
                line = br.readLine();
                flag = line != null;

                if (flag && line.length() != 0) {
                    int n = Integer.parseInt(line);
                    total += n;
                    System.out.println(n);
                } else if (total > 0) {
                    ++elfNum;
                    System.out.println("Elf number " + elfNum + " is " + total);
                    elfInv.add(total);
                    total = 0;
                }
            } while (flag);

            Collections.sort(elfInv, Collections.reverseOrder());
            System.out.println("First (solution to part 1): " + elfInv.getFirst());
            System.out.println("Second " + elfInv.get(1));
            System.out.println("Third " + elfInv.get(2));
            System.out.println("Total (solution to part 2): " + (elfInv.get(0) + elfInv.get(1) + elfInv.get(2)));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}