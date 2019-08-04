package main;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    static void writeFiles(){
        for(File file:files){
            writeFile(file);
        }
    }
    private static void writeFile(File file){
        if(!file.exists()){
            if(file.mkdir()){
                System.out.println(file.getName() + " is created");
            }else{
                System.out.println(file.getName() + " couldn't created.");
            }
        }
    }
}
