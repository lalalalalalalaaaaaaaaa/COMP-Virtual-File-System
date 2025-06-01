package hk.edu.polyu.comp.comp2021.cvfs.model.criterion;

import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.Document;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.File;

import java.io.Serializable;

public class IsDocument implements Criterion, Serializable {

    @Override
    public boolean checkFile(File file){ //override
        return file instanceof Document;
    }

    public String toString(){
        return "NOT type equals \"directory\"";
    }

}
