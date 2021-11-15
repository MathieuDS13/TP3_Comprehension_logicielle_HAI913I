package IdentificationModules;

public class DendroNode {
    public DendroNode left = null;
    public DendroNode right = null;

    public DendroNode (DendroNode left, DendroNode right){
        this.left = left;
        this.right = right;
    }

    public void affiche(){
        left.affiche();
        right.affiche();
    };
}
