import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu implements ActionListener {
    private JFrame frame;
    private JPanel panel;
    private JButton patientsButton;
    private JButton servicesButton;
    private JButton labButton;

    public void mainMenu() {
        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Medical Laboratory System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        panel.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));

        JLabel selectLabel = new JLabel("Select a transaction: ");
        selectLabel.setBounds(10, 10, 250, 25);
        panel.add(selectLabel);

        patientsButton = new JButton("Manage Patient Records");
        patientsButton.setBounds(10, 40, 250, 25);
        patientsButton.addActionListener(this);
        panel.add(patientsButton);

        servicesButton = new JButton("Manage Services");
        servicesButton.setBounds(10, 70, 250, 25);
        servicesButton.addActionListener(this);
        panel.add(servicesButton);

        labButton = new JButton("Manage Laboratory Results");
        labButton.setBounds(10, 100, 250, 25);
        labButton.addActionListener(this);
        panel.add(labButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.dispose();
        if (e.getSource() == patientsButton)
            managePatientRecords();
        else if (e.getSource() == servicesButton)
            manageServices();
        else if (e.getSource() == labButton)
            manageLaboratoryRequest();
    }

    public void managePatientRecords() {
        ManagePatientRecords mpr = new ManagePatientRecords();
        mpr.managePatientRecords();
    }

    public void manageServices() {
        ManageServices ms = new ManageServices();
        ms.manageServices();
    }

    public void manageLaboratoryRequest() {
        ManageLaboratoryRequest mlr = new ManageLaboratoryRequest();
        mlr.manageLaboratoryRequest();
    }
}
