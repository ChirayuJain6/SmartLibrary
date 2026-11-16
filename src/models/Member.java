package models;

/**
 * Member class represents a library member.
 * This is a Model class demonstrating OOP Encapsulation.
 */
public class Member {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String joinDate;   // Stored as "dd-MM-yyyy" string

    // Constructor
    public Member(String id, String name, String email, String phone, String joinDate) {
        this.id       = id;
        this.name     = name;
        this.email    = email;
        this.phone    = phone;
        this.joinDate = joinDate;
    }

    // ─── Getters ──────────────────────────────────────────────────────────────
    public String getId()       { return id; }
    public String getName()     { return name; }
    public String getEmail()    { return email; }
    public String getPhone()    { return phone; }
    public String getJoinDate() { return joinDate; }

    // ─── Setters ──────────────────────────────────────────────────────────────
    public void setName(String name)       { this.name = name; }
    public void setEmail(String email)     { this.email = email; }
    public void setPhone(String phone)     { this.phone = phone; }
    public void setJoinDate(String date)   { this.joinDate = date; }

    /**
     * Converts this Member to a CSV line for file storage.
     */
    public String toCSV() {
        return id + "," + name + "," + email + "," + phone + "," + joinDate;
    }

    /**
     * Creates a Member object from a CSV line read from file.
     */
    public static Member fromCSV(String csvLine) {
        String[] parts = csvLine.split(",", -1);
        if (parts.length < 5) return null;
        String id       = parts[0].trim();
        String name     = parts[1].trim();
        String email    = parts[2].trim();
        String phone    = parts[3].trim();
        String joinDate = parts[4].trim();
        return new Member(id, name, email, phone, joinDate);
    }

    @Override
    public String toString() {
        return "Member{id=" + id + ", name=" + name + "}";
    }
}
