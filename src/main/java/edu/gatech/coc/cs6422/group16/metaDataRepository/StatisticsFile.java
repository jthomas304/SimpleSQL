package edu.gatech.coc.cs6422.group16.metaDataRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

/**
 * Created with IntelliJ IDEA.
 * User: nIcKcHEn
 * Date: 10/31/13
 * Time: 4:42 AM
 * To change this template use File | Settings | File Templates.
 */

/*
 * Edited by thangnguyen 12/04/2014
 */
public class StatisticsFile extends Files {
    private int relationSize;
    private int blockSize;
    private HashMap<String, Integer> distinctValues;

    public StatisticsFile(String dbName, String relName, Vector<AttributeInfo> attrInfo) {
        super(ConfigurationFile.GenDatabaseDirectory(dbName), relName);
        if(distinctValues == null)
            distinctValues = new HashMap<>();
        else
            distinctValues.clear();
        relationSize = 0;
        blockSize = 0;
        ReadFile(attrInfo);
    }

    public void ReadFile(Vector<AttributeInfo> attrInfo) {
        if(OpenFile("Statistics") == true) {
            distinctValues.clear();
            try {
                String line;
                String delimiter = ",";
                while((line = fileLineReader.readLine()) != null) {
                    //skip empty line
                    line = line.trim();
                    if(line.isEmpty() == true)
                        continue;
                    //allow for annotation line starting with a #
                    if(line.charAt(0) == '#')
                        continue;
                    String[] parts = line.split(delimiter);
                    if(parts.length != attrInfo.size() + 1) {
                        String msg = "Line " + fileLineReader.getLineNumber() + " from statistics file " + fileName + " doesn't match the required attributes.";
                        throw new IndexOutOfBoundsException(msg);
                    }
                    relationSize = Integer.valueOf(parts[0].trim());
                    blockSize = Integer.valueOf(parts[1].trim());
                    System.out.println("Test 125: block size of this relation is: "+ blockSize);
                    for(int j = 1; j < attrInfo.size(); j++ ) {
                        distinctValues.put(attrInfo.elementAt(j).GetAttribute(), Integer.valueOf(parts[j+1].trim()));
                        System.out.println("Test 125: Number of distinct values of this attribute is: "+
                                distinctValues.get(attrInfo.elementAt(j).GetAttribute()));
                    }
                }
            } catch (IOException E)
            {
                System.out.println("Error parsing line " + fileLineReader.getLineNumber() + " from configuration file " + fileName + ".");
                relationSize = 0;
                blockSize = 0;
                distinctValues.clear();
            } catch (IndexOutOfBoundsException E) {
                System.out.println(E.getMessage());
                relationSize = 0;
                blockSize = 0;
                distinctValues.clear();
            }
        }

    }

    public int GetDistinctValueOfAttribute(String attrName) {
        if(distinctValues == null)
            return 0;
        else if(distinctValues.get(attrName) == null)
            return 0;
        else

            System.out.println(distinctValues.get(attrName) + " " + attrName + " Test 82");
            return distinctValues.get(attrName);
    }

    public int GetRelationSize() {
        return relationSize;
    }

    public int GetBlockSize() {
        return blockSize;
    }
}
