package ui;

import models.Member;
import services.MemberService;

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
 * MembersPanel provides the full Member management interface.
 * Identical structure to BooksPanel but for Member objects.
 */
public class MembersPanel extends JPanel {

    private MemberService memberService;

    private JTable table;
    private DefaultTableModel tableModel;
    private JTextField searchField;

    private static final String[] COLUMNS = {"ID", "Name", "Email", "Phone", "Join Date"};

    public MembersPanel(MemberService ms) {
        this.memberService = ms;
        setLayout(new BorderLayout());
        setBackground(new Color(241, 245, 249));
        setBorder(new EmptyBorder(30, 30, 30, 30));
        buildUI();
        refresh();
    }

    private void buildUI() {
        add(buildHeader(),    BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel("Members Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(new Color(15, 23, 42));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        searchField = new JTextField(18);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        searchField.putClientProperty("JTextField.placeholderText", "🔍 Search members...");
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        searchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                filterTable(searchField.getText().trim());
            }
        });

        JButton addBtn    = createButton("+ Add Member", new Color(59, 130, 246));
        JButton editBtn   = createButton("✏ Edit",       new Color(16, 185, 129));
        JButton deleteBtn = createButton("🗑 Delete",    new Color(239, 68, 68));
        JButton refreshBtn= createButton("↻ Refresh",    new Color(100, 116, 139));

        controls.add(searchField);
        controls.add(addBtn);
        controls.add(editBtn);
        controls.add(deleteBtn);
        controls.add(refreshBtn);

        header.add(title,    BorderLayout.WEST);
        header.add(controls, BorderLayout.EAST);

        addBtn.addActionListener(e    -> showAddMemberDialog());
        editBtn.addActionListener(e   -> showEditMemberDialog());
        deleteBtn.addActionListener(e -> deleteSelectedMember());
        refreshBtn.addActionListener(e-> refresh());

        return header;
    }

    private JPanel buildTableCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(226, 232, 240), 1));

        tableModel = new DefaultTableModel(COLUMNS, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        table = new JTable(tableModel);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(38);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(220, 252, 231));
        table.setSelectionForeground(new Color(15, 23, 42));
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);

        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 13));
        tableHeader.setBackground(new Color(248, 250, 252));
        tableHeader.setForeground(new Color(71, 85, 105));
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(226, 232, 240)));
        tableHeader.setPreferredSize(new Dimension(0, 42));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < COLUMNS.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(220);
        table.getColumnModel().getColumn(3).setPreferredWidth(130);
        table.getColumnModel().getColumn(4).setPreferredWidth(110);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    // ─── Refresh ──────────────────────────────────────────────────────────────
    public void refresh() {
        tableModel.setRowCount(0);
        ArrayList<Member> members = memberService.getAllMembers();
        for (Member m : members) {
            tableModel.addRow(new Object[]{
                m.getId(), m.getName(), m.getEmail(), m.getPhone(), m.getJoinDate()
            });
        }
    }

    private void filterTable(String keyword) {
        tableModel.setRowCount(0);
        ArrayList<Member> results = keyword.isEmpty()
            ? memberService.getAllMembers()
            : memberService.searchMembers(keyword);
        for (Member m : results) {
            tableModel.addRow(new Object[]{
                m.getId(), m.getName(), m.getEmail(), m.getPhone(), m.getJoinDate()
            });
        }
    }

    // ─── Add Member Dialog ────────────────────────────────────────────────────
    private void showAddMemberDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Member", true);
        dialog.setSize(400, 310);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(25, 25, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField nameField  = createFormField();
        JTextField emailField = createFormField();
        JTextField phoneField = createFormField();

        // Auto-fill today's date
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        JTextField dateField  = createFormField(); dateField.setText(today);

        addFormRow(form, gbc, 0, "Name *",      nameField);
        addFormRow(form, gbc, 1, "Email",        emailField);
        addFormRow(form, gbc, 2, "Phone",        phoneField);
        addFormRow(form, gbc, 3, "Join Date",    dateField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(new Color(248, 250, 252));
        JButton saveBtn   = createButton("Save",   new Color(59, 130, 246));
        JButton cancelBtn = createButton("Cancel", new Color(100, 116, 139));

        saveBtn.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String date  = dateField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name is required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            memberService.addMember(name, email, phone, date);
            refresh();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Member added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ─── Edit Member Dialog ───────────────────────────────────────────────────
    private void showEditMemberDialog() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a member to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String memberId = (String) tableModel.getValueAt(row, 0);
        Member member   = memberService.findById(memberId);
        if (member == null) return;

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Member", true);
        dialog.setSize(400, 280);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(new EmptyBorder(25, 25, 15, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        JTextField nameField  = createFormField(); nameField.setText(member.getName());
        JTextField emailField = createFormField(); emailField.setText(member.getEmail());
        JTextField phoneField = createFormField(); phoneField.setText(member.getPhone());

        addFormRow(form, gbc, 0, "Name",  nameField);
        addFormRow(form, gbc, 1, "Email", emailField);
        addFormRow(form, gbc, 2, "Phone", phoneField);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(new Color(248, 250, 252));
        JButton saveBtn   = createButton("Update", new Color(16, 185, 129));
        JButton cancelBtn = createButton("Cancel", new Color(100, 116, 139));

        saveBtn.addActionListener(e -> {
            String name  = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            memberService.updateMember(memberId, name, email, phone);
            refresh();
            dialog.dispose();
            JOptionPane.showMessageDialog(this, "Member updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });
        cancelBtn.addActionListener(e -> dialog.dispose());

        btnPanel.add(cancelBtn);
        btnPanel.add(saveBtn);
        dialog.add(form, BorderLayout.CENTER);
        dialog.add(btnPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ─── Delete Member ────────────────────────────────────────────────────────
    private void deleteSelectedMember() {
        int row = table.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a member to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String memberId   = (String) tableModel.getValueAt(row, 0);
        String memberName = (String) tableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
            "Delete member \"" + memberName + "\"?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            memberService.deleteMember(memberId);
            refresh();
            JOptionPane.showMessageDialog(this, "Member deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ─── Form Helpers ─────────────────────────────────────────────────────────
    private JTextField createFormField() {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(203, 213, 225), 1),
            new EmptyBorder(6, 10, 6, 10)
        ));
        return f;
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
