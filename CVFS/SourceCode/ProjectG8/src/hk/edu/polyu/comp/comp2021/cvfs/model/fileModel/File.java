package hk.edu.polyu.comp.comp2021.cvfs.model.fileModel;

import java.io.Serializable;

public abstract class File implements Serializable {
    protected String name; //
    protected String type; //
    protected Directory parent;

    public File(String name) {
        setName(name);
    }

    public File(){};

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public abstract int getSize();

    static boolean isValidName(String name) {
        return name.matches("[a-zA-Z0-9]+");
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

}