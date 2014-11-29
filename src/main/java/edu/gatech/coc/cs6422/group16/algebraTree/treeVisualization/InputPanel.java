package edu.gatech.coc.cs6422.group16.algebraTree.treeVisualization;

import edu.gatech.coc.cs6422.group16.frontend.CommandLineInterface;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;

/**
 * Created by Rogue Three on 11/25/2014.
 */
public class InputPanel extends JPanel implements ActionListener {
    private JButton execute = new JButton("Execute");
    private JTextPane textInput;
    private CommandLineInterface mainCMD;

    public InputPanel(CommandLineInterface cmd) {
        mainCMD = cmd;

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        textInput = new JTextPane();
        textInput.setPreferredSize(new Dimension(300, 150));
        textInput.setMaximumSize(new Dimension(300, 150));
        //textInput.setMaximumSize(new Dimension(600, 300));

        execute.addActionListener(this);

        add(textInput);
        add(execute);
    }


    public void actionPerformed(ActionEvent event) {

        // when url box is modified and then enter is pressed
        if(event.getSource() == execute) {
            try {
                mainCMD.processString(textInput.getText());
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.exit(1);
            }
        }
    }

}
