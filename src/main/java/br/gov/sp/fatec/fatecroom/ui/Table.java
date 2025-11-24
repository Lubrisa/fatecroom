package br.gov.sp.fatec.fatecroom.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

public class Table {
    private Table() {}

    // estilo reutilizado
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TITLE_COLOR = new Color(48, 48, 48);
    private static final int TITLE_FONT_SIZE = 18;
    private static final int ROW_HEIGHT = 24;
    private static final int CELL_PADDING = 12;

    private static JLabel buildTitleLabel(String title) {
        var lbl = new JLabel(title, SwingConstants.LEFT);
        lbl.setFont(lbl.getFont().deriveFont(Font.BOLD, TITLE_FONT_SIZE));
        lbl.setForeground(TITLE_COLOR);
        lbl.setOpaque(false);
        lbl.setBorder(BorderFactory.createEmptyBorder(8, 6, 8, 6));
        return lbl;
    }

    private static class SimpleTableModel extends DefaultTableModel {
        SimpleTableModel() { super(); }
        @Override
        public boolean isCellEditable(int row, int column) {
            // permite edição apenas na coluna "Ações" para que o editor com botões funcione
            try {
                return "Ações".equals(getColumnName(column));
            } catch (Exception e) {
                return false;
            }
        }
        @Override
        public Class<?> getColumnClass(int columnIndex) {
            // determina o tipo da coluna baseado nos valores presentes (ex.: Boolean)
            for (int r = 0; r < getRowCount(); r++) {
                Object v = getValueAt(r, columnIndex);
                if (v != null) return v.getClass();
            }
            return Object.class;
        }
    }

    // Renderer que adiciona padding interno às células para evitar que o conteúdo
    // fique encostado nas linhas da tabela.
    private static class PaddedCellRenderer extends DefaultTableCellRenderer {
        private static final javax.swing.border.Border PADDING =
            BorderFactory.createEmptyBorder(6, 8, 6, 8); // top,left,bottom,right

