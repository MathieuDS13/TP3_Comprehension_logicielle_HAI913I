package visitors;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import java.util.ArrayList;
import java.util.List;

public class ClassDeclarationsCollector extends ASTVisitor {
	/* ATTRIBUTES */
	private List<TypeDeclaration> classes = new ArrayList<>();
	
	/* METHODS */
	@Override
	public boolean visit(TypeDeclaration typeDeclaration) {
		if(!typeDeclaration.isInterface())
			classes.add(typeDeclaration);
		
		return false;
	}
	
	public List<TypeDeclaration> getClasses(){
		return classes;
	}
}
