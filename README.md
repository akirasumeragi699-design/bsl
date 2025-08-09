# bsl (byby standard library) - Fabric Helper Library Documentation

## Overview

**bsl** là một thư viện helper nhẹ được thiết kế để hỗ trợ phát triển mod Fabric, cung cấp các API tiện lợi cho:

- UI buttons (nút bấm trên giao diện)
- Keybindings (phím tắt)
- Client tick events (sự kiện tick client)
- Resource reload handling (xử lý tải lại tài nguyên)
- JSON config management (quản lý cấu hình JSON)

Mục tiêu là giúp các nhà phát triển mod dễ dàng thêm các tính năng phổ biến trong các mod Fabric, giúp đạt tính năng tương đương với Quilt và các mod loader khác.

---

## Integration

### 1. Sử dụng bsl trực tiếp trong mã nguồn mod của bạn

Nếu bạn phát triển mod Fabric và muốn dùng bsl ngay trong cùng dự án (được khuyến nghị để dễ debug và phát triển):

- Đặt toàn bộ mã nguồn bsl vào thư mục `src/main/java` trong package `bsl`.
- Trong mã mod, import và sử dụng bsl như bình thường:

```java
import bsl.bsl;

public class MyModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Đăng ký nút bấm trên một màn hình cụ thể
        bsl.addButton(SomeScreen.class, "Click me!", 10, 10, 100, 20, () -> {
            System.out.println("Button clicked!");
        });

        // Đăng ký sự kiện tick client
        bsl.onClientTick(() -> {
            // Logic tick ở đây
        });
    }
}
