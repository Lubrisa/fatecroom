package br.com.fatecroom.ui.reserva;

import br.com.fatecroom.model.ReservaSala;
import br.com.fatecroom.service.ReservaSalaService;
import br.com.fatecroom.ui.common.QueryResult;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Tela de Consulta Avançada de Reservas (por sala, usuário, data).
 */
public class ReservaSalaQueryPanel extends JPanel {

    private final ReservaSalaService service;

    private JTextField txtIdSala;
    private JTextField txtIdUsuario;
    private JTextField txtDataReserva;
    private QueryResult<ReservaSala> queryResult;

    public ReservaSalaQueryPanel(ReservaSalaService service) {
        this.service = service;
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(new Color(245, 245, 245));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // Card de filtros
        JPanel filterCard = new JPanel(new GridBagLayout());
        filterCard.setBackground(Color.WHITE);
        filterCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220)),
                new EmptyBorder(10, 12, 10, 12)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lblTitle = new JLabel("Consulta Avançada de Reservas");
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        lblTitle.setForeground(new Color(55, 71, 79));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        filterCard.add(lblTitle, gbc);

        gbc.gridwidth = 1;

        txtIdSala = new JTextField(8);
        txtIdUsuario = new JTextField(8);
        txtDataReserva = new JTextField(10); // yyyy-MM-dd

        int row = 1;

        // Linha: ID Sala / ID Usuário / Data
        gbc.gridy = row;
        gbc.gridx = 0;
        filterCard.add(new JLabel("ID Sala:"), gbc);
        gbc.gridx = 1;
        filterCard.add(txtIdSala, gbc);

        gbc.gridx = 2;
        filterCard.add(new JLabel("ID Usuário:"), gbc);
        gbc.gridx = 3;
        filterCard.add(txtIdUsuario, gbc);
        row++;

        gbc.gridy = row;
        gbc.gridx = 0;
        filterCard.add(new JLabel("Data (yyyy-MM-dd):"), gbc);
        gbc.gridx = 1;
        filterCard.add(txtDataReserva, gbc);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBackground(new Color(33, 150, 243));
        btnBuscar.setForeground(Color.WHITE);

        gbc.gridx = 3;
        filterCard.add(btnBuscar, gbc);

        add(filterCard, BorderLayout.NORTH);

        // QueryResult
        String[] colunas = {
                "ID Reserva", "ID Sala", "ID Usuário",
                "Data", "Início", "Fim", "Status", "Observação"
        };

        queryResult = new QueryResult<>(
                "Resultados da Consulta",
                colunas,
                r -> new Object[]{
                        r.getIdReserva(),
                        r.getIdSala(),
                        r.getIdUsuario(),
                        r.getDataReserva(),
                        r.getHoraInicio(),
                        r.getHoraFim(),
                        r.getStatus(),
                        r.getObservacao()
                }
        );

        add(queryResult, BorderLayout.CENTER);

        btnBuscar.addActionListener(e -> onBuscar());
    }

    private void onBuscar() {
        try {
            Integer idSala = null;
            Integer idUsuario = null;
            String data = null;

            String idSalaStr = txtIdSala.getText().trim();
            String idUsuarioStr = txtIdUsuario.getText().trim();
            String dataStr = txtDataReserva.getText().trim();

            if (!idSalaStr.isEmpty()) {
                idSala = Integer.parseInt(idSalaStr);
            }
            if (!idUsuarioStr.isEmpty()) {
                idUsuario = Integer.parseInt(idUsuarioStr);
            }
            if (!dataStr.isEmpty()) {
                data = dataStr;
            }

            List<ReservaSala> lista = service.buscarReservas(idSala, idUsuario, data);
            queryResult.setResults(lista);

            if (lista.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Nenhuma reserva encontrada com os filtros informados.",
                        "Sem resultados",
                        JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "ID de sala e ID de usuário devem ser numéricos.",
                    "Filtro inválido",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao buscar reservas: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Retorna a reserva selecionada na tabela (pode ser útil para cancelar).
     */
    public ReservaSala getReservaSelecionada() {
        return queryResult.getSelected();
    }
}
