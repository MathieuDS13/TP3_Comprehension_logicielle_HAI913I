package utility;

import java.io.IOException;
import java.io.OutputStreamWriter;

public abstract class DrawableNode {

    public abstract String getText();

    public abstract DrawableNode getLeft();

    public abstract DrawableNode getRight();

    public void printTree(OutputStreamWriter out) throws IOException {
        if (getRight() != null) {
            getRight().printTree(out, true, "");
        }
        printNodeValue(out);
        if (getLeft() != null) {
            getLeft().printTree(out, false, "");
        }
    }

    private void printNodeValue(OutputStreamWriter out) throws IOException {
        if (getText() == null) {
            out.write("<null>");
        } else {
            out.write(getText());
        }
        out.write('\n');
    }

    // use string and not stringbuffer on purpose as we need to change the indent at each recursion
    private void printTree(OutputStreamWriter out, boolean isRight, String indent) throws IOException {
        if (getRight() != null) {
            getRight().printTree(out, true, indent + (isRight ? "        " : " |      "));
        }
        out.write(indent);
        if (isRight) {
            out.write(" /");
        } else {
            out.write(" \\");
        }
        out.write("----- ");
        printNodeValue(out);
        if (getLeft() != null) {
            getLeft().printTree(out, false, indent + (isRight ? " |      " : "        "));
        }
    }

}