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

package com.android.uiautomator.actions;

import com.android.uiautomator.ControlDefineDialog;
import com.android.uiautomator.UiAutomatorModel;
import com.android.uiautomator.UiAutomatorViewer;
import com.android.uiautomator.tree.UiNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;
import com.android.uiautomator.tree.BasicTreeNode;
import java.io.File;

/**
 * @author huangshuli
 * @Description 命令栏新添加的“选择导出控件”的事件处理
 */
public class ControlDefineAction extends Action {
    private UiAutomatorViewer mViewer;
    private UiAutomatorModel mModel;
    private ControlDefineDialog d;

    public ControlDefineAction(UiAutomatorViewer viewer) {
        super("&选择并导出控件");

        mViewer = viewer;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return ImageHelper.loadImageDescriptorFromResource("images/sel.png");
    }

    @Override
    public void run() {
        d = new ControlDefineDialog(Display.getCurrent().getActiveShell());
        if (d.open() != ControlDefineDialog.OK) {
            return;
        }

    }

    public void updateDlg() {
        if (d == null || d.getShell() == null)
            return;
        mModel = mViewer.getModel();
        UiNode node = (UiNode)mModel.getSelectedNode();
        String xpath = node.getAttribute("xpath");
        String description = node.getAttribute("text");
        String controlClass = node.getAttribute("class");
        d.updateSelXpathInfo(xpath, description, controlClass);
    }
}