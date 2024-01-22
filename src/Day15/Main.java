package Day15;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Advent of Code 2022 challenge, Day 15.
 * Link: <a href="https://adventofcode.com/2022/day/15">...</a>
 * <p>
 * Challenge: Find beacon coordinates based on rough sensor maps and overlays.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    static class Sensor {
        int sx;
        int sy;
        int bx;
        int by;
        int dist;
    }

    static class Segment {
        int first;
        int second;
        public Segment(int f, int s) { this.first = f; this.second = s; }
    }

    boolean senContains(Sensor sen, int x, int y) {
        if (sen.bx == x && sen.by == y) {
            return false;
        }
        int chkDist = Math.abs(sen.sx - x) + Math.abs(sen.sy - y);
        if (chkDist > sen.dist) {
            return false;
        }

        return true;
    }

    static class SegList extends ArrayList<Segment> { }

    void dumpList(SegList list)
    {
        for (Segment s : list) {
            System.out.print("[" + s.first + " - " + s.second + "] ");
        }
        System.out.println();
    }

    SegList merge(SegList list)
    {
        SegList newList;
        newList = list;

        if (newList.size() == 1) {
            return newList;
        }

        newList.sort(new Comparator<Segment>() {
            @Override
            public int compare(Segment a, Segment b) {
                return a.first - b.first;
            }
        });

        SegList mergeList = new SegList();
        Segment lastSeg = newList.getFirst();
        for (int i = 1; i < newList.size(); ++i) {
            Segment seg = newList.get(i);

            if (lastSeg.second + 1 == seg.first) {
                // Right next to each other, combine

                lastSeg.second = seg.second;

            } else if (lastSeg.second < seg.first) {
                // No overlap, keep lastseg and move on

                mergeList.add(lastSeg);
                lastSeg = seg;
            } else if (seg.second <= lastSeg.second) {
                // Seg contained within lastseg, can just throw away

            } else {
                // Seg longer than lastseg, combine into lastSeg, throw away seg

                lastSeg.second = seg.second;
            }
        }
        mergeList.add(lastSeg);

        return mergeList;
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
     * Part 1: Processes the sensor data to determine the number of positions where a beacon cannot possibly exist
     * in a specified row. This method reads sensor data, calculates the Manhattan distance for each sensor to its
     * closest beacon, and then computes the range of coordinates in the specified row where a beacon cannot be present.
     */
    public void part1() {
        String filePath = "src/Day15/sensor.txt";
        ArrayList<Sensor> senList = new ArrayList<>();

        int min_x = 0, min_y = 0, max_x = 0, max_y = 0;
        boolean first = true;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);

                ArrayList<String> tokens = getMatches(s, "[-\\d]+");

                int sx = Integer.parseInt(tokens.get(0));
                int sy = Integer.parseInt(tokens.get(1));
                int bx = Integer.parseInt(tokens.get(2));
                int by = Integer.parseInt(tokens.get(3));

                Sensor sen = new Sensor();
                sen.sx = sx;
                sen.sy = sy;
                sen.bx = bx;
                sen.by = by;
                int dist = Math.abs(sx - bx) + Math.abs(sy - by);
                sen.dist = dist;
                senList.add(sen);
                System.out.println("    Sensor [" + sen.sx + ", " + sen.sy + "], Beacon = [" + sen.bx + ", " + sen.by + "]: dist = " + sen.dist);

                int n = sx - dist;
                if (first || n < min_x)
                    min_x = n;

                n = sx + dist;
                if (first || n > max_x)
                    max_x = n;

                n = sy - dist;
                if (first || n < min_y)
                    min_y = n;

                n = sy + dist;
                if (first || n > max_y)
                    max_y = n;

                first = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Range is [" + min_x + ", " + min_y + "] to [" + max_x + ", " + max_y + "]");

        // Calculate the range of coordinates based on sensor distances
        int check_y = 2000000;
        int noCount = 0;
        for (int check_x = min_x; check_x <= max_x; ++check_x) {
            // Determine the count of positions where a beacon cannot exist for the specified row
            boolean within = false;
            for (Sensor sen : senList) {
                if (senContains(sen, check_x, check_y)) {
                    within = true;
                    break;
                } else {
                    //System.out.println("Not within");
                }
            }

            if (within)
                ++noCount;
        }

        System.out.println("No beacon count is " + noCount);
    }

    /**
     * Part 2: Identifies the only possible position for the distress beacon based on sensor data. This method
     * considers a larger range of coordinates and uses the sensor information to exclude areas where the beacon
     * cannot be. It then identifies the single position where the beacon must be located and calculates its tuning frequency.
     */
    public void part2() {
        String filePath = "src/Day15/sensor.txt";
        int rangeX = 4000000;
        int rangeY = 4000000;

        ArrayList<Sensor> senList = new ArrayList<>();

        ArrayList<SegList> segs = new ArrayList<>();
        for (int i = 0; i < rangeY; ++i) {
            segs.add(new SegList());
        }

        int min_x = 0, min_y = 0, max_x = 0, max_y = 0;
        boolean first = true;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);

                ArrayList<String> tokens = getMatches(s, "[-\\d]+");
                //dumpVec(tokens);

                int sx = Integer.parseInt(tokens.get(0));
                int sy = Integer.parseInt(tokens.get(1));
                int bx = Integer.parseInt(tokens.get(2));
                int by = Integer.parseInt(tokens.get(3));

                Sensor sen = new Sensor();
                sen.sx = sx;
                sen.sy = sy;
                sen.bx = bx;
                sen.by = by;
                int dist = Math.abs(sx - bx) + Math.abs(sy - by);
                sen.dist = dist;
                senList.add(sen);
                System.out.println("    Sensor [" + sen.sx + ", " + sen.sy + "], Beacon = [" + sen.bx + ", " + sen.by + "]: dist = " + sen.dist);

                int n = sx - dist;
                if (first || n < min_x)
                    min_x = n;

                n = sx + dist;
                if (first || n > max_x)
                    max_x = n;

                n = sy - dist;
                if (first || n < min_y)
                    min_y = n;

                n = sy + dist;
                if (first || n > max_y)
                    max_y = n;

                first = false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Range is [" + min_x + ", " + min_y + "] to [" + max_x + ", " + max_y + "]");

        // Determine the range of coordinates to consider based on sensor data
        BiFunction<Integer, Integer, Integer> checkrange = (n, m) -> {
            if (n < 0)
                n = 0;
            if (n > m)
                n = m;
            return n;
        };

