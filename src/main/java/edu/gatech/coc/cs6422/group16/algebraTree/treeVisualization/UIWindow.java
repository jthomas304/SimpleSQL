package edu.gatech.coc.cs6422.group16.algebraTree.treeVisualization;

import edu.gatech.coc.cs6422.group16.frontend.CommandLineInterface;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Rogue Three on 11/25/2014.
 */
public class UIWindow extends JFrame {
    private JButton execute = new JButton("Execute");
    public InputPanel inputPanel;
    private JLabel parTime, valTime, treeTime, optTime, totTime, statement;
    private TextInBoxTreePane treeComp;
    private JPanel bot;

    public UIWindow(CommandLineInterface cmd) {
        // call the command-line-interface:
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container pane = getContentPane();
        pane.setLayout(new BoxLayout(pane, BoxLayout.Y_AXIS));


        JPanel top = new JPanel();


        top.setLayout(new BoxLayout(top, BoxLayout.X_AXIS));

        inputPanel = new InputPanel(cmd);
        //inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));

        //JTextPane textInput = new JTextPane();
        //textInput.setMaximumSize(new Dimension(600, 300));
        //JButton execute = new JButton("Execute");
        //execute.addActionListener(this);

        //inputPanel.add(textInput);
        //inputPanel.add(execute);

        top.add(Box.createHorizontalStrut(50));
        top.add(inputPanel);
        top.add(Box.createHorizontalStrut(50));

        JPanel stats = new JPanel();
        stats.setLayout(new BoxLayout(stats, BoxLayout.X_AXIS));

        JPanel leftStats = new JPanel();
        JPanel rightStats = new JPanel();

        leftStats.setLayout(new BoxLayout(leftStats, BoxLayout.Y_AXIS));

        leftStats.add(new JLabel("Parse Time:"));
        leftStats.add(new JLabel("Validation Time:"));
        leftStats.add(new JLabel("Tree Creation Time:"));
        leftStats.add(new JLabel("Optimization Time:"));
        //leftStats.add(new JLabel("Total Processing Time:"));

        rightStats.setLayout(new BoxLayout(rightStats, BoxLayout.Y_AXIS));

        parTime = new JLabel();
        valTime = new JLabel();
        treeTime = new JLabel();
        optTime = new JLabel();
        //totTime = new JLabel();

        rightStats.add(parTime);
        rightStats.add(valTime);
        rightStats.add(treeTime);
        rightStats.add(optTime);
        //rightStats.add(totTime);

        stats.add(leftStats);
        stats.add(rightStats);

        top.add(stats);
        top.add(Box.createGlue());

        //pane.add(Box.createVerticalStrut(10));
        pane.add(top);
        //pane.add(Box.createVerticalStrut(15));
        pane.add(Box.createVerticalGlue());

        bot = new JPanel();
        bot.setLayout(new BoxLayout(bot, BoxLayout.Y_AXIS));
        bot.setPreferredSize(new Dimension(694,700));
        bot.setMaximumSize(new Dimension(694,700));

        statement = new JLabel("");

        //bot.add(Box.createGlue());

        JPanel botStatement = new JPanel();
        botStatement.setLayout(new BoxLayout(botStatement, BoxLayout.X_AXIS));

        botStatement.add(Box.createGlue());
        botStatement.add(statement);
        botStatement.add(Box.createGlue());

        bot.add(Box.createGlue());
        bot.add(botStatement);


        pane.add(bot);
        //pane.add(Box.createVerticalStrut(10));

        //Display the window
        //pack();
        //setVisible(true);
    }

    public void updateStats(long tot, long parse, long valid, long tree, long opt) {

        //System.out.println(parse);
        parTime.setText(Long.toString(parse)+" ms");
        valTime.setText(Long.toString(valid)+" ms");
        treeTime.setText(Long.toString(tree)+" ms");
        optTime.setText(Long.toString(opt)+" ms");
        //totTime.setText(Long.toString(tot)+" ms");
        revalidate();
    }

    public void updateTree(TextInBoxTreePane tree) {
        if (bot.getComponentCount() > 2) {
            JPanel botTree = new JPanel();
            botTree.setLayout(new BoxLayout(botTree, BoxLayout.X_AXIS));
            botTree.add(Box.createGlue());
            botTree.add(tree);
            botTree.add(Box.createGlue());

            bot.remove(1);
            bot.add(botTree,1);
        }
        else {
            JPanel botTree = new JPanel();
            botTree.setLayout(new BoxLayout(botTree, BoxLayout.X_AXIS));
            botTree.add(Box.createGlue());
            botTree.add(tree);
            botTree.add(Box.createGlue());

            bot.add(botTree,1);
        }

        revalidate();
        System.out.println(bot.size());
    }

    public void updateAlg(String alg) {
        statement.setText(alg);
        revalidate();
    }
}