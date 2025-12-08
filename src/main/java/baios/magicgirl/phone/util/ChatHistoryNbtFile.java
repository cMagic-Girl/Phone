package baios.magicgirl.phone.util;


import baios.magicgirl.phone.MagicGirlPhone;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.neoforged.fml.loading.FMLPaths;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// 自定义NBT文件工具类
public class ChatHistoryNbtFile {
    public static Path getModDir() {
        Path modDir = FMLPaths.MODSDIR.get().resolve(MagicGirlPhone.MODID);
        System.out.println("modDir: " + modDir);
        if (modDir.toFile().mkdirs()){
            System.out.println("modDir created!");
        }
        return modDir;
    }

    public static CompoundTag readChatHistory(String chatOrigin, String chatTarget) {
        int chatHistoryNumber = getChatHistoryNumber(chatOrigin, chatTarget);
        int start;
        CompoundTag chatHistoryList = new CompoundTag();
        if (chatHistoryNumber == 0) {
            return null;
        }else if(chatHistoryNumber<5){
            start = 1;
        }else {
            start = chatHistoryNumber-5;
        }
        CompoundTag chatHistory = getChatHistory(chatOrigin, chatTarget);
        int a = 1 ;
        for (int i = start ; i < chatHistoryNumber+1; i++) {
            CompoundTag messageData = (CompoundTag) chatHistory.get(String.valueOf(i));
            if (messageData != null) {
                chatHistoryList.put(String.valueOf(a), messageData);
                a++;
            }
        }

        return chatHistoryList;
    }


    public static void writeChatHistory(CompoundTag messageData) {
        String chatOrigin = messageData.getString("chatOrigin");
        String chatTarget = messageData.getString("chatTarget");

        //写入直接聊天记录
        int messageID = getChatHistoryNumber(chatOrigin, chatTarget)+1;
        setChatHistoryNumber(chatOrigin, chatTarget, messageID);
        setChatHistoryNumber(chatTarget, chatOrigin, messageID);


        CompoundTag chatHistoryTag = getChatHistory(chatOrigin, chatTarget);
        if (chatHistoryTag == null) {
            chatHistoryTag = new CompoundTag(); // 创建新的 CompoundTag
        }
        chatHistoryTag.put(String.valueOf(messageID), messageData);
        setChatHistory(chatOrigin, chatTarget, chatHistoryTag);

        //写入反向记录文件

        CompoundTag chatHistoryTag_2 = getChatHistory(chatTarget,chatOrigin);
        if (chatHistoryTag_2 == null) {
            chatHistoryTag_2 = new CompoundTag(); // 创建新的 CompoundTag
        }

        chatHistoryTag_2.put(String.valueOf(messageID), messageData);
        setChatHistory(chatTarget,chatOrigin,chatHistoryTag_2);

        System.out.println("messageID:" + messageID);
        System.out.println("chatHistoryTag:" +chatHistoryTag);
    }

    public static int getChatHistoryNumber(String chat1 , String chat2){
        CompoundTag chatHistoryNumberData = fileToNBT(chat1 +"_"+ chat2+"_chat_history_number.nbt");
        if (chatHistoryNumberData != null) {
            return chatHistoryNumberData.getInt("chatHistoryNumber");
        }
        return 0;
    }

    public static void setChatHistoryNumber(String chat1 , String chat2, int chatHistoryNumber){
        CompoundTag chatHistoryNumberData = new CompoundTag();
        chatHistoryNumberData.putInt("chatHistoryNumber", chatHistoryNumber);
        nbtToFile(chatHistoryNumberData, chat1 +"_"+ chat2+"_chat_history_number.nbt");
    }

    public static CompoundTag getChatHistory(String chat1 , String chat2){
        return fileToNBT(chat1 +"_"+ chat2+"_chat_history.nbt");
    }

    public static void setChatHistory(String chat1 , String chat2, CompoundTag nbt){
        nbtToFile(nbt, chat1 +"_"+ chat2+"_chat_history.nbt");
    }


    public static void nbtToFile(CompoundTag nbt, String fileName) {
        Path path = getModDir().resolve(fileName);
        try {
            NbtIo.write(nbt, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static CompoundTag fileToNBT(String fileName) {
        Path filePath = getModDir().resolve(fileName);
        CompoundTag rootTag = null;
        try {
            rootTag = NbtIo.read(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return rootTag;
    }

}