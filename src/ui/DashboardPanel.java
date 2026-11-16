package ui;

import services.BookService;
import services.MemberService;
import services.TransactionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * DashboardPanel shows the library overview:
 *  - Total Books, Total Members, Books Issued, Available Books
 *  - Each stat is shown in a modern "stat card"
 *  - A welcome message at the top
 */
public class DashboardPanel extends JPanel {

    private BookService bookService;
    private MemberService memberService;
    private TransactionService transactionService;

    // Labels to update when refresh() is called
    private JLabel totalBooksLabel;
    private JLabel totalMembersLabel;
    private JLabel issuedBooksLabel;
    private JLabel availableBooksLabel;

    public DashboardPanel(BookService bs, MemberService ms, TransactionService ts) {
        this.bookService        = bs;
        this.memberService      = ms;
        this.transactionService = ts;

        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(30, 30, 30, 30));

        buildUI();
        refresh(); // Load initial stats
    }

    private void buildUI() {
        // ─── Top Header ───────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel title = new JLabel("Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(new Color(15, 23, 42));

        JLabel subtitle = new JLabel("Welcome to SmartLibrary – Library Management System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitle.setForeground(new Color(100, 116, 139));

        JPanel titleGroup = new JPanel();
        titleGroup.setLayout(new BoxLayout(titleGroup, BoxLayout.Y_AXIS));
        titleGroup.setOpaque(false);
        titleGroup.add(title);
        titleGroup.add(Box.createVerticalStrut(4));
        titleGroup.add(subtitle);

        header.add(titleGroup, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ─── Stat Cards Grid ──────────────────────────────────────────────────
        JPanel cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setOpaque(false);

        // Create 4 stat cards
        JPanel card1 = createStatCard("📚 Total Books",     "0", new Color(59, 130, 246));   // Blue
        JPanel card2 = createStatCard("👥 Total Members",   "0", new Color(16, 185, 129));   // Green
        JPanel card3 = createStatCard("🔄 Books Issued",    "0", new Color(245, 158, 11));   // Amber
        JPanel card4 = createStatCard("✅ Available Books", "0", new Color(139, 92, 246));   // Purple

        // Store references to the number labels so we can update them in refresh()
        totalBooksLabel    = findValueLabel(card1);
        totalMembersLabel  = findValueLabel(card2);
        issuedBooksLabel   = findValueLabel(card3);
        availableBooksLabel= findValueLabel(card4);

        cardsPanel.add(card1);
        cardsPanel.add(card2);
        cardsPanel.add(card3);
        cardsPanel.add(card4);

        add(cardsPanel, BorderLayout.CENTER);

        // ─── Bottom Info Panel ────────────────────────────────────────────────
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.setBorder(new EmptyBorder(25, 0, 0, 0));

        JPanel infoCard = new JPanel();
        infoCard.setBackground(Color.WHITE);
        infoCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(20, 25, 20, 25)
        ));
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));

        JLabel infoTitle = new JLabel("Quick Guide");
        infoTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        infoTitle.setForeground(new Color(15, 23, 42));

        JLabel tip1 = new JLabel("  📖  Use the Books section to add, search, edit, or remove books.");
        JLabel tip2 = new JLabel("  👤  Use the Members section to manage library members.");
        JLabel tip3 = new JLabel("  🔄  Use Issue/Return to lend books to members and track returns.");

        for (JLabel lbl : new JLabel[]{tip1, tip2, tip3}) {
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            lbl.setForeground(new Color(71, 85, 105));
        }

        infoCard.add(infoTitle);
        infoCard.add(Box.createVerticalStrut(12));
        infoCard.add(tip1);
        infoCard.add(Box.createVerticalStrut(6));
        infoCard.add(tip2);
        infoCard.add(Box.createVerticalStrut(6));
        infoCard.add(tip3);

        infoPanel.add(infoCard, BorderLayout.CENTER);
        add(infoPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a modern stat card with a title and value.
     * The card has a colored top border and white background.
     */
    private JPanel createStatCard(String title, String value, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor), // Left color strip
            new EmptyBorder(20, 20, 20, 20)
        ));

        // Drop shadow simulation via empty border + background
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
                BorderFactory.createMatteBorder(0, 5, 0, 0, accentColor)
            ),
            new EmptyBorder(18, 18, 18, 18)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(100, 116, 139));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 42));
        valueLabel.setForeground(new Color(15, 23, 42));
        valueLabel.putClientProperty("isValueLabel", Boolean.TRUE); // Mark for retrieval

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    // Helper: finds the big value label inside a card panel
    private JLabel findValueLabel(JPanel card) {
        for (Component c : card.getComponents()) {
            if (c instanceof JLabel) {
                JLabel lbl = (JLabel) c;
                if (Boolean.TRUE.equals(lbl.getClientProperty("isValueLabel"))) {
                    return lbl;
                }
            }
        }
        return new JLabel(); // Fallback — should never happen
    }

    /**
     * Refresh all statistics from the services.
     * Called every time the dashboard is shown.
     */
    public void refresh() {
        if (totalBooksLabel     != null) totalBooksLabel.setText(String.valueOf(bookService.getTotalBooks()));
        if (totalMembersLabel   != null) totalMembersLabel.setText(String.valueOf(memberService.getTotalMembers()));
        if (issuedBooksLabel    != null) issuedBooksLabel.setText(String.valueOf(transactionService.getIssuedCount()));
        if (availableBooksLabel != null) availableBooksLabel.setText(String.valueOf(bookService.getAvailableCount()));
        repaint();
    }
}
