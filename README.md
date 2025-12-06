
# 魔法少女手机 (Magic Girl Phone)

一个 Minecraft NeoForge 模组，为游戏添加手机和聊天功能。


### 前置条件
- Minecraft 1.21.1
- NeoForge 21.1.215 

## 使用说明

### 获取手机
可以通过以下方式获取手机：

- 创造模式物品栏（搜索 "Ema Phone" 或 "Hiro Phone"）

## 开发指南

### 项目结构
```
src/main/java/baios/magicgirl/phone/
├── Config.java                  # 模组配置
├── MagicGirlPhone.java          # 主模组类
├── MagicGirlPhoneClient.java    # 客户端初始化
├── data/
│   └── ChatMessage.java         # 聊天消息数据结构
├── item/
│   ├── EmaPhone.java            # Ema Phone 物品
│   ├── HiroPhone.java           # Hiro Phone 物品
│   └── ModItems.java            # 物品注册
├── menu/
│   ├── ModMenus.java            # 菜单注册
│   └── PhoneMenu.java           # 手机菜单逻辑
├── network/
│   ├── ClientPayloadHandler.java# 客户端网络处理
│   ├── NetworkRegistry.java     # 网络通道注册
│   └── ServerPayloadHandler.java# 服务器网络处理
├── screen/
│   ├── ChatHistoryList.java     # 聊天历史 UI 组件
│   ├── ChatMessageEntry.java    # 聊天消息条目 UI
│   ├── ChatPlayerEntry.java     # 玩家条目 UI
│   ├── ChatPlayerList.java      # 玩家列表 UI 组件
│   ├── IconButton.java          # 自定义图标按钮 UI
│   ├── ModScreens.java          # 界面注册
│   └── PhoneScreen.java         # 主手机界面
└── sound/
    └── SoundRecorder.java       # 录音功能
```
