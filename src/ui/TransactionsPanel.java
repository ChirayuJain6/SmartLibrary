package ui;

import models.Book;
import models.Member;
import models.Transaction;
import services.BookService;
import services.MemberService;
import services.TransactionService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * TransactionsPanel handles:
 *   1. Issue a book to a member
 *   2. Return a book from a member
 *   3. View all transaction history in a table
 *
 * It uses combo boxes populated from BookService and MemberService.
 */
public class TransactionsPanel extends JPanel {

    private BookService bookService;
    private MemberService memberService;
    private TransactionService transactionService;
    private MainFrame mainFrame; // Used to refresh dashboard after transactions

    // Table for showing transaction history
    private JTable table;
    private DefaultTableModel tableModel;

    // Combo boxes for selecting book and member
    private JComboBox<String> bookCombo;
    private JComboBox<String> memberCombo;

    private static final String[] COLUMNS = {"Trans. ID", "Book", "Member", "Issue Date", "Return Date", "Status"};

    public TransactionsPanel(BookService bs, MemberService ms, TransactionService ts, MainFrame mf) {
        this.bookService        = bs;
        this.memberService      = ms;
        this.transactionService = ts;
        this.mainFrame          = mf;

        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        buildUI();
        refresh();
    }

    private void buildUI() {
        // ─── Title ────────────────────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Issue / Return Books");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(15, 23, 42));
        header.add(title, BorderLayout.WEST);
        add(header, BorderLayout.NORTH);

        // ─── Main Content = Left Action Card + Right Table ────────────────────
        JPanel mainContent = new JPanel(new BorderLayout(20, 0));
        mainContent.setOpaque(false);

        // Left: Issue/Return card
        mainContent.add(buildActionCard(), BorderLayout.WEST);

        // Right: History table
        mainContent.add(buildHistoryCard(), BorderLayout.CENTER);

        add(mainContent, BorderLayout.CENTER);
    }

