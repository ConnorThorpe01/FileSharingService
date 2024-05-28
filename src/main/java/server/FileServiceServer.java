package server;


import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class FileServiceServer {
    private Server server;
    private UUID id;

    public void start() throws IOException {
        id = UUID.randomUUID();
        int port = 8080;
        server = ServerBuilder.forPort(port)
                .addService(new Service(id))
                .build()
                .start();

        System.out.println("Server started, listening on " + port);
        String dataDir = System.getenv("DATA_DIR");
        if (dataDir == null) {
            dataDir = ".";
        }
        File dir = new File(dataDir, id.toString());
        boolean s = dir.mkdirs();
        System.out.println("Created new dir: " + s);
        if(s){
            System.out.println("Dir name: " + id.toString());
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.err.println("Shutting down gRPC server since JVM is shutting down");
            FileServiceServer.this.stop();
            System.err.println("Server shut down");
        }));
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public void blockUntilShutdown() throws InterruptedException {
        if (server != null) {
            server.awaitTermination();
        }
    }

    public String getId(){
        return id.toString();
    }
}
