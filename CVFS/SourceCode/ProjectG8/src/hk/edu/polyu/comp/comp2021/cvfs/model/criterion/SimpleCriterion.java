package hk.edu.polyu.comp.comp2021.cvfs.model.criterion;

import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.Directory;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.Document;
import hk.edu.polyu.comp.comp2021.cvfs.model.fileModel.File;

import java.io.Serializable;

public class SimpleCriterion implements Criterion, Serializable {
    private String criName;
    private String attrName;
    private String op;
    private String val;
    /*
    * Command: newSimpleCri criName attrName op val
    * Effect: Construct a simple criterion that can be referenced by criName.
    * A criName contains exactly two English letters, and attrName is either name, type, or size.
    * If attrName is name, op must be contains and val must be a string in the double quote;
    * If attrName is type, op must be equals and val must be a string in the double quote;
    * If attrName is size, op can be >, <, >=, <=, ==, or !=, and val must be an integer
    *
    * */

    public SimpleCriterion(String criName, String attrName, String op, String val) throws IllegalArgumentException{
        if(!criName.matches("[a-zA-Z]{2}"))
            throw new IllegalArgumentException("Criterion name must contains exactly two English letters.");
        if(!(attrName.equals("name")||attrName.equals("type")||attrName.equals("size")))
            throw new IllegalArgumentException("Attribute name must be either name, type or size.");
        if(attrName.equals("name")){
            if(!op.equals("contains")) throw new IllegalArgumentException("Attribute is name, Op must be contains.");
            if(!(val.startsWith("\"") && val.endsWith("\"")))  throw new IllegalArgumentException("Attribute is name, val must be a string in the double quote.");
        }
        if(attrName.equals("type")){
            if(!op.equals("equals")) throw new IllegalArgumentException("Attribute is type, Op must be equals.");
            if(!(val.startsWith("\"") && val.endsWith("\"")))  throw new IllegalArgumentException("Attribute is type, val must be a string in the double quote.");
        }
        if(attrName.equals("size")){
            if(!(op.equals(">")||op.equals("<")||op.equals(">=")||op.equals("<=")||op.equals("==")||op.equals("!=")))
                throw new IllegalArgumentException(("Attribute is size, Op must be >, <, >=, <=, ==, or !=."));
            if(!val.matches("\\d+")) throw new IllegalArgumentException("Attribute is size, val must be an integer.");
        }

        this.criName=criName;
        this.attrName=attrName;
        this.op=op;
        this.val=val;
    }


    @Override
    public boolean checkFile(File file) {
        switch (attrName) {
            case "name":
                String val2=val.substring(1, val.length() - 1);
                return file.getName().contains(val2);
            case "type":
                if(file instanceof Directory) return false;
                String val3=val.substring(1, val.length() - 1);
                    Document document =(Document) file;
                    return document.getType().equals(val3);
                    //return file.getType().equals(val3);
            case "size":
                int size = Integer.parseInt(val);
                int fileSize = file.getSize();
                switch (op) {
                    case ">":
                        return fileSize > size;
                    case "<":
                        return fileSize < size;
                    case ">=":
                        return fileSize >= size;
                    case "<=":
                        return fileSize <= size;
                    case "==":
                        return fileSize == size;
                    case "!=":
                        return fileSize != size;
                }
        }
        return false;
    }

    public String toString(){
        return attrName +" " + op +" "+ val;
    }

}
