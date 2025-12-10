package baios.magicgirl.phone.util;


import baios.magicgirl.phone.MagicGirlPhone;


import baios.magicgirl.phone.screen.PhoneScreen;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.sql.*;

// 自定义NBT文件工具类
public class ChatHistorySql {

    public static Path getModDir() {
        Path modDir = FMLPaths.MODSDIR.get().resolve(MagicGirlPhone.MODID);
        System.out.println("modDir: " + modDir);
        if (modDir.toFile().mkdirs()){
            System.out.println("modDir created!");
        }
        return modDir;
    }

    public static String getSqliteUrl() {
        return "jdbc:sqlite:"+getModDir() + "\\chat_history.db";
    }

    public static void init_db() {
        DriverManager.setLoginTimeout(5);
        try(Connection conn = DriverManager.getConnection(getSqliteUrl());
            Statement stmt = conn.createStatement()) {
            //System.out.println("SQLite连接成功：" + conn);
            String createTableSQL = "CREATE TABLE IF NOT EXISTS user_history (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "chatOrigin TEXT NOT NULL," +
                    "chatTarget TEXT NOT NULL," +
                    "message TEXT NOT NULL," +
                    "dayTimes INTEGER NOT NULL)";
            stmt.execute(createTableSQL);
            //System.out.println("表创建成功（或已存在）");
        } catch (SQLTimeoutException e){
            System.out.println("连接超时");
        }
        catch (SQLException e) {
            System.err.println("数据库访问错误：" + e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public static CompoundTag readChatHistory(String chatOrigin, String chatTarget) {
        init_db();
        CompoundTag chatHistoryList = new CompoundTag();


        String sql = "SELECT id, chatOrigin, chatTarget, message, dayTimes " +
                "FROM user_history " +
                "WHERE (chatOrigin=? AND chatTarget=?) OR (chatOrigin=? AND chatTarget=?) " +
                "ORDER BY id DESC LIMIT 10";

        try (Connection conn = DriverManager.getConnection(getSqliteUrl());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, chatOrigin);
            pstmt.setString(2, chatTarget);
            pstmt.setString(3, chatTarget);
            pstmt.setString(4, chatOrigin);

            // 执行查询并封装结果
            ResultSet rs = pstmt.executeQuery();
            int i=1;
            while (rs.next()) {
                CompoundTag messageData = new CompoundTag();
                messageData.putString("chatOrigin",  rs.getString("chatOrigin"));
                messageData.putString("chatTarget",  rs.getString("chatTarget"));
                messageData.putString("message",  rs.getString("message"));
                messageData.putString("dayTimes",  rs.getString("dayTimes"));
                chatHistoryList.put(String.valueOf(i), messageData);
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Server chatHistoryList:" + chatHistoryList);
        return chatHistoryList;
    }

    public static CompoundTag readChatListLatestMessage(String chatTarget) {
        init_db();
        CompoundTag LatestMessage = new CompoundTag();
        /*
        String sql = "SELECT id, chatOrigin, chatTarget, message, time " +
                "FROM user_history " +
                "WHERE chatTarget = ? " +
                "ORDER BY id DESC";

        try (Connection conn = DriverManager.getConnection(getSqliteUrl());
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, chatTarget);

            ResultSet rs = pstmt.executeQuery();
            // 遍历结果，封装成易读的字符串
            while (rs.next()) {
                LatestMessage.putString(rs.getString("chatTarget"),rs.getString("message"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
         */
        return LatestMessage;
    }


    public static void writeChatHistory(CompoundTag messageData) {
        init_db();
        String insertSql = "INSERT INTO user_history (chatOrigin, chatTarget, message, dayTimes) VALUES (?, ?, ?, ?)";
        String chatOrigin = messageData.getString("chatOrigin");
        String chatTarget = messageData.getString("chatTarget");
        String message = messageData.getString("message");
        int dayTimes = messageData.getInt("dayTimes");
        try (Connection conn = DriverManager.getConnection(getSqliteUrl());
             PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            pstmt.setString(1, chatOrigin);
            pstmt.setString(2, chatTarget);
            pstmt.setString(3, message);
            pstmt.setInt(4, dayTimes);
            pstmt.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace(); // 极简异常处理，实际可替换为日志
            return ;
        }
    }

}