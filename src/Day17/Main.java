package Day17;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Advent of Code 2022 challenge, Day 17.
 * Link: <a href="https://adventofcode.com/2022/day/17">...</a>
 * <p>
 * Challenge: Simulate falling rocks and calculate height of tower of rocks.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    private enum CollType { CLEAR, BOTTOM, SIDE, COLLISION }

    /**
     * Abstract class representing the general structure of a Rock.
     * This class provides the blueprint for various types of rock shapes
     * and their behavior in the chamber.
     */
    abstract static class Rock {
        int x = 0;
        int y = 0;

        void setXY(int new_x, int new_y) {
            this.x = new_x;
            this.y = new_y;
        }

        abstract CollType testColl(ArrayList<StringBuilder> chamber, int x, int y);
        abstract void setRock(ArrayList<StringBuilder> chamber, int x, int y);
        abstract String type();
        abstract int height();
    }

    /**
     * 'Dash' shape rock behavior and collision detection.
     */
    static class Dash extends Rock {
        int height() {
            return 1;
        }

        CollType testColl(ArrayList<StringBuilder> chamber, int x, int y) {
            if (y < 0)
                return CollType.BOTTOM;
            if (x < 0 || x + 3 > 6) {
                return CollType.SIDE;
            }
            StringBuilder s = chamber.get(y);
            if (s.charAt(x) != '.' || s.charAt(x + 1) != '.' || s.charAt(x + 2) != '.' || s.charAt(x + 3) != '.')
                return CollType.COLLISION;
            return CollType.CLEAR;
        }

        void setRock(ArrayList<StringBuilder> chamber, int x, int y) {
            StringBuilder s = chamber.get(y);
            s.setCharAt(x, '#');
            s.setCharAt(x + 1, '#');
            s.setCharAt(x + 2, '#');
            s.setCharAt(x + 3, '#');
        }

        String type() {
            return "Dash";
        }
    }

    /**
     * 'Plus' shape rock behavior and collision detection.
     */
    static class Plus extends Rock {
        int height() {
            return 3;
        }

        CollType testColl(ArrayList<StringBuilder> chamber, int x, int y) {
            if (y-2 < 0)
                return CollType.BOTTOM;
            if (x < 0 || x + 2 > 6)
                return CollType.SIDE;
            if (chamber.get(y).charAt(x+1) != '.' || chamber.get(y-1).charAt(x) != '.' || chamber.get(y-1).charAt(x + 1) != '.' || chamber.get(y-1).charAt(x + 2) != '.' || chamber.get(y-2).charAt(x+1) != '.')
                return CollType.COLLISION;
            return CollType.CLEAR;
        }

        void setRock(ArrayList<StringBuilder> chamber, int x, int y) {
            chamber.get(y).setCharAt(x + 1, '#');
            chamber.get(y - 1).setCharAt(x,'#');
            chamber.get(y - 1).setCharAt(x + 1, '#');
            chamber.get(y - 1).setCharAt(x + 2, '#');
            chamber.get(y - 2).setCharAt(x + 1, '#');
        }

        String type() {
            return "Plus";
        }
    }

    /**
     * 'Ell' shape rock behavior and collision detection.
     */
    static class Ell extends Rock
    {
        int height() {
            return 3;
        }

        CollType testColl(ArrayList<StringBuilder> chamber, int x, int y) {
            if (y-2 < 0)
                return CollType.BOTTOM;
            if (x < 0 || x + 2 > 6)
                return CollType.SIDE;
            if (chamber.get(y).charAt(x+2) != '.' || chamber.get(y-1).charAt(x+2) != '.' || chamber.get(y-2).charAt(x) != '.' || chamber.get(y-2).charAt(x+1) != '.' || chamber.get(y-2).charAt(x+2) != '.')
                return CollType.COLLISION;
            return CollType.CLEAR;
        }

        void setRock(ArrayList<StringBuilder> chamber, int x, int y) {
            chamber.get(y).setCharAt(x+2, '#');
            chamber.get(y-1).setCharAt(x+2, '#');
            chamber.get(y-2).setCharAt(x, '#');
            chamber.get(y-2).setCharAt(x+1, '#');
            chamber.get(y-2).setCharAt(x+2, '#');
        }

        String type()
        {
            return "Ell";
        }
    }

    /**
     * 'Bar' shape rock behavior and collision detection.
     */
    static class Bar extends Rock {
        int height() {
            return 4;
        }

        CollType testColl(ArrayList<StringBuilder> chamber, int x, int y) {
            if (y-3 < 0)
                return CollType.BOTTOM;
            if (x < 0 || x > 6)
                return CollType.SIDE;
            if (chamber.get(y).charAt(x) != '.' || chamber.get(y-1).charAt(x) != '.' || chamber.get(y-2).charAt(x) != '.' || chamber.get(y-3).charAt(x) != '.')
                return CollType.COLLISION;
            return CollType.CLEAR;
        }

        void setRock(ArrayList<StringBuilder> chamber, int x, int y) {
            chamber.get(y).setCharAt(x, '#');
            chamber.get(y - 1).setCharAt(x, '#');
            chamber.get(y - 2).setCharAt(x, '#');
            chamber.get(y - 3).setCharAt(x, '#');
        }

        String type()
        {
            return "Bar";
        }
    }

    /**
     * 'Box' shape rock behavior and collision detection.
     */
    static class Box extends Rock
    {
        int height() {
            return 2;
        }

        CollType testColl(ArrayList<StringBuilder> chamber, int x, int y) {
            if (y-1 < 0)
                return CollType.BOTTOM;
            if (x < 0 || x+1 > 6)
                return CollType.SIDE;
            if (chamber.get(y).charAt(x) != '.' || chamber.get(y).charAt(x+1) != '.' || chamber.get(y-1).charAt(x) != '.' || chamber.get(y-1).charAt(x+1) != '.')
                return CollType.COLLISION;
            return CollType.CLEAR;
        }

        void setRock(ArrayList<StringBuilder> chamber, int x, int y) {
            chamber.get(y).setCharAt(x, '#');
            chamber.get(y).setCharAt(x+1, '#');
            chamber.get(y-1).setCharAt(x, '#');
            chamber.get(y-1).setCharAt(x+1, '#');
        }

        String type()
        {
            return "Box";
        }
    }

    void dumpChamber(ArrayList<StringBuilder> chamber) {
        System.out.println("========");
        for (int y = chamber.size() - 1; y >= 0; --y) {
            System.out.printf("%5d: %s\\n", y, chamber.get(y).toString());
        }
        System.out.println("========");
    }

    int getHeight(ArrayList<StringBuilder> chamber) {
        int tower = 0;
        for (tower = chamber.size() - 1; tower >= 0; --tower) {
            if (! chamber.get(tower).toString().equals("......."))
                break;
        }
        return tower + 1;
    }

    /**
     * Part 1: This method simulates the falling of a fixed number of rocks (2022) in a chamber.
     * It calculates the final height of the pile of rocks after all rocks have fallen.
     * The method iterates through each rock, simulating its movement and handling collisions
     * with the chamber walls, floor, or other rocks.
     */
    public void part1() {
        String filePath = "src/Day17/wind.txt";
        String windList = "";

        Rock[] rocks = new Rock[5];
        rocks[0] = new Dash();
        rocks[1] = new Plus();
        rocks[2] = new Ell();
        rocks[3] = new Bar();
        rocks[4] = new Box();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            windList = br.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<StringBuilder> chamber = new ArrayList<StringBuilder>();
        chamber.add(new StringBuilder("......."));
        chamber.add(new StringBuilder("......."));
        chamber.add(new StringBuilder("......."));
        chamber.add(new StringBuilder("......."));

        final int MAX_ROCKS = 2022;

        int curRock = 0;
        int curWind = 0;
        for (long i = 0; i < MAX_ROCKS; ++i) {
            CollType coll;
            Rock rock = rocks[curRock++];
            int x, y;
            if (curRock >= 5)
                curRock = 0;

            // Figure out if we need to add space to top
            int h = rock.height();

            //cout + "FIGURE SPACE, h= " + h + ", CHAMBER IS : " + endl;
            //dumpChamber(chamber);

            // Find first non-empty
            int empCount = 0;
            for (y = chamber.size() - 1; y >= 0; --y) {
                if (! chamber.get(y).toString().equals("......."))
                    break;
                ++empCount;
            }

            int need = h + 3;
            while (empCount < need) {
                chamber.add(new StringBuilder("......."));
                ++empCount;
            }
            //cout + "ABOUT TO DROP, CHAMBER IS:" + endl;
            //dumpChamber(chamber);

            // Drop rock
            y = chamber.size() - empCount + need - 1;
            x = 2;

            for (;;) {
                //cout + "Current rock: " + rock->type() + " @ [" + x + ", " + y + "]" + endl;

                char wind = windList.charAt(curWind++);
                if (curWind >= windList.length())
                    curWind = 0;

                // Move from the wind
                int dir = (wind == '<' ? -1 : 1);
                x += dir;
                coll = rock.testColl(chamber, x, y);
                if (coll == CollType.SIDE || coll == CollType.COLLISION)
                    x -= dir;
                //cout + "    After wind: [" + x + ", " + y + "]" + endl;

                // Move down
                --y;
                coll = rock.testColl(chamber, x, y);
                //cout + "    Move: [" + x + ", " + y + "], test is " + coll + endl;
                if (coll == CollType.BOTTOM || coll == CollType.COLLISION) {
                    ++y;
                    //cout + "    Move back, setting rock at [" + x + ", " + y + "], test is " + endl;
                    rock.setRock(chamber, x, y);
                    break;
                }

                // Continue movement
            }
        }

        //dumpChamber(chamber);

        // Find first non-empty
        int tower = 0;
        for (tower = chamber.size() - 1; tower >= 0; --tower) {
            if (! chamber.get(tower).toString().equals("......."))
                break;
        }
        System.out.println("Tower is " + (tower+1));
    }

    /**
     * Part 2: This method extends the simulation for a much larger number of rocks (1 trillion).
     * Due to the large number, it includes an optimization to detect patterns in rock falling
     * and calculates the final height without simulating each individual rock.
     * It handles the large iteration count by skipping iterations based on detected
     * patterns in the rock pile's growth.
     */
    void part2()
    {
        String filePath = "src/Day17/wind.txt";
        String windList = "";

//        auto t1 = Clock::now();

        Rock[] rocks = new Rock[5];
        rocks[0] = new Dash();
        rocks[1] = new Plus();
        rocks[2] = new Ell();
        rocks[3] = new Bar();
        rocks[4] = new Box();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            windList = br.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<StringBuilder> chamber = new ArrayList<StringBuilder>();
        chamber.add(new StringBuilder("......."));
        chamber.add(new StringBuilder("......."));
        chamber.add(new StringBuilder("......."));
        chamber.add(new StringBuilder("......."));

        final long MAX_ROCKS = 1000000000000L;

        int curRock = 0;
        int curWind = 0;
        int lastCurWind = 999999;
        long extraHeight = 0;
        int counter = 0;
        long lastHeight = 0;
        long lastI = 0;
        String lastPatt = "";

        for (long i = 0; i < MAX_ROCKS; ++i) {
//        if (++counter >= 1000000) {
//            cout + i + endl;
//            counter = 0;
//
//			auto t2 = Clock::now();
//			auto duration = chrono::duration_cast<chrono::milliseconds>(t2 - t1).count();
//            double d = (double)duration / 1000.;
//
//            cout + "Duration: " + d + " seconds (";
//
//            double est = ((double)duration / 1000.) / ((double)i / (double)MAX_ROCKS);
//            cout + "Est: " + est + ")" + endl;
//        }


            if (curRock == 0 && curWind < lastCurWind) {
                //if (curRock == 0 && curWind == 0) {
                long curH = getHeight(chamber) + extraHeight;
                long diff = curH - lastHeight;
                long i_diff = i - lastI;
                //cout + "At i = " + i + ", height is " + curH + endl;
                System.out.println("At curRock = " + curRock + ", curWind = " + curWind + ", i = " + i + ", idiff = " + i_diff + ", height is " + curH + ", diff = " + diff);
                lastHeight = curH;
                lastCurWind = 10; // 10; // 4
                lastI = i;

                String pattern = Long.toString(i_diff) + "-" + Long.toString(diff);
                if (pattern.equals(lastPatt)) {
                    System.out.println("found pattern");
                    long amtLeft = MAX_ROCKS - i;
                    long repeatCount = amtLeft / i_diff;
                    System.out.println("Repeat count: " + repeatCount);
                    i += repeatCount * i_diff;
                    extraHeight += repeatCount * diff;
                }
                lastPatt = pattern;
            }

            //int curH = getHeight(chamber);
            //cout + "At curRock = " + curRock + ", curWind = " + curWind + ", i = " + i + ", height is " + curH + endl;

            CollType coll;
            Rock rock = rocks[curRock++];
            int x, y;
            if (curRock >= 5)
                curRock = 0;

            // Figure out if we need to add space to top
            int h = rock.height();

            //cout + "FIGURE SPACE, h= " + h + ", CHAMBER IS : " + endl;
            //dumpChamber(chamber);

            // Find first non-empty
            int empCount = 0;
            for (y = chamber.size() - 1; y >= 0; --y) {
                if (! chamber.get(y).toString().equals("......."))
                    break;
                ++empCount;
            }

            int need = h + 3;
            while (empCount < need) {
                chamber.add(new StringBuilder("......."));
                ++empCount;
            }

            while (chamber.size() > 20000) {
                chamber.removeFirst();
                ++extraHeight;
            }

            //cout + "ABOUT TO DROP, CHAMBER IS:" + endl;
            //dumpChamber(chamber);

            // Drop rock
            y = chamber.size() - empCount + need - 1;
            x = 2;

            for (;;) {
                //cout + "Current rock: " + rock->type() + " @ [" + x + ", " + y + "]" + endl;

                char wind = windList.charAt(curWind++);
                if (curWind >= windList.length())
                    curWind = 0;

                // Move from the wind
                int dir = (wind == '<' ? -1 : 1);
                x += dir;
                coll = rock.testColl(chamber, x, y);
                if (coll == CollType.SIDE || coll == CollType.COLLISION)
                    x -= dir;
                //cout + "    After wind: [" + x + ", " + y + "]" + endl;

                // Move down
                --y;
                coll = rock.testColl(chamber, x, y);
                //cout + "    Move: [" + x + ", " + y + "], test is " + coll + endl;
                if (coll == CollType.BOTTOM || coll == CollType.COLLISION) {
                    ++y;
                    //cout + "    Move back, setting rock at [" + x + ", " + y + "], test is " + endl;
                    rock.setRock(chamber, x, y);
                    break;
                }

                // Continue movement
            }
        }

        //dumpChamber(chamber);

        // Find first non-empty
        int tower = 0;
        for (tower = chamber.size() - 1; tower >= 0; --tower) {
            if (! chamber.get(tower).toString().equals("......."))
                break;
        }
        System.out.println("Tower is " + (tower+1));
        System.out.println("Extra is " + extraHeight);
        System.out.println("Total is " + (extraHeight+tower+1));

        {
            long i = MAX_ROCKS;
            long curH = getHeight(chamber) + extraHeight;
            long diff = curH - lastHeight;
            long i_diff = i - lastI;
            //cout + "At i = " + i + ", height is " + curH + endl;
            System.out.println("At curRock = " + curRock + ", curWind = " + curWind + ", i = " + i + ", idiff = " + i_diff + ", height is " + curH + ", diff = " + diff);
        }
    }
}