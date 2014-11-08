package edu.gatech.coc.cs6422.group16.frontend.commandLineObjects;

import edu.gatech.coc.cs6422.group16.algebraTree.*;
import edu.gatech.coc.cs6422.group16.algebraTree.treeVisualization.SwingRelationAlgebraTree;
import edu.gatech.coc.cs6422.group16.executionConfiguration.ExecutionConfig;
import edu.gatech.coc.cs6422.group16.heuristics.CartesianToJoin;
import edu.gatech.coc.cs6422.group16.heuristics.PushSelectionDown;
import edu.gatech.coc.cs6422.group16.metaDataRepository.MetaDataRepository;
import edu.gatech.coc.cs6422.group16.statistics.Statistics;
import edu.gatech.coc.cs6422.group16.statistics.TimerType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class ProcessQueryCommand implements ICommandLineObject
{
    private static void optimizeQueries(List<RelationalAlgebraTree> trees) {
        if (trees != null) {
            ExecutionConfig config = ExecutionConfig.getInstance();
            Statistics stat = Statistics.getInstance();
            stat.addQueryTree(trees.get(0));
            if (config.isEnableHeuristics()) {
                stat.start(TimerType.OPTIMIZATION);
                // trees.get(0) has unoptimized tree, we need to optimize it
                int numberOfRelationPermutations = trees.size();

                /*
                for(int i = 0; i < numberOfRelationPermutations; i++) {
                    SwingRelationAlgebraTree.showInDialog(trees.get(i), "Tree" + Integer.toString(i));
                }
                */

                /*
                //TODO: Find a reliable algorithm to find number of relations from permutations
                int numberOfRelations = 0;
                switch (numberOfRelationPermutations) {
                    case 1: numberOfRelations = 1;
                        break;
                    case 2: numberOfRelations = 2;
                        break;
                    case 6: numberOfRelations = 3;
                        break;
                    default: numberOfRelations = 0;
                        break;
                }
                */

                List<RelationalAlgebraTree> cartNodes = new ArrayList<>();
                getAllCartTypes(trees.get(0), cartNodes);
                int numberOfJoins = cartNodes.size();


                for(int i = 0; i < numberOfRelationPermutations; i++) {
                    RelationalAlgebraTree singleTree = trees.get(i).copyNode();
                    PushSelectionDown.pushSelectionDown(singleTree);
                    CartesianToJoin.cartesianToJoin(singleTree);

                    for (int j = 0; j < numberOfJoins; j++) {
                        // k dependent on how many types of joins we implement
                        List<RelationalAlgebraTree> partialTree = new ArrayList<>();


                        for (int k = 0; k < 3; k++) {

                            if (j == 0) {
                                RelationalAlgebraTree newCopy = singleTree.copyNode();
                                // get jth join
                                List<RelationalAlgebraTree> joinNodes = new ArrayList<>();
                                getAllJoinTypes(newCopy, joinNodes);

                                JoinNode temp = joinNodes.get(j).getCurrentNodeAs(JoinNode.class);

                                if (k == 0) {
                                    temp.replaceNode(temp.toBNLJoin());
                                }
                                else if (k == 1) {
                                    temp.replaceNode(temp.toINLJoin());
                                }
                                else if (k == 2) {
                                    temp.replaceNode(temp.toMJoin());
                                }
                                partialTree.add(newCopy);
                            }
                            else {
                                int count = partialTree.size();
                                for (int l = 0; l < count; l++) {
                                    RelationalAlgebraTree newCopy = partialTree.get(l).copyNode();
                                    // get jth join
                                    List<RelationalAlgebraTree> joinNodes = new ArrayList<>();
                                    getAllJoinTypes(newCopy, joinNodes);

                                    JoinNode temp = joinNodes.get(j).getCurrentNodeAs(JoinNode.class);

                                    if (k == 0) {
                                        temp.replaceNode(temp.toBNLJoin());
                                    }
                                    else if (k == 1) {
                                        temp.replaceNode(temp.toINLJoin());
                                    }
                                    else if (k == 2) {
                                        temp.replaceNode(temp.toMJoin());
                                    }
                                    partialTree.add(newCopy);
                                    partialTree.remove(partialTree.get(l));
                                }
                            }

                        }
                        trees.addAll(partialTree);
                    }
                }

/*
                RelationalAlgebraTree singleTree = trees.get(0).copyNode();
                PushSelectionDown.pushSelectionDown(singleTree);
                CartesianToJoin.cartesianToJoin(singleTree);
*/
                //TODO: Rearrange joins to get most optimal order

                /*
                for (int i = 0; i < 3; i++) {
                    RelationalAlgebraTree singleTree = trees.get(0).copyNode();
                    if (i == 0) {
                        PushSelectionDown.pushSelectionDown(singleTree);
                    }
                    if (i == 1) {
                        CartesianToJoin.cartesianToJoin(singleTree);
                    }
                    if (i == 2) {
                        PushSelectionDown.pushSelectionDown(singleTree);
                        CartesianToJoin.cartesianToJoin(singleTree);
                    }
                    stat.addQueryTree(singleTree);
                }
                */
                stat.stop(TimerType.OPTIMIZATION);

                for(int i = 0; i < trees.size(); i++) {
                    SwingRelationAlgebraTree.showInDialog(trees.get(i), "Tree" + Integer.toString(i));
                }
            }

            /*
            // add all trees to statistics-module:
            for (RelationalAlgebraTree singleTree : trees)
            {
                stat.addQueryTree(singleTree);
            }
            */
        }
    }


    private static void getAllCartTypes(RelationalAlgebraTree current, List<RelationalAlgebraTree> nodeList) {
        if (current.isClass(CartesianProductNode.class))
        {
            nodeList.add(current);
        }
        for (RelationalAlgebraTree child : current.getChildren())
        {
            getAllCartTypes(child, nodeList);
        }
    }

    private static void getAllJoinTypes(RelationalAlgebraTree current, List<RelationalAlgebraTree> nodeList) {
        if (current.isClass(JoinNode.class))
        {
            nodeList.add(current);
        }
        for (RelationalAlgebraTree child : current.getChildren())
        {
            getAllJoinTypes(child, nodeList);
        }
    }

    @Override
    public void execute()
    {
        ExecutionConfig config = ExecutionConfig.getInstance();

        Statistics stat = Statistics.getInstance();
        stat.start(TimerType.FULL);
        List<RelationalAlgebraTree> trees = null;
        // parseTree creates trees
        try
        {
            trees = this.parseTree();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        MetaDataRepository.GetInstance().ReadData();

        // only continue on valid trees:
        if (trees != null)
        {
            if (config.isShowVisualTrees() && config.isShowVisualFirstTree())
            {
                RelationalAlgebraTree t0 = trees.get(0).copyNode();
                SwingRelationAlgebraTree.showInDialog(t0, "Unoptimized Tree");
            }

            optimizeQueries(trees);

            /*
            if (config.isShowVisualTrees() && config.isShowVisualFirstTree())
            {
                RelationalAlgebraTree t1 = trees.get(0).copyNode();
                SwingRelationAlgebraTree.showInDialog(t1, "First tree - Optimized");
            }
            */

            stat.stop(TimerType.FULL);
            stat.print();
        }

        // clean-up after we are done:
        stat.reset();
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

    protected abstract List<RelationalAlgebraTree> parseTree() throws IOException;
}
