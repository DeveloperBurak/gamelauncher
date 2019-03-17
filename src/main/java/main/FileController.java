package main;

import java.io.File;
import java.io.IOException;

public class FileController {

    private static final File folder = new File(System.getProperty("user.home")+"/Documents/Game Launcher");
    private static final File folderImage = new File(folder.getPath() + "/images/");
    private static final File folderShortcut = new File(folder.getPath() + "/shortcuts/");

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
        return folderShortcut.exists() && folderImage.exists();
    }
    static void writeFiles(){
        if(!folder.exists()){
            if(folder.mkdir()){
                System.out.println(folder.getName() + " is created");
            }else{
                System.out.println(folder.getName() + " couldn't created.");
            }
        }
        if(!folderImage.exists()){
            if(folderImage.mkdir()){
                System.out.println(folderImage.getName() + " is created");
            }else{
                System.out.println(folderImage.getName() + " couldn't created.");
            }
        }
        if(!folderShortcut.exists()){
            if(folderShortcut.mkdir()){
                System.out.println(folderShortcut.getName() + " is created.");
            }else{
                System.out.println(folderShortcut.getName() + " couldn't created.");
            }
        }
    }
}