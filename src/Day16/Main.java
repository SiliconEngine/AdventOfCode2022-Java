package Day16;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Advent of Code 2022 challenge, Day 16.
 * Link: <a href="https://adventofcode.com/2022/day/16">...</a>
 * <p>
 * Challenge: Simulate elephants opening valves in a cavern map and find the optimal strategy.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    final boolean DEBUG = false;

    public static void main(String[] args) {
        Main main = new Main();
        main.run(2);
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

    static class StringArrayList extends ArrayList<String> {
        public StringArrayList(ArrayList<String> arrayList) {
            super(arrayList);
        }
        public StringArrayList() {
            super();
        }

        @Override
        public String toString() {
            StringBuilder out = new StringBuilder();
            boolean first = true;
            for (String s : this) {
                if (!first)
                    out.append(",");
                out.append(s);
                first = false;
            }
            return out.toString();
        }
    }

    static class IntArrayList extends ArrayList<Integer> {
        public String toString() {
            StringBuilder out = new StringBuilder();
            boolean first = true;
            for (Integer s : this) {
                if (!first)
                    out.append(",");
                out.append(Integer.toString(s));
                first = false;
            }
            return out.toString();
        }
    }

    static class Valve {
        int rate;
        String name;
        StringArrayList paths;
        public Valve(int r, String n, StringArrayList p) { this.rate = r; this.name = n; this.paths = p; }
    }

    long bitsetToLong(BitSet bits) {
        return bits.toLongArray()[0];
    }

    Map<String, Valve> valveList = new HashMap<>();
    Map<String, Integer> valveDist = new HashMap<>();
    StringArrayList activeValves = new StringArrayList();

    int maxOpen = 0;
    int bestPres = 0;
    int maxPres = 0;

    int calcDist(String name1, String name2) {
        class DistNode {
            String name;
            int dist;
            public DistNode(String n, int d) { this.name = n; this.dist = d; }
        }

        Set<String> visited = new HashSet<>();
        Queue<DistNode> q = new LinkedList<>();

        q.add(new DistNode(name1, 0));
        visited.add(name1);

        while (!q.isEmpty()) {
            DistNode node = q.remove();
            if (node.name.equals(name2)) {
                return node.dist;
            }

            Valve valve = valveList.get(node.name);
            for (String p : valve.paths) {
                if (! visited.contains(p) ) {
                    visited.add(p);
                    q.add(new DistNode(p, node.dist + 1));
                }
            }
        }

        return -1;
    }

    void part1CalcBig(int valveIdx, long checkedVal, int minute, int numOpen, int curPres, int totalPres)
    {
//        BitSet checked = new BitSet(32);
        //checked.set<32> checked = checkedVal;
        BitSet checked = BitSet.valueOf(new long[]{ checkedVal });
        String name = activeValves.get(valveIdx);
        if (DEBUG) System.out.println("Checking valve " + valveIdx + " (" + name + "), checked = " + checkedVal + " (" + checked + ")");

        if (numOpen == maxOpen) {
            if (DEBUG) System.out.println("    All valves open, time left = " + (30 - minute) + ", cur=" + curPres + ", curtotal = " + totalPres);
            totalPres += curPres * (30 - minute);
            if (DEBUG) System.out.println(", final total is " + totalPres);
            if (totalPres > bestPres) {
                if (DEBUG) System.out.println("    New best");
                bestPres = totalPres;
            }
            return;
        }

        for (int i = 0; i < activeValves.size(); ++i) {
            if (DEBUG) System.out.println("    Loop " + i + ": checked = " + checked);
            if (!checked.get(i)) {
                Valve valve = valveList.get(activeValves.get(i));
                int dist = valveDist.get(name + "-" + valve.name);
                if (DEBUG) System.out.println("    Trying " + i + "(" + valve.name + "), dist = " + dist);
                if (DEBUG) System.out.println("    checked = " + checked);

                // Add one for time needed to open valve
                ++dist;

                // Check not enough time to get there and turn on, calculate how we would do just waiting
                if (minute + dist > 30) {
                    int tempTotal = totalPres + curPres * (30 - minute);
                    if (DEBUG) System.out.println("    Not enough time, tempTotal = " + tempTotal);
                    if (tempTotal > bestPres) {
                        if (DEBUG) System.out.println("    New best");
                        bestPres = tempTotal;
                    }

                    continue;
                }

                checked.set(i);
                part1CalcBig(i, checked.toLongArray()[0], minute + dist, numOpen + 1, curPres + valve.rate, totalPres + curPres * dist);
                checked.clear(i);
            }
        }
    }

    static class Runner {
        int travelToIdx;
        int travelTimeLeft;
        public Runner(int a, int b) { this.travelToIdx = a; this.travelTimeLeft = b; }
        public void set(int a, int b) {
            this.travelToIdx = a;
            this.travelTimeLeft = b;
        }
    };
    static class RunnerSet {
        Runner[] r = new Runner[] {new Runner(0, 0), new Runner(0, 0)};
        @Override
        public String toString() {
            Runner r1 = r[0];
            Runner r2 = r[1];
            return "r1 = [v-" + r1.travelToIdx + ", t-" + r1.travelTimeLeft + "] / r2 = [v-" + r2.travelToIdx + ", t-" + r2.travelTimeLeft + "]";
        }
    };

    int maxMin = 0;

    static class Entry {
        RunnerSet runners = new RunnerSet();
        long checkedVal;
        int minute;
        int numOpen;
        int curPres;
        int totalPres;
        public String toString() {
            return "{minute=" + this.minute + ", numOpen=" + this.numOpen + ", curPres=" + this.curPres + ", totalPres=" + this.totalPres + " " + this.runners + "}";
        }
    }

// Example path:
// 0: [  AA] [  AA]
// 1: [T-II] [T-DD]
// 2: [T-JJ] [O-DD]
// 3: [O-JJ] [T-EE]
// 4: [T-II] [T-FF]
// 5: [T-AA] [T-GG]
// 6: [T-BB] [T-HH]
// 7: [O-BB] [O-HH]
// 8: [T-CC] [T-GG]
// 9: [O-CC] [T-FF]
//10: [    ] [T-EE]
//11: [    ] [O-EE]

// 0: [3-JJ] [2-DD] WAS: [0-AA] [0-AA]
// 2: [1-JJ] [5-HH] WAS: [3-JJ] [2-DD]
// 3: [4-BB] [4-HH] WAS: [1-JJ] [5-HH]
// 7: [2-CC] [4-EE] WAS: [4-BB] [4-HH]
// 9: [    ] [2-EE] WAS: [2-CC] [4-EE]
//11: [    ] [    ] WAS: [    ] [2-EE]

    String makeKey(Entry node)
    {
        Runner r1 = node.runners.r[0];
        Runner r2 = node.runners.r[1];

        String key = Integer.toString(node.minute) + "-" + Long.toString(node.checkedVal) + "-" + Integer.toString(node.totalPres) + "-";
        int i1 = r1.travelToIdx * 20 + r1.travelTimeLeft;
        int i2 = r2.travelToIdx * 20 + r2.travelTimeLeft;
        if (i1 > i2)
            key += Integer.toString(i1) + Integer.toString(i2);
        else
            key += Integer.toString(i2) + Integer.toString(i1);

        return key;
    }

    Map<String, Boolean> visited = new HashMap<>();
    Queue<Entry> q = new LinkedList<>();

     /**
      * Part 2 extends the calculation of maximum pressure release by including the help of an elephant,
      * effectively allowing two valves to be opened simultaneously. The algorithm now accounts for two agents (the player and the elephant)
      * and explores the opening sequences that can be achieved together in a reduced time window of 26 minutes.
      * <p>
      * Similar to part1, it uses a recursive approach but now must consider two separate paths of movement and valve opening.
      * Each recursive call determines the next move for both the player and the elephant, tracking their positions,
      * the valves they open, and the accumulated pressure. The complexity increases as the algorithm has to consider combinations
      * of movements for both agents. The method keeps track of the highest pressure achieved with this collaborative effort.
      */
    void part2()
    {
        // Initialize a BitSet to track which valves have been checked
        BitSet checked = new BitSet(32);
        checked.set(0);

        // Initialize the state for the two agents (player and elephant) with their respective starting positions
        Entry startEntry = new Entry();
        startEntry.runners.r[0].travelTimeLeft = 0;
        startEntry.runners.r[0].travelToIdx = 0;
        startEntry.runners.r[1].travelTimeLeft = 0;
        startEntry.runners.r[1].travelToIdx = 0;
        startEntry.checkedVal = 1;
        startEntry.minute = 0;
        startEntry.numOpen = 0;
        startEntry.curPres = 0;
        startEntry.totalPres = 0;
        q.add(startEntry);

        // Begin the BFS (Breadth-First Search) algorithm to explore all possible combinations of valve openings within the time limit
        bfs();
    }

    void bfs()
    {
        int loopCount = 0;
        BitSet checked = new BitSet(32);

        while (! q.isEmpty()) {
            Entry node = q.remove();
            if (DEBUG) System.out.println("NODE: " + node);

            ++loopCount;
//            if (loopCount == 10) System.exit(0);

            checked = BitSet.valueOf(new long[]{node.checkedVal});
            Runner r1 = node.runners.r[0];
            Runner r2 = node.runners.r[1];
            Runner[] rlist = new Runner[2];
            rlist[0] = node.runners.r[0];
            rlist[1] = node.runners.r[1];

            RunnerSet runners = node.runners;
            int minute = node.minute;
            int curPres = node.curPres;
            int totalPres = node.totalPres;

            // See if both travelers arrive in the future and we need to advance the clock
            int minTime = Math.min(r1.travelTimeLeft, r2.travelTimeLeft);
            if (DEBUG) System.out.println(runners + ", minTime = " + minTime);
            if (minTime > 0) {
                minute += minTime;
                totalPres += curPres * minTime;

                for (int ridx = 0; ridx < 2; ++ridx) {
                    if ((rlist[ridx].travelTimeLeft -= minTime) == 0) {
                        // Arrived and turned on valve

                        Valve valve = valveList.get(activeValves.get(rlist[ridx].travelToIdx));
                        if (DEBUG) System.out.println("    Turned on valve " + valve.name);
                        curPres += valve.rate;
                        ++node.numOpen;
                    }
                }
            }
            //System.out.println("    TIME ADVANCED by " + minTime + ": minute = " + minute + ", curPres = " + curPres + ", totalPres = " + totalPres + ", numOpen = " + (int)node.numOpen + " (of " + maxOpen + ")");

            // See if all valves open
            if (node.numOpen == maxOpen) {
                if (DEBUG) System.out.println("    All valves open, time left = " + (26 - minute) + ", cur=" + curPres + ", curtotal = " + totalPres);
                //System.out.println("    All valves open, time left = " + (26 - minute) + ", cur=" + curPres + ", curtotal = " + totalPres;
                totalPres += curPres * (26 - minute);
                if (DEBUG) System.out.println(", final total is " + totalPres);
                if (totalPres > bestPres) {
                    bestPres = totalPres;
                    if (DEBUG) System.out.println("    (ALL OPEN) New best " + bestPres);
                    System.out.println("    (ALL OPEN) New best " + bestPres);
                }
                continue;
            }

            // If only one valve left and we're traveling for it, calculate final value
            //System.out.println("    r1.ttl = " + r1.travelTimeLeft + ", r2.ttl = " + r2.travelTimeLeft);
            if ((node.numOpen + 1) == maxOpen && (r1.travelTimeLeft != 0 || r2.travelTimeLeft != 0)) {
                if (DEBUG) System.out.println("    ONE LEFT: " + node);
                Runner r = r1.travelTimeLeft != 0 ? r1 : r2;
                Valve valve = valveList.get(activeValves.get(r.travelToIdx));

                totalPres += curPres * r.travelTimeLeft;
                minute += r.travelTimeLeft;
                curPres += valve.rate;
                totalPres += (26 - minute) * curPres;
                if (DEBUG) System.out.println("        minute = " + minute + ", totalPres = " + totalPres + ", curPres = " + curPres);

                //int total = totalPres + curPres * (26 - minute) + valve.rate * (26 - (minute + r.travelTimeLeft));;
                //System.out.println("        minute = " + minute + ", totalPres = " + totalPres + ", curPres = " + curPres + ", total = " + total);
                if (totalPres > bestPres) {
                    bestPres = totalPres;
                    System.out.println("    (ONE LEFT) New best " + bestPres);
                }
                continue;
            }

            // Check our best if we just wait here or let other one complete
            int tempTotal = totalPres + curPres * (26 - minute);
            for (int ridx = 0; ridx < 2; ++ridx) {
                if (rlist[ridx].travelTimeLeft != 0) {
                    int dest = runners.r[ridx].travelToIdx;
                    Valve valve = valveList.get(activeValves.get(dest));
                    int timeLeft = runners.r[ridx].travelTimeLeft;
                    int t = 26 - (minute + timeLeft);
                    int extra = 0;
                    if (t > 0) {
                        extra = valve.rate * t;
                    }
                    tempTotal += extra;
                }
            }
            if (tempTotal > bestPres) {
                bestPres = tempTotal;
                System.out.println("WAIT CHECK: new best " + bestPres);
            }

            // See if impossible to beat best pressure so far
            int trial = totalPres + maxPres * (26 - minute);
            if (trial < bestPres) {
                if (DEBUG) System.out.println("Can't beat best, trial = " + trial + ", bestPres = " + bestPres);
                continue;
            }

            // Build list of currently reachable indexes for each
            class Pot {
                int v;
                int d;
                public Pot (int v, int d) { this.v = v; this.d = d; }
                public void set(int v, int d) { this.v = v; this.d = d; }
            }

            Pot[][] potentialList = new Pot[2][50];
            for (int i1 = 0; i1 < 2; ++i1) {
                for (int i2 = 0; i2 < 50; ++i2) {
                    potentialList[i1][i2] = new Pot(0, 0);
                }
            }
            int[] potCount = {0, 0};

            // DEBUG
            //       valve 0: AA
            //       valve 1: BB
            //       valve 2: CC
            //       valve 3: DD
            //       valve 4: EE
            //       valve 5: HH
            //       valve 6: JJ
            // 0: [3-JJ] [2-DD] WAS: [0-AA] [0-AA]
            // 2: [1-JJ] [5-HH] WAS: [3-JJ] [2-DD]
            // 3: [4-BB] [4-HH] WAS: [1-JJ] [5-HH]
            // 7: [2-CC] [4-EE] WAS: [4-BB] [4-HH]
            // 9: [    ] [2-EE] WAS: [2-CC] [4-EE]
            //11: [    ] [    ] WAS: [    ] [2-EE]
//        potCount[0] = potCount[1] = 1;
//        switch (minute) {
//        case 0:
//            potentialList[0][0] = { 6, 3 }; //3-JJ
//            potentialList[1][0] = { 3, 2 }; //2-DD
//            break;
//        case 2:
//            potentialList[0][0] = { 6, 1 }; //1-JJ
//            potentialList[1][0] = { 4, 5 }; //5-HH
//            break;
//        case 3:
//            potentialList[0][0] = { 1, 4 }; //4-BB
//            potentialList[1][0] = { 5, 4 }; //4-HH
//            break;
//        case 7:
//            potentialList[0][0] = { 2, 2 }; //2-CC
//            potentialList[1][0] = { 4, 4 }; //4-EE
//            break;
//        case 9:
//            potentialList[0][0] = { 2, 0 }; //0-CC
//            potentialList[1][0] = { 4, 2 }; //2-EE
//            break;
//        case 11:
//            potentialList[0][0] = { 2, 0 }; //0-CC
//            potentialList[1][0] = { 4, 0 }; //0-EE
//            break;
//        default:
//            System.out.println("INVALID MINUTE " + minute);
//            exit(0);
//        }
//        goto SKIP;

            for (int ridx = 0; ridx < 2; ++ridx) {
                //System.out.println("    Figure out paths, ridx=" + ridx + ", checked=" + checked);
                // Is runner stopped and we need next destination?
                if (rlist[ridx].travelTimeLeft == 0) {
                    int valveIdx = rlist[ridx].travelToIdx;    // Current location
                    String name = activeValves.get(valveIdx);
                    //System.out.println("    " + ridx + ": " + name);

                    // Runner can move, figure out where
                    for (int v = 0; v < activeValves.size(); ++v) {
                        if (!checked.get(v)) {
                            Valve valve = valveList.get(activeValves.get(v));
                            int dist = valveDist.get(name + "-" + valve.name) + 1;

                            // See if reachable in time
                            if (minute + dist < 26) {
                                potentialList[ridx][potCount[ridx]++].set(v, dist);
                                //System.out.println("    index " + v + " is reachable");
                            }
                        }
                    }
                }
            }
            SKIP:

            // See if no further paths -- just calculate the rest if so
            if (r1.travelTimeLeft == 0 && r2.travelTimeLeft == 0 && potCount[0] == 0 && potCount[1] == 0) {
                totalPres += curPres * (26 - minute);
                if (totalPres > bestPres) {
                    bestPres = totalPres;
                    if (DEBUG) System.out.println("    (NO PATHS) New best " + bestPres);
                    System.out.println("    (NO PATHS) New best " + bestPres);
                }
                continue;
            }

            // If either has no further progress, just set to the current one
            for (int ridx = 0; ridx < 2; ++ridx) {
                if (potCount[ridx] == 0) {
                    ++potCount[ridx];
                    potentialList[ridx][0].set(rlist[ridx].travelToIdx, rlist[ridx].travelTimeLeft);
                }
            }

            // Add in potential path of staying put until the end, if not already there
            for (int ridx = 0; ridx < 2; ++ridx) {
                if (! (potCount[ridx] == 1 && potentialList[ridx][0].d > 100)) {
                    potentialList[ridx][potCount[ridx]++].set(rlist[ridx].travelToIdx, 999);
                }
            }

            // Build potential paths from here and add to queue
            //for (int ridx = 0; ridx < 1; ++ridx) {
            //int otherIdx = 1 - ridx;
            int ridx = 0;
            int otherIdx = 1;
            for (int rpotIdx = 0; rpotIdx < potCount[ridx]; ++rpotIdx) {
                for (int opotIdx = 0; opotIdx < potCount[otherIdx]; ++opotIdx) {
                    Entry newNode = new Entry();
                    newNode.runners.r[ridx].set(potentialList[ridx][rpotIdx].v, potentialList[ridx][rpotIdx].d);
                    newNode.runners.r[otherIdx].set(potentialList[otherIdx][opotIdx].v, potentialList[otherIdx][opotIdx].d);

                    int dest1 = newNode.runners.r[0].travelToIdx;
                    int dest2 = newNode.runners.r[1].travelToIdx;
                    if (DEBUG) System.out.print("    Potential moving to [" + dest1 + " / " + newNode.runners.r[0].travelTimeLeft + "] and [" + dest2 + " / " + newNode.runners.r[1].travelTimeLeft + "]");

                    // Check case where going to same place and skip
                    if (dest1 == dest2) {
                        if (DEBUG) System.out.println("    SKIP");
                        continue;
                    }

                    BitSet tempchk = (BitSet) checked.clone();

                    tempchk.set(dest1);
                    tempchk.set(dest2);
                    newNode.checkedVal = bitsetToLong(tempchk);
                    newNode.minute = minute;
                    newNode.numOpen = node.numOpen;
                    newNode.curPres = curPres;
                    newNode.totalPres = totalPres;

                    String key = makeKey(newNode);
                    if (!visited.getOrDefault(key, false)) {
                        q.add(newNode);
                        visited.put(key, true);
                    }
                    if (DEBUG) System.out.println("    New node:" + newNode);

                    //goto ONE_NODE;
                }
            }
            //}
//ONE_NODE:
            ;
        }

        System.out.println("Loop count was " + loopCount);
    }

    void part2CalcBig(RunnerSet runners, long checkedVal, int minute, int numOpen, int curPres, int totalPres)
    {
        BitSet checked = BitSet.valueOf(new long[]{ checkedVal });
        Runner r1 = runners.r[0];
        Runner r2 = runners.r[1];

        // See if impossible to beat best pressure so far
        int trial = totalPres + curPres + maxPres * (26 - minute);
        if (trial < bestPres) {
            if (DEBUG) System.out.println("Can't beat best, trial = " + trial + ", bestPres = " + bestPres);
            return;
        }

        // See if all valves open
        if (numOpen == maxOpen) {
            if (DEBUG) System.out.println("    All valves open, time left = " + (26 - minute) + ", cur=" + curPres + ", curtotal = " + totalPres);
            System.out.println("    All valves open, time left = " + (26 - minute) + ", cur=" + curPres + ", curtotal = " + totalPres);
            totalPres += curPres * (26 - minute);
            if (DEBUG) System.out.println(", final total is " + totalPres);
            if (totalPres > bestPres) {
                bestPres = totalPres;
                if (DEBUG) System.out.println("    (ALL OPEN) New best " + bestPres);
                System.out.println("    (ALL OPEN) New best " + bestPres);
            }
            return;
        }

        // See if both travelers arrive in the future
        int minTime = Math.min(r1.travelTimeLeft, r2.travelTimeLeft);
        if (DEBUG) System.out.println(runners + ", minTime = " + minTime);
        if (minTime > 0) {
            // Check out of time
            if (minute + minTime >= 26) {
                if (DEBUG) System.out.println("    OUT OF TIME");
                //System.out.println("    OUT OF TIME");
                totalPres += curPres * (26 - minute);
                if (totalPres > bestPres) {
                    bestPres = totalPres;
                    if (DEBUG) System.out.println("    New best");
                    System.out.println("    (TOP OOT) New best " + bestPres);
                }
                return;
            }

            minute += minTime;
            totalPres += curPres * minTime;

            for (int ridx = 0; ridx < 2; ++ridx) {
                if ((runners.r[ridx].travelTimeLeft -= minTime) == 0) {
                    // Arrived and turned on valve

                    Valve valve = valveList.get(activeValves.get(runners.r[ridx].travelToIdx));
                    curPres += valve.rate;
                    ++numOpen;
                }
            }
        }
        if (DEBUG) System.out.println("IS NOW " + runners);

        if (minute > maxMin) {
            System.out.println("Minute: " + minute);
            maxMin = minute;
        }

        // AFTER COMPLETING WITH ONE, COMPLETE THE OTHER AND LOOP


        // Whichever one is zero (or both), recursively check the next destination
        boolean hadValve = false;
        for (int ridx = 0; ridx < 2; ++ridx) {
            if (runners.r[ridx].travelTimeLeft == 0) {
                int valveIdx = runners.r[ridx].travelToIdx;
                String name = activeValves.get(valveIdx);
                if (DEBUG) System.out.println(ridx + ": Checking valve " + valveIdx + " (" + name + "), checked = " + checkedVal + " (" + checked + ")");

                for (int i = 0; i < activeValves.size(); ++i) {
                    if (DEBUG) System.out.println("    Loop " + i + ": checked = " + checked);
                    if (!checked.get(i)) {
                        hadValve = true;

                        Valve valve = valveList.get(activeValves.get(i));
                        int dist = valveDist.get(name + "-" + valve.name);
                        if (DEBUG) System.out.println("    Trying " + i + "(" + valve.name + "), dist = " + dist);
                        if (DEBUG) System.out.println("    checked = " + checked);

                        // Add one for time needed to open valve
                        ++dist;

                        // Check not enough time to get there and turn on, calculate how we would do just waiting
                        if (minute + dist >= 26) {
                            int tempTotal = totalPres + curPres * (26 - minute);

                            // Add in additional from other runner potentially arriving somewhere
                            int other = 1 - ridx;
                            int otherDest = runners.r[other].travelToIdx;
                            Valve otherValve = valveList.get(activeValves.get(otherDest));
                            int otherTimeLeft = runners.r[other].travelTimeLeft;
                            int otherTime = (26 - (minute + otherTimeLeft));
                            int otherExtra = 0;
                            if (otherTimeLeft > 0 && otherTime > 0) {
                                otherExtra = otherValve.rate * otherTime;
                            }
                            tempTotal += otherExtra;

                            if (DEBUG) System.out.println("    Not enough time, tempTotal = " + tempTotal);
                            //System.out.println("    Not enough time, tempTotal = " + tempTotal);
                            if (tempTotal > bestPres) {
                                bestPres = tempTotal;
                                if (DEBUG) System.out.println("    New best");
                                System.out.println("    (MAIN OOT) New best " + bestPres);
                                System.out.println("        minute = " + minute + ", ridx=" + ridx + ", dist = " + dist + ", oTimeLeft = " + otherTimeLeft
                                        + ", oTime = " + otherTime + ", oExtra = " + otherExtra + ", oRate = " + otherValve.rate + ", checked: " + checked.get(otherDest));
                                System.out.println("        RUNNERS ARE:    " + runners);
                            }

                            continue;
                        }

                        RunnerSet nextRunners = runners;
                        checked.set(i);
                        nextRunners.r[ridx].travelToIdx = i;
                        nextRunners.r[ridx].travelTimeLeft = dist;

                        if (DEBUG) System.out.println("    CALLING " + nextRunners);
                        part2CalcBig(nextRunners, bitsetToLong(checked), minute, numOpen, curPres, totalPres);

                        checked.clear(i);
                    }
                }
            }
        }

        // If nothing to process, then we're waiting for a runner to arrive, which will be the last valve.
        // We can then close off this line.

        if (!hadValve) {
            int t = Math.max(r1.travelTimeLeft, r2.travelTimeLeft);

            if (DEBUG) System.out.println("NO VALVE, numOpen = " + numOpen + ", checked = " + checked + ", t = " + t);
            if (DEBUG) System.out.println(runners);

            if (DEBUG) System.out.println(runners + ", minTime = " + minTime);
            if (t > 0) {
                // Check out of time
                if (minute + t >= 26) {
                    if (DEBUG) System.out.println("    OUT OF TIME");
                    //System.out.println("    OUT OF TIME");
                    totalPres += curPres * (26 - minute);
                    if (totalPres > bestPres) {
                        bestPres = totalPres;
                        if (DEBUG) System.out.println("    New best");
                        System.out.println("    (NO VALVE OOT) New best " + bestPres);
                    }
                    return;
                }

                minute += t;
                totalPres += curPres * t;

                for (int ridx = 0; ridx < 2; ++ridx) {
                    if (runners.r[ridx].travelTimeLeft > 0) {
                        runners.r[ridx].travelTimeLeft -= t;
                        // Arrived and turned on valve

                        Valve valve = valveList.get(activeValves.get(runners.r[ridx].travelToIdx));
                        curPres += valve.rate;
                        ++numOpen;
                    }
                }
            }

            if (numOpen != maxOpen) {
                System.out.println("SHOULDN'T HAPPEN! numOpen = " + numOpen + ", maxOpen = " + maxOpen);
                System.exit(0);
            }

            if (DEBUG) System.out.println("    END: All valves open, time left = " + (26 - minute) + ", cur=" + curPres + ", curtotal = " + totalPres);
            totalPres += curPres * (26 - minute);
            if (DEBUG) System.out.println(", final total is " + totalPres);
            if (totalPres > bestPres) {
                bestPres = totalPres;
                if (DEBUG) System.out.println("    New best");
                System.out.println("    (END) New best " + bestPres + ", minute = " + minute + ", curtotal = " + totalPres + ", maxPres = " + maxPres);
            }

            return;
        }

    }

    /**
     * Part 1 calculates the maximum pressure that can be released within a 30-minute window.
     * The algorithm involves iterating through all valves, determining the optimal sequence to open them
     * to maximize the total pressure released. It uses a recursive depth-first search approach to explore
     * all possible sequences of valve openings, considering the time constraints and the pressure each valve can release.
     * <p>
     * It tracks the current state (which valves are open, the current pressure, total pressure released so far) and
     * explores different paths by opening one more valve at a time. The recursion terminates when either all valves are opened
     * or there's not enough time left to open more valves. The method keeps track of the best pressure achieved across all paths.
     */
    void part1()
    {
        // Initialize a BitSet to track which valves have been checked
        BitSet checked = new BitSet(32);
        checked.set(0);

        // Recursively calculate the pressure
        part1CalcBig(0, checked.toLongArray()[0], 0, 0, 0, 0);
    }

    public void run(int part) {
        String filePath = "src/Day16/valves.txt";
        maxPres = 0;

        // Always include AA, which is always off and always first
        activeValves.add("AA");

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);

                ArrayList<String> rates = getMatches(s, "\\d+");
                int rate = Integer.parseInt(rates.get(0));
                maxPres += rate;
                if (rate != 0) {
                    ++maxOpen;
                }

                ArrayList<String> valves = getMatches(s, "[A-Z][A-Z]");
                String name = valves.get(0);
                valves.removeFirst();

                System.out.println("TUNNELS: " + valves);

                valveList.put(name, new Valve(rate, name, new StringArrayList(valves)));

                if (rate != 0) {
                    activeValves.add(name);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

//        auto time1 = chrono::high_resolution_clock::now();
        for (int i = 0; i < activeValves.size() - 1; ++i) {
            for (int j = i + 1; j < activeValves.size(); ++j) {
                String valve1 = activeValves.get(i);
                String valve2 = activeValves.get(j);
                int dist = calcDist(valve1, valve2);
                System.out.println("CALC: " + valve1 + " to " + valve2 + ": dist = " + dist);
                valveDist.put(valve1 + "-" + valve2, dist);
                valveDist.put(valve2 + "-" + valve1, dist);
            }
        }

        //for (auto x : valveDist) {
        //System.out.println(x.first + " " + x.second);
        //}
//        auto time2 = chrono::high_resolution_clock::now();

        if (part == 1) {
            part1();
        } else {
            part2();
        }

//        auto time3 = chrono::high_resolution_clock::now();

//        auto diff1 = std::chrono::duration_cast<std::chrono::milliseconds>(time2 - time1).count();
//        auto diff2 = std::chrono::duration_cast<std::chrono::milliseconds>(time3 - time1).count();

        System.out.println("Best was " + bestPres);
//        System.out.println("First phase: " + diff1);
//        System.out.println("Total time: " + diff2);

    }
}