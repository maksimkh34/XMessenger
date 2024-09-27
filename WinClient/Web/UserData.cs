namespace Web
{
    public class UserData
    {
        public string? Login { get; set; }
        public string? PUserName { get; set; }       // p - профиль. Данные p_ всего лишь текст в профиле, не больше
        public string? PUserSurname { get; set; }
        public string? Password { get; set; }
        public string? Email { get; set; }

        public string? Pk
        {
            get => null;
            set => _ = value;
        } // публичный stc

        public string? Secret { get; set; }
        public string? Id { get; set; }
    }
}