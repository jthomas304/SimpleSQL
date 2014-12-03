package edu.gatech.coc.cs6422.group16.algebraTree;

import edu.gatech.coc.cs6422.group16.executionConfiguration.ExecutionConfig;
import edu.gatech.coc.cs6422.group16.metaDataRepository.MetaDataRepository;

import java.util.List;

/**
 * Created by thangnguyen on 11/18/14.
 */
public class MergeJoin extends RelationalAlgebraTree
{
    private Comparison comparison;

    private QualifiedField condition1;

    private QualifiedField condition2;

    public MergeJoin(QualifiedField condition1, Comparison comparison, QualifiedField condition2)
    {
        this.condition1 = condition1;
        this.condition2 = condition2;
        this.comparison = comparison;
    }

    @Override
    public RelationalAlgebraTree copyNode()
    {
        QualifiedField newCond1 = this.condition1.copyNode();
        QualifiedField newCond2 = this.condition2.copyNode();
        return super.copyFields(new MergeJoin(newCond1, this.comparison, newCond2));
    }

    @Override
    public double evaluateCost()
    {
        MetaDataRepository meta = MetaDataRepository.GetInstance();
        double numBlock1 = meta.GetNumberBlock(this.condition1);
        double numBlock2 = meta.GetNumberBlock(this.condition2);
        return Math.ceil((numBlock1*numBlock2 + numBlock1*Math.log(numBlock1) + numBlock2*Math.log(numBlock2))
                + this.getChildren().get(0).evaluateCost() + this.getChildren().get(1).evaluateCost());
    }
    @Override
    public double evaluateSize()
    {
        MetaDataRepository meta = MetaDataRepository.GetInstance();
        // formula: T(R) = (T(S1) * T(S2)) / max(V(R1, a), V(R2, a))
        return Math.ceil((this.getChildren().get(0).evaluateSize() * this.getChildren().get(1).evaluateSize()) / (Math.max(meta.GetDistinctValueOfAttribute(this.condition1),
                meta.GetDistinctValueOfAttribute(this.condition2))));
    }
    @Override
    public String getNodeContent()
    {
        ExecutionConfig config = ExecutionConfig.getInstance();
        if (config.isShowCostsInVisualTree())
        {
            return "Merge Join(" + condition1.toString() + " = " + condition2.toString() + ")\n"
                    + "Cost: "+ this.computeCost() + " , Size: " + this.evaluateSize();
        }
        else
        {
            return "Merge Join(" + condition1.toString() + " = " + condition2.toString() + ")\n"
                    + "Cost: "+ this.computeCost() + " , Size: " + this.evaluateSize();
        }
    }
    @Override
    public boolean validate(List<RelationNode> relationNodes)
    {
        if (this.condition1.validate(relationNodes) && this.condition2.validate(
                relationNodes) && this.getChildCount() == 1)
        {
            return true;
        }
        else
        {
            if (this.getChildCount() != 1)
            {
                System.err.println("Childcount for JoinAsSelectNode invalid: " + this.getChildCount());
            }
            return false;
        }
    }

    @Override
    public String toString()
    {
        String s1 = "(" + this.getChildren().get(0).toString() + ")";
        String s2 = "(" + this.getChildren().get(1).toString() + ")";
        return s1 + "Merge Join{" + condition1.toString() + " " + comparison.toString() + " " + condition2.toString() +
                "}" + s2;
    }

    public Comparison getComparison()
    {
        return comparison;
    }

    public QualifiedField getCondition1()
    {
        return condition1;
    }

    public QualifiedField getCondition2()
    {
        return condition2;
    }

    public void setComparison(Comparison comparison)
    {
        this.comparison = comparison;
    }

    public void setCondition1(QualifiedField condition1)
    {
        this.condition1 = condition1;
    }

    public void setCondition2(QualifiedField condition2)
    {
        this.condition2 = condition2;
    }

    public JoinNode toJoinNode()
    {
        return new JoinNode(condition1, comparison, condition2);
    }


}
