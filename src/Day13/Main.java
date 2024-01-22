package Day13;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Comparator;
import java.util.ArrayList;

/**
 * Advent of Code 2022 challenge, Day 13.
 * Link: <a href="https://adventofcode.com/2022/day/13">...</a>
 * <p>
 * Challenge: Decode nested packet format and determine "decoder key"
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    enum Type {T_SCALAR, T_VECTOR}

    static class Data {
    }

    static class Scalar extends Data {
        int scalar;
    }

    static class EntryList extends Data {
        ArrayList<Entry> list;
    }

    static class Entry {
        Type type;
        Entry parent;
        Data data;
    }

    String dumpEntry(Entry ent) {
        StringBuilder s = new StringBuilder("[");
        boolean first = true;
        for (Entry e : ((EntryList) ent.data).list) {
            if (!first) {
                s.append(",");
            }
            first = false;
            if (e.type == Type.T_SCALAR) {
                s.append(Integer.toString(((Scalar) e.data).scalar));
            } else {
                s.append(dumpEntry(e));
            }
        }
        s.append("]");
        return s.toString();
    }

    enum Result {R_GOOD, R_BAD, R_EQUAL}

    static String fmtResult(Result r) {
        if (r == Result.R_GOOD) {
            return "GOOD";
        } else if (r == Result.R_BAD) {
            return "BAD";
        } else if (r == Result.R_EQUAL) {
            return "EQUAL";
        }
        return "";
    }

    private Result cmpEntry(Entry first, Entry second) {
        ArrayList<Entry> list1 = ((EntryList) first.data).list;
        ArrayList<Entry> list2 = ((EntryList) second.data).list;
        int minSize = Math.min(list1.size(), list2.size());
        System.out.println("    Size: " + list1.size() + " / " + list2.size());

        for (int i = 0; i < minSize; ++i) {
            Entry ent1 = list1.get(i);
            Entry ent2 = list2.get(i);

            if (ent1.type == Type.T_SCALAR && ent2.type == Type.T_SCALAR) {
                if (((Scalar) ent1.data).scalar < ((Scalar) ent2.data).scalar) {
                    System.out.println("    SCALARS IN GOOD ORDER");
                    return Result.R_GOOD;
                }
                if (((Scalar) ent1.data).scalar > ((Scalar) ent2.data).scalar) {
                    System.out.println("    SCALARS IN BAD ORDER");
                    return Result.R_BAD;
                }
                System.out.println("    SCALARS EQUAL");

            } else if (ent1.type == Type.T_VECTOR && ent2.type == Type.T_VECTOR) {
                System.out.println("    COMPARING VECTORS");
                Result r = cmpEntry(ent1, ent2);
                System.out.println("    BACK, RESULT WAS " + fmtResult(r));
                if (r != Result.R_EQUAL) {
                    System.out.println("    RETURNING ABOVE");
                    return r;
                }

            } else {
                // One list, one scalar

                Entry temp = new Entry();
                temp.type = Type.T_VECTOR;
                temp.parent = null;
                temp.data = new EntryList();
                ((EntryList) temp.data).list = new ArrayList<Entry>();
                Result r;

                if (ent1.type == Type.T_VECTOR) {
                    System.out.println("    FIRST WAS VECTOR, 2nd SCALAR");
                    ((EntryList) temp.data).list.add(ent2);
                    r = cmpEntry(ent1, temp);
                } else {
                    System.out.println("    FIRST WAS SCALAR, 2nd VECTOR");
                    ((EntryList) temp.data).list.add(ent1);
                    r = cmpEntry(temp, ent2);
                }

                System.out.println("    RESULT WAS " + fmtResult(r));
                if (r != Result.R_EQUAL) {
                    return r;
                }
            }
        }

        // All equal, see if second shorter
        if (list2.size() > list1.size()) {
            System.out.println("    SECOND LONGER, IS GOOD");
            return Result.R_GOOD;
        }

        if (list2.size() < list1.size()) {
            System.out.println("    FIRST LONGER, IS BAD");
            return Result.R_BAD;
        }

        System.out.println("    LENGTHS WERE EQUAL, RETURNING EQUAL");
        return Result.R_EQUAL;
    }

    static class Pair {
        Entry first;
        Entry second;

        public Pair(Entry f, Entry s) {
            this.first = f;
            this.second = s;
        }
    }

    Entry parseEnt(String s) {
        Entry ent = null;
        Entry curEnt = null;
        int n = -1;
        for (char c : s.toCharArray()) {
            System.out.println("TOKEN: " + c);

            if (c == '[') {
                Entry newEnt = new Entry();
                System.out.println("    NEW LIST " + newEnt);
                newEnt.type = Type.T_VECTOR;
                newEnt.data = new EntryList();
                ((EntryList) newEnt.data).list = new ArrayList<Entry>();
                newEnt.parent = curEnt;

                if (curEnt != null) {
                    ((EntryList) curEnt.data).list.add(newEnt);
                }

                curEnt = newEnt;
                if (ent == null) {
                    ent = newEnt;
                }
            }

            if (c == ',' || c == ']') {
                if (n >= 0) {
                    System.out.println("    HAVE SCALAR " + n);
                    Entry newEnt = new Entry();
                    newEnt.type = Type.T_SCALAR;
                    newEnt.data = new Scalar();
                    ((Scalar) newEnt.data).scalar = n;
                    newEnt.parent = curEnt;
                    ((EntryList) curEnt.data).list.add(newEnt);
                    n = -1;
                }
            }

            if (c >= '0' && c <= '9') {
                if (n < 0) {
                    n = 0;
                }
                n = n * 10 + (c - '0');
            }

            if (c == ']') {
                System.out.println("    END LIST");
                curEnt = curEnt.parent;
            }
        }

        System.out.println("DUMPING " + ent);
        System.out.println(dumpEntry(ent));
        return ent;
    }

    public Comparator sortcmp = new Comparator<Entry>() {
        @Override
        public int compare(Entry a, Entry b) {
            Result r = cmpEntry(a, b);
            return r == Result.R_EQUAL ? 0 : (r == Result.R_GOOD ? -1 : 1);
        }
    };

    final String DECODE1 = "[[2]]";
    final String DECODE2 = "[[6]]";

    /**
     * Part 1: Process each pair of packet entries in the given list, identifying
     * those that are already in the correct order based on the problem's rules. It then
     * calculates and returns the sum of the indices of these correctly ordered pairs.
     */
    public void part1() {
        String filePath = "src/Day13/packets.txt";
        ArrayList<Pair> cmpList = new ArrayList<>();
        Entry first = null;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                if (s.isEmpty()) {
                    continue;
                }

                Entry ent = null;
                Entry curEnt = null;
                int n = -1;
                for (char c : s.toCharArray()) {
                    System.out.println("TOKEN: " + c);

                    if (c == '[') {
                        Entry newEnt = new Entry();
                        System.out.println("    NEW LIST " + newEnt);
                        newEnt.type = Type.T_VECTOR;
                        newEnt.data = new EntryList();
                        ((EntryList) newEnt.data).list = new ArrayList<Entry>();
                        newEnt.parent = curEnt;

                        if (curEnt != null) {
                            ((EntryList) curEnt.data).list.add(newEnt);
                        }

                        curEnt = newEnt;
                        if (ent == null) {
                            ent = newEnt;
                        }

                    }

                    if (c == ',' || c == ']') {
                        if (n >= 0) {
                            System.out.println("    HAVE SCALAR " + n);
                            Entry newEnt = new Entry();
                            newEnt.type = Type.T_SCALAR;
                            newEnt.data = new Scalar();
                            ((Scalar) newEnt.data).scalar = n;
                            newEnt.parent = curEnt;
                            ((EntryList) curEnt.data).list.add(newEnt);
                            n = -1;
                        }
                    }

                    if (c >= '0' && c <= '9') {
                        if (n < 0) {
                            n = 0;
                        }
                        n = n * 10 + (c - '0');
                    }

                    if (c == ']') {
                        System.out.println("    END LIST");
                        curEnt = curEnt.parent;
                    }
                }

                System.out.println("DUMPING " + ent);
                System.out.print(dumpEntry(ent));

                //System.out.println"STOP";
                //string x;
                //cin >> x;

                if (first == null) {
                    first = ent;
                } else {
                    cmpList.add(new Pair(first, ent));
                    first = null;
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Have " + cmpList.size() + " pairs");

        int i = 0;
        int goodSum = 0;
        for (Pair p : cmpList) {
            ++i;
            Result r = cmpEntry(p.first, p.second);
            System.out.println("RESULT #" + i + ": " + fmtResult(r));
            if (r == Result.R_GOOD) {
                goodSum += i;
            }
        }
        System.out.println("Sum is " + goodSum);
    }

    /**
     * Part 2: Organize the entire set of packets, including divider packets, into a sequence that
     * meets the ordering criteria specified. After sorting, it identifies the positions of key divider packets
     * and computes their product, which acts as the decoder key for the distress signal.
     */
    public void part2() {
        String filePath = "src/Day13/packets.txt";
        ArrayList<Entry> packetList = new ArrayList<>();
        String s;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                if (s.isEmpty()) {
                    continue;
                }

                Entry ent = parseEnt(s);
                packetList.add(ent);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Integrates special divider packets into the list, as required by the distress
        // signal protocol for complete packet analysis.
        s = DECODE1;
        packetList.add(parseEnt(s));
        s = DECODE2;
        packetList.add(parseEnt(s));

        // Put each pair of packets is in the correct order based on the ordering rules.
        packetList.sort(sortcmp);

        System.out.println();
        System.out.println("SORT:");

        int i = 0;
        int prod = 1;
        for (Entry ent : packetList) {
            ++i;
            s = dumpEntry(ent);
            System.out.println("Entry #" + i + ": " + s);

            if (s.equals(DECODE1) || s.equals(DECODE2)) {
                prod *= i;
            }
        }

        System.out.println("Prod is " + prod);
    }
}