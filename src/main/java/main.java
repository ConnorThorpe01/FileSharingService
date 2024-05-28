import client.FileClient;
import server.FileServiceServer;

import java.io.IOException;
import java.util.Objects;

public class main{
    public static void main(String[] args) {
        if(args.length < 1){
            System.out.println("Insufficient args");
            System.exit(-1);
        }
        if(Objects.equals(args[0], "-s")) {
            FileServiceServer server = new FileServiceServer();
            try {
                server.start();
                server.blockUntilShutdown();
            } catch (IOException | InterruptedException e) {
                System.out.println(e);
            }
        } if (Objects.equals(args[0], "-c")){
            FileClient fileClient = new FileClient();
            try {
                fileClient.run();
            } catch (Exception e){
                System.out.println(e);
            }
        }
    }
}
