/*
 * Class: ManageLaboratoryRequest
 * Manages Laboratory Requests such as
 *   adding a new laboratory request,
 *   searching for a laboratory request,
 *   deleting a laboratory request,
 *   and editing a laboratory request.
 * */

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ManageLaboratoryRequest implements ActionListener {
    private ArrayList<Request> requests;

    private WriteToFile writeToFile;
    private MainMenu mainMenu;
    private ReadFile readFile;

    private JFrame frame;
    private JPanel panel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton searchButton;
    private JButton returnButton;

    private int scan = 0;
    private int line = -1;
    private int exists = 0;
    private int searched = 0;
    private int methodType = -1;
    private int noRecordFound = 0;
    private String successDialogue;
    private String messageDialogue;

    private String editUpdate;
    private String requestUID;
    private String patientUID;
    private String deleteReason;
    private String finalFileName;
    private String finalRequestUID;
    private String[] display = new String[4];
    private String[][] savedRequests = new String[256][4];

    private int countRequests;
    private int countServices;
    private String[][] labRecords;
    private String[][] serviceRecords;

    private final String FILE_NAME = "_Requests.txt";
    private String[] data = new String[4];

    /* main menu */
    public void manageLaboratoryRequest() {
        mainMenu = new MainMenu();
        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Laboratory Request Records");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
        panel.setLayout(boxLayout);
        panel.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));

        JLabel selectLabel = new JLabel("Select a transaction: ");
        selectLabel.setBounds(10, 10, 250, 25);
        panel.add(selectLabel);

        addButton = new JButton("Add New Laboratory Request");
        addButton.addActionListener(this);
        addButton.setBounds(10, 10, 500, 25);
        panel.add(addButton);

        searchButton = new JButton("Search Laboratory Request");
        searchButton.addActionListener(this);
        searchButton.setBounds(10, 30, 500, 25);
        panel.add(searchButton);

        deleteButton = new JButton("Delete Laboratory Request");
        deleteButton.addActionListener(this);
        deleteButton.setBounds(10, 50, 500, 25);
        panel.add(deleteButton);

        editButton = new JButton("Edit Laboratory Request");
        editButton.addActionListener(this);
        editButton.setBounds(10, 70, 500, 25);
        panel.add(editButton);

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
            addNewLaboratoryRequest();
        else if (e.getSource() == searchButton)
            searchLaboratoryRequest();
        else if (e.getSource() == deleteButton)
            deleteLaboratoryRequest();
        else if (e.getSource() == editButton)
            editLaboratoryRequest();
        else if (e.getSource() == returnButton)
            mainMenu.mainMenu();
    }

    /* generates Request UID */
    public String generateUID(String code) {
        readFile = new ReadFile();

        String[] tempUID = new String[8];

        int Y = Calendar.getInstance().get(Calendar.YEAR);
        String temp = String.valueOf(Y);
        char[] cTemp = new char[temp.length()];
        for (int i = 0; i < temp.length(); i++)
            cTemp[i] = temp.charAt(i);
        for (int i = 0; i < temp.length(); i++)
            tempUID[i] = String.valueOf(cTemp[i]);

        int M = Calendar.getInstance().get(Calendar.MONTH)+1;
        temp = String.valueOf(M);
        for (int i = 0; i < temp.length(); i++)
            cTemp[i] = temp.charAt(i);
        for (int i = 0; i < temp.length(); i++) {
            if (M > 9)
                tempUID[i + 4] = String.valueOf(cTemp[i]);
            else {
                tempUID[4] = "0";
                tempUID[5] = String.valueOf(cTemp[i]);
            }
        }

        int D = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        temp = String.valueOf(D);
        for (int i = 0; i < temp.length(); i++)
            cTemp[i] = temp.charAt(i);
        for (int i = 0; i < temp.length(); i++) {
            if (D > 9)
                tempUID[i + 6] = String.valueOf(cTemp[i]);
            else {
                tempUID[6] = "0";
                tempUID[7] = String.valueOf(cTemp[i]);
            }
        }

        //GET REQUESTS FROM <CODE>_REQUESTS.TXT
        String fileName = code + FILE_NAME;
        int isFirst = readFile.readUID(fileName);
        String prevUID = readFile.getUID();

        String A;
        String B;
        String newUID;

        //CHECK IF FIRST UID TO BE GENERATED
        if(isFirst==1){
            newUID = "AA00";
            String str = String.join("", tempUID);
            return String.join("", code, str, newUID);
        } else {
            A = prevUID.substring(11, 13);
            B = prevUID.substring(prevUID.length()-2);
        }

        //CHECK AA - AA/ZZ and BB - 00/99
        int num = Integer.parseInt(B);
        char strTemp;
        int numTemp;
        if(num==99) {
            if (A.charAt(1)!='Z' && A.charAt(1)<='Z') {
                strTemp = A.charAt(1);
                strTemp++;
                A = String.join("", "A", String.valueOf(strTemp));
            } else {
                strTemp = A.charAt(0);
                strTemp++;
                A = String.join("", String.valueOf(strTemp), "Z");
            }
        } else {
            numTemp = num;
            if(numTemp<9) {
                numTemp++;
                strTemp = '0';
                B = String.join("", String.valueOf(strTemp), String.valueOf(numTemp));
            } else {
                numTemp++;
                B = String.valueOf(numTemp);
            }
        }
        newUID = String.join("", A, B);
        String str = String.join("", tempUID);
        return String.join("", code, str, newUID);
    }

    /* adds a new laboratory request */
    public void addNewLaboratoryRequest() {
        readFile = new ReadFile();
        mainMenu = new MainMenu();
        writeToFile = new WriteToFile();
        requests = new ArrayList<>();

        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Add New Laboratory Request");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setLayout(null);

        JLabel UIDLabel = new JLabel("Enter patient's UID: ");
        UIDLabel.setBounds(10, 10, 120, 20);
        panel.add(UIDLabel);

        JTextField UIDText = new JTextField();
        UIDText.setBounds(130, 10, 250, 25);
        panel.add(UIDText);

        JLabel codeLabel = new JLabel("Enter service code: ");
        codeLabel.setBounds(10, 40, 120, 20);
        panel.add(codeLabel);

        JTextField codeText = new JTextField();
        codeText.setBounds(130, 40, 250, 25);
        panel.add(codeText);

        JButton addButton = new JButton("ADD");
        addButton.setBounds(10, 70, 80, 25);
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String UID = UIDText.getText().toUpperCase();
                String pFile = "Patients.txt";
                int pExists = readFile.checkUID(pFile, UID);

                String code = codeText.getText().toUpperCase();
                String cFile = "services.txt";
                int cExists = readFile.checkCode(cFile, code);

                if (pExists != 1) {
                    frame.dispose();

                    frame = new JFrame();
                    panel = new JPanel();

                    frame.setSize(960, 540);
                    frame.setTitle("Add New Laboratory Request");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
                    panel.setLayout(boxLayout);
                    panel.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));

                    JLabel messageDialogue = new JLabel("Patient record does not exist.");
                    messageDialogue.setBounds(10, 10, 250, 25);
                    messageDialogue.setForeground(Color.RED);
                    panel.add(messageDialogue);

                    JLabel addOrReturnLabel = new JLabel("Would you like to add another laboratory request or return to the main menu?");
                    addOrReturnLabel.setBounds(10, 10, 250, 25);
                    panel.add(addOrReturnLabel);

                    JButton addNewButton = new JButton("Add New Laboratory Request");
                    addNewButton.setBounds(10, 30, 80, 25);
                    addNewButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.dispose();
                            addNewLaboratoryRequest();
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
                } else if (cExists!=1) {
                    frame.dispose();

                    frame = new JFrame();
                    panel = new JPanel();

                    frame.setSize(960, 540);
                    frame.setTitle("Add New Laboratory Request");
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                    BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
                    panel.setLayout(boxLayout);
                    panel.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));

                    JLabel messageDialogue = new JLabel("Service record does not exist.");
                    messageDialogue.setBounds(10, 10, 250, 25);
                    messageDialogue.setForeground(Color.RED);
                    panel.add(messageDialogue);

                    JLabel addOrReturnLabel = new JLabel("Would you like to add another laboratory request or return to the main menu?");
                    addOrReturnLabel.setBounds(10, 10, 250, 25);
                    panel.add(addOrReturnLabel);

                    JButton addNewButton = new JButton("Add New Laboratory Request");
                    addNewButton.setBounds(10, 30, 80, 25);
                    addNewButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.dispose();
                            addNewLaboratoryRequest();
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
                String requestUID = generateUID(code);

                int year = Calendar.getInstance().get(Calendar.YEAR);
                String month;
                int temp = Calendar.getInstance().get(Calendar.MONTH)+1;
                if(temp<9)
                    month = "0" + temp;
                else
                    month = String.valueOf(temp);
                String date;
                temp = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
                if(temp<9)
                    date = "0" + temp;
                else
                    date = String.valueOf(temp);
                String requestDate = String.join("", String.valueOf(year), month, date);

                int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
                String str1;
                if(hour<9)
                    str1 = "0" + hour;
                else
                    str1 = String.valueOf(hour);
                String str2;
                int minute = Calendar.getInstance().get(Calendar.MINUTE);
                if(minute<9)
                    str2 = "0" + minute;
                else
                    str2 = String.valueOf(minute);
                String requestTime = str1 + str2;

                String result = "XXX";

                Request request = new Request(requestUID, UID, requestDate, requestTime, result);
                requests.add(request);

                String fileName = code + FILE_NAME;
                int error = writeToFile.writeToLabRequests(fileName, request);
                frame.dispose();
                frame = new JFrame();
                panel = new JPanel();

                frame.setSize(960, 540);
                frame.setTitle("Error Message");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

                BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
                panel.setLayout(boxLayout);
                panel.setBorder(new EmptyBorder(new Insets(10, 10, 100, 10)));
                if(error==1) {
                    JLabel errorLabel = new JLabel("An error occurred. Please try again.");
                    errorLabel.setBounds(10, 10, 250, 20);
                    errorLabel.setForeground(Color.RED);
                    panel.add(errorLabel);

                    frame.dispose();
                    addNewLaboratoryRequest();
                } else {
                    String dialogue = "Laboratory Request " + requestUID + " has been added to file " + fileName;
                    JLabel dialogueLabel = new JLabel(dialogue);
                    dialogueLabel.setBounds(10, 10, 250, 20);
                    dialogueLabel.setForeground(Color.BLUE);
                    panel.add(dialogueLabel);

                    JLabel addOrReturnLabel = new JLabel("Would you like to add another laboratory request or return to the main menu?");
                    addOrReturnLabel.setBounds(10, 10, 250, 25);
                    panel.add(addOrReturnLabel);

                    JButton addNewButton = new JButton("Add New Laboratory Request");
                    addNewButton.setBounds(10, 30, 80, 25);
                    addNewButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            frame.dispose();
                            addNewLaboratoryRequest();
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
                }
                frame.add(panel);
                frame.setVisible(true);

            }
        });
        panel.add(addButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    /*
    * searchLaboratoryRequest
    * main search method
    * */
    public void searchLaboratoryRequest() {
        mainMenu = new MainMenu();

        methodType = 0;
        searchGUI();
    }

    /*
     * searchLaboratoryRequest
     * displays laboratory request
     * */
    public void displayLaboratoryRequest() {
        mainMenu = new MainMenu();

        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Search Laboratory Request");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel.setLayout(null);

        JLabel UIDLabel = new JLabel("Request's UID");
        UIDLabel.setBounds(10, 10, 100, 20);
        panel.add(UIDLabel);

        JLabel labTestTypeLabel = new JLabel("Lab Test Type");
        labTestTypeLabel.setBounds(200, 10, 100, 20);
        panel.add(labTestTypeLabel);

        JLabel requestDateLabel = new JLabel("Request Date");
        requestDateLabel.setBounds(390, 10, 100, 20);
        panel.add(requestDateLabel);

        JLabel resultLabel = new JLabel("Result");
        resultLabel.setBounds(580, 10, 100, 20);
        panel.add(resultLabel);

        JLabel UID = new JLabel(display[0]);
        UID.setBounds(10, 30, 170, 20);
        panel.add(UID);

        JLabel labTestType = new JLabel(display[1]);
        labTestType.setBounds(200, 30, 170, 20);
        panel.add(labTestType);

        JLabel requestDate = new JLabel(display[2]);
        requestDate.setBounds(390, 30, 170, 20);
        panel.add(requestDate);

        JLabel result = new JLabel(display[3]);
        result.setBounds(580, 30, 170, 20);
        panel.add(result);

        JButton doneButton = new JButton("DONE");
        doneButton.setBounds(10, 70, 80, 25);
        doneButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                successDialogue = null;
                confirmation();
            }
        });
        panel.add(doneButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    /*
     * deleteLaboratoryRequest
     * main delete method
     * */
    public void deleteLaboratoryRequest() {
        mainMenu = new MainMenu();

        methodType = 1;

        searchGUI();
    }

    /*
     * deleteLaboratoryRequest
     * accepts input for reason to delete
     * */
    public void deleteInput() {
        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Delete A Laboratory Request");
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
     * deleteLaboratoryRequest
     * deletes patient record from text file
     * */
    public void delete() {
        String D = "D;";
        String newLine = String.join("", D, deleteReason);

        try {
            File file = new File(finalFileName);
            Scanner scannerFile = new Scanner(file);

            String tempLine = Files.readAllLines(Paths.get(finalFileName)).get(line);
            StringBuilder buffer = new StringBuilder();
            while(scannerFile.hasNextLine()) {
                buffer.append(scannerFile.nextLine()).append(System.lineSeparator());
            }
            String fileContents = buffer.toString();
            scannerFile.close();

            String line1 = String.join("",tempLine,newLine,";");
            fileContents = fileContents.replaceAll(tempLine,line1);
            FileWriter fw = new FileWriter(finalFileName);
            fw.append(fileContents);
            fw.flush();

            String[] splitLine = tempLine.split(";");
            String UID = splitLine[0];

            successDialogue = UID + " has been deleted.";
            confirmation();
        } catch(IOException e) {
            error();
        }
    }

    /*
     * editLaboratoryRequest
     * main delete method
     * */
    public void editLaboratoryRequest() {
        readFile = new ReadFile();
        mainMenu = new MainMenu();

        methodType = 2;

        searchGUI();
    }

    /*
    * editLaboratoryRequest
    * checks if the laboratory request already has a result
    * */
    public void editVerify() {
        readFile = new ReadFile();

        readFile.readRequests(finalFileName);
        labRecords = readFile.getTempReq();

        if (!labRecords[line][4].equals("XXX")) {
            exists = 1;
            messageDialogue = "Laboratory Request " + labRecords[line][0] + " already has results.";
            error();
        } else
            editInput();
    }

    /*
     * editLaboratoryRequest
     * accepts result input
     * */
    public void editInput() {
        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Edit Laboratory Request");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        JLabel newResultsLabel = new JLabel("Enter the laboratory result: ");
        newResultsLabel.setBounds(10, 10, 200, 20);
        panel.add(newResultsLabel);

        JTextField newResults = new JTextField();
        newResults.setBounds(210, 10, 250, 25);
        panel.add(newResults);

        JButton updateButton = new JButton("UPDATE");
        updateButton.setBounds(10, 40, 80, 25);
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUpdate = newResults.getText();
                frame.dispose();
                edit();
            }
        });
        panel.add(updateButton);

        frame.setVisible(true);
    }

    /*
     * editLaboratoryRequest
     * edits patient record from text file
     * */
    public void edit() {
        try {
            File file = new File(finalFileName);
            Scanner scannerFile = new Scanner(file);

            String tempLine = Files.readAllLines(Paths.get(finalFileName)).get(line);
            StringBuilder buffer = new StringBuilder();
            while(scannerFile.hasNextLine()) {
                buffer.append(scannerFile.nextLine()).append(System.lineSeparator());
            }
            String fileContents = buffer.toString();
            scannerFile.close();

            String[] splitLine = tempLine.split(";");
            splitLine[4] = editUpdate;

            String line1 = String.join(";", splitLine);
            line1 = String.join("", line1, ";");

            fileContents = fileContents.replaceAll(tempLine,line1);
            FileWriter fw = new FileWriter(finalFileName);
            fw.append(fileContents);
            fw.flush();

            String UID = splitLine[0];
            successDialogue = "The Laboratory Request " + UID + " has been updated.";
            confirmation();
        } catch (IOException e) {
            error();
        }
    }

    /*
     * asks user if they know the request's UID
     * */
    public void searchGUI() {
        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Searching Laboratory Records...");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        JLabel requestUIDLabel = new JLabel("Do you know the request's UID?");
        requestUIDLabel.setBounds(10, 10, 500, 20);
        panel.add(requestUIDLabel);

        JButton yesButton = new JButton("YES");
        yesButton.setBounds(10, 40, 80, 25);
        yesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                scan = 1;
                searchInput(0);
            }
        });
        panel.add(yesButton);

        JButton noButton = new JButton("NO");
        noButton.setBounds(100, 40, 80, 25);
        noButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                scan = 2;
                searchInput(0);
            }
        });
        panel.add(noButton);

        frame.setVisible(true);
    }

    /*
     * accepts user input for UID
     * also called by managePatientRecords to print
     * */
    public String[] searchInput(int print) {
        readFile = new ReadFile();
        if (print == 1)
            scan = 1;

        frame = new JFrame();
        panel = new JPanel();

        frame.setSize(960, 540);
        frame.setTitle("Searching Laboratory Records...");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);

        panel.setLayout(null);

        switch (scan) {
            case 1 -> {
                JLabel UIDLabel = new JLabel("Enter request's UID: ");
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
                        requestUID = UIDText.getText().toUpperCase();
                        frame.dispose();
                        if (print != 1)
                            searchRecord();
                        else {
                            String code = requestUID.substring(0, 3);
                            String fileName = code + FILE_NAME;

                            // get all lines in laboratory requests file and save to String[][] labRecords
                            readFile.readRequests(fileName);
                            labRecords = readFile.getTempReq();

                            // count total non-null entries in String[][] requests
                            for (String[] request : labRecords)
                                for (int j = 0; j < labRecords[0].length; j++)
                                    if (request[0] != null && request[0].length()==15) {
                                        countRequests++;
                                        break;
                                    }

                            // get all lines in services file and save to String[][] serviceRecords
                            fileName = "services.txt";
                            readFile.readServices(fileName);
                            serviceRecords = readFile.getTempServ();

                            // count total non-null entries in String[][] services
                            for (String[] service : serviceRecords)
                                for (int j = 0; j < serviceRecords[0].length; j++)
                                    if (service[0] != null && service[0].length() == 3) {
                                        countServices++;
                                        break;
                                    }

                            // check for code match
                            for (int i = 0; i < countServices; i++)
                                if (code.equals(serviceRecords[i][0])) {
                                    display[1] = serviceRecords[i][1];
                                    break;
                                }

                            // check for UID match
                            for(int i=0; i<countRequests; i++)
                                if (Objects.equals(requestUID, labRecords[i][0])) {
                                    display[0] = labRecords[i][0];
                                    display[2] = labRecords[i][2];
                                    display[3] = labRecords[i][4];
                                    searched = 1;
                                    line = i;
                                    break;
                                }
                        }
                    }
                });
                panel.add(searchButton);
            }
            case 2 -> {
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
        }
        frame.setVisible(true);
        return display;
    }

    /*
     * searches for laboratory request
     * */
    public void searchRecord() {
        readFile = new ReadFile();

        switch (scan) {
            case 1 -> {
                String code = requestUID.substring(0, 3);
                String fileName = code + FILE_NAME;
                finalFileName = fileName;
                finalRequestUID = requestUID;
                // get all lines in laboratory requests file and save to String[][] labRecords
                int error = readFile.readRequests(fileName);
                if (error == 1)
                    error();
                labRecords = readFile.getTempReq();

                // count total non-null entries in String[][] requests
                for (String[] request : labRecords)
                    for (int j = 0; j < labRecords[0].length; j++)
                        if (request[0] != null && request[0].length()==15) {
                            countRequests++;
                            break;
                        }

                // get all lines in services file and save to String[][] serviceRecords
                fileName = "services.txt";
                error = readFile.readServices(fileName);
                if(error==1)
                    error();
                serviceRecords = readFile.getTempServ();
                // count total non-null entries in String[][] services
                for (String[] service : serviceRecords)
                    for (int j = 0; j < serviceRecords[0].length; j++)
                        if (service[0] != null && service[0].length() == 3) {
                            countServices++;
                            break;
                        }
                // check for code match
                for (int i = 0; i < countServices; i++)
                    if (code.equals(serviceRecords[i][0])) {
                        display[1] = serviceRecords[i][1];
                        break;
                    }

                // check for UID match
                for(int i=0; i<countRequests; i++)
                    if (Objects.equals(requestUID, labRecords[i][0])) {
                        display[0] = labRecords[i][0];
                        display[2] = labRecords[i][2];
                        display[3] = labRecords[i][4];
                        searched = 1;
                        line = i;
                        search();
                    }
            }
            case 2 -> {
                String fileName = "services.txt";
                int error = readFile.readServices(fileName);
                if(error==1)
                    error();
                serviceRecords = readFile.getTempServ();
//                serviceRecords = sortArray(serviceRecords);

                // count total non-null entries in String[][] services
                for (String[] service : serviceRecords)
                    for (int j = 0; j < serviceRecords[0].length; j++)
                        if (service[0] != null && service[0].length() == 3) {
                            countServices++;
                            break;
                        }

                for (int i = 0; i < countServices; i++) {
                    String code = serviceRecords[i][0];
                    fileName = serviceRecords[i][0] + FILE_NAME;
                    error = readFile.readRequests(fileName);
                    if (error==-1)
                        error();
                    String[][] requestsTemp = readFile.getTempReq();
//                    requestsTemp = sortDate(requestsTemp);

                    // count total non-null entries in String[][] temp
                    int countTemp = 0;
                    for (String[] t : requestsTemp)
                        for (int j = 0; j < requestsTemp[0].length; j++)
                            if (!Objects.equals(t[0], null) && t[0].length()==15) {
                                countTemp++;
                                break;
                            }

                    for(int j = countTemp - 1; j >= 0; j--)
                        if (requestsTemp[j][0] != null && requestsTemp[j][1].equals(patientUID) && !Objects.equals(requestsTemp[j][5], "D")) {
                            savedRequests[searched][0] = requestsTemp[j][0];
                            savedRequests[searched][1] = code;
                            savedRequests[searched][2] = requestsTemp[j][2];
                            savedRequests[searched][3] = requestsTemp[j][4];
                            searched++;
                        }
                    Arrays.stream(requestsTemp).forEach(x -> Arrays.fill(x, null));
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
     * checks if there were requests found
     * displays records there are multiple records found
     * */
    public void search() {
        readFile = new ReadFile();

        if (searched==0) {
            noRecordFound = 1;
            error();
        } else if (searched>1) {
            frame = new JFrame();
            panel = new JPanel();

            frame.setSize(960, 540);
            frame.setTitle("Searching Laboratory Records...");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(panel);

            panel.setLayout(null);

            JLabel UIDLabel = new JLabel("Request's UID");
            UIDLabel.setBounds(10, 10, 100, 20);
            panel.add(UIDLabel);

            JLabel labTestTypeLabel = new JLabel("Lab Test Type");
            labTestTypeLabel.setBounds(200, 10, 100, 20);
            panel.add(labTestTypeLabel);

            JLabel requestDateLabel = new JLabel("Request Date");
            requestDateLabel.setBounds(390, 10, 100, 20);
            panel.add(requestDateLabel);

            JLabel resultLabel = new JLabel("Result");
            resultLabel.setBounds(580, 10, 100, 20);
            panel.add(resultLabel);

            int y = 10;
            for (int i = 0; i < searched; i++) {
                y+=20;
                JLabel UID = new JLabel(savedRequests[i][0]);
                UID.setBounds(10, y, 170, 20);
                panel.add(UID);

                JLabel labTestType = new JLabel(savedRequests[i][1]);
                labTestType.setBounds(200, y, 170, 20);
                panel.add(labTestType);

                JLabel requestDate = new JLabel(savedRequests[i][2]);
                requestDate.setBounds(390, y, 170, 20);
                panel.add(requestDate);

                JLabel result = new JLabel(savedRequests[i][3]);
                result.setBounds(580, y, 170, 20);
                panel.add(result);
            }
            y+=30;
            JLabel enterUID = new JLabel("Enter the request's UID: ");
            enterUID.setBounds(10, y, 140, 20);
            panel.add(enterUID);

            JTextField UIDText = new JTextField();
            UIDText.setBounds(150, y, 250, 25);
            panel.add(UIDText);

            JButton enterButton = new JButton("ENTER");
            enterButton.setBounds(10, y+30, 100, 25);
            enterButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    finalRequestUID = UIDText.getText();
                    String code = finalRequestUID.substring(0, 3);
                    finalFileName = code + FILE_NAME;

                    readFile.readRequests(finalFileName);
                    String[][] temp = readFile.getTempReq();
                    // count total non-null entries in String[][] requests
                    for (String[] request : temp)
                        for (int j = 0; j < temp[0].length; j++)
                            if (request[0] != null && request[0].length()==15) {
                                countRequests++;
                                break;
                            }

                    String filename = "services.txt";
                    readFile.readServices(filename);
                    String[][] tempService = readFile.getTempServ();
                    // count total non-null entries in String[][] services
                    int countService = 0;
                    for (String[] service : tempService)
                        for (int j = 0; j < tempService[0].length; j++)
                            if (service[0] != null && service[0].length() == 3) {
                                countService++;
                                break;
                            }

                    for (int i = 0; i < countService; i++)
                        if (Objects.equals(tempService[i][0], (code)) && !Objects.equals(tempService[i][3], "D"))
                            display[1] = tempService[i][1];

                    for(int i = 0; i<countRequests; i++)
                        if (Objects.equals(finalRequestUID, temp[i][0])) {
                            searched = 1;
                            line = i;
                            display[0] = temp[i][0];
                            display[2] = temp[i][2];
                            display[3] = temp[i][4];
                            break;
                        }
                    frame.dispose();
                    searchVerify();
                }
            });
            panel.add(enterButton);
            frame.setVisible(true);
        } else {
            if (methodType == 0)
                displayLaboratoryRequest();
            else if (methodType == 1)
                deleteInput();
            else if (methodType == 2)
                editVerify();
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
                displayLaboratoryRequest();
            else if (methodType == 1)
                deleteInput();
            else if (methodType == 2)
                editVerify();
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
                    searchLaboratoryRequest();
                else if (methodType == 1)
                    deleteLaboratoryRequest();
                else if (methodType == 2)
                    editLaboratoryRequest();
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
        if (exists == 1)
            dialogue = new JLabel(messageDialogue);
        else if (noRecordFound == 1)
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
                    searchLaboratoryRequest();
                else if (methodType == 1)
                    deleteLaboratoryRequest();
                else if (methodType == 2)
                    editLaboratoryRequest();
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
