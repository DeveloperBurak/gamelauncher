package apps;

import helper.Debugging;
import system.OS;
import helper.FileHelper;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class Game {
    private String gameText;
    private File gameExe;
    private File gameImage;
    private boolean isSteamGame;
    private Integer steamAppID;
    private String extension;

    private String getTargetExePath() {
        String extension = FileHelper.getFileExtension(this.gameExe);
        switch (extension) {
            case "lnk":
                return FileHelper.getRealExePath(this.gameExe);
            case "url":
                List<String> props = helper.Windows.readInternetShortcutProperties(this.gameExe);
                if (props != null) {
                    for (Object prop : props) {
                        if (prop.toString().contains("URL=steam")) {
                            isSteamGame = true;
                            String[] urlParts = prop.toString().split("/");
                            steamAppID = Integer.parseInt(urlParts[urlParts.length - 1]);
                        }
                    }
                }

                return extension;
            case "sh":
            case "desktop":
                return this.gameExe.getAbsolutePath();
            default:

                return extension;
        }
    }

    public boolean isSteamGame() {
        return isSteamGame;
    }

    private File getGameExe() {
        return gameExe;
    }

    public Integer getSteamID() {
        return steamAppID;
    }

    public File getGameImage() {
        return gameImage;
    }

    public String getGameText() {
        return gameText;
    }

    public boolean checkGameExist() {
        String targetPath = this.getTargetExePath();
        if (targetPath != null) {
            if (targetPath.equals("url")) {
                return true;
            } else {
                File target = new File(targetPath);
                System.out.println("target : " + targetPath);
                System.out.println("file is searching: " + target.getAbsolutePath());
                return ((target.exists() && !target.isDirectory()));
            }
        } else {
            return false;
        }
    }

    public Game(File gameExe, String gameText, File gameImage) {
        this.gameImage = gameImage;
        this.gameText = gameText;
        this.gameExe = gameExe;
        this.extension = FileHelper.getFileExtension(this.gameExe);
        ui.Main.activity.addForbbidenApp(FileHelper.getFileNameWithExtension(FileHelper.strToFile(this.getTargetExePath())));
    }

    public boolean run() {
        if (OS.isWindows()) {
            try {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", this.getGameExe().getAbsolutePath());
                System.out.println("Program starting");
                Process proc = pb.start();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            return true;
        } else if (OS.isLinux()) {
            try {
                Runtime rt = Runtime.getRuntime();
                String cmdPath = this.getTargetExePath();
                cmdPath = cmdPath.replace(" ", "\\ "); // shell compatible
                String[] command = {"/bin/sh", "-c", "`grep '^Exec' " + cmdPath + " | tail -1 | sed 's/^Exec=//' | sed 's/%.//' | sed 's/^\"//g' | sed 's/\" *$//g'` &\n"};
                System.out.println(Arrays.toString(command));
                Process proc = rt.exec(command);
                Debugging.printStream(proc.getErrorStream());
                return true;
            } catch (IOException IOex) {
                System.out.println(IOex.getMessage());
            }
        } else {
            System.out.println("Not Valid OS");
        }
        return false;
    }
}
