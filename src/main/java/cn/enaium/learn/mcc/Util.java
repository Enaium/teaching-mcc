package cn.enaium.learn.mcc;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * @author Enaium
 */
public class Util {

    public static String username = "";
    public static String password = "";

    public static String doPost(URL url, String post) throws IOException {
        byte[] bytes = post.getBytes(StandardCharsets.UTF_8);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
        con.setRequestProperty("Content-Length", String.valueOf(bytes.length));
        IOUtils.write(bytes, con.getOutputStream());
        return IOUtils.toString(con.getInputStream(), StandardCharsets.UTF_8);
    }
}
