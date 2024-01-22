package Day7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.ArrayList;

/**
 * Advent of Code 2022 challenge, Day 7.
 * Link: <a href="https://adventofcode.com/2022/day/7">...</a>
 * <p>
 * Challenge: Interpret shell commands from log file and simulate file system, and find smallest
 * directory that would free up sufficient space to run an update.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.run(2);
    }

    enum EntryType { DirType, FileType }
    static class Data { }

    static class File extends Data {
        int size;
    }

    static class Dir extends Data {
        Entry parent;
        ArrayList<Entry> entries;
        int totalSize;
    }

    static class Entry {
        String name;
        Data data;
        EntryType type;
    }

    /**
     * Filesystem simulation class
     */
    static class FS {
        private Entry root;

        public FS() {
            root = new Entry();
            root.type = EntryType.DirType;
            root.name = "~";
            root.data = new Dir();
            ((Dir)root.data).parent = null;
            ((Dir)root.data).entries = new ArrayList<Entry>();
            ((Dir)root.data).totalSize = 0;
        }

        /**
         * Return root of filesystem
         */
        Entry getRoot() {
            return root;
        }

        /**
         * Add a new directory to the filesystem
         */
        Entry addDir(Entry parent, String name) {
            System.out.println("--> Adding dir " + name + " to " + parent.name);

            Entry newDir = new Entry();
            newDir.type = EntryType.DirType;
            newDir.name = name;
            newDir.data = new Dir();
            ((Dir)newDir.data).parent = parent;
            ((Dir)newDir.data).entries = new ArrayList<Entry>();
            ((Dir)newDir.data).totalSize = 0;
            ((Dir)parent.data).entries.add(newDir);
            System.out.println("    NEW: name = " + newDir.name + ", parent is " + ((Dir)newDir.data).parent.name);
            return newDir;
        }

        /**
         * Add a new file to the filesystem
         */
        Entry addFile(Entry dir, String name, int size) {
            System.out.println("--> Adding file " + name + ", size " + size + " to " + dir.name + " (" + System.identityHashCode(dir) + ")");
            Entry ent = new Entry();
            ent.type = EntryType.FileType;
            ent.name = name;
            ent.data = new File();
            ((File)ent.data).size = size;
            ((Dir)dir.data).entries.add(ent);

            Entry parent = dir;
            do {
                ((Dir)parent.data).totalSize += size;
                parent = ((Dir)parent.data).parent;
            } while (parent != null);

            return ent;
        }

        /**
         * Change current directory
         */
        Entry chDir(Entry entry, String name) {
            System.out.println("CHDIR " + name + " FROM " + entry.name);
            if (name.equals("/")) {
                return root;
            }

            if (name.equals("..")) {
                return ((Dir) entry.data).parent;
            }

            for (Entry ent : ((Dir)entry.data).entries) {
                if (ent.type == EntryType.DirType && ent.name.equals(name)) {
                    return ent;
                }
            }

            System.out.println("NOT FOUND: " + name);
            dumpFs();
            System.exit(0);
            return null;
        }

        void dumpNode(Entry ent) {
            String parent = ((Dir)ent.data).parent != null ? ((Dir)ent.data).parent.name : "";

            System.out.println("DIR NODE (" + System.identityHashCode(ent) + "): " + ent.name + ", parent : " + System.identityHashCode(((Dir)ent.data).parent)
                    + ", has " + ((Dir)ent.data).entries.size() + " nodes, total size is "
                    + ((Dir)ent.data).totalSize);
            for (Entry sub : ((Dir)ent.data).entries) {
                System.out.println("    Sub (" + System.identityHashCode(sub) + "): " + sub.type + " " + sub.name);
            }
            for (Entry sub : ((Dir)ent.data).entries) {
                if (sub.type == EntryType.DirType) {
                    dumpNode(sub);
                }
            }
            System.out.println();
        }

        void dumpFs()
        {
            System.out.println("---------------- DUMP" + System.lineSeparator() + "ROOT: " + root.name);
            dumpNode(root);
        }

        /**
         * Mutable integer wrapper class
         */
        class MutInt {
            int i = 0;
        }

        /**
         * Recursively searches through the file system starting from a given directory entry,
         * looking for directories whose total size is less than a specified limit (100,000 in this context).
         * This method updates a running total of the sizes of all directories found that meet this criterion.
         */
        void findLess(Entry ent, MutInt total) {
            if (((Dir)ent.data).totalSize < 100000) {
                System.out.println("Found " + ent.name + " total size is " + ((Dir)ent.data).totalSize);
                total.i += ((Dir)ent.data).totalSize;
            }
            for (Entry sub : ((Dir)ent.data).entries) {
                if (sub.type == EntryType.DirType) {
                    findLess(sub, total);
                }
            }
        }

        /**
         * Initiates the process of finding all directories with a total size of at most 100,000.
         * It utilizes the findLess method to recursively traverse the file system,
         * starting from the root, and accumulate the total size of all qualifying directories.
         * Once completed, it prints the grand total size of these directories.
         */
        void findAll() {
            MutInt total = new MutInt();
            total.i = 0;
            findLess(root, total);
            System.out.println("Grand total is " + total.i);
        }

        void dsp(Entry ent) {
            System.out.println("Size " + ent.name + " total size is " + ((Dir)ent.data).totalSize);
            for (Entry sub : ((Dir)ent.data).entries) {
                if (sub.type == EntryType.DirType)
                    dsp(sub);
            }
        }

        void dspSizes() {
            dsp(root);
        }

        /**
         * Recursively searches through the file system starting from a given directory entry,
         * looking for directories whose total size meets or exceeds a specified threshold.
         */
        void findIt(Entry ent, int need, ArrayList<Entry> nodes) {
            if (((Dir)ent.data).totalSize >= need) {
                System.out.println("Found " + ent.name + " total size is " + ((Dir)ent.data).totalSize);
                nodes.add(ent);
            }
            for (Entry sub : ((Dir)ent.data).entries) {
                if (sub.type == EntryType.DirType) {
                    findIt(sub, need, nodes);
                }
            }
        }

        /**
         * Identifies and reports the smallest directory that, if deleted, would free up enough space
         * to satisfy a specified size requirement.
         */
        void findSmallest(int need) {
            ArrayList<Entry> nodes = new ArrayList<>();
            findIt(root, need, nodes);

            Collections.sort(nodes, new Comparator<Entry>() {
                @Override
                public int compare(Entry a, Entry b) {
                    return ((Dir)a.data).totalSize - ((Dir)b.data).totalSize;
                }
            });

            for (Entry ent : nodes) {
                System.out.println("Candidate: " + ent.name + ", size: " + ((Dir)ent.data).totalSize);
            }

            System.out.println("Smallest is " + ((Dir)(nodes.getFirst().data)).totalSize);
        }

    }

    private static final int ST_NORMAL = 1;
    private static final int ST_LS = 2;

    /**
     * Part 1: Reads input from a file representing filesystem commands and structure,
     * builds a representation of the filesystem, and then finds directories
     * whose combined size is at most 100000.
     * <p>
     * Part 2: Find the smallest directory that, if deleted,
     * would free up enough space on the filesystem to run the update.
     * This involves calculating the free space, determining the additional space needed,
     * and identifying the smallest directory that meets this requirement.
     */
    public void run(int part) {
        String filePath = "src/Day7/log.txt";
        int state = ST_NORMAL;
        FS fs = new FS();
        Entry curDir = fs.getRoot();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                String tokens[] = s.split(" ");
                for (;;) {
                    switch (state) {
                    case ST_NORMAL:
                        if (tokens[0].equals("$")) {
                            String cmd = tokens[1];
                            System.out.println("command: " + cmd);
                            if (cmd.equals("cd")) {
                                curDir = fs.chDir(curDir, tokens[2]);
                                System.out.println("DID CHDIR, is now " + curDir.name + " (" + System.identityHashCode(curDir) + ")");
                            } else if (cmd.equals("ls")) {
                                state = ST_LS;
                            }
                        }
                        break;
                    case ST_LS:
                        if (tokens[0].equals("$")) {
                            state = ST_NORMAL;
                            continue;
                        }

                        if (tokens[0].equals("dir")) {
                            fs.addDir(curDir, tokens[1]);
                            break;
                        }

                        System.out.println("LS state, s is " + s);
                        System.out.println("tokens[0] is " + tokens[0]);

                        int size = Integer.parseInt(tokens[0]);
                        String name = tokens[1];
                        System.out.println("ls mode: " + name + ": " + size);
                        fs.addFile(curDir, name, size);
                        break;
                    }

                    break;
                }
                System.out.println("0: " + tokens[0]);
            }

            fs.dumpFs();

            if (part == 1) {
                fs.findAll();
            } else {
                // Part 2
                fs.dspSizes();

                Entry root = fs.getRoot();
                int freeSpace = 70000000 - ((Dir) root.data).totalSize;
                int updSize = 30000000;
                int needSize = updSize - freeSpace;
                System.out.println("Need size is " + needSize);
                fs.findSmallest(needSize);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}