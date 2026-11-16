package services;

import models.Book;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * BookService handles all Book operations:
 *  - Loading books from books.csv on startup
 *  - Saving books to books.csv after every change
 *  - CRUD: Create, Read, Update, Delete
 *
 * Uses ArrayList<Book> as the main in-memory data store.
 */
public class BookService {

    // The CSV file where all books are saved
    private static final String FILE_PATH = "data/books.csv";

    // ArrayList to hold all books in memory (OOP + Collections)
    private ArrayList<Book> books = new ArrayList<>();

    // Counter to generate unique IDs like B001, B002, ...
    private int idCounter = 1;

    // Constructor: loads existing books from file when service is created
    public BookService() {
        loadFromFile();
    }

    // ─── Load from CSV ────────────────────────────────────────────────────────
    /**
     * Reads books.csv line by line.
     * Each line is converted to a Book object using Book.fromCSV().
     * All Book objects are added to the ArrayList.
     */
    private void loadFromFile() {
        books.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return; // No file yet — first run

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue; // Skip empty and comment lines
                Book b = Book.fromCSV(line);
                if (b != null) {
                    books.add(b);
                    // Track the highest ID number so new IDs don't clash
                    int num = extractNumber(b.getId());
                    if (num >= idCounter) idCounter = num + 1;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading books: " + e.getMessage());
        }
    }

    // ─── Save to CSV ──────────────────────────────────────────────────────────
    /**
     * Writes all books in ArrayList to books.csv.
     * Overwrites the file completely each time.
     * Called after every add/update/delete.
     */
    public void saveToFile() {
        try {
            // Make sure the data/ directory exists
            new File("data").mkdirs();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (Book b : books) {          // Loop through ArrayList
                    writer.write(b.toCSV());    // Write CSV line
                    writer.newLine();           // New line after each book
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

    // ─── Generate next unique ID ──────────────────────────────────────────────
    private String generateId() {
        return String.format("B%03d", idCounter++); // e.g., B001
    }

    private int extractNumber(String id) {
        try {
            return Integer.parseInt(id.substring(1)); // Remove "B" prefix
        } catch (Exception e) {
            return 0;
        }
    }

    // ─── CRUD Operations ──────────────────────────────────────────────────────

    /** Add a new book */
    public void addBook(String title, String author, String genre, int year, int quantity) {
        String id = generateId();
        Book book = new Book(id, title, author, genre, year, quantity, quantity);
        books.add(book);   // Add to ArrayList
        saveToFile();       // Persist to CSV
    }

    /** Get all books (returns the full ArrayList) */
    public ArrayList<Book> getAllBooks() {
        return books;
    }

    /** Search books by title or author (case-insensitive) */
    public ArrayList<Book> searchBooks(String keyword) {
        ArrayList<Book> results = new ArrayList<>();
        String kw = keyword.toLowerCase();
        for (Book b : books) {                              // Loop through ArrayList
            if (b.getTitle().toLowerCase().contains(kw) ||
                b.getAuthor().toLowerCase().contains(kw) ||
                b.getGenre().toLowerCase().contains(kw)) {
                results.add(b);
            }
        }
        return results;
    }

    /** Find a book by its ID */
    public Book findById(String id) {
        for (Book b : books) {
            if (b.getId().equals(id)) return b;
        }
        return null;
    }

    /** Update an existing book's details */
    public boolean updateBook(String id, String title, String author, String genre, int year, int totalQty) {
        Book b = findById(id);
        if (b == null) return false;
        // Adjust available quantity proportionally
        int diff = totalQty - b.getTotalQuantity();
        int newAvailable = b.getAvailableQuantity() + diff;
        if (newAvailable < 0) newAvailable = 0;

        b.setTitle(title);
        b.setAuthor(author);
        b.setGenre(genre);
        b.setYear(year);
        b.setTotalQuantity(totalQty);
        b.setAvailableQuantity(newAvailable);
        saveToFile();
        return true;
    }

    /** Delete a book by ID */
    public boolean deleteBook(String id) {
        Book b = findById(id);
        if (b == null) return false;
        books.remove(b);   // Remove from ArrayList
        saveToFile();
        return true;
    }

    /** Decrease available count when a book is issued */
    public boolean issueBook(String id) {
        Book b = findById(id);
        if (b == null || b.getAvailableQuantity() <= 0) return false;
        b.setAvailableQuantity(b.getAvailableQuantity() - 1);
        saveToFile();
        return true;
    }

    /** Increase available count when a book is returned */
    public boolean returnBook(String id) {
        Book b = findById(id);
        if (b == null) return false;
        if (b.getAvailableQuantity() < b.getTotalQuantity()) {
            b.setAvailableQuantity(b.getAvailableQuantity() + 1);
        }
        saveToFile();
        return true;
    }

    /** Count total books */
    public int getTotalBooks() { return books.size(); }

    /** Count books currently available */
    public int getAvailableCount() {
        int count = 0;
        for (Book b : books) {
            if (b.getAvailableQuantity() > 0) count++;
        }
        return count;
    }
}
