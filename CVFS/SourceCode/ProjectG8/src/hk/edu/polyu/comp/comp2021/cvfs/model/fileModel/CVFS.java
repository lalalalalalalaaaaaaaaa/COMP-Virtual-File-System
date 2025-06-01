package hk.edu.polyu.comp.comp2021.cvfs.model.fileModel;

import hk.edu.polyu.comp.comp2021.cvfs.model.command.*;
import hk.edu.polyu.comp.comp2021.cvfs.model.criterion.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CVFS implements Serializable{
    private VirtualDisk curVD;
    private Directory curWorkDir;
    private List<String> types;
    private HashMap<String, Criterion> criterionHashMap =new HashMap<>();
    private CommandManager commandManager;

    public VirtualDisk getVirtualDisk() {
        return curVD;
    }

    public Directory getCurrentDir() {
        return curWorkDir;
    }

    public void setCurWorkDir(Directory dir){
        this.curWorkDir=dir;
    }

    public CVFS(int maxDiskSize){
        this.curVD=new VirtualDisk(maxDiskSize);
        this.types = Arrays.asList("txt", "java", "html", "css");
        this.curWorkDir = curVD.getRootDir();
        this.commandManager = new CommandManager();
    }


    public void newVD(int diskSize){
        this.curVD=new VirtualDisk(diskSize);
        this.curWorkDir=curVD.getRootDir();

    }


public void newDoc(String docName, String docType, String docContent){
    if(docName.length()>10 || !File.isValidName(docName)){
        throw new IllegalArgumentException("Invalid file name");
    }
    if (!types.contains(docType)) {
        throw new IllegalArgumentException("Invalid document type. Allowed types are: " + String.join(",", types));
    }
    if (curVD.isFull()) {
        throw new IllegalStateException("Disk is full");
    }
    if (curWorkDir.getFile(docName) != null) {
        throw new IllegalArgumentException("A file or directory with the same name already exists");
    }
    Document newDoc= new Document(docName,docType,docContent);
    curVD.spaceUsed(newDoc.getSize());
    curWorkDir.addFile(newDoc);
    Command command = new newDocCommand(this, newDoc);
    commandManager.putinto(command);
}

public void newDir(String dirName){
    if(dirName.length()>10 || !File.isValidName(dirName)){
        throw new IllegalArgumentException("Invalid file name");
    }
    if (curVD.isFull()) {
        throw new IllegalStateException("Disk is full");
    }
    if (curWorkDir.getFile(dirName) != null) {
        throw new IllegalArgumentException("A file or directory with the same name already exists");
    }
    Directory newDir=new Directory(dirName);
    curVD.spaceUsed(newDir.getSize());
    curWorkDir.addFile(newDir);//add dir in root dir
    Command command = new newDirCommand(this, newDir);
    commandManager.putinto(command);
}

    public void delete(String fileName){
        File targetFile = null;
        for (File file : curWorkDir.getFiles()) {
            if (file.getName().equals(fileName)) {
                targetFile = file;
                break;
            }
        }

        if (targetFile == null) {
            throw new IllegalArgumentException("File does not exist");
        }

        curWorkDir.removeFile(targetFile);
        curVD.spaceRemained(targetFile.getSize());
        System.out.println("File deleted successfully.");
        Command command = new deleteCommand(this, targetFile);
        commandManager.putinto(command);
    }

    public void rename(String oldFileName, String newFileName){
        File targetFile = null;
        for (File file : curWorkDir.getFiles()) {
            if (file.getName().equals(oldFileName)) {
                targetFile = file;
                break;
            }
        }
        if (targetFile == null) {
            throw new IllegalArgumentException("File does not exist");
        }
        if (newFileName.length() > 10 || !File.isValidName(newFileName)) {
            throw new IllegalArgumentException("Invalid new file name");
        }
        if (curWorkDir.getFile(newFileName) != null) {
            throw new IllegalArgumentException("A file or directory with the same name already exists");
        }
        targetFile.setName(newFileName);
        System.out.println("File renamed successfully.");
        Command command = new renameCommand(this, oldFileName, newFileName);
        commandManager.putinto(command);
    }

    public void changeDir(String dirName){
        Directory preDir=getCurrentDir();

        if (dirName.equals("..")) {
            if (curWorkDir.getParent() != null) {
                curWorkDir = curWorkDir.getParent();
                System.out.println("Changed directory to the parent directory.");
            } else {
                System.out.println("Already in the root directory.");
            }
        } else {
            boolean flag=false;
            for (File file : curWorkDir.getFiles()) {
                if (file instanceof Directory && file.getName().equals(dirName)) {
                    curWorkDir = (Directory) file;
                    System.out.println("Changed directory to " + dirName);
                    flag=true;
                    break;
                }
            }
            if(!flag) throw new IllegalArgumentException("Directory not found");
        }
        Command command = new changeDirCommand(this, preDir, getCurrentDir());
        commandManager.putinto(command);
    }

    public void list(){
        List<File> files = curWorkDir.getFiles();
        int totalNumber = 0;
        int totalSize = 0;
        System.out.println("Files in directory " + curWorkDir.getName()+":");
        for (File file : files) {
            if (file instanceof Document) {
                Document doc = (Document) file;
                System.out.printf("%s %s %d bytes%n", doc.getName(), doc.getType(), doc.getSize());
            } else if (file instanceof Directory) {
                Directory dir = (Directory) file;
                System.out.printf("%s %d bytes%n", dir.getName(), dir.getSize());
            }
            totalNumber++;
            totalSize += file.getSize();
        }

        System.out.println("Total files in the working directory: " + totalNumber);
        System.out.println("Total size of the working directory: " + totalSize + " bytes");
    }

    public void rList(){
        List<File> files = curWorkDir.getFiles();
        int totalNumber = 0;
        int totalSize = 0;
        int level=0;
        recursiveListFiles(files, level, totalNumber, totalSize);
    }

    private void recursiveListFiles(List<File> files, int level, int totalNumber, int totalSize){
        for (File file : files) {
            String indentation = "  ".repeat(level);
            if (file instanceof Document) {
                Document doc = (Document) file;
                System.out.printf("%s%s %s %d bytes%n", indentation, doc.getName(), doc.getType(), doc.getSize());
                totalNumber++;
                totalSize += doc.getSize();
            } else if (file instanceof Directory) {
                Directory dir = (Directory) file;
                System.out.printf("%s%s %d bytes%n", indentation, dir.getName(), dir.getSize());
                totalNumber++;
                totalSize += dir.getSize();
                recursiveListFiles(dir.getFiles(), level + 1, totalNumber, totalSize);
            }
        }

        if (level == 0) {
            System.out.println("Total files: " + totalNumber);
            System.out.println("Total size: " + totalSize + " bytes");
        }
    }


/**********************************************************************/

public HashMap<String, Criterion> getCriterionHashMap(){
    return criterionHashMap;
}

public void newSimpleCri(String criName, String attrName, String op, String val){
    if(criterionHashMap.get(criName)!=null) throw new IllegalArgumentException("Criterion " + criName + " already exists.");
    criterionHashMap.put(criName, new SimpleCriterion(criName, attrName, op, val));
    Command command = new newSimpleCriCommand(this, criName, attrName, op, val);
    commandManager.putinto(command);

}

public void isDocument(){
    criterionHashMap.put("IsDocument", new IsDocument());
}

public void newNegation(String criName1, String criName2) {

        Criterion cri2 = criterionHashMap.get(criName2);
        if (cri2 == null) throw new IllegalArgumentException("Criterion " + criName2 + " doesn't exist.");
        if(criterionHashMap.get(criName1)!=null) throw new IllegalArgumentException("Criterion " + criName1 + " already exists.");
        criterionHashMap.put(criName1, new NegationCriterion(criName1, cri2));
    Command command = new newNegationCriCommand(this, criName1, criName2);
    commandManager.putinto(command);

}

public void newBinaryCri(String criName1, String criName3, String logicOp, String criName4){
        Criterion cri3=criterionHashMap.get(criName3);
        Criterion cri4=criterionHashMap.get(criName4);
        if(cri3==null) throw new IllegalArgumentException("Criterion " + criName3 + " doesn't exist.");
        if(cri4==null) throw new IllegalArgumentException("Criterion " + criName4 + " doesn't exist.");
        if(criterionHashMap.get(criName1)!=null) throw new IllegalArgumentException("Criterion " + criName1 + " already exists.");
        criterionHashMap.put(criName1, new BinaryCriterion(criName1, cri3, logicOp, cri4));
}

public void printAllCriteria(){

    if(criterionHashMap.size()==0) System.out.println("No criterion accessible.");
    for (String criName : criterionHashMap.keySet()) {
        System.out.println(criName + ": " + criterionHashMap.get(criName).toString());
    }

}

public List<File> search(String criName) throws IllegalArgumentException{
    List<File> files = new ArrayList<>();
    int size=0;
    Criterion criterion =  this.getCriterionHashMap().get(criName);
    if(criterion==null) throw new IllegalArgumentException("The criterion doesn't exist.");
    for(File file : this.curWorkDir.getFiles()){
        if(criterion.checkFile(file)){
            files.add(file);
            size+=file.getSize();
        }
    }
    int number = files.size();
    System.out.println("Total number of files: " + number + ";" + " Total size of files: " + size + ".");
    return files;
}

public void printSatifiedFile(List<File> files){
    for(File file : files) {
        if (file instanceof Document) {
            Document doc = (Document) file;
            System.out.printf("%s %s %d bytes%n", doc.getName(), doc.getType(), doc.getSize());
        } else if (file instanceof Directory) {
            Directory dir = (Directory) file;
            System.out.printf("%s %d bytes%n", dir.getName(), dir.getSize());
        }
    }
}

public List<File> rSearch(String criName)throws IllegalArgumentException{
    List<File> files = new ArrayList<>();
    Criterion criterion =  criterionHashMap.get(criName);
    if(criterion==null) throw new IllegalArgumentException("The criterion doesn't exist.");
    int size = rSearch(criterion, files, this.curWorkDir, 0);
    int number = files.size();
    System.out.println("Total number of files: " + number + "; Total size of files: " + size + ".");
    return files;
}
private int rSearch(Criterion criteria, List<File> files, Directory curDir,int size){
    if(curDir.getFiles().size()==0) return 0;
    for(File file : curDir.getFiles()) {
        if (file instanceof Directory && ((Directory) file).getFiles().size() != 0)
            size += rSearch(criteria, files, (Directory) file, 0);
        else if (criteria.checkFile(file)) {
            files.add(file);
            size += file.getSize();
        }
    }

    return size;
}



/******************************************************************************/

//we finish save & load & bonus1 in control.Controller.java

public void quit(){
    System.exit(0);
}

/*Bonus2************************************************************************/

    public void undo() {
        commandManager.undo();
    }

    public void redo() {
        commandManager.redo();
    }

}

