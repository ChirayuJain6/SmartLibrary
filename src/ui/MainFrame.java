package ui;

import services.BookService;
import services.MemberService;
import services.TransactionService;

import javax.swing.*;
import java.awt.*;

/**
 * MainFrame is the main application window.
 * It contains:
 *   - A left sidebar with navigation buttons
 *   - A right content area that switches panels (CardLayout)
 *
 * All services are created here and shared with panels.
 */
public class MainFrame extends JFrame {

    // ─── Services (shared across all panels) ──────────────────────────────────
    private BookService bookService;
    private MemberService memberService;
    private TransactionService transactionService;

    // ─── UI Components ────────────────────────────────────────────────────────
    private JPanel contentArea;       // Right-side panel holder
    private CardLayout cardLayout;    // Switches between different panels

    // Panel name constants
    public static final String PANEL_DASHBOARD    = "DASHBOARD";
    public static final String PANEL_BOOKS        = "BOOKS";
    public static final String PANEL_MEMBERS      = "MEMBERS";
    public static final String PANEL_TRANSACTIONS = "TRANSACTIONS";

    // Colors
    static final Color SIDEBAR_BG    = new Color(15, 23, 42);      // Very dark navy
    static final Color SIDEBAR_HOVER = new Color(30, 41, 59);
    static final Color ACCENT_BLUE   = new Color(59, 130, 246);
    static final Color TEXT_WHITE    = Color.WHITE;
    static final Color TEXT_GRAY     = new Color(148, 163, 184);

    public MainFrame() {
        // Initialize services first
        bookService        = new BookService();
        memberService      = new MemberService();
        transactionService = new TransactionService();

        setupFrame();
        buildUI();
        showPanel(PANEL_DASHBOARD);  // Show dashboard at startup
    }

    private void setupFrame() {
        setTitle("SmartLibrary – Library Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 720);
        setMinimumSize(new Dimension(900, 600));
        setLocationRelativeTo(null); // Center on screen
        setLayout(new BorderLayout());
    }

    private void buildUI() {
        // ─── Left Sidebar ─────────────────────────────────────────────────────
        JPanel sidebar = buildSidebar();
        add(sidebar, BorderLayout.WEST);

        // ─── Right Content Area (uses CardLayout to switch panels) ────────────
        cardLayout  = new CardLayout();
        contentArea = new JPanel(cardLayout);
        contentArea.setBackground(new Color(241, 245, 249)); // light gray-blue

        // Create and register all panels
        contentArea.add(new DashboardPanel(bookService, memberService, transactionService), PANEL_DASHBOARD);
        contentArea.add(new BooksPanel(bookService), PANEL_BOOKS);
        contentArea.add(new MembersPanel(memberService), PANEL_MEMBERS);
        contentArea.add(new TransactionsPanel(bookService, memberService, transactionService, this), PANEL_TRANSACTIONS);

        add(contentArea, BorderLayout.CENTER);
    }

    // ─── Build the Left Sidebar ───────────────────────────────────────────────
    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(220, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // ── App Logo / Title area ──────────────────────────────────────────────
        JPanel logoArea = new JPanel();
        logoArea.setBackground(SIDEBAR_BG);
        logoArea.setLayout(new BoxLayout(logoArea, BoxLayout.Y_AXIS));
        logoArea.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        logoArea.setMaximumSize(new Dimension(220, 110));

        JLabel icon = new JLabel("📚");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appName = new JLabel("SmartLibrary");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 17));
        appName.setForeground(TEXT_WHITE);
        appName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel appSub = new JLabel("Management System");
        appSub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        appSub.setForeground(TEXT_GRAY);
        appSub.setAlignmentX(Component.CENTER_ALIGNMENT);

        logoArea.add(icon);
        logoArea.add(Box.createVerticalStrut(6));
        logoArea.add(appName);
        logoArea.add(Box.createVerticalStrut(2));
        logoArea.add(appSub);
        sidebar.add(logoArea);

        // ── Separator ─────────────────────────────────────────────────────────
        sidebar.add(createSeparator());
        sidebar.add(Box.createVerticalStrut(10));

        // ── Navigation Buttons ────────────────────────────────────────────────
        sidebar.add(createNavButton("🏠  Dashboard",    PANEL_DASHBOARD));
        sidebar.add(createNavButton("📖  Books",        PANEL_BOOKS));
        sidebar.add(createNavButton("👤  Members",      PANEL_MEMBERS));
        sidebar.add(createNavButton("🔄  Issue / Return", PANEL_TRANSACTIONS));

        // Push everything else to bottom
        sidebar.add(Box.createVerticalGlue());

        // ── Bottom label ──────────────────────────────────────────────────────
        JLabel footer = new JLabel("© 2024 SmartLibrary");
        footer.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        footer.setForeground(TEXT_GRAY);
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(footer);
        sidebar.add(Box.createVerticalStrut(15));

        return sidebar;
    }

    // Creates a styled navigation button
    private JButton createNavButton(String text, String panelName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        btn.setForeground(TEXT_GRAY);
        btn.setBackground(SIDEBAR_BG);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMaximumSize(new Dimension(220, 48));
        btn.setPreferredSize(new Dimension(220, 48));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));

        // Hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btn.setBackground(SIDEBAR_HOVER);
                btn.setForeground(TEXT_WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btn.setBackground(SIDEBAR_BG);
                btn.setForeground(TEXT_GRAY);
            }
        });

        // On click: switch the content panel
        btn.addActionListener(e -> showPanel(panelName));
        return btn;
    }

    // Creates a subtle horizontal separator line
    private JPanel createSeparator() {
        JPanel sep = new JPanel();
        sep.setBackground(new Color(30, 41, 59));
        sep.setMaximumSize(new Dimension(220, 1));
        sep.setPreferredSize(new Dimension(220, 1));
        return sep;
    }

    /**
     * Switches the visible panel in the CardLayout content area.
     * Refreshes the panel before showing it (to reflect latest data).
     */
    public void showPanel(String panelName) {
        // Refresh each panel's data before switching to it
        Component[] components = contentArea.getComponents();
        for (Component c : components) {
            if (panelName.equals(PANEL_DASHBOARD) && c instanceof DashboardPanel) {
                ((DashboardPanel) c).refresh();
            } else if (panelName.equals(PANEL_BOOKS) && c instanceof BooksPanel) {
                ((BooksPanel) c).refresh();
            } else if (panelName.equals(PANEL_MEMBERS) && c instanceof MembersPanel) {
                ((MembersPanel) c).refresh();
            } else if (panelName.equals(PANEL_TRANSACTIONS) && c instanceof TransactionsPanel) {
                ((TransactionsPanel) c).refresh();
            }
        }
        cardLayout.show(contentArea, panelName);
    }
}
