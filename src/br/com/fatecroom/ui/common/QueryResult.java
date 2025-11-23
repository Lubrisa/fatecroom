package br.com.fatecroom.ui.common;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.function.Function;

/**
 * Componente genérico para exibir resultados de consulta em uma JTable.
 * UI "moderna" com card e sorter automático.
 */
public class QueryResult<T> extends JPanel {

    private JTable table;
    private DefaultTableModel tableModel;
    private List<T> currentData;
    private final Function<T, Object[]> rowMapper;

    public QueryResult(String title, String[] columnNames, Function<T, Object[]> rowMapper) {
        this.rowMapper = rowMapper;
        initComponents(title, columnNames);
    }

    private void initComponents(String title, String[] columnNames) {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // Card principal
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        card.setBackground(Color.WHITE);

        // Header bonito
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        lblTitle.setForeground(new Color(60, 63, 65));
        lblTitle.setBorder(new EmptyBorder(0, 0, 8, 0));

        card.add(lblTitle, BorderLayout.NORTH);

        // Tabela
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // somente leitura
            }
        };

        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setRowHeight(24);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setShowVerticalLines(false);
        table.setGridColor(new Color(230, 230, 230));
        table.setAutoCreateRowSorter(true); // ordenar colunas

        JTableHeaderModernizer.stylize(table);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        card.add(scrollPane, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    /**
     * Atualiza os dados exibidos na tabela.
     */
    public void setResults(List<T> data) {
        this.currentData = data;
        tableModel.setRowCount(0);

        if (data == null) return;

        for (T item : data) {
            tableModel.addRow(rowMapper.apply(item));
        }
    }

    /**
     * Retorna o objeto da linha selecionada ou null se nada estiver selecionado.
     */
    public T getSelected() {
        if (currentData == null) return null;
        int idx = table.getSelectedRow();
        if (idx < 0) return null;

        // Considerando o sorter, converte índice da view para o modelo
        int modelIdx = table.convertRowIndexToModel(idx);
        if (modelIdx < 0 || modelIdx >= currentData.size()) return null;

        return currentData.get(modelIdx);
    }

    public JTable getTable() {
        return table;
    }

    /**
     * Helper para deixar o cabeçalho da JTable mais moderno.
     */
    private static class JTableHeaderModernizer {
        static void stylize(JTable table) {
            table.getTableHeader().setReorderingAllowed(false);
            table.getTableHeader().setFont(
                    table.getTableHeader().getFont().deriveFont(Font.BOLD, 13f)
            );
            table.getTableHeader().setBackground(new Color(240, 240, 240));
            table.getTableHeader().setForeground(new Color(60, 63, 65));
        }
    }
}
