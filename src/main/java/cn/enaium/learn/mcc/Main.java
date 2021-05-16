package cn.enaium.learn.mcc;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

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
        var gameDir = new File(".", ".minecraft");

        var jvmArgs = "-XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=16M -XX:-UseAdaptiveSizePolicy -XX:-OmitStackTraceInFastThrow -Xmn128m -Xmx1792m";
        var mainClass = "net.minecraft.client.main.Main";
        var username = "Player" + new Random().nextInt(999);
        var titleVersion = "\" " + name + " " + version + " \"";
        var assetsDir = new File(gameDir, "assets");
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

        var libraryDir = new File(gameDir, "libraries");

        var nativeDir = new File(versionDir, "natives");

        for (JsonElement jsonElement : gameJson.get("libraries").getAsJsonArray()) {
            var downloads = jsonElement.getAsJsonObject().get("downloads").getAsJsonObject();
            if (downloads.has("artifact")) {
                var artifact = downloads.get("artifact").getAsJsonObject();
                var path = new File(libraryDir, artifact.get("path").getAsString());
                libraries.append(path).append(";");
                if (!path.exists()) {
                    FileUtils.writeByteArrayToFile(path, IOUtils.toByteArray(new URL(artifact.get("url").getAsString())));
                }
            }

            if (downloads.has("classifiers")) {
                var classifiers = downloads.get("classifiers").getAsJsonObject();
                var nativeName = "natives-linux";
                var osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
                if (osName.contains("win")) {
                    nativeName = "natives-windows";
                    if (!classifiers.has(nativeName)) {
                        nativeName = "natives-windows-64";
                    }
                } else if (osName.contains("mac")) {
                    nativeName = "natives-osx";
                    if (!classifiers.has(nativeName)) {
                        nativeName = "natives-macos";
                    }
                }

                if (!classifiers.has(nativeName)) {
                    continue;
                }

                var path = new File(libraryDir, classifiers.get(nativeName).getAsJsonObject().get("path").getAsString());
                var url = classifiers.get(nativeName).getAsJsonObject().get("url").getAsString();

                if (!path.exists()) {
                    FileUtils.writeByteArrayToFile(path, IOUtils.toByteArray(new URL(url)));
                } else {
                    var jarFile = new JarFile(path);
                    var entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        var jarEntry = entries.nextElement();
                        if (jarEntry.isDirectory() || jarEntry.getName().contains("META-INF")) {
                            continue;
                        }

                        var inputStream = jarFile.getInputStream(jarEntry);
                        FileUtils.writeByteArrayToFile(new File(nativeDir, jarEntry.getName()), IOUtils.toByteArray(inputStream));
                        inputStream.close();
                    }
                    jarFile.close();
                }
            }
        }

        libraries.append(gameJarFile);

        var indexDir = new File(assetsDir, "indexes");
        if (!indexDir.exists()) {
            indexDir.mkdir();
        }

        var objectDir = new File(assetsDir, "objects");
        if (!objectDir.exists()) {
            objectDir.mkdir();
        }

        var indexContent = IOUtils.toString(new URL(gameJson.get("assetIndex").getAsJsonObject().get("url").getAsString()), StandardCharsets.UTF_8);
        var indexObject = gson.fromJson(indexContent, JsonObject.class);
        for (Map.Entry<String, JsonElement> objects : indexObject.get("objects").getAsJsonObject().entrySet()) {
            var assetObject = gson.fromJson(objects.getValue(), AssetObject.class);
            var objectIndexDir = new File(objectDir, assetObject.getHash().substring(0, 2));
            if (!objectIndexDir.exists()) {
                objectIndexDir.mkdir();
            }
            var objectIndexFile = new File(objectIndexDir, assetObject.getHash());

            if (!objectIndexFile.exists()) {
                FileUtils.writeByteArrayToFile(objectIndexFile, IOUtils.toByteArray(new URL("https://resources.download.minecraft.net/" + assetObject.getHash().substring(0, 2) + "/" + assetObject.getHash())));
            }
        }

        FileUtils.writeStringToFile(new File(indexDir, gameVersion + ".json"), indexContent, StandardCharsets.UTF_8);

        var param = """
                {
                    "agent": {
                        "name": "Minecraft",
                        "version": 1
                    },
                    "username": "${username}",
                    "password": "${password}",
                    "clientToken": "${uuid}",
                    "requestUser": true
                }
                """;

        param = param.replace("${username}", Util.username);
        param = param.replace("${password}", Util.password);
        param = param.replace("${uuid}", UUID.randomUUID().toString());

        var ret = gson.fromJson(Util.doPost(new URL("https://authserver.mojang.com/authenticate"), param), JsonObject.class);
        var selectedProfile = ret.get("selectedProfile").getAsJsonObject();
        username = selectedProfile.get("name").getAsString();
        uuid = selectedProfile.get("id").getAsString();
        accessToken = ret.get("accessToken").getAsString();

        var text = java + "/bin/java.exe " + jvmArgs + " -Djava.library.path=" + nativeDir + " -Dminecraft.launcher.brand="
                + name + " -Dminecraft.launcher.version=" + version + " -cp " + libraries + " " + mainClass
                + " --username " + username + " --version " + titleVersion +
                " --gameDir " + gameDir + " --assetsDir " + assetsDir + " --assetIndex " + gameVersion
                + " --uuid " + uuid + " --accessToken " + accessToken + " --userProperties {} --userType mojang "
                + "--width 854 --height 480";

        var exec = Runtime.getRuntime().exec(text);
        BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
        }
    }
}
