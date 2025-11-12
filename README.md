![Banner SAJISEHAT](screenshot/sajisehat-banner.png)

# SAJISEHAT 🍽️  
Pendamping Cerdas Konsumsi Gula Harian

**Selamat datang di SAJISEHAT!** ✨  

SAJISEHAT adalah aplikasi mobile yang membantu pengguna memahami dan memantau konsumsi gula harian hanya dengan memindai label gizi pada kemasan makanan atau minuman. Di tengah gaya hidup serba instan, SAJISEHAT hadir sebagai teman kecil yang mengingatkan: *“Gula hari ini sudah seberapa banyak, ya?”*  

Dengan antarmuka yang ramah, fitur scan berbasis **Google ML Kit**, dan pencatatan riwayat konsumsi (trek), SAJISEHAT ingin menjembatani data di label gizi menjadi informasi yang mudah dipahami dan relevan untuk kesehatan sehari-hari.

---

## 👥 Tim Pengembang

Dibangun oleh mahasiswa Teknologi Informasi Universitas Brawijaya yang berkolaborasi untuk menggabungkan **riset UX**, **desain UI**, dan **pengembangan mobile modern**.

| 👤 Nama                      | 🎓 Program Studi         | 🛠️ Peran                                                                                       | 
|-----------------------------|--------------------------|-------------------------------------------------------------------------------------------------- |
| **Aulia Permata Kumala**    | Teknologi Informasi      | Product Owner, UX Researcher                                                                      |
| **Nofa Nisrina Salsabila**  | Teknologi Informasi      | UI Designer, Mobile App Developer (Google ML Kit–based label scanning & daily sugar tracking)     | 
| **Lovely Ito Pandjaitan**   | Teknologi Informasi      | Mobile App Developer (MVVM-based architecture with Firebase backend & authentication)             | 

---

## 🎨 Desain & Tampilan Aplikasi

> **Penasaran seperti apa tampilan SAJISEHAT?**  
> Kami merancang UI dengan fokus pada kesederhanaan, keterbacaan, dan nuansa yang sehat & nyaman untuk pengguna.

> 🔗 ✨ *(opsional, ganti jika ada)*  
> [Figma UI Design – SAJISEHAT](https://figma.com/...)

### 🖼️ Overview Layar Utama

| Beranda & Navigasi | Hasil Scan & Trek |
|:--:|:--:|
| ![Homepage SAJISEHAT](screenshot/home-sajisehat.png) | ![Scan Result SAJISEHAT](screenshot/scan-result-sajisehat.png) |

*(Silakan ganti `screenshot/*.png` dengan path gambar kamu sendiri di repo.)*

---

## ❓ SAJISEHAT Itu Apa?

Secara singkat:

> **SAJISEHAT = aplikasi pemindai label gizi berbasis Google ML Kit**  
> yang mengubah informasi “gula per sajian” menjadi insight praktis seperti:  
> *“Ini termasuk gula rendah, sedang, atau tinggi, dan kira-kira berapa % dari kebutuhan harianmu.”*

Aplikasi ini dirancang untuk:

- Membantu pengguna **membaca label gizi dengan lebih mudah**.
- Menjadi pengingat kecil mengenai **batas konsumsi gula harian**.
- Menyediakan **riwayat konsumsi** sehingga pengguna dapat melihat pola dari waktu ke waktu.

---

## ✨ Fitur Utama SAJISEHAT

### 1. Scan Label Gizi (Google ML Kit)

- Pengguna dapat memindai label gizi menggunakan:
  - Kamera (real-time document scanner)
  - Import dari galeri
- Teknologi:
  - **Google ML Kit Document Scanner**
- Data yang diambil (jika tersedia di label):
  - Takaran saji (gram)
  - Jumlah sajian per kemasan
  - Gula per sajian (gram)
  - (Opsional) gula per kemasan
- Aplikasi melakukan:
  - Perhitungan **persentase kebutuhan gula harian** (estimasi).
  - Klasifikasi **level gula**: RENDAH / SEDANG / TINGGI.
- Ditampilkan dengan tampilan yang ringkas & mudah dimengerti.

### 2. Trek Konsumsi Gula (Daily Sugar Tracking)

- Setelah scan, hasil dapat disimpan sebagai **trek harian**.
- Trek menyimpan nilai gula per sajian/per produk.
- Pengguna dapat melihat riwayat untuk:
  - Menyadari frekuensi konsumsi gula.
  - Mengontrol kebiasaan minum/makan manis.

### 3. Beranda (Home)

- Entry point utama aplikasi.
- Menyediakan:
  - Akses cepat ke **Scan**, **Trek**, **Katalog**, **Notifikasi**, **Profil**.
  - Ringkasan singkat atau insight (dapat dikembangkan).

### 4. Katalog Produk (Catalog)

- Berisi daftar produk contoh.
- Dapat digunakan sebagai:
  - Referensi produk dengan gula rendah/sedang/tinggi.
  - Media edukasi terkait pilihan konsumsi yang lebih sehat.

### 5. Notifikasi Harian (Daily Sugar Notification)

- Pengingat konsumsi gula harian.
- Menggunakan **worker terjadwal** (contoh: `DailySugarNotificationWorker`) untuk:
  - Mengirim notifikasi harian kepada pengguna.
  - Mengajak pengguna melakukan scan atau mengecek trek konsumsinya.
- Tersedia **Notification Screen** untuk pengaturan.

### 6. Profil Pengguna (Profile Screen)

- Halaman profil tempat personalisasi pengalaman.
- Dapat dikembangkan untuk:
  - Target gula harian.
  - Preferensi notifikasi.
  - Pengaturan akun / autentikasi.

---

## 🧠 Arsitektur & Pola Desain

SAJISEHAT memanfaatkan arsitektur modern Android yang modular dan scalable.

### Arsitektur Utama

- **MVVM (Model–View–ViewModel)**  
  - UI (View/Composable) hanya mengonsumsi `UiState` dari `ViewModel`.
  - Logika bisnis dan pengambilan data diletakkan di ViewModel + Repository.

- **Feature-based Modularization**  
  Folder dipisah per fitur:
  - `feature/scan`
  - `feature/home`
  - `feature/catalog`
  - `feature/notification`
  - `feature/profile`
  - dll.

- **State Management**  
  - `StateFlow` & `MutableStateFlow` untuk menyimpan state UI (misalnya `ScanUiState`).
  - UI mengobserve dengan `collectAsState()` di Jetpack Compose.

### Contoh Struktur Proyek (Ringkas)

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
