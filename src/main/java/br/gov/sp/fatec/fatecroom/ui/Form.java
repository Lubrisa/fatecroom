package br.gov.sp.fatec.fatecroom.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Utility class for creating forms with various input fields and submit behavior.
 */
public final class Form {
    private Form() {}

    // Constants
    private static final Color CARD_BACKGROUND = Color.WHITE;
    private static final Color TITLE_COLOR = new Color(128, 128, 128); // #808080
    private static final Color BUTTON_BG = new Color(128, 128, 128); // #808080
    private static final Color BUTTON_FG = Color.WHITE;
    private static final Insets FIELD_INSETS = new Insets(3, 3, 3, 3);
    private static final int PADDING = 8;
    private static final int VERTICAL_SPACE = 6;
    private static final int TITLE_FONT_SIZE = 16;
    private static final String CLEAR_BUTTON_TEXT = "Limpar";
    private static final String DEFAULT_SUBMIT_BUTTON_TEXT = "Enviar";

    public static JTextField createTextField(int minLen, int maxLen, boolean required, String initialValue) {
        var tf = new JTextField();
        tf.setColumns(Math.min(Math.max(10, minLen * 2), 40));
        if (initialValue != null) tf.setText(initialValue);
        return tf;
    }

    public static JPasswordField createPasswordField(int minLen, int maxLen, boolean required, String initialValue) {
        var pf = new JPasswordField();
        pf.setColumns(Math.min(Math.max(10, minLen * 2), 40));
        if (initialValue != null) pf.setText(initialValue);
        return pf;
    }

    public static JComboBox<String> createDropdownField(String[] options, boolean required, String initialValue) {
        var combo = new JComboBox<>(options);
        if (initialValue != null) combo.setSelectedItem(initialValue);
        return combo;
    }

    public static JCheckBox createCheckboxField(boolean initialValue) {
        var cb = new JCheckBox();
        cb.setSelected(initialValue);
        return cb;
    }

    private static Map<String, String> collectFormData(Map<String, ? extends Component> fields) {
        var result = new LinkedHashMap<String, String>();
        for (var e : fields.entrySet()) {
            var key = e.getKey();
            var c = e.getValue();
            String val;

            switch (c) {
                case JPasswordField jPasswordField -> val = new String(jPasswordField.getPassword());
                case JTextField jTextField -> val = jTextField.getText();
                case JCheckBox jCheckBox -> val = Boolean.toString(jCheckBox.isSelected());
                case JComboBox<?> jComboBox -> {
                    var sel = jComboBox.getSelectedItem();
                    val = sel == null ? "" : sel.toString();
                }
                default -> val = c.toString();
            }

            result.put(key, val);
        }
        return result;
    }

    private static void clearFormFields(Map<String, ? extends Component> fields) {
        for (var e : fields.entrySet()) {
            var c = e.getValue();
            switch (c) {
                case JPasswordField jPasswordField -> jPasswordField.setText("");
                case JTextField jTextField -> jTextField.setText("");
                case JCheckBox jCheckBox -> jCheckBox.setSelected(false);
                case JComboBox<?> jComboBox -> jComboBox.setSelectedItem(null);
                default -> // nothing for unknown component types
                {
                    // nothing for unknown component types
                }
            }
        }
    }

