package Day25;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Advent of Code 2022 challenge, Day 25.
 * Link: <a href="https://adventofcode.com/2022/day/25">...</a>
 * <p>
 * Challenge: Convert "SNAFU"-encoded strings to and from integers.
 *
 * @author Tim Behrendsen
 * @version 1.0
 */

public class Main {
    public static void main(String[] args) {
        Main main = new Main();
        main.part1();
    }

    class Digits {
        int[] a = new int[100];
    }

    /**
     * Converts a SNAFU (Special Numeral-Analogue Fuel Units) string to its decimal equivalent.
     * SNAFU is a numeral system based on powers of 5 with unique character representations:
     * '2', '1', '0', '-', and '=' correspond to 2, 1, 0, -1, and -2, respectively.
     *
     * The method iterates through each character of the SNAFU string from left to right,
     * converting each character to its decimal value and accumulating the result considering
     * the powers of 5.
     *
     * @param snafu The SNAFU string to be converted to decimal.
     * @return The decimal equivalent of the SNAFU string.
     */
    long toDec(String snafu) {
        long n = 0;
        for (char c : snafu.toCharArray()) {
            int val = 0;
            if (c >= '0' && c <= '2') {
                val = c - '0';
            } else if (c == '-') {
                val = -1;
            } else if (c == '=') {
                val = -2;
            }

            n = n * 5 + val;
            //System.out.println(c + ": " + val + ", n is now " + n);
        }

        return n;
    }

    String dsp(Digits digits) {
        String s = "";
        int leftmost = 0;
        for (int i = 100; --i >= 0; ) {
            if (digits.a[i] != 0) {
                leftmost = i;
                break;
            }
        }

        boolean first = true;
        for (int i = leftmost; i >= 0; --i) {
            if (!first)
                s += ", ";
            first = false;
            s += Integer.toString(digits.a[i]);
        }

        return s;
    }

    String cvt(Digits digits) {
        String sym = "=-012";
        StringBuilder s = new StringBuilder();
        int leftmost = 0;
        for (int i = 100; --i >= 0; ) {
            if (digits.a[i] != 0) {
                leftmost = i;
                break;
            }
        }
        for (int i = leftmost; i >= 0; --i)
            s.append(sym.charAt(digits.a[i] + 2));
        return s.toString();
    }

    class IntPair {
        int first;
        int second;
        public IntPair(int f, int s) {
            first = f;
            second = s;
        }
        public String toString() {
            return "[" + first + ", " + second + "]";
        }
    }

    /**
     * Converts a decimal number to its SNAFU (Special Numeral-Analogue Fuel Units) representation.
     * The SNAFU format uses a base-5 numbering system with digits represented as '2', '1', '0', '-', and '='.
     *
     * This method applies a modified base conversion algorithm. It processes the decimal number
     * by repeatedly dividing it by 5 and determining the remainder. Each remainder is then
     * mapped to its corresponding SNAFU digit. The process handles borrowings and carryovers
     * for negative remainders.
     *
     * @param dec The decimal number to be converted to SNAFU format.
     * @return The SNAFU representation of the given decimal number.
     */
    String toSnafu(long dec)
    {
        long n = dec;
        int base = 5;
        Digits digits = new Digits();
        int place = 0;
        IntPair[] sdigits = new IntPair[] {
            new IntPair(0, 0),
            new IntPair(0, 1),
            new IntPair(0, 2),
            new IntPair(1, -2),
            new IntPair(1, -1),
        };

        while (n != 0) {
            int m = (int)(n % base);
            digits.a[place] += sdigits[m].second;
            digits.a[place + 1] += sdigits[m].first;
            ++place;

            for (int chk = 0; chk <= place; ++chk) {
                int nwrk = digits.a[chk] % base;
                if (nwrk > 2) {
                    //System.out.println("[POS: nwrk = " + nwrk + "] pair=" + sdigits[nwrk] + " " + dsp(digits));
                    digits.a[chk] -= nwrk;
                    digits.a[chk] += sdigits[nwrk].second;
                    digits.a[chk + 1] += sdigits[nwrk].first;
                    //System.out.println("[FIX: nwrk = " + nwrk + "] " + dsp(digits));
                }
                if (nwrk < -2) {
                    System.out.println("[NEG: nwrk = " + nwrk + "]");
                    // Equivalent: Add 5 to lsd, subtract 1 from msd
                    digits.a[chk] += 5;
                    digits.a[chk + 1] -= 1;
                    System.out.println("[FIX: nwrk = " + nwrk + "] " + dsp(digits));
                    System.exit(0);
                }
            }

            n = n / base;
            //System.out.println(digits.size() + ": " + m + ", sdigits = [" + sdigits[m].first + ", " + sdigits[m].second + "], n is now " + n);
            //System.out.println(place + ": " + dsp(digits) + ", n = " + n);
        }

        return cvt(digits);
    }

    public void part1() {
        String filePath = "src/Day25/snafu.txt";

        long total = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String s;
            while ((s = br.readLine()) != null) {
                System.out.println(s);

                //System.out.println(s + ": " + toDec(s));
                long dec = toDec(s);
                total += dec;

                String chk = toSnafu(dec);
                if (! chk.equals(s)) {
                    System.out.println("BAD: " + s + ", chk = " + chk);
                    System.exit(0);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Total is " + total);
        System.out.println("Snafu is " + toSnafu(total) + " (" + toDec(toSnafu(total)) + ")");

        //String snafu = "1=-0-2";
        //System.out.println(snafu + ": " + toDec(snafu));

        //char sym[5] = { '=', '-', '0', '1', '2' };

        String sym = "ABCDEFGHIJKL";
        String snafu = "";
        //srand(time(0));
        //long dec = rand();
        //long dec = 314159265;

        //System.out.println(dec + ": " + cvt(digits) + " (" + dsp(digits) + ")");

        //for (long x = 123555; x < 1235557; x += 777) {
        //System.out.println(x + ": " + toSnafu(x));
        //}
    }
}