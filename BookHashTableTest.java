/**
 * Filename: TestHashTableDeb.java Project: p3 Authors: Debra Deppeler (deppeler@cs.wisc.edu)
 * 
 * Semester: Fall 2018 Course: CS400
 * 
 * Due Date: before 10pm on 10/29 Version: 1.0
 * 
 * Credits: None so far
 * 
 * Bugs: TODO: add any known bugs, or unsolved problems here
 */

import org.junit.After;
import java.io.FileNotFoundException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Random;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test HashTable class implementation to ensure that required functionality works for all cases.
 */
public class BookHashTableTest {

  // Default name of books data file
  public static final String BOOKS = "books.csv";

  // Empty hash tables that can be used by tests
  static BookHashTable bookObject;
  static ArrayList<Book> bookTable;

  static final int INIT_CAPACITY = 2;
  static final double LOAD_FACTOR_THRESHOLD = 0.49;

  static Random RNG = new Random(0); // seeded to make results repeatable (deterministic)

  /** Create a large array of keys and matching values for use in any test */
  @BeforeAll
  public static void beforeClass() throws Exception {
    bookTable = BookParser.parse(BOOKS);
  }

  /** Initialize empty hash table to be used in each test */
  @BeforeEach
  public void setUp() throws Exception {
    // TODO: change HashTable for final solution
    bookObject = new BookHashTable(INIT_CAPACITY, LOAD_FACTOR_THRESHOLD);
  }

  /** Not much to do, just make sure that variables are reset */
  @AfterEach
  public void tearDown() throws Exception {
    bookObject = null;
  }

  private void insertMany(ArrayList<Book> bookTable)
      throws IllegalNullKeyException, DuplicateKeyException {
    for (int i = 0; i < bookTable.size(); i++) {
      bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
    }
  }

  /**
   * IMPLEMENTED AS EXAMPLE FOR YOU Tests that a HashTable is empty upon initialization
   */
  @Test
  public void test000_collision_scheme() {
    if (bookObject == null)
      fail("Gg");
    int scheme = bookObject.getCollisionResolutionScheme();
    if (scheme < 1 || scheme > 9)
      fail("collision resolution must be indicated with 1-9");
  }


  /**
   * IMPLEMENTED AS EXAMPLE FOR YOU Tests that a HashTable is empty upon initialization
   */
  @Test
  public void test000_IsEmpty() {
    // "size with 0 entries:"
    assertEquals(0, bookObject.numKeys());
  }

