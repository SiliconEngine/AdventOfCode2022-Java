package Day19;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Advent of Code 2022 challenge, Day 19.
 * Link: <a href="https://adventofcode.com/2022/day/19">...</a>
 * <p>
 * Challenge: Calculate largest number of geodes that can be produced by machine blueprints.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part(2);
    }

    // Data structure for robot blueprint
    static class Blueprint {
        int num;
        int oreRobotCost;
        int clayRobotCost;
        int obsRobotOreCost;
        int obsRobotClayCost;
        int geodeRobotOreCost;
        int geodeRobotObsCost;
        int oreRobotCap;
        int clayRobotCap;

        public String toString() {
            return "num=" + num + ", oreRobotCost=" + oreRobotCost + ", clayRobotCost=" + clayRobotCost
                    + ", obsRobotOreCost=" + obsRobotOreCost + ", obsRobotClayCost=" + obsRobotClayCost
                    + ", geodeRobotOreCost=" + geodeRobotOreCost + ", geodeRobotObsCost=" + geodeRobotObsCost
                    + ", oreRobotCap=" + oreRobotCap + ", clayRobotCap=" + clayRobotCap;
        }
    }

    static class Resources implements Cloneable {
        int numOre = 0;
        int numClay = 0;
        int numObs = 0;
        int numGeode = 0;
        int numOreRobots = 0;
        int numClayRobots = 0;
        int numObsRobots = 0;
        int numGeodeRobots = 0;

        public String toString() {
            return "numOre=" + numOre + ", numClay=" + numClay + ", numObs=" + numObs + ", numGeode=" + numGeode +
                    ", numOreRobots=" + numOreRobots + ", numClayRobots=" + numClayRobots + ", numObsRobots=" + numObsRobots +
                    ", numGeodeRobots=" + numGeodeRobots;
        }

        @Override
        public Object clone() {
            try {
                Resources r = (Resources)super.clone();
                return r;
            } catch (CloneNotSupportedException e) {
                // This should not happen since we are Cloneable
                throw new AssertionError();
            }
        }
    }

    int maxGeodes;
    int numMinutes;
    int maxBlueprints;

    /**
     * Simulates the progression of one minute in the resource collection and robot building process.
     * This recursive method updates the resource counts based on current robot numbers, decides the type of robot to build next, and calls itself to simulate the next minute.
     *
     * @param min Current minute in the simulation.
     * @param b The blueprint being used for the robot production strategy.
     * @param r The current state of resources and robots.
     */
    void nextMin(int min, Blueprint b, final Resources r) {
        ++min;

        Resources r2 = (Resources)r.clone();
        r2.numOre += r2.numOreRobots;
        r2.numClay += r2.numClayRobots;
        r2.numObs += r2.numObsRobots;
        r2.numGeode += r2.numGeodeRobots;
        //System.out.println("    Min " + min + ": collected " + r2);
        int minLeft = numMinutes - min;

        if (min == numMinutes) {
            if (r2.numGeode > maxGeodes) {
                maxGeodes = r2.numGeode;
                System.out.println("END: maxGeodes = " + maxGeodes);
            }
            return;
        }

        //if (minLeft < 10) {
        if (true) {
            int t = minLeft + 1;
            int check = r.numGeode + r.numGeodeRobots * t + (t * (t - 1)) / 2;
            if (check < maxGeodes) {
                //System.out.println("PRUNE at " + minLeft);
                return;
            }
        }

        if (min > numMinutes) {
            System.out.println("BAD");
            System.exit(0);
        }

        boolean canBuildClay = (r.numOre >= b.clayRobotCost);
        boolean canBuildObs = (r.numOre >= b.obsRobotOreCost && r.numClay >= b.obsRobotClayCost);
        boolean canBuildGeode = (r.numOre >= b.geodeRobotOreCost && r.numObs >= b.geodeRobotObsCost);

        // Decide what to build
        //if (r.numOre >= b.oreRobotCost && r.numOre <= b.oreRobotCap && ! canBuildClay && ! canBuildObs && ! canBuildGeode) {
        //if (r.numOre >= b.oreRobotCost && r.numOre <= b.oreRobotCap && r.numOreRobots < b.oreRobotCap) {
        if (r.numOre >= b.oreRobotCost && r.numOreRobots < b.oreRobotCap) {
            Resources r3 = (Resources)r2.clone();
            //System.out.println("    Building ore robot");
            ++r3.numOreRobots;
            r3.numOre -= b.oreRobotCost;
            nextMin(min, b, r3);
        }

        if (r.numOre >= b.clayRobotCost && r.numClayRobots < b.obsRobotClayCost) {
            Resources r3 = (Resources)r2.clone();
            //System.out.println("    Building clay robot");
            ++r3.numClayRobots;
            r3.numOre -= b.clayRobotCost;
            nextMin(min, b, r3);
        }

        if (r.numOre >= b.obsRobotOreCost && r.numClay >= b.obsRobotClayCost && r.numObsRobots < b.geodeRobotObsCost) {
            Resources r3 = (Resources)r2.clone();
            //System.out.println("    Building obs robot");
            ++r3.numObsRobots;
            r3.numOre -= b.obsRobotOreCost;
            r3.numClay -= b.obsRobotClayCost;
            nextMin(min, b, r3);
        }

        if (r.numOre >= b.geodeRobotOreCost && r.numObs >= b.geodeRobotObsCost) {
            Resources r3 = (Resources)r2.clone();
            //System.out.println("    Building geode robot");
            ++r3.numGeodeRobots;
            r3.numOre -= b.geodeRobotOreCost;
            r3.numObs -= b.geodeRobotObsCost;

            int maxCost = Math.max(b.geodeRobotOreCost - r3.numOre, b.geodeRobotObsCost - r3.numObs);
            nextMin(min, b, r3);
        }

        //if (r.numOre < b.oreRobotCap) {
        //if (! canBuildGeode) {
        nextMin(min, b, r2);
        //}
    }

    /**
     * Calculates the maximum number of geodes that can be opened using a given blueprint within the set time limit.
     * Initializes the simulation with a single ore-collecting robot and calls the recursive `nextMin` method to simulate each minute.
     *
     * @param b The blueprint to use for calculating the maximum number of geodes.
     * @return The maximum number of geodes that can be opened using the given blueprint.
     */
    int calcMax(Blueprint b) {
        Resources r = new Resources();
        r.numOreRobots = 1;
        maxGeodes = 0;
        nextMin(0, b, r);

        return maxGeodes;
    }

    ArrayList<String> getMatches(String s, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(s);
        ArrayList<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group());
        }

        return matches;
    }

    /**
     * Solves the Advent of Code Day 19 challenge for a given part (either part 1 or part 2).
     * This method reads a set of robot blueprints from a file and determines the maximum number of geodes that can be opened within a specified time limit using each blueprint.
     * <p>
     * 1. Read and parse blueprints from a file, creating a Blueprint object for each.
     * 2. Depending on the part (part 1 or part 2), set the time limit and the number of blueprints to process.
     * 3. For each blueprint:
     *    a. Initialize the resource state with one ore-collecting robot.
     *    b. Use a recursive method (nextMin) to simulate each minute of the operation, making decisions on what type of robot to build next based on the blueprint specifications and current resource availability.
     *    c. Within nextMin, resources are gathered, and the possibility of constructing new robots is evaluated at each minute. The state is cloned to explore different building strategies.
     *    d. Track the maximum number of geodes that can be opened by the end of the time limit.
     * 4. For part 1, sum up the product of blueprint ID and maximum geodes for each blueprint. For part 2, calculate the product of maximum geodes for the first three blueprints.
     * 5. Return the total quality level for part 1 or the product for part 2.
     */
    public void part(int partNum) {
        String filePath = "src/Day19/blueprint.txt";
        ArrayList<Blueprint> blueList = new ArrayList<>();

        if (partNum == 1) {
            numMinutes = 24;
            maxBlueprints = 999;
        } else {
            numMinutes = 32;
            maxBlueprints = 3;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);

                ArrayList<String> nums = getMatches(s, "\\d+");
                System.out.println(nums);

                Blueprint b = new Blueprint();
                b.num = Integer.parseInt(nums.get(0));
                b.oreRobotCost = Integer.parseInt(nums.get(1));
                b.clayRobotCost = Integer.parseInt(nums.get(2));
                b.obsRobotOreCost = Integer.parseInt(nums.get(3));
                b.obsRobotClayCost = Integer.parseInt(nums.get(4));
                b.geodeRobotOreCost = Integer.parseInt(nums.get(5));
                b.geodeRobotObsCost = Integer.parseInt(nums.get(6));
                b.oreRobotCap = Math.max(Math.max(Math.max(b.oreRobotCost, b.clayRobotCost), b.obsRobotOreCost), b.geodeRobotOreCost);
                b.clayRobotCap = b.obsRobotClayCost;
                blueList.add(b);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        int maxCount = maxBlueprints;
        int total = 0;
        int prod = 1;
        for (Blueprint b : blueList) {
            int num_g = calcMax(b);
            System.out.println("Max for blueprint " + b.num + " was " + num_g);
            total += b.num * num_g;
            prod *= num_g;
            if (--maxCount == 0)
                break;
        }

        System.out.println("Total quality level is " + total);
        System.out.println("Product was " + prod);

    }
}