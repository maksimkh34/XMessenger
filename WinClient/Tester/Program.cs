using Web;

namespace Tester
{
    internal class Program
    {
        private static void Main(string[] args)
        {
            Config.Init();
            Message msg = new("Hello igor", 111, 444);
            var response = NetManager.Send(msg).Result;
            Console.WriteLine(response.StatusCode == 200
                ? "Success! "
                : $"Error {response.StatusCode}: {response.Message}");
        }

    }
}