    private static JLabel buildTitle(String title) {
        var titleLabel = new JLabel(title);
        Font base = titleLabel.getFont();
        titleLabel.setFont(base.deriveFont(Font.BOLD, TITLE_FONT_SIZE));
        titleLabel.setForeground(TITLE_COLOR);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, VERTICAL_SPACE, 0));
        return titleLabel;
    }

    private static JPanel buildFormArea(LinkedHashMap<String, ? extends Component> fields) {
        var formArea = new JPanel(new GridBagLayout());
        // ensure the form area itself uses the card background
        formArea.setOpaque(true);
        formArea.setBackground(CARD_BACKGROUND);

        var gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.insets = FIELD_INSETS;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;
        for (var e : fields.entrySet()) {
            gridBagConstraints.gridx = 0;
            gridBagConstraints.gridy = row;
            gridBagConstraints.weightx = 0;
            gridBagConstraints.anchor = GridBagConstraints.WEST;

            // label: make opaque so its background shows the card color
            var label = new JLabel(e.getKey());
            label.setOpaque(true);
            label.setBackground(CARD_BACKGROUND);
            formArea.add(label, gridBagConstraints);

            gridBagConstraints.gridx = 1;
            gridBagConstraints.weightx = 1.0;
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;

            var field = e.getValue();
            // ensure the component paints its background as the card color
            if (field instanceof JComponent jc) {
                jc.setOpaque(true);
                jc.setBackground(CARD_BACKGROUND);
                jc.setAlignmentX(Component.LEFT_ALIGNMENT);
            } else if (field != null) {
                field.setBackground(CARD_BACKGROUND);
            }

            formArea.add(field, gridBagConstraints);
            row++;
        }
        return formArea;
    }

    private static void styleButton(JButton b) {
        b.setBackground(BUTTON_BG);
        b.setForeground(BUTTON_FG);
        b.setOpaque(true);
        b.setBorderPainted(false);
    }

    private static JPanel buildBottomPanel(
        LinkedHashMap<String, ? extends Component> fields,
        BiConsumer<Map<String, String>, JLabel> onSubmit,
        String submitButtonText
    ) {
        var bottom = new JPanel(new BorderLayout(6, 6));
        var status = new JLabel(" ");
        bottom.add(status, BorderLayout.CENTER);

        var buttons = new JPanel();
        buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));

        var clear = new JButton(CLEAR_BUTTON_TEXT);
        styleButton(clear);
        clear.addActionListener(ae -> {
            clearFormFields(fields);
            status.setText(" ");
        });

        var submit = new JButton(submitButtonText);
        styleButton(submit);
        submit.addActionListener(ae -> {
            try {
                onSubmit.accept(collectFormData(fields), status);
            } catch (Exception ex) {
                status.setText("Erro: " + ex.getMessage());
            }
        });

        buttons.add(clear);
        buttons.add(Box.createHorizontalStrut(8));
        buttons.add(submit);

        bottom.add(buttons, BorderLayout.EAST);
        return bottom;
    }

    private static JPanel createCard( Component content ) {
        var card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
        card.add(content, BorderLayout.CENTER);
        return card;
    }

    /**
     * Creates a form panel with the specified configurations.
     * @param title The title of the form.
     * @param fields A LinkedHashMap where keys are field labels and values are the corresponding input components.
     * @param onSubmit A BiConsumer that handles form submission. It receives the collected form data and a status JLabel.
     * @param submitButtonText The text to display on the submit button.
     * @return A JPanel containing the constructed form.
     */
   public static JPanel create(
        String title,
        LinkedHashMap<String, ? extends Component> fields,
        BiConsumer<Map<String, String>, JLabel> onSubmit,
        String submitButtonText
    ) {
        if (fields == null || fields.isEmpty())
            throw new IllegalArgumentException("Fields map cannot be null or empty.");
        if (onSubmit == null)
            throw new IllegalArgumentException("onSubmit handler cannot be null.");

        var container = new JPanel(new BorderLayout(8, 8));
        container.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));

        if (title != null && !title.isBlank()) {
            container.add(buildTitle(title), BorderLayout.NORTH);
        }

        var formArea = buildFormArea(fields);
        var bottom = buildBottomPanel(
            fields,
            onSubmit,
            submitButtonText != null && !submitButtonText.isBlank()
                ? submitButtonText
                : DEFAULT_SUBMIT_BUTTON_TEXT
        );

        // wrapper contains formArea + bottom so the whole block has a fixed preferred height
        var wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.add(formArea);
        wrapper.add(Box.createVerticalStrut(VERTICAL_SPACE));
        wrapper.add(bottom);

        // ensure central panels use the card background so the whole area appears white
        formArea.setOpaque(true);
        formArea.setBackground(CARD_BACKGROUND);
        bottom.setOpaque(true);
        bottom.setBackground(CARD_BACKGROUND);
        wrapper.setOpaque(true);
        wrapper.setBackground(CARD_BACKGROUND);

        // cap maximum height to the preferred height so it won't stretch vertically on tall screens
        var pref = wrapper.getPreferredSize();
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));

        // put wrapper inside a white card
        var card = createCard(wrapper);

        // keep card at its preferred height by placing it at NORTH
        container.add(card, BorderLayout.NORTH);

        return container;
    }
}