# Memory management simulator

A from-scratch implementation of an LRU cache using a **HashMap + custom doubly linked list** combination — no `LinkedHashMap` shortcut.

---

## How It Works

### The Core Idea

An LRU cache keeps the **most recently used** items and evicts the **least recently used** item when the cache is full.

### Why HashMap + Doubly Linked List?

| Structure | Role |
|---|---|
| `HashMap<Integer, Node>` | O(1) key lookup — jumps straight to the node |
| Doubly Linked List | O(1) insert/remove anywhere — maintains recency order |

Using both together:

- **get(key)** → HashMap finds the `Node` in O(1), then the list re-links it to the front in O(1).
- **put(key, value)** → HashMap checks existence in O(1). If eviction is needed, the tail node (LRU) is removed in O(1) via its `prev` pointer.

Two **dummy sentinel nodes** (`head` and `tail`) eliminate null-pointer edge cases at the list boundaries. The real MRU node always sits right after `head`; the real LRU node always sits right before `tail`.

```
head <-> [MRU] <-> ... <-> [LRU] <-> tail
```

---

## Big-O Complexity

| Operation | Time | Notes |
|---|---|---|
| `get(key)` | O(1) | HashMap lookup + list re-link |
| `put(key, value)` | O(1) | HashMap insert/update + list insert + optional eviction |
| `stateAsString()` | O(n) | Traverses the whole list |
| Space | O(n) | n = capacity (map + list nodes) |

---

## Edge Cases Handled

- **Capacity <= 0** — `IllegalArgumentException` is thrown in the constructor.
- **get() on missing key** — returns `-1`.
- **put() on existing key** — updates the value in-place and moves the node to front (no duplicate entries, no spurious eviction).
- **Capacity = 1** — every second `put` evicts the only existing entry.
- **Empty cache** — `stateAsString()` returns `"(empty)"`.
- **Chained evictions** — multiple consecutive puts each evict exactly one LRU entry correctly.

---

## Compile & Run

### Prerequisites
- Java 8 or later installed (`java -version`)

### Compile
```bash
cd LRUCache/src
javac *.java
```

### Run the interactive demo
```bash
java Main
```

Example session:
```
Enter cache capacity: 3
LRU cache created with capacity 3.
Commands: PUT <key> <value> | GET <key> | PRINT | EXIT
-----------------------------------------------------------
> PUT 1 100
  OK
> PUT 2 200
  OK
> PUT 3 300
  OK
> PRINT
  [3:300 -> 2:200 -> 1:100]
> GET 1
  HIT 100
> PRINT
  [1:100 -> 3:300 -> 2:200]
> PUT 4 400
  OK
> PRINT
  [4:400 -> 1:100 -> 3:300]
> GET 2
  MISS
> EXIT
Bye
```

### Run the test suite
```bash
java LRUCacheTest
```

---

## Real-World Uses

| System | How LRU is Used |
|---|---|
| **Redis / Memcached** | In-memory key-value stores use LRU (or LRU approximations) to evict cold data when memory is full |
| **OS Page Replacement** | Operating systems approximate LRU for virtual memory page eviction |
| **Browser Caching** | Browsers keep recently visited page resources in memory/disk caches using LRU-like policies |
| **CDN Edge Caching** | Edge nodes cache hot content and evict cold objects when disk is full |
| **Image-loading libraries** | Android Glide / iOS Kingfisher use LRU memory caches to avoid re-decoding images from disk |

---

## Possible Extensions

| Extension | Description |
|---|---|
| **LFU Cache** | Evict the *least frequently* used key instead of least recently; requires a frequency map and per-frequency bucket lists |
| **Thread Safety** | Wrap operations in `synchronized` or use `ReentrantReadWriteLock`; or use `ConcurrentHashMap` with striped locking for higher throughput |
| **TTL Expiry** | Tag each node with an expiry timestamp; run a background sweeper thread or check staleness lazily on `get` |
| **Generic Types** | Parameterise as `LRUCache<K, V>` so it works beyond `int`-to-`int` mappings |
| **Product-Lookup Layer** | Wrap the cache as a `ProductCache` service that falls through to a database on MISS and back-fills the cache automatically |
| **Metrics / Telemetry** | Track hit rate, miss rate, and eviction count; expose via a `/metrics` endpoint for Prometheus scraping |

---

## Project Structure

```
LRUCache/
├── src/
│   ├── Node.java          # Doubly linked list node (key, value, prev, next)
│   ├── LRUCache.java      # Cache implementation (HashMap + DLL)
│   ├── Main.java          # Interactive CLI demo
│   └── LRUCacheTest.java  # Plain-Java unit tests (10 tests, no framework)
├── README.md
└── .gitignore
```
