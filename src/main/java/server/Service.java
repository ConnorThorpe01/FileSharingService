package server;

import FileService.FileServiceGrpc;
import FileService.FileServiceProto;
import com.google.protobuf.ByteString;
import io.grpc.stub.StreamObserver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Service extends FileServiceGrpc.FileServiceImplBase {
    private final UUID serverId;
    private final String dataDir;
    public Service(UUID serverId){
        super();
        this.serverId = serverId;
        String tmp = System.getenv("DATA_DIR");
        if(!tmp.isEmpty()){
            dataDir = tmp.concat("/");
        } else {
            dataDir = "";
        }
    }

    // In-memory storage for files and comments
    private final Map<String, FileStorage> fileStore = new ConcurrentHashMap<>();

    // Helper class to store file data and associated comment
    private static class FileStorage {
        byte[] fileData;

        FileStorage(byte[] fileData) {
            this.fileData = fileData;
        }
    }

    @Override
    public void downloadFile(FileServiceProto.DownloadFileRequest request, StreamObserver<FileServiceProto.DownloadFileResponse> responseObserver) {
        String filename = request.getFilename();
//        FileStorage fileStorage = fileStore.get(filename);
        FileStorage fileStorage = null;
        try{
            Path filePath = Paths.get(dataDir + serverId + "/" + filename);
            byte[] file = Files.readAllBytes(filePath);
            fileStorage = new FileStorage(file);
        } catch (IOException e) {
            System.out.println(e);
        }
        if (fileStorage != null) {
            // Respond with the file data and associated comment
            FileServiceProto.DownloadFileResponse response = FileServiceProto.DownloadFileResponse.newBuilder()
                    .setFilename(filename)
                    .setContent(ByteString.copyFrom(fileStorage.fileData))
                    .build();

            responseObserver.onNext(response);
        } else {
            // File not found
            FileServiceProto.DownloadFileResponse response = FileServiceProto.DownloadFileResponse.newBuilder()
                    .setFilename(filename)
                    .setContent(ByteString.EMPTY)
                    .build();

            responseObserver.onNext(response);
        }

        responseObserver.onCompleted();
    }

    @Override
    public void uploadFile(FileServiceProto.UploadFileRequest request, StreamObserver<FileServiceProto.UploadFileResponse> responseObserver) {
        String filename = request.getFilename();
        byte[] fileData = request.getContent().toByteArray();

        // Store the file and comment in the in-memory storage
//        fileStore.put(filename, new FileStorage(fileData));
        try {
            boolean f = new File(dataDir + serverId + "/" + filename).createNewFile();
            Path filePath = Paths.get(dataDir + serverId + "/" + filename);
            Files.write(filePath, fileData);
        } catch (IOException e){
            System.out.println(e);
        }

        // Respond to the client
        FileServiceProto.UploadFileResponse response = FileServiceProto.UploadFileResponse.newBuilder()
                .setMessage("File uploaded successfully")
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
