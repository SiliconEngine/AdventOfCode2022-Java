package Day9;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Advent of Code 2022 challenge, Day 9.
 * Link: <a href="https://adventofcode.com/2022/day/9">...</a>
 * <p>
 * Challenge: Simulate rope motion and calculate number of positions visited at least once.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    /**
     * Part 1: Simulate the movement of a rope with two knots (head and tail)
     * based on a series of given directions and distances. It calculates the number
     * of unique positions that the tail of the rope occupies at least once.
     */
    public void part1() {
        String filePath = "src/Day9/moves.txt"; // Replace with the path to your file

        Map<String, Boolean> check = new HashMap<>();

        int hx = 1, hy = 5, tx = 1, ty = 5;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                String[] tokens = s.split(" ");
                char cmd = tokens[0].charAt(0);
                int dist = Integer.parseInt(tokens[1]);

                for (int i = 0; i < dist; ++i) {
                    switch (cmd) {
                    case 'L':
                        --hx;
                        break;
                    case 'R':
                        ++hx;
                        break;
                    case 'U':
                        --hy;
                        break;
                    case 'D':
                        ++hy;
                        break;
                    default:
                        System.out.println("BAD");
                        System.exit(0);
                    }

                    // Update the tail
                    int dx = Math.abs(hx - tx);
                    int dy = Math.abs(hy - ty);
                    int sx = Integer.compare(hx, tx);
                    int sy = Integer.compare(hy, ty);

                    if (dx > 0 && dy > 1 || dy > 0 && dx > 1) {
                        // Need diagonal move

                        tx += sx;
                        ty += sy;

                    } else if (dx > 1) {
                        // Need x move

                        tx += sx;
                    } else if (dy > 1) {
                        // Need y move

                        ty += sy;
                    }

                    String key = Integer.toString(tx) + " " + Integer.toString(ty);
                    check.put(key, true);

                    System.out.println("    " + i + ": HEAD: [" + hx + ", " + hy + "] TAIL : [" + tx + ", " + ty + "]");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        int size = check.size();
        System.out.println("size is " + size);

    }

    /**
     * part2 - This method extends the rope simulation from part1 to include a rope
     * consisting of ten knots. The head of the rope moves according to a series
     * of commands, and each subsequent knot follows the preceding knot using
     * the same movement rules.
     */
    public void part2() {
        String filePath = "src/Day9/moves.txt"; // Replace with the path to your file

        HashSet<String> check = new HashSet<>();
        int init_x = 30;
        int init_y = 50;

        int hx = init_x;
        int hy = init_y;

        int[] tails_x = new int[9];
        int[] tails_y = new int[9];
        for (int i = 0; i < 9; ++i) {
            tails_x[i] = init_x;
            tails_y[i] = init_y;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                String[] tokens = s.split(" ");
                char cmd = tokens[0].charAt(0);
                int dist = Integer.parseInt(tokens[1]);

                for (int i = 0; i < dist; ++i) {
                    switch (cmd) {
                        case 'L':
                            --hx;
                            break;
                        case 'R':
                            ++hx;
                            break;
                        case 'U':
                            --hy;
                            break;
                        case 'D':
                            ++hy;
                            break;
                        default:
                            System.out.println("BAD");
                            System.exit(0);
                    }

                    int cur_hx = hx;
                    int cur_hy = hy;

                    // Update the tails
                    for (int t = 0; t < 9; ++t) {
                        int tx = tails_x[t];
                        int ty = tails_y[t];

                        int dx = Math.abs(cur_hx - tx);
                        int dy = Math.abs(cur_hy - ty);
                        int sx = Integer.compare(cur_hx, tx);
                        int sy = Integer.compare(cur_hy, ty);

                        if (dx > 0 && dy > 1 || dy > 0 && dx > 1) {
                            // Need diagonal move

                            tx += sx;
                            ty += sy;

                        }
                        else if (dx > 1) {
                            // Need x move

                            tx += sx;
                        }
                        else if (dy > 1) {
                            // Need y move

                            ty += sy;
                        }

                        tails_x[t] = tx;
                        tails_y[t] = ty;

                        cur_hx = tx;
                        cur_hy = ty;
                    }

                    String key = Integer.toString(tails_x[8]) + " " + Integer.toString(tails_y[8]);
                    check.add(key);

                    System.out.println("TAIL WAS: " + key);
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        int size = check.size();
        System.out.println("size is " + size);
    }
}