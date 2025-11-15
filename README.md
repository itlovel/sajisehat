[![Banner SAJISEHAT](https://github.com/itlovel/sajisehat/blob/main/screenshot/frontend_sajisehat_banner.png?raw=true)](https://github.com/itlovel/sajisehat/blob/main/screenshot/frontend_sajisehat_banner.png?raw=true)

# SAJISEHAT 🍽️  
**Aplikasi Pemindai Konsumsi Gula Harian**

SAJISEHAT adalah aplikasi mobile berbasis Android yang membantu pengguna memahami dan memantau konsumsi gula harian melalui **pemindaian label gizi**.  
Pengguna cukup memotret label gizi produk (misalnya minuman kemasan), lalu aplikasi akan:

- Membaca kandungan gula per sajian
- Mengkonversi ke estimasi **persentase kebutuhan gula harian**
- Menyimpan riwayat konsumsi gula untuk dipantau dari waktu ke waktu  

Cocok untuk:  
**mahasiswa, pekerja, dan keluarga muda** yang ingin tetap menikmati minuman manis, tapi dengan kontrol yang lebih sadar dan terukur. 😉

---

## 🧩 Tujuan & Masalah yang Ingin Diselesaikan

### Masalah yang Umum Terjadi
- Banyak orang **tidak membaca** atau **tidak paham** label gizi.
- Informasi seperti *“Gula 18 g per sajian”* terasa abstrak.
- Tidak ada tracking sederhana untuk melihat **total gula harian** yang sudah dikonsumsi.

### Solusi yang Ditawarkan SAJISEHAT
1. **Scan Label Gizi Otomatis**  
   Menggunakan kamera dan **Google ML Kit**, aplikasi mendeteksi teks pada label gizi, mengekstrak informasi gula, dan menampilkannya dengan bahasa yang lebih sederhana.

2. **Konversi ke Estimasi Gula Harian**  
   Kami mengestimasi persentase konsumsi gula dari batas rekomendasi harian (misalnya berdasarkan acuan WHO / angka kecukupan lainnya) sehingga lebih mudah dipahami.

3. **Tracking Konsumsi Gula**  
   Hasil setiap scan dapat disimpan sebagai **riwayat harian**, sehingga pengguna bisa:
   - Menyadari pola konsumsi gula.
   - Mengurangi kebiasaan minum/makan manis berlebih.

---

## ✨ Fitur Utama

### 1. 🔐 Autentikasi & Onboarding
- Registrasi & login menggunakan **email + password**.
- Integrasi **Firebase Authentication**.
- Onboarding screen untuk menjelaskan konsep SAJISEHAT kepada pengguna baru.
- Data profil pengguna disimpan di **Cloud Firestore**.

### 2. 📷 Scan Label Gizi (Scan Screen)
- Menggunakan:
  - **Google ML Kit Document Scanner**
  - **Google ML Kit Text Recognition**
- Alur:
  1. Pengguna memotret label gizi.
  2. Sistem membaca teks, melakukan parsing, dan mencari segmen yang relevan (gula, takaran saji, dll.).
  3. Aplikasi menampilkan:
     - Kandungan gula per sajian.
     - Estimasi persentase kebutuhan gula harian.
     - Insight sederhana (misalnya “cukup tinggi”, “perlu diperhatikan”, dll. – dapat dikembangkan).

### 3. 📊 Trek Konsumsi Gula (Trek Screen)
- Menampilkan daftar riwayat hasil scan per hari.
- Pengguna dapat melihat:
  - Tanggal dan waktu konsumsi.
  - Nama/jenis produk.
  - Estimasi kontribusi gula terhadap total harian.
- Dapat dikembangkan menjadi grafik tren konsumsi gula.

### 4. 🏠 Beranda (Home Screen)
- Ringkasan singkat:
  - Akses cepat ke fitur **Scan**, **Trek**, **Katalog**, **Notifikasi**, **Profil**.
  - Tips kesehatan singkat seputar konsumsi gula.
  - Banner edukasi singkat yang dapat dirotasi/di-scroll.

### 5. 📚 Katalog Produk (Catalog)
- Menampilkan daftar produk contoh yang sudah di-*pre-fill*.
- Dapat berfungsi sebagai:
  - Referensi produk dengan kandungan gula **rendah/sedang/tinggi**.
  - Media edukasi tentang pilihan konsumsi yang lebih sehat.

### 6. 🔔 Notifikasi Gula Harian (Daily Sugar Reminder)
- Menggunakan **WorkManager** untuk menjadwalkan notifikasi harian.
- Notifikasi mengingatkan pengguna untuk:
  - Melakukan scan minuman/makanan yang dikonsumsi hari itu.
  - Memeriksa total konsumsi gula harian di halaman Trek.

### 7. 🙋‍♀️ Profil Pengguna (Profile Screen)
- Menampilkan informasi dasar profil (nama, email, foto).
- Pengaturan sederhana (misal: logout, preferensi notifikasi).
- Dapat dikembangkan untuk:
  - Target gula harian personal.
  - Preferensi bahasa atau tema.

---

## 🧰 Daftar Framework, Library, dan Tools yang Digunakan  

### 1. Bahasa & Platform
- **Kotlin** (Android)
- **Android SDK**
- **Gradle (Kotlin DSL)**

### 2. Framework & UI
- **Jetpack Compose**  
  - `androidx.compose.ui`, `androidx.compose.foundation`, `androidx.compose.material3`
- **AndroidX Activity Compose**  
  - `androidx.activity:activity-compose`
- **AndroidX Core KTX**  
  - `androidx.core:core-ktx`
- **AndroidX Lifecycle Runtime KTX**  
  - `androidx.lifecycle:lifecycle-runtime-ktx`
- (Opsional / dapat diaktifkan) **Navigation Compose**  
  - `androidx.navigation:navigation-compose`

### 3. Arsitektur, State Management & Background Task
- **MVVM (Model–View–ViewModel)**  
  - ViewModel + Flow/State untuk pengelolaan UI state.
- **Kotlin Coroutines & Flow**  
  - Untuk operasi asynchronous dan reaktif.
- **WorkManager**  
  - `androidx.work:work-runtime-ktx` untuk penjadwalan notifikasi harian.

### 4. Backend & Autentikasi
- **Firebase Authentication**  
  - `com.google.firebase:firebase-auth`
- **Cloud Firestore**  
  - `com.google.firebase:firebase-firestore`
- **Google Identity Services & AndroidX Credentials**  
  - `androidx.credentials:credentials`
  - `androidx.credentials:credentials-play-services-auth`
  - `com.google.android.libraries.identity.googleid:googleid`

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
  - Preferensi pengguna (misalnya pengingat harian).

### 8. Image Loading & UI Pendukung
- **Coil Compose**  
  - `io.coil-kt:coil-compose` untuk memuat gambar (foto profil, ilustrasi, dsb).
- **AndroidX ExifInterface**  
  - `androidx.exifinterface:exifinterface` untuk manajemen metadata gambar (bila diperlukan).
- **AndroidX TV Material (opsional)**  
  - `androidx.tv:tv-material` – dapat dimanfaatkan untuk komponen UI tambahan.

### 9. Pengembangan & Build Tools
- **Android Studio** (Koala+, Ladybug, atau versi terbaru)
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

- **Layer Data**
  - Repository untuk:
    - Autentikasi (`AuthRepository`, `FirebaseAuthRepository`)
    - Scan & parsing label gizi (`ScanRepository`, `NutritionLabelParser`)
    - Trek riwayat konsumsi (`TrekRepository`)
    - Prefs lokal (`AppPrefs` via DataStore)

- **Layer Domain (Logic)**
  - Logika:
    - Ekstraksi gula dari teks label.
    - Perhitungan estimasi persentase gula harian.
    - Pengelolaan riwayat scan & notifikasi.

- **Layer Presentation (UI)**
  - Jetpack Compose Screen:
    - `OnboardingScreen`, `LoginScreen`, `RegisterScreen`
    - `HomeScreen`, `ScanScreen`, `TrekScreen`
    - `CatalogScreen`, `NotificationScreen`, `ProfileScreen`
  - Masing-masing terhubung ke `ViewModel` terkait.

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