//        auto t1 = Clock::now();

        // Process each row within the range to find segments where a beacon cannot be
        for (int y = 0; y < rangeY; ++y) {
            //System.out.println("TESTING ROW " + y);
            SegList segList = new SegList();

            // For each sensor, calculate the segment in the current row it covers
            for (Sensor sen : senList) {
                int dist = sen.dist;
                //System.out.println("CHECKING [" + sen.sx + ", " + sen.sy + "], Beacon = [" + sen.bx + ", " + sen.by + "]: dist = " + sen.dist);
                // Does y intersect with this sensor area?
                if (y < sen.sy - dist || y > sen.sy + dist) {
                    //System.out.println("    DID NOT INTERSECT");
                    continue;
                }

                int diff = sen.dist - Math.abs(sen.sy - y);
                segList.add(new Segment(checkrange.apply(sen.sx - diff, rangeX), checkrange.apply(sen.sx + diff, rangeX)));
            }

            // Merge overlapping segments to simplify the list of segments
            SegList mergeList = merge(segList);
            if (mergeList.size() > 1) {
                // Identify the gap in segments where the beacon could possibly be located
                System.out.print("GAP y = " + y + ": ");
                dumpList(mergeList);
                // Get coord between, which will be X
                long ansX = (mergeList.get(0).second + mergeList.get(1).first) / 2;
                System.out.println("    ANSWER: " + (ansX * 4000000 + (long)y));
            }
        }

//        auto t2 = Clock::now();
//        auto duration = chrono::duration_cast<chrono::milliseconds>(t2 - t1).count();

//        System.out.println("Duration: " + duration + " milliseconds");
    }

    
}