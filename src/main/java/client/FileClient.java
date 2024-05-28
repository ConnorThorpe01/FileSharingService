package client;

import FileService.FileServiceGrpc;
import FileService.FileServiceProto;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class FileClient {
    public void run() {
        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080)
                .usePlaintext()
                .build();

        FileServiceGrpc.FileServiceBlockingStub stub = FileServiceGrpc.newBlockingStub(channel);
        boolean quit = false;
        Scanner scan = new Scanner(System.in);

        while (!quit) {
            System.out.print("Enter command (upload/download/q): ");
            String input = scan.nextLine().trim();

            switch (input) {
                case "q":
                    quit = true;
                    break;

                case "upload":
                    System.out.print("Enter name of file: ");
                    String uploadName = scan.nextLine().trim();
                    System.out.print("Enter path to file: ");
                    String uploadPath = scan.nextLine().trim();

                    try {
                        byte[] content = Files.readAllBytes(Paths.get(uploadPath));
                        FileServiceProto.UploadFileRequest uploadRequest = FileServiceProto.UploadFileRequest.newBuilder()
                                .setFilename(uploadName)
                                .setContent(ByteString.copyFrom(content))
                                .build();
                        FileServiceProto.UploadFileResponse uploadResponse = stub.uploadFile(uploadRequest);
                        System.out.println("Upload response: " + uploadResponse.getMessage());
                    } catch (Exception e) {
                        System.err.println("Failed to upload file: " + e.getMessage());
                    }
                    break;

                case "download":
                    System.out.print("Enter name of file: ");
                    String downloadName = scan.nextLine().trim();

                    try {
                        FileServiceProto.DownloadFileRequest downloadRequest = FileServiceProto.DownloadFileRequest.newBuilder()
                                .setFilename(downloadName)
                                .build();
                        FileServiceProto.DownloadFileResponse downloadResponse = stub.downloadFile(downloadRequest);
                        Files.write(Paths.get("downloaded_" + downloadName), downloadResponse.getContent().toByteArray());
                        System.out.println("Downloaded file: " + downloadResponse.getFilename());
                    } catch (Exception e) {
                        System.err.println("Failed to download file: " + e.getMessage());
                    }
                    break;

                default:
                    System.out.println("Not a valid command. Please enter 'upload', 'download', or 'q' to quit.");
                    break;
            }
        }

        channel.shutdown();
    }
}


