package penaltyclient.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Lớp static để quản lý kết nối socket và stream.
 */
public class SocketService {

    private static Socket socket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;

    /**
     * Kết nối đến server và khởi tạo streams.
     */
    public static void connect(String host, int port) throws IOException {
        if (socket == null || socket.isClosed()) {
            socket = new Socket(host, port);
            // QUAN TRỌNG: Khởi tạo ObjectOutputStream TRƯỚC ObjectInputStream
            // để tránh bị block khi khởi tạo
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
        }
    }

    public static ObjectOutputStream getOutputStream() throws IOException {
        if (out == null) {
            throw new IOException("Socket is not connected or output stream is not initialized.");
        }
        return out;
    }

    public static ObjectInputStream getInputStream() throws IOException {
        if (in == null) {
            throw new IOException("Socket is not connected or input stream is not initialized.");
        }
        return in;
    }

    /**
     * Đóng kết nối.
     */
    public static void close() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            in = null;
            out = null;
            socket = null;
        }
    }
}