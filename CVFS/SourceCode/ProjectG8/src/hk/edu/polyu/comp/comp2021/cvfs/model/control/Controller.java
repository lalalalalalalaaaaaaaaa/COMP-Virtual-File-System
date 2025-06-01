package hk.edu.polyu.comp.comp2021.cvfs.model.control;

import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.CVFS;

import java.io.*;

//used for req 15, 16, bonus1
public class Controller implements Serializable {
    CVFS cvfs;

    public Controller(CVFS cvfs){
        this.cvfs=cvfs;
    }
    public void save(String filePath) {
        try {
            FileOutputStream fileOut = new FileOutputStream(filePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
            objectOut.writeObject(cvfs);
            objectOut.close();
            fileOut.close();
            System.out.println("CVFS saved successfully.");
        } catch (Exception e) {
            System.out.println("Error saving virtual disk.");
        }
    }

    public static CVFS load(String filePath) {
        try {
            FileInputStream fileIn = new FileInputStream(filePath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);
            System.out.println("CVFS loaded successfully.");
            return (CVFS) objectIn.readObject();
        } catch (Exception e) {
            System.out.println("Error loading virtual disk: File not found.");
            return null;
        }
    }
}
