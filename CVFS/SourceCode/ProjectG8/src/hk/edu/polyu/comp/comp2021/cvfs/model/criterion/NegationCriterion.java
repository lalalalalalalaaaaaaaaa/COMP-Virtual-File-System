package hk.edu.polyu.comp.comp2021.cvfs.model.criterion;

import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.File;

import java.io.Serializable;

public class NegationCriterion implements Criterion, Serializable {
    private String criName1;
    private Criterion cri2;

    public NegationCriterion(String criName1, Criterion cri2){
        if(!criName1.matches("[a-zA-Z]{2}"))
            throw new IllegalArgumentException("Criterion name must contains exactly two English letters.");
        this.criName1=criName1;
        this.cri2=cri2;
    }

    @Override
    public boolean checkFile(File file) { //override
        return !cri2.checkFile(file);
    }

    public String toString(){
        return "NOT" + " " + cri2.toString();
    }
}
