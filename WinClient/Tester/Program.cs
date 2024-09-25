using Web;

namespace Tester;

internal class Program
{
    private static async Task Main(string[] args)
    {
        Config.Init();
        await NetManager.RequestPublicKey();
        Message msg = new("msg", 111, 444);
        var response = NetManager.Send(msg).Result;
        Console.WriteLine(response.StatusCode == 200
            ? "Success! "
            : $"Error {response.StatusCode}: {response.Message}");
    }
}