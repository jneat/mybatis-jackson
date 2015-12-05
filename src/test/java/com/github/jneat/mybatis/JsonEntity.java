package com.github.jneat.mybatis;

import com.fasterxml.jackson.core.TreeNode;

import java.io.Serializable;

public class JsonEntity implements Serializable {

    private static final long serialVersionUID = 2361613838967425855L;

    private long id;

    private TreeNode jsonArray;

    private TreeNode jsonObject;

    private JsonNodeValue nodeArray = JsonNodeValue.EMPTY;

    private JsonNodeValue nodeObject = JsonNodeValue.EMPTY;

    public JsonEntity() {
    }

    public JsonEntity(long id, TreeNode jsonArray, TreeNode jsonObject, JsonNodeValue nodeArray, JsonNodeValue nodeObject) {
        this.id = id;
        this.jsonArray = jsonArray;
        this.jsonObject = jsonObject;
        this.nodeArray = nodeArray;
        this.nodeObject = nodeObject;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TreeNode getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(TreeNode jsonArray) {
        this.jsonArray = jsonArray;
    }

    public TreeNode getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(TreeNode jsonObject) {
        this.jsonObject = jsonObject;
    }

    public JsonNodeValue getNodeArray() {
        return nodeArray;
    }

    public void setNodeArray(JsonNodeValue nodeArray) {
        this.nodeArray = JsonNodeValue.orEmpty(nodeArray);
    }

    public JsonNodeValue getNodeObject() {
        return nodeObject;
    }

    public void setNodeObject(JsonNodeValue nodeObject) {
        this.nodeObject = JsonNodeValue.orEmpty(nodeObject);
    }
}
