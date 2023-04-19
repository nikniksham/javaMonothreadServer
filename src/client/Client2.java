package client;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Client2 {

    private static Socket clientSocket; //сокет для общения
    private static BufferedReader reader; // нам нужен ридер читающий с консоли, иначе как
    // мы узнаем что хочет сказать клиент?
    private static BufferedReader in; // поток чтения из сокета
    private static BufferedWriter out; // поток записи в сокет

    public static void main(String[] args) {
        System.out.println("Клиент запущен");
        List<String> commands = new ArrayList<>();
        commands.add("12345678");
        Scanner scanner = new Scanner(System.in);
        String input = "";

        try {
            clientSocket = new Socket("localhost", 4004); // коннектимся
            reader = new BufferedReader(new InputStreamReader(System.in));
            // читать
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // писать
            out = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            boolean run = true;
            while (run) {
                CompletableFuture<String> waitMessageFromServer = CompletableFuture.supplyAsync(() -> wait_new_message(in));

                while (!waitMessageFromServer.isDone()) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//                    System.out.println("ConsoleInputReadTask run() called.");
                    do {
                        try {
                            if (br.ready()) {
                                commands.add(br.readLine());
                            }
                        } catch (Exception e) {
//                            System.out.println("ConsoleInputReadTask() cancelled");
                        }
                    } while (!waitMessageFromServer.isDone());
                }

                String mes = waitMessageFromServer.get();

                if (mes != null) {
//                    System.out.println("Получил письмо ---> " + waitMessageFromServer.get());
                    if (mes.equals("Готов принимать данные")) {
                        String s = "";
//                        System.out.println("2");
                        for (String l : commands) {
                            s += l + "\n";
                        }
                        commands.clear();
//                        System.out.print(s);
                        out.write(s + "end\n");
                        out.flush();
                    } else {
                        System.out.println(mes);
                    }
                }
//                out.write("dya\naga\n" + "end\n");
////                out.write("aga\n");
//                out.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String wait_new_message(BufferedReader in) {
        try {
            return in.readLine();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}