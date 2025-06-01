package hk.edu.polyu.comp.comp2021.cvfs.model.fileModel;

import java.io.Serializable;

public class VirtualDisk implements Serializable {
    private int maxSize;
    private int usedSize;
    private Directory rootDir;

    public VirtualDisk(int maxSize) {

        setMaxSize(maxSize);
        this.usedSize=0;
        this.rootDir = new Directory("/");
    }

    public int getMaxSize(){
        return maxSize;
    }

    public void setMaxSize(int maxSize){
        this.maxSize = maxSize;
    }

    public void spaceUsed(int size) {
        if (isFull() || usedSize + size > maxSize) {
            throw new IllegalStateException("Disk is full");
        }
        usedSize += size;
    }

    public int getUsedSize() {return usedSize;}

    public boolean isFull() {
        return usedSize >= maxSize;
    }

    public void spaceRemained(int size){
        usedSize -= size;
    }

    public Directory getRootDir(){
        return rootDir;
    }

}