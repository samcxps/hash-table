import java.util.ArrayList;

/**
 * Samson Cain's implementation of a Hash Table
 *  * 
 * @author Samson Cain
 * @email srcain@wisc.edu
 * @class CS400 - Programming 3
 * @lecture 001
 * 
 * @project p3a Hash Table
 * 
 * @date October 21, 2019
 * 
 * 
 */

/**
 * The hashing function I used is a Java implementation (or best possible implementation) of the
 * hashing algorithm djb2. I wanted to learn more about hashing functions so I researched some
 * popular ones and chose not to use the default Java hashCode() method. I settled on djb2 because
 * it seemed to be one of the more popular ones for Strings as well as incorporated some techniques
 * we learned in class such as bit shifting.
 * 
 * I tested both djb2 and the default hashCode() function and it seemd that djb2 created a more
 * uniform distribution and caused less collisions.
 * 
 */

/**
 * This hash table utilizes collision resolution schema #4: An ArrayList of ArrayLists and uses
 * buckets to store nodes. Each bucket is an ArrayList of HashTableNodes.
 * 
 * The HashTableNode is an inner class that takes a String key and a Book book as parameters when
 * creating a new node.
 * 
 * When resizing is necessary, it resizes the new table to the current capacity * 2 + 1.
 * 
 * @param <K> unique comparable identifier for each <K,V> pair, may not be null
 * @param <V> associated value with a key, value may be null
 */
public class BookHashTable implements HashTableADT<String, Book> {

  /** The initial capacity that is used if none is specified user */
  static final int DEFAULT_CAPACITY = 101;

  /** The load factor that is used if none is specified by user */
  static final double DEFAULT_LOAD_FACTOR_THRESHOLD = 0.75;

  /** The ArrayList of ArrayLists used for the hash table */
  private ArrayList<ArrayList<HashTableNode>> hashTable;

  /** The load factor threshold of the table */
  private double loadFactorThreshold;

  /** The current capacity of the hash table */
  private int capacity;

  /** The current size of the hash table */
  private int size;

  /**
   * default no-argument constructor.
   * 
   * Uses default capacity and sets load factor threshold for the newly created hash table.
   */
  public BookHashTable() {
    this(DEFAULT_CAPACITY, DEFAULT_LOAD_FACTOR_THRESHOLD);
  }

  /**
   * Creates an empty hash table with the specified capacity and load factor.
   * 
   * @param initialCapacity number of elements table should hold at start.
   * @param loadFactorThreshold the ratio of items/capacity that causes table to resize and rehash
   */
  public BookHashTable(int initialCapacity, double loadFactorThreshold) {
    this.hashTable = new ArrayList<ArrayList<HashTableNode>>(initialCapacity);
    this.loadFactorThreshold = loadFactorThreshold;
    this.capacity = initialCapacity;
    this.size = 0;

    // set all elements in hash table to null
    for (int i = 0; i < this.capacity; i++) {
      hashTable.add(new ArrayList<HashTableNode>());
    }

  }

  /**
   * Inserts a new HashTableNode into the hash table
   * 
   * @param String key - key of node to be inserted
   * @param Book value - value of node to be inserted
   * 
   * @throws IllegalNullKeyException when the key provided is null
   * @throws DuplicateKeyException when the key already exists in the hash table
   */
  @Override
  public void insert(String key, Book value) throws IllegalNullKeyException, DuplicateKeyException {
    // check for null key
    if (key == null) {
      throw new IllegalNullKeyException();
    }

    // check current load factor and resize if necessary
    // System.out.println("LOAD FACTOR: " + (float) this.size / this.capacity);
    if ((float) this.size / this.capacity >= this.loadFactorThreshold) {
      this.resize();
    }

    // create new hash table node
    HashTableNode newNode = new HashTableNode(key, value);

    // get hash index for new node
    int hashIndex = hash(key);

    // get bucket of hash table nodes at hash index
    ArrayList<HashTableNode> bucket = hashTable.get(hashIndex);

    // check for duplicate key
    for (HashTableNode node : bucket) {
      if (node.getKey().equals(key)) {
        throw new DuplicateKeyException();
      }
    }

    bucket.add(newNode);
    this.size++;
  }

  /**
   * Removes a HashTableNode from the hash table
   * 
   * @param String key - key of node to be removed
   * 
   * @return boolean - true if the keyw as removed, false if the key was not removed (not found)
   * 
   * @throws IllegalNullKeyException when the key provided is null
   */
  @Override
  public boolean remove(String key) throws IllegalNullKeyException {
    // check for null key
    if (key == null) {
      throw new IllegalNullKeyException();
    }

    // get hash index for new node
    int hashIndex = hash(key);

    // get bucket of hash table nodes at hash index
    ArrayList<HashTableNode> bucket = hashTable.get(hashIndex);

    // find node if it exists. If it exists set to null
    // and decrease hash table size by 1.
    for (int i = 0; i < bucket.size(); i++) {
      if (bucket.get(i).getKey().equals(key)) {
        bucket.remove(i);
        this.size--;
        return true;
      }
    }

    return false;
  }

