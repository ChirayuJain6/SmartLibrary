package services;

import models.Member;
import java.io.*;
import java.util.ArrayList;

/**
 * MemberService handles all Member operations.
 * Works exactly like BookService but for Member objects.
 * Uses ArrayList<Member> as in-memory storage.
 */
public class MemberService {

    private static final String FILE_PATH = "data/members.csv";
    private ArrayList<Member> members = new ArrayList<>();
    private int idCounter = 1;

    public MemberService() {
        loadFromFile();
    }

    // ─── Load from CSV ────────────────────────────────────────────────────────
    private void loadFromFile() {
        members.clear();
        File file = new File(FILE_PATH);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith("#")) continue;
                Member m = Member.fromCSV(line);
                if (m != null) {
                    members.add(m);
                    int num = extractNumber(m.getId());
                    if (num >= idCounter) idCounter = num + 1;
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading members: " + e.getMessage());
        }
    }

    // ─── Save to CSV ──────────────────────────────────────────────────────────
    public void saveToFile() {
        try {
            new File("data").mkdirs();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                for (Member m : members) {
                    writer.write(m.toCSV());
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving members: " + e.getMessage());
        }
    }

    private String generateId() {
        return String.format("M%03d", idCounter++); // e.g., M001
    }

    private int extractNumber(String id) {
        try {
            return Integer.parseInt(id.substring(1));
        } catch (Exception e) {
            return 0;
        }
    }

    // ─── CRUD Operations ──────────────────────────────────────────────────────

    /** Add a new member */
    public void addMember(String name, String email, String phone, String joinDate) {
        String id = generateId();
        Member member = new Member(id, name, email, phone, joinDate);
        members.add(member);
        saveToFile();
    }

    /** Get all members */
    public ArrayList<Member> getAllMembers() {
        return members;
    }

    /** Search members by name, email, or phone */
    public ArrayList<Member> searchMembers(String keyword) {
        ArrayList<Member> results = new ArrayList<>();
        String kw = keyword.toLowerCase();
        for (Member m : members) {
            if (m.getName().toLowerCase().contains(kw) ||
                m.getEmail().toLowerCase().contains(kw) ||
                m.getPhone().toLowerCase().contains(kw)) {
                results.add(m);
            }
        }
        return results;
    }

    /** Find a member by ID */
    public Member findById(String id) {
        for (Member m : members) {
            if (m.getId().equals(id)) return m;
        }
        return null;
    }

    /** Update member details */
    public boolean updateMember(String id, String name, String email, String phone) {
        Member m = findById(id);
        if (m == null) return false;
        m.setName(name);
        m.setEmail(email);
        m.setPhone(phone);
        saveToFile();
        return true;
    }

    /** Delete a member */
    public boolean deleteMember(String id) {
        Member m = findById(id);
        if (m == null) return false;
        members.remove(m);
        saveToFile();
        return true;
    }

    /** Total count */
    public int getTotalMembers() { return members.size(); }
}
