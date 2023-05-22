package client;

import commands.Command;
import model.Response;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;

public class Client {

    DatagramChannel channel;
    SocketAddress serverAddress;

    public Client(String ipAddress, int port) throws IOException {
        channel = DatagramChannel.open();
        serverAddress = new InetSocketAddress(ipAddress, port);
        //channel.bind(new InetSocketAddress(12345));
        channel.connect(serverAddress);

    }

    public void recieve_response(DatagramChannel channel){
        // Получаем ответ от сервера
        ByteBuffer buffer = ByteBuffer.allocate(2048);
        try {
            channel.receive(buffer);
        } catch (IOException e) {
            System.out.println("Server is unavailable! Wait a bit and try again");
            return;
        }
        buffer.flip();
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array(), buffer.position(), buffer.remaining());
        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(bais);
        } catch (IOException e) {
            System.out.println("Error while creating ObjectInputStream!");
            return;
        }
        Response response;
        try {
            response = (Response) ois.readObject();
        } catch (IOException e) {
            System.out.println("Unable to read response");
            return;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(UserInput.ANSI_BLUE +"Command execution result:" + UserInput.ANSI_RESET);
        System.out.println(UserInput.ANSI_PURPLE + response.data + UserInput.ANSI_RESET);
    }

    public void sendCommand(Command command) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(baos);
        } catch (IOException e) {
            System.out.println("Can't create ObjectOutputStream!");
            return;
        }
        try {
            oos.writeObject(command);
        } catch (IOException e) {
            System.out.println("Can't serialize Command!");
            return;
        }
        byte[] data = baos.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(data);
        try {
            channel.write(buffer);
            System.out.println(UserInput.ANSI_GREEN + "Command is sent to server!" + UserInput.ANSI_RESET);
        } catch (IOException e) {
            System.out.println("Error while sending packet!");
            return;
        }
        recieve_response(channel);
    }

}
