package randomexploration;


import java.util.LinkedList;

public class Node {
    
    private static int ID_Static = 0;
    private int ID; 
    private double cost;
    private int level;
    private LinkedList<Node> childs;
    private Node father;
    
    public Node(){
        ID=ID_Static;
        ID_Static++;
        cost=0;
        childs = new LinkedList();
    }

    public int getID() {
        return ID;
    }
    
    public int totalIDS(){
        return ID_Static;
    }
    
    public Node(int cost, int level, Node father){
        this();
        this.cost=cost;
        this.level=level;
        this.father=father;
    }
    
    public void addChild(int cost){
        Node c = new Node(cost,this.level+1,this);
        childs.add(c);     
    }
    
    public void removeChild(int i){
        childs.remove(i);
    }
    
    public Node getChild(int i){
        return childs.get(i);
    }
    
    public Node getChildWithID(int ID){
        int i = 0;
        boolean found = false;
        Node aux = null;
        while(!found){         
            if(childs.get(i).getID()==ID){
                found = true;
                aux = childs.get(i);
            }
            i++;
        }
        return aux;
    }

    public LinkedList<Node> getChilds() {
        return childs;
    }

    public void setChilds(LinkedList<Node> childs) {
        this.childs = childs;
    }

    public Node getFather() {
        return father;
    }

    public void setFather(Node father) {
        this.father = father;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    
    @Override
    public boolean equals(Object o){
        return this.ID==((Node)o).getID();
    }
    
    
    
}

