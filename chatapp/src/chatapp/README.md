# Chat Application - Source Code

## Tổng Quan / Overview

Thư mục `src/chatapp` chứa mã nguồn của một ứng dụng chat đa người dùng (Multi-user Chat Application) được xây dựng bằng Java. Ứng dụng hỗ trợ cả giao diện console và giao diện GUI (Graphical User Interface) với kiến trúc Client-Server.

**This directory contains the source code for a multi-user chat application built with Java. The application supports both console and GUI interfaces with a Client-Server architecture.**

## Cấu trúc File / File Structure

```
src/chatapp/
├── ChatServer.java         # Server ứng dụng (Console version)
├── ChatClient.java         # Client ứng dụng (Console version)
├── ClientHandler.java      # Xử lý kết nối từ mỗi client
├── GuiServer.java          # Server với giao diện GUI
├── GuiClient.java          # Client với giao diện GUI
├── ServerUI.java           # UI Controller cho Server
├── ServerUI.form           # UI Layout cho Server
├── ClientUI.java           # UI Controller cho Client
├── ClientUI.form           # UI Layout cho Client
├── DownloadHomepage.java   # Trang chủ/Homepage
└── README.md              # Documentation
```

## Chi Tiết Các Thành Phần / Component Details

### 1. **ChatServer.java**
- Ứng dụng server phiên bản console
- Lắng nghe kết nối từ các client trên cổng mặc định (port 8080)
- Quản lý danh sách các client kết nối
- Tạo một thread `ClientHandler` cho mỗi client mới
- **Main functionality**: Server initialization and client acceptance loop

```java
// Default port: 8080
// Mode: Console-based
```

### 2. **ChatClient.java**
- Ứng dụng client phiên bản console
- Kết nối đến server tại localhost:8080
- Có hai thread:
  - Thread nhận: Lắng nghe tin nhắn từ server
  - Thread gửi: Đọc input từ người dùng và gửi đi
- **Main functionality**: Establishes connection and manages bi-directional communication

### 3. **ClientHandler.java**
- Một thread riêng để xử lý mỗi client kết nối
- Nhận tin nhắn từ client
- Phát tin nhắn đến tất cả các client khác kết nối
- Quản lý disconnect và cleanup
- **Key method**: `sendMessage()` - Broadcast messages to other clients

**Đặc điểm chính**: 
- Sử dụng synchronized để đảm bảo thread-safety với danh sách clients
- Khi client gửi tin nhắn, tất cả client khác (except sender) sẽ nhận được
- Tự động xóa client khỏi danh sách khi disconnect

### 4. **GuiServer.java**
- Server phiên bản GUI sử dụng Swing
- Giao diện cho phép:
  - Nhập port server
  - Nút bắt đầu (Start) server
  - Khu vực hiển thị chat
  - Trường nhập tin nhắn và nút gửi
- Quản lý `ClientConnection` objects cho mỗi client
- **Main functionality**: GUI-based server with visual chat area

### 5. **GuiClient.java**
- Client phiên bản GUI sử dụng Swing
- Giao diện cho phép:
  - Nhập IP address của server
  - Nhập port
  - Nút kết nối (Connect)
  - Khu vực hiển thị chat
  - Trường nhập tin nhắn và nút gửi
- Trạng thái kết nối: `connected` boolean flag
- **Main functionality**: GUI-based client with connection management

### 6. **ServerUI.java & ServerUI.form**
- Form-based UI cho server
- Được tạo bởi NetBeans IDE visual designer
- `ServerUI.form` - Layout file (XML)
- `ServerUI.java` - Controller logic

### 7. **ClientUI.java & ClientUI.form**
- Form-based UI cho client
- Được tạo bởi NetBeans IDE visual designer
- `ClientUI.form` - Layout file (XML)
- `ClientUI.java` - Controller logic

### 8. **DownloadHomepage.java**
- Trang chủ hoặc tính năng tải tài nguyên
- **Purpose**: To be determined based on implementation

## Kiến Trúc / Architecture