    // ─── Left: Issue/Return Action Card ──────────────────────────────────────
    private JPanel buildActionCard() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(25, 25, 25, 25)
        ));
        card.setPreferredSize(new Dimension(310, 0));

        // ── Section title ──────────────────────────────────────────────────────
        JLabel sectionTitle = new JLabel("Transaction Form");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sectionTitle.setForeground(new Color(15, 23, 42));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Select a book and member below:");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(100, 116, 139));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(sectionTitle);
        card.add(Box.createVerticalStrut(4));
        card.add(sub);
        card.add(Box.createVerticalStrut(20));

        // ── Separator ──────────────────────────────────────────────────────────
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(sep);
        card.add(Box.createVerticalStrut(20));

        // ── Book selector ──────────────────────────────────────────────────────
        JLabel bookLabel = formLabel("Select Book:");
        bookLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(bookLabel);
        card.add(Box.createVerticalStrut(6));

        bookCombo = new JComboBox<>();
        bookCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        bookCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        bookCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(bookCombo);
        card.add(Box.createVerticalStrut(16));

        // ── Member selector ────────────────────────────────────────────────────
        JLabel memberLabel = formLabel("Select Member:");
        memberLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(memberLabel);
        card.add(Box.createVerticalStrut(6));

        memberCombo = new JComboBox<>();
        memberCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        memberCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        memberCombo.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        card.add(memberCombo);
        card.add(Box.createVerticalStrut(30));

        // ── Issue Button ───────────────────────────────────────────────────────
        JButton issueBtn = createButton("📤  Issue Book", new Color(59, 130, 246));
        issueBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        issueBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        card.add(issueBtn);
        card.add(Box.createVerticalStrut(10));

        // ── Return Button ──────────────────────────────────────────────────────
        JButton returnBtn = createButton("📥  Return Book", new Color(16, 185, 129));
        returnBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        returnBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        card.add(returnBtn);

        card.add(Box.createVerticalGlue());

        // ─── Button Actions ───────────────────────────────────────────────────
        issueBtn.addActionListener(e  -> handleIssueBook());
        returnBtn.addActionListener(e -> handleReturnBook());

        return card;
    }

    // ─── Right: History Table ─────────────────────────────────────────────────
    private JPanel buildHistoryCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(226, 232, 240), 1),
            new EmptyBorder(0, 0, 0, 0)
        ));

        // Title bar of the card
        JPanel cardHeader = new JPanel(new BorderLayout());
        cardHeader.setBackground(new Color(248, 250, 252));
        cardHeader.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(226, 232, 240)),
            new EmptyBorder(12, 18, 12, 18)
        ));
        JLabel histTitle = new JLabel("Transaction History");
        histTitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        histTitle.setForeground(new Color(15, 23, 42));

        JButton refreshBtn = createButton("↻ Refresh", new Color(100, 116, 139));
        refreshBtn.addActionListener(e -> refresh());

        cardHeader.add(histTitle,   BorderLayout.WEST);
        cardHeader.add(refreshBtn,  BorderLayout.EAST);

        // Table
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(38);
        table.setGridColor(new Color(241, 245, 249));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(new Color(248, 250, 252));
        th.setForeground(new Color(71, 85, 105));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(226, 232, 240)));
        th.setPreferredSize(new Dimension(0, 42));

        // Custom cell renderer to color ISSUED/RETURNED status column
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                lbl.setHorizontalAlignment(SwingConstants.CENTER);
                String status = value == null ? "" : value.toString();
                if (status.equals("ISSUED"))   { lbl.setForeground(new Color(245, 158, 11)); lbl.setFont(lbl.getFont().deriveFont(Font.BOLD)); }
                else if (status.equals("RETURNED")) { lbl.setForeground(new Color(16, 185, 129)); lbl.setFont(lbl.getFont().deriveFont(Font.BOLD)); }
                else { lbl.setForeground(Color.BLACK); }
                return lbl;
            }
        });

        // Center-align other columns
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < 5; i++) table.getColumnModel().getColumn(i).setCellRenderer(center);

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(80);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(90);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(cardHeader, BorderLayout.NORTH);
        card.add(scroll,     BorderLayout.CENTER);
        return card;
    }

    // ─── Handle Issue Book ────────────────────────────────────────────────────
    private void handleIssueBook() {
        int bookIdx   = bookCombo.getSelectedIndex();
        int memberIdx = memberCombo.getSelectedIndex();

        if (bookIdx < 0 || memberIdx < 0) {
            JOptionPane.showMessageDialog(this, "Please select both a book and a member.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<Book>   books   = bookService.getAllBooks();
        ArrayList<Member> members = memberService.getAllMembers();

        if (bookIdx >= books.size() || memberIdx >= members.size()) {
            JOptionPane.showMessageDialog(this, "Invalid selection. Please refresh and try again.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book   book   = books.get(bookIdx);
        Member member = members.get(memberIdx);

        if (book.getAvailableQuantity() <= 0) {
            JOptionPane.showMessageDialog(this, "\"" + book.getTitle() + "\" is not available right now.", "Not Available", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (transactionService.hasActiveIssue(book.getId(), member.getId())) {
            JOptionPane.showMessageDialog(this,
                member.getName() + " has already issued \"" + book.getTitle() + "\" and hasn't returned it yet.",
                "Already Issued", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String txId  = transactionService.issueBook(book.getId(), member.getId(),
                           book.getTitle(), member.getName(), today);
        if (txId != null) {
            bookService.issueBook(book.getId());
            refresh();
            // Also refresh the dashboard statistics
            mainFrame.showPanel(MainFrame.PANEL_TRANSACTIONS);
            JOptionPane.showMessageDialog(this,
                "Book \"" + book.getTitle() + "\" issued to " + member.getName() + " successfully!\nTransaction ID: " + txId,
                "Issued!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ─── Handle Return Book ───────────────────────────────────────────────────
    private void handleReturnBook() {
        int bookIdx   = bookCombo.getSelectedIndex();
        int memberIdx = memberCombo.getSelectedIndex();

        if (bookIdx < 0 || memberIdx < 0) {
            JOptionPane.showMessageDialog(this, "Please select both a book and a member.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        ArrayList<Book>   books   = bookService.getAllBooks();
        ArrayList<Member> members = memberService.getAllMembers();

        Book   book   = books.get(bookIdx);
        Member member = members.get(memberIdx);

        if (!transactionService.hasActiveIssue(book.getId(), member.getId())) {
            JOptionPane.showMessageDialog(this,
                member.getName() + " has not issued \"" + book.getTitle() + "\" or it's already returned.",
                "No Active Issue", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String today   = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        boolean success = transactionService.returnBook(book.getId(), member.getId(), today);
        if (success) {
            bookService.returnBook(book.getId());
            refresh();
            JOptionPane.showMessageDialog(this,
                "Book \"" + book.getTitle() + "\" returned successfully by " + member.getName() + "!",
                "Returned!", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ─── Refresh ──────────────────────────────────────────────────────────────
    public void refresh() {
        // Refresh combo boxes
        bookCombo.removeAllItems();
        memberCombo.removeAllItems();

        for (Book b   : bookService.getAllBooks())     bookCombo.addItem(b.getId() + " – " + b.getTitle() + " (Avail: " + b.getAvailableQuantity() + ")");
        for (Member m : memberService.getAllMembers()) memberCombo.addItem(m.getId() + " – " + m.getName());

        // Refresh history table
        tableModel.setRowCount(0);
        ArrayList<Transaction> txList = transactionService.getAllTransactions();
        // Show latest transactions first (reverse order)
        for (int i = txList.size() - 1; i >= 0; i--) {
            Transaction t = txList.get(i);
            tableModel.addRow(new Object[]{
                t.getId(), t.getBookTitle(), t.getMemberName(),
                t.getIssueDate(), t.getReturnDate().isEmpty() ? "—" : t.getReturnDate(),
                t.getStatus()
            });
        }
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────
    private JLabel formLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(71, 85, 105));
        return lbl;
    }

    private JButton createButton(String text, Color bg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setOpaque(true);
        return btn;
    }
}
