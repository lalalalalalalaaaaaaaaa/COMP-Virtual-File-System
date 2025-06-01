package hk.edu.polyu.comp.comp2021.cvfs.model.command;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.*;

import java.io.Serializable;

public class newDirCommand implements Command, Serializable {
    private CVFS cvfs;
    private Directory newDir;

    public newDirCommand(CVFS cvfs, File newDir) {
        this.cvfs = cvfs;
        this.newDir=(Directory) newDir;
    }

    @Override
    public void undo() {
        cvfs.getCurrentDir().removeFile(newDir);
    }

    @Override
    public void redo() {
        cvfs.getCurrentDir().addFile(newDir);
    }
}