package br.com.fatecroom.ui.reserva;

import br.com.fatecroom.model.ReservaSala;
import br.com.fatecroom.service.ReservaSalaService;

import javax.swing.*;
import java.awt.*;

/**
 * Janela que encapsula o formulário de reserva e integra com o Service.
 */
public class ReservaSalaFormFrame extends JFrame {

    private final ReservaSalaService service;
    private final ReservaSalaFormPanel formPanel;

    public ReservaSalaFormFrame(ReservaSalaService service) {
        super("Nova Reserva de Sala / Laboratório");
        this.service = service;
        this.formPanel = new ReservaSalaFormPanel();
        initComponents();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        add(formPanel, BorderLayout.CENTER);

        JButton btnSalvar = new JButton("Salvar Reserva");
        btnSalvar.setFocusPainted(false);
        btnSalvar.setBackground(new Color(33, 150, 243));
        btnSalvar.setForeground(Color.WHITE);

        JButton btnLimpar = new JButton("Limpar");
        btnLimpar.setFocusPainted(false);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        buttons.add(btnLimpar);
        buttons.add(btnSalvar);

        add(buttons, BorderLayout.SOUTH);

        btnSalvar.addActionListener(e -> onSalvar());
        btnLimpar.addActionListener(e -> formPanel.clear());

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(720, 380);
        setLocationRelativeTo(null);
    }

    private void onSalvar() {
        try {
            ReservaSala nova = formPanel.getData();

            boolean disponivel = service.isDisponivel(
                    nova.getIdSala(),
                    nova.getDataReserva(),
                    nova.getHoraInicio(),
                    nova.getHoraFim()
            );

            if (!disponivel) {
                JOptionPane.showMessageDialog(this,
                        "Sala indisponível para o período informado.",
                        "Indisponível",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            service.registrarReserva(nova);

            JOptionPane.showMessageDialog(this,
                    "Reserva registrada com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);

            formPanel.clear();

        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Dados inválidos",
                    JOptionPane.WARNING_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Erro ao salvar reserva: " + ex.getMessage(),
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
