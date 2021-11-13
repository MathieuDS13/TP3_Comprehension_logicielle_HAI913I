package graphs;

import org.eclipse.jdt.core.dom.*;
import utility.Utility;
import visitors.ClassDeclarationsCollector;
import visitors.MethodDeclarationsCollector;
import visitors.MethodInvocationsCollector;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class StaticCouplingGraph extends CouplingGraph {


    public StaticCouplingGraph(String projectPath) throws IOException {
        super(projectPath);
    }

    public static StaticCouplingGraph createCouplingGraph(String projectPath, CompilationUnit cUnit) throws IOException {
        StaticCouplingGraph graph = new StaticCouplingGraph(projectPath);
        ClassDeclarationsCollector classCollector = new ClassDeclarationsCollector();
        cUnit.accept(classCollector);

        for (TypeDeclaration cls : classCollector.getClasses()) {
            graph.addClass(cls);
            MethodDeclarationsCollector methodCollector = new MethodDeclarationsCollector();
            cls.accept(methodCollector);

            for (MethodDeclaration method : methodCollector.getMethods())
                graph.addInvocations(cls, method);
        }
        //Récupérer les déclarations de types
        //Récupérer les déclarations de méthodes
        //A partir des déclarations de méthodes s'il s'agit d'une businessMethod des invocations l'ajouter à la map

        return graph;
    }

    public static StaticCouplingGraph createCouplingGraph(String projectPath) throws IOException {
        StaticCouplingGraph graph = new StaticCouplingGraph(projectPath);

        for (CompilationUnit unit : graph.parser.parseProject()) {
            StaticCouplingGraph partial = createCouplingGraph(projectPath, unit);
            graph.addClasses(partial.getClasses());
            graph.addInvocations(partial.getClassCalls());
        }
        return graph;
    }

    private boolean addInvocations(TypeDeclaration cls, MethodDeclaration methodDeclaration) {
        if (methodDeclaration.getBody() != null) {
            MethodInvocationsCollector invocationCollector = new MethodInvocationsCollector();
            this.addInvocations(cls, methodDeclaration, invocationCollector);
            this.addSuperInvocations(cls, methodDeclaration, invocationCollector);
        }

        return methodDeclaration.getBody() != null;
    }

    private void addInvocations(TypeDeclaration cls, MethodDeclaration method,
                                MethodInvocationsCollector invocationCollector) {
        method.accept(invocationCollector);
        for (MethodInvocation invocation : invocationCollector.getMethodInvocations())
            this.addInvocation(cls, invocation);
    }

    private void addSuperInvocations(TypeDeclaration cls, MethodDeclaration method, MethodInvocationsCollector invocationCollector) {
        for (SuperMethodInvocation superInvocation : invocationCollector.getSuperMethodInvocations())
            this.addInvocation(cls, superInvocation);
    }

    public boolean addClass(TypeDeclaration declaration) {
        String className = Utility.getClassFullyQualifiedName(declaration);
        return addClass(className);
    }

}
