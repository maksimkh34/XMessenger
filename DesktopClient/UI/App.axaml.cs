using System;
using Avalonia;
using Avalonia.Controls.ApplicationLifetimes;
using Avalonia.Markup.Xaml;
using Context;
using Model;
using Serilog;

namespace UI
{
    public partial class App : Application
    {
        private readonly MainModel _model = new();

        public override void Initialize()
        {
            AvaloniaXamlLoader.Load(this);
        }

        public override async void OnFrameworkInitializationCompleted()
        {
            if (ApplicationLifetime is IClassicDesktopStyleApplicationLifetime desktop)
            {
                desktop.MainWindow = new MainWindow();
            }

            base.OnFrameworkInitializationCompleted(); 
            await _model.InitMainModel();

            var regRsp = await Client.Register("email@mail.ru", "l0gin", "pwd");
            var statusRsp = await Client.TrySendOnline();
        }
    }
}