using System;
using Avalonia;
using Avalonia.Controls.ApplicationLifetimes;
using Avalonia.Markup.Xaml;
using Context;
using Model;
using Serilog;

namespace UI
{
    public class App : Application
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
            try
            {
                await _model.InitMainModel();
            }
            catch (System.Net.Http.HttpRequestException)
            {
                // tell user that we work offline
            }
        }
    }
}