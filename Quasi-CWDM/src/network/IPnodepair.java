package network;

/**
 * Created with IntelliJ IDEA.
 *
 * @Auther: Jq zhou
 * @Date: 2021/06/24/13:39
 * @Description:
 */
public class IPnodepair {
    private String name;
    private int index;
    private Node srcNode;
    private Node desNode;

    public IPnodepair(String name,int index,Node srcNode,Node desNode){
        this.name=name;
        this.index=index;
        this.desNode=desNode;
        this.srcNode=srcNode;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public Node getSrcNode() {
        return srcNode;
    }

    public void setSrcNode(Node srcNode) {
        this.srcNode = srcNode;
    }

    public Node getDesNode() {
        return desNode;
    }

    public void setDesNode(Node desNode) {
        this.desNode = desNode;
    }
}
