package routeSearch;

import network.Link;
import network.Node;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * @Auther: Jq zhou
 * @Date: 2021/04/16/20:01
 * @Description:
 */
public class Constraint {
    private ArrayList<Node> excludedNodeList = null;
    private ArrayList<Link> excludedLinkList = null;


    public Constraint() {
        this.excludedNodeList = new ArrayList<Node>();
        this.excludedLinkList = new ArrayList<Link>();
//        this.excludedToRList = new ArrayList<ToR>();
//        this.excludedRingList = new ArrayList<Ring>();
    }

    public ArrayList<Node> getExcludedNodeList() {
        return excludedNodeList;
    }

    public void setExcludedNodeList(ArrayList<Node> excludedNodeList) {
        this.excludedNodeList = excludedNodeList;
    }

    public ArrayList<Link> getExcludedLinkList() {
        return excludedLinkList;
    }

    public void setExcludedLinkList(ArrayList<Link> excludedLinkList) {
        this.excludedLinkList = excludedLinkList;
    }

//    public ArrayList<ToR> getExcludedToRList() {
//        return excludedToRList;
//    }
//
//    public void setExcludedToRList(ArrayList<ToR> excludedToRList) {
//        this.excludedToRList = excludedToRList;
//    }
//
//    public ArrayList<Ring> getExcludedRingList() {
//        return excludedRingList;
//    }
//
//    public void setExcludedRingList(ArrayList<Ring> excludedRingList) {
//        this.excludedRingList = excludedRingList;
//    }


}