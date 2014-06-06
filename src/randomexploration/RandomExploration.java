/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package randomexploration;
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

/**
 *
 * @author andemurillo
 */
public class RandomExploration {
    
    private static final DecimalFormat d2 = new DecimalFormat( "0.00" );
    private static final DecimalFormat d3 = new DecimalFormat( "0.000" );
    private static final DecimalFormat d5 = new DecimalFormat( "0.00000" );
    
    private static String path = (System.getProperty("user.dir") + "/src/randomexploration/files/");
    
    private static StringBuilder errors = new StringBuilder();
    private static StringBuilder expPaths = new StringBuilder();
    private static StringBuilder calcPaths = new StringBuilder();
    private static StringBuilder samples = new StringBuilder();

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        int b = 10;
        int d = 4;
        double r = 0.01;
        double s = 0.005;
        boolean showTree = false;
        
        int totalPaths = 0;
        int paths = 0;
        double error = 0.0;
        
        for(int j=0;j<1;j++){//A loop to give statistical significance to results
            
            r=0.01;
        
            for(int k=1;k<=33;k++){//A loop to test with increasing % of sample size

            System.out.println("Iteration: " + k + ", Current sampling factor: "+ d2.format(r) +"");    

            Tree tree = new Tree();
            tree.generateTree(b, d,null);//Branch / Depth
            //System.out.println("Total nodes: " + tree.totalNodes()+"\n");
            tree.calculateAllPaths(); 

            totalPaths = tree.getPaths();

            tree.traverseTree(showTree);        
//            System.out.println("\nCurrent min: " + tree.min + " (" + tree.trueMin + ")" + " [" + tree.currentDepth + "]");
//            System.out.println("Current error: " + d5.format(100*(Math.abs(tree.min-tree.trueMin)/tree.trueMin)) + " %");  
//            System.out.println("\n====================================\n");


                for(int i=0;i<d;i++){
                    tree.generateSample(r);
                    tree.exploreSample();
                    tree.pruneSample(s);
                    tree.traverseTree(showTree);
                    paths=tree.getPaths();

                    //We stop when achieving a total of 5% remaining paths
                    if(i==3||(paths<(int)(Math.ceil(totalPaths*0.05)))){
                        System.out.println("\nCurrent min: " + tree.min + " (" + tree.trueMin + ")" + " [" + tree.currentDepth + "]");
                        System.out.println("Current error: " + d5.format(100*(Math.abs(tree.min-tree.trueMin)/tree.trueMin)) + " %");
                        errors.append(d5.format(100*(Math.abs(tree.min-tree.trueMin)/tree.trueMin)));
                        errors.append("\n");
                        samples.append(d3.format(r));
                        samples.append("\n");
                        expPaths.append(""+tree.exploredPaths);
                        expPaths.append("\n");
                        calcPaths.append(""+tree.calculatedPaths);
                        calcPaths.append("\n");
                        i=d;
                    }
//                    System.out.println("\nCurrent min: " + tree.min + " (" + tree.trueMin + ")" + " [" + tree.currentDepth + "]");
//                    System.out.println("Current error: " + d5.format(100*(Math.abs(tree.min-tree.trueMin)/tree.trueMin)) + " %");
//                    System.out.println("Explored paths so far: " + tree.exploredPaths);
//                    System.out.println("Calculated paths so far: " + tree.calculatedPaths);
//                    System.out.println("\n++++++++++++++++++++++++++++++++++\n");
//                    
//                    writeFile(path+"/errors.txt",d5.format(100*(Math.abs(tree.min-tree.trueMin)/tree.trueMin)));
//                    writeFile(path+"/samples.txt",d3.format(r));
//                    writeFile(path+"/explored.txt",""+tree.exploredPaths);
//                    writeFile(path+"/calculated.txt",""+tree.calculatedPaths);
                }  

            r+=0.03;

            }


            writeFile(path+"/errors.txt",errors.toString());
            writeFile(path+"/samples.txt",samples.toString());
            writeFile(path+"/explored.txt",expPaths.toString());
            writeFile(path+"/calculated.txt",calcPaths.toString());
        
        }
        
//        System.out.println(samples.toString());
//        System.out.println(errors.toString());
    }
    
    
    public static void writeFile(String pathname, String doc){
        
  
        try{
            FileWriter fw = new FileWriter(pathname);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(doc);
            bw.close();
        }
        catch(Exception e){
            System.out.println(e.toString());
        }    
    }
    
    
}
