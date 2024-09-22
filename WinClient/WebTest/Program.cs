using System.Text;
using Newtonsoft.Json;


namespace WebTest
{
    public class MyObject
    {
        public int Id { get; set; }
        public string? Name { get; set; }
    }

    public class Program
    {
        private const string ServerUrl = @"http://localhost:1588";
        private static readonly HttpClient Client = new();

        public static async Task Main(string[] args)
        {
            var myObject = new MyObject { Id = 15, Name = "Test" };
            var json = JsonConvert.SerializeObject(myObject);
            var content = new StringContent(json, Encoding.UTF8, "application/json");

            var response = await Client.PostAsync(ServerUrl, content);
            var responseString = await response.Content.ReadAsStringAsync();

            Console.WriteLine(responseString);
        }
    }
}