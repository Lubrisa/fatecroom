package br.com.fatecroom.ui.reserva;

import br.com.fatecroom.model.ReservaSala;
import br.com.fatecroom.ui.common.Form;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Formulário de cadastro/edição de Reserva de Sala/Laboratório.
 * Foca em UI mais moderna, com card e espaçamento.
 */
public class ReservaSalaFormPanel extends JPanel implements Form<ReservaSala> {

    private JTextField txtIdSala;
    private JTextField txtIdUsuario;
    private JTextField txtDataReserva;
    private JTextField txtHoraInicio;
    private JTextField txtHoraFim;
    private JComboBox<String> cbStatus;
    private JTextArea txtObservacao;

    public ReservaSalaFormPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // Card principal
        JPanel card = new JPanel(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(14, 16, 16, 16)
        ));
        card.setBackground(Color.WHITE);

        // Header
        JLabel lblTitle = new JLabel("Reserva de Sala / Laboratório");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        lblTitle.setForeground(new Color(55, 71, 79));

        JLabel lblSubtitle = new JLabel("Preencha os dados da reserva abaixo.");
        lblSubtitle.setForeground(new Color(120, 120, 120));

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.add(lblTitle);
        header.add(Box.createVerticalStrut(2));
        header.add(lblSubtitle);

        card.add(header, BorderLayout.NORTH);

        // Form (GridBag)
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        txtIdSala = new JTextField(10);
        txtIdUsuario = new JTextField(10);
        txtDataReserva = new JTextField(10);  // yyyy-MM-dd
        txtHoraInicio = new JTextField(5);    // HH:mm
        txtHoraFim = new JTextField(5);       // HH:mm
        cbStatus = new JComboBox<>(new String[]{"PENDENTE", "CONFIRMADA", "CANCELADA"});
        txtObservacao = new JTextArea(4, 20);

        txtObservacao.setLineWrap(true);
        txtObservacao.setWrapStyleWord(true);
        JScrollPane scrollObs = new JScrollPane(txtObservacao);
        scrollObs.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

        int row = 0;

        // Linha 1: ID Sala / ID Usuário
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("ID Sala:"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtIdSala, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("ID Usuário:"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtIdUsuario, gbc);
        row++;

        // Linha 2: Data / Hora Início / Hora Fim
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Data (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        formPanel.add(txtDataReserva, gbc);

        gbc.gridx = 2;
        formPanel.add(new JLabel("Início (HH:mm):"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtHoraInicio, gbc);
        row++;

        gbc.gridy = row;
        gbc.gridx = 2;
        formPanel.add(new JLabel("Fim (HH:mm):"), gbc);
        gbc.gridx = 3;
        formPanel.add(txtHoraFim, gbc);
        row++;

        // Linha 3: Status
        gbc.gridy = row;
        gbc.gridx = 0;
        formPanel.add(new JLabel("Status:"), gbc);
        gbc.gridx = 1;
        gbc.gridwidth = 3;
        formPanel.add(cbStatus, gbc);
        gbc.gridwidth = 1;
        row++;

        // Linha 4: Observação (ocupa linha inteira)
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Observação:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        gbc.weightx = 1;
        gbc.weighty = 1;
        formPanel.add(scrollObs, gbc);

        card.add(formPanel, BorderLayout.CENTER);

        add(card, BorderLayout.CENTER);
    }

    @Override
    public ReservaSala getData() {
        String idSalaStr = txtIdSala.getText().trim();
        String idUsuarioStr = txtIdUsuario.getText().trim();
        String data = txtDataReserva.getText().trim();
        String inicio = txtHoraInicio.getText().trim();
        String fim = txtHoraFim.getText().trim();

        if (idSalaStr.isEmpty() || idUsuarioStr.isEmpty() ||
                data.isEmpty() || inicio.isEmpty() || fim.isEmpty()) {
            throw new IllegalArgumentException("Preencha todos os campos obrigatórios.");
        }

        ReservaSala r = new ReservaSala();
        r.setIdSala(Integer.parseInt(idSalaStr));
        r.setIdUsuario(Integer.parseInt(idUsuarioStr));
        r.setDataReserva(data);
        r.setHoraInicio(inicio);
        r.setHoraFim(fim);
        r.setStatus((String) cbStatus.getSelectedItem());
        r.setObservacao(txtObservacao.getText().trim());

        return r;
    }

    @Override
    public void setData(ReservaSala data) {
        if (data == null) {
            clear();
            return;
        }
        txtIdSala.setText(String.valueOf(data.getIdSala()));
        txtIdUsuario.setText(String.valueOf(data.getIdUsuario()));
        txtDataReserva.setText(data.getDataReserva());
        txtHoraInicio.setText(data.getHoraInicio());
        txtHoraFim.setText(data.getHoraFim());
        cbStatus.setSelectedItem(data.getStatus());
        txtObservacao.setText(data.getObservacao());
    }

    @Override
    public void clear() {
        txtIdSala.setText("");
        txtIdUsuario.setText("");
        txtDataReserva.setText("");
        txtHoraInicio.setText("");
        txtHoraFim.setText("");
        cbStatus.setSelectedItem("PENDENTE");
        txtObservacao.setText("");
    }
}
