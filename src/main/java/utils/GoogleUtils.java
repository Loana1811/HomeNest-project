/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

import java.io.*;
import java.net.*;
import org.json.simple.*;
import org.json.simple.parser.*;

public class GoogleUtils {

    private static final String CLIENT_ID = "105183721878-bqd17ufa649vuo2qqogsjiqbuboskpin.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "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";
    private static final String REDIRECT_URI = "http://localhost:8080/HomeNest/login-google";
    private static final String TOKEN_ENDPOINT = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_ENDPOINT = "https://www.googleapis.com/oauth2/v2/userinfo";

    public static String getToken(String code) throws IOException, ParseException {
        URL url = new URL(TOKEN_ENDPOINT);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);

        String params = "code=" + code
                + "&client_id=" + CLIENT_ID
                + "&client_secret=" + CLIENT_SECRET
                + "&redirect_uri=" + REDIRECT_URI
                + "&grant_type=authorization_code";

        OutputStream os = conn.getOutputStream();
        os.write(params.getBytes());
        os.flush();
        os.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder resp = new StringBuilder();
        String input;
        while ((input = in.readLine()) != null) {
            resp.append(input);
        }
        in.close();

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(resp.toString());
        return (String) json.get("access_token");
    }

    public static GoogleUser getUserInfo(String accessToken) throws IOException, ParseException {
        URL url = new URL(USER_INFO_ENDPOINT + "?access_token=" + accessToken);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder resp = new StringBuilder();
        String input;
        while ((input = in.readLine()) != null) {
            resp.append(input);
        }
        in.close();

        JSONParser parser = new JSONParser();
        JSONObject json = (JSONObject) parser.parse(resp.toString());

        GoogleUser user = new GoogleUser();
        user.setEmail((String) json.get("email"));
        user.setName((String) json.get("name"));
        return user;
    }
}
        
