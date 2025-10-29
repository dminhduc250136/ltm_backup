package penaltyserver.controller;

import penaltyserver.model.User;



public class MatchController {
    public MatchController() {
        
    }
    
    public static void startMatch(String bUser, User self) {
        System.out.println("Da khoi tao match cua 2 player:" + bUser + " " + self.getUsername());
    }

}
