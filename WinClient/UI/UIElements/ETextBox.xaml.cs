using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace UI.UIElements
{
    /// <summary>
    /// Логика взаимодействия для ETextBox.xaml
    /// </summary>
    public partial class ETextBox
    {
        public ETextBox()
        {
            InitializeComponent();
        }

        private void MainTextBox_OnTextChanged(object sender, TextChangedEventArgs e)
        {
            if (Placeholder == null) return;
            if (string.IsNullOrEmpty(MainTextBox.Text))
            {
                Placeholder.VerticalAlignment = VerticalAlignment.Bottom;
                Placeholder.FontSize = MainTextBox.FontSize;
                Placeholder.Margin = new Thickness(1, 0, 10, 1);
            }
            else
            {
                Placeholder.VerticalAlignment = VerticalAlignment.Top;
                Placeholder.FontSize = MainTextBox.FontSize * 0.5;
                Placeholder.Margin = new Thickness(2,0,0,0);
            }
        }

        private void MainTextBox_OnGotFocus(object sender, RoutedEventArgs e)
        {
            
        }

        private void MainTextBox_OnLostFocus(object sender, RoutedEventArgs e)
        {
            
        }

        private void Placeholder_OnGotFocus(object sender, RoutedEventArgs e)
        {
            MainTextBox.Focus();
        }
    }
}
