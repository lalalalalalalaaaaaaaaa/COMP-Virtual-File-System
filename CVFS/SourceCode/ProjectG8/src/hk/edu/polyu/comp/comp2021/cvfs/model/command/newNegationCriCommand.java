package hk.edu.polyu.comp.comp2021.cvfs.model.command;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.*;
import hk.edu.polyu.comp.comp2021.cvfs.model.criterion.*;

import java.io.Serializable;

public class newNegationCriCommand implements Command, Serializable {
    private CVFS cvfs;
    private String criName1;
    private NegationCriterion createdCriterion;
   private String criName2;
    public newNegationCriCommand(CVFS cvfs, String criName1, String criName2){
        this.cvfs = cvfs;
        this.criName1 = criName1;
        this.criName2=criName2;
    }


    @Override
    public void undo() {
        cvfs.getCriterionHashMap().remove(criName1);
    }

    @Override
    public void redo() {
        cvfs.newNegation(criName1, criName2);
    }
}
