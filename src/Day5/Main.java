package Day5;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

/**
 * Advent of Code 2022 challenge, Day 5.
 * Link: <a href="https://adventofcode.com/2022/day/5">...</a>
 * <p>
 * Challenge: Simulate cargo crane loading procedure and figure out top of stacks.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        part2();
    }

    static void dumpStack(Vector<Vector<Character>> stacks) {
        int i = 1;
        for (Vector<Character> s : stacks) {
            System.out.print("stack " + i++ + ": ");
            for (Character c : s) {
                System.out.print(" " + c);
            }
            System.out.println();
        }
    }

    /**
     * Part 1: Simulates the cargo crane loading procedure using a CrateMover 9000 model.
     * This method reads the initial stacks configuration and the series of moves from a file.
     * It then simulates these moves under the CrateMover 9000's constraints, where crates
     * are moved individually, potentially altering the order within the stacks.
     */
    public static void part1() {
        String filePath = "src/Day5/crates.txt"; // Replace with the path to your file

        Vector<Vector<Character>> stacks = new Vector<Vector<Character>>();
        for (int i = 0; i < 9; ++i) {
            stacks.add(new Vector<Character>());
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println("'" + s + "', len = " + s.length());

                if (s.length() == 0)
                    break;

                if (s.substring(1, 2).equals("1"))
                    continue;

                s += " ".repeat(36 - s.length());
                for (int i = 0; i < 9; ++i) {
                    int idx = i * 4 + 1;
                    char letter = s.substring(idx, idx+1).charAt(0);
                    if (letter != ' ') {
                        System.out.print(" : " + letter);
                        stacks.get(i).insertElementAt(letter, 0);
                    }
                }
                System.out.println();
            }

            dumpStack(stacks);

            while ((s = br.readLine()) != null) {
                String[] list = s.split(" ");
                int count = Integer.parseInt(list[1]);
                int from = Integer.parseInt(list[3]);
                int to = Integer.parseInt(list[5]);
                System.out.println("MOVE: " + count + " FROM " + from + " TO " + to);
                --from;
                --to;

                for (int i = 0; i < count; ++i) {
                    char crate = stacks.get(from).lastElement();
                    stacks.get(from).removeLast();
                    stacks.get(to).add(crate);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dumpStack(stacks);
    }

    /**
     * Simulates the cargo crane loading procedure using the upgraded CrateMover 9001 model.
     * Similar to part1, this method reads the initial configuration and the series of moves.
     * The CrateMover 9001 can move multiple crates at once, preserving their order.
     */
    public static void part2() {
        String filePath = "src/Day5/crates.txt"; // Replace with the path to your file

        Vector<Vector<Character>> stacks = new Vector<Vector<Character>>();
        for (int i = 0; i < 9; ++i) {
            stacks.add(new Vector<Character>());
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println("'" + s + "', len = " + s.length());

                if (s.length() == 0)
                    break;

                if (s.substring(1, 2).equals("1"))
                    continue;

                s += " ".repeat(36 - s.length());
                for (int i = 0; i < 9; ++i) {
                    int idx = i * 4 + 1;
                    char letter = s.substring(idx, idx+1).charAt(0);
                    if (letter != ' ') {
                        System.out.print(" : " + letter);
                        stacks.get(i).insertElementAt(letter, 0);
                    }
                }
                System.out.println();
            }

            dumpStack(stacks);

            while ((s = br.readLine()) != null) {
                String[] list = s.split(" ");
                int count = Integer.parseInt(list[1]);
                int from = Integer.parseInt(list[3]);
                int to = Integer.parseInt(list[5]);
                System.out.println("MOVE: " + count + " FROM " + from + " TO " + to);
                --from;
                --to;

                Vector<Character> move = new Vector<>();
                for (int i = 0; i < count; ++i) {
                    char crate = stacks.get(from).lastElement();
                    System.out.println("    pop: " + crate);
                    stacks.get(from).removeLast();
                    move.add(crate);
                }

                for (int i = count-1; i >= 0; --i) {
                    char crate = move.get(i);
                    System.out.println("    push: " + crate);
                    stacks.get(to).add(crate);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        dumpStack(stacks);
    }
}