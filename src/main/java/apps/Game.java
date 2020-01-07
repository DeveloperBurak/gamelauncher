package apps;

import system.OS;
import helper.FileHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Game {
    private String gameText;
    private File gameExe;
    private File gameImage;
    private boolean isSteamGame;
    private Integer steamAppID;

    private String getTargetExePath() {
        String extension = FileHelper.getFileExtension(this.gameExe);
        switch (extension) {
            case "lnk":
                return FileHelper.getRealExePath(this.gameExe);
            case "url":
                List props = helper.Windows.readInternetShortcutProperties(this.gameExe);
                for (Object prop : props) {
                    if (prop.toString().contains("URL=steam")) {
                        isSteamGame = true;
                        String[] urlParts = prop.toString().split("/");
                        steamAppID = Integer.parseInt(urlParts[urlParts.length - 1]);
                    }
                }
                return extension;
            case "sh":
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
        ui.Main.activity.addForbbidenApp(FileHelper.getFileNameWithExtension(FileHelper.strToFile(this.getTargetExePath())));
    }

    public boolean run() {
        String op = OS.getOperatingSystem();
        if (op.equals("Windows 10") || op.equals("Windows 7")) {
            try {
                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", this.getGameExe().getAbsolutePath());
//                                    Main.steamGameDetected = game.getIsSteamGame();
                System.out.println("Program starting");
                Process proc = pb.start();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
            return true;
        } else if (op.equals("Linux")) {
            try {
                String cmdPath = this.getTargetExePath();
                cmdPath = cmdPath.replace("Game Launcher", "'Game Launcher'"); // shell compatible
                String[] command = {"/bin/bash", "-c", cmdPath};
                ProcessBuilder pb = new ProcessBuilder(command);
                Process proc = pb.start();
                return true;
            } catch (IOException IOex) {
                System.out.println(IOex.getMessage());
            }
        } else {
            System.out.println("Unsupported OS : " + op);
        }
        return false;
    }
}
