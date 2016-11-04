/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.uiautomator;

import java.io.File;
import java.util.*;

import org.dom4j.Element;
import org.eclipse.swt.graphics.Rectangle;

import com.android.uiautomator.tree.AttributePair;
import com.android.uiautomator.tree.BasicTreeNode;
import com.android.uiautomator.tree.BasicTreeNode.IFindNodeListener;
import com.android.uiautomator.tree.UiHierarchyXmlLoader;
import com.android.uiautomator.tree.UiNode;

/**
 * @author huangshuli
 * @Description 业务模型层，负责xpath解析、节点搜索处理等。
 */
public class UiAutomatorModel {
    private BasicTreeNode mRootNode;
    private UiNode uRootNode;
    private BasicTreeNode mSelectedNode;
    private Rectangle mCurrentDrawingRect;
    private List<Rectangle> mNafNodes;

    // determines whether we lookup the leaf UI node on mouse move of screenshot image
    private boolean mExploreMode = true;

    private boolean mShowNafNodes = false;
    private List<UiNode> mNodelist;
    private Set<String> mSearchKeySet = new HashSet<String>();

    public UiAutomatorModel(File xmlDumpFile) {
        mSearchKeySet.add("text");
        mSearchKeySet.add("content-desc");
        Const.document = null;
        UiHierarchyXmlLoader loader = new UiHierarchyXmlLoader();
//        System.out.println(xmlDumpFile.getAbsolutePath());
        Const.document = loader.getDocument(xmlDumpFile.getAbsolutePath());
        List<Element> list = Const.document.selectNodes("//node");
        for(int i=0; i<list.size(); i++){
        	Element e = list.get(i);
//        	System.out.println(e.attributeValue("class"));
        	e.setName(e.attributeValue("class"));
        }
        BasicTreeNode rootNode = loader.parseXml(xmlDumpFile.getAbsolutePath());
        if (rootNode == null) {
            System.err.println("null rootnode after parsing.");
            throw new IllegalArgumentException("Invalid ui automator hierarchy file.");
        }

        mNafNodes = loader.getNafNodes();
        if (mRootNode != null) {
            mRootNode.clearAllChildren();
        }

        mRootNode = rootNode;
        mExploreMode = true;
        mNodelist = loader.getAllNodes();
    }

    public BasicTreeNode getXmlRootNode() {
        return mRootNode;
    }

    public UiNode getuRootNode(){
        return  uRootNode;
    }

    public BasicTreeNode getSelectedNode() {
        return mSelectedNode;
    }

    public List<UiNode> getmNodelist(){
        return mNodelist;
    }
    /**
     * change node selection in the Model recalculate the rect to highlight,
     * also notifies the View to refresh accordingly
     *
     * @param node
     */
    public void setSelectedNode(BasicTreeNode node) {
        mSelectedNode = node;
        if (mSelectedNode instanceof UiNode) {
            UiNode uiNode = (UiNode) mSelectedNode;
            mCurrentDrawingRect = new Rectangle(uiNode.x, uiNode.y, uiNode.width, uiNode.height);
        } else {
            mCurrentDrawingRect = null;
        }
    }

    public Rectangle getCurrentDrawingRect() {
        return mCurrentDrawingRect;
    }

    /**
     * Do a search in tree to find a leaf node or deepest parent node containing the coordinate
     *
     * @param x
     * @param y
     * @return
     */
    public BasicTreeNode updateSelectionForCoordinates(int x, int y) {
        BasicTreeNode node = null;

        if (mRootNode != null) {
            MinAreaFindNodeListener listener = new MinAreaFindNodeListener();
            boolean found = mRootNode.findLeafMostNodesAtPoint(x, y, listener);
            if (found && listener.mNode != null && !listener.mNode.equals(mSelectedNode)) {
                node = listener.mNode;
            }
        }

        return node;
    }

    public boolean isExploreMode() {
        return mExploreMode;
    }

    public void toggleExploreMode() {
        mExploreMode = !mExploreMode;
    }

    public void setExploreMode(boolean exploreMode) {
        mExploreMode = exploreMode;
    }

    private static class MinAreaFindNodeListener implements IFindNodeListener {
        BasicTreeNode mNode = null;

        @Override
        public void onFoundNode(BasicTreeNode node) {
            if (mNode == null) {
                mNode = node;
            } else {
                if ((node.height * node.width) < (mNode.height * mNode.width)) {
                    mNode = node;
                }
            }
        }
    }

    public List<Rectangle> getNafNodes() {
        return mNafNodes;
    }

    public void toggleShowNaf() {
        mShowNafNodes = !mShowNafNodes;
    }

    public boolean shouldShowNafNodes() {
        return mShowNafNodes;
    }

