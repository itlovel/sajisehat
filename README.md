# SAJISEHAT 🍽️  
Aplikasi Pendamping Konsumsi Gula Harian

SAJISEHAT adalah aplikasi Android yang membantu pengguna memahami dan memantau konsumsi gula harian melalui pemindaian label gizi pada kemasan produk makanan/minuman.  

Dengan memanfaatkan pemindaian dokumen (ML Kit), perhitungan otomatis kadar gula, dan pencatatan riwayat konsumsi (trek), SAJISEHAT bertujuan untuk memberi edukasi sederhana namun berdampak tentang pentingnya membatasi konsumsi gula harian.

---

## ✨ Fitur Utama

### 1. Scan Label Gizi
- Pemindaian label gizi menggunakan **kamera** atau **galeri**.
- Menggunakan **Google ML Kit Document Scanner** untuk mengambil gambar label gizi.
- Ekstraksi informasi penting, seperti:
  - Takaran saji (gram)
  - Jumlah sajian per kemasan
  - Gula per sajian (gram)
  - (Opsional) gula per kemasan
- Aplikasi menghitung:
  - Perkiraan **% kebutuhan gula harian**
  - **Level gula**: RENDAH, SEDANG, TINGGI
- Menampilkan hasil dalam UI yang ringkas dan mudah dipahami.

### 2. Trek Konsumsi Gula (Save Trek)
- Setelah proses scan, pengguna dapat menyimpan hasil analisis sebagai **trek** (riwayat konsumsi).
- Riwayat ini dapat digunakan untuk:
  - Memantau pola konsumsi gula harian.
  - Menyadari seberapa sering konsumsi produk bergula tinggi.

### 3. Beranda (Home)
- Halaman utama aplikasi.
- Menyajikan ringkasan dan akses cepat ke fitur-fitur:
  - Scan
  - Trek
  - Katalog
  - Notifikasi
  - Profil

### 4. Katalog Produk (Catalog)
- Menampilkan daftar produk/makanan/minuman sebagai referensi.
- Dapat digunakan untuk edukasi contoh produk dengan kadar gula rendah, sedang, dan tinggi.

### 5. Notifikasi & Pengingat (Notification)
- Pengingat terkait konsumsi gula harian.
- Menggunakan worker terjadwal (contoh: `DailySugarNotificationWorker`) untuk:
  - Mengirim notifikasi harian.
  - Mengingatkan pengguna untuk mengecek atau mencatat konsumsi gula hari ini.
- Tersedia layar **Notification** untuk mengelola pengaturan notifikasi.

### 6. Profil Pengguna (Profile)
- Halaman profil untuk mengelola informasi dasar pengguna.
- Tempat pengaturan preferensi (misalnya target konsumsi gula, dsb) **(dapat dikembangkan lebih lanjut)**.

---

## 🧠 Arsitektur Aplikasi

SAJISEHAT menggunakan arsitektur modern Android yang modular dan mudah dikembangkan:

- **Pattern**:  
  - MVVM (Model–View–ViewModel)
  - Feature-based (per fitur): `feature/scan`, `feature/home`, `feature/catalog`, `feature/notification`, `feature/profile`, dll.

- **State Management**:
  - `StateFlow` / `MutableStateFlow` di dalam `ViewModel`.
  - UI (Jetpack Compose) bereaksi terhadap perubahan state (unidirectional data flow).
  - Contoh: `ScanUiState`, `ScanViewModel`, `NotificationViewModel`.

- **Layering (secara garis besar)**:
  - `data/`  
    - Repository dan model data (misalnya `ScanRepository`, `NutritionScanResult`).
  - `feature/`  
    - UI dan ViewModel per fitur (Scan, Home, Catalog, Notification, Profile).
  - `navigation/`  
    - Definisi destinasi dan route (`Destinations`, `NavHost`).
  - `prefs/`  
    - Pengelolaan preference (contoh: `AppPrefs` untuk menyimpan status izin kamera).
  - `ui/components/`  
    - Komponen UI reusable (contoh: `LoadingDialog`).

---

## 🧩 Framework, Library, dan Tools yang Digunakan

> Sesuai ketentuan lomba, daftar framework/library/tools utama yang digunakan adalah sebagai berikut:

### Bahasa & Platform
- **Kotlin** — Bahasa utama pengembangan aplikasi Android.
- **Android SDK** — Platform untuk membangun aplikasi mobile.

### UI & Desain
- **Jetpack Compose**  
  - UI deklaratif modern untuk Android.
  - Digunakan untuk seluruh layar (Home, Scan, Catalog, Notification, Profile, dll).
- **Material 3 (Material Design Components)**  
  - Komponen UI dengan guideline Material Design 3.
  - Tema warna, typography, shape, dsb.

### Arsitektur & Reaktivitas
- **AndroidX Lifecycle & ViewModel**
  - `ViewModel` untuk menampung dan mengelola state UI.
- **Kotlin Coroutines**
  - `viewModelScope`, `launch`, `delay`, dll untuk operasi asynchronous.
- **Kotlin Flow / StateFlow**
  - Aliran data reaktif yang di-observe di UI dengan `collectAsState()`.

### Data & Preferences
- **AppPrefs (berbasis DataStore / SharedPreferences)**  
  - Menyimpan pengaturan aplikasi, misalnya status izin kamera.

### Machine Learning & Vision
- **Google ML Kit Document Scanner**
  - `GmsDocumentScanning`, `GmsDocumentScannerOptions`, `GmsDocumentScanningResult`.
  - Digunakan untuk:
    - Pemotretan dan pemilihan gambar label gizi dari kamera/galeri.
    - Menghasilkan `Uri` gambar yang selanjutnya dianalisis oleh `ScanRepository`.

### Background Task & Notifikasi
- **WorkManager / Worker (AndroidX)**  
  - Contoh: `DailySugarNotificationWorker` untuk menjadwalkan notifikasi harian.
- **Android Notification APIs**  
  - Menampilkan notifikasi lokal untuk mengingatkan konsumsi gula / penggunaan aplikasi.

### Build, Version Control & Tools
- **Gradle (KTS)** — Sistem build (`build.gradle.kts`, `libs.versions.toml`).
- **Android Studio** — IDE utama pengembangan aplikasi.
- **Git & GitHub** — Version control, kolaborasi, dan manajemen branch.

_(Jika pada project sebenarnya digunakan library lain seperti Hilt, Retrofit, Room, Firebase, dsb, dapat ditambahkan di sini.)_

---

## 📂 Struktur Proyek (Ringkasan)

```text
app/
 ├─ src/main/java/com/example/sajisehat/
 │   ├─ App.kt
 │   ├─ MainActivity.kt
 │   ├─ navigation/
 │   │   └─ Destinations.kt
 │   ├─ data/
 │   │   ├─ scan/
 │   │   │   ├─ ScanRepository.kt
 │   │   │   └─ model/NutritionScanResult.kt
 │   │   └─ prefs/
 │   │       └─ AppPrefs.kt
 │   ├─ feature/
 │   │   ├─ home/
 │   │   ├─ catalog/
 │   │   ├─ profile/
 │   │   ├─ notification/
 │   │   │   ├─ NotificationScreen.kt
 │   │   │   └─ NotificationViewModel.kt
 │   │   └─ scan/
 │   │       ├─ ScanUiState.kt
 │   │       ├─ ScanViewModel.kt
 │   │       └─ ScanRoute/ScanScreen.kt
 │   └─ ui/components/
 │       └─ LoadingDialog.kt
 ├─ src/main/AndroidManifest.xml
 └─ build.gradle.kts
