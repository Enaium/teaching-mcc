package cn.enaium.learn.mcc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author Enaium
 */
@SuppressWarnings("ResultOfMethodCallIgnored")
public class Main {
    public static void main(String[] args) throws Exception {
        var name = "mcc";
        var version = "1.0";
        var java = System.getProperty("java.home");
        var gameVersion = "1.8.9";
        var gameDir = new File(".",".minecraft");

        var jvmArgs = "-XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=16M -XX:-UseAdaptiveSizePolicy -XX:-OmitStackTraceInFastThrow -Xmn128m -Xmx1792m";
        var natives = "C:/Users/Enaium/AppData/Roaming/.minecraft/versions/1.8.9/natives";
        var mainClass = "net.minecraft.client.main.Main";
        var username = "Enaium";
        var titleVersion = "\" " + name + " " + version + " \"";
        var assetsDir = "C:/Users/Enaium/AppData/Roaming/.minecraft/assets";
        var assetIndex = "1.8";
        var uuid = "0";
        var accessToken = "0";

        var gson = new Gson();
        var gameListJson = IOUtils.toString(new URL("https://launchermeta.mojang.com/mc/game/version_manifest_v2.json"), StandardCharsets.UTF_8);
        var gameList = gson.fromJson(gameListJson, JsonObject.class).get("versions").getAsJsonArray();
        var gameJsonURL = "";
        for (JsonElement jsonElement : gameList) {
            if (jsonElement.getAsJsonObject().get("id").getAsString().equals(gameVersion)) {
                gameJsonURL = jsonElement.getAsJsonObject().get("url").getAsString();
            }
        }

        if (gameJsonURL.equals("")) {
            throw new RuntimeException(gameVersion + " Not Found!");
        }

        var gameJson = gson.fromJson(IOUtils.toString(new URL(gameJsonURL), StandardCharsets.UTF_8), JsonObject.class);
        var gameJarUrl = gameJson.get("downloads").getAsJsonObject().get("client").getAsJsonObject().get("url").getAsString();

        var versionsDir = new File(gameDir, "versions");

        if (!versionsDir.exists()) {
            versionsDir.mkdir();
        }

        var versionDir = new File(versionsDir, gameVersion);

        if (!versionDir.exists()) {
            versionDir.mkdir();
        }

        var gameJarFile = new File(versionDir, gameVersion + ".jar");

        if (!gameJarFile.exists()) {
            FileUtils.writeByteArrayToFile(gameJarFile, IOUtils.toByteArray(new URL(gameJarUrl)));
        }

        var libraries = new StringBuilder();

        var libraryDir = new File(gameDir,"libraries");

        for (JsonElement jsonElement : gameJson.get("libraries").getAsJsonArray()) {
            var downloads = jsonElement.getAsJsonObject().get("downloads").getAsJsonObject();
            if (downloads.has("artifact")) {
                var artifact = downloads.get("artifact").getAsJsonObject();
                var path = new File(libraryDir, artifact.get("path").getAsString());
                libraries.append(path).append(";");
                if (!path.exists()) {
                    FileUtils.writeByteArrayToFile(path,IOUtils.toByteArray(new URL(artifact.get("url").getAsString())));
                }
            }
        }

        libraries.append(gameJarFile);


        var text = java + "/bin/java.exe " + jvmArgs + " -Djava.library.path=" + natives + " -Dminecraft.launcher.brand="
                + name + " -Dminecraft.launcher.version=" + version + " -cp " + libraries + " " + mainClass
                + " --username " + username + " --version " + titleVersion +
                " --gameDir " + gameDir + " --assetsDir " + assetsDir + " --assetIndex " + assetIndex
                + " --uuid " + uuid + " --accessToken " + accessToken + " --userProperties {} --userType mojang "
                + "--width 854 --height 480";

        try {
            var exec = Runtime.getRuntime().exec(text);
            BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }
}
