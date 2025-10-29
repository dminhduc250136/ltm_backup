package penaltyclient.model;

import java.io.ObjectInputStream;
import javafx.application.Platform; 
import penaltyclient.controller.LobbyController;
import java.io.IOException;
import java.util.List; // Cần import List
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Lắng nghe các thông điệp từ server trên một luồng riêng.
 * Đã cập nhật để xử lý phản hồi GET_ONLINE_USERS.
 */
public class ClientListener implements Runnable {
    
    private ObjectInputStream in;
    private LobbyController lobbyController;

    public ClientListener(LobbyController lobbyController) {
        this.lobbyController = lobbyController;
        try {
            this.in = SocketService.getInputStream();
        } catch (IOException e) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    @Override
    public void run() {
        try {
            while(true) {
                // Chỉ có luồng này được phép đọc 'in.readObject()'
                Object obj = in.readObject(); 
                
                if (obj instanceof String) {
                    // Xử lý các command dạng String
                    String message = (String) obj;
                    String[] parts = message.split(":");
                    String command = parts[0];

                    switch(command) {
                        case "INVITE_FROM": {
                            String invitePlayer = parts[1];
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    lobbyController.showInvitationAlert(invitePlayer);
                                }
                            });
                            break;
                        }
                        case "INVITE_SUCCESS": {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    lobbyController.showAlert("Invitation Sent", "Invited successfully!");
                                }
                            });
                            break;
                        }
                        // ... (các case khác của bạn) ...
                        
                        case "INVITE_FAIL":
                        case "INVITE_RESPONSE_ACCEPT":
                        case "INVITE_RESPONSE_DECLINE":
                            // Bạn có thể gộp các case xử lý Alert đơn giản
                            final String alertTitle = command;
                            final String alertMessage = parts[1]; // Hoặc xử lý parts[1]
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    lobbyController.showAlert(alertTitle, alertMessage);
                                }
                            });
                            break;

                        case "START_MATCH": {
                            int matchId = Integer.parseInt(parts[1]);
//                             Platform.runLater(new Runnable() {
//                                @Override
//                                public void run() {
//                                    lobbyController.startMatch(matchId);
//                                }
//                            });
                            System.out.println("Ban da tham ga match:" + matchId);
                            break;
                        }
                    }
                } 
                // === SỬA LỖI ĐA LUỒNG ===
                // Nếu đối tượng nhận được là một List (phản hồi từ GET_ONLINE_USERS)
                else if (obj instanceof List) { 
                    try {
                        // Giả định đây là List<String>
                        @SuppressWarnings("unchecked") // Bỏ qua cảnh báo cast
                        List<String> userList = (List<String>) obj;
                        
                        // Gọi hàm cập nhật giao diện trong LobbyController
                        lobbyController.updateOnlinePlayers(userList);
                        
                    } catch (ClassCastException e) {
                        System.err.println("ClientListener: Đã nhận 1 List nhưng không phải List<String>!");
                    }
                    // TODO: Bạn cũng nên làm điều tương tự cho
                    // List<MatchRecord> (cho lịch sử) và List<RankingEntry> (cho xếp hạng)
                    // Bằng cách kiểm tra `obj instanceof List` và kiểm tra phần tử đầu tiên
                }
                // =========================
            }
        }
        catch(Exception e) {
            System.out.println("ClientListener ngắt kết nối: " + e.getMessage());
            // (Lỗi StreamCorruptedException sẽ xảy ra ở đây nếu 2 luồng cùng đọc)
        }
    }
}