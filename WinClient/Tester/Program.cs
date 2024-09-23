using Web;

namespace Tester
{
    internal class Program
    {
        private static void Main(string[] args)
        {
            Web.Config.Load();
            Message msg = new("Hello world!", 111, 444);
            var response = Web.NetManager.Send(msg).Result;
            Console.WriteLine(response.StatusCode == 200
                ? "Success! "
                : $"Error {response.StatusCode}: {response.Message}");
        }

    }
}
