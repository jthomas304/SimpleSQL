package edu.gatech.coc.cs6422.group16.algebraTree;

import edu.gatech.coc.cs6422.group16.executionConfiguration.ExecutionConfig;
import edu.gatech.coc.cs6422.group16.metaDataRepository.MetaDataRepository;

import java.util.List;
/*
 * Edited by thangnguyen 12/04/2014
 */

public class RelationNode extends RelationalAlgebraTree
{
    private String relation;

    public RelationNode(String relation)
    {
        this.relation = relation;
    }

    @Override
    public RelationalAlgebraTree copyNode()
    {
        return super.copyFields(new RelationNode(this.relation));
    }

    @Override
    public double evaluateCost()
    {
        MetaDataRepository meta = MetaDataRepository.GetInstance();
        return 0;
        //return Math.ceil(meta.GetRelationSize(this.relation));
        //return meta.GetRelationSize(this.relation)/meta.GetBlockSize(this.relation);
    }
    @Override
    public double evaluateSize()
    {
        MetaDataRepository meta = MetaDataRepository.GetInstance();
        return Math.ceil(meta.GetRelationSize(this.relation));
        //return meta.GetRelationSize(this.relation)/meta.GetBlockSize(this.relation);
    }
    @Override
    public String getNodeContent()
    {
        ExecutionConfig config = ExecutionConfig.getInstance();
        if (config.isShowCostsInVisualTree())
        {
            return this.getRelation() + "\n" + "Cost: " + this.computeCost() + " ,Size: " + this.evaluateSize();
        }
        else
        {
            return this.getRelation() + "\n" + "Cost: " + this.computeCost() + " ,Size: " + this.evaluateSize();
        }
    }

    @Override
    public boolean validate(List<RelationNode> relationNodes)
    {
        if (RelationNodesIncludeRelation(relationNodes, this.relation))
        {
            MetaDataRepository meta = MetaDataRepository.GetInstance();
            System.out.println(this.relation + " Test 29");
            if (meta.IsRelationValid(this.relation))
            {
                return true;
            }
            else
            {
                System.err.println("Invalid relation: " + this.relation);
                return false;
            }
        }
        else
        {
            System.err.println("Invalid relation: " + this.relation);
            return false;
        }
    }

    public void addChild(RelationalAlgebraTree node)
    {
        throw new IllegalStateException("A RelationNode is not allowed to have children!");
    }

    @Override
    public String toString()
    {
        return this.relation;
    }

    public String getRelation()
    {
        return relation;
    }

    public void setRelation(String relation)
    {
        this.relation = relation;
    }
}
