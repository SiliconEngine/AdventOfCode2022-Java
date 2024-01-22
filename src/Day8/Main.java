package Day8;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.ArrayList;

/**
 * Advent of Code 2022 challenge, Day 8.
 * Link: <a href="https://adventofcode.com/2022/day/8">...</a>
 * <p>
 * Challenge: From forest map calculate visibility distance and "scenic scores".
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    static class Pair {
        String s;
        int n;
        public Pair(String s, int n) {
            this.s = s;
            this.n = n;
        }
    }

    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    /**
     * Part 1: Calculates and prints the number of visible trees in the grid.
     * Visibility is determined by checking each tree's height against other trees
     * in the same row and column. Trees are considered visible if no other tree
     * of equal or greater height blocks them when viewed from the grid's edges.
     */
    public void part1() {
        String filePath = "src/Day8/trees.txt"; // Replace with the path to your file

        ArrayList<String> trees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                trees.add(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Iterate through each tree in the grid to check its visibility
        int numLines = trees.size();
        int visible = 0;
        for (int r = 0; r < numLines; ++r) {
            String row = trees.get(r);
            System.out.println(row);
            for (int c = 0; c < row.length(); ++c) {
                char ht = row.charAt(c);
                System.out.println("Checking r=" + r + ", c=" + c + " ht is " + ht);

                boolean vis = true;
                for (int left = c - 1; left >= 0; --left) {
                    if (row.charAt(left) >= ht) {
                        vis = false;
                        break;
                    }
                }
                if (vis) {
                    ++visible;
                    System.out.println("coord " + r + ", " + c + " is visible from left (" + visible + ")");
                    continue;
                }

                vis = true;
                for (int right = c + 1; right < row.length(); ++right) {
                    if (row.charAt(right) >= ht) {
                        vis = false;
                        break;
                    }
                }
                if (vis) {
                    ++visible;
                    System.out.println("coord " + r + ", " + c + " is visible from right (" + visible + ")");
                    continue;
                }

                vis = true;
                for (int up = r - 1; up >= 0; --up) {
                    if (trees.get(up).charAt(c) >= ht) {
                        vis = false;
                        break;
                    }
                }
                if (vis) {
                    ++visible;
                    System.out.println("coord " + r + ", " + c + " is visible from up (" + visible + ")");
                    continue;
                }

                vis = true;
                for (int down = r + 1; down < numLines; ++down) {
                    if (trees.get(down).charAt(c) >= ht) {
                        vis = false;
                        break;
                    }
                }
                if (vis) {
                    ++visible;
                    System.out.println("coord + " + r + ", " + c + " is visible from down (" + visible + ")");
                    continue;
                }

                System.out.println("coord " + r + ", " + c + " is NOT VISIBLE");
            }

        }

        System.out.println("Total visible is " + visible);
    }

    /**
     * Part 2: Calculates and prints the highest scenic score possible for any tree in the grid.
     * The scenic score is calculated by multiplying the viewing distance in each of the
     * four directions (up, down, left, right). The viewing distance is the count of trees
     * seen before encountering a tree of the same or greater height or reaching the grid edge.
     */
    public void part2() {
        String filePath = "src/Day8/trees.txt"; // Replace with the path to your file

        ArrayList<String> trees = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                trees.add(s);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Pair> scores = new ArrayList<>();

        // Iterate through each tree in the grid to calculate its scenic score
        int numLines = trees.size();
        int visible = 0;
        for (int r = 0; r < numLines; ++r) {
            String row = trees.get(r);
            System.out.println(row);

            for (int c = 0; c < row.length(); ++c) {
                char ht = row.charAt(c);

                int leftCount = 0;
                for (int left = c - 1; left >= 0; --left) {
                    ++leftCount;
                    if (row.charAt(left) >= ht) {
                        break;
                    }
                }

                int rightCount = 0;
                for (int right = c + 1; right < row.length(); ++right) {
                    ++rightCount;
                    if (row.charAt(right) >= ht) {
                        break;
                    }
                }

                int upCount = 0;
                for (int up = r - 1; up >= 0; --up) {
                    ++upCount;
                    if (trees.get(up).charAt(c) >= ht) {
                        break;
                    }
                }

                int downCount = 0;
                for (int down = r + 1; down < numLines; ++down) {
                    ++downCount;
                    if (trees.get(down).charAt(c) >= ht) {
                        break;
                    }
                }

                System.out.println("left = " + leftCount + ", right = " + rightCount + ", up = " + upCount + ", down = " + downCount);
                int score = leftCount * rightCount * upCount * downCount;
                System.out.println("coord " + r + ", " + c + " (" + row.charAt(c) + ") score is " + score);

                String key = Integer.toString(r) + "-" + Integer.toString(c);

                Pair p = new Pair(key, score);
                scores.add(p);
            }
        }

        // Sort the trees based on their scenic scores in ascending order
        scores.sort(new Comparator<Pair>() {
            @Override
            public int compare(Pair a, Pair b) {
                return a.n - b.n;
            }
        });

        for (Pair p : scores) {
            System.out.println("Score: " + p.n + ", key is " + p.s);
        }

    }
}