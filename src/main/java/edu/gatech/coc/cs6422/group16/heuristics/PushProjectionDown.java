package edu.gatech.coc.cs6422.group16.heuristics;

import edu.gatech.coc.cs6422.group16.algebraTree.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Edited by thangnguyen on 12/04/14.
 */

public class PushProjectionDown {
    public static void pushProjectionDown(RelationalAlgebraTree root) throws IllegalArgumentException {
        if (root == null) {
            throw new IllegalArgumentException("Relational algebra tree cannot be null.");
        }

        //Get all projections at the root
        List<RelationalAlgebraTree> projectNode = new ArrayList<>();
        getAllProjectTypes(root, projectNode);
        ProjectNode projectRootNode = projectNode.get(0).getCurrentNodeAs(ProjectNode.class);
        ProjectNode newProjectRootNode = new ProjectNode(projectRootNode.getProjections(), SelectionType.FIELDS);

        //Get all join nodes
        List<RelationalAlgebraTree> joinNodes = new ArrayList<>();
        getAllJoinTypes(root, joinNodes);
        int numberOfJoins = joinNodes.size();

        //Create to 2 lists of condition at the given join node
        List<QualifiedField> listCondition1 = new ArrayList<>();
        List<QualifiedField> listCondition2 = new ArrayList<>();
        for (int j = 0; j < numberOfJoins; j++) {

            JoinNode current = joinNodes.get(j).getCurrentNodeAs(JoinNode.class);
            listCondition1 = new ArrayList<>();
            listCondition2 = new ArrayList<>();
            listCondition1.add(current.getCondition1());
            listCondition2.add(current.getCondition2());

            ProjectNode newProjectRootNode1 = new ProjectNode(listCondition1, SelectionType.FIELDS);
            ProjectNode newProjectRootNode2 = new ProjectNode(listCondition2, SelectionType.FIELDS);

            RelationalAlgebraTree c1 = current.getChildren().get(0);
            RelationalAlgebraTree c2 = current.getChildren().get(1);

            if (c1.isClass(RelationNode.class)) {
                RelationNode newNode = c1.getCurrentNodeAs(RelationNode.class);
                if(newNode.getRelation().equals(current.getCondition1().getRelation())) {
                    newProjectRootNode1 = addProjection(newProjectRootNode, c1, newProjectRootNode1, current);
                    newProjectRootNode2 = addProjection(newProjectRootNode, c2, newProjectRootNode2, current);
                    newProjectRootNode1.addChild(c1);
                    newProjectRootNode2.addChild(c2);
                    current.getChildren().set(0, newProjectRootNode1);
                    current.getChildren().set(1, newProjectRootNode2);

                } else {
                    newProjectRootNode1 = addProjection(newProjectRootNode, c2, newProjectRootNode1, current);
                    newProjectRootNode2 = addProjection(newProjectRootNode, c1, newProjectRootNode2, current);
                    newProjectRootNode1.addChild(c2);
                    newProjectRootNode2.addChild(c1);
                    current.getChildren().set(0, newProjectRootNode2);
                    current.getChildren().set(1, newProjectRootNode1);
                }
            } else if (c1.isClass(SelectNode.class)) {
                SelectNode newNode = c1.getCurrentNodeAs(SelectNode.class);
                if(newNode.getField().getRelation().equals(current.getCondition1().getRelation())) {
                    newProjectRootNode1 = addProjection(newProjectRootNode, c1, newProjectRootNode1, current);
                    newProjectRootNode2 = addProjection(newProjectRootNode, c2, newProjectRootNode2, current);
                    newProjectRootNode1.addChild(c1);
                    newProjectRootNode2.addChild(c2);
                    current.getChildren().set(0, newProjectRootNode1);
                    current.getChildren().set(1, newProjectRootNode2);
                } else {
                    newProjectRootNode1 = addProjection(newProjectRootNode, c2, newProjectRootNode1, current);
                    newProjectRootNode2 = addProjection(newProjectRootNode, c1, newProjectRootNode2, current);
                    newProjectRootNode1.addChild(c2);
                    newProjectRootNode2.addChild(c1);
                    current.getChildren().set(0, newProjectRootNode2);
                    current.getChildren().set(1, newProjectRootNode1);
                }
            } else if (c1.isClass(JoinNode.class)) {
                JoinNode newNode = c1.getCurrentNodeAs(JoinNode.class);
                if (newNode.getCondition1().getRelation().equals(current.getCondition1().getRelation())
                        || newNode.getCondition2().getRelation().equals(current.getCondition1().getRelation())) {
                    newProjectRootNode1 = addProjection(newProjectRootNode, c1, newProjectRootNode1, current);
                    newProjectRootNode2 = addProjection(newProjectRootNode, c2, newProjectRootNode2, current);
                    newProjectRootNode1.addChild(c1);
                    newProjectRootNode2.addChild(c2);
                    current.getChildren().set(0, newProjectRootNode1);
                    current.getChildren().set(1, newProjectRootNode2);

                } else {
                    newProjectRootNode1 = addProjection(newProjectRootNode, c2, newProjectRootNode1, current);
                    newProjectRootNode2 = addProjection(newProjectRootNode, c1, newProjectRootNode2, current);
                    newProjectRootNode1.addChild(c2);
                    newProjectRootNode2.addChild(c1);
                    current.getChildren().set(0, newProjectRootNode2);
                    current.getChildren().set(1, newProjectRootNode1);
                }
            }
        }
    }

