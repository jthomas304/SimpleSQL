package edu.gatech.coc.cs6422.group16.frontend.commandLineObjects;

import edu.gatech.coc.cs6422.group16.algebraTree.treeVisualization.UIWindow;

public interface ICommandLineObject
{
    public void execute(UIWindow window);

    public String longHelp();

    public boolean providesLongHelp();

    public boolean providesShortHelp();

    public void setCommand(String command);

    public String shortHelp();
}