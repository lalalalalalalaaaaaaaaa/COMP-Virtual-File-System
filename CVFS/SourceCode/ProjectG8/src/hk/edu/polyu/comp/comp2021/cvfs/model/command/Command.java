package hk.edu.polyu.comp.comp2021.cvfs.model.command;

public interface Command {

    void undo();
    void redo();
}
