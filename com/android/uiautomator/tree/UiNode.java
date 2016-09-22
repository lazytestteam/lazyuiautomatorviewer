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

package com.android.uiautomator.tree;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.android.uiautomator.tree.BasicTreeNode;
import org.dom4j.Document;
import org.dom4j.Element;

import com.android.uiautomator.Const;

public class UiNode extends BasicTreeNode {
	private static final Pattern BOUNDS_PATTERN = Pattern
			.compile("\\[-?(\\d+),-?(\\d+)\\]\\[-?(\\d+),-?(\\d+)\\]");
	// use LinkedHashMap to preserve the order of the attributes
	private final Map<String, String> mAttributes = new LinkedHashMap<String, String>();
	private String mDisplayName = "ShouldNotSeeMe";
	private Object[] mCachedAttributesArray;
    private List<UiNode> uChildren = new ArrayList<UiNode>();

	public void addAtrribute(String key, String value) {
		mAttributes.put(key, value);
		updateDisplayName();
		if ("bounds".equals(key)) {
			updateBounds(value);
		}
	}

	/**
	 * Builds the display name based on attributes of the node
	 */
	private void updateDisplayName() {
		String className = mAttributes.get("class");
		if (className == null)
			return;
		String text = mAttributes.get("text");
		if (text == null)
			return;
		String contentDescription = mAttributes.get("content-desc");
		if (contentDescription == null)
			return;
		String index = mAttributes.get("index");
		if (index == null)
			return;
		String bounds = mAttributes.get("bounds");
		if (bounds == null) {
			return;
		}
		// shorten the standard class names, otherwise it takes up too much
		// space on UI
		className = className.replace("android.widget.", "");
		className = className.replace("android.view.", "");
		StringBuilder builder = new StringBuilder();
		builder.append('(');
		builder.append(index);
		builder.append(") ");
		builder.append(className);
		if (!text.isEmpty()) {
			builder.append(':');
			builder.append(text);
		}
		if (!contentDescription.isEmpty()) {
			builder.append(" {");
			builder.append(contentDescription);
			builder.append('}');
		}
		builder.append(' ');
		builder.append(bounds);
		mDisplayName = builder.toString();
	}

	private void updateBounds(String bounds) {
		Matcher m = BOUNDS_PATTERN.matcher(bounds);
		if (m.matches()) {
			x = Integer.parseInt(m.group(1));
			y = Integer.parseInt(m.group(2));
			width = Integer.parseInt(m.group(3)) - x;
			height = Integer.parseInt(m.group(4)) - y;
			mHasBounds = true;
		} else {
			throw new RuntimeException("Invalid bounds: " + bounds);
		}
	}

	@Override
	public String toString() {
		return mDisplayName;
	}

	public String getAttribute(String key) {
		return mAttributes.get(key);
	}

    public List<UiNode> getUChildren(){
        return uChildren;
    }


    @Override
	public Object[] getAttributesArray() {
		// this approach means we do not handle the situation where an attribute
		// is added
		// after this function is first called. This is currently not a concern
		// because the
		// tree is supposed to be readonly
		if (mCachedAttributesArray == null) {
			mCachedAttributesArray = new Object[mAttributes.size()];
			int i = 0;
			for (String attr : mAttributes.keySet()) {
				mCachedAttributesArray[i++] = new AttributePair(attr,
						mAttributes.get(attr));
			}
		}
		return mCachedAttributesArray;
	}

	public String getXpath() {
		Document document;
		String className = getNodeClassAttribute();
		String xpath = "/" + className;
		boolean flag = false;
		//------------------------------
		String resourceid = getAttribute("resource-id");
		if(resourceid != null && !resourceid.equals("")){
			String idxpath = "//"+ className + "[@resource-id=\'" + resourceid + "\']";
			List<Element> list = UiHierarchyXmlLoader.getElementObjects(Const.document, idxpath.replaceAll("\\\\\"", "\""));
			if(list.size()==1){
				xpath = idxpath.substring(1);
				return xpath;
			}else if(list.size()<4){
				String a = list.get(0).getUniquePath();
				String b = list.get(0).getPath();
				resourceid = resourceid.replaceAll("'", "\\\\'");
				xpath += "[@resource-id=\'" + resourceid + "\'";
				flag = true;
			}
		}
		
		String text = getAttribute("text");
		if (text != null && !text.equals("")) {
			text = text.replaceAll("\"", "\\\\\"");
			if(flag){
				xpath += " and @text=\'" + text + "\'";
			}else{
				xpath += "[@text=\'" + text + "\'";
				flag = true;
			}
		}
		String content_desc = getAttribute("content-desc");
		if(!content_desc.equals("")){
			content_desc = content_desc.replaceAll("'", "\\\\'");
			if(flag){
				xpath += " and @content-desc=\'" + content_desc + "\'";
			}else{
				xpath += "[@content-desc=\'" + content_desc + "\'";
				flag = true;
			}
		}
		
		if(flag){
			xpath = xpath + "]";
		}
		return xpath;
	}
	
	public String getXpath2() {
		String index = getAttribute("index");
		String xpath = getXpath();
		if(!index.equals("")){
			xpath = xpath.replace("]", "");
			if(xpath.contains("[")){
				xpath += " and @index=\\\"" + index + "\\\"]";
			}else{
				xpath += "[@index=\\\"" + index + "\\\"]";
			}
		}
		return xpath;
	}
	public String getIndexXpath() {
		String className = getNodeClassAttribute();
		String xpath = "/" + className;
//		int index = this.classNameIndex;
//		if(index!=1)
			xpath = xpath+"["+this.classNameIndex+"]";
		return xpath;
	}
	public String getNodeClassAttribute() {
		return this.mAttributes.get("class");
	}

    public Map<String,String> getAttributes(){
        return mAttributes;
    }
}
