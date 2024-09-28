using System.Configuration;
using System.Data;
using System.Windows;
using System.Windows.Navigation;
using Model.Auth;

namespace UI
{
    /// <summary>
    /// Interaction logic for App.xaml
    /// </summary>
    public partial class App
    {
        private readonly Model.MainModel _model = new();

        private async void App_OnStartup(object sender, StartupEventArgs e)
        {
            await _model.InitMainModel();
        }
    }

}
