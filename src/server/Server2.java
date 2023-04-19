package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Server2 {
    private ServerSocket server; // серверсокет
    private Map<Socket, Map<BufferedReader, BufferedWriter>> clients;
    private Map<BufferedReader, BufferedWriter> loc;
    private Socket clientSocket;
    private Date c_date;
    private boolean run = true;

    public Server2() throws IOException {
        clients = new HashMap<>();
        server = new ServerSocket(4004);

        System.out.println("Сервер запущен");

        try {
            while (run) {
                CompletableFuture<Socket> waitNewClient = CompletableFuture.supplyAsync(() -> wait_new_client(server));
                while (!waitNewClient.isDone()) {
                    for (Socket client : clients.keySet()) {
                        Map<BufferedReader, BufferedWriter> buff = clients.get(client);
                        BufferedReader in = buff.keySet().iterator().next();
                        BufferedWriter out = buff.values().iterator().next();
                        CompletableFuture<ArrayList<String>> waitMessage = CompletableFuture.supplyAsync(() -> wait_new_message(in));

                        c_date = new Date();
                        out.write("Готов принимать данные\n");
                        out.flush();
                        while (!waitMessage.isDone()) {
                            if (new Date().getTime() - c_date.getTime() > 1000) {
                                waitMessage.cancel(true);
                            }
                        }

                        if (!waitMessage.isCancelled()) {
//                            System.out.println("!!!");
                            ArrayList<String> commands = waitMessage.get();
                            if (commands != null && commands.size() > 0) {
//                                System.out.println("Получены сообщения:");
                                for (String com : commands) {
                                    System.out.println("--> " + com);
                                }
                                out.write("Данные\nПриняты\n");
                                out.flush();
//                                run = false;
//                                break;
                            }
                        } else {
                            System.out.println("не поймали");
                        }
                    }

                    if (!run) {
                        break;
                    }

                }

                System.out.println("Кого-то поймали в свои сети...");
                clientSocket = waitNewClient.get();
                if (clientSocket != null) {
                    System.out.println(clientSocket);
                    loc = new HashMap<>();
                    loc.put(new BufferedReader(new InputStreamReader(clientSocket.getInputStream())), new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                    clients.put(clientSocket, loc);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            System.out.println("Сервер выключен");
        }
    }

    private static Socket wait_new_client(ServerSocket server) {
        try {
            return server.accept();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static ArrayList<String> wait_new_message(BufferedReader in) {
        try {
            ArrayList<String> commands = new ArrayList();
            for (Iterator<String> it = in.lines().iterator(); it.hasNext(); ) {
                String s = it.next();
                if (s.equals("end")) {break;}
                commands.add(s);
            }
//            System.out.println(commands);
            return commands;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
