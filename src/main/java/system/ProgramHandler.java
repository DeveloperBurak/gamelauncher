package system;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;

import java.util.ArrayList;
import java.util.Arrays;

public class ProgramHandler extends OS {
    private static final String[] predefinedForbiddenApps = {"chrome.exe", "opera.exe", "Spotify.exe", "steam.exe"};
    private static ArrayList<String> forbiddenApps = new ArrayList<>(Arrays.asList(predefinedForbiddenApps));

    public void addForbbidenApp(String app) {
        if (app != null) forbiddenApps.add(app);
    }

    public static String getActiveProgram() {
        if (isWindows()) {
            WinDef.HWND fg = User32.INSTANCE.GetForegroundWindow();
            // don't print the name if it's still the same window as previously
            String fgImageName = getImageName(fg);
            if (fgImageName == null) {
                return "Failed to get the image name!";
            } else {
                String[] exePath = fgImageName.split("\\\\");
                return exePath[exePath.length - 1];
            }
        }
        System.out.println("Not valid OS");
        return null;
    }



    public static void showForbiddenApps() {
        System.out.println(forbiddenApps);
    }

    /**
     * checks the active program is legal, if it not so, program will
     */
    public static boolean isLegalProgram(String app) {
        return (!forbiddenApps.contains(app));
    }
}
