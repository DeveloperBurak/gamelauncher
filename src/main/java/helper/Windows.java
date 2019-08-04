package helper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class Windows {

    private Windows() {
    }

    // see note
    private static final String WINDOWS_DESKTOP = "Desktop";

    /**
     * the current user desktop path
     *
     * @return the current user desktop path
     */
    private static String getWindowsCurrentUserDesktopPath() {
        return System.getenv("userprofile") + "/" + WINDOWS_DESKTOP;
    }

    /**
     * Create an Internet shortcut on User's Desktop no icon specified
     *
     * @param name   name of the shortcut
     * @param target URL
     */
    public static void createInternetShortcutOnDesktop(String name, String target) throws IOException {
        String path = getWindowsCurrentUserDesktopPath() + "/" + name + ".URL";
        createInternetShortcut(name, path, target, "");
    }

    /**
     * Create an Internet shortcut on User's Desktop, icon specified
     *
     * @param name   name of the shortcut
     * @param target URL
     * @param icon   URL (ex. http://www.server.com/favicon.ico)
     */
    public static void createInternetShortcutOnDesktop(String name, String target, String icon) throws IOException {
        String path = getWindowsCurrentUserDesktopPath() + "/" + name + ".URL";
        createInternetShortcut(name, path, target, icon);
    }

    /**
     * Create an Internet shortcut
     *
     * @param name   name of the shortcut
     * @param where  location of the shortcut
     * @param target URL
     * @param icon   URL (ex. http://www.server.com/favicon.ico)
     */
    static void createInternetShortcut(String name, String where, String target, String icon) throws IOException {
        FileWriter fw = new FileWriter(where);
        fw.write("[InternetShortcut]\n");
        fw.write("URL=" + target + "\n");
        if (!icon.equals("")) {
            fw.write("IconFile=" + icon + "\n");
        }
        fw.flush();
        fw.close();
    }

    public static List readInternetShortcutProperties(File file) {
        try {
            return Files.readAllLines(file.toPath());
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    public static void main(String[] args) throws IOException {
        Windows.createInternetShortcutOnDesktop("GOOGLE", "http://www.google.com");
    }
}