    private static ProjectNode addProjection(ProjectNode projectRootNode, RelationalAlgebraTree node,
                                                       ProjectNode newProjectNode, JoinNode current) {
        QualifiedField qf;
        boolean change = true;
        if (node.isClass(RelationNode.class)) {
            RelationNode newNode = node.getCurrentNodeAs(RelationNode.class);
            for (int i = 0; i < projectRootNode.getProjections().size(); i++) {
                qf = projectRootNode.getProjections().get(i);
                if (qf.getRelation().equals(newNode.getRelation())) {
                    for (QualifiedField q : newProjectNode.getProjections()) {
                        if (q.getRelation().equals(qf.getRelation())
                                && q.getAttribute().equals(qf.getAttribute())) {
                            change = false;
                        }
                    }
                    if (change) newProjectNode.addProjection(qf);
                    change = true;
                }
            }
        } else if (node.isClass(SelectNode.class)) {
            SelectNode newNode = node.getCurrentNodeAs(SelectNode.class);
            for (int i = 0; i < projectRootNode.getProjections().size(); i++) {
                qf = projectRootNode.getProjections().get(i);
                if (qf.getRelation().equals(newNode.getField().getRelation())) {
                    for (QualifiedField q : newProjectNode.getProjections()) {
                        if (q.getRelation().equals(qf.getRelation())
                                && q.getAttribute().equals(qf.getAttribute())) {
                            change = false;
                        }
                    }
                    if (change) newProjectNode.addProjection(qf);
                    change = true;
                }
            }
        } else if (node.isClass(JoinNode.class)) {
            JoinNode newNode = node.getCurrentNodeAs(JoinNode.class);
            for (int i = 0; i < projectRootNode.getProjections().size(); i++) {
                qf = projectRootNode.getProjections().get(i);
                if (qf.getRelation().equals(current.getCondition1().getRelation())) {
                    if (newNode.getCondition1().getRelation().equals(current.getCondition1().getRelation())
                        || (newNode.getCondition2().getRelation().equals(current.getCondition1().getRelation()))) {
                        for (QualifiedField q : newProjectNode.getProjections()) {
                            if (q.getRelation().equals(qf.getRelation())
                                    && q.getAttribute().equals(qf.getAttribute())) {
                                change = false;
                            }
                        }
                        if (change) newProjectNode.addProjection(qf);
                        change = true;
                    }
                } else if  (qf.getRelation().equals(current.getCondition1().getRelation())) {
                    if (newNode.getCondition1().getRelation().equals(current.getCondition2().getRelation())
                            || (newNode.getCondition2().getRelation().equals(current.getCondition2().getRelation()))) {
                        for (QualifiedField q : newProjectNode.getProjections()) {
                            if (q.getRelation().equals(qf.getRelation())
                                    && q.getAttribute().equals(qf.getAttribute())) {
                                change = false;
                            }
                        }
                        if (change) newProjectNode.addProjection(qf);
                        change = true;
                    }
                }
            }
        }
        return newProjectNode;
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

    private static void getAllProjectTypes(RelationalAlgebraTree current, List<RelationalAlgebraTree> nodeList) {
        if (current.isClass(ProjectNode.class))
        {
            nodeList.add(current);
        }
        for (RelationalAlgebraTree child : current.getChildren())
        {
            getAllProjectTypes(child, nodeList);
        }
    }
}