    public List<BasicTreeNode> searchNode(String tofind) {
        List<BasicTreeNode> result = new LinkedList<BasicTreeNode>();
        for (BasicTreeNode node : mNodelist) {
            Object[] attrs = node.getAttributesArray();
            for (Object attr : attrs) {
                if (!mSearchKeySet.contains(((AttributePair) attr).key))
                    continue;
                if (((AttributePair) attr).value.toLowerCase().contains(tofind.toLowerCase())) {
                    result.add(node);
                    break;
                }
            }
        }
        return result;
    }


    public List<BasicTreeNode> searchByXpath(String xpath){
        Map<String,String> xMap = parseXpath(xpath);
        String class0 = xMap.get("class0");
        String resourceId = xMap.get("resource-id");
        String text  = xMap.get("text");
        String contentDesc = xMap.get("content-desc");
        int length = xMap.size()-4;


        if (resourceId !="" || text != "" || contentDesc != ""){
            List<BasicTreeNode> parents = new ArrayList<BasicTreeNode>();
            List<BasicTreeNode> childList = new ArrayList<BasicTreeNode>();
            int index =1;
            for (BasicTreeNode node :mNodelist){
                UiNode uNode = (UiNode)node;
                String uText="";
                String uResourceId = "";
                String uContentDesc = "";
                if (resourceId != ""){
                    uResourceId = uNode.getAttributes().get("resource-id");
                }
                if (text != ""){
                    uText = uNode.getAttributes().get("text");
                }
                if (contentDesc != ""){
                    uContentDesc = uNode.getAttributes().get("content-desc");
                }

                if (uResourceId.equals(resourceId) && uText.equals(text) && uContentDesc.equals(contentDesc)){
                    parents.add(node);
                }
            }

            if (xMap.size() == 4){
                if (parents.size()==0){
                    return null;
                }
                else {
                    return parents;
                }
            }
            else{
                while(index <= length){
                    String classNameStr = xMap.get("class"+index);
                    childList.clear();
                    for (BasicTreeNode node : parents){
                        List<BasicTreeNode> childs = node.getChildrenList();
                        if (classNameStr.indexOf("[")>=0){
                            String className = classNameStr.substring(0,classNameStr.indexOf("["));
                            int indexStr = Integer.parseInt(classNameStr.substring(classNameStr.indexOf("[")+1,classNameStr.indexOf("]")));

                            int number = 0;
                            for (int i =0;i<childs.size();i++){
                                UiNode uNode = (UiNode)childs.get(i);

                                if (uNode.getAttributes().get("class").equals(className)){
                                    number++;
                                    if (number == indexStr){
                                        childList.add(childs.get(i));
                                    }
                                }
                            }
                        }
                        else {
                            for (BasicTreeNode child : childs){
                                childList.add(child);
                            }
                        }
                    }
                    if (classNameStr.indexOf("[")>=0){
                        String className = classNameStr.substring(0,classNameStr.indexOf("["));

                        parents.clear();

                        for (BasicTreeNode node : childList){
                            UiNode child = (UiNode)node;
                            if (child.getAttributes().get("class").equals(className)){
                                parents.add(node);
                            }
                        }
                    }
                    else{
                        parents.clear();

                        for (BasicTreeNode node : childList){
                            UiNode child = (UiNode)node;
                            if (child.getAttributes().get("class").equals(classNameStr)){
                                parents.add(node);
                            }
                        }
                    }
                    index ++;
                }
                if (parents.size()==0){
                    return null;
                }
                else {
                    return parents;
                }
            }
        }
        else {
            List<BasicTreeNode> parents = new ArrayList<BasicTreeNode>();
            List<BasicTreeNode> childList = new ArrayList<BasicTreeNode>();
            int index = 1;
            for (BasicTreeNode node : mNodelist){
                UiNode uNode = (UiNode)node;
                if (uNode.getAttributes().get("class").equals(class0)){
                    parents.add(node);
                }
            }

            if (xMap.size() == 4){
                if (parents.size()==0){
                    return null;
                }
                else {
                    return parents;
                }
            }
            else{
                while(index <= length){
                    String classNameStr = xMap.get("class"+index);
                    childList.clear();
                    for (BasicTreeNode node : parents){
                        List<BasicTreeNode> childs = node.getChildrenList();
                        if (classNameStr.indexOf("[")>=0){
                            String className = classNameStr.substring(0,classNameStr.indexOf("["));
                            int indexStr = Integer.parseInt(classNameStr.substring(classNameStr.indexOf("[")+1,classNameStr.indexOf("]")));

                            int number = 0;
                            for (int i =0;i<childs.size();i++){
                                UiNode uNode = (UiNode)childs.get(i);

                                if (uNode.getAttributes().get("class").equals(className)){
                                    number++;
                                    if (number == indexStr){
                                        childList.add(childs.get(i));
                                    }
                                }
                            }
                        }
                        else {
                            for (BasicTreeNode child : childs){
                                childList.add(child);
                            }
                        }
                    }
                    if (classNameStr.indexOf("[")>=0){
                        String className = classNameStr.substring(0,classNameStr.indexOf("["));

                        parents.clear();

                        for (BasicTreeNode node : childList){
                            UiNode child = (UiNode)node;
                            if (child.getAttributes().get("class").equals(className)){
                                parents.add(node);
                            }
                        }
                    }
                    else{
                        parents.clear();

                        for (BasicTreeNode node : childList){
                            UiNode child = (UiNode)node;
                            if (child.getAttributes().get("class").equals(classNameStr)){
                                parents.add(node);
                            }
                        }
                    }
                    index ++;
                }
                if (parents.size()==0){
                    return null;
                }
                else {
                    return parents;
                }
            }
        }
    }


