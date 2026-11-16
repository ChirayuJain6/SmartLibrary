package ui;

import models.Book;
import services.BookService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.ArrayList;

/**
 * BooksPanel provides the full Books management interface:
 *  - A searchable table of all books
 *  - Buttons to Add, Edit, Delete books
 *  - A search bar that filters the table in real time
 */
public class BooksPanel extends JPanel {

    private BookService bookService;

    // ─── UI Components ────────────────────────────────────────────────────────
    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    // Column headers for the table
    private static final String[] COLUMNS = {"ID", "Title", "Author", "Genre", "Year", "Total", "Available"};

    public BooksPanel(BookService bs) {
        this.bookService = bs;
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        buildUI();
        refresh();
    }

    private void buildUI() {
        // ─── Header Row ───────────────────────────────────────────────────────
        JPanel header = buildHeader();
        add(header, BorderLayout.NORTH);

        // ─── Table ────────────────────────────────────────────────────────────
        JPanel tableCard = buildTableCard();
        add(tableCard, BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        // Title
        JLabel title = new JLabel("Books Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(15, 23, 42));

        // Right-side: Search + Buttons
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        // Search field
        searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.setToolTipText("Search by title, author, or genre");
        searchField.putClientProperty("JTextField.placeholderText", "🔍 Search books...");
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterTable(searchField.getText().trim());
            }
        });

        JButton addBtn    = createButton("+ Add Book",  new Color(59, 130, 246));
        JButton editBtn   = createButton("✏ Edit",      new Color(16, 185, 129));
        JButton deleteBtn = createButton("🗑 Delete",   new Color(239, 68, 68));
        JButton refreshBtn= createButton("↻ Refresh",   new Color(100, 116, 139));

        controls.add(searchField);
        controls.add(addBtn);
        controls.add(editBtn);
        controls.add(deleteBtn);
        controls.add(refreshBtn);

        header.add(title,    BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);

        // ─── Button Actions ───────────────────────────────────────────────────
        addBtn.addActionListener(e -> showAddBookDialog());
        editBtn.addActionListener(e -> showEditBookDialog());
        deleteBtn.addActionListener(e -> deleteSelectedBook());
        refreshBtn.addActionListener(e -> refresh());

