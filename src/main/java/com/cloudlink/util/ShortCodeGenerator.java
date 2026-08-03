package com.cloudlink.util;
import java.util.Random;

public class ShortCodeGenerator {
    private static final String CHARACTERS="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH =8;
    private final Random random = new Random();
    public String generateCode(){
        StringBuilder code = new StringBuilder();
        for(int i =0; i < CODE_LENGTH; i++){
            int index = random.nextInt(CHARACTERS.length());
            char randomCharacter = CHARACTERS.charAt(index);
            code.append(randomCharacter);
        }
        return code.toString();
    }
}
