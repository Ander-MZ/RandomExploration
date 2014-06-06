package randomexploration;

import java.security.SecureRandom;
import java.util.LinkedList;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Collections;

public class Tree {
    
    private static final int MAX_VALUE=2147483647;
    private static final int MIN_VALUE=-2147483648;
    public int exploredPaths;
    public int calculatedPaths;
    private int b;//Branching factor
    private int d;//Depth factor
    public double min;//Minimum cost
    public double max;//Maximum cost
    private Node root;
    private LinkedList<LinkedList<Double>> costs;
    private LinkedList<LinkedList<Integer>> paths;
    private LinkedList<Double> pathsCosts;
    private LinkedList<LinkedList<Integer>> sample;
    private LinkedList<Double> sampleCosts;
    private LinkedList<Node> leaves;
    private final SecureRandom random;
    public double trueMin, trueMax, currentMin;
    public int currentDepth;
    private HashSet<Integer> topLevel;

    public Tree(){
        random = new SecureRandom();
        leaves = new LinkedList();
        costs = new LinkedList();
        trueMin = MAX_VALUE;
        trueMax = MIN_VALUE;
        currentDepth = 0;
    }
    
    private void initCosts(){
        pathsCosts = new LinkedList();
        for(int i=0;i<paths.size();i++){
            pathsCosts.add(-1.0);
        }
    }
    
    public int getPaths(){
        return paths.size();
    }    
        
    /*
    This method constructs a new tree given the following
    parameters:
        
        @b      : The branching factor of the tree
        @d      : The depth of the tree
        @root   : A node to start (null->new tree)
    */
    public void generateTree(int b, int d, Node root){
        this.b=b;
        this.d=d;
        if(root==null){
            this.root = new Node(0,0,null);
        }
        loadCosts(d,b);
        generateTreeP(d,0,this.root);
        //System.out.println("Total possible paths: " + (int)Math.pow(b, d) + "\n"); 
    }
    
    private void generateTreeP(int d_, int c_, Node currentNode){
        if(d_==0){//Current node is a leaf
            leaves.add(currentNode);
        }
        if(currentNode.getFather()!=null){
                currentNode.setCost(retrieveCost(d_,c_));             
        }  
        if(d_>0){          
            int b_=b;
            while(b_>0){
                currentNode.addChild(b-b_);
                generateTreeP(d_-1,b_,currentNode.getChild(b-b_));
                b_--;
            }          
        }
    }
    
    //Atachs a cost to each component
    private void loadCosts(int d, int b){  
        LinkedList<Double> component = new LinkedList();
        for(int i=0;i<d;i++){
            for(int j=0;j<b;j++){
                component.add(psRandom_weight());
            }
            costs.add(component);
            component = new LinkedList();
        }
    }
          
    //Generates cost distribution
    private double psRandom_weight(){
        SecureRandom sr1 = new SecureRandom();
        double alpha = 3;
        double beta = 1;
        return Math.pow(((-1.0/alpha)*Math.log(1.0-sr1.nextDouble())),(1.0/beta));
    }
    
    private double retrieveCost(int d, int c){  
        LinkedList<Double> phase = costs.get(d);
        return phase.get(c-1);
    }
    
    /*
    This method generates a list containing all
    the possible paths the tree has.
    */
    public void calculateAllPaths(){
        paths = new LinkedList();
        LinkedList<Integer> path = new LinkedList();
        Node current;
        int limit = (int)Math.pow(b, d);
        for(int i=0;i<limit;i++){
            current = leaves.get(i);
            while(current.getFather()!=null){//If current node is not the root
                path.add(current.getID());
                current = current.getFather();
            }
            paths.add(i, path);
            path = new LinkedList();
        }
        
        this.initCosts();
        leaves=null;
    } 
    
