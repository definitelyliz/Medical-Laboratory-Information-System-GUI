/*
 * Class: ManageServices
 * Manages Services offered such as
 *   adding a new service,
 *   searching for a service,
 *   deleting a service,
 *   and editing a service.
 * */


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ManageServices {
    private ArrayList<Service> services;
    String fileName = "services.txt";

    private MainMenu mainMenu;
    private ReadFile readFile;
    private WriteToFile writeFile;
    private String code;
    private String desc;
    private int price;
    private int scan;
    private int line;
    private JFrame frame;
    private JPanel panel;
    private JLabel added;
    private JTextField getCode;
    private JTextField getDesc;
    private JTextField getPrice;
    private JTextField inputKey;
    private JTextField inputCode;
    private JTextField delReason;

    public void manageServices() {
        mainMenu = new MainMenu();
        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Services");
        frame.add(panel);

        panel.setLayout(null);

        JLabel selectLabel = new JLabel("Select a transaction:");
        selectLabel.setBounds(10, 10, 250, 25);

        JButton add = new JButton("Add New Service");
        add.setBounds(10, 30, 250, 25);
        add.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                addGui();
            }
        });

        JButton search = new JButton("Search Service");
        search.setBounds(10, 50, 250, 25);
        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                searchGui();
            }
        });

        JButton delete = new JButton("Delete Service");
        delete.setBounds(10, 70, 250, 25);
        delete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                deleteGui();
            }
        });

        JButton edit = new JButton("Edit Service");
        edit.setBounds(10, 90, 250, 25);
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                editGui();
            }
        });

        JButton menu = new JButton("Main Menu");
        menu.setBounds(10, 110, 250, 25);
        menu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                mainMenu.mainMenu();
            }
        });

        panel.add(selectLabel);
        panel.add(add);
        panel.add(search);
        panel.add(delete);
        panel.add(edit);
        panel.add(menu);

        frame.setVisible(true);
    }

    /*
     * adds a new service
     * accepts parameter int type for editService
     * */

    public void addService(int type) {
        //check if service code already exists
        int exists;
        exists = readFile.checkCode(fileName, code);

        if (exists == 1) {
            JLabel codeExists = new JLabel("Service code already exists! Please try again!");
            codeExists.setBounds(10, 260, 300, 25);

            addGui();
            panel.add(codeExists);
        }else{
            Service service = new Service(code, desc, price);
            services.add(service);

            int error = writeFile.writeToServices(fileName, service);
            if (error == 1)
                addService(0);
            else
                added = new JLabel(code + " " + desc + " has been added.");
                added.setBounds(10, 70, 165, 25);
                panel.add(added);
            if (type == 1)
                return;

                frame.dispose();

                frame = new JFrame();
                panel = new JPanel();
                frame.add(panel, BorderLayout.LINE_START);
                frame.setSize(960, 540);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setTitle("Add Service");
                frame.add(panel);
                panel.setLayout(null);

                panel.add(added);
                JLabel newCode = new JLabel("Do you want to add another service?");
                newCode.setBounds(10, 10, 250, 25);

                JButton addMore = new JButton("YES");
                addMore.setBounds(10, 30, 165, 25);
                addMore.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                      frame.dispose();
                      addGui();
                    }
                });

            JButton dontAdd = new JButton("NO");
            dontAdd.setBounds(180, 30, 165, 25);
            dontAdd.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    mainMenu.mainMenu();
                }
            });

            panel.add(newCode);
            panel.add(addMore);
            panel.add(dontAdd);

            frame.setVisible(true);

        }
    }

    //    searches for a service
    public void searchService() {
        mainMenu = new MainMenu();

        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Search Service");
        frame.add(panel);

        JLabel notFound = new JLabel(" ");
        notFound.setBounds(10, 60, 500, 25);

        JLabel tryAgain = new JLabel(" ");
        tryAgain.setBounds(10, 80, 500, 25);


        line = search();

        JButton searchAgain;
        JButton retMenu;

        if(line==-1)
            searchService();
        else if(line==-2) {
            notFound.setText("No record found.");
            tryAgain.setText("Would you like to try again or return to the main menu?");

            searchAgain = new JButton("Search again");
            searchAgain.setBounds(10, 110, 165, 25);
            searchAgain.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    searchGui();
                }
            });

            retMenu = new JButton("Return to Main Menu");
            retMenu.setBounds(180, 110, 165, 25);
            retMenu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    mainMenu.mainMenu();
                }
            });
            panel.add(notFound);
            panel.add(tryAgain);
            panel.add(searchAgain);
            panel.add(retMenu);

            frame.setVisible(true);
        } else {
            tryAgain.setText("Would you like to search for another service?");
            tryAgain.setBounds(10,90,500,25);

            searchAgain = new JButton("Search again");
            searchAgain.setBounds(10, 110, 165, 25);
            searchAgain.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    searchGui();
                }
            });

            retMenu = new JButton("Return to Main Menu");
            retMenu.setBounds(180, 110, 165, 25);
            retMenu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    mainMenu.mainMenu();
                }
            });

            panel.add(tryAgain);
            panel.add(searchAgain);
            panel.add(retMenu);

            frame.setVisible(true);
        }
        frame.setVisible(true);
    }

    //    deletes a service
    public void deleteService() {
        mainMenu = new MainMenu();

        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Delete Service");
        frame.add(panel);

        JLabel notFound = new JLabel(" ");
        notFound.setBounds(10, 60, 500, 25);
        panel.add(notFound);

        JLabel tryAgain = new JLabel(" ");
        tryAgain.setBounds(10, 80, 500, 25);

        line = search();

        JButton retMenu;
        JButton searchAgain;
        if (line == -1)
            deleteService();
        else if (line == -2) {
            notFound.setText("No record found.");
            tryAgain.setText("Would you like to try again or return to the main menu?");

            searchAgain = new JButton("Delete again");
            searchAgain.setBounds(10, 110, 165, 25);
            searchAgain.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    searchService();
                }
            });


            retMenu = new JButton("Return to Main Menu");
            retMenu.setBounds(180, 110, 165, 25);
            retMenu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    mainMenu.mainMenu();
                }
            });

            panel.add(tryAgain);
            panel.add(searchAgain);
            panel.add(retMenu);

            frame.setVisible(true);
        } else {
            delete(1,line);
            tryAgain.setText("Would you like to delete another service?");
            tryAgain.setBounds(10, 50, 500, 25);

            searchAgain = new JButton("Delete again");
            searchAgain.setBounds(10, 80, 165, 25);
            searchAgain.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    deleteGui();
                }
            });

            retMenu = new JButton("Return to Main Menu");
            retMenu.setBounds(180, 80, 165, 25);
            retMenu.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    mainMenu.mainMenu();
                }
            });

            panel.add(tryAgain);
            panel.add(searchAgain);
            panel.add(retMenu);

            JLabel newCode = new JLabel("Would you like to delete another service?");
            newCode.setBounds(10, 10, 250, 25);

            JButton addMore = new JButton("YES");
            addMore.setBounds(10, 30, 165, 25);
            addMore.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    deleteGui();
                }
            });

            JButton dontAdd = new JButton("NO");
            dontAdd.setBounds(180, 30, 165, 25);
            dontAdd.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    frame.dispose();
                    mainMenu.mainMenu();
                }
            });

            frame.setVisible(true);
        }
    }

    //    edit a service
    public void editService() {
        mainMenu = new MainMenu();

        frame = new JFrame();
        panel = new JPanel();
        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Add Service");
        frame.add(panel);
        panel.setLayout(null);

        String input;

        int line = search();
        delete(2, line);
        addService(1);

        System.out.println();

        JLabel editAgain = new JLabel("Do you want to edit another patient record?");
        editAgain.setBounds(10,90,500,25);
        panel.add(editAgain);

        JButton yes = new JButton("YES");
        yes.setBounds(10, 110, 165, 25);
        yes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                editGui();
            }
        });

        JButton no = new JButton("NO");
        no.setBounds(180, 110, 165, 25);
        no.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                mainMenu.mainMenu();
            }
        });

        panel.add(yes);
        panel.add(no);

        frame.setVisible(true);
    }

    public void addGui() {
        mainMenu = new MainMenu();
        readFile = new ReadFile();
        writeFile = new WriteToFile();
        services = new ArrayList<>();

        frame = new JFrame();
        panel = new JPanel();
        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Add Service");
        frame.add(panel);
        panel.setLayout(null);

        JLabel codeLabel = new JLabel("Enter unique 3-code Service Code:");
        codeLabel.setBounds(10, 10, 500, 25);
        panel.add(codeLabel);

        getCode = new JTextField(20);
        getCode.setBounds(10, 30, 165, 25);
        panel.add(getCode);
        panel.add(getCode);

        JLabel descLabel = new JLabel("Enter laboratory service Description:");
        descLabel.setBounds(10, 70, 500, 25);
        panel.add(descLabel);

        getDesc = new JTextField(20);
        getDesc.setBounds(10, 90, 165, 25);
        panel.add(getDesc);

        JLabel priceLabel = new JLabel("Enter laboratory service Price:");
        priceLabel.setBounds(10, 130, 500, 25);
        panel.add(priceLabel);

        getPrice = new JTextField(20);
        getPrice.setBounds(10, 150, 165, 25);
        panel.add(getPrice);

        JLabel save = new JLabel("Would you like to save this service?");
        JButton yes = new JButton("YES");
        JButton no = new JButton("NO");
        yes.setBounds(10, 225, 165, 25);
        no.setBounds(180, 225, 165, 25);
        save.setBounds(10, 200, 500, 25);

        yes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                code = getCode.getText().toUpperCase();
                desc = getDesc.getText();
                price = Integer.parseInt(getPrice.getText());
                addService(0);
            }
        });
        no.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                mainMenu.mainMenu();
            }
        });

        panel.add(save);
        panel.add(yes);
        panel.add(no);

        frame.setVisible(true);
    }


    public void searchGui() {
        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Services");
        frame.add(panel);

        panel.setLayout(null);

        JLabel searchCode1 = new JLabel("");

        searchCode1.setText("Do you know the service code?");
        searchCode1.setBounds(10, 10, 250, 25);

        JButton sYes = new JButton("YES");
        sYes.setBounds(10, 40, 165, 25);
        sYes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                scan = 1;
                searchInput();
            }
        });

        JButton sNo = new JButton("NO");
        sNo.setBounds(180, 40, 165, 25);
        sNo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                scan = 2;
                searchInput();
            }
        });

        panel.add(searchCode1);
        panel.add(sYes);
        panel.add(sNo);
        frame.dispose();
        frame.setVisible(true);
    }
    
    public void searchInput(){
        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Services");
        frame.add(panel);

        panel.setLayout(null);
        
        switch(scan){
            case 1 -> {
                JLabel searchLabel = new JLabel("Enter service code");
                searchLabel.setBounds(10, 10, 500, 25);
                panel.add(searchLabel);

                inputCode = new JTextField(20);
                inputCode.setBounds(10, 30, 165, 25);
                panel.add(inputCode);

                JButton searchCode = new JButton("Search");
                searchCode.setBounds(180, 30, 165, 25);
                panel.add(searchCode);
                searchCode.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        searchService();
                    }
                });
            }
            case 2 -> {
                JLabel descLabel = new JLabel("Input a keyword of the service's description");
                descLabel.setBounds(10, 10, 500, 25);
                panel.add(descLabel);

                inputKey = new JTextField(20);
                inputKey.setBounds(10, 30, 165, 25);
                panel.add(inputKey);

                JButton searchKey = new JButton("Search");
                searchKey.setBounds(180, 30, 165, 25);
                panel.add(searchKey);
                searchKey.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        searchService();
                    }
                });
            }
        }
        frame.setVisible(true);
    }

    /*
     * searches services.txt for methods:
     *   searchService(), deleteService(), and editService()
     * returns the line number needed
     * */
    public int search() {
        readFile = new ReadFile();

        int line = 0;
        String input = null;

        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Services");
        frame.add(panel);

        panel.setLayout(null);

        JLabel code = new JLabel("Service Code");
        JLabel desc = new JLabel("Description");
        JLabel price = new JLabel("Price");
        code.setBounds(10, 10, 500, 25);
        desc.setBounds(150, 10, 500, 25);
        price.setBounds(290, 10, 500, 25);

        JLabel finalCode = new JLabel("");
        finalCode.setBounds(10, 30, 500, 25);
        JLabel finalDesc = new JLabel("");
        finalDesc.setBounds(150, 30, 500, 25);
        JLabel finalPrice = new JLabel("");
        finalPrice.setBounds(290, 30, 500, 25);

        // get all lines in services.txt and save to String[][] services
        String fileName = "services.txt";
        int error = readFile.readServices(fileName);
        if(error==1)
            return -1;
        String[][] services = readFile.getTempServ();
        // count total non-null entries in String[][] services
        int count = 0;
        for (String[] service : services)
            for (int j = 0; j < services[0].length; j++)
                if (service[0] != null && service[0].length() == 3) {
                    count++;
                    break;
                }
        // switch case
        // get input from user to search match/es in String services[][]
        int searched = 0;
        int[] lines = new int[256];
        String searchCode;

        switch (scan) {
            case 1 -> {
                searchCode = inputCode.getText();
                for (int i = 0; i <= count; i++) {
                    if (Objects.equals(services[i][0], searchCode) && !Objects.equals(services[i][3], "D")) {
                        searched = 1;
                        lines[0] = i;
                        break;
                    } else
                        lines[0] = -2;
                }
            }
            case 2 -> {
                input = inputKey.getText();
                for (int i = 0; i <= count; i++) {                       // for every service
                    if(services[i][1] != null){
                        String[] temp = services[i][1].split(" ");          // get words in description
                        // for every word in description
                        for (String s : temp)
                            try {
                                if (!Objects.equals(services[i][3], "D"))
                                    if (s.equalsIgnoreCase(input)) {
                                        lines[searched] = i;
                                        searched++;
                                    }
                            } catch (NullPointerException ignored) { }
                    }
                }

            }
            default -> {
                System.out.println("An error occurred. Please try again.");
                System.out.println();
                return -1;
            }
        }

        // if there is only 1 match search, return line number to line
        // else: ask user to input the patient's UID to display
        String[] temp;
        if(searched==0)
            return -2;
        else if(searched>1) {
            panel.add(code);
            panel.add(desc);
            panel.add(price);
            try {
                int y = 30;
                for(int i=0; i < services.length; i++) {
                    temp = services[i][1].split(" ");
                    for (int j = 0; j < temp.length; j++) {
                        if (!Objects.equals(services[i][3], "D"))
                            if (temp[j].equalsIgnoreCase(input)) {
                                finalCode = new JLabel(services[i][0]);
                                finalDesc = new JLabel(services[i][1]);
                                finalPrice = new JLabel(services[i][2]);

                                finalCode.setBounds(10, y, 500, 25);
                                finalDesc.setBounds(150, y, 500, 25);
                                finalPrice.setBounds(290, y, 500, 25);

                                y += 20;
                                panel.add(code);
                                panel.add(desc);
                                panel.add(price);
                                panel.add(finalCode);
                                panel.add(finalDesc);
                                panel.add(finalPrice);
                                frame.setVisible(true);
                            }
                    }
                }

            } catch (NullPointerException ignored) {}
            scan = 1;
            searchInput();
        }else{
            line = lines[0];

            finalCode.setText(services[line][0]);
            finalDesc.setText(services[line][1]);
            finalPrice.setText(services[line][2]);

            panel.add(code);
            panel.add(desc);
            panel.add(price);
            panel.add(finalCode);
            panel.add(finalDesc);
            panel.add(finalPrice);

            frame.setVisible(true);
        }
        return line;
    }

    public void deleteGui(){
        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Delete Service");
        frame.add(panel);

        panel.setLayout(null);

        JLabel searchCode1 = new JLabel("");

        searchCode1.setText("Do you know the service code?");
        searchCode1.setBounds(10, 10, 250, 25);

        JButton sYes = new JButton("YES");
        sYes.setBounds(10, 40, 165, 25);
        sYes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                scan = 1;
                deleteInput();
            }
        });

        JButton sNo = new JButton("NO");
        sNo.setBounds(180, 40, 165, 25);
        sNo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                scan = 2;
                deleteInput();
            }
        });

        panel.add(searchCode1);
        panel.add(sYes);
        panel.add(sNo);
        frame.dispose();
        frame.setVisible(true);
    }

    public void deleteInput(){
        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Delete Service");
        frame.add(panel);

        panel.setLayout(null);

        switch(scan){
            case 1 -> {
                JLabel searchLabel = new JLabel("Enter service code");
                searchLabel.setBounds(10, 10, 500, 25);
                panel.add(searchLabel);

                inputCode = new JTextField(20);
                inputCode.setBounds(10, 30, 165, 25);
                panel.add(inputCode);

                JButton searchCode = new JButton("continue");
                searchCode.setBounds(180, 30, 165, 25);
                panel.add(searchCode);
                searchCode.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        deleteReason();
                    }
                });
            }
            case 2 -> {
                JLabel descLabel = new JLabel("Input a keyword of the service's description");
                descLabel.setBounds(10, 10, 500, 25);
                panel.add(descLabel);

                inputKey = new JTextField(20);
                inputKey.setBounds(10, 30, 165, 25);
                panel.add(inputKey);

                JButton searchKey = new JButton("continue");
                searchKey.setBounds(180, 30, 165, 25);
                panel.add(searchKey);
                searchKey.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        deleteReason();
                    }
                });
            }
        }
        frame.setVisible(true);
    }

    public void deleteReason(){
        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Delete Service");
        frame.add(panel);

        JLabel delLabel = new JLabel("Please state reason for deletion");
        delLabel.setBounds(10, 10, 500, 25);
        panel.add(delLabel);

        panel.setLayout(null);

        delReason = new JTextField(20);
        delReason.setBounds(10, 30, 165, 25);
        panel.add(delReason);

        JButton delCode = new JButton("Delete");
        delCode.setBounds(180, 30, 100, 25);
        panel.add(delCode);
        delCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                deleteService();
            }

        });

        frame.setVisible(true);
    }

    /*
     * deletes a service for methods:
     *   deleteService() and editService()
     * accepts parameters:
     *   int type for deleteService() and editService()
     *   int line for which line will be deleted
     * */
    public void delete(int type, int line) {
        String reason = delReason.getText();
        String D = "D;";
        String newLine = String.join("", D, reason);

        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Services");
        frame.add(panel);

        panel.setLayout(null);

        String fileName = "services.txt";
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
            String code = splitLine[0];
            String description = splitLine[1];

            JLabel deleted = new JLabel(code + " " + description + " has been deleted");
            deleted.setBounds(10,10,500,25);
            panel.add(deleted);
        } catch(IOException e) {
            System.out.println("Error occurred. Please try again");
            if (type==1)
                deleteService();
            else
                editService();
        }
        frame.setVisible(true);
    }

    public void editGui(){
        mainMenu = new MainMenu();
        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Delete Service");
        frame.add(panel);
        panel.setLayout(null);

        JLabel eDesc1 = new JLabel("The services cannot be edited.");
        JLabel eDesc2 = new JLabel("If you would like to edit an existing service, the service will be first deleted from the file, and a new service will be created");
        JLabel eDesc3 = new JLabel("Do you know the service code?");

        eDesc1.setBounds(10, 10, 200, 25);
        eDesc2.setBounds(10,30,1000,25);
        eDesc3.setBounds(10,50,200,25);

        JButton edit = new JButton("YES");
        edit.setBounds(10, 80,165,25);
        edit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                scan = 1;
                editInput();
            }
        });

        JButton edit2 = new JButton("NO");
        edit2.setBounds(180, 80,165,25);
        edit2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                scan = 2;
                editInput();
            }
        });

        JButton menu = new JButton("Main Menu");
        menu.setBounds(350, 80,165,25);
        menu.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                mainMenu.mainMenu();
            }
        });

        panel.add(eDesc1);
        panel.add(eDesc2);
        panel.add(eDesc3);
        panel.add(edit);
        panel.add(edit2);
        panel.add(menu);


        frame.setVisible(true);
    }

    public void editInput(){
        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Delete Service");
        frame.add(panel);

        panel.setLayout(null);

        switch(scan){
            case 1 -> {
                JLabel searchLabel = new JLabel("Enter service code");
                searchLabel.setBounds(10, 10, 500, 25);
                panel.add(searchLabel);

                inputCode = new JTextField(20);
                inputCode.setBounds(10, 30, 165, 25);
                panel.add(inputCode);

                JButton searchCode = new JButton("continue");
                searchCode.setBounds(180, 30, 165, 25);
                panel.add(searchCode);
                searchCode.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        editReason();
                    }
                });
            }
            case 2 -> {
                JLabel descLabel = new JLabel("Input a keyword of the service's description");
                descLabel.setBounds(10, 10, 500, 25);
                panel.add(descLabel);

                inputKey = new JTextField(20);
                inputKey.setBounds(10, 30, 165, 25);
                panel.add(inputKey);

                JButton searchKey = new JButton("continue");
                searchKey.setBounds(180, 30, 165, 25);
                panel.add(searchKey);
                searchKey.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        frame.dispose();
                        editReason();
                    }
                });
            }
        }
        frame.setVisible(true);
    }

    public void editReason(){
        frame = new JFrame();
        panel = new JPanel();

        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Delete Service");
        frame.add(panel);

        JLabel delLabel = new JLabel("Please state reason for deletion");
        delLabel.setBounds(10, 10, 500, 25);
        panel.add(delLabel);

        panel.setLayout(null);

        delReason = new JTextField(20);
        delReason.setBounds(10, 30, 165, 25);
        panel.add(delReason);

        JButton delCode = new JButton("Delete");
        delCode.setBounds(180, 30, 100, 25);
        panel.add(delCode);
        delCode.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                editAdd();
            }

        });

        frame.setVisible(true);
    }

    public void editAdd(){
        mainMenu = new MainMenu();
        readFile = new ReadFile();
        writeFile = new WriteToFile();
        services = new ArrayList<>();

        frame = new JFrame();
        panel = new JPanel();
        frame.add(panel, BorderLayout.LINE_START);
        frame.setSize(960, 540);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Add Service");
        frame.add(panel);
        panel.setLayout(null);

        JLabel codeLabel = new JLabel("Enter unique 3-code Service Code:");
        codeLabel.setBounds(10, 10, 500, 25);
        panel.add(codeLabel);

        getCode = new JTextField(20);
        getCode.setBounds(10, 30, 165, 25);
        panel.add(getCode);
        panel.add(getCode);

        JLabel descLabel = new JLabel("Enter laboratory service Description:");
        descLabel.setBounds(10, 70, 500, 25);
        panel.add(descLabel);

        getDesc = new JTextField(20);
        getDesc.setBounds(10, 90, 165, 25);
        panel.add(getDesc);

        JLabel priceLabel = new JLabel("Enter laboratory service Price:");
        priceLabel.setBounds(10, 130, 500, 25);
        panel.add(priceLabel);

        getPrice = new JTextField(20);
        getPrice.setBounds(10, 150, 165, 25);
        panel.add(getPrice);

        JLabel save = new JLabel("Would you like to save this service?");
        JButton yes = new JButton("YES");
        JButton no = new JButton("NO");
        yes.setBounds(10, 225, 165, 25);
        no.setBounds(180, 225, 165, 25);
        save.setBounds(10, 200, 500, 25);

        yes.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                code = getCode.getText().toUpperCase();
                desc = getDesc.getText();
                price = Integer.parseInt(getPrice.getText());
                editService();
            }
        });
        no.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                mainMenu.mainMenu();
            }
        });

        panel.add(save);
        panel.add(yes);
        panel.add(no);

        frame.setVisible(true);
    }

    // sort array by code
    public static String[][] sortArray(String[][] data) {
        int nonNull = 0;
        for (int i = 0; i < data[0].length; i++) {
            if (data[0][i] != null) {
                nonNull++;
            }
        }

        int counter = 0;
        String[][] newData = new String[nonNull][];
        for (int i = 0; i < data[0].length; i++) {
            if (data[0][i] != null) {
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

