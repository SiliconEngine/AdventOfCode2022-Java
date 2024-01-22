package Day10;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Advent of Code 2022 challenge, Day 10.
 * Link: <a href="https://adventofcode.com/2022/day/10">...</a>
 * <p>
 * Challenge: Simulate "CRT monitor" by interpreting control signal commands.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    public void setpix(StringBuilder screen, int x, int cycle) {
        int col = cycle % 40;
        if ((x - 1) <= col && col <= (x + 1)) {
            screen.setCharAt(cycle, '#');
        }
    }

    /**
     * Part 1: Calculates and prints the signal strength at specified cycle intervals.
     * The method reads a set of instructions from a file, processes them to update the value
     * of register X, and calculates the signal strength (cycle number multiplied by the value
     * of X) at every cycle. Specific signal strengths at cycles 20, 60, 100, 140, 180, and 220
     * are then summed and printed.
     */
    public void part1() {
        String filePath = "src/Day10/tube.txt";
        ArrayList<Integer> strengths = new ArrayList<>();
        strengths.add(0);

        int cycle = 0;
        int x = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                String[] tokens = s.split(" ");
                String cmd = tokens[0];

                // Calculate and store the signal strength for the current cycle
                if (cmd.equals("noop")) {
                    ++cycle;
                    strengths.add(cycle * x);
                    System.out.println("Cycle " + cycle + ": x = " + x + ", signal = " + (cycle *x));
                } else if (cmd.equals("addx")) {
                    int amt = Integer.parseInt(tokens[1]);

                    ++cycle;
                    strengths.add(cycle * x);
                    System.out.println("Cycle " + cycle + ": x = " + x + ", signal = " + (cycle * x));
                    ++cycle;
                    strengths.add(cycle * x);
                    System.out.println("Cycle " + cycle + ": x = " + x + ", signal = " + (cycle * x));
                    x += amt;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("20=" + strengths.get(20) + ", 60=" + strengths.get(60) + ", 100=" + strengths.get(100) + ", 140=" + strengths.get(140) + ", 180=" + strengths.get(180) + ", 220=" + strengths.get(220));
        int total = strengths.get(20) + strengths.get(60) + strengths.get(100) + strengths.get(140) + strengths.get(180) + strengths.get(220);
        System.out.println("Total is " + total);

    }

    /**
     * Part 2: Simulates a CRT monitor by rendering an image based on CPU instructions.
     * This method reads instructions from a file and updates the position of a sprite
     * on a simulated CRT screen. The sprite's position is determined by the value of
     * register X and is drawn on the screen accordingly.
     */
    public void part2() {
        String filePath = "src/Day10/tube.txt";

        StringBuilder screen = new StringBuilder(String.join("", Collections.nCopies(240, ".")));

        int cycle = 0;
        int x = 1;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                String[] tokens = s.split(" ");
                String cmd = tokens[0];

                // Update the screen pixel based on the command
                if (cmd.equals("noop")) {
                    System.out.println("Cycle " + cycle + ": x = " + x + ", cycle = " + (cycle * x));
                    setpix(screen, x, cycle);
                    ++cycle;

                } else if (cmd.equals("addx")) {
                    int amt = Integer.parseInt(tokens[1]);

                    System.out.println("Cycle " + cycle + ": x = " + x + ", cycle = " + (cycle * x));
                    setpix(screen, x, cycle);
                    ++cycle;

                    System.out.println("Cycle " + cycle + ": x = " + x + ", cycle = " + (cycle * x));
                    setpix(screen, x, cycle);
                    ++cycle;

                    x += amt;
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int line = 0; line < 6; ++line) {
            System.out.println(screen.substring(line * 40, line * 40 + 40));
        }

    }
}