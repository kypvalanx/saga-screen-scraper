package swse.editor;

import javax.swing.*;
import java.awt.*;

public class Editor {
    public static void main(String[] args) {
        Editor editor = new Editor();
    }

    public Editor(){
        JFrame frame = new JFrame("Dataset Editor");

        // 400 width and 500 height
        int width = 1920;
        int height = 1080;


        frame.setSize(width, height);

        // using no layout managers
        frame.setLayout(new FlowLayout());

        JTree dataTree = new JTree();
        dataTree.setSize(width/3, height);

        frame.add(dataTree);

        JPanel recordDisplay = new JPanel();
        recordDisplay.setSize(2*width/3, height);
        frame.add(recordDisplay);



        // making the frame visible
        frame.setVisible(true);
    }
}
