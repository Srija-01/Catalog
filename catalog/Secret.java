import java.math.BigInteger;
import java.util.*;

public class Secret {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Enter the number of roots (n): ");
        int n = scanner.nextInt();

        System.out.println("Enter the minimum number of roots required to solve (k): ");
        int k = scanner.nextInt();

        Map<Integer, BigInteger> points = new TreeMap<>();

        System.out.println("Enter the roots in the format: <x> <base> <value>");
        for (int i = 0; i < n; i++) {
            System.out.println("Root " + (i + 1) + ":");
            int x = scanner.nextInt();
            int base = scanner.nextInt();
            String value = scanner.next();
            BigInteger y = new BigInteger(value, base);
            points.put(x, y);
        }

        try {
            BigInteger secret = findConstant(points, k);
            System.out.println("The constant term (secret) is: " + secret);
        } catch (ArithmeticException e) {
            System.out.println("Error: Unable to compute the modular inverse. Ensure the input is valid.");
        }
    }

    private static BigInteger findConstant(Map<Integer, BigInteger> points, int k) {
        return lagrangeInterpolation(points, k);
    }

    private static BigInteger lagrangeInterpolation(Map<Integer, BigInteger> points, int k) {
        BigInteger result = BigInteger.ZERO;
        BigInteger modulus = BigInteger.probablePrime(256, new Random()); // Use a prime modulus

        for (Map.Entry<Integer, BigInteger> entry1 : points.entrySet()) {
            int xi = entry1.getKey();
            BigInteger yi = entry1.getValue();

            BigInteger li = BigInteger.ONE;
            for (Map.Entry<Integer, BigInteger> entry2 : points.entrySet()) {
                int xj = entry2.getKey();
                if (xi != xj) {
                    BigInteger numerator = BigInteger.valueOf(-xj);
                    BigInteger denominator = BigInteger.valueOf(xi - xj);

                    // Use modInverse safely
                    denominator = denominator.mod(modulus);
                    if (!denominator.gcd(modulus).equals(BigInteger.ONE)) {
                        throw new ArithmeticException("Denominator and modulus are not coprime.");
                    }
                    denominator = denominator.modInverse(modulus);

                    li = li.multiply(numerator).multiply(denominator).mod(modulus);
                }
            }

            result = result.add(yi.multiply(li)).mod(modulus);
        }

        return result;
    }
}