```
┌─────────────────────────────────────────────────────┐
│                    CHAT SERVER                      │
│  (ChatServer.java or GuiServer.java)                │
│                                                     │
│  ServerSocket listening on port 8080                │
│                                                     │
│  ┌─────────────────────────────────────────────┐  │
│  │  Clients List                               │  │
│  │  ├─ ClientHandler (Client 1)                │  │
│  │  ├─ ClientHandler (Client 2)                │  │
│  │  └─ ClientHandler (Client 3)                │  │
│  └─────────────────────────────────────────────┴──┘
│
│  Broadcast mechanism: When Client 1 sends message,
│  Server forwards to Client 2 and Client 3
│
└─────────────────────────────────────────────────────┘

┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   CLIENT 1   │  │   CLIENT 2   │  │   CLIENT 3   │
│ (ChatClient/ │  │ (ChatClient/ │  │ (ChatClient/ │
│ GuiClient)   │  │ GuiClient)   │  │ GuiClient)   │
└──────────────┘  └──────────────┘  └──────────────┘
```

## Luồng Hoạt Động / Communication Flow

1. **Server Startup**
   - `ChatServer.main()` hoặc `GuiServer` khởi tạo
   - ServerSocket lắng nghe trên port 8080
   - Chờ client kết nối

2. **Client Connection**
   - Client kết nối đến server:port
   - Server chấp nhận connection và tạo `ClientHandler` mới
   - ClientHandler được thêm vào danh sách clients
   - Gửi tin nhắn chào mừng tới client

3. **Message Exchange**
   - Client gửi tin nhắn qua `PrintWriter`
   - `ClientHandler` nhận tin nhắn từ `BufferedReader`
   - Server phát (broadcast) tin nhắn đến tất cả clients khác
   - Mỗi client nhận tin nhắn qua `BufferedReader`

4. **Disconnection**
   - Khi client ngắt kết nối
   - `ClientHandler.run()` kết thúc (IOException)
   - Client bị xóa khỏi danh sách
   - Socket đóng lại (cleanup)

## Hướng Dẫn Sử Dụng / Usage Guide

### Phiên Bản Console / Console Version

**Server:**
```bash
javac src/chatapp/*.java
java -cp src chatapp.ChatServer
# Output: "Server is running on port 8080"
```

**Client:**
```bash
java -cp src chatapp.ChatClient
# Nhập tin nhắn tại console
```

### Phiên Bản GUI / GUI Version

**Server GUI:**
```bash
java -cp src chatapp.GuiServer
# Giao diện đồ họa sẽ mở ra
# Chọn port → Click "Start Server"
```

**Client GUI:**
```bash
java -cp src chatapp.GuiClient
# Nhập IP (127.0.0.1) và Port (8080)
# Click "Connect"
# Gửi tin nhắn qua giao diện
```

## Công Nghệ Sử Dụng / Technologies

- **Language**: Java
- **Networking**: Java Sockets (ServerSocket, Socket)
- **Threading**: Multi-threading untuk concurrent client handling
- **GUI**: Swing/AWT (cho GuiServer, GuiClient)
- **IDE**: NetBeans (.form files từ NetBeans visual designer)

## Thread Safety

- `ClientHandler` extends `Thread` để xử lý mỗi client song song
- Sử dụng `synchronized` block khi truy cập danh sách `clients`
- Mỗi client handler có I/O stream riêng (`BufferedReader`, `PrintWriter`)

## Các Tính Năng / Features

✓ Multi-client support (Hỗ trợ nhiều client)  
✓ Real-time messaging (Tin nhắn thời gian thực)  
✓ Broadcast messages (Phát tin nhắn đến tất cả)  
✓ Console mode (Chế độ console)  
✓ GUI mode (Chế độ giao diện đồ họa)  
✓ Automatic connection management (Quản lý kết nối tự động)  

## Cảnh Báo & Giới Hạn / Limitations & Notes

⚠ Chỉ hỗ trợ kết nối local (localhost)  
⚠ Không có xác thực user  
⚠ Không có mã hóa tin nhắn  
⚠ Tin nhắn được phát đến tất cả client (không hỗ trợ tin nhắn riêng)  
⚠ Khi server dừng, tất cả client bị ngắt kết nối  

## Mở Rộng Tiềm Năng / Potential Improvements

- [ ] Thêm xác thực user (username/password)
- [ ] Hỗ trợ tin nhắn private
- [ ] Lưu lịch sử chat
- [ ] Mã hóa tin nhắn
- [ ] Hỗ trợ emoji, media files
- [ ] Persistent data storage
- [ ] Connection pooling
- [ ] Reconnection mechanism

---

**Last Updated**: April 5, 2026  
**Status**: Working  
**Version**: 1.0
