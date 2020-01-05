package activities;

import com.sun.jna.platform.win32.Advapi32Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.prefs.BackingStoreException;

import com.sun.jna.platform.win32.WinReg;

public class SteamGameHandler extends OS {
    private static ArrayList<Integer> runningSteamAppsID = new ArrayList<Integer>();

    public static ArrayList<Integer> getRunningGames() {
        return runningSteamAppsID;
    }

    /**
     * this syncs the runningSteamAppsID at the same time
     */
    public static boolean isStillRunningSteamGame() throws BackingStoreException, IOException, InterruptedException {
        if (isWindows() && runningSteamAppsID.size() > 0) {
            ArrayList<Integer> removing = new ArrayList<Integer>();
            for (Integer id : runningSteamAppsID) {
                int running = Advapi32Util.registryGetIntValue(WinReg.HKEY_CURRENT_USER, "Software\\Valve\\Steam\\Apps\\" + id.toString(), "Running");
                System.out.println(id + " : " + running);
                if (running == 0) {
                    removing.add(id);
                }
            }
            for (Integer id : removing) {
                removeRunningSteamGame(id);
            }
        }
        return (runningSteamAppsID.size() > 0);
    }

    public static void addRunningSteamGame(Integer id) {
        System.out.println("Eklenen id: " + id);
        if (id != null) {
            runningSteamAppsID.add(id);
        }
    }

    public static void removeRunningSteamGame(Integer id) {
        runningSteamAppsID.remove(id);
    }
}
