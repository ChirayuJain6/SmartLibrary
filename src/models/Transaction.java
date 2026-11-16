package models;

/**
 * Transaction class represents a book issue or return record.
 * status = "ISSUED" when book is out, "RETURNED" after return.
 */
public class Transaction {

    private String id;
    private String bookId;
    private String memberId;
    private String bookTitle;    // Stored for display convenience
    private String memberName;   // Stored for display convenience
    private String issueDate;
    private String returnDate;   // Empty string if not yet returned
    private String status;       // "ISSUED" or "RETURNED"

    // Constructor
    public Transaction(String id, String bookId, String memberId,
                       String bookTitle, String memberName,
                       String issueDate, String returnDate, String status) {
        this.id         = id;
        this.bookId     = bookId;
        this.memberId   = memberId;
        this.bookTitle  = bookTitle;
        this.memberName = memberName;
        this.issueDate  = issueDate;
        this.returnDate = returnDate;
        this.status     = status;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────
    public String getId()          { return id; }
    public String getBookId()      { return bookId; }
    public String getMemberId()    { return memberId; }
    public String getBookTitle()   { return bookTitle; }
    public String getMemberName()  { return memberName; }
    public String getIssueDate()   { return issueDate; }
    public String getReturnDate()  { return returnDate; }
    public String getStatus()      { return status; }

    // ─── Setters ──────────────────────────────────────────────────────────────
    public void setReturnDate(String returnDate) { this.returnDate = returnDate; }
    public void setStatus(String status)         { this.status = status; }

    /**
     * Converts this Transaction to CSV.
     * Uses | as separator for returnDate field (may be empty).
     */
    public String toCSV() {
        // Use pipe | to separate fields that might contain commas
        // Actually we use comma but wrap carefully
        return id + "," + bookId + "," + memberId + "," +
               bookTitle + "," + memberName + "," +
               issueDate + "," + returnDate + "," + status;
    }

    /**
     * Creates a Transaction from a CSV line.
     */
    public static Transaction fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", 8);  // max 8 splits
        if (parts.length < 8) return null;
        return new Transaction(
            parts[0].trim(), parts[1].trim(), parts[2].trim(),
            parts[3].trim(), parts[4].trim(),
            parts[5].trim(), parts[6].trim(), parts[7].trim()
        );
    }

    @Override
    public String toString() {
        return "Transaction{id=" + id + ", book=" + bookTitle + ", member=" + memberName + ", status=" + status + "}";
    }
}
