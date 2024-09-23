using Newtonsoft.Json;

namespace Web
{
    public static class Config
    {
        private static string ConfigPath {
            get
            {
#if DEBUG
                return "../../../../../config.conf";
#else
                return "config.conf";
#endif
            }
        }
        private static Dictionary<string, string> _values = [];

        public static void Init()
        {
            if (!File.Exists(ConfigPath)) return;
            var json = File.ReadAllText(ConfigPath);
            _values = JsonConvert.DeserializeObject<Dictionary<string, string>>(json) ?? [];
        }

        public static void Save()
        {
            var json = JsonConvert.SerializeObject(_values, Formatting.Indented);
            File.WriteAllText(ConfigPath, json);
        }

        public static string GetValue(string key)
        {
            if (_values.TryGetValue(key, out var value)) return value;
            throw new KeyNotFoundException("No value found with key " + key + ". ");
        }

        public static void SetValue(string key, string value)
        {
            _values[key] = value;
        }

        // ReSharper disable once InconsistentNaming
        public static string ServerIP = "ServerIP";
        public static string ServerPort = "ServerPort";
        public static string ServerPath = "ServerPath";
    }
}
