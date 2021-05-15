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
        var libraries = "C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/com/mojang/netty/1.6/netty-1.6.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/oshi-project/oshi-core/1.1/oshi-core-1.1.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/net/java/dev/jna/jna/3.4.0/jna-3.4.0.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/net/java/dev/jna/platform/3.4.0/platform-3.4.0.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/com/ibm/icu/icu4j-core-mojang/51.2/icu4j-core-mojang-51.2.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/net/sf/jopt-simple/jopt-simple/4.6/jopt-simple-4.6.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/com/paulscode/codecjorbis/20101023/codecjorbis-20101023.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/com/paulscode/codecwav/20101023/codecwav-20101023.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/com/paulscode/libraryjavasound/20101123/libraryjavasound-20101123.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/com/paulscode/librarylwjglopenal/20100824/librarylwjglopenal-20100824.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/com/paulscode/soundsystem/20120107/soundsystem-20120107.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/io/netty/netty-all/4.0.23.Final/netty-all-4.0.23.Final.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/com/google/guava/guava/17.0/guava-17.0.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/org/apache/commons/commons-lang3/3.3.2/commons-lang3-3.3.2.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/commons-io/commons-io/2.4/commons-io-2.4.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/commons-codec/commons-codec/1.9/commons-codec-1.9.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/net/java/jinput/jinput/2.0.5/jinput-2.0.5.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/net/java/jutils/jutils/1.0.0/jutils-1.0.0.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/com/google/code/gson/gson/2.2.4/gson-2.2.4.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/com/mojang/authlib/1.5.21/authlib-1.5.21.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/com/mojang/realms/1.7.59/realms-1.7.59.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/org/apache/commons/commons-compress/1.8.1/commons-compress-1.8.1.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/org/apache/httpcomponents/httpclient/4.3.3/httpclient-4.3.3.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/commons-logging/commons-logging/1.1.3/commons-logging-1.1.3.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/org/apache/httpcomponents/httpcore/4.3.2/httpcore-4.3.2.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/org/apache/logging/log4j/log4j-api/2.0-beta9/log4j-api-2.0-beta9.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/org/apache/logging/log4j/log4j-core/2.0-beta9/log4j-core-2.0-beta9.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/org/lwjgl/lwjgl/lwjgl/2.9.4-nightly-20150209/lwjgl-2.9.4-nightly-20150209.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/org/lwjgl/lwjgl/lwjgl_util/2.9.4-nightly-20150209/lwjgl_util-2.9.4-nightly-20150209.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/libraries/tv/twitch/twitch/6.5/twitch-6.5.jar;C:/Users/Enaium/AppData/Roaming/.minecraft/versions/1.8.9/1.8.9.jar";
        var mainClass = "net.minecraft.client.main.Main";
        var username = "Enaium";
        var titleVersion = "\" " + name + " " + version + " \"";
        var assetsDir = "C:/Users/Enaium/AppData/Roaming/.minecraft/assets";
        var assetIndex = "1.8";
        var uuid = "0";
        var accessToken = "0";
        var text = java + "/bin/java.exe " + jvmArgs + " -Djava.library.path=" + natives + " -Dminecraft.launcher.brand="
                + name + " -Dminecraft.launcher.version=" + version + " -cp " + libraries + " " + mainClass
                + " --username " + username + " --version " + titleVersion +
                " --gameDir " + gameDir + " --assetsDir " + assetsDir + " --assetIndex " + assetIndex
                + " --uuid " + uuid + " --accessToken " + accessToken + " --userProperties {} --userType mojang "
                + "--width 854 --height 480";


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
