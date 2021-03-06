package edu.gatech.coc.cs6422.group16.parsing;

import SimpleSQL.SimpleSQLLexer;
import SimpleSQL.SimpleSQLParser;
import edu.gatech.coc.cs6422.group16.algebraTree.*;
import edu.gatech.coc.cs6422.group16.algebraTree.treeGeneration.TreeGenerator;
import edu.gatech.coc.cs6422.group16.executionConfiguration.ExecutionConfig;
import edu.gatech.coc.cs6422.group16.statistics.Statistics;
import edu.gatech.coc.cs6422.group16.statistics.TimerType;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.misc.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
/*
 * Edited by thangnguyen 12/04/2014
 */
public class QueryParser
{
    private class QueryParserErrorListener implements ANTLRErrorListener
    {
        @Override
        public void syntaxError(Recognizer<?, ?> recognizer, @Nullable Object offendingSymbol, int line,
                                int charPositionInLine, String msg, @Nullable RecognitionException e)
        {
            errorReported = true;
        }

        @Override
        // not needed
        public void reportAmbiguity(@NotNull Parser recognizer, @NotNull DFA dfa, int startIndex, int stopIndex,
                                    boolean exact, @NotNull BitSet ambigAlts, @NotNull ATNConfigSet configs)
        {
        }

        @Override
        // not needed
        public void reportAttemptingFullContext(@NotNull Parser recognizer, @NotNull DFA dfa, int startIndex,
                                                int stopIndex, @Nullable BitSet conflictingAlts, @NotNull ATNConfigSet configs)
        {
        }

        @Override
        // not needed
        public void reportContextSensitivity(@NotNull Parser recognizer, @NotNull DFA dfa, int startIndex,
                                             int stopIndex, int prediction, @NotNull ATNConfigSet configs)
        {
        }
    }

    private static void BuildCartesianLeaveTree(RelationalAlgebraTree tree, List<RelationNode> relationNodes)
    {
        RelationalAlgebraTree currentNode = RelationalAlgebraTree.findDeepestNode(tree);

        // build cartesian-product as left/right-most tree, if more than 1 table:
        if (relationNodes.size() > 1)
        {
            int size = relationNodes.size();
            RelationalAlgebraTree tree2 = new CartesianProductNode();
            // take out first 2 relations:
            tree2.addChild(relationNodes.get(0));
            tree2.addChild(relationNodes.get(1));

            System.out.println("Test 158 | Relation Nodes: " + relationNodes.get(0));
            System.out.println("Test 158 | Relation Nodes: " + relationNodes.get(1));

            int i = 2;
            while (i < size)
            {
                RelationalAlgebraTree tree3 = new CartesianProductNode();
                tree3.addChild(tree2);
                tree3.addChild(relationNodes.get(i));
                i++;
                tree2 = tree3;
            }
            // add new subtree to whole tree:
            currentNode.addChild(tree2);
        }
        else
        {
            // else just add relation and we are done:
            currentNode.addChild(relationNodes.get(0));
        }
    }

    public static List<RelationalAlgebraTree> parseFile(String filename) throws IOException
    {
        QueryParser p = new QueryParser();
        List<RelationalAlgebraTree> trees = null;
        try
        {
            trees = p.parseQuery(new ANTLRFileStream(filename));
        }
        catch (FileNotFoundException e)
        {
            System.err.println("The file \"" + filename + "\" could not be found!");
        }
        return trees;
    }

    public static List<RelationalAlgebraTree> parseString(String query) throws IOException
    {
        QueryParser p = new QueryParser();
        return p.parseQuery(new ANTLRInputStream(query));
    }

    private boolean errorReported = false;

    private List<RelationalAlgebraTree> parseQuery(CharStream input) throws IOException
    {
        Statistics stats = Statistics.getInstance();
        stats.start(TimerType.PARSE);
        SimpleSQLLexer lexer = new SimpleSQLLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SimpleSQLParser p = new SimpleSQLParser(tokens);
        p.setBuildParseTree(true);

        p.addErrorListener(new QueryParserErrorListener());

        ParserRuleContext t = p.statement();

        if (this.errorReported)
        {
            // if there was one error, stop processing here!
            return null;
        }

        SimpleSQLParser.ProjectionsContext projections = t.getChild(SimpleSQLParser.ProjectionsContext.class, 0);
        SimpleSQLParser.RelationsContext relations = t.getChild(SimpleSQLParser.RelationsContext.class, 0);
        SimpleSQLParser.SelectionsContext selections = t.getChild(SimpleSQLParser.SelectionsContext.class, 0);

        ProjectNode projectNode = ParseHelpers.parseProjections(projections);


        List<JoinNode> joinNodes = ParseHelpers.parseJoinNodes(selections);

        List<SelectNode> selectNodes = ParseHelpers.parseSelectNodes(selections);
        List<RelationNode> relationNodes = ParseHelpers.parseRelations(relations);
        System.out.println("Test 154 | Relation Nodes: " + relationNodes);
        System.out.println("Test 154 | SelectNodes: " + selectNodes);

        RelationalAlgebraTree currentNode = projectNode;
        for (SelectNode s : selectNodes)
        {
            currentNode.addChild(s);
            currentNode = s;
            System.out.println(currentNode + "Test 155");
        }

        //System.out.println("Test 168 | Current Nodes: " + currentNode);

        for (JoinNode j : joinNodes)
        {
            JoinAsSelectNode n = j.toJoinAsSelectNode();
            currentNode.addChild(n);
            currentNode = n;
        }

        //System.out.println("168 | Current Node :" + currentNode);
        stats.stop(TimerType.PARSE);

        stats.start(TimerType.VALIDATION);

        // validation of the fields comes now:
        // build a fake-tree for validation with all the relations as leaves with cartesian-products in it:
        RelationalAlgebraTree validationTree = projectNode.copyNode();

        BuildCartesianLeaveTree(validationTree, relationNodes);
        System.out.println("Test 157 | Validation tree: " + validationTree);
        boolean valid = validationTree.validateTree(relationNodes);
        if (!valid)
        {
            System.err.println("Invalid query!");
            return null;
        }
        stats.stop(TimerType.VALIDATION);

        stats.start(TimerType.TREE_GENERATION);

        ExecutionConfig config = ExecutionConfig.getInstance();

        List<RelationalAlgebraTree> allTrees = new ArrayList<>();
        List<RelationalAlgebraTree> possibleRelationTrees = TreeGenerator.generateAllPossibleTrees(relationNodes,
                config.getNumberOfTrees());



        // Convert all the possible relation trees into full-fledged relation-algebra-trees:
        for (RelationalAlgebraTree singleTree : possibleRelationTrees)
        {
            System.out.println("Test 160 | The current tree: " +singleTree);
            RelationalAlgebraTree newRoot = projectNode.copyNode();
            //System.out.println("Test 160 | New Root: " + newRoot);
            // the deepest node has to be "refound":
            RelationalAlgebraTree newCurrentNode = RelationalAlgebraTree.findDeepestNode(newRoot);
            //System.out.println("Test 160 | New Current Node: " + newCurrentNode);
            // just add the relation-tree to the deepest node, done!
            newCurrentNode.addChild(singleTree);


            //SwingRelationAlgebraTree.showInDialog(newRoot, "Permutation");
            allTrees.add(newRoot);
        }
        System.out.println("Test 156| Size of all possible trees: " + allTrees.size());
        stats.stop(TimerType.TREE_GENERATION);

        return allTrees;
    }
}