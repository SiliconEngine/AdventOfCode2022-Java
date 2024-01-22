package Day23;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * Advent of Code 2022 challenge, Day 23.
 * Link: <a href="https://adventofcode.com/2022/day/23">...</a>
 * <p>
 * Challenge: Simulate the Elves' travel process and find the round where no elf moves.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    // A wrapper class to allow mutation of integers (used in map structures).
    static class MutInteger {
        int n;
        public MutInteger(int n) {
            this.n =n;
        }
    }

    // Represents an elf with its position and proposed movement.
    static class Elf {
        int x;
        int y;
        boolean moving;
        int propX;
        int propY;
        public String toString() {
            return "[" + x + ", " + y + "]";
        }
    };

    enum Dir { NORTH, SOUTH, WEST, EAST };

    String makeKey(int x, int y) {
        return Integer.toString(x) + "," + Integer.toString(y);
    }

    int countScore(ArrayList<Elf> elves) {
        int min_x = 9999999, min_y = 9999999, max_x = -9999999, max_y = -9999999;

        for (Elf e : elves) {
            if (e.x < min_x)
                min_x = e.x;
            if (e.x > max_x)
                max_x = e.x;
            if (e.y < min_y)
                min_y = e.y;
            if (e.y > max_y)
                max_y = e.y;
        }
        System.out.println("min_x = " + min_x + ", min_y = " + min_y + ", max_x = " + max_x + ", max_y = " + max_y);

        return (max_x - min_x + 1) * (max_y - min_y + 1) - elves.size();
    }


    void dsp(ArrayList<Elf> elves) {
        System.out.println("MAP:");
        int min_x = 9999999, min_y = 9999999, max_x = -9999999, max_y = -9999999;

        for (Elf e : elves) {
            if (e.x < min_x)
                min_x = e.x;
            if (e.x > max_x)
                max_x = e.x;
            if (e.y < min_y)
                min_y = e.y;
            if (e.y > max_y)
                max_y = e.y;
        }
        System.out.println("min_x = " + min_x + ", min_y = " + min_y + ", max_x = " + max_x + ", max_y = " + max_y);

        int offsetX = 0;
        int offsetY = 0;
        if (min_x < 0) {
            offsetX = -min_x;
        }
        if (min_y < 0) {
            offsetY = -min_y;
        }

        if (min_x > 0) {
            min_x = 0;
        }
        if (min_y > 0) {
            min_y = 0;
        }

        ArrayList<StringBuilder> out = new ArrayList<>();
        int len = max_x - min_x + 1;
        for (int i = min_y + offsetY; i <= max_y + offsetY; ++i) {
            out.add(new StringBuilder(".".repeat(len)));
        }

        for (Elf e : elves) {
            out.get(e.y + offsetY).setCharAt(e.x + offsetX, '#');
        }

        for (StringBuilder s : out) {
            System.out.println(s);
        }
    }

    public void part2() {
        String filePath = "src/Day23/elves.txt";

        Dir[] dirs = new Dir[] { Dir.NORTH, Dir.SOUTH, Dir.WEST, Dir.EAST };

        String[] dirnames = new String[4];
        dirnames[Dir.NORTH.ordinal()] = "NORTH";
        dirnames[Dir.SOUTH.ordinal()] = "SOUTH";
        dirnames[Dir.WEST.ordinal()] = "WEST";
        dirnames[Dir.EAST.ordinal()] = "EAST";

        int curDir = 0;
        final long NUM_ROUNDS = 1000000000L;

        ArrayList<Elf> elves = new ArrayList<>();
        Map<String, Elf> pos = new HashMap<>();

        int y = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);

                int x = 0;
                for (char c : s.toCharArray()) {
                    if (c == '#') {
                        Elf e = new Elf();
                        e.x = x;
                        e.y = y;
                        e.moving = false;
                        e.propX = 0;
                        e.propY = 0;
                        elves.add(e);

                        String key = makeKey(x, y);
                        pos.put(key, e);
                    }
                    ++x;
                }

                ++y;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        dsp(elves);

        for (int round = 0; round < NUM_ROUNDS; ++round) {
            Map<String, MutInteger> prop = new HashMap<>();

            // Loop through each elf to determine their proposed movement.
            for (Elf e :elves) {
                String key1 = "", key2 = "", key3 = "";
                //System.out.println("Checking elf " + e);

                // Check all directions to propose a move for the current elf.
                Dir newDir = null;
                int goodCount = 0;
                for (int i = 0; i < 4; ++i) {
                    Dir dir = dirs[(curDir + i) % 4];
                    if (dir == Dir.NORTH) {
                        key1 = makeKey(e.x - 1, e.y - 1);
                        key2 = makeKey(e.x, e.y - 1);
                        key3 = makeKey(e.x + 1, e.y - 1);
                    } else if (dir == Dir.SOUTH) {
                        key1 = makeKey(e.x - 1, e.y + 1);
                        key2 = makeKey(e.x, e.y + 1);
                        key3 = makeKey(e.x + 1, e.y + 1);
                    } else if (dir == Dir.EAST) {
                        key1 = makeKey(e.x + 1, e.y - 1);
                        key2 = makeKey(e.x + 1, e.y);
                        key3 = makeKey(e.x + 1, e.y + 1);
                    } else if (dir == Dir.WEST) {
                        key1 = makeKey(e.x - 1, e.y - 1);
                        key2 = makeKey(e.x - 1, e.y);
                        key3 = makeKey(e.x - 1, e.y + 1);
                    }

                    //System.out.println("    Checking dir " + dirnames[dir]);
                    if (pos.get(key1) == null && pos.get(key2) == null && pos.get(key3) == null) {
                        ++goodCount;
                        //System.out.println("    WAS OK");
                        if (newDir == null)
                            newDir = dir;
                    }
                }

                if (goodCount == 4) {
                    //System.out.println("    All directions good, not moving");
                    newDir = null;
                }

                if (newDir == null) {
                    e.moving = false;
                    //System.out.println("    NO MOVE");
                } else {
                    //System.out.println("    Tentative move to " + dirnames[newDir]);
                    e.moving = true;
                    switch (newDir) {
                        case Dir.NORTH:
                            e.propX = e.x;
                            e.propY = e.y - 1;
                            break;
                        case Dir.SOUTH:
                            e.propX = e.x;
                            e.propY = e.y + 1;
                            break;
                        case Dir.EAST:
                            e.propX = e.x + 1;
                            e.propY = e.y;
                            break;
                        case Dir.WEST:
                            e.propX = e.x - 1;
                            e.propY = e.y;
                            break;
                    }

                    String chkKey = makeKey(e.propX, e.propY);
                    //System.out.println("Set prop for key " + chkKey);
                    MutInteger m = prop.get(chkKey);
                    if (m == null) {
                        prop.put(chkKey, new MutInteger(1));
                    } else {
                        ++prop.get(chkKey).n;
                    }
                }
            }

            //System.out.println("PROP IS");
            //for (auto p : prop) {
            //System.out.println("    " + p.first + ": " + p.second);
            //}

            // Apply the movements to the elves if the proposed position is not contested.
            int numMoves = 0;
            for (Elf e :elves) {
                if (e.moving) {
                    String key = makeKey(e.propX, e.propY);
                    //System.out.println("Checking ok to move for elf " + e + "(" + key + ") count is " + prop.get(key).n);
                    if (prop.get(key).n == 1) {
                        pos.remove(makeKey(e.x, e.y));
                        e.x = e.propX;
                        e.y = e.propY;
                        pos.put(key, e);
                        ++numMoves;
                    }
                }
            }

            if (numMoves == 0) {
                System.out.println("Did not do a move, round = " + (round + 1));
                break;
            }

            System.out.println(System.lineSeparator() + "ROUND " + (round + 1) + ", moves = " + numMoves);

            //System.out.println(System.lineSeparator() + "ROUND " + round + 1);
            //dsp(elves);

            curDir = (curDir + 1) % 4;
        }
    }
}