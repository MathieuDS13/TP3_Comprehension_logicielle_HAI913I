package IdentificationModules;

import graphs.SpoonGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DendroGraph {
    private List<DendroNode> listNode = new ArrayList();
    private Map<DendroNode, Map<DendroNode, Integer>> mapCouplage = new HashMap<>();
    private SpoonGraph spoon;

    public DendroGraph (String path){
        this.spoon = new SpoonGraph(path);
        try {
            for (int i = 0; i < spoon.getListClass().size() - 1; i++){
                for (int j = i + 1; j < spoon.getListClass().size(); j++){
                    DendroLeafNode leaf1 = new DendroLeafNode(spoon.getListClass().get(i));
                    DendroLeafNode leaf2 = new DendroLeafNode(spoon.getListClass().get(j));
                    Map<DendroNode, Integer> node = new HashMap<>();
                    node.put(leaf2, spoon.couplageEntre2Classes(leaf1.getLeaf(), leaf2.getLeaf()));
                    mapCouplage.put(leaf1, node);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        try {
            for (int i = 0; i < spoon.getListClass().size(); i++){
                DendroLeafNode leaf = new DendroLeafNode(spoon.getListClass().get(i));
                listNode.add(leaf);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void reduceMaxMapCouplage(){
        int max = 0;
        DendroNode nodeMax1 = null;
        DendroNode nodeMax2 = null;

        for (var  node : mapCouplage.entrySet()){
            for (var node2 : node.getValue().entrySet()){
                if(node2.getValue() > max){
                    nodeMax1 = node.getKey();
                    nodeMax2 = node2.getKey();
                }
            }
        }

        for (var node : mapCouplage.entrySet()){
            if(node.getKey().equals(nodeMax1) || node.getKey().equals(nodeMax2)){
                for(var node2: node.getValue().entrySet()){
                    node2.setValue(node2.getValue() + max);
                }
            }else{
                for (var node2: node.getValue().entrySet()){
                    if(node2.getKey().equals(nodeMax1)||node2.getKey().equals(nodeMax2)){
                        node2.setValue(node2.getValue() + max);
                    }
                }
            }
        }

        DendroNode dendroNode = new DendroNode(nodeMax1, nodeMax2);

    }
}