    public Map<String,String> parseXpath(String xpath){
        xpath = xpath.substring(2,xpath.length());
        String first="";
        String[] other =null;
        String[] xlist = null;
        String[] attributeList = null;
        Map<String,String> map = new HashMap<String, String>();
        map.put("class0","");
        map.put("text","");
        map.put("resource-id","");
        map.put("content-desc","");

        if (xpath.indexOf("@resource-id")>=0){
            int end = xpath.indexOf("']")+2;
            first = xpath.substring(0,end);
            if (end<xpath.length()){
                other = xpath.substring(end+1,xpath.length()).split("/");
            }
        }
        else {
            xlist = xpath.split("/");
        }
        if (first != ""){
            String xclass = first.substring(0,first.indexOf("[@"));
            map.remove("class0");
            map.put("class0",xclass);
            int begin = first.indexOf("[@")+1;
            int end = first.indexOf("']");
            String firstNode = first.substring(begin,end);
            if (firstNode.indexOf("and")>=0){
                attributeList = firstNode.split("\\s*and\\s*");
                for (int i =0 ;i<attributeList.length;i++){
                    if (attributeList[i].indexOf("resource-id")>=0){
                        String resourceId  = attributeList[i].substring(attributeList[i].indexOf("=")+2,attributeList[i].lastIndexOf("'"));
                        map.remove("resource-id");
                        map.put("resource-id",resourceId);
                    }
                    else if (attributeList[i].indexOf("text")>=0){
                        String text  = attributeList[i].substring(attributeList[i].indexOf("=")+2,attributeList[i].lastIndexOf("'"));
                        map.remove("text");
                        map.put("text",text);
                    }
                    else if (attributeList[i].indexOf("content-desc")>=0){
                        String contentDesc  = attributeList[i].substring(attributeList[i].indexOf("=")+2,attributeList[i].lastIndexOf("'"));
                        map.remove("content-desc");
                        map.put("content-desc",contentDesc);
                    }
                }
            }
            else{
                map.remove("resource-id");
                map.put("resource-id",firstNode.substring(firstNode.indexOf("=")+2,firstNode.length()));
            }

            if (other != null){
                for(int i =0;i< other.length; i++){
                    map.put("class"+(i+1),other[i]);
                }
            }
        }
        else{
            if (xlist[0].indexOf("@")>=0){
                String xclass = xlist[0].substring(0,xlist[0].indexOf("[@"));
                map.remove("class0");
                map.put("class0",xclass);

                String firstNode = xlist[0].substring(xlist[0].indexOf("[@")+1,xlist[0].indexOf("']")+2);

                if (firstNode.indexOf("and")>=0){
                    attributeList = firstNode.split("\\s*and\\s*");
                    for (int i =0;i<attributeList.length;i++){
                        if (attributeList[i].indexOf("text")>0){
                            String text = attributeList[i].substring(attributeList[i].indexOf("=")+2,attributeList[i].indexOf("'"));
                            map.remove("text");
                            map.put("text",text);
                        }
                        else if (attributeList[i].indexOf("content-desc")>=0){
                            String contentDesc = attributeList[i].substring(attributeList[i].indexOf("=")+2,attributeList[i].indexOf("'"));
                            map.remove("content-desc");
                            map.put("content-desc",contentDesc);
                        }
                    }
                }
                else{
                    if (firstNode.indexOf("text")>=0){
                        String text = firstNode.substring(firstNode.indexOf("=")+2,firstNode.indexOf("']"));
                        map.remove("text");
                        map.put("text",text);
                    }
                    else{
                        int begin = firstNode.indexOf("=")+2;
                        int end = firstNode.indexOf("']");
                        String contentDesc = firstNode.substring(begin,end);
                        map.remove("content-desc");
                        map.put("content-desc",contentDesc);
                    }
                }

                for(int i =1;i< xlist.length; i++){
                    map.put("class"+(i),xlist[i]);
                }
            }
            else {
                map.remove("class0");
                map.put("class0",xlist[0]);
                for(int i =1;i< xlist.length; i++){
                    map.put("class"+(i),xlist[i]);
                }
            }
        }
        return map;
    }


}
