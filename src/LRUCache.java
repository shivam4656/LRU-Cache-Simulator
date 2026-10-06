import java.util.HashMap;

/**
 * LRUCache - a Least Recently Used cache backed by a HashMap and a
 * custom doubly linked list.
 *
 * The HashMap maps each key to its Node in O(1).
 * The doubly linked list keeps nodes ordered from most-recently-used
 * (just after the dummy head) to least-recently-used (just before the
 * dummy tail). Dummy sentinel nodes eliminate all null-pointer edge
 * cases when adding or removing at the boundaries.
 *
 * All public operations run in O(1) amortised time.
 */
public class LRUCache {

    private final int capacity;
    private final HashMap<Integer, Node> map;

    /** Dummy head sentinel - the node AFTER this is the most-recently used. */
    private final Node head;
    /** Dummy tail sentinel - the node BEFORE this is the least-recently used. */
    private final Node tail;

    /**
     * Constructs an LRUCache with the given maximum capacity.
     *
     * @param capacity maximum number of entries; must be >= 1
     * @throws IllegalArgumentException if capacity <= 0
     */
    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Capacity must be > 0, but got: " + capacity);
        }
        this.capacity = capacity;
        this.map      = new HashMap<>();

        // Sentinel nodes - key/value are unused for these two special nodes
        head      = new Node(0, 0);
        tail      = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    /**
     * Returns the value for the given key, or -1 if not present.
     * A successful lookup moves the key to the front (most-recently used).
     *
     * @param key the key to look up
     * @return the cached value, or -1 if the key is absent
     */
    public int get(int key) {
        Node node = map.get(key);
        if (node == null) {
            return -1;         // cache miss
        }
        moveToFront(node);     // refresh recency
        return node.value;
    }

    /**
     * Inserts or updates a key-value pair in the cache.
     *
     * If the key already exists, its value is updated and the entry is moved
     * to the front. If the key is new and the cache is at capacity, the
     * least-recently-used entry (at the tail) is evicted first. The new or
     * updated entry is always placed at the front.
     *
     * @param key   the key
     * @param value the value to store
     */
    public void put(int key, int value) {
        Node existing = map.get(key);

        if (existing != null) {
            existing.value = value;
            moveToFront(existing);
            return;
        }

        // Evict the LRU entry if we are at capacity
        if (map.size() == capacity) {
            Node lru = tail.prev;   // node just before dummy tail
            removeNode(lru);
            map.remove(lru.key);
        }

        // Insert the new entry at the front
        Node newNode = new Node(key, value);
        map.put(key, newNode);
        addToFront(newNode);
    }

    /**
     * Returns a snapshot of the cache contents ordered from most-recently
     * used to least-recently used.
     * Example: "3:300 -> 1:100 -> 2:200"
     *
     * @return formatted cache state string, or "(empty)" if the cache is empty
     */
    public String stateAsString() {
        StringBuilder sb = new StringBuilder();
        Node cur = head.next;
        while (cur != tail) {
            if (sb.length() > 0) sb.append(" -> ");
            sb.append(cur.key).append(':').append(cur.value);
            cur = cur.next;
        }
        return sb.length() == 0 ? "(empty)" : sb.toString();
    }

    /**
     * Returns the current number of entries in the cache.
     */
    public int size() {
        return map.size();
    }

    // ---- Private helpers ------------------------------------------------

    /** Inserts node immediately after the dummy head (most-recent position). */
    private void addToFront(Node node) {
        node.next      = head.next;
        node.prev      = head;
        head.next.prev = node;
        head.next      = node;
    }

    /** Removes node from its current list position by relinking neighbours. */
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    /** Moves an existing node to the front (removeNode + addToFront). */
    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }
}