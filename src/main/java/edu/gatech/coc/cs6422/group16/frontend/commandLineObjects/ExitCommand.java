package edu.gatech.coc.cs6422.group16.frontend.commandLineObjects;

import edu.gatech.coc.cs6422.group16.algebraTree.treeVisualization.UIWindow;

public class ExitCommand implements ICommandLineObject
{
    @Override
    public void execute(UIWindow window)
    {
    }

    @Override
    public String longHelp()
    {
        return null;
    }

    @Override
    public boolean providesLongHelp()
    {
        return false;
    }

    @Override
    public boolean providesShortHelp()
    {
        return true;
    }

    @Override
    public void setCommand(String command)
    {
    }

    @Override
    public String shortHelp()
    {
        return "exit\tExits the command-line\tSynonym: q";
    }
}