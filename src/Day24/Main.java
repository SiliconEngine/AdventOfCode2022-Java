package Day24;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Advent of Code 2022 challenge, Day 24.
 * Link: <a href="https://adventofcode.com/2022/day/24">...</a>
 * <p>
 * Challenge: Navigate through a map avoiding blizzards and calculate fewest number of minutes
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {

    final boolean DEBUG = false;
    final boolean DEBUG_SHOW_MAP = false;
    final int DEBUG_MAX_MIN = 2000;
    final boolean SHOW_PROG = true;

    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    enum Dir { STAY, NORTH, SOUTH, WEST, EAST };

    /**
     * Represents a blizzard in the valley with a position (x, y) and a direction of movement.
     * The direction is one of the enum values: NORTH, SOUTH, EAST, WEST, or STAY.
     */
    static class Blizzard {
        int x;
        int y;
        Dir dir;
        public String toString() {
            return "[" + x + ", " + y + "] " + dir;
        }
    }

    static class PotentialMove {
        int x;
        int y;
        Dir dir;
        public PotentialMove(int x, int y, Dir dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }
    }

    static class MutInteger {
        int n;
        public MutInteger(int n) {
            this.n = n;
        }
    }

    static class PairInt {
        int first;
        int second;
        public PairInt(int first, int second) {
            this.first = first;
            this.second = second;
        }
    }

    static class Valley {
        Map<String, MutInteger> bcount = new HashMap<>();
        ArrayList<Blizzard> blist = new ArrayList<>();
        int min_x;
        int min_y;
        int max_x;
        int max_y;

        public Valley clone() {
            Valley v = new Valley();
            v.bcount = new HashMap<>();
            v.blist = new ArrayList<>();
            v.min_x = min_x;
            v.min_y = min_y;
            v.max_x = max_x;
            v.max_y = max_y;

            v.bcount.putAll(bcount);
            v.blist.addAll(blist);
            return v;
        }

        public String makeKey(Blizzard b) {
            return Integer.toString(b.x) + "," + Integer.toString(b.y);
        }

        public String makeKey(int x, int y) {
            return Integer.toString(x) + "," + Integer.toString(y);
        }

        public void addBlizzard(Blizzard b) {
            blist.add(b);
            bcount.put(makeKey(b), new MutInteger(1));
        }

        public boolean testBlizzard(int x, int y) {
            if (y < 0 || y > max_y || x < 0 || x > max_x)
                return true;

            String key = makeKey(x, y);
            bcount.putIfAbsent(key, new MutInteger(0));
            return bcount.get(key).n > 0;
        }

        public void dumpBlizzards() {
            for (Blizzard b : blist) {
                System.out.print("BLIZZARD: ");
                System.out.println(b);
            }
        }

        public void dumpBCount()
        {
            System.out.println("BCOUNT:");
            for (Map.Entry<String, MutInteger> b : bcount.entrySet()) {
                System.out.println("    " + b.getKey() + ": " + b.getValue());
            }
        }

        public void moveBlizzards() {
            for (Blizzard b : blist) {
                --bcount.get(makeKey(b)).n;

                switch (b.dir) {
                    case WEST:
                        if (--b.x < 1) {
                            b.x = max_x-1;
                        }
                        break;
                    case EAST:
                        if (++b.x >= max_x) {
                            b.x = 1;
                        }
                        break;
                    case NORTH:
                        if (--b.y < 1) {
                            b.y = max_y-1;
                        }
                        break;
                    case SOUTH:
                        if (++b.y >= max_y) {
                            b.y = 1;
                        }
                        break;
                }

                String key = makeKey(b);
                bcount.putIfAbsent(key, new MutInteger(0));
                ++bcount.get(key).n;
            }
        }

        public ArrayList<StringBuilder> makeMap() {
            ArrayList<StringBuilder> m = new ArrayList<>();
            //m.add(String(max_x + 2, '#'));
            for (int y = min_y; y <= max_y; ++y) {
                m.add(new StringBuilder(".".repeat(max_x + 1)));
            }

            for (Blizzard b : blist) {
                char c = switch (b.dir) {
                    case Dir.STAY -> '#';
                    case Dir.WEST -> '<';
                    case Dir.EAST -> '>';
                    case Dir.NORTH -> '^';
                    case Dir.SOUTH -> 'v';
                };

                char curC = m.get(b.y).charAt(b.x);
                if (curC == '.') {
                    m.get(b.y).setCharAt(b.x, c);
                }
                else if (curC >= '0' && curC <= '9') {
                    m.get(b.y).setCharAt(b.x, (char)(curC + 1));
                }
                else {
                    m.get(b.y).setCharAt(b.x, '2');
                }
            }

            //dumpBCount();
            return m;
        }

        public void dumpMap()
        {
            ArrayList<StringBuilder> list = makeMap();
            for (StringBuilder s : list) {
                System.out.println(s);
            }
        }
    }

    static class Entry {
        int x;
        int y;
        int currDest;
        int minute;
        int dist;
        public Entry(int x, int y, int currDest, int minute, int dist) {
            this.x = x;
            this.y = y;
            this.currDest = currDest;
            this.minute = minute;
            this.dist = dist;
        }
    }

    /**
     * Computes the shortest path to navigate through the valley while avoiding blizzards.
     * Implements a breadth-first search (BFS) algorithm to explore all possible paths and
     * selects the one with the minimum number of steps required to reach the destination.
     */
    void compPath(Valley vly, int start_x, int start_y, ArrayList<PairInt> destList) {
        Map<Integer, ArrayList<StringBuilder>> mapList = new HashMap<>();

        // Maps each minute to the corresponding state of the valley, allowing us to track the positions of blizzards over time
        Map<Integer, Valley> valleyList = new HashMap<>();

        valleyList.put(0, vly);
        int min_x = vly.min_x;
        int max_x = vly.max_x;
        int min_y = vly.min_y;
        int max_y = vly.max_y;

        int numDest = destList.size();

        // Queue for BFS. Stores the positions to be explored along with the current time and destination.
        Queue<Entry> q = new LinkedList<>();
        q.add(new Entry(start_x, start_y, 0, 0, 0));

        Map<String, Boolean> visited = new HashMap<>();

        int progMin = 0;
        int progX = 0;
        int progY = 0;
        int maxQ = 0;
        int[] bestList = new int[10];

        int bestTime = -1;
        while (! q.isEmpty()) {
            Entry e = q.peek();
            int x = e.x;
            int y = e.y;
            int currDest = e.currDest;
            int end_x = destList.get(currDest).first;
            int end_y = destList.get(currDest).second;
            int minute = e.minute;
            int nextMin = minute + 1;

            if (DEBUG_MAX_MIN != 0 && minute >= DEBUG_MAX_MIN) {
                System.out.println("TOO MANY MINUTES");
                break;
            }

            Valley currVly = valleyList.get(minute);

            if (SHOW_PROG) {
                if (nextMin > progMin) {
                    System.out.println("Progress minute: " + nextMin);
                    progMin = nextMin;
                }

                if (x > progX) {
                    System.out.println("Progress x: " + x);
                    progX = x;
                }

                if (y > progY) {
                    System.out.println("Progress y: " + y);
                    progY = y;
                }

                if (q.size() > maxQ) {
                    System.out.println("Q SIZE: " + q.size());
                    maxQ = q.size() + 100;
                }
            }

            boolean showDebug = DEBUG;
            boolean showMap = DEBUG_SHOW_MAP;
            //if (nextMin > 18 && currDest != 1) showDebug = 0, showMap = 0;

            if (showDebug) System.out.println("Processing queue: " + currDest + " / " + minute + " [" + x + ", " + y + "] END: [" + end_x + ", " + end_y + "] ");
            if (showMap) currVly.dumpMap();

            // If already worse, we can stop this path
            if (bestTime > 0 && nextMin > bestTime) {
                System.out.println("     Already worse than best time " + bestTime + " (nextMin = " + nextMin + ")");
                q.remove();
                continue;
            }

            // Do end check
            if (x == end_x && y == end_y) {
                int t = e.dist;
                if (bestList[currDest] < 0 || t < bestList[currDest]) {
                    bestList[currDest] = t;
                }

                System.out.println("======" + System.lineSeparator() + "REACHED END for dest " + currDest+1 + " [" + end_x + ", " + end_y + "] bestList = " + bestList[currDest]);
                ++currDest;
                if (currDest == numDest) {
                    System.out.println(" FINAL: dist was " + t);
                    q.remove();
                    if (bestTime < 0 || t < bestTime) {
                        bestTime = t;
                    }
                    continue;
                }
                System.out.println();

                // Reached end of this destination, move to next
                end_x = destList.get(currDest).first;
                end_y = destList.get(currDest).second;
                System.out.println("DEST IS NOW [" + end_x + ", " + end_y + "]");
            }

            if (valleyList.get(nextMin) == null) {
                //System.out.println("    Creating new valley at time " + minute);
                valleyList.put(nextMin, currVly.clone());
                valleyList.get(nextMin).moveBlizzards();
                mapList.put(nextMin, valleyList.get(nextMin).makeMap());
            }
            Valley nextVly = valleyList.get(nextMin);

            if (showMap) nextVly.dumpMap();

            // Build list of potential moves
            int moveCount = 0;
            PotentialMove[] moveList = new PotentialMove[5];

            // SOUTH
            if (y < max_y) {
                if (!nextVly.testBlizzard(x, y + 1)) {
                    moveList[moveCount++] = new PotentialMove(x, y + 1, Dir.SOUTH);
                }
            }
            // EAST
            if (x < max_x) {
                if (!nextVly.testBlizzard(x + 1, y)) {
                    moveList[moveCount++] = new PotentialMove(x + 1, y, Dir.EAST);
                }
            }
            // WEST
            if (x > 1) {
                if (!nextVly.testBlizzard(x - 1, y)) {
                    moveList[moveCount++] = new PotentialMove(x - 1, y, Dir.WEST);
                }
            }
            // NORTH
            if (y > 0) {
                if (!nextVly.testBlizzard(x, y - 1)) {
                    moveList[moveCount++] = new PotentialMove(x, y - 1, Dir.NORTH);
                }
            }

            // STAY
            if (! nextVly.testBlizzard(x, y)) {
                moveList[moveCount++] = new PotentialMove(x, y, Dir.STAY);
            }

            for (int i = 0; i < moveCount; ++i) {
                PotentialMove m = moveList[i];
                if (showDebug) System.out.println("    POTENTIAL MOVE: min = " + nextMin + " [" + m.x + ", " + m.y + "] " + m.dir);
                String key = Integer.toString(m.x) + "-" + Integer.toString(m.y) + "-" + Integer.toString(currDest)
                        + "-" + Integer.toString(nextMin);
                if (visited.get(key) != null) {
                    if (showDebug) System.out.println("        " + key + " VISITED");
                    continue;
                }

                Entry newEnt = new Entry(m.x, m.y, currDest, nextMin, e.dist + 1);
                q.add(newEnt);

                if (nextMin <= 18 || (nextMin > 18 && currDest == 1)) {
                    mapList.get(nextMin).get(m.y).setCharAt(m.x, '*');
                }

                //System.out.println("SET: " + key);
                visited.put(key, true);
            }

            q.remove();
            //if (--testCount == 0) exit(0);
        }

//    for (auto m : mapList) {
//        ArrayList<StringBuilder> list = m.second;
//        System.out.println("Minute:" + m.first);
//        for (String s : list) {
//            System.out.println(s);
//        }
//    }

        System.out.println("Best time was " + bestTime);
    }

    public void part2() {
        String filePath = "src/Day24/map.txt";

        int min_x = 0;
        int max_x = 0;
        int min_y = 0;
        int max_y = 0;
        Valley vly = new Valley();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);

                max_x = s.length() - 1;

                int x = 0;
                for (char c : s.toCharArray()) {
                    Dir dir = null;
                    switch (c) {
                        case '.':
                            break;
                        case '#':
                            dir = Dir.STAY;
                            break;
                        case '<':
                            dir = Dir.WEST;
                            break;
                        case '>':
                            dir = Dir.EAST;
                            break;
                        case '^':
                            dir = Dir.NORTH;
                            break;
                        case 'v':
                            dir = Dir.SOUTH;
                            break;
                    }

                    if (dir != null) {
                        Blizzard b = new Blizzard();
                        b.dir = dir;
                        b.x = x;
                        b.y = max_y;
                        vly.addBlizzard(b);
                    }

                    ++x;
                }
                ++max_y;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        --max_y;
        System.out.println("min_x = " + min_x + ", min_y = " + min_y + ", max_x = " + max_x + ", max_y = " + max_y);

        vly.min_x = min_x;
        vly.max_x = max_x;
        vly.min_y = min_y;
        vly.max_y = max_y;

        vly.dumpMap();

        int start_x = 1;
        int start_y = 0;
        int end_x = max_x-1;
        int end_y = max_y;

        ArrayList<PairInt> destList = new ArrayList<>();
        destList.add(new PairInt(end_x, end_y));
        destList.add(new PairInt(start_x, start_y));
        destList.add(new PairInt(end_x, end_y));

        compPath(vly, start_x, start_y, destList);
    }
}