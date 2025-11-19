package gr.uoi.dit.master2025.gkouvas.dppclient.model;

/**
 * Helper object to store additional metadata inside a TreeView node.
 * This allows us to attach type + id to each TreeItem.
 */
public class TreeNodeData {

    private final String type;   // site, building, device
    private final Long id;
    private final String label;

    public TreeNodeData(String type, Long id, String label) {
        this.type = type;
        this.id = id;
        this.label = label;
    }

    public String getType() {
        return type;
    }

    public Long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label; // what appears in TreeView
    }
}

