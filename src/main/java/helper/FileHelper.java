package helper;

import mslinks.ShellLink;
import java.io.File;

import java.util.Optional;

public class FileHelper {
    public static String getFileNameWithExtension(java.io.File file) {
        // Handle null case specially.
        String exePath = file.getAbsolutePath();
        String[] path = exePath.split("\\\\");
        return path[path.length - 1];
    }
    public static String getRealExePath(java.io.File file){
        // Handle null case specially.
        String exePath = null;
        try {
            try {
                exePath = new ShellLink(file).resolveTarget();
//                System.out.println("resolved: " + exePath);
            } catch (mslinks.ShellLinkException e) {
//                System.out.println(e.getMessage());
            }
        } catch (java.io.IOException e) {
//            System.out.println(e.getMessage());
        }
        if (exePath == null) return null;
        return exePath;
    }
//    public static String
    public static String stripExtension(String str) {
        // Handle null case specially.
        if (str == null) return null;
        // Get position of last '.'.
        int pos = str.lastIndexOf(".");
        // If there wasn't any '.' just return the string as is.
        if (pos == -1) return str;
        // Otherwise return the string, up to the dot.
        return str.substring(0, pos);
    }
    public static String getFileExtension(java.io.File file) {
        String fileName = file.getAbsolutePath();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }

    public static File strToFile(String path){
        return new File(path);
    }

}
