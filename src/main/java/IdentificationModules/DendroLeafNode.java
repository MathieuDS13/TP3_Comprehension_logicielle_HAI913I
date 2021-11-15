package IdentificationModules;

import spoon.reflect.declaration.CtClass;

public class DendroLeafNode extends DendroNode {
    private DendroNode left = null;
    private DendroNode right = null;

    private CtClass leaf;

    public DendroLeafNode(CtClass leaf){
        super(null, null);
        this.leaf = leaf;
    }

    @Override
    public void affiche() {
        System.out.println(leaf.getSimpleName());
    }

    public CtClass getLeaf(){
        return this.leaf;
    }
}
