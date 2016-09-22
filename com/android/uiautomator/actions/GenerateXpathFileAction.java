package com.android.uiautomator.actions;

import com.android.uiautomator.*;
import com.android.uiautomator.tree.UiNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;

import java.util.List;


/**
 * @author wylaihuiying
 * @version V1.0
 * @Title: ${FILE_NAME}
 * @Package com.android.uiautomator.actions
 * @Description:
 * @date 2016/7/25
 */
public class GenerateXpathFileAction extends Action {
    private UiAutomatorViewer mViewer;
    private UiAutomatorModel mModel;
    private GenerateFile table;

    public GenerateXpathFileAction(UiAutomatorViewer viewer) {
        super("&导出所有控件");
        this.mViewer = viewer;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return ImageHelper.loadImageDescriptorFromResource("images/generateFile.png");
    }

    @Override
    public void run() {
        if (mViewer.getModelFile() == null){
            MessageDialog.openError(mViewer.getShell(),"Wait wait wait","Please  analyze screenshots before");
            return;
        }

        mModel = mViewer.getModel();

        List<UiNode> nodeList = mModel.getmNodelist();
        table = new GenerateFile();
        table.generateFile(nodeList);

    }
}