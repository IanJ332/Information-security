import java.util.HashMap;
import java.util.Scanner;

/**
 * A simple program to calculate hamming distance.
 * <br>For self-use only, so can't take numbers that are too big,
 * only simple input validation, no clear prompts, etc.
 *
 * @author Yan Chen
 */
public class Hamming {
    private static final HashMap<String, String> DATABASE = new HashMap<>();
    private static final double THRESHOLD = 0.32;

    public static void main(String[] args) {
        // try-with-resource so it will be closed properly
        try (Scanner in = new Scanner(System.in)) {
            System.out.println("=====\nEnrollment phase:");
            execute(in, Hamming::enroll);
            System.out.println("=====\nRecognition phase:");
            execute(in, Hamming::recognize);
            System.out.println("Goodbye!");
        } catch (Exception e) {
            System.out.println("Something wrong.");
            System.out.println(e.getMessage());
        }
    }

    /**
     * Repeat a method until user says no.
     *
     * @param in     scanner to get user input
     * @param method the method being executed
     */
    private static void execute(Scanner in, MyMethod method) {
        boolean enroll = DATABASE.isEmpty();
        do {
            System.out.print("Name: ");
            String name = getName(in, enroll);
            System.out.print("Iris code (in hex please): ");
            String iris = getIris(in);

            method.run(name, iris);

            System.out.print("More data? (y/n): ");
            String input = in.nextLine();
            if (input.equalsIgnoreCase("n")) break;
        } while (true);
    }

    /**
     * Get the name (repeat until getting valid name).
     *
     * @param in     scanner to get user input
     * @param enroll whether it's for enrollment phase or not
     * @return valid name:
     * <ul>
     *     <li>Name should not be empty</li>
     *     <li>when in recognition phase, name also need to be in database</li>
     * </ul>
     */
    private static String getName(Scanner in, boolean enroll) {
        do {
            String result = in.nextLine();
            if (result.isBlank())
                System.out.print("Empty input! \nPlease enter a name: ");
            else if (!enroll && !DATABASE.containsKey(result))
                System.out.print("Sorry, name not found. \nPlease enter a name again: ");
            else return result;
        } while (true);
    }

    /**
     * Get the iris code in binary (repeat until getting valid code).
     *
     * @return iris code in binary
     */
    private static String getIris(Scanner in) {
        do {
            String result = in.nextLine();
            String binary = hexToBin(result);
            if (binary != null)
                return binary;
            System.out.print("Invalid input! \nPlease enter a hex number (less than 63 bits): ");
        } while (true);
    }

    /**
     * Enrollment phase: record data (name and iris code in hex) to database
     *
     * @param name person can get access
     * @param iris the iris code (in binary)
     */
    private static void enroll(String name, String iris) {
        DATABASE.put(name, iris);
        System.out.printf(">> %s's iris code (in binary) = %s recorded\n", name, iris);
    }

    /**
     * Recognition phase: recognize the iris by computing hamming distance.
     * Give access if distance > 0.32.
     *
     * @param name who is trying to be authenticated
     * @param iris the iris code (in binary)
     */
    private static void recognize(String name, String iris) {
        String expect = DATABASE.get(name);
        double hamming = hammingDistance(iris, expect);
        System.out.printf("Hamming Distance = %.2f\n", hamming);
        // Accept iris scan as match if distance < 0.32 (L22 P13)
        String result = hamming < THRESHOLD ? "granted" : "denied";
        System.out.printf("Access %s for %s\n", result, name);
    }

    /**
     * Convert a hex string to binary.
     * Using long, so the range is -2^63 to 2^63 (big enough for 63-bit)
     * If you want to do a bigger one, can convert every 4 bytes (8 hex digits, 32 bits) using long
     *
     * @param hex string in hex
     * @return binary string, or null if conversion failed
     */
    private static String hexToBin(String hex) {
        try {
            return Long.toBinaryString(Long.parseLong(hex, 16));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Compute hamming distance between two data
     *
     * @param s1 first data (in binary)
     * @param s2 second data (in binary)
     * @return hamming distance in double
     */
    private static double hammingDistance(String s1, String s2) {
        // Convert input to binary and padding if necessary
        String[] s = setup(s1, s2);
        int mismatch = 0;
        for (int i = 0; i < s[0].length(); i++) {
            if (s[0].charAt(i) != s[1].charAt(i))
                mismatch++;
        }
        return mismatch * 1.0 / s[0].length();
    }

    /**
     * Make the input to the same length.
     * Will pad with 0's before the binary string that is shorter.
     *
     * @param s1 first data (in binary)
     * @param s2 s2 second data (in binary)
     * @return a string array with 2 strings
     */
    private static String[] setup(String s1, String s2) {
        int numOfPadding = Math.abs(s1.length() - s2.length());
        String padding = "0".repeat(numOfPadding);
        // If first string longer, pad second string;
        // Otherwise, pad first string
        return s1.length() > s2.length() ?
                new String[]{s1, padding + s2} :
                new String[]{padding + s1, s2};
    }

    /**
     * Interface to call back a method.
     */
    interface MyMethod {
        void run(String name, String iris);
    }
}