    public void printPaths(){
        Iterator it_1 = paths.iterator(); 
        while(it_1.hasNext()){
            Iterator it_2 = ((LinkedList<Integer>)it_1.next()).iterator();
            while(it_2.hasNext()){
                System.out.println("\t"+it_2.next() );
            }
            System.out.println("");
        }
        System.out.println("------");
        
    }
    
    /*
    This method prints the config. tree (if boolean print is true) and ALSO 
    calculates the cost of each path, while identifying the true minimum of the 
    full space along the current minimum.
    */
    
    public void traverseTree(boolean print){
        min=MAX_VALUE;
        max=MIN_VALUE;
        traverseTree(root,0,print);
    }
    
    private void traverseTree(Node currentNode, double acumCost, boolean print){
        String tab = "\t";
        String arrow = "--> ";
        for(int i=0;i<currentNode.getLevel();i++){
            tab+= "\t";
        }
        
        if(currentNode.getLevel()==d){//Node is a leave
            if(currentDepth==0&&acumCost<trueMin){
                trueMin=acumCost;
            }
            if(acumCost<min){
                min = acumCost;
            }
            if(currentDepth==0&&acumCost>trueMax){
                trueMax=acumCost;
            }
            if(acumCost>max){
                max = acumCost;
            }
            if(print)System.out.println(tab+arrow+currentNode.getID() + " (" + currentNode.getCost() +") " + "[" + acumCost + "]");
        }
        else{
            if(print)System.out.println(tab+arrow+currentNode.getID() + " (" + currentNode.getCost() +")");
            
            for(Node x : currentNode.getChilds() ){
                traverseTree(x,acumCost+x.getCost(),print);  
            }
        }     
    }
    
    /*
    This method generates a random sample of
    a size equal to a certain % of the total paths
    of the tree at the moment
    */
    public void generateSample(double r){
        HashSet<Integer> explored = new HashSet();
        sample = new LinkedList();
        int top = (int)(Math.ceil(r*paths.size()));//Number of paths in the sample
        if(top>paths.size()) top=paths.size();
        int index;
        for(int i=0;i<top;i++){
            index = random.nextInt(paths.size());
            if(!explored.contains(index)){
                explored.add(index);
                sample.add(paths.get(index));
            }
            else{
                i--;//Retry iteration to get a new, unused path
            }
        }
        
        currentDepth++;
    }
    
    /*
    This method explores all the paths in the sample throught 
    all the phases and calculates 
    */    
        public void exploreSample(){
        sampleCosts = new LinkedList();
        Node aux = null;
        double acc;
        //We explore each sample and calculate the costs
        for (int i = 0; i < sample.size(); i++) {
            
            double n = prevCalc(i);
            
            //This path hasn't been calculated
            if (n < 0) {               
                acc = 0.0;               
                int start;             
                aux=root;
                
                while(aux.getLevel()<this.d){
                    start=sample.get(i).get((this.d-1)-aux.getLevel());
                    aux=aux.getChildWithID(start);
                    acc+=aux.getCost();                  
                }
                
                int j=0;
                for(LinkedList<Integer> path : paths){
                    if(path.getFirst()==aux.getID()){
                        j=paths.indexOf(path);
                    }
                }
                pathsCosts.set(j, acc);
                
                calculatedPaths++;

            } 
            else{
                acc=n;
            }
            
            sampleCosts.add(acc);
            exploredPaths++;

        }
    }
    
    /*
    This method returns the cost of a path if it has been previously
        traversed, or -1 if it hasn't.
    */
    private double prevCalc(int index){
        int leaf = sample.get(index).get(0);
        int i=0;
        for(LinkedList<Integer> ids : paths){
            if(ids.get(0)==leaf){
                i=paths.indexOf(ids);
            }
        }    
        
        return pathsCosts.get(i);
    }
    
