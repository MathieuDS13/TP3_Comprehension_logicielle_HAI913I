package graphs;

import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.ArrayList;
import java.util.List;

public class SpoonGraph {
    private String source;
    public SpoonAPI spoon = new Launcher();
    private List<CtElement> extract;

    SpoonGraph (String source){
        this.source = source;
        spoon.addInputResource(source);
        spoon.buildModel();
        this.extract = spoon.getModel().getRootPackage().getElements(new TypeFilter<CtElement>(CtElement.class));
    }

    public void printAST(){
        for (CtElement element : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtElement>(CtElement.class))) {
            System.out.println(element.prettyprint());
        }
    }

    public void printClassList(){
        for (CtClass<?> meth : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtClass>(CtClass.class))) {
            System.out.println(meth.getSimpleName());
        }
    }

    public List<CtClass> getListClass(){
        List<CtClass> listClasses = new ArrayList();
        for (CtClass<?> classes : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtClass>(CtClass.class))) {
            listClasses.add(classes);
        }
        return listClasses;
    }

    public List<CtMethod> getListMethodedeClass(CtClass classParam) {
        List<CtMethod> listMethode = new ArrayList();
        for(CtMethod<?> methodes : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtMethod>(CtMethod.class))){
            if (methodes.getClass().equals(classParam)){
                listMethode.add(methodes);
            }
        }
        return listMethode;
    }

    public List<CtInvocation> getListInvocationClass(CtClass classParam) {
        List<CtInvocation> listInvoc = new ArrayList();
        for(CtInvocation<?> invocation : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtInvocation>(CtInvocation.class))){
            listInvoc.add(invocation);
        }
        return listInvoc;
    }

    public CtClass getCtClassByName (String nom) {
        CtClass res = null;
        for (CtClass<?> classes : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtClass>(CtClass.class))) {
            if(classes.getSimpleName().equals(nom)){
                res = classes;
            }
        }

        if (res == null){
            System.out.println("Class null");
        }
        return res;
    }

    public void printInvocation(){
        for (CtInvocation invocation : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtInvocation>(CtInvocation.class))) {

            System.out.println( invocation.prettyprint() + " est de type " + invocation.getExecutable().getDeclaringType());
        }
    }

    public int couplageEntre2Classes (CtClass a, CtClass b){
        int counter = 0;
        List<CtInvocation> listInvocationClassA = getListInvocationClass(a);
        List<CtInvocation> listInvocationClassB = getListInvocationClass(b);

        for (CtInvocation<?> invoque : listInvocationClassA){
            if (invoque.getExecutable().getDeclaringType().getSimpleName().equals(b.getSimpleName())){
                counter ++;
            }
        }

        for (CtInvocation<?> invoque : listInvocationClassB){
            if (invoque.getExecutable().getDeclaringType().getSimpleName().equals(a.getSimpleName())){
                counter ++;
            }
        }

        System.out.println("Nombre de couplage entre class : " + a.getSimpleName() + " et class : " + b.getSimpleName() + " est : " + counter);

        return counter;
    }

    public void printCouplageAll(){
        for (CtClass classA : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtClass>(CtClass.class))){
            for (CtClass classB : spoon.getModel().getRootPackage().getElements(new TypeFilter<CtClass>(CtClass.class))){
                if(!classA.equals(classB)){
                    couplageEntre2Classes(classA, classB);
                }
            }
        }
    }



    public static void main(String[] args) {
        SpoonGraph spoonGraph = new SpoonGraph("/home/nam/Desktop/HAI 913 Evolution logicielle/TP3_Couplage/TP3_Comprehension_logicielle_HAI913I/Project_to_Test/CSPproject3/src");
        spoonGraph.printCouplageAll();
    }

}
