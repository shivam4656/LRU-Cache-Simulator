import java.util.Scanner;

/**
 * Main - interactive command-line demo for LRUCache.
 *
 * Usage:
 *   javac *.java
 *   java Main
 *
 * Commands (case-insensitive):
 *   PUT <key> <value>   store a key-value pair
 *   GET <key>           retrieve a value; prints HIT <value> or MISS
 *   PRINT               display current cache state (MRU -> LRU)
 *   EXIT                quit the program
 */
public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt for capacity
        LRUCache cache = null;
        while (cache == null) {
            System.out.print("Enter cache capacity: ");
            String capLine = scanner.nextLine().trim();
            try {
                int cap = Integer.parseInt(capLine);
                cache = new LRUCache(cap);
                System.out.println("LRU cache created with capacity " + cap + ".");
            } catch (NumberFormatException e) {
                System.out.println("  [Error] Please enter a valid integer.");
            } catch (IllegalArgumentException e) {
                System.out.println("  [Error] " + e.getMessage());
            }
        }

        System.out.println("Commands: PUT <key> <value> | GET <key> | PRINT | EXIT");
        System.out.println("-----------------------------------------------------------");

        while (scanner.hasNextLine()) {
            System.out.print("> ");
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+");
            String   cmd   = parts[0].toUpperCase();

            try {
                switch (cmd) {

                    case "PUT": {
                        if (parts.length < 3) {
                            System.out.println("  Usage: PUT <key> <value>");
                            break;
                        }
                        int k = Integer.parseInt(parts[1]);
                        int v = Integer.parseInt(parts[2]);
                        cache.put(k, v);
                        System.out.println("  OK");
                        break;
                    }

                    case "GET": {
                        if (parts.length < 2) {
                            System.out.println("  Usage: GET <key>");
                            break;
                        }
                        int k      = Integer.parseInt(parts[1]);
                        int result = cache.get(k);
                        if (result == -1) {
                            System.out.println("  MISS");
                        } else {
                            System.out.println("  HIT " + result);
                        }
                        break;
                    }

                    case "PRINT": {
                        System.out.println("  [" + cache.stateAsString() + "]");
                        break;
                    }

                    case "EXIT": {
                        System.out.println("Bye");
                        scanner.close();
                        return;
                    }

                    default: {
                        System.out.println("  [Error] Unknown command: " + cmd);
                        System.out.println("  Commands: PUT <key> <value> | GET <key> | PRINT | EXIT");
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("  [Error] Key and value must be integers.");
                System.out.println("  Usage hint: PUT <int> <int> | GET <int>");
            }
        }

        System.out.println("Bye");
        scanner.close();
    }
}