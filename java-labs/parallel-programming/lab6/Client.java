import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

public class Client {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 12345;

    public static void main(String[] args) {
        try (BufferedReader consoleReader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
                Socket socket = new Socket(HOST, PORT);
                // Настраиваем потоки для общения с сервером
                BufferedReader serverReader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                PrintWriter serverWriter = new PrintWriter(
                        new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8), true)) {

            System.out.println("Подключено к серверу " + HOST + ":" + PORT);

            System.out.print("Введите ваш никнейм: ");
            String nickname = consoleReader.readLine();
            if (nickname == null || nickname.trim().isEmpty()) {
                nickname = "Anonymous";
            }
            serverWriter.println(nickname.trim());
            System.out.println("Можно писать сообщения. Команда exit_server выключает сервер.");

            // Запускаем поток для чтения сообщений от сервера
            Thread receiveThread = new Thread(() -> {
                try {
                    String message;
                    while ((message = serverReader.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("Ошибка чтения от сервера: " + e.getMessage());
                }
            });
            // Делаем поток демоном, чтобы он не блокировал завершение программы
            receiveThread.setDaemon(true);
            // Запускаем поток для получения сообщений от сервера
            receiveThread.start();

            // Читаем сообщения с консоли и отправляем их на сервер
            String userMessage;
            while ((userMessage = consoleReader.readLine()) != null) {
                serverWriter.println(userMessage);
                if ("exit_server".equalsIgnoreCase(userMessage.trim())) {
                    break;
                }
            }

            System.out.println("Соединение закрыто сервером.");
        } catch (IOException e) {
            System.out.println("Ошибка клиента: " + e.getMessage());
        }
    }
}
