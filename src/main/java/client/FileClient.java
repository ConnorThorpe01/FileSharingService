package client;

import FileService.FileServiceGrpc;
import FileService.FileServiceProto;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;

public class FileClient {
    public void run() throws Exception {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        FileServiceGrpc.FileServiceBlockingStub stub = FileServiceGrpc.newBlockingStub(channel);

        // Upload a file
        byte[] content = Files.readAllBytes(Paths.get("TestFile.txt"));
        FileServiceProto.UploadFileRequest uploadRequest = FileServiceProto.UploadFileRequest.newBuilder()
                .setFilename("TestFile.txt")
                .setContent(ByteString.copyFrom(content))
                .build();
        FileServiceProto.UploadFileResponse uploadResponse = stub.uploadFile(uploadRequest);
        System.out.println("Upload response: " + uploadResponse.getMessage());

        // Download a file
        FileServiceProto.DownloadFileRequest downloadRequest = FileServiceProto.DownloadFileRequest.newBuilder()
                .setFilename("TestFile.txt")
                .build();
        FileServiceProto.DownloadFileResponse downloadResponse = stub.downloadFile(downloadRequest);
        Files.write(Paths.get("downloaded_example.txt"), downloadResponse.getContent().toByteArray());
        System.out.println("Downloaded file: " + downloadResponse.getFilename());

        channel.shutdown();
    }
}


