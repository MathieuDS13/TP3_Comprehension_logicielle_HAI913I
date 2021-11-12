package visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.ArrayList;
import java.util.List;

public class MethodDeclarationsCollector extends ASTVisitor {
	/* ATTRIBUTES */
	private List<MethodDeclaration> methods = new ArrayList<>();

	/* METHODS */
	@Override
	public boolean visit(MethodDeclaration methodDeclaration) {
		methods.add(methodDeclaration);
		return false;
	}
	
	public List<MethodDeclaration> getMethods() {
		return methods;
	}
}
