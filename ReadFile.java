/*
* Class: ReadFile
* Reads needed files and stores into a 2d array
* */

import java.io.*;
import java.util.Scanner;

public class ReadFile {
    private String[][] tempSearch = new String[256][11];
    private String[][] tempServ = new String[256][11];
    private String[][] tempReq = new String[256][11];
    private String UID;

//getting all lines from patients file
    public int readPatients(String fileName) {
        int error;
        int counter = 0;

        try {
            File file = new File(fileName);
            Scanner scannerFile = new Scanner(file);

            while(scannerFile.hasNextLine()) {
                String temp = scannerFile.nextLine();
                String[] tempLine = temp.split(";");

                for (int i = counter; i < counter + 1; i++) {
                    if (tempLine.length <= 9) {
                        for (int j = 0; j < 9; j++)
                            if (tempLine[j] != null)
                                tempSearch[i][j] = tempLine[j];
                    } else if (tempLine.length == 11) {
                        for (int j = 0; j < 11; j++)
                            if (tempLine[j] != null) {
                                tempSearch[i][j] = tempLine[j];
                            }
                    }
                }
                counter++;
            }

            error = 0;
        } catch (IOException e) {
            error = 1;
        }
        return error;
    }

//getting all lines from services file
    public int readServices(String fileName) {
        int error;
        int counter = 0;

        try {
            File file = new File(fileName);
            Scanner scannerFile = new Scanner(file);

            while(scannerFile.hasNextLine()) {
                String temp = scannerFile.nextLine();
                String[] tempLine = temp.split(";");
                for (int i = counter; i < counter + 1; i++) {
                    if (tempLine.length <= 3) {
                        for (int j = 0; j < 3; j++)
                            if (tempLine[j] != null)
                                tempServ[i][j] = tempLine[j];
                    } else if (tempLine.length == 5) {
                        for (int j = 0; j < 5; j++)
                            if (tempLine[j] != null) {
                                tempServ[i][j] = tempLine[j];
                            }
                    }
                }
                counter++;
            }

            error = 0;
        } catch (IOException e) {
            error = 1;
        }
        return error;
    }

//getting all lines from requests file
    public int readRequests(String fileName) {
        int error;
        int counter = 0;
        try {
            File file = new File(fileName);
            boolean exists = file.exists();
            if(!exists)
                return -1;

            Scanner scannerFile = new Scanner(file);

            while(scannerFile.hasNextLine()) {
                String temp = scannerFile.nextLine();
                String[] tempLine = temp.split(";");
                for (int i = counter; i < counter + 1; i++) {
                    if (tempLine.length <= 5) {
                        for (int j = 0; j < 5; j++)
                            if (tempLine[j] != null)
                                tempReq[i][j] = tempLine[j];
                    } else if (tempLine.length == 7) {
                        for (int j = 0; j < 7; j++)
                            if (tempLine[j] != null) {
                                tempReq[i][j] = tempLine[j];
                            }
                    }
                }
                counter++;
            }
            error = 0;
        } catch (FileNotFoundException | NullPointerException e) {
            return 0;
        }
        return error;
    }

//reading UID for generateUID
    public int readUID(String fileName) {
        int isFirst;
        String line;
        String temp = null;
        try {
            FileReader file = new FileReader(fileName);
            BufferedReader buffer = new BufferedReader(file);

            while((line = buffer.readLine())!=null) {
                temp = line;
            }
            assert temp != null;
            String[] split = temp.split(";");
            UID = split[0];

            isFirst = 0;
        } catch (IOException | NullPointerException e) {
            isFirst = 1;
        }
        return isFirst;
    }

// checks patient's UID if it exists - Laboratory Requests
    public int checkUID(String fileName, String UID) {
        int exists = 0;
        try {
            File file = new File(fileName);
            Scanner scannerFile = new Scanner(file);

            while(scannerFile.hasNextLine()) {
                String temp = scannerFile.nextLine();
                String[] tempLine = temp.split(";");
                if (tempLine[0].equalsIgnoreCase(UID) && tempLine.length<10) {
                    return 1;
                }
            }
        } catch (IOException | NullPointerException ignored) {}
        return exists;
    }

// checks if service code exists - manage services
    public int checkCode(String fileName, String code) {
        int exists = 0;
        try {
            File file = new File(fileName);
            Scanner scannerFile = new Scanner(file);

            while(scannerFile.hasNextLine()) {
                String temp = scannerFile.nextLine();
                String[] tempLine = temp.split(";");
                if (tempLine[0].equalsIgnoreCase(code) && tempLine.length<4) {
                    return 1;
                }
            }
        } catch (IOException | NullPointerException ignored) {}
        return exists;
    }

    public String[][] getTempSearch() {
        return tempSearch.clone();
    }

    public String[][] getTempServ() {
        return tempServ.clone();
    }

    public String[][] getTempReq() {
        return tempReq.clone();
    }

    public String getUID() {
        return UID;
    }
}