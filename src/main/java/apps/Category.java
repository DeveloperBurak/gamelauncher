package apps;

import helper.FileHelper;

import java.io.File;
import java.util.ArrayList;

public class Category {
    private String category_name;
    private ArrayList<Game> games = new ArrayList<>();

    public Category(String category_name) {
        this.category_name = category_name;
    }

    public String getCategoryName() {
        return category_name;
    }

    public ArrayList<Game> getGames() {
        return this.games;
    }

    public void addGame(File gameExe, String gameText, File gameImage) {
        Game game = new Game(gameExe, gameText, gameImage);
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
}
