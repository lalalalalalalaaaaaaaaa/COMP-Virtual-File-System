package hk.edu.polyu.comp.comp2021.cvfs.model.command;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.*;

import java.io.Serializable;

public class newDocCommand implements Command, Serializable {
    private CVFS cvfs;
    private Document newDoc;



    public newDocCommand(CVFS cvfs, File doc){
        this.cvfs = cvfs;
        this.newDoc=(Document) doc;
    }


    @Override
    public void undo() {
        cvfs.getCurrentDir().removeFile(newDoc);

    }

    @Override
    public void redo() {
        cvfs.getCurrentDir().addFile(newDoc);
    }

}