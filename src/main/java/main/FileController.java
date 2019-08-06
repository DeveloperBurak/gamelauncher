package main;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;

public class FileController {

    private static final File folder = new File(System.getProperty("user.home")+"/Documents/Game Launcher");
    private static final File folderImage = new File(folder.getPath() +File.separator+ "images"+File.separator);
    private static final File folderShortcut = new File(folder.getPath() +File.separator+ "shortcuts"+File.separator);
    private static final File folderUserDatas = new File(folder.getPath() +File.separator+ "userdatas"+File.separator);

    private static ArrayList<File> files = new ArrayList<>(Arrays.asList(folder,folderShortcut,folderImage,folderUserDatas));

    static File getUserDirectory(){
        return folderUserDatas;
    }
    public static File getFolder() {
        return folder;
    }

    public static File getFolderImage() {
        return folderImage;
    }

    public static File getFolderShortcut() {
        return folderShortcut;
    }
    static boolean checkExistsFolders(){
        for(File file:files){
            if(!file.exists()){
                return false;
            }
        }
        return true;
    }
    static boolean writeFile(File file,String context){
        try{
            FileWriter fw=new FileWriter(file);
            fw.write(context);
            fw.close();
        }catch(Exception e){System.out.println(e); return false;}
        System.out.println("Success...");
        return true;
    }

    static void writePredefinedFolders(){
        for(File file:files){
            writeFolder(file);
        }
    }
    static void writeFolder(File folder){
        if(!folder.exists()){
            if(folder.mkdir()){
                System.out.println(folder.getName() + " is created");
            }else{
                System.out.println(folder.getName() + " couldn't created.");
            }
        }
    }
}
