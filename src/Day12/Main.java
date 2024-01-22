package Day12;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.Vector;
import java.util.PriorityQueue;
import java.util.function.BiConsumer;

/**
 * Advent of Code 2022 challenge, Day 12.
 * Link: <a href="https://adventofcode.com/2022/day/12">...</a>
 * <p>
 * Challenge: Navigate map and determine fewest steps to get to location with best comm signal.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    final String BOLD = "\u001b[33m";
    final String UNBOLD = "\u001b[0m";

    void moveCursor(int row, int col) {
        System.out.print("\u001b[" + row + ";" + col + "H");
    }

    static class Hill {
        int row;
        int col;
        char height;
        int dist;
        boolean visited;
    }

    static class HillArray {
        Vector<Vector<Hill>> hills;
        public HillArray(int rows, int cols) {
            hills = new Vector<Vector<Hill>>();
            for (int r = 0; r < rows; ++r) {
                Vector<Hill> newRow = new Vector<>();
                hills.add(newRow);
                for (int c = 0; c < cols; ++c) {
                    Hill hill = new Hill();
                    hill.row = r;
                    hill.col = c;
                    newRow.add(hill);
                }
            }
        }
        public Hill set(int row, int col, char height, int dist, boolean visited) {
            Hill hill = hills.get(row).get(col);
            hill.height = height;
            hill.dist = dist;
            hill.visited = visited;
            return hill;
        }

        public Hill get(int row, int col) {
            return hills.get(row).get(col);
        }
    }

    public static Comparator<Hill> hillcmp = new Comparator<Hill>() {
        @Override
        public int compare (Hill a, Hill b) {
            return a.dist - b.dist;
        }
    };

    void dumpHills(int rows, int cols, HillArray hills) {
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                Hill hill = hills.get(r, c);
                if (hill.visited) {
                    System.out.print(BOLD + hill.height + UNBOLD);
                } else {
                    System.out.print(hill.height);
                }
            }
            System.out.println();
        }
    }

    /**
     * Part 1: This method reads a map of hills from a file and finds the shortest path
     * from a starting point (S) to an ending point (E) through the hills.
     * The pathfinding algorithm used is Dijkstra's algorithm.
     */
    public void part1() {
        String filePath = "src/Day12/hills.txt";

        int srow = 0, scol = 0, erow = 0, ecol = 0;
        Vector<String> heights = new Vector<>();

        int rows = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                heights.add(s);

                int pos = s.indexOf('S');
                if (pos >= 0) {
                    srow = rows;
                    scol = pos;
                }

                pos = s.indexOf('E');
                if (pos >= 0) {
                    erow = rows;
                    ecol = pos;
                }

                ++rows;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        int cols = heights.get(0).length();

        // Initialize the hills array and a priority queue for the hills.
        // Set the distance for the starting hill to 0 and others to a very high value.
        PriorityQueue<Hill> pq = new PriorityQueue<>(hillcmp);
        HillArray hills = new HillArray(rows, cols);
        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                Hill hill = hills.get(r, c);
                hill.dist = (r == srow && c == scol) ? 0 : Integer.MAX_VALUE;
                pq.add(hill);
            }
        }

        dumpHills(rows, cols, hills);

        // Main loop for the pathfinding algorithm.
        // Continuously fetches the hill with the smallest distance from the queue
        // and updates the distances of its adjacent hills.
        int testLoop = 20000;
        while (!pq.isEmpty()) {
            //dumpHills(rows, cols, hills);

            if (--testLoop == 0) {
                System.out.println("queue size is " + pq.size());
                System.out.println("end node is [" + erow + ", " + ecol + "]");
                System.exit(0);
            }

            Hill nextHill = pq.poll();
            int curRow = nextHill.row;
            int curCol = nextHill.col;

            if (curRow == erow && curCol == ecol) {
                System.out.println("REACHED END [" + erow + ", " + ecol + "], dist = " + nextHill.dist);
                System.exit(0);
            }

            char c = heights.get(curRow).charAt(curCol);
            Hill curHill = hills.get(curRow, curCol);
            int curDist = curHill.dist;

            if (curDist > 10000000) {
                Hill endHill = hills.get(erow, ecol);
                moveCursor(45, 0);
                System.out.println("NO PATH? End dist is " + endHill.dist);
                System.exit(0);
            }

            if (c == 'E') {
                System.out.print("PATH WAS: ");
            }
            if (c == 'S') {
                c = 'a';
            }

            curHill.visited = true;
            moveCursor(curRow, curCol);

            class TestDirWork {
                char c;
                int curDist;
                int curRow;
                int curCol;
            }
            TestDirWork work = new TestDirWork();
            work.c = c;
            work.curDist = curDist;
            work.curRow = curRow;
            work.curCol = curCol;

            BiConsumer<Integer, Integer> testDir = (rowOff, colOff) -> {
                int trow = work.curRow + rowOff;
                int tcol = work.curCol + colOff;
                if (trow < 0 || tcol < 0 || tcol >= heights.getFirst().length() || trow >= heights.size())
                    return;

                char tc = heights.get(trow).charAt(tcol);
                if (tc == 'E') {
                    tc = 'z';
                }
                Hill hill = hills.get(trow, tcol);
                //System.out.println("    [" + trow + ", " + tcol + "]: c = " + c + ", tc = " + tc + ", v=" + hill->visited;

                if ((tc <= work.c + 1) && ! hill.visited) {
                    int newDist = work.curDist + 1;

                    //System.out.println("  Valid to check, dist = " + hill->dist + ", new = " + newDist;
                    if (newDist < hill.dist) {
                        pq.remove(hill);
                        hill.dist = newDist;
                        pq.add(hill);
                        //System.out.println(", UPDATING";
                    }
                    //System.out.println();
                }
                //System.out.println();

                return;
            };

            testDir.accept(-1, 0);     // up
            testDir.accept(1, 0);      // down
            testDir.accept(0, -1);     // left
            testDir.accept(0, 1);      // right
        }

    }

    /**
     * Part 2: * This method extends the pathfinding algorithm from part1.
     * It now evaluates multiple starting points and finds the overall shortest path
     * to the end point from any of the starting points.
     */
    public void part2() {
        String filePath = "src/Day12/hills.txt";

        int srow = 0, scol = 0, erow = 0, ecol = 0;
        Vector<String> heights = new Vector<>();
        boolean dsp = false;

        int rows = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                heights.add(s);

                int pos = s.indexOf('S');
                if (pos >= 0) {
                    srow = rows;
                    scol = pos;
                }

                pos = s.indexOf('E');
                if (pos >= 0) {
                    erow = rows;
                    ecol = pos;
                }

                ++rows;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        int cols = heights.get(0).length();

        // Setup the hills array and a list of potential lowest starting points.
        // Initialize distances and visited flags.
        HillArray hills = new HillArray(rows, cols);
        Vector<Hill> lowestList = new Vector<>();

        for (int r = 0; r < rows; ++r) {
            for (int c = 0; c < cols; ++c) {
                int dist = (r == srow && c == scol) ? 0 : 999999999;
                char h = heights.get(r).charAt(c);
                Hill hill = hills.set(r, c, h, dist, false);
                if (h == 'a' || h == 'S') {
                    lowestList.add(hill);
                }
            }
        }

        // Iterate over all starting hills to find the shortest path from any of them.
        // Each iteration resets the hills' distances and visited flags.
        int lowSteps = -1;
        for (Hill startHill : lowestList) {
            PriorityQueue<Hill> pq = new PriorityQueue<>(hillcmp);

            for (int r = 0; r < rows; ++r) {
                for (int c = 0; c < cols; ++c) {
                    Hill hill = hills.get(r, c);
                    hill.dist = (r == srow && c == scol) ? 0 : Integer.MAX_VALUE;
                    hill.visited = false;
                    pq.add(hill);
                }
            }
            srow = startHill.row;
            scol = startHill.col;

            if (dsp) {
                dumpHills(rows, cols, hills);
            }

            // Pathfinding logic for each starting point.
            // Uses a similar approach as in part1 but resets the hills' information for each new start.
            boolean found = false;
            while (!pq.isEmpty()) {
                //dumpHills(rows, cols, hills);

                Hill nextHill = pq.poll();
                int curRow = nextHill.row;
                int curCol = nextHill.col;

                if (curRow == erow && curCol == ecol) {
                    found = true;
                    break;
                }

                char c = heights.get(curRow).charAt(curCol);
                Hill curHill = hills.get(curRow, curCol);
                int curDist = curHill.dist;

                if (curDist > 10000000) {
                    Hill endHill = hills.get(erow, ecol);
                    break;
                }

                if (c == 'E') {
                    System.out.print("PATH WAS: ");
                }
                if (c == 'S') {
                    c = 'a';
                }

                if (dsp) {
                    curHill.visited = true;
                    moveCursor(curRow, curCol);
                    System.out.print(BOLD + curHill.height + UNBOLD);
                }

                class TestDirWork {
                    char c;
                    int curDist;
                    int curRow;
                    int curCol;
                }
                TestDirWork work = new TestDirWork();
                work.c = c;
                work.curDist = curDist;
                work.curRow = curRow;
                work.curCol = curCol;

                BiConsumer<Integer, Integer> testDir = (rowOff, colOff) -> {
                    int trow = work.curRow + rowOff;
                    int tcol = work.curCol + colOff;
                    if (trow < 0 || tcol < 0 || tcol >= heights.get(0).length() || trow >= heights.size())
                        return;

                    char tc = heights.get(trow).charAt(tcol);
                    if (tc == 'E') {
                        tc = 'z';
                    }
                    Hill hill = hills.get(trow, tcol);

                    if ((tc <= work.c + 1) && !hill.visited) {
                        int newDist = work.curDist + 1;

                        //System.out.println("  Valid to check, dist = " + hill->dist + ", new = " + newDist;
                        if (newDist < hill.dist) {
                            pq.remove(hill);
                            hill.dist = newDist;
                            pq.add(hill);
                            //System.out.println(", UPDATING";
                        }
                        //System.out.println();
                    }
                    //System.out.println();

                    return;
                };

                testDir.accept(-1, 0);     // up
                testDir.accept(1, 0);      // down
                testDir.accept(0, -1);     // left
                testDir.accept(0, 1);      // right
            }

            if (!found) {
                System.out.println("NOT FOUND, start = [" + srow + ", " + scol + "]");
            } else {
                Hill endHill = hills.get(erow, ecol);
                System.out.println("REACHED END FOR [" + srow + ", " + scol + "] END = [" + erow + ", " + ecol + "], dist = " + endHill.dist);

                if (lowSteps < 0 || lowSteps > endHill.dist) {
                    lowSteps = endHill.dist;
                }
            }
        }

        System.out.println("Lowest was " + lowSteps);
    }
}