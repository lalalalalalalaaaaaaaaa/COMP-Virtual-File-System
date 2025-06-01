package hk.edu.polyu.comp.comp2021.cvfs.model.fileModel;

import java.io.Serializable;

public class Document extends File implements Serializable {
    private String content;

    public Document(String name, String type, String content) {
        super(name);
        setType(type);
        setContent(content);
    }


    public String getType() {
        return type;
    }
    public void setType(String type){
        this.type = type;
    }

    public String getContent(){
        return content;
    }

    public void setContent(String content){
        this.content = content;

    }

    @Override
    public int getSize(){
        return 40+content.length()*2;
    }


}