[![Banner SAJISEHAT](https://github.com/itlovel/sajisehat/blob/main/screenshot/frontend_sajisehat_banner.png?raw=true)](https://github.com/itlovel/sajisehat/blob/main/screenshot/frontend_sajisehat_banner.png?raw=true)

# SAJISEHAT 🍽️  
**Aplikasi Pemindai Konsumsi Gula Harian**

SAJISEHAT adalah aplikasi mobile berbasis Android yang membantu pengguna memahami dan memantau konsumsi gula harian melalui **pemindaian label gizi**.  
Pengguna cukup memotret label gizi produk (misalnya minuman kemasan), lalu aplikasi akan:

- Mengirim gambar ke backend untuk mendeteksi bagian penting pada label gizi  
- Membaca teks dari bagian-bagian tersebut (OCR)  
- Mengonversi ke estimasi **persentase kebutuhan gula harian**  
- Menyimpan riwayat konsumsi gula untuk dipantau dari waktu ke waktu  

Cocok untuk:  
**remaja dan dewasa muda** yang ingin tetap menikmati makanan dan minuman kemasan, tapi dengan kontrol yang lebih sadar dan terukur. 😉

---

## 🧩 Tujuan & Masalah yang Ingin Diselesaikan

### Masalah yang Umum Terjadi
- Banyak orang **tidak membaca** atau **tidak paham** label gizi.  
- Konsumsi gula berlebih karena tidak ada gambaran yang jelas.  
- Tidak ada tracking sederhana untuk melihat **total gula harian** yang sudah dikonsumsi.

### Solusi yang Ditawarkan SAJISEHAT
1. **Scan Label Gizi Terbantu AI**  
   Menggunakan kamera dan **Google ML Kit Document Scanner** untuk mengambil gambar label gizi, kemudian backend (Flask + Roboflow) membantu mendeteksi bagian penting seperti takaran saji, sajian per kemasan, dan total gula.

2. **Konversi ke Estimasi Gula Harian**  
   Aplikasi mengestimasi persentase konsumsi gula dari batas rekomendasi harian (misalnya berdasarkan acuan WHO / angka kecukupan lainnya) sehingga lebih mudah dipahami.

3. **Tracking Konsumsi Gula**  
   Hasil setiap scan dapat disimpan sebagai **riwayat harian**, sehingga pengguna bisa:
   - Menyadari pola konsumsi gula.  
   - Mengurangi kebiasaan minum/makan manis berlebih.

---

## 🔄 Alur Fitur Scan (End-to-End)

![Alur Fitur Scan](https://github.com/itlovel/sajisehat/raw/main/screenshot/alur_fitur_scan.png)

1. **Capture Gambar – Frontend**  
   - Pengguna memotret label gizi menggunakan **Google ML Kit Document Scanner** sebagai antarmuka kamera.

2. **Kirim ke Backend – Layout/Object Detection**  
   - Aplikasi mengirim gambar ke **SAJISEHAT Backend API** (Flask di Render).  
   - Backend memanggil **Roboflow Workflow** untuk mendeteksi **tiga objek utama**:
     - Takaran saji  
     - Jumlah sajian per kemasan  
     - Total gula  
   - Backend mengembalikan ke aplikasi **bounding box/ROI** untuk masing-masing objek.

3. **OCR di Perangkat – ML Kit Text Recognition**  
   - Aplikasi menggunakan **Google ML Kit Text Recognition** untuk melakukan OCR **hanya pada ROI** yang sudah ditandai backend.  
   - Dengan cara ini, OCR menjadi:
     - Lebih fokus  
     - Lebih akurat  
     - Minim gangguan teks lain di label

4. **Parsing & Perhitungan Gula**  
   - Hasil OCR diparsing menjadi:
     - `serving_size_gram`  
     - `servings_per_pack`  
     - `sugar_per_serving_gram` / `sugar_per_pack_gram`  
   - Aplikasi menghitung estimasi **persentase kebutuhan gula harian** per produk.

5. **Penyimpanan & Visualisasi**  
   - Data disimpan sebagai riwayat konsumsi di **Cloud Firestore**.  
   - Pengguna dapat melihat riwayat dan tren konsumsi gula harian di aplikasi.

---

## ✨ Fitur Utama

### 1. 🔐 Autentikasi & Onboarding
- Registrasi & login menggunakan **email + password**  
- Integrasi **Firebase Authentication**  
- Onboarding screen untuk menjelaskan konsep SAJISEHAT kepada pengguna baru  
- Data profil pengguna disimpan di **Cloud Firestore**

### 2. 📷 Scan Label Gizi (Scan Screen)
- Menggunakan:
  - **Google ML Kit Document Scanner** untuk antarmuka kamera  
  - **Google ML Kit Text Recognition** untuk OCR di perangkat  
  - **SAJISEHAT Backend API** (Flask + Roboflow) untuk deteksi layout
- Alur:
  1. Pengguna memotret label gizi.  
  2. Aplikasi mengirim gambar ke backend → backend mendeteksi ROI (takaran saji, sajian per kemasan, total gula) → backend mengembalikan bounding box.  
  3. Aplikasi menjalankan ML Kit Text Recognition di setiap ROI.  
  4. Aplikasi menampilkan:
     - Kandungan gula per sajian  
     - Estimasi persentase kebutuhan gula harian  
     - Insight sederhana (misalnya “cukup tinggi”, “perlu diperhatikan”, dll. – dapat dikembangkan)

### 3. 📊 Trek Konsumsi Gula (Trek Screen)
- Menampilkan daftar riwayat hasil scan per hari  
- Pengguna dapat melihat:
  - Tanggal dan waktu konsumsi  
  - Nama/jenis produk  
  - Estimasi kontribusi gula terhadap total harian  
- Dapat dikembangkan menjadi grafik tren konsumsi gula

### 4. 🏠 Beranda (Home Screen)
- Ringkasan singkat:
  - Akses cepat ke fitur **Scan**, **Trek**, **Katalog**, **Notifikasi**, **Profil**  
  - Tips kesehatan singkat seputar konsumsi gula  
  - Banner edukasi singkat yang dapat dirotasi/di-scroll

### 5. 📚 Katalog Produk (Catalog)
- Menampilkan daftar produk contoh yang sudah di-*pre-fill*  
- Dapat berfungsi sebagai:
  - Referensi produk dengan kandungan gula **rendah/sedang/tinggi**  
  - Media edukasi tentang pilihan konsumsi yang lebih sehat  

### 6. 🔔 Notifikasi Gula Harian (Daily Sugar Reminder)
- Menggunakan **WorkManager** untuk menjadwalkan notifikasi harian  
- Notifikasi mengingatkan pengguna untuk:
  - Melakukan scan minuman/makanan yang dikonsumsi hari itu  
  - Memeriksa total konsumsi gula harian di halaman Trek  

### 7. 🙋‍♀️ Profil Pengguna (Profile Screen)
- Menampilkan informasi dasar profil (nama, email, foto)  
- Pengaturan sederhana (misal: logout, preferensi notifikasi)  
- Melihat produk yang ditandai  
- Dapat dikembangkan untuk:
  - Target gula harian personal  
  - Preferensi bahasa atau tema  

---

## 🧰 Daftar Framework, Library, dan Tools yang Digunakan

### 1. Bahasa & Platform
- **Kotlin** (Android)  
- **Android SDK**  
- **Gradle (Kotlin DSL)**  

### 2. Framework & UI
- **Jetpack Compose**  
  - `androidx.compose.ui`  
  - `androidx.compose.foundation`  
  - `androidx.compose.material3`  
- **AndroidX Activity Compose**  
  - `androidx.activity:activity-compose`  
- **AndroidX Core KTX**  
  - `androidx.core:core-ktx`  
- **AndroidX Lifecycle Runtime KTX**  
  - `androidx.lifecycle:lifecycle-runtime-ktx`  
- (Opsional) **Navigation Compose**  
  - `androidx.navigation:navigation-compose`  

### 3. Arsitektur, State Management & Background Task
- **MVVM (Model–View–ViewModel)**  
  - ViewModel + Flow/State untuk pengelolaan UI state  
- **Kotlin Coroutines & Flow**  
  - Untuk operasi asynchronous dan reaktif  
- **WorkManager**  
  - `androidx.work:work-runtime-ktx` untuk penjadwalan notifikasi harian  

### 4. Backend & Autentikasi
- **Firebase Authentication**  
  - `com.google.firebase:firebase-auth`  
- **Cloud Firestore**  
  - `com.google.firebase:firebase-firestore`  
- **Google Identity Services & AndroidX Credentials**  
  - `androidx.credentials:credentials`  
  - `androidx.credentials:credentials-play-services-auth`  
  - `com.google.android.libraries.identity.googleid:googleid`  
- **SAJISEHAT Backend API (Flask + Roboflow)**  
  - Diakses via Retrofit sebagai REST API eksternal untuk deteksi layout label gizi  

### 5. Computer Vision & Text Recognition (ML)
- **Google ML Kit Document Scanner**  
  - `com.google.android.gms:play-services-mlkit-document-scanner`  
- **Google ML Kit Text Recognition**  
  - `com.google.mlkit:text-recognition`  
- **Google Play Services ML Kit**  
  - `com.google.android.gms:play-services-mlkit-text-recognition`  
  - `com.google.android.gms:play-services-mlkit-text-recognition-common`  
- **ML Kit Vision Common**  
  - `com.google.mlkit:vision-common`  

### 6. Networking & Data Parsing
- **Retrofit 2**  
  - `com.squareup.retrofit2:retrofit`  
- **Gson Converter**  
  - `com.squareup.retrofit2:converter-gson`  
- **OkHttp**  
  - `com.squareup.okhttp3:okhttp`  

### 7. Penyimpanan Data Lokal
- **AndroidX DataStore Preferences**  
  - `androidx.datastore:datastore-preferences`  
  - `androidx.datastore:datastore-preferences-core-android`  
  Digunakan untuk menyimpan:
  - Status onboarding  
  - Preferensi pengguna (misalnya pengingat harian)  

### 8. Image Loading & UI Pendukung
- **Coil Compose**  
  - `io.coil-kt:coil-compose` untuk memuat gambar (foto profil, ilustrasi, dsb.)  
- **AndroidX ExifInterface**  
  - `androidx.exifinterface:exifinterface` untuk manajemen metadata gambar (bila diperlukan)  
- **AndroidX TV Material (opsional)**  
  - `androidx.tv:tv-material` – dapat dimanfaatkan untuk komponen UI tambahan  

### 9. Pengembangan & Build Tools
- **Android Studio** (Meerkat)  
- **Gradle** (Android Gradle Plugin)  
- **Git & GitHub/GitLab** untuk version control  

### 10. Testing
- **JUnit 4**  
  - `junit:junit`  
- **AndroidX Test JUnit**  
  - `androidx.test.ext:junit`  
- **Espresso**  
  - `androidx.test.espresso:espresso-core`  
- **Compose UI Test**  
  - `androidx.compose.ui:ui-test-junit4`  

---

## 🏛️ Arsitektur Aplikasi

Aplikasi menerapkan pola sederhana berbasis **Clean Architecture + MVVM**:

### Layer Data
- Repository untuk:
  - Autentikasi (`AuthRepository`, `FirebaseAuthRepository`)  
  - Scan label gizi (`ScanRepository`)  
    - Mengirim gambar ke **SAJISEHAT Backend API**  
    - Menerima bounding box/ROI dari backend  
  - Pengolahan hasil OCR & riwayat konsumsi (`TrekRepository`)  
  - Prefs lokal (`AppPrefs` via DataStore)  

### Layer Domain (Logic)
- Logika:
  - Menggabungkan bounding box (dari backend) dan hasil OCR (dari ML Kit)  
  - Perhitungan estimasi persentase gula harian  
  - Pengelolaan riwayat scan & notifikasi  

### Layer Presentation (UI)
- Jetpack Compose Screen:
  - `OnboardingScreen`, `LoginScreen`, `RegisterScreen`  
  - `HomeScreen`, `ScanScreen`, `TrekScreen`  
  - `CatalogScreen`, `NotificationScreen`, `ProfileScreen`  
- Masing-masing terhubung ke ViewModel terkait.

---

## 📁 Struktur Proyek (Ringkasan)

```text
app/
 ├─ src/main/
 │   ├─ java/com/example/sajisehat/
 │   │   ├─ App.kt
 │   │   ├─ MainActivity.kt
 │   │   ├─ data/
 │   │   │   ├─ auth/...
 │   │   │   ├─ scan/...
 │   │   │   ├─ trek/...
 │   │   │   └─ prefs/...
 │   │   ├─ feature/
 │   │   │   ├─ auth/
 │   │   │   ├─ home/
 │   │   │   ├─ catalog/
 │   │   │   ├─ notification/
 │   │   │   ├─ profile/
 │   │   │   └─ scan/
 │   │   ├─ ui/components/
 │   │   └─ ui/theme/
 │   ├─ res/...
 │   └─ AndroidManifest.xml
 └─ build.gradle.kts
