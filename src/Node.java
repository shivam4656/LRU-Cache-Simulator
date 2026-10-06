/**
 * Node - a single element in the doubly linked list used by LRUCache.
 *
 * Each node stores a key-value pair and carries prev/next pointers so it
 * can be spliced into or removed from the list in O(1) time.
 */
public class Node {
    int  key;
    int  value;
    Node prev;
    Node next;

    /**
     * Creates a node with the given key and value.
     * prev and next are left null; they are wired up by LRUCache.
     *
     * @param key   the cache key
     * @param value the cached value
     */
    public Node(int key, int value) {
        this.key   = key;
        this.value = value;
    }
}