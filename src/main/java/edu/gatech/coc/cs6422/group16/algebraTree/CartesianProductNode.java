package edu.gatech.coc.cs6422.group16.algebraTree;

import edu.gatech.coc.cs6422.group16.executionConfiguration.ExecutionConfig;

import java.util.List;
/*
 * Edited by thangnguyen 12/04/2014
 */
public class CartesianProductNode extends RelationalAlgebraTree
{
    @Override
    public RelationalAlgebraTree copyNode()
    {
        return super.copyFields(new CartesianProductNode());
    }

    @Override
    public double evaluateCost()
    {
        return Math.ceil(this.getChildren().get(0).evaluateSize() * this.getChildren().get(1).evaluateSize()
                + this.getChildren().get(0).evaluateCost() + this.getChildren().get(1).evaluateCost());
    }
    @Override
    public double evaluateSize()
    {
        return Math.ceil(this.getChildren().get(0).evaluateSize() * this.getChildren().get(1).evaluateSize());
    }
    @Override
    public String getNodeContent()
    {
        ExecutionConfig config = ExecutionConfig.getInstance();
        if (config.isShowCostsInVisualTree())
        {
            return "x \n " + "Cost: " + this.computeCost() + " ,Size: " + this.evaluateSize();
        }
        else
        {
            return "x \n " + "Cost: " + this.computeCost() + " ,Size: " + this.evaluateSize();
        }
    }

    @Override
    public boolean validate(List<RelationNode> relationNodes)
    {
        if (getChildCount() == 2)
        {
            return true;
        }
        else
        {
            if (this.getChildCount() != 2)
            {
                System.err.println("Childcount for CartesianProductNode invalid: " + this.getChildCount());
            }
            return false;
        }
    }

    @Override
    public String toString()
    {
        String s1 = "(" + this.getChildren().get(0).toString() + ")";
        String s2 = "(" + this.getChildren().get(1).toString() + ")";
        return s1 + "x" + s2;
    }
}
