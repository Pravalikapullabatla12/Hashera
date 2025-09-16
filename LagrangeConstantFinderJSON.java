import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LagrangeConstantFinderJSON {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in, StandardCharsets.UTF_8);
        StringBuilder sb = new StringBuilder();

        System.out.println("Paste JSON input and press Ctrl+Z (Windows) or Ctrl+D (Linux/Mac) to finish:");

        // Read multiline JSON until EOF
        while (sc.hasNextLine()) {
            sb.append(sc.nextLine()).append("\n");
        }
        sc.close();

        String input = sb.toString().trim();
        if (input.isEmpty()) {
            System.out.println("No input provided.");
            return;
        }

        // --- Parse keys (n, k) ---
        int n = extractInt(input, "\"n\"");
        int k = extractInt(input, "\"k\"");

        // --- Parse (x,y) points ---
        List<Integer> xList = new ArrayList<>();
        List<BigInteger> yList = new ArrayList<>();

        int count = 0;
        String[] lines = input.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String line = lines[i].trim();

            if (line.matches("\"\\d+\": \\{")) { // e.g. "1": {
                String key = line.substring(1, line.indexOf("\"", 1));
                int x = Integer.parseInt(key);

                // Next 2 lines → base & value
                String baseLine = lines[++i].trim();
                String valueLine = lines[++i].trim();

                int base = Integer.parseInt(baseLine.split(":")[1].replace("\"", "").replace(",", "").trim());
                String value = valueLine.split(":")[1].replace("\"", "").replace(",", "").trim();

                BigInteger y = new BigInteger(value, base);

                count++;
                if (count <= k) {
                    xList.add(x);
                    yList.add(y);
                }
            }
        }

        // --- Compute constant term using Lagrange interpolation ---
        BigInteger constant = lagrangeInterpolationAtZero(xList, yList);

        System.out.println("Using first " + k + " points out of " + n);
        System.out.println("Constant term (c) = " + constant.toString());
    }

    // Utility: extract integer from JSON-like input
    private static int extractInt(String input, String key) {
        int idx = input.indexOf(key);
        if (idx == -1) return 0;
        String sub = input.substring(idx);
        sub = sub.substring(sub.indexOf(":") + 1).trim();
        String num = sub.split("[,}]")[0].trim();
        return Integer.parseInt(num);
    }

    // Lagrange interpolation at x=0
    private static BigInteger lagrangeInterpolationAtZero(List<Integer> x, List<BigInteger> y) {
        int k = x.size();
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger term = y.get(i);
            BigInteger num = BigInteger.ONE;
            BigInteger den = BigInteger.ONE;

            for (int j = 0; j < k; j++) {
                if (i == j) continue;
                num = num.multiply(BigInteger.valueOf(-x.get(j)));
                den = den.multiply(BigInteger.valueOf(x.get(i) - x.get(j)));
            }

            BigInteger li = num.divide(den); // exact division
            term = term.multiply(li);
            result = result.add(term);
        }
        return result;
    }
}