  /**
   * Gets a HashTableNode with the provided key and returns it
   * 
   * @param String key - key of node to find
   * 
   * @return Book - Book object found in hash table
   * 
   * @throws IllegalNullKeyException when the key provided is null
   * @throws KeyNotFoundException when the key was not found in the hash table
   * 
   */
  @Override
  public Book get(String key) throws IllegalNullKeyException, KeyNotFoundException {
    // check for null key
    if (key == null) {
      throw new IllegalNullKeyException();
    }

    // get hash index for node
    int hashIndex = hash(key);

    // get bucket of hash table nodes at the hash index
    ArrayList<HashTableNode> bucket = hashTable.get(hashIndex);

    // find the node if it exists in the bucket and return it
    for (HashTableNode node : bucket) {
      if (node.getKey().equals(key)) {
        return node.getData();
      }
    }

    // instead of returning null if not found we throw KeyNotFoundException()
    throw new KeyNotFoundException();
  }

  /**
   * Resizes the hash table.
   * 
   * This method is called in the insert() function when the load factor of the hash table exceeds
   * its load factor threshold.
   * 
   * Capacity increases from current capacity to capacity * 2 + 1
   */
  private void resize() {
    // change capacity FIRST so our hashing function works properly when rehashing
    // old elements
    this.capacity = (this.capacity * 2) + 1;

    // create new temporary hash table
    ArrayList<ArrayList<HashTableNode>> tempList =
        new ArrayList<ArrayList<HashTableNode>>(this.capacity);

    // set all elements in temporary hash table to null
    for (int i = 0; i < this.capacity; i++) {
      tempList.add(new ArrayList<HashTableNode>());
    }

    // checks every index in current hash table
    for (int i = 0; i < hashTable.size(); i++) {
      // get bucket at index
      ArrayList<HashTableNode> bucket = hashTable.get(i);

      // if the bucket is not empty go through every item in bucket
      if (bucket.size() > 0) {
        for (HashTableNode node : bucket) {

          // get new hash index for item
          int hashIndex = hash(node.getKey());

          // add new node to correct spot in temporary hash table
          tempList.get(hashIndex).add(new HashTableNode(node.getKey(), node.getData()));
        }
      }
    }

    // make the temp list the new hash table
    this.hashTable = tempList;
  }

  /**
   * Returns a hash index of the key provided for use in the hash table.
   * 
   * Uses the djb2 hashing algorithm. After hash is calculated, Math.abs() is called
   * on it to ensure it is a positive number, and is then modded by the hash tables
   * capacity so the index works for the current hash table. 
   * 
   * @param String key - to hash
   * 
   * @return int - hash index for given key
   * 
   * @return the hash index of the key provided
   */
  private int hash(String key) {
    int hash = 5381;
    for (int i = 0; i < key.length(); i++) {
      hash = key.charAt(i) + ((hash << 5) - hash);
    }

    return Math.abs(hash % this.capacity);
  }

  /**
   * Prints all the buckets in the hash table.
   * 
   * I used this to visually check distribution of elements after resizing.
   * 
   * Not needed for the actual assignment.
   */
  public void printTable() {
    for (ArrayList<HashTableNode> bucket : this.hashTable) {
      System.out.println(bucket.toString());
    }
  }

  /////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////

  /**
   * Returns the current amount of elements in the hash table
   */
  @Override
  public int numKeys() {
    return this.size;
  }

  /**
   * Returns the current load factor threshold of the hash table
   */
  @Override
  public double getLoadFactorThreshold() {
    return this.loadFactorThreshold;
  }

  /**
   * Returns the current capacity of the hash table
   */
  @Override
  public int getCapacity() {
    return this.capacity;
  }

  /**
   * Returns the collision resolution schema of the hash table
   */
  @Override
  public int getCollisionResolutionScheme() {
    return 4;
  }

  /////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////

  /**
   * Hash table node inner class used for storing key/value pairs in hash table.
   * 
   * Does not use generic objects and is limited to a String for the key and a Book object for the
   * value/data. Could be changed to accept any type but it is not necessary for this assignment.
   * 
   * @author samsoncain
   */
  private class HashTableNode {
    private String key; // key
    private Book book; // data

    public HashTableNode(String key, Book book) {
      this.key = key;
      this.book = book;
    }

    public String getKey() {
      return this.key;
    }

    public Book getData() {
      return this.book;
    }

  }

}