        return header;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        // Create table model (non-editable directly)
        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int row, int col) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(38);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Style the header
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(248, 250, 252));
        tableHeader.setForeground(new Color(71, 85, 105));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(226, 232, 240)));
        tableHeader.setPreferredSize(new Dimension(0, 42));

        // Center-align all cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);   // ID
        table.getColumnModel().getColumn(1).setPreferredWidth(220);  // Title
        table.getColumnModel().getColumn(2).setPreferredWidth(160);  // Author
        table.getColumnModel().getColumn(3).setPreferredWidth(100);  // Genre
        table.getColumnModel().getColumn(4).setPreferredWidth(60);   // Year
        table.getColumnModel().getColumn(5).setPreferredWidth(60);   // Total
        table.getColumnModel().getColumn(6).setPreferredWidth(80);   // Available

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);

        card.add(scrollPane, BorderLayout.CENTER);
        return card;
    }

    // ─── Helper to create styled buttons ─────────────────────────────────────
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

    // ─── Refresh Table ────────────────────────────────────────────────────────
    /**
     * Clears and repopulates the table from the BookService's ArrayList.
     */
    public void refresh() {
        tableModel.setRowCount(0); // Clear existing rows
        ArrayList<Book> books = bookService.getAllBooks();
        for (Book b : books) {
            tableModel.addRow(new Object[]{
                b.getId(), b.getTitle(), b.getAuthor(), b.getGenre(),
                b.getYear(), b.getTotalQuantity(), b.getAvailableQuantity()
            });
        }
    }

    // ─── Filter Table by Search ───────────────────────────────────────────────
    private void filterTable(String keyword) {
        tableModel.setRowCount(0);
        ArrayList<Book> results = keyword.isEmpty()
            ? bookService.getAllBooks()
            : bookService.searchBooks(keyword);
        for (Book b : results) {
            tableModel.addRow(new Object[]{
                b.getId(), b.getTitle(), b.getAuthor(), b.getGenre(),
                b.getYear(), b.getTotalQuantity(), b.getAvailableQuantity()
            });
        }
    }

    // ─── Add Book Dialog ──────────────────────────────────────────────────────
    private void showAddBookDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Book", true);
        dialog.setSize(420, 370);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(25, 25, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Form fields
        JTextField titleField  = createFormField();
        JTextField authorField = createFormField();
        JTextField genreField  = createFormField();
        JTextField yearField   = createFormField();
        JTextField qtyField    = createFormField();

        addFormRow(form, gbc, 0, "Title *",    titleField);
        addFormRow(form, gbc, 1, "Author *",   authorField);
        addFormRow(form, gbc, 2, "Genre",      genreField);
        addFormRow(form, gbc, 3, "Year",       yearField);
        addFormRow(form, gbc, 4, "Quantity *", qtyField);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(new Color(248, 250, 252));
        JButton saveBtn   = createButton("Save", new Color(59, 130, 246));
        JButton cancelBtn = createButton("Cancel", new Color(100, 116, 139));

        saveBtn.addActionListener(e -> {
            String title  = titleField.getText().trim();
            String author = authorField.getText().trim();
            String genre  = genreField.getText().trim().isEmpty() ? "General" : genreField.getText().trim();
            String yearStr= yearField.getText().trim();
            String qtyStr = qtyField.getText().trim();

            if (title.isEmpty() || author.isEmpty() || qtyStr.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in Title, Author, and Quantity.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            try {
                int year = yearStr.isEmpty() ? 2024 : Integer.parseInt(yearStr);
                int qty  = Integer.parseInt(qtyStr);
                if (qty <= 0) { JOptionPane.showMessageDialog(dialog, "Quantity must be > 0.", "Error", JOptionPane.ERROR_MESSAGE); return; }
                bookService.addBook(title, author, genre, year, qty);
                refresh();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Book added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Year and Quantity must be numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);

        dialog.add(form,     BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ─── Edit Book Dialog ─────────────────────────────────────────────────────
    private void showEditBookDialog() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String bookId = (String) tableModel.getValueAt(selectedRow, 0);
        Book book = bookService.findById(bookId);
        if (book == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Book", true);
        dialog.setSize(420, 370);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(25, 25, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField titleField  = createFormField(); titleField.setText(book.getTitle());
        JTextField authorField = createFormField(); authorField.setText(book.getAuthor());
        JTextField genreField  = createFormField(); genreField.setText(book.getGenre());
        JTextField yearField   = createFormField(); yearField.setText(String.valueOf(book.getYear()));
        JTextField qtyField    = createFormField(); qtyField.setText(String.valueOf(book.getTotalQuantity()));

        addFormRow(form, gbc, 0, "Title",    titleField);
        addFormRow(form, gbc, 1, "Author",   authorField);
        addFormRow(form, gbc, 2, "Genre",    genreField);
        addFormRow(form, gbc, 3, "Year",     yearField);
        addFormRow(form, gbc, 4, "Total Qty",qtyField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(new Color(248, 250, 252));
        JButton saveBtn   = createButton("Update", new Color(16, 185, 129));
        JButton cancelBtn = createButton("Cancel",  new Color(100, 116, 139));

        saveBtn.addActionListener(e -> {
            try {
                String title  = titleField.getText().trim();
                String author = authorField.getText().trim();
                String genre  = genreField.getText().trim();
                int year      = Integer.parseInt(yearField.getText().trim());
                int qty       = Integer.parseInt(qtyField.getText().trim());
                if (title.isEmpty() || author.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Title and Author cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                bookService.updateBook(bookId, title, author, genre, year, qty);
                refresh();
                dialog.dispose();
                JOptionPane.showMessageDialog(this, "Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Year and Quantity must be numbers.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ─── Delete Book ──────────────────────────────────────────────────────────
    private void deleteSelectedBook() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String bookId    = (String) tableModel.getValueAt(selectedRow, 0);
        String bookTitle = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete \"" + bookTitle + "\"?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            bookService.deleteBook(bookId);
            refresh();
            JOptionPane.showMessageDialog(this, "Book deleted successfully!", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ─── Form Helpers ─────────────────────────────────────────────────────────
    private JTextField createFormField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return field;
    }

    private void addFormRow(JPanel form, GridBagConstraints gbc, int row, String label, JComponent field) {
        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lbl.setForeground(new Color(71, 85, 105));
        form.add(lbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        form.add(field, gbc);
    }
}
