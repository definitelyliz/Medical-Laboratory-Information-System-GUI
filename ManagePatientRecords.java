/*
* Class: ManagePatientRecords
* Manages Patient Records such as
*   adding a new patient record,
*   searching for a patient record,
*   deleting a patient record,
*   and editing a patient record.
* */

import com.toedter.calendar.JDateChooser;

import javax.swing.Timer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class ManagePatientRecords implements ActionListener {
    private ArrayList<Patient> patients;

    private ManageLaboratoryRequest manageLaboratoryRequest;
    private WriteToFile writeToFile;
    private ReadFile readFile;
    private MainMenu mainMenu;

    private JFrame frame;
    private JPanel panel;
    private JFrame displayFrame;
    private JPanel displayPanel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JButton returnButton;

    private int scan = 0;
    private int line = -1;
    private int searched = 0;
    private int methodType = -1;
    private int[] lines = new int[256];
    private int noRecordFound = 0;
    private String successDialogue;

    private String patientUID;
    private String nationalIDNo;
    private String lastName;
    private String firstName;
    private String birthday;
    private String finalUID;
    private String deleteReason;
    private String editUpdate;

    private int countServices;
    private int countPatients;
    private String[][] services;
    private String[][] patientRecords;

    /* main menu */
    public void managePatientRecords() {
        mainMenu = new MainMenu();
        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Patient Records");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        panel.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));

        JLabel selectLabel = new JLabel("Select a transaction: ");
        selectLabel.setBounds(10, 10, 250, 25);
        panel.add(selectLabel);

        addButton = new JButton("Add New Patient");
        addButton.addActionListener(this);
        addButton.setBounds(10, 10, 500, 25);
        panel.add(addButton);

        editButton = new JButton("Edit Patient Record");
        editButton.addActionListener(this);
        addButton.setBounds(10, 30, 500, 25);
        panel.add(editButton);

        deleteButton = new JButton("Delete Patient Record");
        deleteButton.addActionListener(this);
        deleteButton.setBounds(10, 50, 500, 25);
        panel.add(deleteButton);

        searchButton = new JButton("Search Patient Record");
        searchButton.addActionListener(this);
        searchButton.setBounds(10, 70, 500, 25);
        panel.add(searchButton);

        returnButton = new JButton("Return to Main Menu");
        returnButton.addActionListener(this);
        returnButton.setBounds(10, 90, 500, 25);
        panel.add(returnButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        frame.dispose();
        if (e.getSource() == addButton)
            addNewPatient();
        else if (e.getSource() == editButton)
            editPatientRecord();
        else if (e.getSource() == deleteButton)
            deletePatientRecord();
        else if (e.getSource() == searchButton)
            searchPatientRecord();
        else if (e.getSource() == returnButton)
            mainMenu.mainMenu();
    }

    /* generates Patient UID */
    public String generateUID() {
        readFile = new ReadFile();

        String[] tempUID = new String[7];
        tempUID[0] = "P";

        int B = Calendar.getInstance().get(Calendar.YEAR);
        String temp = String.valueOf(B);
        char[] cTemp = new char[temp.length()];
        for (int i = 0; i < temp.length(); i++)
            cTemp[i] = temp.charAt(i);
        for (int i = 0; i < temp.length(); i++)
            tempUID[i + 1] = String.valueOf(cTemp[i]);

        int C = Calendar.getInstance().get(Calendar.MONTH)+1;
        temp = String.valueOf(C);
        for (int i = 0; i < temp.length(); i++)
            cTemp[i] = temp.charAt(i);
        for (int i = 0; i < temp.length(); i++) {
            if (C > 9)
                tempUID[i + 5] = String.valueOf(cTemp[i]);
            else {
                tempUID[5] = "0";
                tempUID[6] = String.valueOf(cTemp[i]);
            }
        }

        //GET UID FROM PATIENTS.TXT
        String fileName = "Patients.txt";
        int isFirst = readFile.readUID(fileName);
        String prevUID = readFile.getUID();

        String D;
        String E;
        String newUID;

        //CHECK IF FIRST UID TO BE GENERATED
        if(isFirst==1){
            newUID = "AAA00";
            String str = String.join("", tempUID);
            return String.join("",str, newUID);
        }
        else {
            D = prevUID.substring(7, 10);
            E = prevUID.substring(prevUID.length()-2);
        }

        //CHECK DDD - AAA/ZZZ and EE - 00/99
        int num = Integer.parseInt(E);
        char strTemp;
        int numTemp;
        if(num==99){
            if(D.charAt(2)!='Z' && D.charAt(2)<='Z') {
                strTemp = D.charAt(2);
                strTemp++;
                D = D.substring(0, D.length()-1);
                D = String.join("", D, String.valueOf(strTemp));
            } else if (D.charAt(1)!='Z' && D.charAt(1)<='Z') {
                strTemp = D.charAt(1);
                strTemp++;
                D = D.substring(0, D.length()-2);
                D = String.join("", D, String.valueOf(strTemp), "Z");
            } else {
                strTemp = D.charAt(0);
                strTemp++;
                D = String.join("", String.valueOf(strTemp), "ZZ");
            }
            E = "00";
        } else {
            numTemp = num;
            if(numTemp<9) {
                numTemp++;
                strTemp = '0';
                E = String.join("", String.valueOf(strTemp), String.valueOf(numTemp));
            } else {
                numTemp++;
                E = String.valueOf(numTemp);
            }
        }
        newUID = String.join("", D, E);

        String str = String.join("", tempUID);
        return String.join("", str, newUID);

    }

    /* adds a new patient record */
    public void addNewPatient() {
        patients = new ArrayList<>();
        writeToFile = new WriteToFile();
        mainMenu = new MainMenu();

        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Add New Patient");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setLayout(null);

        JLabel firstNameLabel = new JLabel("First Name: ");
        firstNameLabel.setBounds(10, 10, 80, 20);
        panel.add(firstNameLabel);

        JTextField firstNameText = new JTextField();
        firstNameText.setBounds(100, 10, 250, 25);
        panel.add(firstNameText);

        JLabel lastNameLabel = new JLabel("Last Name: ");
        lastNameLabel.setBounds(10, 40, 80, 20);
        panel.add(lastNameLabel);

        JTextField lastNameText = new JTextField();
        lastNameText.setBounds(100, 40, 250, 25);
        panel.add(lastNameText);

        JLabel middleNameLabel = new JLabel("Middle Name: ");
        middleNameLabel.setBounds(10, 70, 80, 20);
        panel.add(middleNameLabel);

        JTextField middleNameText = new JTextField();
        middleNameText.setBounds(100, 70, 250, 25);
        panel.add(middleNameText);

        JLabel birthdayLabel = new JLabel("Birthday: ");
        birthdayLabel.setBounds(10, 100, 80, 20);
        panel.add(birthdayLabel);

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setBounds(100, 100, 250, 25);
        dateChooser.setDateFormatString("yyyyMMdd");
        frame.getContentPane().add(dateChooser);
        panel.add(dateChooser);

        JLabel genderLabel = new JLabel("Gender: ");
        genderLabel.setBounds(10, 130, 80, 20);
        panel.add(genderLabel);

        JRadioButton femaleButton = new JRadioButton();
        femaleButton.setText("Female");
        femaleButton.setBounds(100, 130, 100, 25);
        panel.add(femaleButton);

        JRadioButton maleButton = new JRadioButton();
        maleButton.setText("Male");
        maleButton.setBounds(200, 130, 250, 25);
        panel.add(maleButton);

        ButtonGroup buttonGroup = new ButtonGroup();
        buttonGroup.add(femaleButton);
        buttonGroup.add(maleButton);

        JLabel addressLabel = new JLabel("Address: ");
        addressLabel.setBounds(10, 160, 80, 20);
        panel.add(addressLabel);

        JTextField addressText = new JTextField();
        addressText.setBounds(100, 160, 250, 25);
        panel.add(addressText);

        JLabel phoneNoLabel = new JLabel("Phone No.: ");
        phoneNoLabel.setBounds(10, 190, 80, 20);
        panel.add(phoneNoLabel);

        JTextField phoneNoText = new JTextField();
        phoneNoText.setBounds(100, 190, 250, 25);
        phoneNoText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    long x = Long.parseLong(phoneNoText.getText());
                } catch (NumberFormatException nfe) {
                    String temp = phoneNoText.getText();
                    temp = temp.substring(0, temp.length()-1);
                    phoneNoText.setText(temp);
                }
            }
        });
        panel.add(phoneNoText);

        JLabel nationalIdLabel = new JLabel("National ID No.: ");
        nationalIdLabel.setBounds(10, 220, 100, 20);
        panel.add(nationalIdLabel);

        JTextField nationalIdText = new JTextField();
        nationalIdText.setBounds(100, 220, 250, 25);
        nationalIdText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    long x = Long.parseLong(nationalIdText.getText());
                } catch (NumberFormatException nfe) {
                    String temp = nationalIdText.getText();
                    temp = temp.substring(0, temp.length()-1);
                    nationalIdText.setText(temp);
                }
            }
        });
        panel.add(nationalIdText);

        JLabel saveLabel = new JLabel("Save Patient Record?");
        saveLabel.setBounds(10, 280, 500, 20);
        panel.add(saveLabel);

        JButton yesButton = new JButton("YES");
        yesButton.setBounds(10, 300, 80, 25);
        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                // add input validation
                String patientCodeIdentifier = generateUID();
                String firstName = firstNameText.getText();
                String lastName = lastNameText.getText();
                String middleName = middleNameText.getText();

                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                String birthday = dateFormat.format(dateChooser.getDate());

                String gender = null;
                if (femaleButton.isSelected())
                    gender = "F";
                else if (maleButton.isSelected())
                    gender = "M";
                String address = addressText.getText();
                String phoneNo = phoneNoText.getText();
                long nationalIdNo = Long.parseLong(nationalIdText.getText());

                Patient patient = new Patient(patientCodeIdentifier, lastName, firstName, middleName, birthday, gender, address, phoneNo, nationalIdNo);
                patients.add(patient);

                String filename = "Patients.txt";
                int error = writeToFile.writeToPatients(filename, patient);
                if (error == 1) {
                    frame = new JFrame();
                    panel = new JPanel();

                    frame.setSize(960, 540);
                    frame.setTitle("Error Message");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    panel.setLayout(new BorderLayout());

                    JLabel errorLabel = new JLabel("Error occurred. Please try again.");
                    errorLabel.setBounds(10, 10, 250, 25);
                    errorLabel.setForeground(Color.RED);
                    panel.add(errorLabel);

                    Timer timer = new Timer(5000, null);
                    timer.setRepeats(false);
                    timer.start();

                    frame.dispose();
                    addNewPatient();
                }

                frame.dispose();

                frame = new JFrame();
                panel = new JPanel();

                frame.setSize(960, 540);
                frame.setTitle("Add New Patient");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
                panel.setLayout(boxLayout);
                panel.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));

                JLabel messageDialogue = new JLabel("Patient record successfully added.");
                messageDialogue.setBounds(10, 10, 250, 25);
                messageDialogue.setForeground(Color.BLUE);
                panel.add(messageDialogue);

                JLabel addOrReturnLabel = new JLabel("Would you like to add another patient or return to the main menu?");
                addOrReturnLabel.setBounds(10, 10, 250, 25);
                panel.add(addOrReturnLabel);

                JButton addNewButton = new JButton("Add New Patient");
                addNewButton.setBounds(10, 30, 80, 25);
                addNewButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        addNewPatient();
                    }
                });
                panel.add(addNewButton);

                JButton returnButton = new JButton("Return to the Main Menu");
                returnButton.setBounds(100, 30, 80, 25);
                returnButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        mainMenu.mainMenu();
                    }
                });
                panel.add(returnButton);

                frame.add(panel);
                frame.setVisible(true);
            }
        });
        panel.add(yesButton);

        JButton noButton = new JButton("NO");
        noButton.setBounds(100, 300, 80, 25);
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();

                frame = new JFrame();
                panel = new JPanel();

                frame.setSize(960, 540);
                frame.setTitle("Patient Records");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
                panel.setLayout(boxLayout);
                panel.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));

                JLabel messageDialogue = new JLabel("Patient record not added.");
                messageDialogue.setBounds(10, 10, 250, 25);
                messageDialogue.setForeground(Color.RED);
                panel.add(messageDialogue);

                JLabel addOrReturnLabel = new JLabel("Would you like to add another patient or return to the main menu?");
                addOrReturnLabel.setBounds(10, 50, 250, 25);
                panel.add(addOrReturnLabel);

                JButton addNewButton = new JButton("Add New Patient");
                addNewButton.setBounds(10, 80, 80, 25);
                addNewButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        addNewPatient();
                    }
                });
                panel.add(addNewButton);

                JButton returnButton = new JButton("Return to the Main Menu");
                returnButton.setBounds(100, 80, 80, 25);
                returnButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        mainMenu.mainMenu();
                    }
                });
                panel.add(returnButton);

                frame.add(panel);
                frame.setVisible(true);
            }
        });
        panel.add(noButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    /*
    * searchPatientRecord
    * main search method
    * methodType 0
    * */
    public void searchPatientRecord() {
        readFile = new ReadFile();

        methodType = 0;

        // get all lines in Patients.txt and save to String[][] patients
        String fileName = "Patients.txt";
        int error = readFile.readPatients(fileName);
        if (error==1)
            error();
        patientRecords = readFile.getTempSearch();

        // count total non-null entries in String[][] patients
        for (String[] patient : patientRecords)
            if (patient[0] != null)
                countPatients++;

        // get all lines in services.txt and save to String[][] patients
        fileName = "services.txt";
        error = readFile.readServices(fileName);
        if (error==1)
            error();
        services = readFile.getTempServ();
//        services = sortArray(services);

        // count all services
        for (String[] service : services)
            for (int j = 0; j < services[0].length; j++)
                if (service[0] != null && service[0].length() == 3) {
                    countServices++;
                    break;
                }

        searchUID();
    }

    /*
    * searchPatientRecord
    * displays patient records
    * */
    public void displayPatient() {
        mainMenu = new MainMenu();

        displayFrame = new JFrame();
        displayPanel = new JPanel();

        displayFrame.setSize(960, 540);
        displayFrame.setTitle("Search Patient Records");
        displayFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        displayFrame.setLocationRelativeTo(null);
        displayFrame.add(displayPanel);

        displayPanel.setLayout(null);

        String patientUID = patientRecords[line][0];

        JLabel UIDLabel = new JLabel("Patient's UID: ");
        UIDLabel.setBounds(10, 10, 95, 20);
        displayPanel.add(UIDLabel);

        JLabel UIDInfo = new JLabel(patientRecords[line][0]);
        UIDInfo.setBounds(105, 10, 250, 20);
        displayPanel.add(UIDInfo);

        JLabel nameLabel = new JLabel("Name: ");
        nameLabel.setBounds(10, 30, 95, 20);
        displayPanel.add(nameLabel);

        JLabel nameInfo = new JLabel(patientRecords[line][1] + ", " + patientRecords[line][2] + " " + patientRecords[line][3]);
        nameInfo.setBounds(105, 30, 250, 20);
        displayPanel.add(nameInfo);

        JLabel birthdayLabel = new JLabel("Birthday: ");
        birthdayLabel.setBounds(10, 50, 95, 20);
        displayPanel.add(birthdayLabel);

        JLabel birthdayInfo = new JLabel(patientRecords[line][4]);
        birthdayInfo.setBounds(105, 50, 250, 20);
        displayPanel.add(birthdayInfo);

        JLabel addressLabel = new JLabel("Address: ");
        addressLabel.setBounds(10, 70, 95, 20);
        displayPanel.add(addressLabel);

        JLabel addressInfo = new JLabel(patientRecords[line][6]);
        addressInfo.setBounds(105, 70, 250, 20);
        displayPanel.add(addressInfo);

        JLabel phoneNoLabel = new JLabel("Phone Number: ");
        phoneNoLabel.setBounds(10, 90, 95, 20);
        displayPanel.add(phoneNoLabel);

        JLabel phoneNoInfo = new JLabel(patientRecords[line][7]);
        phoneNoInfo.setBounds(105, 90, 250, 20);
        displayPanel.add(phoneNoInfo);

        JLabel nationalIDNoLabel = new JLabel("National ID no.: ");
        nationalIDNoLabel.setBounds(10, 110, 95, 20);
        displayPanel.add(nationalIDNoLabel);

        JLabel nationalIDNoInfo = new JLabel(patientRecords[line][8]);
        nationalIDNoInfo.setBounds(105, 110, 250, 20);
        displayPanel.add(nationalIDNoInfo);

        String code;

        JLabel requestUIDLabel = new JLabel("Request's UID");
        requestUIDLabel.setBounds(10, 150, 100, 20);
        displayPanel.add(requestUIDLabel);

        JLabel labTestTypeLabel = new JLabel("Lab Test Type");
        labTestTypeLabel.setBounds(200, 150, 100, 20);
        displayPanel.add(labTestTypeLabel);

        JLabel requestDateLabel = new JLabel("Request Date");
        requestDateLabel.setBounds(390, 150, 100, 20);
        displayPanel.add(requestDateLabel);

        JLabel requestLabel = new JLabel("Result");
        requestLabel.setBounds(580, 150, 100, 20);
        displayPanel.add(requestLabel);

        int y = 150;
        for(int i=0; i<countServices; i++) {
            code = services[i][0];
            String description = services[i][1];

            String fileName = code + "_Requests.txt";
            int error = readFile.readRequests(fileName);
            if (error == 1)
                error();
            String[][] requests = readFile.getTempReq();
//            requests = sortDate(requests);

            int length = 0;
            for (String[] request : requests)
                for (int j = 0; j < services[0].length; j++)
                    if (request[0] != null && request[0].length() == 15) {
                        length++;
                        break;
                    }

            File file = new File(fileName);
            boolean exists = file.exists();

            for (int j = length - 1; j >= 0; j--) {
                if (patientUID.equals(requests[j][1]) && exists) {
                    y+=20;
                    JLabel requestUID = new JLabel(requests[j][0]);
                    requestUID.setBounds(10, y, 170, 20);
                    displayPanel.add(requestUID);

                    JLabel labTestType = new JLabel(description);
                    labTestType.setBounds(200, y, 170, 20);
                    displayPanel.add(labTestType);

                    JLabel requestDate = new JLabel(requests[j][2]);
                    requestDate.setBounds(390, y, 170, 20);
                    displayPanel.add(requestDate);

                    JLabel results = new JLabel(requests[j][4]);
                    results.setBounds(580, y, 170, 20);
                    displayPanel.add(results);
                }
            }
            Arrays.stream(requests).forEach(x -> Arrays.fill(x, null));
        }
        JLabel buffer = new JLabel("");
        buffer.setBounds(10, y+30, 100, 20);
        displayPanel.add(buffer);

        displayFrame.setVisible(true);

        printResultsDialogue();
    }

    /*
    * searchPatientRecord
    * asks user if they want to print labs
    * */
    public void printResultsDialogue() {
        JFrame popUpFrame = new JFrame();
        JPanel popUpPanel = new JPanel();

        popUpFrame.setSize(960, 540);
        popUpFrame.setTitle("Print Laboratory Request");
        popUpFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BoxLayout boxLayout = new BoxLayout(popUpPanel, BoxLayout.Y_AXIS);
        popUpPanel.setLayout(boxLayout);
        popUpPanel.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));
        popUpFrame.add(popUpPanel);

        JLabel printDialogue = new JLabel("Do you want to print a laboratory test result?");
        printDialogue.setBounds(10, 10, 500, 20);
        popUpPanel.add(printDialogue);

        JButton yesButton = new JButton("YES");
        yesButton.setBounds(10, 40, 80, 25);
        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                popUpFrame.dispose();
                printResults();
            }
        });
        popUpPanel.add(yesButton);

        JButton noButton = new JButton("NO");
        noButton.setBounds(110, 40, 80, 25);
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                displayFrame.dispose();
                popUpFrame.dispose();
                mainMenu.mainMenu();
            }
        });
        popUpPanel.add(noButton);

        popUpFrame.pack();
        popUpFrame.setVisible(true);
    }

    /*
    * searchPatientRecord
    * prints labs
    * */
    public void printResults() {
        manageLaboratoryRequest = new ManageLaboratoryRequest();

        String[] ret = manageLaboratoryRequest.searchInput(1);
//        displayFrame.dispose();

        String name = patientRecords[line][1] + ", " + patientRecords[line][2] + " " + patientRecords[line][3];
        String sUID = ret[0];
        String pUID = patientRecords[line][0];
        String birthday = patientRecords[line][4];
        String gender = patientRecords[line][5];
        String phoneNo = patientRecords[line][7];
        String test = ret[3];
        String result = ret[2];
        String rDate = ret[1];

        // calculating age
        int birthYear = Integer.parseInt(birthday.substring(0, 4));
        int birthMonth = Integer.parseInt(birthday.substring(4, 6));
        int birthDay = Integer.parseInt(birthday.substring(birthday.length()-2));
        LocalDate start = LocalDate.of(birthYear, birthMonth, birthDay); // use for age-calculation: LocalDate.now()
        LocalDate end = LocalDate.now(ZoneId.systemDefault());
        int age = (int) ChronoUnit.YEARS.between(start, end);

        //print pdf
        String pdfName = patientRecords[line][1] + "_" + sUID + "_" + rDate + ".pdf";
        try {
            PrintLabResults.printPdf(pdfName, name, sUID, pUID, rDate, birthday, gender, phoneNo, age, test, result);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /*
    * deletePatientRecord
    * main delete method
    * methodType 1
    * */
    public void deletePatientRecord() {
        mainMenu = new MainMenu();
        readFile = new ReadFile();

        methodType = 1;

        // get all lines in Patients.txt and save to String[][] patients
        String fileName = "Patients.txt";
        int error = readFile.readPatients(fileName);
        if (error==1)
            error();
        patientRecords = readFile.getTempSearch();

        // count total non-null entries in String[][] patients
        for (String[] patient : patientRecords)
            if (patient[0] != null)
                countPatients++;

        searchUID();
    }

    /*
    * deletePatientRecord
    * accepts input for reason to delete
    * */
    public void deleteInput() {
        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Delete A Patient Record");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        JLabel reasonLabel = new JLabel("Please state reason for deletion: ");
        reasonLabel.setBounds(10, 10, 200, 20);
        panel.add(reasonLabel);

        JTextField reasonText = new JTextField();
        reasonText.setBounds(210, 10, 250, 25);
        panel.add(reasonText);

        JButton deleteButton = new JButton("DELETE");
        deleteButton.setBounds(10, 40, 100, 25);
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteReason = reasonText.getText();
                frame.dispose();
                delete();
            }
        });
        panel.add(deleteButton);

        frame.setVisible(true);
    }

    /*
    * deletePatientRecord
    * deletes patient record from text file
    * */
    public void delete() {
        String D = "D;";
        String newLine = String.join("", D, deleteReason);
        String fileName = "Patients.txt";

        try {
            File file = new File(fileName);
            Scanner scannerFile = new Scanner(file);

            String tempLine = Files.readAllLines(Paths.get(fileName)).get(line);
            StringBuilder buffer = new StringBuilder();
            while(scannerFile.hasNextLine()) {
                buffer.append(scannerFile.nextLine()).append(System.lineSeparator());
            }
            String fileContents = buffer.toString();
            scannerFile.close();

            String line1 = String.join("",tempLine,newLine,";");
            fileContents = fileContents.replaceAll(tempLine,line1);
            FileWriter fw = new FileWriter(fileName);
            fw.append(fileContents);
            fw.flush();

            String[] splitLine = tempLine.split(";");
            String UID = splitLine[0];

            successDialogue = "Data of patient " + UID + " has been deleted.";
            confirmation();
        } catch(IOException e) {
            error();
        }
    }

    /*
     * editPatientRecord
     * main edit method
     * methodType 2
     * */
    public void editPatientRecord() {
        mainMenu = new MainMenu();
        readFile = new ReadFile();

        methodType = 2;

        // get all lines in Patients.txt and save to String[][] patients
        String fileName = "Patients.txt";
        int error = readFile.readPatients(fileName);
        if (error==1)
            error();
        patientRecords = readFile.getTempSearch();

        // count total non-null entries in String[][] patients
        for (String[] patient : patientRecords)
            if (patient[0] != null)
                countPatients++;

        searchUID();
    }

    /*
     * editPatientRecord
     * asks user what they want to edit; address or phone number
     * */
    public void editOption() {
        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Edit Patient Record");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        panel.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));

        JLabel updateLabel = new JLabel("Would you like to update the patient's Address or Phone Number?");
        updateLabel.setBounds(10, 10, 500, 20);
        panel.add(updateLabel);

        JButton addressButton = new JButton("Update Address");
        addressButton.setBounds(10, 10, 100, 25);
        addressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scan = 1;
                frame.dispose();
                editInput();
            }
        });
        panel.add(addressButton);

        JButton phoneNoButton = new JButton("Update Phone Number");
        phoneNoButton.setBounds(10, 10, 100, 25);
        phoneNoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scan = 2;
                frame.dispose();
                editInput();
            }
        });
        panel.add(phoneNoButton);

        frame.setVisible(true);
    }

    /*
     * editPatientRecord
     * accepts new value for address or phone number
     * */
    public void editInput() {
        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Edit Patient Record");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        switch (scan) {
            case 1 -> {
                JLabel newAddressLabel = new JLabel("Enter the patient's new Address: ");
                newAddressLabel.setBounds(10, 10, 200, 20);
                panel.add(newAddressLabel);

                JTextField newAddress = new JTextField();
                newAddress.setBounds(210, 10, 250, 25);
                panel.add(newAddress);

                JButton updateButton = new JButton("UPDATE");
                updateButton.setBounds(10, 40, 80, 25);
                updateButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        editUpdate = newAddress.getText();
                        frame.dispose();
                        edit();
                    }
                });
                panel.add(updateButton);
            }
            case 2 -> {
                JLabel newPhoneNoLabel = new JLabel("Enter the patient's new Phone number: ");
                newPhoneNoLabel.setBounds(10, 10, 230, 20);
                panel.add(newPhoneNoLabel);

                JTextField newPhoneNo = new JTextField();
                newPhoneNo.setBounds(240, 10, 250, 25);
                panel.add(newPhoneNo);

                JButton updateButton = new JButton("UPDATE");
                updateButton.setBounds(10, 40, 120, 20);
                updateButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        editUpdate = newPhoneNo.getText();
                        frame.dispose();
                        edit();
                    }
                });
                panel.add(updateButton);
            }
        }
        frame.setVisible(true);
    }

    /*
    * editPatientRecord
    * edits patient record from text file
    * */
    public void edit() {
        String fileName = "Patients.txt";

        try {
            File file = new File(fileName);
            Scanner scannerFile = new Scanner(file);

            String tempLine = Files.readAllLines(Paths.get(fileName)).get(line);
            StringBuilder buffer = new StringBuilder();
            while(scannerFile.hasNextLine()) {
                buffer.append(scannerFile.nextLine()).append(System.lineSeparator());
            }
            String fileContents = buffer.toString();
            scannerFile.close();

            String[] splitLine = tempLine.split(";");
            if (scan == 1)
                splitLine[6] = editUpdate;
            else if (scan == 2)
                splitLine[7] = editUpdate;

            String line1 = String.join(";", splitLine);
            line1 = String.join("", line1, ";");

            fileContents = fileContents.replaceAll(tempLine,line1);
            FileWriter fw = new FileWriter(fileName);
            fw.append(fileContents);
            fw.flush();

            String UID = splitLine[0];
            successDialogue = "The Address/Phone Number of patient " + UID + " has been updated.";
            confirmation();
        } catch (IOException e) {
            error();
        }
    }

    /*
    * asks user if they know the patient's UID
    * */
    public void searchUID() {
        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Searching Patient Records...");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        JLabel patientUIDLabel = new JLabel("Do you know the patient's UID?");
        patientUIDLabel.setBounds(10, 10, 500, 20);
        panel.add(patientUIDLabel);

        JButton yesUIDButton = new JButton("YES");
        yesUIDButton.setBounds(10, 40, 80, 25);
        yesUIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                scan = 1;
                searchInput();
            }
        });
        panel.add(yesUIDButton);

        JButton noUIDButton = new JButton("NO");
        noUIDButton.setBounds(100, 40, 80, 25);
        noUIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                scan = 3;
                searchID();
            }
        });
        panel.add(noUIDButton);

        frame.setVisible(true);
    }

    /*
     * asks user if they know the patient's national ID no.
     * */
    public void searchID() {
        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Searching Patient Records...");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        JLabel patientUIDLabel = new JLabel("Do you know the patient's National ID no.?");
        patientUIDLabel.setBounds(10, 10, 500, 20);
        panel.add(patientUIDLabel);

        JButton yesUIDButton = new JButton("YES");
        yesUIDButton.setBounds(10, 40, 80, 25);
        yesUIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                scan = 2;
                searchInput();
            }
        });
        panel.add(yesUIDButton);

        JButton noUIDButton = new JButton("NO");
        noUIDButton.setBounds(100, 40, 80, 25);
        noUIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                scan = 3;
                searchInput();
            }
        });
        panel.add(noUIDButton);

        frame.setVisible(true);
    }

    /*
     *  accepts user input for search key/s
     * */
    public void searchInput() {
        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Searching Patient Records...");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.add(panel);

        panel.setLayout(null);

        switch (scan) {
            case 1 -> {
                JLabel UIDLabel = new JLabel("Enter patient's UID: ");
                UIDLabel.setBounds(10, 10, 120, 20);
                panel.add(UIDLabel);

                JTextField UIDText = new JTextField();
                UIDText.setBounds(140, 10, 250, 25);
                panel.add(UIDText);

                JButton searchButton = new JButton("SEARCH");
                searchButton.setBounds(10, 40, 100, 25);
                searchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        patientUID = UIDText.getText().toUpperCase();
                        frame.dispose();
                        searchRecord();
                    }
                });
                panel.add(searchButton);
            }
            case 2 -> {
                JLabel IDLabel = new JLabel("Enter patient's National ID no.: ");
                IDLabel.setBounds(10, 10, 180, 20);
                panel.add(IDLabel);

                JTextField IDText = new JTextField();
                IDText.setBounds(200, 10, 250, 25);
                panel.add(IDText);

                JButton searchButton = new JButton("SEARCH");
                searchButton.setBounds(10, 40, 100, 25);
                searchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        nationalIDNo = IDText.getText();
                        frame.dispose();
                        searchRecord();
                    }
                });
                panel.add(searchButton);
            }
            case 3 -> {
                JLabel lastNameLabel = new JLabel("Enter patient's Last name: ");
                lastNameLabel.setBounds(10, 10, 180, 20);
                panel.add(lastNameLabel);

                JTextField lastNameText = new JTextField();
                lastNameText.setBounds(200, 10, 250, 25);
                panel.add(lastNameText);

                JLabel firstNameLabel = new JLabel("Enter patient's First name: ");
                firstNameLabel.setBounds(10, 40, 180, 20);
                panel.add(firstNameLabel);

                JTextField firstNameText = new JTextField();
                firstNameText.setBounds(200, 40, 250, 25);
                panel.add(firstNameText);

                JLabel birthdayLabel = new JLabel("Enter patient's Birthday: ");
                birthdayLabel.setBounds(10, 70, 180, 20);
                panel.add(birthdayLabel);

                JDateChooser dateChooser = new JDateChooser();
                dateChooser.setBounds(200, 70, 250, 25);
                dateChooser.setDateFormatString("yyyyMMdd");
                frame.getContentPane().add(dateChooser);
                panel.add(dateChooser);

                JButton searchButton = new JButton("SEARCH");
                searchButton.setBounds(10, 100, 100, 25);
                searchButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        lastName = lastNameText.getText();
                        firstName = firstNameText.getText();

                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                        birthday = dateFormat.format(dateChooser.getDate());

                        frame.dispose();
                        searchRecord();
                    }
                });
                panel.add(searchButton);
            }
        }
        frame.add(panel);
        frame.setVisible(true);
    }

    /*
     * searches for patient record
     * */
    public void searchRecord() {
        switch (scan) {
            case 1 -> {
                for (int i = 0; i < countPatients; i++)
                    if (!Objects.equals(patientRecords[i][0], null)) {
                        if(patientRecords[i][0].equalsIgnoreCase(patientUID) && !patientRecords[i][9].equalsIgnoreCase("D")) {
                            searched = 1;
                            lines[0] = i;
                            search();
                        }
                    }
            }
            case 2 -> {
                for (int i = 0; i < countPatients; i++)
                    if (!Objects.equals(patientRecords[i][0], null)) {
                        if (patientRecords[i][8].equalsIgnoreCase(nationalIDNo) && !patientRecords[i][9].equalsIgnoreCase("D")) {
                            searched = 1;
                            lines[0] = i;
                            search();
                        }
                    }
            }
            case 3 -> {
                for (int i = 0; i < countPatients; i++) {
                    try {
                        if (!Objects.equals(patientRecords[i][0], null)) {
                            if (!patientRecords[i][9].equals("D") && patientRecords[i][1].equalsIgnoreCase(lastName) && patientRecords[i][2].equalsIgnoreCase(firstName) && patientRecords[i][4].equalsIgnoreCase(birthday)) {
                                lines[searched] = i;
                                searched++;
                            }
                        }
                    } catch (NullPointerException ignored) {}
                }
                search();
            }
        }
        if (searched == 0) {
            noRecordFound = 1;
            error();
        }
    }

    /*
     * checks if there were patients found
     * displays records there are multiple records found
     * */
    public void search() {
        if (searched==0) {
            noRecordFound = 1;
            error();
        } else if (searched>1) {
            frame = new JFrame();
            panel = new JPanel();

            frame.setSize(1080, 540);
            frame.setTitle("Searching Patient Records...");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);

            panel.setLayout(null);

            JLabel UIDLabel = new JLabel("Patient's UID");
            UIDLabel.setBounds(10, 10, 100, 20);
            panel.add(UIDLabel);

            JLabel lastNameLabel = new JLabel("Last Name");
            lastNameLabel.setBounds(120, 10, 100, 20);
            panel.add(lastNameLabel);

            JLabel firsNameLabel = new JLabel("First Name");
            firsNameLabel.setBounds(210, 10, 100, 20);
            panel.add(firsNameLabel);

            JLabel middleNameLabel = new JLabel("Middle Name");
            middleNameLabel.setBounds(300, 10, 100, 20);
            panel.add(middleNameLabel);

            JLabel birthdayLabel = new JLabel("Birthday");
            birthdayLabel.setBounds(390, 10, 100, 20);
            panel.add(birthdayLabel);

            JLabel genderLabel = new JLabel("Gender");
            genderLabel.setBounds(480, 10, 100, 20);
            panel.add(genderLabel);

            JLabel addressLabel = new JLabel("Address");
            addressLabel.setBounds(570, 10, 100, 20);
            panel.add(addressLabel);

            JLabel phoneNumberLabel = new JLabel("Phone Number");
            phoneNumberLabel.setBounds(740, 10, 100, 20);
            panel.add(phoneNumberLabel);

            JLabel nationalIDLabel = new JLabel("National ID. No");
            nationalIDLabel.setBounds(850, 10, 100, 20);
            panel.add(nationalIDLabel);

            int y = 10;
            for (int i = 0; i<lines.length; i++) {
                if (patientRecords[i][0] != null)
                    if (patientRecords[i][1].equalsIgnoreCase(lastName) && patientRecords[i][2].equalsIgnoreCase(firstName) && patientRecords[i][4].equalsIgnoreCase(birthday)) {
                        y += 20;

                        JLabel UID = new JLabel(patientRecords[i][0]);
                        UID.setBounds(10, y, 100, 20);
                        panel.add(UID);

                        JLabel lastName = new JLabel(patientRecords[i][1]);
                        lastName.setBounds(120, y, 100, 20);
                        panel.add(lastName);

                        JLabel firstName = new JLabel(patientRecords[i][2]);
                        firstName.setBounds(210, y, 100, 20);
                        panel.add(firstName);

                        JLabel middleName = new JLabel(patientRecords[i][3]);
                        middleName.setBounds(300, y, 100, 20);
                        panel.add(middleName);

                        JLabel birthday = new JLabel(patientRecords[i][4]);
                        birthday.setBounds(390, y, 100, 20);
                        panel.add(birthday);

                        JLabel gender = new JLabel(patientRecords[i][5]);
                        gender.setBounds(480, y, 100, 20);
                        panel.add(gender);

                        JLabel address = new JLabel(patientRecords[i][6]);
                        address.setBounds(570, y, 100, 20);
                        panel.add(address);

                        JLabel phoneNumber = new JLabel(patientRecords[i][7]);
                        phoneNumber.setBounds(740, y, 100, 20);
                        panel.add(phoneNumber);

                        JLabel nationalID = new JLabel(patientRecords[i][8]);
                        nationalID.setBounds(850, y, 100, 20);
                        panel.add(nationalID);
                    }
            }
            y += 30;
            JLabel enterUID = new JLabel("Enter patient's UID: ");
            enterUID.setBounds(10, y, 120, 20);
            panel.add(enterUID);

            JTextField UIDText = new JTextField();
            UIDText.setBounds(140, y, 250, 25);
            panel.add(UIDText);

            JButton enterButton = new JButton("ENTER");
            enterButton.setBounds(10, y+30, 100, 25);
            enterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    finalUID = UIDText.getText();
                    for(int i=0; i< patientRecords.length; i++)
                        if (!Objects.equals(patientRecords[i][0], null)) {
                            if (patientRecords[i][0].equalsIgnoreCase(finalUID)) {
                                line = i;
                                searched = 1;
                                break;
                            }
                        }
                    frame.dispose();
                    searchVerify();
                }
            });
            panel.add(enterButton);
            frame.setVisible(true);

        } else {
            line = lines[0];
            if (methodType == 0)
                displayPatient();
            else if (methodType == 1)
                deleteInput();
            else if (methodType == 2)
                editOption();
        }
    }

    /*
    * calls respective methods after search()
    * */
    public void searchVerify() {
        if (searched!=1) {
            noRecordFound = 1;
            error();
        } else {
            if (methodType == 0)
                displayPatient();
            else if (methodType == 1)
                deleteInput();
            else if (methodType == 2)
                editOption();
        }
    }

    /* message dialogue for successful transaction */
    public void confirmation() {
        mainMenu = new MainMenu();

        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Transaction Successful");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        panel.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));

        JLabel dialogue = new JLabel(successDialogue);
        dialogue.setBounds(10, 10, 150, 20);
        dialogue.setForeground(Color.BLUE);
        panel.add(dialogue);

        if (methodType == 0) {
            JLabel decisionDialogue = new JLabel("Do you want to search for another patient record or return to the Main Menu?");
            decisionDialogue.setBounds(10, 40, 150, 20);
            panel.add(decisionDialogue);
        } else if (methodType == 1) {
            JLabel decisionDialogue = new JLabel("Do you want to delete another patient record or return to the Main Menu?");
            decisionDialogue.setBounds(10, 40, 150, 20);
            panel.add(decisionDialogue);
        } else if (methodType == 2) {
            JLabel decisionDialogue = new JLabel("Do you want to edit another patient record or return to the Main Menu?");
            decisionDialogue.setBounds(10, 40, 150, 20);
            panel.add(decisionDialogue);
        }

        JButton tryAgainButton = new JButton("Try Again");
        tryAgainButton.setBounds(10, 10, 80, 25);
        tryAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                if (methodType == 0)
                    searchPatientRecord();
                else if (methodType == 1)
                    deletePatientRecord();
                else if (methodType == 2)
                    editPatientRecord();
            }
        });
        panel.add(tryAgainButton);

        JButton returnButton = new JButton("Main Menu");
        returnButton.setBounds(100, 10, 80, 25);
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                mainMenu.mainMenu();
            }
        });
        panel.add(returnButton);

        frame.setVisible(true);
    }

    /* message dialogue for unsuccessful transaction */
    public void error() {
        mainMenu = new MainMenu();

        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Transaction Unsuccessful");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        panel.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));

        JLabel dialogue;
        if (noRecordFound == 1)
            dialogue = new JLabel("No record found.");
        else
            dialogue = new JLabel("An error occurred. Please check if the file exists.");
        dialogue.setBounds(10, 10, 150, 20);
        dialogue.setForeground(Color.RED);
        panel.add(dialogue);

        if (methodType == 0) {
            JLabel decisionDialogue = new JLabel("Would you like to try searching for another patient record again or return to the Main Menu?");
            decisionDialogue.setBounds(10, 40, 150, 20);
            panel.add(decisionDialogue);
        } else if (methodType == 1) {
            JLabel decisionDialogue = new JLabel("Would you like to try deleting another patient record again or return to the Main Menu?");
            decisionDialogue.setBounds(10, 40, 150, 20);
            panel.add(decisionDialogue);
        } else if (methodType == 2) {
            JLabel decisionDialogue = new JLabel("Would you like to try editing another patient record again or return to the Main Menu?");
            decisionDialogue.setBounds(10, 40, 150, 20);
            panel.add(decisionDialogue);
        }

        JButton tryAgainButton = new JButton("Try Again");
        tryAgainButton.setBounds(10, 10, 80, 25);
        tryAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                if (methodType == 0)
                    searchPatientRecord();
                else if (methodType == 1)
                    deletePatientRecord();
                else if (methodType == 2)
                    editPatientRecord();
            }
        });
        panel.add(tryAgainButton);

        JButton returnButton = new JButton("Main Menu");
        returnButton.setBounds(100, 10, 80, 25);
        returnButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                mainMenu.mainMenu();
            }
        });
        panel.add(returnButton);

        frame.setVisible(true);
    }

    /* sorts array by UID */
    public static String[][] sortArray(String[][] data) {
        int nonNull = 0;
        for(int i = 0; i < data[0].length; i++) {
            if(data[0][i] != null) {
                nonNull++;
            }
        }

        int counter = 0;
        String[][] newData = new String[nonNull][];
        for(int i = 0; i < data[0].length; i++) {
            if(data[0][i] != null) {
                newData[counter] = data[i];
                counter++;
            }
        }

        Arrays.sort(newData, (entry1, entry2) -> {
            final String time1 = entry1[0];
            final String time2 = entry2[0];


            return time1.compareTo(time2);

        });
        return newData;
    }

    /* sorts array by date */
    public static String[][] sortDate(String[][] data) {
        int nonNull = 0;
        for(int i = 0; i < data[0].length; i++) {
            if(data[i][2] != null) {
                nonNull++;
            }
        }

        int counter = 0;
        String[][] newData = new String[nonNull][];
        for(int i = 0; i < data[0].length; i++) {
            if(data[i][2] != null) {
                newData[counter] = data[i];
                counter++;
            }
        }

        Arrays.sort(newData, (entry1, entry2) -> {
            final String time1 = entry1[0];
            final String time2 = entry2[0];

            return time1.compareTo(time2);

        });
        return newData;
    }
}