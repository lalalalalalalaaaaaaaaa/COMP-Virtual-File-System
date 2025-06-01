package hk.edu.polyu.comp.comp2021.cvfs;

import hk.edu.polyu.comp.comp2021.cvfs.model.control.CommandLineInterface;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.CVFS;


public class Application {

    public static void main(String[] args){
        CVFS cvfs = new CVFS(10000);
        // Initialize and utilize the system
        CommandLineInterface cli = new CommandLineInterface(cvfs);
        cli.start();
    }
}