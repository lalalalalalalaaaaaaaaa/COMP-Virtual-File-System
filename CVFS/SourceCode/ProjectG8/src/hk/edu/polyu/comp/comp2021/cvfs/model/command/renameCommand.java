package hk.edu.polyu.comp.comp2021.cvfs.model.command;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.*;

import java.io.Serializable;

public class renameCommand implements Command, Serializable {
    private CVFS cvfs;
    private String oldFileName;
    private String newFileName;
    private File renamedFile;

    public renameCommand(CVFS cvfs, String oldFileName, String newFileName) {
        this.cvfs = cvfs;
        this.oldFileName = oldFileName;
        this.newFileName = newFileName;
    }

    @Override
    public void undo() {
        renamedFile=cvfs.getCurrentDir().getFile(newFileName);
        if (renamedFile != null) {
            renamedFile.setName(oldFileName);
        }
    }

    @Override
    public void redo() {
        renamedFile=cvfs.getCurrentDir().getFile(oldFileName);
        if (renamedFile != null) {
            renamedFile.setName(newFileName);
        }
    }
}

