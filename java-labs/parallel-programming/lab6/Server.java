import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server {
    private static final int PORT = 12345;
    // Список клиентов, использующий CopyOnWriteArrayList для безопасной работы в
    // многопоточном окружении
    private static final CopyOnWriteArrayList<ClientConnection> clients = new CopyOnWriteArrayList<>();
    private static volatile boolean running = true;
    private static ServerSocket serverSocket;

    public static void main(String[] args) {
        // Запускаем поток для принятия клиентов
        Thread acceptThread = new Thread(Server::acceptClients);
        acceptThread.start();

        System.out.println("Сервер запущен на порту " + PORT + ".");
        System.out.println("Введите сообщение для рассылки клиентам.");
        System.out.println("Команда exit_server завершает сервер.");

        // Читаем сообщения с консоли и отправляем их всем клиентам
        try (BufferedReader consoleReader = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8))) {
            String message;
            while (running && (message = consoleReader.readLine()) != null) {
                if ("exit_server".equalsIgnoreCase(message.trim())) {
                    shutdownServer();
                    break;
                }
                broadcast("[SERVER] " + message, null);
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения консоли сервера: " + e.getMessage());
        }
    }

    // Метод для принятия клиентов
    private static void acceptClients() {
        try {
            serverSocket = new ServerSocket(PORT);
            while (running) {
                // Ждем подключения клиента
                Socket clientSocket = serverSocket.accept();
                // Создаем объект для управления подключением клиента
                ClientConnection client = new ClientConnection(clientSocket);

                // Читаем никнейм клиента
                String nickname = client.reader.readLine();
                if (nickname == null || nickname.trim().isEmpty()) {
                    client.close();
                    continue;
                }
                client.nickname = nickname.trim();

                clients.add(client);

                System.out.println("Клиент подключился: " + client.getClientId());
                broadcast("[SERVER] Подключился пользователь " + client.nickname, null);

                // Запускаем поток для обработки сообщений от клиента
                Thread clientThread = new Thread(() -> handleClient(client));
                clientThread.start();
            }
        } catch (IOException e) {
            if (running) {
                System.out.println("Ошибка сервера: " + e.getMessage());
            }
        }
    }

    // Метод для обработки сообщений от клиента
    private static void handleClient(ClientConnection client) {
        try {
            // Читаем сообщения от клиента и рассылаем их всем остальным
            String message;
            while ((message = client.reader.readLine()) != null) {
                if ("exit_server".equalsIgnoreCase(message.trim())) {
                    shutdownServer();
                    break;
                }
                String outgoing = "[" + client.nickname + "] " + message;
                System.out.println(outgoing);
                // Рассылаем сообщение всем клиентам, кроме отправителя
                broadcast(outgoing, client);
            }
        } catch (IOException e) {
            System.out.println("Ошибка клиента " + client.getClientId() + ": " + e.getMessage());
        } finally {
            // Удаляем клиента из списка и закрываем его соединение
            clients.remove(client);
            client.close();
            if (running) {
                System.out.println("Клиент отключился: " + client.getClientId());
                broadcast("[SERVER] Отключился пользователь " + client.nickname, null);
            }
        }
    }

    // Метод для рассылки сообщений всем клиентам, кроме исключенного
    private static void broadcast(String message, ClientConnection exclude) {
        for (ClientConnection client : clients) {
            // Если клиент является исключением, пропускаем его
            if (client == exclude) {
                continue;
            }
            // Отправляем сообщение клиенту
            client.writer.println(message);
            if (client.writer.checkError()) {
                clients.remove(client);
                client.close();
            }
        }
    }

    // Метод для безопасного завершения работы сервера
    private static synchronized void shutdownServer() {
        if (!running) {
            return;
        }

        running = false;
        System.out.println("Сервер завершает работу...");
        broadcast("[SERVER] Сервер выключается.", null);

        for (ClientConnection client : clients) {
            client.close();
        }
        clients.clear();

        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
        } catch (IOException e) {
            System.out.println("Ошибка при закрытии ServerSocket: " + e.getMessage());
        }
    }

    // Вложенный класс для представления подключения клиента
    private static class ClientConnection {
        private final Socket socket;
        private final BufferedReader reader;
        private final PrintWriter writer;
        private String nickname;

        // Конструктор для инициализации клиента
        private ClientConnection(Socket socket) throws IOException {
            this.socket = socket;
            this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            this.writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8),
                    true);
        }

        // Метод для получения идентификатора клиента (IP:порт)
        private String getClientId() {
            return socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
        }

        // Метод для закрытия соединения с клиентом
        private void close() {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
    }
}
