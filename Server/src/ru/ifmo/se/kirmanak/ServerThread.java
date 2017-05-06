package ru.ifmo.se.kirmanak;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class ServerThread implements Runnable {
    private static final int BA_SIZE = 6000;
    private final List<InetSocketAddress> clients = new ArrayList<>();
    private final DatagramSocket socket;
    private InetSocketAddress source;

    public ServerThread(int port) throws SocketException {
        socket = new DatagramSocket(port);
    }

    public void sendMessageToAll(Message message) {
        try {
            System.out.println("Отправляю сообщение всем клиентам...");
            final byte[] sendBuf = messageToArr(message);
            if (!clients.isEmpty()) clients.forEach((address) -> {
                if (!address.equals(source))
                    sendArr(address, sendBuf);
            });
            source = null;
            System.out.println("Отправка завершена.");
        } catch (IOException e) {
            System.err.println("Ошибка при отправке сообщения.");
            System.err.println(e.getMessage());
        }
    }

    private void sendArr(InetSocketAddress address, byte[] sendBuf) {
        ByteBuffer buffer = ByteBuffer.allocate(BA_SIZE);
        buffer.clear();
        buffer.put(sendBuf);
        buffer.flip();
        try {
            System.out.println("Отправляю сообщение на " + address.toString());
            int sentBytes = EntryPoint.getServerChannel().send(buffer, address);
            System.out.println("Отправлено " + sentBytes + " байт.");
        } catch (IOException e) {
            System.out.println("Ошибка при отправке на " + address.toString());
            System.err.println(e.getMessage());
        }
    }

    private byte[] messageToArr(Message message) throws IOException {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(BA_SIZE);
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(message);
        return baos.toByteArray();
    }

    private void handleMessage(DatagramPacket packet) {
        try {
            final ByteArrayInputStream bais = new ByteArrayInputStream(packet.getData());
            final ObjectInputStream ois = new ObjectInputStream(bais);
            Message receivedMessage = (Message) ois.readObject();
            InetSocketAddress address = new InetSocketAddress(packet.getAddress(), packet.getPort() + 1);
            if (receivedMessage.getSTATUS()) {
                System.out.println("Пришла полная коллекция от " + packet.getSocketAddress().toString());
                EntryPoint.getCollection().setAll(receivedMessage.getList());
                source = address;
            } else {
                System.out.println("Пришёл запрос на коллекцию от " + packet.getSocketAddress().toString());
                ArrayList<Humans> list = new ArrayList<>(EntryPoint.getCollection());
                Message message = new Message(true, list);
                sendArr(address, messageToArr(message));
                if (source != null && source.equals(address)) source = null;
            }
            if (!clients.contains(address)) clients.add(address);
        } catch (IOException | ClassNotFoundException err) {
            System.err.println("Что-то пошло не так при обработке полученного...");
            System.err.println(err.getMessage());
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                final byte[] bytes = new byte[BA_SIZE];
                final DatagramPacket packet = new DatagramPacket(bytes, BA_SIZE);
                System.out.println("Ожидаю новый пакет...");
                socket.receive(packet);
                System.out.println("Пришла новая датаграмма от " + packet.getSocketAddress().toString());
                handleMessage(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