        @Override
        public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                boolean isSelected, boolean hasFocus,
                                                                int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setBorder(PADDING);
            setHorizontalAlignment(SwingConstants.LEFT);
            setOpaque(false); // evita que a seleção altere o background visual
            return this;
        }
    }

    // Renderer/Editor para a coluna de Ações: mostra botões (um por ação) com espaçamento.
    // Agora usa Consumers que recebem apenas o registro (Map<String,String>).
    private static class ActionCell extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
        private final Map<String, Consumer<Map<String, String>>> actions;
        private final List<Map<String, String>> data;

        ActionCell(Map<String, Consumer<Map<String, String>>> actions, List<Map<String, String>> data) {
            this.actions = actions;
            this.data = data;
        }

        private JPanel buildPanel(int row, boolean editable) {
            var panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
            panel.setOpaque(false);
            for (var entry : actions.entrySet()) {
                var key = entry.getKey();
                var btn = new JButton(key);
                btn.setFocusable(false);
                // mostrar aparência habilitada; editor irá receber clique
                btn.setEnabled(true);
                btn.setBorderPainted(true);
                btn.setContentAreaFilled(true);
                btn.setOpaque(true);
                if (editable) {
                    btn.addActionListener(e -> {
                        // defensivo: índice pode variar se tabela for reordenada, usamos row passado
                        if (row >= 0 && row < data.size()) {
                            try {
                                entry.getValue().accept(data.get(row));
                            } catch (Exception ex) {
                                // falhas devem ser tratadas/mostradas pelo Consumer (alertas)
                            }
                        }
                        stopCellEditing();
                    });
                }
                panel.add(btn);
            }
            return panel;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return buildPanel(row, false);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            return buildPanel(row, true);
        }

        @Override
        public Object getCellEditorValue() { return null; }
    }

    private static void addColumns(DefaultTableModel model, List<String> columns) {
        for (var c : columns) model.addColumn(c);
    }
    
    private static void addRows(DefaultTableModel model, List<String> columns, List<Map<String, String>> data) {
        for (var rowData : data) {
            var row = new Object[columns.size()];
            for (int i = 0; i < columns.size(); i++) {
                String raw = rowData.get(columns.get(i));
                if (raw != null) {
                    var s = raw.trim();
                    if ("true".equalsIgnoreCase(s) || "false".equalsIgnoreCase(s)) {
                        row[i] = Boolean.valueOf(s);
                    } else {
                        row[i] = raw;
                    }
                } else {
                    row[i] = null;
                }
            }
            model.addRow(row);
        }
    }

    private static void adjustColumnWidths(JTable table) {
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        var headerRenderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        // adicionar padding também ao header para alinhamento visual com as células
        headerRenderer.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        table.getTableHeader().setDefaultRenderer(headerRenderer);

        var model = table.getModel();
        int cols = table.getColumnModel().getColumnCount();
        int maxRowHeight = ROW_HEIGHT;

        for (int col = 0; col < cols; col++) {
            int maxWidth = 0;
            TableColumn column = table.getColumnModel().getColumn(col);

            var headerComp = headerRenderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, 0, col);
            maxWidth = Math.max(maxWidth, headerComp.getPreferredSize().width);
            maxRowHeight = Math.max(maxRowHeight, headerComp.getPreferredSize().height);

            for (int row = 0; row < model.getRowCount(); row++) {
                var cellRenderer = table.getCellRenderer(row, col);
                var comp = cellRenderer.getTableCellRendererComponent(table, model.getValueAt(row, col), false, false, row, col);
                maxWidth = Math.max(maxWidth, comp.getPreferredSize().width);
                maxRowHeight = Math.max(maxRowHeight, comp.getPreferredSize().height);
            }

            column.setPreferredWidth(maxWidth + CELL_PADDING);
        }

        table.setRowHeight(Math.max(ROW_HEIGHT, maxRowHeight));
    }

    private static JPanel scrollPanelWithTitle(String title, JScrollPane scroll) {
        var panel = new JPanel(new BorderLayout(0,0));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));
        if (title != null && !title.isBlank()) panel.add(buildTitleLabel(title), BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    /**
     * Cria um painel contendo uma tabela com os dados fornecidos, incluindo
     * uma barra de rolagem e um título opcional.
     * @param title título da tabela (pode ser null ou vazio)
     * @param columns lista de nomes das colunas
     * @param data lista de registros, cada um representado como um mapa de nome da coluna para valor
     * @param rowActions mapa de ações para cada linha, onde a chave é o nome do botão e o valor é um Consumer que recebe o registro
     * @return painel contendo a tabela com barra de rolagem e título opcional
     */
    public static JPanel create(String title, List<String> columns, List<Map<String, String>> data, Map<String, Consumer<Map<String, String>>> rowActions) {
        var main = new JPanel(new BorderLayout(0,0));
        main.setBackground(CARD_BACKGROUND);
        main.setBorder(BorderFactory.createEmptyBorder(0,0,0,0));

        // modelo e tabela separados para clareza
        var model = buildModel(columns, data);
        var table = buildTable(model, data, rowActions);

        adjustColumnWidths(table);

        var scroll = createScrollPane(table);
        var content = scrollPanelWithTitle(title, scroll);

        main.add(content, BorderLayout.CENTER);
        return main;
    }

    // constrói e popula o modelo
    private static SimpleTableModel buildModel(List<String> columns, List<Map<String, String>> data) {
        var model = new SimpleTableModel();
        addColumns(model, columns);
        addRows(model, columns, data);
        return model;
    }

    // cria a JTable e aplica renderers/configurações; adiciona coluna de ações se houver ações
    private static JTable buildTable(SimpleTableModel model, List<Map<String, String>> data, Map<String, Consumer<Map<String, String>>> rowActions) {
        var table = new JTable(model);

        // renderer para Boolean: exibe JCheckBox centralizado (apenas visual)
        table.setDefaultRenderer(Boolean.class, new TableCellRenderer() {
            private final JCheckBox cb = new JCheckBox();
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                                                                    boolean isSelected, boolean hasFocus,
                                                                    int row, int column) {
                cb.setSelected(Boolean.TRUE.equals(value));
                cb.setHorizontalAlignment(SwingConstants.CENTER);
                cb.setOpaque(false);
                return cb;
            }
        });

        // aplica renderer com padding para todas as células de objeto
        table.setDefaultRenderer(Object.class, new PaddedCellRenderer());
        configureTableAppearance(table);

        // se houver ações definidas, adiciona coluna "Ações" e configura renderer/editor
        if (rowActions != null && !rowActions.isEmpty()) {
            addActionsColumn(table, model, rowActions, data);
        }

        return table;
    }

    // configura aparência geral da tabela
    private static void configureTableAppearance(JTable table) {
        table.setBackground(CARD_BACKGROUND);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);
        table.getTableHeader().setBackground(CARD_BACKGROUND);
        table.getTableHeader().setFont(table.getTableHeader().getFont().deriveFont(Font.BOLD));
        table.setFillsViewportHeight(true);

        // remove indicação visual de seleção: deixamos seleção com mesmas cores do fundo
        table.setSelectionBackground(CARD_BACKGROUND);
        table.setSelectionForeground(table.getForeground());
    }

    // adiciona coluna "Ações" e configura renderer/editor
    private static void addActionsColumn(JTable table, SimpleTableModel model, Map<String, Consumer<Map<String, String>>> rowActions, List<Map<String, String>> data) {
        model.addColumn("Ações"); // adiciona coluna para todas as linhas atuais
        // cria instância do renderer/editor que tem acesso aos dados e ações
        var actionCell = new ActionCell(rowActions, data);
        int lastIdx = table.getColumnModel().getColumnCount() - 1;
        var actionsColumn = table.getColumnModel().getColumn(lastIdx);
        actionsColumn.setCellRenderer(actionCell);
        actionsColumn.setCellEditor(actionCell);
        actionsColumn.setPreferredWidth(120);
    }

    // cria JScrollPane padronizado para a tabela
    private static JScrollPane createScrollPane(JTable table) {
        var scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(CARD_BACKGROUND);
        scroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        scroll.setPreferredSize(new Dimension(700, 300));
        scroll.setViewportBorder(BorderFactory.createEmptyBorder());
        return scroll;
    }
}