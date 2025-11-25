 package br.com.fatecroom;

import br.com.fatecroom.service.ReservaSalaService;
import br.com.fatecroom.service.ReservaSalaServiceMock;
import br.com.fatecroom.ui.reserva.ReservaSalaFormFrame;
import br.com.fatecroom.ui.reserva.ReservaSalaQueryFrame;

import javax.swing.*;

//Mock criado para substituir o Repository que será preenchido com os commits do Luigi
public class Main {

    public static void main(String[] args) {
        // Deixa a UI mais moderna (Nimbus)
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // se der erro, segue com o padrão mesmo
        }

        SwingUtilities.invokeLater(() -> {
            // Service em memória para teste
            ReservaSalaService service = new ReservaSalaServiceMock();

            // Abre a tela de cadastro
            ReservaSalaFormFrame formFrame = new ReservaSalaFormFrame(service);
            formFrame.setVisible(true);

            // Abre a tela de consulta
            ReservaSalaQueryFrame queryFrame = new ReservaSalaQueryFrame(service);
            queryFrame.setLocation(
                    formFrame.getX() + formFrame.getWidth() + 10,
                    formFrame.getY()
            );
            queryFrame.setVisible(true);
        });
    }
}
