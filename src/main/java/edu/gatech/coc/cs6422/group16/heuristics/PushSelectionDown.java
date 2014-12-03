package edu.gatech.coc.cs6422.group16.heuristics;

import edu.gatech.coc.cs6422.group16.algebraTree.*;

public class PushSelectionDown
{
    public static boolean TraverseCartesianProductNode(final SelectNode selNode, RelationalAlgebraTree root)
    {
        System.out.println("Test 165: Selection Node: " + selNode);
        System.out.println("Test 165: Root " + root);
        boolean change = false;
        if (root == null)
            return change;
        CartesianProductNode carProdNode = root.getCurrentNodeAs(CartesianProductNode.class);
        SelectNode selectionNode = root.getCurrentNodeAs(SelectNode.class);
        ProjectNode projNode = root.getCurrentNodeAs(ProjectNode.class);

        System.out.println("Test 165: Cartesian Product Node: " + carProdNode);
        System.out.println("Test 165: Select Node: " + selectionNode);
        System.out.println("Test 165: Project Node: " + projNode);

        if (carProdNode == null && selectionNode ==null && projNode == null)
        {
            return change;
        }
        RelationalAlgebraTree c1 = root.getChildren().get(0);
        RelationalAlgebraTree c2 = null;
        if (root.getChildCount() > 1)
            c2 = root.getChildren().get(1);

        System.out.println("Test 165: Root's child c1 " + c1);
        System.out.println("Test 165: Root's child c2 " + c2);
        RelationNode relNode;

        if (c1 != null)
        {
            if ((relNode = c1.getCurrentNodeAs(RelationNode.class)) != null)
            {
                System.out.println("Test 165: rel Node of Child 1 " + relNode);
                //is a relation node
                if (relNode.getRelation().equals(selNode.getField().getRelation()))
                {
                    SelectNode newSelNode = new SelectNode(selNode.getField(), selNode.getComparison(),
                            selNode.getValue());
                    System.out.println("Test 165: New Selection Node " + newSelNode);
                    System.out.println("Test 165: New Selection Node's Field " + selNode.getField());
                    System.out.println("Test 165: New Selection Node's Comparison " + selNode.getComparison());
                    System.out.println("Test 165: New Selection Node's Value " + selNode.getValue());
                    root.insertNodeInSubtree(0, newSelNode);
                    System.out.println("Test 165: New Root " + root);
                    change = true;
                }
            }
        }
        if (c2 != null)
        {
            if ((relNode = c2.getCurrentNodeAs(RelationNode.class)) != null)
            {
                if (relNode.getRelation().equals(selNode.getField().getRelation()))
                {
                    SelectNode newSelNode = new SelectNode(selNode.getField(), selNode.getComparison(),
                            selNode.getValue());

                    carProdNode.insertNodeInSubtree(1, newSelNode);
                    System.out.println("Test 165: New Cartesian Product Node " + carProdNode);
                    change = true;
                }
            }
        }
        return change || TraverseCartesianProductNode(selNode, c1)
                || TraverseCartesianProductNode(selNode, c2);
    }

    public static void pushSelectionDown(RelationalAlgebraTree root) throws IllegalArgumentException
    {
        if (root == null)
        {
            throw new IllegalArgumentException("Relational algebra tree cannot be null.");
        }
        RelationalAlgebraTree searchCur, searchParent, cur, parent = root;
        ProjectNode pn = root.getCurrentNodeAs(ProjectNode.class);
        SelectNode selNode;

        if (pn == null)
        {
            System.out.println("Root node must be a project node.");
            return;
        }




        System.out.println("Test 167: Project Node: " + pn);

        cur = root.getChildren().get(0);
        System.out.println("Test 166: Current Node: " + cur);

        while (cur != null && cur.getCurrentNodeAs(SelectNode.class) == null)
        {
            parent = cur;
            if (cur.getChildCount() > 0)
            {
                cur = parent.getChildren().get(0);
                System.out.println("Test 166: New Current Node: " + cur);
            }
            else
            {
                // no SelectNode found!
                return;
            }
        }

        //traverse the following SelectNode, for each of them, check if push-down is available
        while (cur != null && cur.getCurrentNodeAs(SelectNode.class) != null)
        {
            //from the cur SelectNode, search down till CartesianProductNode is found
            selNode = cur.getCurrentNodeAs(SelectNode.class);
            System.out.println("Test 166: New Selection Node: " + selNode);
            searchParent = cur;
            System.out.println("Test 166: New Search Parent Node: " + searchParent);
            System.out.println("Test 166: Size of Children of New Search Parent Node: "
                    + searchParent.getChildren().size());

            searchCur = searchParent.getChildren().get(0);
            System.out.println("Test 166: Search Current Node: " + searchCur);
            while (searchCur != null && searchCur.getCurrentNodeAs(CartesianProductNode.class) == null)
            {
                if (searchCur.getCurrentNodeAs(JoinNode.class) != null)
                //need to figure out what JoinNode is
                {
                    return;
                }
                searchParent = searchCur;
                searchCur = searchParent.getChildren().get(0);
                System.out.println("Test 166: New Search Current Node: " + searchCur);
            }
            //if no CartesianProductNode is found, there would be no push-down opportunity
            if (searchCur == null)
            {
                break;
            }
            System.out.println("Test 166: Select Node: " + selNode);
            System.out.println("Test 166: Search Node: " + searchCur);

            if (TraverseCartesianProductNode(selNode, searchCur))
            {
                RelationalAlgebraTree tempCur = cur.getChildren().get(0);
                System.out.println("Test 166: The temp current Node: " + tempCur);
                cur.deleteNode();
                System.out.println("Test 166: The new current node after deleting node: " + cur);
                cur = tempCur;
                System.out.println("Test 166: The new current Node: " + cur);
                System.out.println("Test 166: The sel Node: " + selNode);
                //                cur = cur.getChildren().get(0);
                //                parent.getChildren().remove(0);
                //                parent.getChildren().add(cur);
            }
            else
            {
                parent = cur;
                cur = parent.getChildren().get(0);
                System.out.println("Test 166: If we can't find selNode: " + cur);
            }
        }





    }


}