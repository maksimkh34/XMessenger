using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;
using Model;
using Model.Auth;
using Web.NetInteraction;

namespace UI.ViewModel
{
    internal class AuthViewModel
    {
        public ICommand TryLoginCommand { get; }
        public ICommand TryRegisterCommand { get; }

        public required string Email { get; set; } = "email@m.l";
        public required string Password { get; set; } = "12345";
        public required string Login { get; set; } = "login";

        public AuthViewModel()
        {
            TryLoginCommand = new DelegateCommand(TryLogin, null);
            TryRegisterCommand = new DelegateCommand(TryRegister, null);
        }

        public async void TryLogin(object param)
        {
            AuthResponse result;
            if (Email.Contains('@') && Email.Contains('.'))
            {
                MessageBox.Show("Вход с помощью почты...");
                result = await Auth.LoginEmail(Email, Password);
            }
            else
            {
                MessageBox.Show("Вход с помощью логина...");
                result = await Auth.LoginLogin(Email, Password);
            }

            switch (result.Result)
            {
                case AuthResult.AUTH_SUCCESS:
                    MessageBox.Show("Вы успешно вошли! "); 
                    break;
                case AuthResult.EMAIL_WAITING_CODE:
                    MessageBox.Show("Код на email отправлен. ");
                    break;
                case AuthResult.ERROR_PROCESSING:
                    MessageBox.Show("Ошибка обработки запроса. ");
                    break;
                case AuthResult.INVALID_PASSWORD:
                    MessageBox.Show("Неверный пароль. ");
                    break;
                case AuthResult.USER_NOT_REGISTERED:
                    MessageBox.Show("Вы не зарегистрированы! ");
                    break;
                default:
                    throw new ArgumentOutOfRangeException();
            }
        }

        public async void TryRegister(object param)
        {
            var result = await Auth.Register(Email, Login, Password);

            switch (result.Result)
            {
                case AuthResult.AUTH_SUCCESS:
                    MessageBox.Show("Вы успешно зарегистрировались! ");
                    break;
                case AuthResult.EMAIL_WAITING_CODE:
                    MessageBox.Show("Код на email отправлен. ");
                    break;
                case AuthResult.ERROR_PROCESSING:
                    MessageBox.Show("Ошибка обработки запроса. ");
                    break;
                case AuthResult.INVALID_PASSWORD:
                    MessageBox.Show("Неверный пароль. ");
                    break;
                case AuthResult.USER_NOT_REGISTERED:
                    MessageBox.Show("Вы не зарегистрированы! ");
                    break;
                default:
                    throw new ArgumentOutOfRangeException();
            }
        }
    }
}
