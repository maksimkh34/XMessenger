namespace Tester
{
    internal class Program
    {
        private static void Main(string[] args)
        {
            Web.Config.Load();
            var response = Web.NetManager.Send(new MyObject() { Id = 1352345, Name = "TEST!!!!" }).Result;
            Console.WriteLine(response.StatusCode == 200
                ? "Success! "
                : $"Error {response.StatusCode}: {response.Message}");
            MyObject obj = new MyObject();
        }

        public class MyObject()
        {
            public string? Name { get; set; }
            public int Id { get; set; }

        }
    }
}
