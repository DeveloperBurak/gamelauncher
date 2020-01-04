package main;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


public class SteamAPI {
    private static final String STEAM_API_KEY = "F43B1DDF20E0781DBFC68B8B68E0255B";
    private static final File STEAM_USER_FILE = new File(FileController.getUserDirectory().getAbsolutePath() + File.separator + "steam.json");
    private static String steamID;
    private static SteamUser userInfo;

    public static boolean isExistsUserProperties() {
        System.out.println("exist");
        return STEAM_USER_FILE.exists();
    }

    public static void initUser(String steamUserName) {
//        System.out.println("Fetching Steam Infos...");
        try {
            steamID = getSteamIDFromURLName(steamUserName);
            userInfo = getSteamInfoFromAPI();
        } catch (IOException e) {
//            System.out.println(e.getMessage());
        }
    }



    public static SteamUser getUser() {
        return userInfo;
    }

    public static void printUser() {
        System.out.println(userInfo);
    }

    private static String getSteamIDFromURLName(String username) throws IOException {
        URL url = new URL("http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=" + STEAM_API_KEY + "&vanityurl=" + username);
        InputStreamReader reader = new InputStreamReader(url.openStream());
        try {
            Response response = new Gson().fromJson(reader, Response.class);
            if (response.getGeneralResponse().getSuccess().equals("1")) {
                return response.getGeneralResponse().getSteamID();
            } else if (response.getGeneralResponse().getSuccess().equals("42")) { // steam's code.
                return username;
            } else {
                return null;
            }
        } catch (NullPointerException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public static SteamUser readUserInfoFromFile() throws IOException {
        String fileReader = new String(Files.readAllBytes(Paths.get(STEAM_USER_FILE.getAbsolutePath())));
        Response response = new Gson().fromJson(fileReader, Response.class);
        return response.getGeneralResponse().getPlayers().get(0);
    }

    private static SteamUser getSteamInfoFromAPI() throws IOException {
        URL url = new URL("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + STEAM_API_KEY + "&steamids=" + steamID);
        InputStreamReader reader = new InputStreamReader(url.openStream());
        try {
            JsonObject response = new Gson().fromJson(reader, JsonObject.class);
            Response customResponse = new Gson().fromJson(response, Response.class);
            if (customResponse.getGeneralResponse().getPlayers().size() > 0) {
                SteamUser player = customResponse.getGeneralResponse().getPlayers().get(0);
                String fetchedUser = response.toString();
                File userInfoFile = STEAM_USER_FILE;
                if (FileController.writeFile(userInfoFile, fetchedUser)) { // write steam user infos.
                    String fileReader = new String(Files.readAllBytes(Paths.get(userInfoFile.getAbsolutePath())));
                    Response fileJSON = new Gson().fromJson(fileReader, Response.class);
                    if (fileJSON.getGeneralResponse().getPlayers().size() > 0) {
                        return fileJSON.getGeneralResponse().getPlayers().get(0);
                    } else {
                        return null;
                    }
                }
            } else {
                System.out.println("There is no user: " + steamID);
                return null;
            }


        } catch (UnsupportedOperationException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private SteamGameNews getSteamGameNews(String id) throws IOException {
        return getSteamGameNews(id, 3);
    }

    private SteamGameNews getSteamGameNews(String id, int count) throws IOException {
        URL url = new URL("https://api.steampowered.com/ISteamNews/GetNewsForApp/v2/?appid=" + id + "&count=" + count);
        InputStreamReader reader = new InputStreamReader(url.openStream());
        Response response = new Gson().fromJson(reader, Response.class);
        return response.getGameNewsResponse();
    }


    class Response {
        SteamPlayers response;
        SteamGameNews appnews;

        SteamPlayers getGeneralResponse() {
            return response;
        }

        SteamGameNews getGameNewsResponse() {
            return appnews;
        }
    }

    class SteamGameNews {
        SteamGameNews appnews;

        SteamGameNews getGameNews() {
            return appnews;
        }
    }

    class SteamPlayers {
        List<SteamUser> players;
        String steamid;
        String success;

        String getSuccess() {
            return success;
        }

        String getSteamID() {
            return steamid;
        }

        private List<SteamUser> getPlayers() {
            return players;
        }
    }

    public class SteamUser {
        private String personaname;
        private String realname;
        public String getRealName() {
            return this.realname;
        }
        public String getPersonaName() {
            return this.personaname;
        }
    }


}