  /**
   * IMPLEMENTED AS EXAMPLE FOR YOU Tests that a HashTable is not empty after adding one (key,book)
   * pair
   * 
   * @throws DuplicateKeyException
   * @throws IllegalNullKeyException
   */
  @Test
  public void test001_IsNotEmpty() throws IllegalNullKeyException, DuplicateKeyException {
    bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));
    String expected = "" + 1;
    // "size with one entry:"
    assertEquals(expected, "" + bookObject.numKeys());
  }

  /**
   * IMPLEMENTED AS EXAMPLE FOR YOU Test if the hash table will be resized after adding two
   * (key,book) pairs given the load factor is 0.49 and initial capacity to be 2.
   */
  @Test
  public void test002_Resize() throws IllegalNullKeyException, DuplicateKeyException {
    bookObject.insert(bookTable.get(0).getKey(), bookTable.get(0));
    int cap1 = bookObject.getCapacity();
    bookObject.insert(bookTable.get(1).getKey(), bookTable.get(1));
    int cap2 = bookObject.getCapacity();

    // "size with one entry:"
    assertTrue(cap2 > cap1 & cap1 == 2);
  }

  /**
   * Inserts 10 books and checks size, then removes books and makes sure size is 0
   * 
   * @throws IllegalNullKeyException
   * @throws DuplicateKeyException
   */
  @Test
  public void test003_InsertAndRemove() throws IllegalNullKeyException, DuplicateKeyException {
    for (int i = 0; i < 10; i++) {
      bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
    }
    assertTrue(bookObject.numKeys() == 10);

    for (int i = 0; i < 10; i++) {
      bookObject.remove(bookTable.get(i).getKey());
    }
    assertTrue(bookObject.numKeys() == 0);
  }

  /**
   * Inserts all 8124 books and ensures the hash table size is correct
   * 
   * @throws IllegalNullKeyException
   * @throws DuplicateKeyException
   */
  @Test
  public void test004_InsertAll() throws IllegalNullKeyException, DuplicateKeyException {
    insertMany(bookTable);
    assertTrue(bookObject.numKeys() == 8124);
    bookObject.printTable();
  }

  /**
   * Inserts all 8124 books and then removes all to ensure size is 0
   * 
   * @throws IllegalNullKeyException
   * @throws DuplicateKeyException
   */
  @Test
  public void test005_InsertAllRemoveAll() throws IllegalNullKeyException, DuplicateKeyException {
    insertMany(bookTable);
    assertTrue(bookObject.numKeys() == 8124);

    for (int i = 0; i < 8124; i++) {
      bookObject.remove(bookTable.get(i).getKey());
    }
    assertTrue(bookObject.numKeys() == 0);
  }

  /**
   * Inserts 10 books and checks the get() function on all of them
   * 
   * @throws IllegalNullKeyException
   * @throws DuplicateKeyException
   * @throws KeyNotFoundException
   */
  @Test
  public void test006_InsertAndCheckGet()
      throws IllegalNullKeyException, DuplicateKeyException, KeyNotFoundException {
    // insert first 10 books
    for (int i = 0; i < 10; i++) {
      bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
    }

    // get first 10 books
    for (int i = 0; i < 10; i++) {
      bookObject.get(bookTable.get(i).getKey());
    }

    // get book that does not exist to make sure KeyNotFoundException is thrown
    try {
      bookObject.get(bookTable.get(11).getKey());
    } catch (KeyNotFoundException e) {
    } catch (Exception e) {
      fail("unknown exception " + e);
    }
  }

  /**
   * Attempts to insert same 10 books twice and ensure DuplicateKeyException is thrown
   * 
   * @throws IllegalNullKeyException
   * @throws DuplicateKeyException
   * @throws KeyNotFoundException
   */
  @Test
  public void test007_InsertDuplicateKey()
      throws IllegalNullKeyException, DuplicateKeyException, KeyNotFoundException {
    // insert first 10 books
    for (int i = 0; i < 10; i++) {
      bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
    }

    try {
      // try to insert first 10 books again
      for (int i = 0; i < 10; i++) {
        bookObject.insert(bookTable.get(i).getKey(), bookTable.get(i));
      }
    } catch (DuplicateKeyException e) {

    } catch (Exception e) {
      fail("unknown exception " + e);
    }
  }
  
  /**
   * Attempts to insert null key to make sure IllegalNullKeyException is working
   * 
   * @throws IllegalNullKeyException
   * @throws DuplicateKeyException
   * @throws KeyNotFoundException
   */
  @Test
  public void test008_InsertNullKey()
      throws IllegalNullKeyException, DuplicateKeyException, KeyNotFoundException {

    try {
      // try to insert 10 null books
      for (int i = 0; i < 10; i++) {
        bookObject.insert(null, bookTable.get(i));
      }
    } catch (IllegalNullKeyException e) {

    } catch (Exception e) {
      fail("unknown exception " + e);
    }
  }
  
  /**
   * Attempts to insert valid keys with null values to make sure it works properly
   * 
   * @throws IllegalNullKeyException
   * @throws DuplicateKeyException
   * @throws KeyNotFoundException
   */
  @Test
  public void test009_InsertNullValues()
      throws IllegalNullKeyException, DuplicateKeyException, KeyNotFoundException {

    try {
      // try to insert 10 valid keys with null values
      for (int i = 0; i < 10; i++) {
        bookObject.insert(bookTable.get(i).getKey(), null);
      }
    } catch (Exception e) {
      fail("unknown exception " + e);
    }
  }



}
