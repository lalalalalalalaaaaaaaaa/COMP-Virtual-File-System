package hk.edu.polyu.comp.comp2021.cvfs.model.command;

import java.io.Serializable;
import java.util.Deque;
import java.util.LinkedList;

public class CommandManager implements Serializable {
    private Deque<Command> history = new LinkedList<>();
    private Deque<Command> redoStack = new LinkedList<>();

    public void putinto(Command command){
        history.addLast(command);
        redoStack.clear();
    }

    public void undo() {
        if (!history.isEmpty()) {
            Command command = history.removeLast();
            command.undo();
            redoStack.addLast(command);
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            Command command = redoStack.removeLast();
            command.redo();
            history.addLast(command);
        }
    }
}
