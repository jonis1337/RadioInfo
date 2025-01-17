import com.formdev.flatlaf.themes.FlatMacDarkLaf;
import se.umu.cs.appjava.controller.PageController;
import se.umu.cs.appjava.view.MainWindow;
import javax.swing.*;

public class Main {
    public static void main(String[] args){
        try{
            UIManager.setLookAndFeel(new FlatMacDarkLaf());
        } catch (UnsupportedLookAndFeelException e) {
            System.out.println("Error: UnsupportedLookAndFeelException");
        }

        SwingUtilities.invokeLater(() -> {
            new PageController(new MainWindow());


        });
    }
}
