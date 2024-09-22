public class Logger {
    public static void Log(String message, LogLevel level) {
        // тут нужно логировать сообщения. в зависимости
        // от левела можно поменять цвет, вывести текущее время, стэк выовов, и т.д.

        // также неплохо было бы добавить лог в файл, именем которого было бы время запуска сервера
        // а пока что логи выглядят так
        String msg = String.format("[%s]: %s", level.toString(), message);
        System.out.print(msg);
    }
}
