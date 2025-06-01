package hk.edu.polyu.comp.comp2021.cvfs.model.fileModel;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Directory extends File implements Serializable {
    private List<File> files;
    private Directory parent;

    public Directory(String name){
        super(name);
        this.files = new ArrayList<>();
    }

    public Directory(){
        super();
    }

    public List<File> getFiles(){
        return files;
    }

    public File getFile(String fileName){
        for(File file: this.getFiles()){
            if(file.getName().equals(fileName)) return file;
        }
        return null;
    }

    public Directory getParent() {
        return parent;
    }

    public void setParent(Directory parent) {
        this.parent = parent;
    }

    public void addFile(File file){
        files.add(file);
        file.setParent(this);
    }

    public void removeFile(File file){
        files.remove(file);
    }

    @Override
    public int getSize() { //override
        int size=0;
        for (File file:files) {
            size+= file.getSize(); //dynamic binding
        }
        return 40+size;
    }

}