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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.File;

/**
 * Implements a file selection dialog for both screen shot and xml dump file
 *
 * "OK" button won't be enabled unless both files are selected
 * It also has a convenience feature such that if one file has been picked, and the other
 * file path is empty, then selection for the other file will start from the same base folder
 *
 */
public class ControlDefineDialog extends Dialog {
    private static final int FIXED_TEXT_FIELD_WIDTH = 300;
    private static final int DEFAULT_LAYOUT_SPACING = 10;
    private Text mXpathText;
    private Text mDescriptionText;
    private Text mControlNameText;
    private boolean mFileChanged = false;
    private Button mOkButton;

    private static File sScreenshotFile;
    private static File sXmlDumpFile;

    /**
     * Create the dialog.
     * @param parentShell
     */
    public ControlDefineDialog(Shell parentShell) {
        super(parentShell);
        setShellStyle(SWT.DIALOG_TRIM | SWT.MODELESS);
    }

    /**
     * Create contents of the dialog.
     * @param parent
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        GridLayout gl_container = new GridLayout(2, false);
        gl_container.verticalSpacing = DEFAULT_LAYOUT_SPACING;
        gl_container.horizontalSpacing = DEFAULT_LAYOUT_SPACING;
        gl_container.marginWidth = DEFAULT_LAYOUT_SPACING;
        gl_container.marginHeight = DEFAULT_LAYOUT_SPACING;
        container.setLayout(gl_container);

        Group openScreenshotGroup = new Group(container, SWT.NONE);
        openScreenshotGroup.setLayout(new GridLayout(1, false));
        openScreenshotGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        openScreenshotGroup.setText("Xpath");

        mXpathText = new Text(openScreenshotGroup, SWT.BORDER | SWT.READ_ONLY);
        if (sScreenshotFile != null) {
            mXpathText.setText(sScreenshotFile.getAbsolutePath());
        }
        GridData gd_screenShotText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_screenShotText.minimumWidth = FIXED_TEXT_FIELD_WIDTH;
        gd_screenShotText.widthHint = FIXED_TEXT_FIELD_WIDTH;
        mXpathText.setLayoutData(gd_screenShotText);

        Composite composite = new Composite(container, SWT.NONE);
        composite.setLayout(new FillLayout(SWT.HORIZONTAL));
        GridData gd_composite = new GridData(SWT.FILL, SWT.FILL, false, false, 1, 4);
        gd_composite.widthHint = 171;
        composite.setLayoutData(gd_composite);
        Group group = new Group(composite, SWT.NONE);
        group.setText("已选控件");
        group.setLayout(new FillLayout(SWT.HORIZONTAL));
        List list = new List(group, SWT.BORDER);
        insertData(list);

        Group openXmlGroup = new Group(container, SWT.NONE);
        openXmlGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        openXmlGroup.setText("Description");
        openXmlGroup.setLayout(new GridLayout(1, false));

        mDescriptionText = new Text(openXmlGroup, SWT.BORDER | SWT.READ_ONLY);
        // mDescriptionText.setEditable(false);
        if (sXmlDumpFile != null) {
            mDescriptionText.setText(sXmlDumpFile.getAbsolutePath());
        }
        GridData gd_xmlText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_xmlText.minimumWidth = FIXED_TEXT_FIELD_WIDTH;
        gd_xmlText.widthHint = FIXED_TEXT_FIELD_WIDTH;
        mDescriptionText.setLayoutData(gd_xmlText);

        Group controlName = new Group(container, SWT.NONE);
        controlName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
        controlName.setText("Control Name");
        controlName.setLayout(new GridLayout(1, false));

        mControlNameText = new Text(controlName, SWT.BORDER | SWT.READ_ONLY);
       // mControlNameText.setEditable(false);
        if (sXmlDumpFile != null) {
            mControlNameText.setText(sXmlDumpFile.getAbsolutePath());
        }

        GridData gd_nameText = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
        gd_xmlText.minimumWidth = FIXED_TEXT_FIELD_WIDTH;
        gd_xmlText.widthHint = FIXED_TEXT_FIELD_WIDTH;
        mControlNameText.setLayoutData(gd_nameText);

        Button addButton = new Button(container, SWT.NONE);
        addButton.setText("添加");
        addButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                //doSth();
            }
        });

        Button delButton = new Button(container, SWT.NONE);
        delButton.setText("删除选中");
        delButton.addListener(SWT.Selection, new Listener() {
            @Override
            public void handleEvent(Event event) {
                //doSth();
            }
        });
        return container;
    }

    /**
     * Create contents of the button bar.
     * @param parent
     */
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        mOkButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
   //     updateButtonState();
    }

    /**
     * Return the initial size of the dialog.
     */
    @Override
    protected Point getInitialSize() {
        return new Point(568, 356);
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("控件—变量定义");
    }

    private void updateButtonState() {
        mOkButton.setEnabled(sXmlDumpFile != null && sXmlDumpFile.isFile());
    }

    public boolean hasFileChanged() {
        return mFileChanged;
    }

    public File getScreenshotFile() {
        return sScreenshotFile;
    }

    public File getXmlDumpFile() {
        return sXmlDumpFile;
    }

    private void insertData(List list) {
        for(int i=1;i<=10;i++){
            list.add("控件"+i);
        }

    }
}
