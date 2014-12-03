package edu.gatech.coc.cs6422.group16.algebraTree;

import edu.gatech.coc.cs6422.group16.executionConfiguration.ExecutionConfig;
import edu.gatech.coc.cs6422.group16.metaDataRepository.MetaDataRepository;

import java.util.List;

/**
 * Created by thangnguyen on 11/18/14.
 */
public class BNLJoin extends RelationalAlgebraTree
    {
        private Comparison comparison;

        private QualifiedField condition1;

        private QualifiedField condition2;

        public BNLJoin(QualifiedField condition1, Comparison comparison, QualifiedField condition2)
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
            return super.copyFields(new BNLJoin(newCond1, this.comparison, newCond2));
        }

        @Override
        public double evaluateCost(List<Double> childrenCost)
        {
            MetaDataRepository meta = MetaDataRepository.GetInstance();
            double numBlock1 = meta.GetNumberBlock(this.condition1);
            double numBlock2 = meta.GetNumberBlock(this.condition2);
            System.out.println("Test 173: numBlock1" + numBlock1);
            System.out.println("Test 173: numBlock1" + numBlock2);
            System.out.println("Test 173: BNL Cost" +Math.min(numBlock1*numBlock2 + numBlock1, numBlock1*numBlock2+numBlock2));
            return Math.min(numBlock1*numBlock2 + numBlock1, numBlock1*numBlock2+numBlock2);
        }
        @Override
        public double evaluateSize(List<Double> childrenSize)
        {
            MetaDataRepository meta = MetaDataRepository.GetInstance();
            // formula: T(R) = (T(S1) * T(S2)) / max(V(R1, a), V(R2, a))
            return (childrenSize.get(0) * childrenSize.get(1)) / (Math.max(meta.GetDistinctValueOfAttribute(this.condition1),
                    meta.GetDistinctValueOfAttribute(this.condition2)));
        }
        @Override
        public String getNodeContent()
        {
            ExecutionConfig config = ExecutionConfig.getInstance();
            if (config.isShowCostsInVisualTree())
            {
                return "BLJoin(" + condition1.toString() + " = " + condition2.toString() + ")\n"
                        + this.computeCost() + " , " + this.computeSize();
            }
            else
            {
                return "BLJoin(" + condition1.toString() + " = " + condition2.toString() +")\n" + this.computeCost();
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
            return "\u03c3(" + condition1.toString() + " " + comparison.toString() + " " + condition2.toString() +
                    ")" + s1;
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
