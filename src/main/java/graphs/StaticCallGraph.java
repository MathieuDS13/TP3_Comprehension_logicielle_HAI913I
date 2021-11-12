package graphs;

import org.eclipse.jdt.core.dom.*;
import utility.Utility;
import visitors.ClassDeclarationsCollector;
import visitors.MethodDeclarationsCollector;
import visitors.MethodInvocationsCollector;

import java.io.IOException;

public class StaticCallGraph extends CallGraph {

    /* CONSTRUCTOR */
    private StaticCallGraph(String projectPath) {
        super(projectPath);
    }

    /* METHODS */
    public static StaticCallGraph createCallGraph(String projectPath, CompilationUnit cUnit) {
        StaticCallGraph graph = new StaticCallGraph(projectPath);
        ClassDeclarationsCollector classCollector = new ClassDeclarationsCollector();
        cUnit.accept(classCollector);

        for (TypeDeclaration cls : classCollector.getClasses()) {
            MethodDeclarationsCollector methodCollector = new MethodDeclarationsCollector();
            cls.accept(methodCollector);

            for (MethodDeclaration method : methodCollector.getMethods())
                graph.addMethodAndInvocations(cls, method);
        }

        return graph;
    }

    public static StaticCallGraph createCallGraph(String projectPath)
            throws IOException {
        StaticCallGraph graph = new StaticCallGraph(projectPath);

        for (CompilationUnit cUnit : graph.parser.parseProject()) {
            StaticCallGraph partial = StaticCallGraph.createCallGraph(projectPath, cUnit);
            graph.addMethods(partial.getMethods());
            graph.addInvocations(partial.getInvocations());
        }

        return graph;
    }

    private boolean addMethodAndInvocations(TypeDeclaration cls, MethodDeclaration method) {
        if (method.getBody() != null) {
            String methodName = Utility.getMethodFullyQualifiedName(cls, method);
            this.addMethod(methodName);

            MethodInvocationsCollector invocationCollector = new MethodInvocationsCollector();
            this.addInvocations(cls, method, methodName, invocationCollector);
            this.addSuperInvocations(methodName, invocationCollector);
        }

        return method.getBody() != null;
    }

    private void addInvocations(TypeDeclaration cls, MethodDeclaration method,
                                String methodName, MethodInvocationsCollector invocationCollector) {
        method.accept(invocationCollector);

        for (MethodInvocation invocation : invocationCollector.getMethodInvocations()) {
            String invocationName = getMethodInvocationName(cls, invocation);
            this.addMethod(invocationName);
            this.addInvocation(methodName, invocationName);
        }
    }

    private String getMethodInvocationName(TypeDeclaration cls, MethodInvocation invocation) {
        Expression expr = invocation.getExpression();
        String invocationName = "";

        if (expr != null) {
            ITypeBinding type = expr.resolveTypeBinding();
            if (type != null) {
                invocationName = "Invocation : " + type.getQualifiedName() + "::" + invocation.getName().toString();
            } else {
                invocationName = "Expression : " + expr + "::" + invocation.getName().toString();
                MethodInvocationsCollector collector = new MethodInvocationsCollector();
                expr.accept(collector);
                for (MethodInvocation invoc :
                        collector.getMethodInvocations()) {
                    String name = this.getMethodInvocationName(cls, invoc);
                    System.out.println(invocationName + " " + invoc.getName() + " " + name);
                }
            }
        } else {
            invocationName = "Class FQN : " + Utility.getClassFullyQualifiedName(cls)
                    + "::" + invocation.getName().toString();
        }
        return invocationName;
    }

    private void addSuperInvocations(String methodName, MethodInvocationsCollector invocationCollector) {
        for (SuperMethodInvocation superInvocation : invocationCollector.getSuperMethodInvocations()) {
            String superInvocationName = superInvocation.getName().getFullyQualifiedName();
            this.addMethod(superInvocationName);
            this.addInvocation(methodName, superInvocationName);
        }
    }
}
