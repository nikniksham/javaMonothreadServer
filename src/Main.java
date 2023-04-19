import server.Server2;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException, IOException {
        Server2 ser = new Server2();
//        CompletableFuture<Long> completableFuture = CompletableFuture.supplyAsync(() -> factorial(20));
//        while (!completableFuture.isDone()) {
//            System.out.println("CompletableFuture is not finished yet...");
//        }
//        long result = completableFuture.get();
//        System.out.println(result);
    }

    private static long factorial(long x) {
        if (x < 2) {
            return 1;
        }
        return x * factorial(x - 1);
    }
}