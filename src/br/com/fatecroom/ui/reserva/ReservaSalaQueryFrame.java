package br.com.fatecroom.ui.reserva;

import br.com.fatecroom.service.ReservaSalaService;

import javax.swing.*;
import java.awt.*;

/**
 * Janela que encapsula a tela de consulta avançada de reservas.
 */
public class ReservaSalaQueryFrame extends JFrame {

    public ReservaSalaQueryFrame(ReservaSalaService service) {
        super("Consulta Avançada de Reservas");
        ReservaSalaQueryPanel panel = new ReservaSalaQueryPanel(service);
        initComponents(panel);
    }

    private void initComponents(ReservaSalaQueryPanel panel) {
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(900, 500);
        setLocationRelativeTo(null);
    }
}
