# Nhóm 2_ Quản lý chi tiêu Carat

## Giới thiệu
Carat là một ứng dụng quản lý chi tiêu được phát triển bởi Nhóm 2, giúp người dùng theo dõi và quản lý chi tiêu một cách hiệu quả. Ứng dụng được xây dựng trên nền tảng Android, cung cấp các công cụ hữu ích để quản lý tài chính cá nhân.

## Thành viên nhóm
- Lê Đức Kiên
- Lê Nguyễn Ngọc Tú Hương
- Trần Phương Đan
- Trần Bảo Thiên Anh
- Nguyễn Hoàng Sang

## Cấu trúc dự án
```
Nhóm 2_Carat-System/
├── CaratExpense/                    # Ứng dụng Android chính
│   ├── app/                        # Thư mục chứa mã nguồn ứng dụng
│   │   ├── src/                   # Mã nguồn
│   │   │   ├── main/             # Mã nguồn chính
│   │   │   │   ├── java/        # Mã nguồn Java/Kotlin
│   │   │   │   │   ├── activities/    # Các màn hình chính
│   │   │   │   │   ├── adapters/      # Adapters cho RecyclerViews
│   │   │   │   │   ├── models/        # Các model dữ liệu
│   │   │   │   │   ├── utils/         # Các tiện ích
│   │   │   │   │   └── database/      # Xử lý cơ sở dữ liệu
│   │   │   │   ├── res/              # Tài nguyên
│   │   │   │   │   ├── layout/       # Giao diện XML
│   │   │   │   │   ├── drawable/     # Hình ảnh, icons
│   │   │   │   │   ├── values/       # Strings, colors, styles
│   │   │   │   │   └── menu/         # Menu resources
│   │   │   │   └── AndroidManifest.xml
│   │   │   └── test/                 # Unit tests
│   │   └── build/                    # Thư mục build
│   ├── gradle/                       # Gradle wrapper
│   ├── build/                        # Thư mục build của project
│   ├── .gradle/                      # Cache của Gradle
│   ├── .idea/                        # Cấu hình IDE
│   ├── build.gradle                  # Cấu hình build chính
│   ├── settings.gradle              # Cấu hình project
│   ├── gradle.properties            # Properties của Gradle
│   ├── gradlew                      # Gradle wrapper (Unix)
│   ├── gradlew.bat                  # Gradle wrapper (Windows)
│   └── local.properties             # Cấu hình local
│
├── docs/                            # Tài liệu dự án
│   ├── requirements/                # Tài liệu yêu cầu
│   ├── design/                      # Tài liệu thiết kế
│   └── api/                         # Tài liệu API
│
├── assets/                          # Tài nguyên dự án
│   ├── images/                      # Hình ảnh
│   └── icons/                       # Icons
│
└── README.md                        # Tài liệu dự án
```

### Mô tả chi tiết các thành phần:

#### 1. CaratExpense/
- **app/**: Chứa toàn bộ mã nguồn của ứng dụng Android
  - **src/main/java/**: Mã nguồn chính
    - **activities/**: Chứa các màn hình chính của ứng dụng
    - **adapters/**: Các adapter cho RecyclerViews và ListViews
    - **models/**: Các class model dữ liệu
    - **utils/**: Các tiện ích và helper classes
    - **database/**: Xử lý cơ sở dữ liệu SQLite
  - **src/main/res/**: Tài nguyên ứng dụng
    - **layout/**: Các file XML định nghĩa giao diện
    - **drawable/**: Hình ảnh, icons và resources
    - **values/**: Strings, colors, styles và themes
    - **menu/**: Các file menu XML

#### 2. docs/
- Chứa tất cả tài liệu liên quan đến dự án
- Bao gồm yêu cầu, thiết kế và tài liệu API

#### 3. assets/
- Chứa các tài nguyên tĩnh của dự án
- Bao gồm hình ảnh và icons

## Tính năng chính
- Quản lý chi tiêu hàng ngày
- Theo dõi ngân sách
- Báo cáo thống kê chi tiêu
- Phân loại chi tiêu theo danh mục
- Đặt mục tiêu tiết kiệm
- Thông báo nhắc nhở

## Công nghệ sử dụng
- Android Studio
- Java/Kotlin
- SQLite Database
- Material Design
- Firebase (tùy chọn)

## Hướng dẫn cài đặt
1. Clone repository:
```bash
git clone https://github.com/tuhuong7/Nhom-2-Carat-System.git
```

2. Mở project CaratExpense trong Android Studio

3. Đợi Gradle sync hoàn tất

4. Build và chạy ứng dụng

## Yêu cầu hệ thống
- Android Studio Arctic Fox hoặc mới hơn
- JDK 11 hoặc mới hơn
- Android SDK 21 trở lên
- Gradle 7.0 trở lên

