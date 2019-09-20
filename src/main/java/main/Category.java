package main;

import helper.FileHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Category {
    private String category_name;
    private ArrayList<Category.Game> games = new ArrayList<>();

    public Category(String category_name) {
        this.category_name = category_name;
    }

    public String getCategoryName() {
        return category_name;
    }

    public ArrayList<Category.Game> getGames() {
        return this.games;
    }

    public void addGame(File gameExe, String gameText, File gameImage) {
        Category.Game game = new Category.Game(gameExe, gameText, gameImage);
        this.games.add(game);
    }

    public void removeGames() {
        this.games.clear();
    }

    public void addGamesFromFolder(File folderParam) {
        File[] files = folderParam.listFiles();
        if (files != null) {
            for (final File fileEntry : files) {
                if (fileEntry.isFile()) {
                    String gameText = FileHelper.stripExtension(fileEntry.getName());
                    File fileImage = new File(fileEntry.getParentFile().getParentFile().getParentFile().getPath() + File.separator + "images" + File.separator + gameText + ".jpg");
                    this.addGame(fileEntry, gameText, fileImage);
                }
            }
        }
    }

    public static class Game {
        private String gameText;
        private File gameExe;
        private File gameImage;
        private boolean isSteamGame;

        private String getTargetExePath() {
            String extension = FileHelper.getFileExtension(this.gameExe);
            if (extension.equals("lnk")) {
                System.out.println("lnk found, real path: " + FileHelper.getRealExePath(this.gameExe));
                return FileHelper.getRealExePath(this.gameExe);
            } else if (extension.equals("url")) {
                System.out.println("Url Found");
                List props = helper.Windows.readInternetShortcutProperties(this.gameExe);
                for(Object prop:props){
                    System.out.println(prop);
                    if(prop.toString().contains("URL=steam")){
                        isSteamGame = true;
                    }
                }
//                System.out.println("Url target: "+prop.getProperty("target"));
//                System.out.println("url");
                return extension;
            } else if(extension.equals("sh")){
//                System.out.println("sh");
                return this.gameExe.getAbsolutePath();
            } else {

                return extension;
            }
        }

        public boolean getIsSteamGame(){
            return isSteamGame;
        }

        public File getGameExe() {
            return gameExe;
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
    }
}
