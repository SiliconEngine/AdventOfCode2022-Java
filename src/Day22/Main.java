package Day22;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Advent of Code 2022 challenge, Day 22.
 * Link: <a href="https://adventofcode.com/2022/day/22">...</a>
 * <p>
 * Challenge: Simulate a map on the surface of a cube and trace path used by monkeys.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part2();
    }

    interface TransposeFunc
    {
        public int[] transpose(int length, int row, int col);
    }

    class Adjoin {
        Face face;
        int newDir;
        TransposeFunc transFunc;
        public Adjoin(Face face, int newDir, TransposeFunc transFunc) {
            this.face = face;
            this.newDir = newDir;
            this.transFunc = transFunc;
        }
    };

    class Face {
        int num;
        int rowOff;
        int colOff;
        ArrayList<String> rows = new ArrayList<>();
        Adjoin[] adjoin = new Adjoin[4];
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (String s : rows) {
                sb.append(s).append(System.lineSeparator());
            }
            return sb.toString();
        }
    };

//    ostream& operator+(ostream& out, const Face& f) {
//        for (String s : f.rows) {
//            System.out.println(s);
//        }
//        return out;
//    }

    ArrayList<StringBuilder> board = new ArrayList<>();

    //enum Dir { D_RIGHT, D_DOWN, D_LEFT, D_UP };
    final int D_RIGHT = 0;
    final int D_DOWN = 1;
    final int D_LEFT = 2;
    final int D_UP = 3;

     /**
     * Part 1: Reads the board layout and movement instructions from a file,
     * then simulates the movement across the board. The movement accounts for
     * wrapping around the board edges and stopping at walls. The final position
     * and facing direction are used to calculate a score representing the puzzle solution.
     */
    public void part1() {
        String filePath = "src/Day22/map.txt";
        String path_s = "";

        String[] dirs = new String[4];
        dirs[D_RIGHT] = "RIGHT";
        dirs[D_UP] = "UP";
        dirs[D_LEFT] = "LEFT";
        dirs[D_DOWN] = "DOWN";

        int[] scoreDir = new int[4];
        scoreDir[D_RIGHT] = 0;
        scoreDir[D_DOWN] = 1;
        scoreDir[D_LEFT] = 2;
        scoreDir[D_UP] = 3;

        int[] turnLeft = new int[4];
        turnLeft[D_RIGHT] = D_UP;
        turnLeft[D_UP] = D_LEFT;
        turnLeft[D_LEFT] = D_DOWN;
        turnLeft[D_DOWN] = D_RIGHT;

        int[] turnRight = new int[4];
        turnRight[D_RIGHT] = D_DOWN;
        turnRight[D_DOWN] = D_LEFT;
        turnRight[D_LEFT] = D_UP;
        turnRight[D_UP] = D_RIGHT;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                if (s.isEmpty()) {
                    path_s = br.readLine();
                    break;
                }
                board.add(new StringBuilder(s));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(path_s);

        // Process each character in the path string to build the path array
        ArrayList<String> path = new ArrayList<>();
        StringBuilder accum = new StringBuilder();
        for (char c : path_s.toCharArray()) {
            if (c >= '0' && c <= '9') {
                accum.append(c);
            } else {
                if (!accum.isEmpty()) {
                    path.add(accum.toString());
                    accum.setLength(0);
                }
                path.add((String)"" + c);
            }
        }
        if (!accum.isEmpty())
            path.add(accum.toString());

        int row = 0;
        int col = 0;
        int dir = D_RIGHT;
        for (col = 0; col < board.get(0).length(); ++col) {
            if (board.get(0).charAt(col) == '.')
                break;
        }
        System.out.println("Initial coordinates: [" + row + ", " + col + "]");

        // Main loop to process each instruction and move on the map accordingly
        for (String p : path) {
            System.out.println("Path: " + p + ", coord: [" + row + ", " + col + "], dir = " + dirs[dir]);
            if (p.equals("L")) {
                dir = turnLeft[dir];
                System.out.println("    DIR now " + dirs[dir]);
                continue;
            }
            if (p.equals("R")) {
                dir = turnRight[dir];
                System.out.println("    DIR now " + dirs[dir]);
                continue;
            }

            int amt = Integer.parseInt(p);
            while (amt-- != 0) {
                int saveR = row;
                int saveC = col;

                switch (dir) {
                    case D_UP:
                        --row;
                        System.out.println("    Move up to row " + row);
                        if (row < 0 || col >= board.get(row).length() || board.get(row).charAt(col) == ' ') {
                            row = board.size() - 1;
                            while (col >= board.get(row).length() || board.get(row).charAt(col) == ' ')
                                --row;
                            System.out.println("        Wraparound, row is now " + row);
                        }
                        break;
                    case D_DOWN:
                        ++row;
                        System.out.println("    Move down to row " + row);
                        if (row >= board.size() || col >= board.get(row).length() || board.get(row).charAt(col) == ' ') {
                            row = 0;
                            while (col >= board.get(row).length() || board.get(row).charAt(col) == ' ')
                                ++row;
                            System.out.println("        Wraparound, row is now " + row);
                        }
                        break;
                    case D_LEFT:
                        --col;
                        System.out.println("    Move left to col " + col);
                        if (col < 0 || board.get(row).charAt(col) == ' ') {
                            col = board.get(row).length() - 1;
                            while (board.get(row).charAt(col) == ' ')
                                --col;
                            System.out.println("        Wraparound, col is now " + col);
                        }
                        break;
                    case D_RIGHT:
                        ++col;
                        System.out.println("    Move right to col " + col);
                        if (col >= board.get(row).length() || board.get(row).charAt(col) == ' ') {
                            col = 0;
                            while (board.get(row).charAt(col) == ' ')
                                ++col;
                            System.out.println("        Wraparound, col is now " + col);
                        }
                        break;
                }

                if (board.get(row).charAt(col) == '#') {
                    row = saveR;
                    col = saveC;
                    System.out.println("    Hit wall, back to [" + row + ", " + col + "]");
                    break;
                }
            }
        }

        int score = 1000 * (row + 1) + 4 * (col + 1) + scoreDir[dir];
        System.out.println("score is " + score);
    }

    /**
     * Part 2 -  This method extends the simulation from part1() to a cube-shaped map.
     * It reads the same initial map and instructions, but now the movement
     * wraps around the faces of a cube. The algorithm accounts for the cube's geometry
     * when moving off one face onto another. The final score is calculated based on the
     * adjusted position and orientation considering the cube structure.
     */
    public void part2() {
        String filePath = "src/Day22/map.txt";
        int length = 50; // 4;
        String path_s = "";

        String[] dirs = new String[4];
        dirs[D_RIGHT] = "RIGHT";
        dirs[D_UP] = "UP";
        dirs[D_LEFT] = "LEFT";
        dirs[D_DOWN] = "DOWN";

        int[] scoreDir = new int[4];
        scoreDir[D_RIGHT] = 0;
        scoreDir[D_DOWN] = 1;
        scoreDir[D_LEFT] = 2;
        scoreDir[D_UP] = 3;

        int[] turnLeft = new int[4];
        turnLeft[D_RIGHT] = D_UP;
        turnLeft[D_UP] = D_LEFT;
        turnLeft[D_LEFT] = D_DOWN;
        turnLeft[D_DOWN] = D_RIGHT;

        int[] turnRight = new int[4];
        turnRight[D_RIGHT] = D_DOWN;
        turnRight[D_DOWN] = D_LEFT;
        turnRight[D_LEFT] = D_UP;
        turnRight[D_UP] = D_RIGHT;

        Face[] faces = { new Face(), new Face(), new Face(), new Face(), new Face(), new Face() };

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                if (s.isEmpty()) {
                    path_s = br.readLine();
                    break;
                }

                board.add(new StringBuilder(s));
            }
            Face face1 = new Face(), face2 = new Face(), face3 = new Face(), face4 = new Face(), face5 = new Face(), face6 = new Face();
            int offset = 0;

            for (int r = 0; r < length; ++r) {
                offset = length;
                s = board.get(r).substring(offset, offset+length);
                face1.rows.add(s);
                face1.rowOff = 0;
                face1.colOff = offset;

                offset += length;
                s = board.get(r).substring(offset, offset+length);
                face2.rows.add(s);
                face2.rowOff = 0;
                face2.colOff = offset;
            }

            for (int r = length; r < length*2; ++r) {
                offset = length;
                s = board.get(r).substring(offset, offset+length);
                face3.rows.add(s);
                face3.rowOff = length;
                face3.colOff = offset;
            }

            for (int r = length*2; r < length*3; ++r) {
                offset = 0;
                s = board.get(r).substring(offset, offset+length);
                face4.rows.add(s);
                face4.rowOff = length*2;
                face4.colOff = offset;

                offset += length;
                s = board.get(r).substring(offset, offset+length);
                face5.rows.add(s);
                face5.rowOff = length*2;
                face5.colOff = offset;
            }

            for (int r = length * 3; r < length * 4; ++r) {
                offset = 0;
                s = board.get(r).substring(offset, offset+length);
                face6.rows.add(s);
                face6.rowOff = length * 3;
                face6.colOff = offset;
            }

            face1.adjoin[D_UP] = new Adjoin(face6, D_RIGHT, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    ++row;
                    row = col;
                    col = 0;
                    return new int[] { row, col };
                }
            });
            face1.adjoin[D_DOWN] = new Adjoin(face3, D_DOWN, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    --row;
                    row = 0;
                    return new int[] { row, col };
                }
            });
            face1.adjoin[D_LEFT] = new Adjoin(face4, D_RIGHT, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    ++col;
                    col = 0;
                    row = (length - 1) - row;
                    return new int[] { row, col };
                }
            });
            face1.adjoin[D_RIGHT] = new Adjoin(face2, D_RIGHT, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    --col;
                    col = 0;
                    return new int[] { row, col };
                }
            });

            face2.adjoin[D_UP] = new Adjoin(face6, D_UP, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    ++row;
                    row = length - 1;
                    return new int[] { row, col };
                }
            });
            face2.adjoin[D_DOWN] = new Adjoin(face3, D_LEFT, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    --row;
                    row = col;
                    col = length - 1;
                    return new int[] { row, col };
                }
            });
            face2.adjoin[D_LEFT] = new Adjoin(face1, D_LEFT, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    ++col;
                    col = length - 1;
                    return new int[] { row, col };
                }
            });
            face2.adjoin[D_RIGHT] = new Adjoin(face5, D_LEFT, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    --col;
                    row = (length - 1) - row;
                    col = length - 1;
                    return new int[] { row, col };
                }
            });

            face3.adjoin[D_UP] = new Adjoin(face1, D_UP, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    ++row;
                    row = length - 1;
                    return new int[] { row, col };
                }
            });
            face3.adjoin[D_DOWN] = new Adjoin(face5, D_DOWN, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    --row;
                    row = 0;
                    return new int[] { row, col };
                }
            });
            face3.adjoin[D_LEFT] = new Adjoin(face4, D_DOWN, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    ++col;
                    col = row;
                    row = 0;
                    return new int[] { row, col };
                }
            });
            face3.adjoin[D_RIGHT] = new Adjoin(face2, D_UP, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    --col;
                    col = row;
                    row = length - 1;
                    return new int[] { row, col };
                }
            });

            face4.adjoin[D_UP] = new Adjoin(face3, D_RIGHT, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    ++row;
                    row = col;
                    col = 0;
                    return new int[] { row, col };
                }
            });
            face4.adjoin[D_DOWN] = new Adjoin(face6, D_DOWN, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    --row;
                    row = 0;
                    return new int[] { row, col };
                }
            });
            face4.adjoin[D_LEFT] = new Adjoin(face1, D_RIGHT, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    ++col;
                    col = 0;
                    row = (length - 1) - row;
                    return new int[] { row, col };
                }
            });
            face4.adjoin[D_RIGHT] = new Adjoin(face5, D_RIGHT, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    --col;
                    col = 0;
                    return new int[] { row, col };
                }
            });

            face5.adjoin[D_UP] = new Adjoin(face3, D_UP, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    ++row;
                    row = (length - 1);
                    return new int[] { row, col };
                }
            });
            face5.adjoin[D_DOWN] = new Adjoin(face6, D_LEFT, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    --row;
                    row = col;
                    col = length - 1;
                    return new int[] { row, col };
                }
            });
            face5.adjoin[D_LEFT] = new Adjoin(face4, D_LEFT, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    ++col;
                    col = length - 1;
                    return new int[] { row, col };
                }
            });
            face5.adjoin[D_RIGHT] = new Adjoin(face2, D_LEFT, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    --col;
                    col = length - 1;
                    row = (length - 1) - row;
                    return new int[] { row, col };
                }
            });

            face6.adjoin[D_UP] = new Adjoin(face4, D_UP, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    ++row;
                    row = length - 1;
                    return new int[] { row, col };
                }
            });
            face6.adjoin[D_DOWN] = new Adjoin(face2, D_DOWN, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    --row;
                    row = 0;
                    return new int[] { row, col };
                }
            });
            face6.adjoin[D_LEFT] = new Adjoin(face1, D_DOWN, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    ++col;
                    col = row;
                    row = 0;
                    return new int[] { row, col };
                }
            });
            face6.adjoin[D_RIGHT] = new Adjoin(face5, D_UP, new TransposeFunc() {
                public int[] transpose(int length, int row, int col) {
                    --col;
                    col = row;
                    row = length - 1;
                    return new int[] { row, col };
                }
            });


            faces[0] = face1;
            faces[1] = face2;
            faces[2] = face3;
            faces[3] = face4;
            faces[4] = face5;
            faces[5] = face6;


            int n = 0;
            for (Face f : faces) {
                f.num = ++n;
                System.out.println(System.lineSeparator() + "FACE: " + f.num);
                System.out.println(f);
            }

            System.out.println(path_s);

            ArrayList<String> path = new ArrayList<>();
            StringBuilder accum = new StringBuilder();
            for (char c : path_s.toCharArray()) {
                if (c >= '0' && c <= '9') {
                    accum.append(c);
                } else {
                    if (! accum.isEmpty()) {
                        path.add(accum.toString());
                        accum.setLength(0);
                    }
                    path.add((String)"" + c);
                }
            }
            if (! accum.isEmpty())
                path.add(accum.toString());

            Face face = faces[0];

            int row = 0;
            int col = 0;
            int dir = D_RIGHT;
            for (col = 0; col < board.get(0).length(); ++col) {
                if (face.rows.get(0).charAt(col) == '.')
                    break;
            }
            System.out.println("Initial coordinates: face " + face.num + " [" + row + ", " + col + "]");

            for (String p : path) {
                System.out.println("Path: " + p + ", face " + face.num + ", coord: [" + row + ", " + col + "] , dir = " + dirs[dir]);
                if (p.equals("L")) {
                    dir = turnLeft[dir];
                    System.out.println("    DIR now " + dirs[dir]);
                    continue;
                }
                if (p.equals("R")) {
                    dir = turnRight[dir];
                    System.out.println("    DIR now " + dirs[dir]);
                    continue;
                }

                int amt = Integer.parseInt(p);
                while (amt-- != 0) {
                    int saveR = row;
                    int saveC = col;
                    Face saveFace = face;
                    int saveDir = dir;

                    switch (dir) {
                        case D_UP:
                            --row;
                            System.out.println("    Move up to row " + row);
                            if (row < 0) {
                                Adjoin a = face.adjoin[dir];
                                int[] ret = a.transFunc.transpose(length, row, col);
                                row = ret[0];
                                col = ret[1];
                                face = a.face;
                                dir = a.newDir;
                                System.out.println("        Wraparound, moved to face " + face.num + " coords [" + row + ", " + col + "], dir = " + dirs[dir]);
                            }
                            break;
                        case D_DOWN:
                            ++row;
                            System.out.println("    Move down to row " + row);
                            if (row >= length) {
                                Adjoin a = face.adjoin[dir];
                                int[] ret = a.transFunc.transpose(length, row, col);
                                row = ret[0];
                                col = ret[1];
                                face = a.face;
                                dir = a.newDir;
                                System.out.println("        Wraparound, moved to face " + face.num + " coords [" + row + ", " + col + "], dir = " + dirs[dir]);
                            }
                            break;
                        case D_LEFT:
                            --col;
                            System.out.println("    Move left to col " + col);
                            if (col < 0) {
                                Adjoin a = face.adjoin[dir];
                                int[] ret = a.transFunc.transpose(length, row, col);
                                row = ret[0];
                                col = ret[1];
                                face = a.face;
                                dir = a.newDir;
                                System.out.println("        Wraparound, moved to face " + face.num + " coords [" + row + ", " + col + "], dir = " + dirs[dir]);
                            }
                            break;
                        case D_RIGHT:
                            ++col;
                            System.out.println("    Move right to col " + col);
                            if (col >= length) {
                                Adjoin a = face.adjoin[dir];
                                int[] ret = a.transFunc.transpose(length, row, col);
                                row = ret[0];
                                col = ret[1];
                                face = a.face;
                                dir = a.newDir;
                                System.out.println("        Wraparound, moved to face " + face.num + " coords [" + row + ", " + col + "], dir = " + dirs[dir]);
                            }
                            break;
                    }

                    if (face.rows.get(row).charAt(col) == '#') {
                        row = saveR;
                        col = saveC;
                        dir = saveDir;
                        face = saveFace;
                        System.out.println("    Hit wall, back to face " + face.num + " coords [" + row + ", " + col + "], dir = " + dirs[dir]);
                        break;
                    }
                }
            }

            System.out.println("End, at face " + face.num + " coords [" + row + ", " + col + "], dir = " + dirs[dir]);

            int adjRow = face.rowOff + row;
            int adjCol = face.colOff + col;

            System.out.println("adjRow = " + adjRow + ", adjCol = " + adjCol);

            int score = 1000 * (adjRow + 1) + 4 * (adjCol + 1) + scoreDir[dir];
            System.out.println("score is " + score);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}