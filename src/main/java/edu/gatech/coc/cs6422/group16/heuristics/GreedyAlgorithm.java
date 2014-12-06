package edu.gatech.coc.cs6422.group16.heuristics;

//import com.sun.java.util.jar.pack.ConstantPool;
import edu.gatech.coc.cs6422.group16.algebraTree.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: nIcKcHEn
 * Date: 11/25/13
 * Time: 1:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class GreedyAlgorithm {
    public static void TransformViaGreedyAlgorithm(RelationalAlgebraTree root) {
        List<RelationalAlgebraTree> markedNodes = new ArrayList<>();
        List<RelationalAlgebraTree> comboNodes = new ArrayList<>();
        comboNodes = getAllComboNodes(root);

        // while not all join-selects are converted or |markedNode| < |joinAsSelectNodes|, continue converting
        while ((comboNodes.size() > 0) && (markedNodes.size() < comboNodes.size())) {
            RelationalAlgebraTree nextComboNode = searchNextComboNode(root, markedNodes);
            if (nextComboNode != null) {
                List<RelationNode> possibleRelations = new ArrayList<>();
                getAllNodesOfType(nextComboNode, RelationNode.class, possibleRelations);
                List<Double> temp = new ArrayList<>();

                // when a union type only has two relations can compare them directly
                if (possibleRelations.size() == 2) {
                    double leftSideCost = possibleRelations.get(0).evaluateCost();
                    double rightSideCost = possibleRelations.get(1).evaluateCost();
                    RelationalAlgebraTree leftNode = possibleRelations.get(0).copyNode();
                    RelationalAlgebraTree rightNode = possibleRelations.get(1).copyNode();

                    if (rightSideCost < leftSideCost) {
                        possibleRelations.get(0).replaceNode(rightNode);
                        possibleRelations.get(1).replaceNode(leftNode);
                    }
                    markedNodes.add(nextComboNode);
                }
                else {
                    if (getAllComboNodes(nextComboNode).size() > 0) {
//                        leftSideCombo = true;
                    }

                }

            }
        }
    }

    private static RelationalAlgebraTree combinationParent(RelationalAlgebraTree relation) {
        RelationalAlgebraTree node = relation;
        while (notCombination(node)) {
            node = node.getParent();
        }

        return node;
    }

    private static boolean notCombination(RelationalAlgebraTree node) {
        boolean join, joinAsSelect, BNL, IndexedNestedLoop, Merge, Hash;

        join = (null == node.getCurrentNodeAs(JoinNode.class)) ? true : false;
        BNL = (null == node.getCurrentNodeAs(BNLJoin.class)) ? true : false;
        IndexedNestedLoop = (null == node.getCurrentNodeAs(IndexedNestedLoopJoin.class)) ? true : false;
        Merge = (null == node.getCurrentNodeAs(MergeJoin.class)) ? true : false;
        Hash = (null == node.getCurrentNodeAs(HashJoin.class)) ? true : false;

        return join && BNL && IndexedNestedLoop && Merge && Hash;
    }

    private static <T extends RelationalAlgebraTree> void getAllNodesOfType(RelationalAlgebraTree current, Class<? extends T> classType, List<T> nodeList) {
        if (current.isClass(classType)) {
            nodeList.add(current.getCurrentNodeAs(classType));
        }
        for (RelationalAlgebraTree child : current.getChildren()) {
            getAllNodesOfType(child, classType, nodeList);
        }
    }

    private static ArrayList<RelationalAlgebraTree> getAllComboNodes(RelationalAlgebraTree current) {
        ArrayList<RelationalAlgebraTree> nodeList = new ArrayList<>();
        if (current.isClass(CartesianProductNode.class)) {
            nodeList.add(current.getCurrentNodeAs(CartesianProductNode.class));
        }
        else if (current.isClass(JoinNode.class)) {
            nodeList.add(current.getCurrentNodeAs(JoinNode.class));
        }
        else if (current.isClass(BNLJoin.class)) {
            nodeList.add(current.getCurrentNodeAs(BNLJoin.class));
        }
        else if (current.isClass(IndexedNestedLoopJoin.class)) {
            nodeList.add(current.getCurrentNodeAs(IndexedNestedLoopJoin.class));
        }
        else if (current.isClass(MergeJoin.class)) {
            nodeList.add(current.getCurrentNodeAs(MergeJoin.class));
        }
        else if (current.isClass(HashJoin.class)) {
            nodeList.add(current.getCurrentNodeAs(HashJoin.class));
        }


        for (RelationalAlgebraTree child : current.getChildren()) {
            nodeList.addAll(getAllComboNodes(child));
        }

        return nodeList;
    }

    private static RelationalAlgebraTree searchNextComboNode(RelationalAlgebraTree start, List<RelationalAlgebraTree> markedNodes) {
        // loop all children, calling recursively, resulting in a depth-first-search:
        for (RelationalAlgebraTree child : start.getChildren()) {
            RelationalAlgebraTree nextUnionNode = searchNextComboNode(child, markedNodes);
            if (nextUnionNode != null) {
                return nextUnionNode;
            }
        }
        // only return if the current node has not been marked and is a CartesianProductNode:
        if (!markedNodes.contains(start) && isCombo(start) ) {
            return start;
        }
        return null;
    }

    private static boolean isCombo(RelationalAlgebraTree node) {
        boolean cart, join, BNL, IndexedNestedLoop, Merge, Hash;

        cart = node.isClass(CartesianProductNode.class);
        join = node.isClass(JoinNode.class);
        BNL = node.isClass(BNLJoin.class);
        IndexedNestedLoop = node.isClass(IndexedNestedLoopJoin.class);
        Merge = node.isClass(MergeJoin.class);
        Hash = node.isClass(HashJoin.class);

        return cart || join || BNL || IndexedNestedLoop || Merge || Hash;
    }

    private static boolean relationInList(List<RelationNode> relations, QualifiedField field) {
        for (RelationNode relation : relations) {
            if (relation.getRelation().equals(field.getRelation())) {
                return true;
            }
        }
        return false;
    }
}