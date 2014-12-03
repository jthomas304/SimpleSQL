package edu.gatech.coc.cs6422.group16.algebraTree;

import java.util.ArrayList;
import java.util.List;

public abstract class RelationalAlgebraTree
{
    public static boolean RelationNodesIncludeRelation(List<RelationNode> nodes, String relation)
    {   //Boolean to check whether the relation is found or not
        boolean relationFound = false;

        //RelationNode is a list of Node
        //What is Node?
        for (RelationNode node : nodes)
        {
            if (node.getRelation().equals(relation))
            {
                relationFound = true;
                break;
            }
        }
        return relationFound;
    }

    public static RelationalAlgebraTree findDeepestNode(RelationalAlgebraTree currentNode)
    {
        if (currentNode.isLeave())
        {
            return currentNode;
        }
        else
        {
            return findDeepestNode(currentNode.getChildren().get(0));
        }
    }

    private List<RelationalAlgebraTree> children = new ArrayList<>();

    private RelationalAlgebraTree parent = null;

    public abstract RelationalAlgebraTree copyNode();

    public abstract double evaluateCost(List<Double> childrenCost);
    public abstract double evaluateSize(List<Double> childrenCost);

    public abstract String getNodeContent();

    public abstract boolean validate(List<RelationNode> relationNodes);

    public void addChild(RelationalAlgebraTree node)
    {
        children.add(node);
        //System.out.println(node + " Test 107");
        node.setParent(this);
    }

    public double computeCost()
    {
        List<Double> childrenCost = new ArrayList<>();
        for (RelationalAlgebraTree c : this.children)
        {
            childrenCost.add(c.computeCost());
        }
        double ownCost = this.evaluateCost(childrenCost);
        double childCost = 0;

        for (Double c : childrenCost)
        {
            childCost += c;
        }


        System.out.println(
                "Test 112: \n" + "This relation: " + this
                + "\n This Children" + this.children
                + "\n Own Cost: " + ownCost
                + "\n Child Cost" + childCost);

        return ownCost + childCost;
    }

    public double computeSize()
    {
        List<Double> childrenSize = new ArrayList<>();
        for (RelationalAlgebraTree c : this.children)
        {
            childrenSize.add(c.computeSize());
        }
        double ownSize = this.evaluateSize(childrenSize);
        System.out.println(
                "Test 112: \n" + "This relation: " + this
                        + "\n This Children" + this.children
                        + "\n Own Cost: " + ownSize);

        return ownSize;
    }


    public RelationalAlgebraTree copyFields(RelationalAlgebraTree other)
    {
        for (RelationalAlgebraTree child : this.children)
        {
            other.addChild(child.copyNode());
        }
        return other;
    }

    public void deleteNode()
    {
        // save parent in time:
        RelationalAlgebraTree parent = this.parent;
        System.out.println("Test 161 | Parent Node :" + parent);
        // first, remove ourself from the children list of the parent:
        this.parent.removeChild(this);
        System.out.println("Test 161 | The remove Node" + this);
        // add all this children to the parent
        for (RelationalAlgebraTree child : children)
        {
            parent.addChild(child);
        }
        // empty children-list
        this.children.clear();
        // done!
    }

    public int getChildCount()
    {
        return this.children.size();
    }

    public List<RelationalAlgebraTree> getChildren()
    {
        return this.children;
    }

    public <T extends RelationalAlgebraTree> T getCurrentNodeAs(Class<? extends T> classType)
    {
        if (classType.isInstance(this))
        {
            return classType.cast(this);
        }
        return null;
    }

    public RelationalAlgebraTree getParent()
    {
        return parent;
    }

    public void insertNodeInSubtree(int indexOfSubtree, RelationalAlgebraTree insertNode)
    {
        // first get the child that will be pushed down:
        RelationalAlgebraTree node = this.children.get(indexOfSubtree);
        //System.out.println("Test 170: insertNode:  " + insertNode);
        System.out.println("Test 170: Node:  " + node);
        // delete this node from our children list:
        this.children.remove(indexOfSubtree);
        System.out.println("Test 170: Children after removing:  " + children);
        // now insert the new node:
        this.children.add(indexOfSubtree, insertNode);


        //System.out.println("Test 170: Children after adding:  " + children);

        // set the parent correctly:
        insertNode.setParent(this);

        //System.out.println("Test 170: insertNode after setting parent  " + insertNode);
        // and add the shifted node as child to the inserted node:
        insertNode.addChild(node);
        System.out.println("Test 170: Node added:  " + node);
        System.out.println("Test 170: insertNode after adding:  " + insertNode);
    }

    public boolean isClass(Class classType)
    {
        return classType.isInstance(this);
    }

    public boolean isLeave()
    {
        return this.children.size() == 0;
    }

    public boolean isRoot()
    {
        return this.parent != null;
    }

    public void removeChild(RelationalAlgebraTree node)
    {
        children.remove(node);
        node.setParent(null);
    }

    public void replaceNode(RelationalAlgebraTree replacement)
    {
        // just exchange children:
        for (RelationalAlgebraTree child : children)
        {
            replacement.addChild(child);
        }
        RelationalAlgebraTree parent = this.parent;

        // delete ourself out of the parent-child-list:
        this.parent.removeChild(this);

        // add in replacement node at parent:
        parent.addChild(replacement);
    }

    public void setParent(RelationalAlgebraTree parent)
    {
        this.parent = parent;
    }

    @Override
    public String toString()
    {
        return "";
    }

    public boolean validateTree(List<RelationNode> relationNodes)
    {
        boolean valid = true;
        System.out.println(this.children + "Test 27");
        System.out.println(relationNodes + "Test 28");
        for (RelationalAlgebraTree child : this.children)
        {
            if (!child.validateTree(relationNodes))
            {
                valid = false;
                break;
            }
        }
        return this.validate(relationNodes) && valid;
    }
}

