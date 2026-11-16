package services;

import models.Transaction;
import java.io.*;
import java.util.ArrayList;

/**
 * TransactionService handles Book Issue and Return operations.
 * It maintains a log of all transactions in transactions.csv.
 */
public class TransactionService {

    private static final String FILE_PATH = "data/transactions.csv";
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private int idCounter = 1;

    public TransactionService() {
        loadFromFile();
    }

    // ─── Load from CSV ────────────────────────────────────────────────────────
    private void loadFromFile() {
        transactions.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                Transaction t = Transaction.fromCSV(line);
                if (t != null) {
                    transactions.add(t);
                    int num = extractNumber(t.getId());
                    if (num >= idCounter) idCounter = num + 1;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }
    }

    // ─── Save to CSV ──────────────────────────────────────────────────────────
    public void saveToFile() {
        try {
            new File("data").mkdirs();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (Transaction t : transactions) {
                    writer.write(t.toCSV());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving transactions: " + e.getMessage());
        }
    }

    private String generateId() {
        return String.format("T%03d", idCounter++); // e.g., T001
    }

    private int extractNumber(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (Exception e) {
            return 0;
        }
    }

    // ─── Issue a book ─────────────────────────────────────────────────────────
    /**
     * Creates a new ISSUED transaction record.
     * Returns the transaction ID if successful.
     */
    public String issueBook(String bookId, String memberId, String bookTitle, String memberName, String issueDate) {
        // Check if this member already has this book issued (not yet returned)
        for (Transaction t : transactions) {
            if (t.getBookId().equals(bookId) &&
                t.getMemberId().equals(memberId) &&
                t.getStatus().equals("ISSUED")) {
                return null; // Already issued
            }
        }
        String id = generateId();
        Transaction t = new Transaction(id, bookId, memberId, bookTitle, memberName, issueDate, "", "ISSUED");
        transactions.add(t);
        saveToFile();
        return id;
    }

    // ─── Return a book ────────────────────────────────────────────────────────
    /**
     * Finds an active ISSUED transaction and marks it as RETURNED.
     * Returns true if a matching transaction was found and updated.
     */
    public boolean returnBook(String bookId, String memberId, String returnDate) {
        for (Transaction t : transactions) {
            if (t.getBookId().equals(bookId) &&
                t.getMemberId().equals(memberId) &&
                t.getStatus().equals("ISSUED")) {
                t.setReturnDate(returnDate);
                t.setStatus("RETURNED");
                saveToFile();
                return true;
            }
        }
        return false;
    }

    /** Get all transactions */
    public ArrayList<Transaction> getAllTransactions() {
        return transactions;
    }

    /** Get only currently issued (active) transactions */
    public ArrayList<Transaction> getIssuedTransactions() {
        ArrayList<Transaction> issued = new ArrayList<>();
        for (Transaction t : transactions) {
            if (t.getStatus().equals("ISSUED")) issued.add(t);
        }
        return issued;
    }

    /** Count of currently issued books */
    public int getIssuedCount() {
        return getIssuedTransactions().size();
    }

    /** Count of total transactions */
    public int getTotalTransactions() {
        return transactions.size();
    }

    /**
     * Check if a member has already issued a specific book and not returned it.
     */
    public boolean hasActiveIssue(String bookId, String memberId) {
        for (Transaction t : transactions) {
            if (t.getBookId().equals(bookId) &&
                t.getMemberId().equals(memberId) &&
                t.getStatus().equals("ISSUED")) {
                return true;
            }
        }
        return false;
    }
}
