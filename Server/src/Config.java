import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final String ConfigFileName = "config.conf";
    private static String GetConfigPath() {
        // debug
        Path path = Paths.get(System.getProperty("user.dir"));
        Path parentPath = path.getParent();
        return parentPath + "/" + ConfigFileName;
        // release
        //return "config.conf";
    }
    private static Map<String, String> values = new HashMap<>();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void load() throws IOException{
        File configFile = new File(GetConfigPath());
        if (!configFile.exists()) return;
        String json = new String(Files.readAllBytes(Paths.get(GetConfigPath())));
        values = objectMapper.readValue(json, new TypeReference<Map<String, String>>() {});
    }

    public static void save() throws IOException{
        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(values);
        Files.write(Paths.get(GetConfigPath()), json.getBytes());
    }

    public static String getValue(String key) {
        if (values.containsKey(key)) {
            return values.get(key);
        } else {
            throw new IllegalArgumentException("No value found with key " + key + ".");
        }
    }

    public static void setValue(String key, String value) {
        values.put(key, value);
    }

    // Константы для ключей конфигурации
    public static final String SERVER_IP = "ServerIP";
    public static final String SERVER_PORT = "ServerPort";
    public static final String SERVER_PATH = "ServerPath";
}
