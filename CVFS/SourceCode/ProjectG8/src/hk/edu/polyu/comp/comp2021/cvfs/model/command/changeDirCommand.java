package hk.edu.polyu.comp.comp2021.cvfs.model.command;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.*;

import java.io.Serializable;

public class changeDirCommand implements Command, Serializable {
    private CVFS cvfs;
    private Directory previousDir;
    private Directory targetDir;

    public changeDirCommand(CVFS cvfs, Directory previousDir, Directory targetDir) {
        this.cvfs = cvfs;
        this.previousDir=previousDir;
        this.targetDir = targetDir;
    }


    @Override
    public void undo() {
        cvfs.setCurWorkDir(previousDir);
    }

    @Override
    public void redo() {
        cvfs.setCurWorkDir(targetDir);
    }
}
