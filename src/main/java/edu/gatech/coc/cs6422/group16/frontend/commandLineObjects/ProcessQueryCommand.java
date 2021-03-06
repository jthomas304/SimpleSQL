package edu.gatech.coc.cs6422.group16.frontend.commandLineObjects;
import edu.gatech.coc.cs6422.group16.algebraTree.CartesianProductNode;
import edu.gatech.coc.cs6422.group16.algebraTree.JoinNode;
import edu.gatech.coc.cs6422.group16.algebraTree.RelationalAlgebraTree;
import edu.gatech.coc.cs6422.group16.algebraTree.treeVisualization.SwingRelationAlgebraTree;
import edu.gatech.coc.cs6422.group16.algebraTree.treeVisualization.UIWindow;
import edu.gatech.coc.cs6422.group16.executionConfiguration.ExecutionConfig;
import edu.gatech.coc.cs6422.group16.heuristics.CartesianToJoin;
import edu.gatech.coc.cs6422.group16.heuristics.PushSelectionDown;
import edu.gatech.coc.cs6422.group16.heuristics.PushProjectionDown;
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


                SwingRelationAlgebraTree.showInDialog(trees.get(0), "Unoptimized Tree");
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

                //RelationalAlgebraTree copyIt = trees.get(0).copyNode();
                //GreedyAlgorithm.TransformViaGreedyAlgorithm(copyIt);
                //SwingRelationAlgebraTree.showInDialog(copyIt, "Greedy");


                // Go through each table order permutation
                for(int i = 0; i < numberOfRelationPermutations; i++) {
                    RelationalAlgebraTree singleTree = trees.get(i).copyNode();
                    PushSelectionDown.pushSelectionDown(singleTree);
                    CartesianToJoin.cartesianToJoin(singleTree);
                    PushProjectionDown.pushProjectionDown(singleTree);

                    List<RelationalAlgebraTree> oriJoinNodes = new ArrayList<>();
                    getAllJoinTypes(singleTree, oriJoinNodes);
                    int numberOfJoins = oriJoinNodes.size();
                    System.out.println(numberOfJoins);

                    //SwingRelationAlgebraTree.showInDialog(singleTree, "Tree");

                    List<RelationalAlgebraTree> partialTree = new ArrayList<>();

                    //trees.add(singleTree);

                    // for each cartesian product node in the unoptimized algebra tree
                    for (int j = 0; j < numberOfJoins; j++) {
                        // k dependent on how many types of joins we implement

                        int count = (int)(Math.pow(4,j));
                        for (int k = 0; k < 4; k++) {

                            if (j == 0) {
                                RelationalAlgebraTree newCopy = singleTree.copyNode();
                                // get jth join
                                List<RelationalAlgebraTree> joinNodes = new ArrayList<>();
                                getAllJoinTypes(newCopy, joinNodes);

                                JoinNode temp = joinNodes.get(0).getCurrentNodeAs(JoinNode.class);
                                temp.replaceNode(temp.toSpecificJoin(k));
                                //SwingRelationAlgebraTree.showInDialog(newCopy,"Tree" + Integer.toString(k));
                                partialTree.add(newCopy);
                            }
                            else {
                                //int count = partialTree.size();
                                for (int l = 0; l < count; l++) {
                                    RelationalAlgebraTree newCopy = partialTree.get(l).copyNode();
                                    // get jth join
                                    List<RelationalAlgebraTree> joinNodes = new ArrayList<>();
                                    getAllJoinTypes(newCopy, joinNodes);


                                    //System.out.println("Inner: " + Integer.toString(l) + " Num nodes: " +Integer.toString(joinNodes.size()));

                                    JoinNode temp = joinNodes.get(0).getCurrentNodeAs(JoinNode.class);
                                    temp.replaceNode(temp.toSpecificJoin(k));
                                    partialTree.add(count, newCopy);
                                    //partialTree.remove(partialTree.get(l));

//                                    if ((i == 0) && (j == 1) && (k == 0) && (l == 0)) {
//                                        SwingRelationAlgebraTree.showInDialog(partialTree.get(l), "Tester1");
//                                        SwingRelationAlgebraTree.showInDialog(partialTree.get(l).copyNode(), "Copy");
//                                        SwingRelationAlgebraTree.showInDialog(newCopy, "Tester");
//                                    }


                                }
                            }

                        }

                        if ((i == 0) && (j == 1)) {
                            for (int k = 0; k < 4; k++) {
                                SwingRelationAlgebraTree.showInDialog(partialTree.get(k), "Eraser");
                            }
                        }

                        if (j > 0) {

                            ArrayList<RelationalAlgebraTree> tempList = new ArrayList<>();
                            for (int k = 0; k < count; k++) {
                                tempList.add(partialTree.get(k));
                            }
                            for (int k = 0; k < tempList.size(); k++) {
                                partialTree.remove(tempList.get(k));
                            }
                        }


                    }

                    trees.addAll(partialTree);
                    /*
                    for (RelationalAlgebraTree aTree : partialTree) {
                        stat.addQueryTree(aTree);
                    }
                    */
                }


                stat.stop(TimerType.OPTIMIZATION);



                for(int i = 0; i < trees.size(); i++) {
                    stat.addQueryTree(trees.get(i));
                    //SwingRelationAlgebraTree.showInDialog(trees.get(i), "Tree" + Integer.toString(i));
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

    @Override
    public void execute(UIWindow window)
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
            stat.updateUI(window);
            stat.getBestTree(window);
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