package main;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;


public class Steam {
    private static final String steamAPIKey = "F43B1DDF20E0781DBFC68B8B68E0255B";
    private static String steamID;
    private static SteamUser userInfo;

    public static boolean isExistsUserProperties() {
        String userDataRoot = FileController.getUserDirectory().getAbsolutePath();
        File steamDatas = new File(userDataRoot + "steam.json");
        System.out.println("checking files...");
        if (steamDatas.exists()) {
            System.out.println("user data exists.");
            return true;
        } else {
            System.out.println("user data not exists.");
            return false;
        }
    }

    public static void initUser(String steamUserName) {
        System.out.println("Fetching Steam Infos...");
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
        URL url = new URL("http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=" + steamAPIKey + "&vanityurl=" + username);
        InputStreamReader reader = new InputStreamReader(url.openStream());
//        System.out.println(new Gson().fromJson(reader, JsonObject.class)); // for debug
        Response response = new Gson().fromJson(reader, Response.class);
        return response.getPlayerResponse().getSteamID();
    }

    private static SteamUser getSteamInfoFromAPI() throws IOException {
        URL url = new URL("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + steamAPIKey + "&steamids=" + steamID);
        InputStreamReader reader = new InputStreamReader(url.openStream());
        Response response = new Gson().fromJson(reader, Response.class);
        return response.getPlayerResponse().getPlayers().get(0);
    }

    private SteamGameNews getSteamGameNews() throws IOException {
        URL url = new URL("https://api.steampowered.com/ISteamNews/GetNewsForApp/v2/?appid=359320&count=3");
        InputStreamReader reader = new InputStreamReader(url.openStream());
        Response response = new Gson().fromJson(reader, Response.class);
        return response.getGameNewsResponse();
    }

    class SteamGameNews {
        SteamGameNews appnews;

        SteamGameNews getGameNews() {
            return appnews;
        }
    }

    class Response {
        SteamPlayers response;
        SteamGameNews appnews;

        SteamPlayers getPlayerResponse() {
            return response;
        }

        SteamGameNews getGameNewsResponse() {
            return appnews;
        }
    }

    class SteamPlayers {
        List<SteamUser> players;
        String steamid;

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

//        public String getName() {
//            return this.name;
//        }
    }


}
