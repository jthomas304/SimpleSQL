package edu.gatech.coc.cs6422.group16.heuristics;

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
        List<RelationalAlgebraTree> unionNodes = new ArrayList<>();
        getAllNodesOfType(root, JoinNode.class, unionNodes);
        getAllNodesOfType(root, CartesianProductNode.class, unionNodes);

        // while not all join-selects are converted or |markedNode| < |joinAsSelectNodes|, continue converting
        while ((unionNodes.size() > 0) && (markedNodes.size() < unionNodes.size())) {
            RelationalAlgebraTree nextUnionNode = searchNextUnionNode(root, markedNodes);
            if (nextUnionNode != null) {
                List<RelationNode> possibleRelations = new ArrayList<>();
                getAllNodesOfType(nextUnionNode, RelationNode.class, possibleRelations);
                List<Double> temp = new ArrayList<>();

                // when a union type only has two relations can compare them directly
                if (possibleRelations.size() == 2) {
                    double leftSideCost = possibleRelations.get(0).evaluateCost(temp);
                    double rightSideCost = possibleRelations.get(1).evaluateCost(temp);
                    RelationalAlgebraTree leftNode = possibleRelations.get(0).copyNode();
                    RelationalAlgebraTree rightNode = possibleRelations.get(1).copyNode();

                    if (rightSideCost < leftSideCost) {
                        possibleRelations.get(0).replaceNode(rightNode);
                        possibleRelations.get(1).replaceNode(leftNode);
                    }
                }
                else {


                }

            }
        }
    }

    private static <T extends RelationalAlgebraTree> void getAllNodesOfType(RelationalAlgebraTree current, Class<? extends T> classType, List<T> nodeList) {
        if (current.isClass(classType)) {
            nodeList.add(current.getCurrentNodeAs(classType));
        }
        for (RelationalAlgebraTree child : current.getChildren()) {
            getAllNodesOfType(child, classType, nodeList);
        }
    }

    private static RelationalAlgebraTree searchNextUnionNode(RelationalAlgebraTree start, List<RelationalAlgebraTree> markedNodes) {
        // loop all children, calling recursively, resulting in a depth-first-search:
        for (RelationalAlgebraTree child : start.getChildren()) {
            RelationalAlgebraTree nextUnionNode = searchNextUnionNode(child, markedNodes);
            if (nextUnionNode != null) {
                return nextUnionNode;
            }
        }
        // only return if the current node has not been marked and is a CartesianProductNode:
        if (!markedNodes.contains(start) && (start.isClass(CartesianProductNode.class) || start.isClass(JoinNode.class)) ) {
            return start;
        }
        return null;
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
