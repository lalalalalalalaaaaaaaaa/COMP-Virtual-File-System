package hk.edu.polyu.comp.comp2021.cvfs.model.command;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.*;
import hk.edu.polyu.comp.comp2021.cvfs.model.criterion.*;

import java.io.Serializable;


public class newSimpleCriCommand implements Command, Serializable {
    private CVFS cvfs;
    private String criName;
    private String attrName;
    private String op;
    private String val;
    private SimpleCriterion createdCriterion;

    public newSimpleCriCommand(CVFS cvfs, String criName, String attrName, String op, String val) {
        this.cvfs = cvfs;
        this.criName = criName;
        this.attrName = attrName;
        this.op = op;
        this.val = val;
    }

    @Override
    public void undo() {
        cvfs.getCriterionHashMap().remove(criName);
    }

    @Override
    public void redo() {
        cvfs.newSimpleCri(criName, attrName, op, val);
    }
}
