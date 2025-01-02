Secret Finder
A Java program that computes the constant term 
𝑐
c of a polynomial using the Lagrange interpolation method. This program accepts input in JSON format and supports parsing with either the org.json or Gson library.

Setup and Installation
Option 1: Using Maven
Open the pom.xml file in your project.
Add the required dependencies for JSON parsing:
For org.json:
xml
Copy code
<dependency>
    <groupId>org.json</groupId>
    <artifactId>json</artifactId>
    <version>20210307</version>
</dependency>
For Gson:
xml
Copy code
<dependency>
    <groupId>com.google.code.gson</groupId>
    <artifactId>gson</artifactId>
    <version>2.8.9</version>
</dependency>
Save the pom.xml file.
Run the following Maven command to download the dependencies:
sh
Copy code
mvn install
Option 2: Manually Download the JAR
Download the JAR files:
org.json JAR
Gson JAR
Add the downloaded JAR to your project’s classpath:
For IntelliJ IDEA or Eclipse:
Go to project settings.
Add the JAR in the dependencies section.
For Command Line: Use the -cp option to include the JAR during compilation:
sh
Copy code
javac -cp json-20210307.jar Secret.java
java -cp .:json-20210307.jar Secret
How to Use
Input Format
The program accepts a JSON object as input. The structure includes:

keys: Specifies the total number of roots (n) and the minimum number of roots required (k).
Numeric keys representing the roots:
Each root includes:
base: Base of the encoded 
𝑦
y-value.
value: Encoded 
𝑦
y-value.
Example Input
json
Copy code
{
  "keys": {
    "n": 4,
    "k": 3
  },
  "1": {
    "base": "10",
    "value": "4"
  },
  "2": {
    "base": "2",
    "value": "111"
  },
  "3": {
    "base": "10",
    "value": "12"
  },
  "6": {
    "base": "4",
    "value": "213"
  }
}
Running the Program
Compile the program:
sh
Copy code
javac SecretFinder.java
Execute the program:
sh
Copy code
java SecretFinder
Provide the JSON input when prompted.
How It Works
Input Parsing:

Parses the JSON input using org.json or Gson.
Decodes 
𝑦
y-values using their respective bases.
Lagrange Interpolation:

Computes the constant term 
𝑐
c of the polynomial using modular arithmetic.
Avoids modular inversion errors with a prime modulus.
Output:

Displays the calculated constant term.
Error Handling
The program gracefully handles cases where the modular inverse cannot be computed and displays an appropriate error message.
Ensure the input JSON is well-formed and all necessary keys (n, k, base, value) are provided.
Complete Code
java
Copy code
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.math.BigInteger;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class SecretFinder {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter JSON input:");
        String input = scanner.nextLine();

        BigInteger result = findConstant(input);
        System.out.println("The constant term (secret) is: " + result);
    }

    private static BigInteger findConstant(String input) {
        Gson gson = new Gson();
        JsonObject json = gson.fromJson(input, JsonObject.class);
        JsonObject keys = json.getAsJsonObject("keys");
        int k = keys.get("k").getAsInt();

        Map<Integer, BigInteger> points = new TreeMap<>();
        for (String key : json.keySet()) {
            if (key.matches("\\d+")) {
                JsonObject point = json.getAsJsonObject(key);
                int x = Integer.parseInt(key);
                int base = point.get("base").getAsInt();
                String value = point.get("value").getAsString();
                BigInteger y = new BigInteger(value, base);
                points.put(x, y);
            }
        }

        return lagrangeInterpolation(points, k);
    }

    private static BigInteger lagrangeInterpolation(Map<Integer, BigInteger> points, int k) {
        BigInteger result = BigInteger.ZERO;
        BigInteger modulus = BigInteger.valueOf(1L).shiftLeft(256);

        for (Map.Entry<Integer, BigInteger> entry1 : points.entrySet()) {
            int xi = entry1.getKey();
            BigInteger yi = entry1.getValue();

            BigInteger li = BigInteger.ONE;
            for (Map.Entry<Integer, BigInteger> entry2 : points.entrySet()) {
                int xj = entry2.getKey();
                if (xi != xj) {
                    BigInteger numerator = BigInteger.valueOf(-xj);
                    BigInteger denominator = BigInteger.valueOf(xi - xj).modInverse(modulus);
                    li = li.multiply(numerator).multiply(denominator).mod(modulus);
                }
            }

            result = result.add(yi.multiply(li)).mod(modulus);
        }

        return result;
    }
}
Notes
Replace org.json operations with Gson equivalents for simplicity and flexibility.
Use modular arithmetic carefully to avoid runtime exceptions.
