package Day14;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.ArrayList;

/**
 * Advent of Code 2022 challenge, Day 14.
 * Link: <a href="https://adventofcode.com/2022/day/14">...</a>
 * <p>
 * Challenge: Simulate falling sand and calculate how much sand under scenarios.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    static int sgn(int val) {
        return (0 < val ? 1 : 0) - (val < 0 ? 1 : 0);
    }

    public static int findFirstNotOf(StringBuilder str, String charsToSkip) {
        for (int i = 0; i < str.length(); i++) {
            if (charsToSkip.indexOf(str.charAt(i)) == -1) {
                return i; // Found a character not in charsToSkip
            }
        }
        return -1; // Not found
    }

    void dspGrid(ArrayList<StringBuilder> grid) {
        int min_x = 9999;
        int max_x = 0;
        for (StringBuilder s : grid) {
            int p = findFirstNotOf(s,".");
            if (p > 0) {
                if (p < min_x)
                    min_x = p;
            }

            p = findFirstNotOf(s, ".");
            if (p != 999 && p >= 0) {
                if (p > max_x)
                    max_x = p;
            }
        }

        min_x -= 5;
        if (min_x < 0) min_x = 0;
        max_x += 5;
        if (max_x > 999) max_x = 999;

        for (StringBuilder s : grid) {
            System.out.println(s.substring(min_x, max_x));
        }
    }

    /**
     * Part 1: Simulate sand falling in a cave system until all sand falls into the abyss.
     * <p>
     * This method initializes a grid based on input data representing cave structures.
     * It then simulates the movement of sand, starting from a fixed point, and tracks
     * how the sand falls through the grid. The simulation continues until sand no longer
     * comes to rest within the grid boundaries, indicating it has fallen into the abyss.
     * The method finally outputs the count of sand units that came to rest.
     */
    public void part1() {
        String filePath = "src/Day14/caves.txt";

        // Initialize a grid representing the cave system. Each cell in the grid can either
        // be air ('.'), rock ('#'), or sand ('o').
        ArrayList<StringBuilder> grid = new ArrayList<>();
        grid.add(new StringBuilder(".".repeat(1000)));
        grid.get(0).setCharAt(500, '+');

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);

                String[] list = s.split( " -> ");

                int lastx = -1;
                int lasty = -1;
                for (String p : list) {
                    String[] c = p.split(",");
                    int x = Integer.parseInt(c[0]);
                    int y = Integer.parseInt(c[1]);

                    if (lastx < 0) {
                        lastx = x;
                        lasty = y;
                        continue;
                    }

                    int dx = sgn(x - lastx);
                    int dy = sgn(y - lasty);

                    int tx = lastx;
                    int ty = lasty;
                    System.out.println("MOVE FROM [" + lastx + ", " + lasty + "] TO [" + x + ", " + y + "] dx=" + dx + ", dy=" + dy);
                    for(;;) {
                        while (ty >= grid.size()) {
                            grid.add(new StringBuilder(".".repeat(1000)));
                        }
                        System.out.println("SET: [" + tx + ", " + ty + "] size = " + grid.size());
                        grid.get(ty).setCharAt(tx, '#');

                        if (tx == x && ty == y)
                            break;

                        tx += dx;
                        ty += dy;
                    }

                    lastx = x;
                    lasty = y;
                }
            }

            // Simulate the movement of sand starting from a fixed source point.
            // The sand follows a specific set of rules to move: it tries to fall straight down,
            // then diagonally left, and finally diagonally right. If all paths are blocked, the
            // sand comes to rest. This loop continues for each sand unit until it either comes to
            // rest or falls out of the grid.
            int sandCount = 0;
            for (;;) {
                int sand_x = 500;
                int sand_y = 0;
                ++sandCount;

                while (++sand_y < grid.size()) {
                    System.out.print("TESTING: [" + sand_x + ", " + sand_y + "]: ");

                    if (grid.get(sand_y).charAt(sand_x) == '.') {
                        System.out.println("    PATH BELOW");
                        continue;
                    }

                    --sand_x;
                    if (grid.get(sand_y).charAt(sand_x) == '.') {
                        System.out.println("    PATH LEFT");
                        continue;
                    }

                    sand_x += 2;
                    if (grid.get(sand_y).charAt(sand_x) == '.') {
                        System.out.println("    PATH RIGHT");
                        continue;
                    }

                    --sand_y;
                    --sand_x;
                    System.out.println("BLOCKED");
                    grid.get(sand_y).setCharAt(sand_x, 'o');
                    break;
                }

                System.out.println("    SAND DROP COMPLETE");
                if (sand_y == grid.size()) {
                    System.out.println("    HIT BOTTOM");
                    break;
                }

                //dspGrid(grid);
                //if (sandCount == 22) exit(0);
            }

            dspGrid(grid);
            System.out.println("sand count is " + sandCount + " (answer is +1)");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Part 2: Simulates falling sand with an additional floor until the source is blocked.
     *
     * This method is an extension of the part1 simulation. It includes an extra horizontal line
     * at the bottom, representing the cave floor. The simulation proceeds similar to part1, but
     * here, it continues until the sand blocks the source, effectively stopping further sand flow.
     * The total count of sand units that come to rest before the source is blocked is then output.
     */
    public void part2() {
        String filePath = "src/Day14/caves.txt";

        // Initialize a grid representing the cave system. Each cell in the grid can either
        // be air ('.'), rock ('#'), or sand ('o').
        ArrayList<StringBuilder> grid = new ArrayList<>();
        grid.add(new StringBuilder(".".repeat(1000)));
        grid.get(0).setCharAt(500, '+');

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);

                String[] list = s.split(" -> ");
                int lastx = -1;
                int lasty = -1;
                for (String p : list) {
                    String[] c = p.split(",");
                    int x = Integer.parseInt(c[0]);
                    int y = Integer.parseInt(c[1]);

                    if (lastx < 0) {
                        lastx = x;
                        lasty = y;
                        continue;
                    }

                    int dx = sgn(x - lastx);
                    int dy = sgn(y - lasty);

                    int tx = lastx;
                    int ty = lasty;
                    System.out.println("MOVE FROM [" + lastx + ", " + lasty + "] TO [" + x + ", " + y + "] dx=" + dx + ", dy=" + dy);
                    for(;;) {
                        while (ty >= grid.size()) {
                            grid.add(new StringBuilder(".".repeat(1000)));
                        }
                        System.out.println("SET: [" + tx + ", " + ty + "] size = " + grid.size());
                        grid.get(ty).setCharAt(tx, '#');

                        if (tx == x && ty == y)
                            break;

                        tx += dx;
                        ty += dy;
                    }

                    lastx = x;
                    lasty = y;
                }

            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        grid.add(new StringBuilder(".".repeat(1000)));
        grid.add(new StringBuilder("#".repeat(1000)));

        // Simulate the movement of sand starting from a fixed source point.
        // The sand follows a specific set of rules to move: it tries to fall straight down,
        // then diagonally left, and finally diagonally right. If all paths are blocked, the
        // sand comes to rest. This loop continues for each sand unit until it blocks the source.
        int sandCount = 0;
        for (;;) {
            int sand_x = 500;
            int sand_y = 0;
            ++sandCount;

            for (;;) {
                ++sand_y;
                //System.out.println("TESTING: [" + sand_x + ", " + sand_y + "]: ";

                if (grid.get(sand_y).charAt(sand_x) == '.') {
                    //System.out.println("    PATH BELOW");
                    continue;
                }

                --sand_x;
                if (grid.get(sand_y).charAt(sand_x) == '.') {
                    //System.out.println("    PATH LEFT");
                    continue;
                }

                sand_x += 2;
                if (grid.get(sand_y).charAt(sand_x) == '.') {
                    //System.out.println("    PATH RIGHT");
                    continue;
                }

                --sand_y;
                --sand_x;
                //System.out.println("BLOCKED");
                grid.get(sand_y).setCharAt(sand_x, 'o');
                break;
            }

            System.out.println("    SAND DROP COMPLETE");
            if (sand_y == 0) {
                System.out.println("    HIT TOP");
                break;
            }

            //dspGrid(grid);
            //if (sandCount == 100) exit(0);
        }

        dspGrid(grid);
        System.out.println("sand count is " + sandCount);
    }
}