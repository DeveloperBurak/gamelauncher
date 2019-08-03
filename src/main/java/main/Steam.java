package main;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;


public class Steam {
    private static final String steamAPIKey = "F43B1DDF20E0781DBFC68B8B68E0255B";
    private String steamID;
    private static SteamUser userInfo;

    public void initUser(String steamUserName){
        System.out.println("Fetching Steam Infos...");
        try {
            String id = this.getSteamIDFromURLName(steamUserName);
            this.steamID = id;
            userInfo = getSteamInfoFromAPI();
        } catch (IOException e) {
//            System.out.println(e.getMessage());
        }
    }

    public static SteamUser getUser(){
        return userInfo;
    }

    public void printUser(){
        System.out.println(userInfo);
    }

    private String getSteamIDFromURLName(String username) throws IOException {
        URL url = new URL("http://api.steampowered.com/ISteamUser/ResolveVanityURL/v0001/?key=" + steamAPIKey + "&vanityurl=" + username);
        InputStreamReader reader = new InputStreamReader(url.openStream());
//        System.out.println(new Gson().fromJson(reader, JsonObject.class)); // for debug
        Response response = new Gson().fromJson(reader, Response.class);
        return response.getResponse().getSteamID();
    }

    private SteamUser getSteamInfoFromAPI() throws IOException {
        URL url = new URL("http://api.steampowered.com/ISteamUser/GetPlayerSummaries/v0002/?key=" + steamAPIKey + "&steamids=" + this.steamID);
        InputStreamReader reader = new InputStreamReader(url.openStream());
        Response response = new Gson().fromJson(reader, Response.class);
        return response.getResponse().getPlayers().get(0);
    }

    private void getSteamRealName() {

    }

    class Response {
        SteamPlayers response;

        SteamPlayers getResponse() {
            return response;
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
