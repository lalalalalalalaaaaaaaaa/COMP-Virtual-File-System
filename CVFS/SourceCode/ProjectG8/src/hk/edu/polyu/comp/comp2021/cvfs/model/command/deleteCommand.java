package hk.edu.polyu.comp.comp2021.cvfs.model.command;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.*;

import java.io.Serializable;

public class deleteCommand implements Command, Serializable {
    private CVFS cvfs;
    private File targetFile;

    public deleteCommand(CVFS cvfs, File targetFile) {
        this.cvfs = cvfs;
        this.targetFile=targetFile;
    }

    @Override
    public void undo() {
        cvfs.getCurrentDir().addFile(targetFile);
    }

    @Override
    public void redo() {
        cvfs.getCurrentDir().removeFile(targetFile);
    }
}