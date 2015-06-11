package com.apql.Apql.tree;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Created by corno on 1/08/2014.
 */
public abstract class DraggableNodeTree extends DefaultMutableTreeNode {
    private String id;
    private String name;
    private String pathNode;

    public DraggableNodeTree(String id, String name, String pathNode){
        this.id=id;
        this.name=name;
        this.pathNode=pathNode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathNode() {
        return pathNode;
    }

    public void setPathNode(String path) {
        this.pathNode = path;
    }

}
