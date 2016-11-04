package com.android.uiautomator;


import com.android.uiautomator.tree.UiNode;
//import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
//import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Display;

import java.awt.*;
import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wylaihuiying
 * @version V1.0
 * @Title: ${FILE_NAME}
 * @Package com.android.uiautomator
 * @Description:命令栏新添加的 “一键导出控件”的事件处理
 * @date 2016/7/26
 */
public class GenerateFile {

    public void generateFile(List<UiNode> nodes) {
        removeLayout(nodes,"Layout");
        try {

            FileDialog dialog = new FileDialog(Display.getDefault().getActiveShell(),SWT.SAVE);
            dialog.setText("保存java文件");
            dialog.setOverwrite(true);
            dialog.setFilterExtensions(new String[]{"*.java"});
            String file = dialog.open();

            if(file == null){
                return;
            }
            
            String packageName ="test";

            String fileName = dialog.getFileName();
            String className = fileName.substring(0,fileName.indexOf("."));
            String path= "";


            if (file.contains("com")){
                path = dialog.getFilterPath();
                int begin = path.indexOf("com");
                packageName= path.substring(begin,path.length()).replace("\\",".");
            }

            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file,false),"UTF-8");

            writer.write("package "+packageName+";\r\n");
            writer.write("import lazy.android.annotations.*;\r\n" +
                    "import lazy.android.bean.BaseBean;\r\n" +
                    "import lazy.android.controls.*;\r\n" +
                    "import io.appium.java_client.AppiumDriver;\r\n\r\n");
            writer.write("\r\n");
            writer.write("public class "+className+" extends BaseBean{\r\n\r\n");
            writer.write("\r\n");
            int textViewNum = 1;
            int editTextNum = 1;
            int buttonNum = 1;
            int checkBoxNum = 1;
            int radioButtonNum = 1;
            int spinnerNum = 1;
            int viewNum = 1;
            for (int i = 0; i < nodes.size(); i++) {
                String mclass = nodes.get(i).getAttributes().get("class");
                String mresource_id = nodes.get(i).getAttributes().get("resource-id");
                String mxpath = nodes.get(i).getAttributes().get("xpath");

                writer.write("    @Xpath(xpath={\"" + mxpath.replace("\\\"", "'") + "\"})\r\n");

                //writer.write("    @FullIndexXpath(fullIndexXpath={\""+nodes.get(i).getAttributes().get("fullIndexXpath")+"\"})\r\n");

                if (nodes.get(i).getAttributes().get("text").equals("")){
                    writer.write("    @Description(description=\"\")\r\n");
                }
                else{
                    writer.write("    @Description(description=\""+nodes.get(i).getAttributes().get("text")+"\")\r\n");
                }



                if (mclass.equals("android.widget.TextView")) {
                    char[] chars = new char[1];
                    String str = mclass.substring(15, mclass.length());
                    chars[0] = str.charAt(0);
                    String temp = new String(chars);
                    String widgetName = "";

                    if ((mresource_id != "") && (mxpath.indexOf(mresource_id)>=0) && (mxpath.indexOf("/")<0)){
                        mresource_id  = mresource_id.substring(mresource_id.indexOf("/")+1,mresource_id.length());
                        String[] name = mresource_id.split("_");
                        for (int k =0 ;k<name.length; k++){
                            if (k>0){
                                String first = name[k].substring(0,1).toUpperCase();
                                String rest = name[k].substring(1,name[k].length());
                                name[k] = new StringBuffer(first).append(rest).toString();
                            }
                            widgetName = widgetName+name[k];
                        }

                        writer.write("    public  PlainText  " + widgetName + ";\r\n");
                    }else {
                        writer.write("    public  PlainText  " + str.replaceFirst(temp,temp.toLowerCase()) + "" + textViewNum + ";\r\n");
                        textViewNum++;
                    }

                    writer.write("\r\n\r\n");

                }
                else if (mclass.equals("android.widget.EditText")) {
                    char[] chars = new char[1];
                    String str = mclass.substring(15,mclass.length());
                    chars[0] = str.charAt(0);
                    String temp = new String(chars);

                    String widgetName = "";

                    if ((mresource_id != "") && (mxpath.indexOf(mresource_id)>=0)&& (mxpath.indexOf("/")<0)){
                        mresource_id  = mresource_id.substring(mresource_id.indexOf("/")+1,mresource_id.length());
                        String[] name = mresource_id.split("_");
                        for (int k =0 ;k<name.length; k++){
                            if (k>0){
                                String first = name[k].substring(0,1).toUpperCase();
                                String rest = name[k].substring(1,name[k].length());
                                name[k] = new StringBuffer(first).append(rest).toString();
                            }
                            widgetName = widgetName+name[k];
                        }

                        writer.write("    public  Text  " + widgetName + ";\r\n");
                    }else{
                        writer.write("    public  Text  " + str.replaceFirst(temp, temp.toLowerCase()) + "" + editTextNum + ";\r\n");
                        editTextNum++;
                    }

                    writer.write("\r\n\r\n");

                }
                else if (mclass.equals("android.widget.Button")) {
                    char[] chars = new char[1];
                    String str = mclass.substring(15,mclass.length());
                    chars[0] = str.charAt(0);
                    String temp = new String(chars);

                    String widgetName = "";

                    if ((mresource_id != "") && (mxpath.indexOf(mresource_id)>=0)&& (mxpath.indexOf("/")<0)){
                        mresource_id  = mresource_id.substring(mresource_id.indexOf("/")+1,mresource_id.length());
                        String[] name = mresource_id.split("_");
                        for (int k =0 ;k<name.length; k++){
                            if (k>0){
                                String first = name[k].substring(0,1).toUpperCase();
                                String rest = name[k].substring(1,name[k].length());
                                name[k] = new StringBuffer(first).append(rest).toString();
                            }
                            widgetName = widgetName+name[k];
                        }

                        writer.write("    public  Click  " + widgetName + ";\r\n");
                    }else {
                        writer.write("    public  Click  " + str.replaceFirst(temp, temp.toLowerCase()) + "" + buttonNum + ";\r\n");
                        buttonNum++;
                    }

                    writer.write("\r\n\r\n");

                }
                else if (mclass.equals("android.widget.CheckBox")) {
                    char[] chars = new char[1];
                    String str = mclass.substring(15,mclass.length());
                    chars[0] = str.charAt(0);
                    String temp = new String(chars);

                    String widgetName = "";

                    if ((mresource_id != "") && (mxpath.indexOf(mresource_id)>=0)&& (mxpath.indexOf("/")<0)){
                        mresource_id  = mresource_id.substring(mresource_id.indexOf("/")+1,mresource_id.length());
                        String[] name = mresource_id.split("_");
                        for (int k =0 ;k<name.length; k++){
                            if (k>0){
                                String first = name[k].substring(0,1).toUpperCase();
                                String rest = name[k].substring(1,name[k].length());
                                name[k] = new StringBuffer(first).append(rest).toString();
                            }
                            widgetName = widgetName+name[k];
                        }

                        writer.write("    public  Click  " + widgetName + ";\r\n");
                    }else {
                        writer.write("    public  Click  " + str.replaceFirst(temp, temp.toLowerCase()) + "" + checkBoxNum + ";\r\n");
                        checkBoxNum++;
                    }

                    writer.write("\r\n\r\n");
                }
                else if (mclass.equals("android.widget.RadioButton")) {
                    char[] chars = new char[1];
                    String str = mclass.substring(15, mclass.length());
                    chars[0] = str.charAt(0);
                    String temp = new String(chars);


                    String widgetName = "";

                    if ((mresource_id != "") && (mxpath.indexOf(mresource_id)>=0)&& (mxpath.indexOf("/")<0)){
                        mresource_id  = mresource_id.substring(mresource_id.indexOf("/")+1,mresource_id.length());
                        String[] name = mresource_id.split("_");
                        for (int k =0 ;k<name.length; k++){
                            if (k>0){
                                String first = name[k].substring(0,1).toUpperCase();
                                String rest = name[k].substring(1,name[k].length());
                                name[k] = new StringBuffer(first).append(rest).toString();
                            }
                            widgetName = widgetName+name[k];
                        }

                        writer.write("    public  Click  " + widgetName + ";\r\n");
                    }else {
                        writer.write("    public  Click  " + str.replaceFirst(temp, temp.toLowerCase()) + "" + radioButtonNum + ";\r\n");
                        radioButtonNum++;
                    }

                    writer.write("\r\n\r\n");
                }
                else if (mclass.equals("android.widget.Spinner")) {
                    char[] chars = new char[1];
                    String str = mclass.substring(15, mclass.length());
                    chars[0] = str.charAt(0);
                    String temp = new String(chars);

                    String widgetName = "";

                    if ((mresource_id != "") && (mxpath.indexOf(mresource_id)>=0)&& (mxpath.indexOf("/")<0)){
                        mresource_id  = mresource_id.substring(mresource_id.indexOf("/")+1,mresource_id.length());
                        String[] name = mresource_id.split("_");
                        for (int k =0 ;k<name.length; k++){
                            if (k>0){
                                String first = name[k].substring(0,1).toUpperCase();
                                String rest = name[k].substring(1,name[k].length());
                                name[k] = new StringBuffer(first).append(rest).toString();
                            }
                            widgetName = widgetName+name[k];
                        }

                        writer.write("    public  Select  " + widgetName + ";\r\n");
                    }
                    else {
                        writer.write("    public  Select  " + str.replaceFirst(temp, temp.toLowerCase()) + "" + spinnerNum + ";\r\n");
                        spinnerNum++;
                    }

                    writer.write("\r\n\r\n");
                }
                else {
                    int j = mclass.lastIndexOf(".");
                    char[] chars = new char[1];
                    String str = mclass.substring(j + 1, mclass.length());
                    chars[0] = str.charAt(0);
                    String temp = new String(chars);

                    String widgetName = "";

                    if ((mresource_id != "") && (mxpath.indexOf(mresource_id)>=0)&& (mxpath.indexOf("/")<0)){
                        mresource_id  = mresource_id.substring(mresource_id.indexOf("/")+1,mresource_id.length());
                        String[] name = mresource_id.split("_");
                        for (int k =0 ;k<name.length; k++){
                            if (k>0){
                                String first = name[k].substring(0,1).toUpperCase();
                                String rest = name[k].substring(1,name[k].length());
                                name[k] = new StringBuffer(first).append(rest).toString();
                            }
                            widgetName = widgetName+name[k];
                        }

                        writer.write("    public  View  " + widgetName + ";\r\n");
                    }else {
                        writer.write("    public  View  " + str.replaceFirst(temp, temp.toLowerCase()) + "" + viewNum + ";\r\n");
                        viewNum++;
                    }

                    writer.write("\r\n\r\n");
                }
            }
            writer.write("\r\n\r\n");
            writer.write("    public "+className+"(AppiumDriver aDriver){super(aDriver);}\r\n\r\n");
            writer.write("}");
            writer.close();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

   private void removeLayout(List<UiNode> list,String reg){
       Pattern p = Pattern.compile(reg,Pattern.CASE_INSENSITIVE);
       for (int i =0;i<list.size();i++){
           Matcher m = p.matcher(list.get(i).getAttributes().get("class"));
           if (m.find()){
               list.remove(i);
               removeLayout(list,reg);
           }
       }
   }

}
