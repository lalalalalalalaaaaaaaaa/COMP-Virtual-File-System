package hk.edu.polyu.comp.comp2021.cvfs.model.control;

import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.CVFS;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.File;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CommandLineInterface {
    private CVFS cvfs;

    public CommandLineInterface(CVFS cvfs) {
        this.cvfs = cvfs; // setup
    }

    public void start() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println();
        System.out.println("***************************************************************************************************************************************");
        System.out.println("                                 Welcome to the Command-Line Based COMP Virtual File System!                                                                     ");
        System.out.println("                                The system provides you with diverse commands for file management.");
        System.out.println("                             The CVFS system is completed by HUANG Ling Ru, SHEN Ling Hui, Tan Rou Xin");
        System.out.println("***************************************************************************************************************************************");
        System.out.println();
        System.out.println("Available commands: newVD, newDoc, newDir, delete, rename, changeDir, list, rList\n"+
                "                    newSimpleCri, newNegationCri, newIsDocCri, newBinaryCri, search, rSearch\n"+
                "                    save, load, undo, redo, quit.");
        System.out.println("Now you can input your commands here (Case doesn't matter):");


            while (running) {
                try {
                    System.out.print("> ");
                    String input = scanner.nextLine();
                    String[] commandParts = input.trim().split("\\s+");

                    if (commandParts.length == 0) {
                        System.out.println("Please enter a command.");
                        continue;
                    }
                    String command = commandParts[0].toLowerCase();

                    switch (command) {
                        case "quit":
                            running = false;
                            System.out.println("Exiting COMP Virtual File System...");
                            break;
                        case "newvd":
                            if (commandParts.length != 2) {
                                System.out.println("Usage: newVD <diskSize>");
                                break;
                            }
                            int diskSize = Integer.parseInt(commandParts[1]);
                            cvfs.newVD(diskSize);
                            System.out.println("A new Virtual Disk is created.");
                            break;
                        case "newdoc":
                            if (commandParts.length < 4) {
                                System.out.println("Usage: newDoc <docName> <docType> <docContent>");
                                break;
                            }
                            String docContent = String.join(" ", Arrays.copyOfRange(commandParts, 3, commandParts.length));
                            cvfs.newDoc(commandParts[1], commandParts[2], docContent);
                            System.out.println("Document created successfully.");
                            break;
                        case "newdir":
                            if (commandParts.length != 2) {
                                System.out.println("Usage: newDir <dirName>");
                                break;
                            }
                            cvfs.newDir(commandParts[1]);
                            System.out.println("Directory created successfully.");
                            break;
                        case "delete":
                            if (commandParts.length != 2) {
                                System.out.println("Usage: delete <fileName>");
                                break;
                            }
                            cvfs.delete(commandParts[1]);
                            break;
                        case "rename":
                            if (commandParts.length != 3) {
                                System.out.println("Usage: rename <oldFileName> <newFileName>");
                                break;
                            }
                            cvfs.rename(commandParts[1], commandParts[2]);
                            break;
                        case "changedir":
                            if (commandParts.length != 2) {
                                System.out.println("Usage: changeDir <directoryName>");
                                break;
                            }
                            cvfs.changeDir(commandParts[1]);
                            break;
                        case "list":
                            cvfs.list();
                            break;
                        case "rlist":
                            cvfs.rList();
                            break;
                        case "newsimplecri":
                            if(commandParts.length != 5){
                                System.out.println("Usage: newSimpleCri <criName> <attrName> <op> <val>");
                                break;
                            }
                            cvfs.newSimpleCri(commandParts[1], commandParts[2], commandParts[3], commandParts[4]);
                                System.out.println("A simple criterion is created.");
                            break;
                        case "newisdoccri":
                            cvfs.isDocument();
                            System.out.println("An IsDocument criterion is created.");
                            break;
                        case "newnegationcri":
                            if(commandParts.length != 3){
                                System.out.println("Usage: newNegation <criName1> <criName2>");
                                break;
                            }
                            cvfs.newNegation(commandParts[1],commandParts[2]);
                                System.out.println("A Negation criterion is created.");
                            break;
                        case "newbinarycri":
                            if(commandParts.length != 5){
                                System.out.println("Usage: newBinaryCri <criName1> <criName3> <op> <criName4>");
                                break;
                            }
                            cvfs.newBinaryCri(commandParts[1], commandParts[2], commandParts[3], commandParts[4]);
                                System.out.println("A Binary criterion is created.");
                            break;
                        case "printallcri":
                            cvfs.printAllCriteria();
                            break;
                        case "search":
                            if(commandParts.length!=2){
                                System.out.println("Usage: search <criName>");
                                break;
                            }
                            List<File> satisfiedFiles = cvfs.search(commandParts[1]);
                            System.out.println("Do you want to display the file list that satisfying the criterion? (Enter Y or N");
                            String choice = scanner.nextLine();
                            if(choice.equals("Y")) cvfs.printSatifiedFile(satisfiedFiles);
                            else if(choice.equals("N")) System.out.println("OK, you can enter next command.");
                            break;
                        case "rsearch":
                            if(commandParts.length!=2){
                                System.out.println("Usage: rSearch <criName>");
                                break;
                            }
                            List<File> rSatisfiedFiles = cvfs.rSearch(commandParts[1]);
                            System.out.println("Do you want to display the file list that satisfying the criterion? (Enter Y or N");
                            String choice2 = scanner.nextLine();
                            if(choice2.equals("Y")) cvfs.printSatifiedFile(rSatisfiedFiles);
                            else if(choice2.equals("N")) System.out.println("OK, you can enter next command.");
                            break;
                        case "save":
                            if(commandParts.length!=2){
                                System.out.println("Usage: save <path>");
                                break;
                            }
                            Controller controller=new Controller(this.cvfs);
                            controller.save(commandParts[1]);
                            break;
                        case "load":
                            if(commandParts.length!=2){
                                System.out.println("Usage: load <path>");
                                break;
                            }
                            this.cvfs=Controller.load(commandParts[1]);
                            break;
                        case "undo":
                            cvfs.undo();
                            break;
                        case "redo":
                            cvfs.redo();
                            break;
                        default:
                            System.out.println("Unexpected command. Please try again.");
                            break;
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    System.out.println("Invalid command format. Please try again.");
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number format. Please try again.");
                } catch (IllegalArgumentException e) {
                    System.out.println(e.getMessage());
                } catch (Exception e) {
                    System.out.println("An unexpected error occurred: " + e.getMessage());
                }
            }
    }
}