using System.Windows.Input;

namespace Model
{
    public class DelegateCommand(Action<object> openAction, Predicate<object>? canExecutePredicate, Action? closeWindow = null) : ICommand
    {
        public static bool DefaultCanExecute(object param) => true;

        public event EventHandler? CanExecuteChanged;

        public bool CanExecute(object? parameter)
        {
            return canExecutePredicate?.Invoke(parameter!) ?? true;
        }

        public void Execute(object? parameter)
        {
            openAction(parameter!);
            try { closeWindow?.Invoke(); }
            catch (System.ComponentModel.Win32Exception) { }
        }

        public void RaiseCanExecuteChanged() => CanExecuteChanged?.Invoke(this, EventArgs.Empty);
    }
}