    /*
    This method receives as parameter the following:
    
        @p  : A number between 0 and 1 representing a % of paths to keep
    
    It takes the random sample and discards the paths that 
    do not fall within the selection. Also, if any top level branches were 
    cut-off it prunes the tree by removing childs from the root, and
    finally removes the corresponding branches from the tree.
    */
    public boolean pruneSample(double p){
        int top = (int)(Math.floor((1-p)*sample.size()));//Number of paths that will be removed
        if(top==sample.size()){
            top--;
        }
        double max = MIN_VALUE;
        boolean branch = false;
        int index = 0;
        for(int i=0;i<top;i++){
            for(int j=0;j<sampleCosts.size();j++){
                if(sampleCosts.get(j)>max){
                    max=sampleCosts.get(j);
                    index=j;
                }
            }
            sampleCosts.remove(index);
            sample.remove(index);
            max = MIN_VALUE;
            index=0;
        }
        
        //Toplevel is a set containing the first element of each path
        topLevel = new HashSet();
        
        for(int i=0;i<sample.size();i++){
            topLevel.add(sample.get(i).get(this.d-currentDepth));
        }
        
        //We check if we can prune the tree, i.e with a branch that is not
        //present on the random sample   
         
        //System.out.println("Paths before prune: " + paths.size()); 
        pruneTree(1,root);
        //System.out.println("Paths after prune: " + paths.size());
        return branch;
        
    }   
    /*
    This method prunes the tree on the required level. 
    
        @level   : The current level of recursion
        @current : The current node
    */
    private void pruneTree(int level, Node current){
        //If we are on the level of pruning
        if (level == this.currentDepth) {
            Node child;
            int i = 0;
            int limit = current.getChilds().size();

            while (i < limit) {
                child = current.getChilds().get(i);
                //If the node is not among the top level, it must be pruned away
                if (!topLevel.contains(child.getID())) {
                    current.getChilds().remove(child);
                    clearPaths(child.getID() + (this.d-child.getLevel()), (child.getID() + interNodes()));
                    limit--;
                }
                else{
                    i++;
                }
                
                
            }
        } 
        else {
            for(Node child : current.getChilds()){
                pruneTree(level+1,child);
            }
        }
    }
    
    /*
    This method eliminates the paths whose parents
    have been pruned away.
    */
    private void clearPaths(int a, int b){   
        
        //System.out.print("Clear");
        
        long ini = System.nanoTime();     
        boolean finish=false;
        int i=0;
        int id;
        
        
        i=startIndex(a,b);
        
        if(i==paths.size()){
            System.out.println();
        }
        
        while(!finish){
            id=paths.get(i).getFirst();
            if((id>=a)&&(id<=b)){
                paths.remove(i); 
                if(i==paths.size()){
                    finish=true;
                }
            }
            else{
                finish=true;
            }
        }      
        
        //System.out.println(" " + ((System.nanoTime()-ini)/1000000000.0));
        
    }
    
    /*
    Auxiliar method for pruning unused branches
    */
    private int startIndex(int a, int b){
        
        int start = 0;
        int end=paths.size();
        int currentID=0;
        int index = 0;
        
        boolean found = false;
        
        while(!found){
            
            index=(int)(start+(Math.floor((end-start)/2.0)));
            currentID = paths.get(index).getFirst();
            
            if(currentID<a){//We are below the interval
                start = index;
            }
            else if(currentID>b){//We are above the interval
                end = index;
            }
            else{//We are within the desired interval
                found = true;
            }     
        }
        
        //Now we want to get the starting index of the interval
              
        while(currentID>a){
            index--;
            currentID = paths.get(index).getFirst();
        }       
        
        return index;
    }
    
    //Return the interval of IDs that must be pruned
    public int interNodes(){
        int nodes=0;
        for(int i=1;i<=(this.d-currentDepth);i++){
            nodes+=Math.pow(this.b, i);
            //nodes+=i*this.b;
        }
        return nodes;
    }
    
    //Return the number of remaining nodes (components) on the tree
    public int totalNodes(){
        Node temp = new Node();
        return temp.totalIDS()-1;
    }

}
