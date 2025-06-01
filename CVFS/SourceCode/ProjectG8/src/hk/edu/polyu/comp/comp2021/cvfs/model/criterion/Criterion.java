package hk.edu.polyu.comp.comp2021.cvfs.model.criterion;

import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.File;

public interface Criterion {

    boolean checkFile(File file);

    String toString();

}
