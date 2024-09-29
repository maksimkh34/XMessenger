package net.pkg;

public class DefaultPackages {
    public static Package invalidMethod = new Package(405,
            "Method not allowed (expected: POST)",
            true);

    public static Package unauthorized = new Package(401,
            "Unauthorized",
            true);

    public static Package invalidDataFormat = new Package(400,
            "Invalid data format",
            true);

    public static Package success = new Package(200,
            "OK",
            true);
    public static Package invalidTDeviceID = new Package(461,
            "No entities.TDevice ID found",
            true);
}
