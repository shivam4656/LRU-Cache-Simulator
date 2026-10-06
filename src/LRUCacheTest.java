/**
 * LRUCacheTest - plain-Java unit tests for LRUCache (no external framework).
 *
 * Compile: javac *.java
 * Run:     java LRUCacheTest
 */
public class LRUCacheTest {

    private static int passed = 0;
    private static int failed = 0;

    private static void assertEqual(String name, int expected, int actual) {
        if (expected == actual) {
            System.out.println("  PASS: " + name);
            passed++;
        } else {
            System.out.println("  FAIL: " + name
                + " -- expected " + expected + " but got " + actual);
            failed++;
        }
    }

    private static void assertTrue(String name, boolean cond) {
        if (cond) {
            System.out.println("  PASS: " + name);
            passed++;
        } else {
            System.out.println("  FAIL: " + name + " -- condition was false");
            failed++;
        }
    }

    // ------------------------------------------------------------------

    /** 1. Basic put then get returns correct value. */
    static void testBasicPutAndGet() {
        System.out.println("\n[Test 1] Basic put then get");
        LRUCache c = new LRUCache(3);
        c.put(1, 100); c.put(2, 200); c.put(3, 300);
        assertEqual("get(1) == 100", 100, c.get(1));
        assertEqual("get(2) == 200", 200, c.get(2));
        assertEqual("get(3) == 300", 300, c.get(3));
    }

    /** 2. Eviction removes the LRU key when capacity is exceeded. */
    static void testEvictionRemovesLRU() {
        System.out.println("\n[Test 2] Eviction removes the LRU entry");
        LRUCache c = new LRUCache(2);
        c.put(1, 10);
        c.put(2, 20);
        c.put(3, 30);   // evicts key 1 (LRU)
        assertEqual("get(1) == -1 (evicted)", -1, c.get(1));
        assertEqual("get(2) == 20 (present)",  20, c.get(2));
        assertEqual("get(3) == 30 (inserted)", 30, c.get(3));
    }

    /** 3. Updating an existing key moves it to front - no spurious eviction. */
    static void testUpdateMovesToFront() {
        System.out.println("\n[Test 3] Updating an existing key moves it to front");
        LRUCache c = new LRUCache(2);
        c.put(1, 10);
        c.put(2, 20);
        c.put(1, 999);  // update key 1; key 2 becomes LRU
        c.put(3, 30);   // evicts key 2
        assertEqual("get(1) == 999 (not evicted)", 999, c.get(1));
        assertEqual("get(2) == -1  (evicted)",      -1, c.get(2));
        assertEqual("get(3) == 30  (inserted)",     30, c.get(3));
    }

    /** 4. get() refreshes recency and changes what gets evicted next. */
    static void testGetRefreshesRecency() {
        System.out.println("\n[Test 4] get() refreshes recency");
        LRUCache c = new LRUCache(2);
        c.put(1, 10);
        c.put(2, 20);
        c.get(1);       // key 1 is now MRU; key 2 is LRU
        c.put(3, 30);   // evicts key 2
        assertEqual("get(2) == -1 (became LRU)", -1, c.get(2));
        assertEqual("get(1) == 10 (protected)",   10, c.get(1));
        assertEqual("get(3) == 30 (inserted)",    30, c.get(3));
    }

    /** 5. Capacity = 1 - second put evicts the first immediately. */
    static void testCapacityOne() {
        System.out.println("\n[Test 5] Capacity = 1");
        LRUCache c = new LRUCache(1);
        c.put(1, 10);
        assertEqual("get(1) == 10 before eviction", 10, c.get(1));
        c.put(2, 20);
        assertEqual("get(1) == -1 (evicted)",       -1, c.get(1));
        assertEqual("get(2) == 20 (only entry)",    20, c.get(2));
    }

    /** 6. Getting a missing key returns -1. */
    static void testGetMissingKey() {
        System.out.println("\n[Test 6] get() on missing key returns -1");
        LRUCache c = new LRUCache(5);
        assertEqual("get(42) on empty cache",    -1, c.get(42));
        c.put(1, 100);
        assertEqual("get(99) on non-empty cache", -1, c.get(99));
    }

    /** 7. IllegalArgumentException for capacity <= 0. */
    static void testInvalidCapacity() {
        System.out.println("\n[Test 7] IllegalArgumentException for capacity <= 0");
        boolean threw0 = false, threwNeg = false;
        try { new LRUCache(0);  } catch (IllegalArgumentException e) { threw0   = true; }
        try { new LRUCache(-5); } catch (IllegalArgumentException e) { threwNeg = true; }
        assertTrue("capacity 0 throws",  threw0);
        assertTrue("capacity -5 throws", threwNeg);
    }

    /** 8. stateAsString() reflects MRU-to-LRU order. */
    static void testStateAsStringOrder() {
        System.out.println("\n[Test 8] stateAsString() order");
        LRUCache c = new LRUCache(3);
        c.put(1, 10); c.put(2, 20); c.put(3, 30);
        String s = c.stateAsString();
        assertTrue("starts with 3:30 (MRU)", s.startsWith("3:30"));
        assertTrue("ends with 1:10 (LRU)",   s.endsWith("1:10"));
        c.get(1);  // key 1 becomes MRU
        s = c.stateAsString();
        assertTrue("after get(1), starts with 1:10", s.startsWith("1:10"));
    }

    /** 9. size() tracks entries correctly across puts and evictions. */
    static void testSizeTracking() {
        System.out.println("\n[Test 9] size() tracks entries correctly");
        LRUCache c = new LRUCache(3);
        assertEqual("initially 0",          0, c.size());
        c.put(1, 1);
        assertEqual("1 after first put",    1, c.size());
        c.put(2, 2); c.put(3, 3);
        assertEqual("3 at capacity",        3, c.size());
        c.put(4, 4);   // eviction
        assertEqual("still 3 after evict", 3, c.size());
    }

    /** 10. Chained evictions across multiple puts. */
    static void testChainedEvictions() {
        System.out.println("\n[Test 10] Chained evictions");
        LRUCache c = new LRUCache(2);
        c.put(1, 1); c.put(2, 2);
        c.put(3, 3);   // evicts 1
        c.put(4, 4);   // evicts 2
        c.put(5, 5);   // evicts 3
        assertEqual("key 1 evicted", -1, c.get(1));
        assertEqual("key 2 evicted", -1, c.get(2));
        assertEqual("key 3 evicted", -1, c.get(3));
        assertEqual("key 4 present",  4, c.get(4));
        assertEqual("key 5 present",  5, c.get(5));
    }

    // ------------------------------------------------------------------

    public static void main(String[] args) {
        System.out.println("============================================");
        System.out.println("  LRUCache Test Suite");
        System.out.println("============================================");

        testBasicPutAndGet();
        testEvictionRemovesLRU();
        testUpdateMovesToFront();
        testGetRefreshesRecency();
        testCapacityOne();
        testGetMissingKey();
        testInvalidCapacity();
        testStateAsStringOrder();
        testSizeTracking();
        testChainedEvictions();

        System.out.println("\n============================================");
        int total = passed + failed;
        System.out.printf("  Results: %d / %d passed%n", passed, total);
        if (failed == 0) {
            System.out.println("  ALL TESTS PASSED");
        } else {
            System.out.println("  " + failed + " FAILED -- see above");
        }
        System.out.println("============================================");

        if (failed > 0) System.exit(1);
    }
}