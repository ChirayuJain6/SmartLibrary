package models;

/**
 * Book class represents a book in the library.
 * This is a Model class (part of OOP - Encapsulation).
 * All data is stored privately and accessed via getters/setters.
 */
public class Book {

    // Private fields - Encapsulation (OOP concept)
    private String id;
    private String title;
    private String author;
    private String genre;
    private int year;
    private int totalQuantity;
    private int availableQuantity;

    // Constructor - used to create a new Book object
    public Book(String id, String title, String author, String genre, int year, int totalQuantity, int availableQuantity) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.year = year;
        this.totalQuantity = totalQuantity;
        this.availableQuantity = availableQuantity;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────
    public String getId()               { return id; }
    public String getTitle()            { return title; }
    public String getAuthor()           { return author; }
    public String getGenre()            { return genre; }
    public int    getYear()             { return year; }
    public int    getTotalQuantity()    { return totalQuantity; }
    public int    getAvailableQuantity(){ return availableQuantity; }

    // ─── Setters ──────────────────────────────────────────────────────────────
    public void setTitle(String title)                      { this.title = title; }
    public void setAuthor(String author)                    { this.author = author; }
    public void setGenre(String genre)                      { this.genre = genre; }
    public void setYear(int year)                           { this.year = year; }
    public void setTotalQuantity(int totalQuantity)         { this.totalQuantity = totalQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    /**
     * Converts this Book object to a CSV line (comma-separated).
     * Used for saving data to books.csv file.
     */
    public String toCSV() {
        return id + "," + title + "," + author + "," + genre + "," + year + "," + totalQuantity + "," + availableQuantity;
    }

    /**
     * Creates a Book object from a CSV line.
     * Used for loading data from books.csv file.
     * This is a static factory method.
     */
    public static Book fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 7) return null;
        try {
            String id     = parts[0].trim();
            String title  = parts[1].trim();
            String author = parts[2].trim();
            String genre  = parts[3].trim();
            int year      = Integer.parseInt(parts[4].trim());
            int total     = Integer.parseInt(parts[5].trim());
            int available = Integer.parseInt(parts[6].trim());
            return new Book(id, title, author, genre, year, total, available);
        } catch (NumberFormatException e) {
            return null; // Skip bad lines
        }
    }

    // toString used for debugging
    @Override
    public String toString() {
        return "Book{id=" + id + ", title=" + title + ", author=" + author + "}";
    }
}
