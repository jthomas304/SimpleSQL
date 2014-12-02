package edu.gatech.coc.cs6422.group16.frontend.commandLineObjects;
import edu.gatech.coc.cs6422.group16.algebraTree.*;
import edu.gatech.coc.cs6422.group16.algebraTree.RelationalAlgebraTree;
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
    private static void optimizeQueries(List<RelationalAlgebraTree> trees)
    {
        if (trees != null)
        {
            ExecutionConfig config = ExecutionConfig.getInstance();
            Statistics stat = Statistics.getInstance();
            if (config.isEnableHeuristics())
            {
                stat.start(TimerType.OPTIMIZATION);
                int numberOfRelationPermutations = trees.size();
                List<RelationalAlgebraTree> cartNodes = new ArrayList<>();



                for(int i = 0; i < numberOfRelationPermutations; i++) {
                    RelationalAlgebraTree singleTree = trees.get(i).copyNode();
                    PushSelectionDown.pushSelectionDown(singleTree);
                    CartesianToJoin.cartesianToJoin(singleTree);
                    List<RelationalAlgebraTree> joinNodes = new ArrayList<>();
                    getAllJoinTypes(singleTree, joinNodes);
                    int numberOfJoins = joinNodes.size();

                    for (int j = 0; j < numberOfJoins; j++) {
                        // k dependent on how many types of joins we implement
                        List<RelationalAlgebraTree> partialTree = new ArrayList<>();


                        for (int k = 0; k < 4; k++) {

                            if (j == 0) {
                                RelationalAlgebraTree newCopy = singleTree.copyNode();
                                // get jth join
                                joinNodes = new ArrayList<>();
                                getAllJoinTypes(newCopy, joinNodes);

                                JoinNode temp = joinNodes.get(j).getCurrentNodeAs(JoinNode.class);
                                temp.replaceNode(temp.toSpecificJoin(k));
                                partialTree.add(newCopy);
                            }
                            else {
                                int count = partialTree.size();
                                for (int l = 0; l < count; l++) {
                                    RelationalAlgebraTree newCopy = partialTree.get(l).copyNode();
                                    // get jth join
                                    joinNodes = new ArrayList<>();
                                    getAllJoinTypes(newCopy, joinNodes);
                                    JoinNode temp = joinNodes.get(j).getCurrentNodeAs(JoinNode.class);
                                    temp.replaceNode(temp.toSpecificJoin(k));
                                    partialTree.add(newCopy);
                                    partialTree.remove(partialTree.get(l));
                                }
                            }

                        }
                        trees.addAll(partialTree);
                        for (RelationalAlgebraTree aTree : partialTree) {
                            stat.addQueryTree(aTree);
                        }

                    }

                }
                stat.stop(TimerType.OPTIMIZATION);
            }
            config.setShowVisualTrees(true);
            config.setShowCostsInVisualTree(true);
            for(int i = 0; i < trees.size(); i++) {

                SwingRelationAlgebraTree.showInDialog(trees.get(i), "Tree" + Integer.toString(i));
            }

        // add all trees to statistics-module:
            for (RelationalAlgebraTree singleTree : trees)
            {   System.out.println("Test 140 + single tree got added to the query tree: " + singleTree);
                stat.addQueryTree(singleTree);
            }
        }
    }

    @Override
    public void execute()
    {
        ExecutionConfig config = ExecutionConfig.getInstance();
        Statistics stat = Statistics.getInstance();
        stat.start(TimerType.FULL);
        List<RelationalAlgebraTree> trees = null;
        try
        {
            trees = this.parseTree();
            System.out.println(" Test 141: parse trees :" + trees);
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
                System.out.println(trees.get(0) + "Test 20");
                SwingRelationAlgebraTree.showInDialog(t0, "First tree - Unoptimized");
            }

            optimizeQueries(trees);

            if (config.isShowVisualTrees() && config.isShowVisualFirstTree())
            {
                RelationalAlgebraTree t1 = trees.get(0).copyNode();
                System.out.println(trees.get(0) + "Test 21");
                SwingRelationAlgebraTree.showInDialog(t1, "First tree - Optimized");
            }

            stat.stop(TimerType.FULL);
            stat.print();
        }
        System.out.println(" Test 37");
        // clean-up after we are done:
        stat.reset();
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
