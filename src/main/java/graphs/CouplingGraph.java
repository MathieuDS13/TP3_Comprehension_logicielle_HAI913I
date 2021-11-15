package graphs;

import loggers.ConsoleLogger;
import loggers.FileLogger;
import loggers.LogRequest;
import loggers.StandardLogRequestLevel;
import org.eclipse.jdt.core.dom.*;
import processors.ASTProcessor;
import utility.Utility;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.LongStream;

public class CouplingGraph extends ASTProcessor {

    FileLogger loggerChain;

    Set<String> classes = new TreeSet<>();

    //Map<Class name, Map<Class name, CallsCount> classCalls
    Map<String, Map<String, Integer>> classCalls = new TreeMap<>();

    public CouplingGraph(String projectPath) {
        super(projectPath);
        setLoggerChain();
    }

    protected void setLoggerChain() {
        loggerChain = new FileLogger(StandardLogRequestLevel.DEBUG);
        loggerChain.setNextLogger(new ConsoleLogger(StandardLogRequestLevel.INFO));
    }

    public void log() {
        loggerChain.setFilePath(parser.getProjectSrcPath() + "static-couplingGraph.info");
        loggerChain.log(new LogRequest(this.toString(),
                StandardLogRequestLevel.DEBUG));
    }

    public boolean addClass(String className) {
        return classes.add(className);
    }

    public void addInvocation(String classFrom, String classTo) {
        if (classCalls.containsKey(classFrom)) {
            if (classCalls.get(classFrom).containsKey(classTo)) {
                int callCount = classCalls.get(classFrom).get(classTo);
                classCalls.get(classFrom).put(classTo, callCount + 1);
            } else {
                classCalls.get(classFrom).put(classTo, 1);
            }
        } else {
            classCalls.put(classFrom, new TreeMap<>());
            classCalls.get(classFrom).put(classTo, 1);
        }
    }

    public void addInvocation(String source, String destination, int occurrences) {

        if (classCalls.containsKey(source)) {
            int oldCount;
            oldCount = classCalls.get(source).getOrDefault(destination, 0);
            classCalls.get(source).put(destination, oldCount + occurrences);
        } else {
            classCalls.put(source, new HashMap<>());
            classCalls.get(source).put(destination, occurrences);
        }

    }

    public void addInvocation(TypeDeclaration source, Expression destination) {
        String sourceClass = Utility.getClassFullyQualifiedName(source);
        String invokedMethod = getInvokedMethodSignature(destination);

        if (!isBusinessMethod(invokedMethod))
            return;

        String calledMethodClass = Utility.getAnyMethodInvocationDeclaringClass((MethodInvocation) destination);
        addInvocation(sourceClass, calledMethodClass);
    }

    private boolean isBusinessMethod(String invokedMethodSignature) {
        String declaringTypeFQN = invokedMethodSignature.split("::")[0];
        int indexOfTypeDotInFQN = declaringTypeFQN.lastIndexOf(".");
        String containingPackageFQN = declaringTypeFQN.substring(0, indexOfTypeDotInFQN);
        return new File(
                parser.getProjectSrcPath(),
                containingPackageFQN.replace(".", File.separator)
        ).exists();
    }

    private String getInvokedMethodSignature(Expression invocation) {
        if (invocation instanceof MethodInvocation)
            return Utility
                    .getMethodInvocationDefaultFormat(
                            (MethodInvocation) invocation);
        else
            return Utility
                    .getMethodSuperInvocationDefaultFormat(
                            (SuperMethodInvocation) invocation);
    }

    public boolean addClasses(Set<String> classes) {
        return this.classes.addAll(classes);
    }

    public void addInvocations(Map<String, Map<String, Integer>> map) {
        for (String source : map.keySet())
            for (String destination : map.get(source).keySet())
                this.addInvocation(source, destination, map.get(source).get(destination));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Number of classes : ").append(getClassCount()).append(" classes");
        builder.append("\nTotal number of calls : ").append(getNumberOfCalls()).append(" calls");

        builder.append("\n\nClass calls :");
        for (String entry :
                classCalls.keySet()) {
            builder.append("\n\n\tClass : ").append(entry).append(" : ");
            for (String classCalled :
                    classCalls.get(entry).keySet()) {
                builder.append("\n\t\tclass called -> ").append(classCalled).append(" (").append(classCalls.get(entry).get(classCalled)).append(" time(s))");
            }
        }
        return builder.toString();
    }

    public Set<String> getClasses() {
        return classes;
    }

    public Map<String, Map<String, Integer>> getClassCalls() {
        return classCalls;
    }

    public long getNumberOfCalls() {
        return classCalls.keySet()
                .stream()
                .map(source -> classCalls.get(source))
                .map(Map::values)
                .flatMap(Collection::stream)
                .flatMapToLong(value -> LongStream.of((long) value))
                .sum();
    }

    public long getClassCount() {
        return classes.size();
    }

    public double getCoupling(String firstClass, String secondClass) {
        //Si les classes n'existent pas dans le graph
        if (!classes.contains(firstClass) && !classes.contains(secondClass)) return 0;

        //Sinon :
        int callsFromFirstToSec = 0;
        int callsFromSecToFirst = 0;

        if (classCalls.containsKey(firstClass)) {
            callsFromFirstToSec = classCalls.get(firstClass).getOrDefault(secondClass, 0);
        }
        if (classCalls.containsKey(secondClass)) {
            callsFromSecToFirst = classCalls.get(secondClass).getOrDefault(firstClass, 0);
        }

        int sumOfCalls = callsFromFirstToSec + callsFromSecToFirst;
        return (double) sumOfCalls / (double) getNumberOfCalls();
    }

}
