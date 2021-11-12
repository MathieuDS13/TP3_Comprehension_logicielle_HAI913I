package parsers;

import org.apache.commons.io.FileUtils;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class EclipseJDTASTParser extends Parser<ASTParser> {

    /* CONSTRUCTOR */
    public EclipseJDTASTParser(String projectPath) {
        super(projectPath);
    }

    /* METHODS */
    public void setParser(int level, int kind, boolean resolveBindings,
                          boolean bindingsRecovery, String encoding) {
        parser = ASTParser.newParser(level);
        parser.setKind(kind);
        parser.setResolveBindings(resolveBindings);
        parser.setBindingsRecovery(bindingsRecovery);
        parser.setCompilerOptions(JavaCore.getOptions());
        parser.setUnitName("");
        parser.setEnvironment(new String[]{getJREPath()},
                new String[]{getProjectPath()},
                new String[]{encoding}, true);
    }

    public CompilationUnit parse(File sourceFile) throws IOException {
        //Ajouté après
        parser.setEnvironment(new String[]{getJREPath()},
                new String[]{getProjectPath()}, new String[]{"UTF-8"}, true);
        parser.setUnitName(sourceFile.getAbsolutePath());
        parser.setResolveBindings(true);

        //Existant
        Charset platformCharset = null;
        parser.setSource(FileUtils.readFileToString(sourceFile, platformCharset).toCharArray());

        return (CompilationUnit) parser.createAST(null);
    }

    public List<CompilationUnit> parseProject() throws IOException {

        List<CompilationUnit> cUnits = new ArrayList<>();

        for (File sourceFile : listJavaProjectFiles())
            cUnits.add(parse(sourceFile));

        return cUnits;
    }

    @Override
    public void configure() {
        setParser(AST.JLS4, ASTParser.K_COMPILATION_UNIT, true, true, "UTF-8");
    }
}
