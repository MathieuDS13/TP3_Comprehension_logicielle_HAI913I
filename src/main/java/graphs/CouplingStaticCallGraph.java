package graphs;

import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CouplingStaticCallGraph {

    String projectPath;
    StaticCallGraph staticGraph;
    Map<TypeDeclaration, Map<TypeDeclaration, Integer>> couplingGraph;

    private CouplingStaticCallGraph(String projectPath) {
        setProjectPath(projectPath);
        initiateCouplingGraph();
    }

    public void initiateCouplingGraph() {
        this.couplingGraph = new HashMap<>();
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }

    public void setStaticGraph(StaticCallGraph graph) {
        this.staticGraph = graph;
    }

    //C:/Users/Utilisateur/Google Drive/Cours_Master_2/913_Evol_Restructu_logiciels/TP3_comprehension/tp2/src/
    public static CouplingStaticCallGraph createCouplingGraph(String projectPath) throws IOException {
        //CouplingStaticCallGraph graph = new CouplingStaticCallGraph(projectPath);
        CouplingStaticCallGraph graph = new CouplingStaticCallGraph(projectPath);
        graph.setStaticGraph(StaticCallGraph.createCallGraph(projectPath));

        //TODO réaliser les opérations sur le graphe
        return graph;
    }

    public void print() {
        Map<String, Map<String, Integer>> map = staticGraph.getInvocations();

        for (String source : map.keySet())
            //Sous la forme package.Classe::methode

            System.out.println(source);

        //System.out.println(methodName);
    }


}
