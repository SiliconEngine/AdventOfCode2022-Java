package Day18;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Advent of Code 2022 challenge, Day 18.
 * Link: <a href="https://adventofcode.com/2022/day/18">...</a>
 * <p>
 * Challenge: Calculate total surface areas of set of cubes with adjacent faces.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }
    String make(String x, String y, String z)
    {
        return x + "," + y + "," + z;
    }

    String makeKey(ArrayList<String> list)
    {
        return list.get(0) + " - " + list.get(1) + " - " + list.get(2) + " - " + list.get(3);
    }

    enum FaceDir { DIR_XM, DIR_XP, DIR_YM, DIR_YP, DIR_ZM, DIR_ZP };

    static class Coord implements Cloneable {
        int x = 0;
        int y = 0;
        int z = 0;

        public Coord(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        boolean isLess(Coord other) {
            if (x != other.x)
                return x < other.x;
            if (y != other.y)
                return y < other.y;
            return z < other.z;
        }

        @Override
        public Object clone() {
            try {
                // Call clone from Object (shallow copy)
                Coord c = (Coord) super.clone();
//                c.x = this.x;
//                c.y = this.y;
//                c.z = this.z;
                return c;
            } catch (CloneNotSupportedException e) {
                // This should not happen since we are Cloneable
                throw new AssertionError();
            }
        }

        @Override
        public String toString() {
            return this.x + "," + this.y + "," + this.z;
        }

        @Override
        public boolean equals(Object obj) {
            Coord c = (Coord)obj;
            return this.x == c.x && this.y == c.y && this.z == c.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }

    static class Face {
        Coord c;
        String key;
        FaceDir dir;
    };

    static class Block {
        Coord c;
        Face[] faces = new Face[6];
        public Block(Coord c, Face face1, Face face2, Face face3, Face face4, Face face5, Face face6) {
            this.c = c;
            this.faces[0] = face1;
            this.faces[1] = face2;
            this.faces[2] = face3;
            this.faces[3] = face4;
            this.faces[4] = face5;
            this.faces[5] = face6;
        }
    };

    enum BlockStat { STAT_UNKNOWN, STAT_OUTSIDE, STAT_INSIDE };

    Face makeFace(Coord c, FaceDir dir)
    {
        Face face = new Face();
        face.dir = dir;
        face.c = c;
        ArrayList<String> faceList = new ArrayList<>();

        String xm = Integer.toString(c.x - 1) + ".5";
        String xp = Integer.toString(c.x) + ".5";
        String ym = Integer.toString(c.y - 1) + ".5";
        String yp = Integer.toString(c.y) + ".5";
        String zm = Integer.toString(c.z - 1) + ".5";
        String zp = Integer.toString(c.z) + ".5";

        switch (dir) {
            case DIR_XM:
                // xm face
                faceList.add(make(xm, ym, zm));
                faceList.add(make(xm, ym, zp));
                faceList.add(make(xm, yp, zm));
                faceList.add(make(xm, yp, zp));
                break;

            case DIR_XP:
                // xp face
                faceList.add(make(xp, ym, zm));
                faceList.add(make(xp, ym, zp));
                faceList.add(make(xp, yp, zm));
                faceList.add(make(xp, yp, zp));
                break;

            case DIR_YM:
                // ym face
                faceList.add(make(xm, ym, zm));
                faceList.add(make(xm, ym, zp));
                faceList.add(make(xp, ym, zm));
                faceList.add(make(xp, ym, zp));
                break;

            case DIR_YP:
                // yp face
                faceList.add(make(xm, yp, zm));
                faceList.add(make(xm, yp, zp));
                faceList.add(make(xp, yp, zm));
                faceList.add(make(xp, yp, zp));
                break;

            case DIR_ZM:
                // zm face
                faceList.add(make(xm, ym, zm));
                faceList.add(make(xp, ym, zm));
                faceList.add(make(xm, yp, zm));
                faceList.add(make(xp, yp, zm));
                break;

            case DIR_ZP:
                // zp face
                faceList.add(make(xm, ym, zp));
                faceList.add(make(xp, ym, zp));
                faceList.add(make(xm, yp, zp));
                faceList.add(make(xp, yp, zp));
                break;
        }

        Collections.sort(faceList);
        face.key = makeKey(faceList);
        return face;
    }

    Map<Coord, Block> blocks = new HashMap<>();
    Map<Coord, BlockStat> status = new HashMap<>();
    int max_x = -999, max_y = -999, max_z = -999, min_x = 999, min_y = 999, min_z = 999;

    boolean testOOR(Coord c) {
        return (c.x < min_x || c.x > max_x || c.y < min_x || c.y > max_x || c.z < min_x || c.z > max_x);
    }

    boolean testOutside(Face face) {
        Coord cur;

        cur = (Coord)face.c.clone();
        switch (face.dir) {
            case DIR_XM: --cur.x; break;
            case DIR_XP: ++cur.x; break;
            case DIR_YM: --cur.y; break;
            case DIR_YP: ++cur.y; break;
            case DIR_ZM: --cur.z; break;
            case DIR_ZP: ++cur.z; break;
        }

        Map<Coord, Boolean> visited = new HashMap<>();
        ArrayList<Coord> checked = new ArrayList<>();
        Queue<Coord> q = new LinkedList<>();
        q.add(cur);
        checked.add(cur);

        BlockStat stat = BlockStat.STAT_UNKNOWN;
DONE:   while (!q.isEmpty()) {
            cur = q.peek();
            System.out.println("    QUEUE: " + cur + " stat is " + status.getOrDefault(cur, BlockStat.STAT_UNKNOWN) + ", length is " + q.size());

            q.remove();
            visited.put(cur, true);

            if (status.getOrDefault(cur, BlockStat.STAT_UNKNOWN) != BlockStat.STAT_UNKNOWN) {
                stat = status.get(cur);
                break;
            }

            Coord[] testList = { (Coord)cur.clone(), (Coord)cur.clone(), (Coord)cur.clone(), (Coord)cur.clone(), (Coord)cur.clone(), (Coord)cur.clone() };
            --testList[0].x;
            ++testList[1].x;
            --testList[2].y;
            ++testList[3].y;
            --testList[4].z;
            ++testList[5].z;

            for (Coord tc : testList) {
                // Hit another block?
                if (blocks.get(tc) != null) {
                    System.out.println("        HIT: " + tc);
                    continue;
                }

                // Already visited?
                if (visited.get(tc) != null) {
                    System.out.println("        VISITED: " + tc);
                    continue;
                }

                // Out of range?
                if (testOOR(tc)) {
                    System.out.println("        OOR: " + tc);
                    stat = BlockStat.STAT_OUTSIDE;
                    break DONE;
                }

                System.out.println("        QUEUING: " + tc);
                q.add(tc);
                visited.put(tc, true);
                checked.add(tc);
            }
        }
        System.out.println("    DONE, stat = " + stat);

        // If no path outside, then must be inside
        if (stat == BlockStat.STAT_UNKNOWN) {
            stat = BlockStat.STAT_INSIDE;
        }

        // Update all checked blocks with what we just found out
        for (Coord c : checked) {
            System.out.println("    UPDATING + " + c + " to stat = " + stat);
            if (status.getOrDefault(c, BlockStat.STAT_UNKNOWN) != BlockStat.STAT_UNKNOWN && status.get(c) != stat) {
                System.out.println("CONTRADICTION: c = " + c + ", current stat is " + status.getOrDefault(c, BlockStat.STAT_UNKNOWN) + " updating to " + stat);
                System.exit(0);
            }

            status.put(c, stat);
        }

        System.out.println("    RETURNING STAT " + stat);
        return stat == BlockStat.STAT_OUTSIDE;
    }

    /**
     * Part 1: Calculates the total surface area of a set of cubes.
     * The method reads cube coordinates from a file, then generates and counts the unique faces of these cubes.
     * Each face is identified by its coordinates, and faces are considered unique if they are not shared between cubes.
     * The total count of unique faces gives the total exposed surface area.
     */
    public void part1() {
        String filePath = "src/Day18/blocks.txt";
        Map<String, Integer> faces = new HashMap<String, Integer>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                String[] coords = s.split(",");

                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                int z = Integer.parseInt(coords[2]);

                // For each cube, calculate the coordinates of its six faces (xm, xp, ym, yp, zm, zp)
                String xm = Integer.toString(x - 1) + ".5";
                String xp = Integer.toString(x) + ".5";
                String ym = Integer.toString(y - 1) + ".5";
                String yp = Integer.toString(y) + ".5";
                String zm = Integer.toString(z - 1) + ".5";
                String zp = Integer.toString(z) + ".5";

                ArrayList<ArrayList<String>> newFaces = new ArrayList<>();

                // xm face
                ArrayList<String> face1 = new ArrayList<>(Arrays.asList(make(xm, ym, zm), make(xm, ym, zp), make(xm, yp, zm), make(xm, yp, zp)));
                newFaces.add(face1);

                // xp face
                ArrayList<String> face2 = new ArrayList<>(Arrays.asList(make(xp, ym, zm), make(xp, ym, zp), make(xp, yp, zm), make(xp, yp, zp)));
                newFaces.add(face2);

                // ym face
                ArrayList<String> face3 = new ArrayList<>(Arrays.asList(make(xm, ym, zm), make(xm, ym, zp), make(xp, ym, zm), make(xp, ym, zp)));
                newFaces.add(face3);

                // yp face
                ArrayList<String> face4 = new ArrayList<>(Arrays.asList(make(xm, yp, zm), make(xm, yp, zp), make(xp, yp, zm), make(xp, yp, zp)));
                newFaces.add(face4);

                // zm face
                ArrayList<String> face5 = new ArrayList<>(Arrays.asList(make(xm, ym, zm), make(xp, ym, zm), make(xm, yp, zm), make(xp, yp, zm)));
                newFaces.add(face5);

                // zp face
                ArrayList<String> face6 = new ArrayList<>(Arrays.asList(make(xm, ym, zp), make(xp, ym, zp), make(xm, yp, zp), make(xp, yp, zp)));
                newFaces.add(face6);

                // Generate faces for each side of the cube and count them in the 'faces' map
                // Duplicate faces (shared between cubes) will have a count greater than 1
                for (int i = 0; i < 6; ++i) {
                    ArrayList<String> f = newFaces.get(i);
                    Collections.sort(f);
                    String k = makeKey(f);
                    faces.merge(k, 1, Integer::sum);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Iterate over the 'faces' map to count how many faces are unique (count == 1)
        // This count represents the total exposed surface area
        int count = 0;
        for (Map.Entry<String, Integer> e : faces.entrySet()) {
            System.out.println(e.getKey() + ": " + e.getValue());
            if (e.getValue() == 1)
                ++count;
        }

        System.out.println("count is " + count);
    }

    /**
     * Part 2: Calculates the exterior surface area of a set of cubes, considering only the faces exposed to the outside environment.
     * The method reads cube coordinates, creates a 3D grid representation, and determines which cube faces are external.
     * It checks each face of every cube to see if it has an unobstructed path to the outside, indicating it's an exterior face.
     * The count of such faces gives the total exterior surface area.
     */
    void part2() {
        String filePath = "src/Day18/blocks.txt";

        Map<String, Integer> faces = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);

                String[] coords = s.split(",");
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                int z = Integer.parseInt(coords[2]);
                System.out.println("    -> x = " + x + ", y = " + y + ", z = " + z);
                if (x > max_x)
                    max_x = x;
                if (y > max_y)
                    max_y = y;
                if (z > max_z)
                    max_z = z;
                if (x < min_x)
                    min_x = x;
                if (y < min_y)
                    min_y = y;
                if (z < min_z)
                    min_z = z;

                // For each cube, create and store its faces and corresponding block object
                Coord c = new Coord(x, y, z);
                Face face1 = makeFace(c, FaceDir.DIR_XM);
                Face face2 = makeFace(c, FaceDir.DIR_XP);
                Face face3 = makeFace(c, FaceDir.DIR_YM);
                Face face4 = makeFace(c, FaceDir.DIR_YP);
                Face face5 = makeFace(c, FaceDir.DIR_ZM);
                Face face6 = makeFace(c, FaceDir.DIR_ZP);

                faces.merge(face1.key, 1, Integer::sum);
                faces.merge(face2.key, 1, Integer::sum);
                faces.merge(face3.key, 1, Integer::sum);
                faces.merge(face4.key, 1, Integer::sum);
                faces.merge(face5.key, 1, Integer::sum);
                faces.merge(face6.key, 1, Integer::sum);

                Block block = new Block(c, face1, face2, face3, face4, face5, face6);

                blocks.put(c, block);
                status.put(c, BlockStat.STAT_UNKNOWN);

//                if (x == 8 && y == 11 && z == 6) {
//                    Coord chk = new Coord(8, 11, 6);
//                    BlockStat stat = status.get(chk);
//                    System.out.println("STATUS IS " + stat);
//                    System.exit(0);
//
//                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("START");
        int rawCount = 0;
        int outCount = 0;
        int debugCount = 100;

        // Iterate over all cubes to determine if each face is an exterior face
        for (Map.Entry<Coord, Block> b : blocks.entrySet()) {
            Block block = b.getValue();
            for (Face f : block.faces) {
//                System.out.println("Doing face: " + f.key + ", get = " + faces.get(f.key));
                if (faces.get(f.key) == 1) {
                    ++rawCount;
                    if (testOutside(f)) {
                        ++outCount;
                    }
                }
//                if (--debugCount == 0) System.exit(0);
            }
        }

        System.out.println("rawCount is " + rawCount);
        System.out.println("outCount is " + outCount);
    }
}