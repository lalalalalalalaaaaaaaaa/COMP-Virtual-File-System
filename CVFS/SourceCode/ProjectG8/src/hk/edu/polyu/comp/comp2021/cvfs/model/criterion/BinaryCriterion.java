package hk.edu.polyu.comp.comp2021.cvfs.model.criterion;

import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.File;

import java.io.Serializable;

public class BinaryCriterion implements Criterion, Serializable {
    private String criName1;
    private Criterion cri3;
    private Criterion cri4;
    private String logicOp;


    public BinaryCriterion(String criName1, Criterion cri3, String logicOp, Criterion cri4){
        if(!criName1.matches("[a-zA-Z]{2}"))
            throw new IllegalArgumentException("Criterion name must contains exactly two English letters.");
        if(!(logicOp.equals("&&")||logicOp.equals("||")))
            throw new IllegalArgumentException("LogicOp is either && or ||");
        this.criName1=criName1;
        this.cri3=cri3;
        this.logicOp=logicOp;
        this.cri4=cri4;

    }

    @Override
    public boolean checkFile(File file){
        switch(logicOp){
            case("&&"):
                return cri3.checkFile(file) && cri4.checkFile(file);
            case("||"):
                return cri3.checkFile(file) || cri4.checkFile(file);
        }
        return false;
    }

    public String toString(){
        return cri3.toString() + " " + logicOp + " " + cri4.toString();
    }

}

