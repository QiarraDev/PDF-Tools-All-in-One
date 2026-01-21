Dokumen ini menjelaskan desain sistem teknis untuk aplikasi PDF Tools All‑in‑One, dengan fokus pada offline processing, performa ringan, dan keamanan agar aman dirilis di Google Play Store serta cocok untuk passive income.
1. Tujuan Sistem
Menyediakan tools PDF lengkap berbasis on‑device processing
Tidak bergantung pada backend/server
Mudah dikembangkan dan dirawat oleh solo developer
Aman dari sisi privasi & kebijakan Play Store
2. Arsitektur Sistem
2.1. Clean Architecture
Memisahkan logika bisnis dari UI dan data layer
Memudahkan testing & maintenance
Memungkinkan penggantian library tanpa mengubah bisnis logic
2.2. Layering
Presentation Layer
Activity/Fragment
ViewModel
ViewBinding/Compose UI
Data Layer
Repository
PDF Library (on‑device)
File System Access
Domain Layer
UseCases (opsional untuk MVP)
Business Logic
2.3. Dependency Injection
Menggunakan Hilt untuk dependency injection
Memudahkan manajemen dependensi
Mendukung modularisasi di masa depan
3. Teknologi Stack
3.1. Bahasa Pemrograman
Kotlin (primary)
Java (legacy code, jika ada)
3.2. PDF Processing Libraries
AndroidPdfViewer (viewing)
PdfBox (manipulasi, konversi)
iText (opsional, berbayar)
MuPDF (opsional, performa tinggi)
3.3. UI Framework
Jetpack Compose (primary)
XML Layouts (legacy, jika ada)
3.4. Architecture Components
ViewModel
LiveData/StateFlow
Room (untuk metadata, jika perlu)
3.5. Storage
Internal Storage (cache, temporary files)
External Storage (user files)
Scoped Storage (Android 10+)
3.6. Dependency Injection
Hilt
3.7. Testing
JUnit
Espresso
Mockito
4. Desain Database (Opsional)
4.1. Metadata Database
Digunakan untuk menyimpan metadata file PDF
Tidak menyimpan konten PDF
Memudahkan pencarian & manajemen file
4.2. Tabel
FileMetadata
id (PK)
filePath (String)
fileName (String)
fileSize (Long)
createdDate (Long)
modifiedDate (Long)
pageCount (Int)
4.3. Database Library
Room (disarankan)
SQLite (native)
5. Desain API (Jika Ada)
5.1. API untuk Update & Analytics
Firebase Remote Config (untuk flag fitur)
Firebase Analytics (untuk usage tracking)
Firebase Crashlytics (untuk error reporting)
5.2. API untuk Iklan
AdMob (primary)
Unity Ads (opsional)
5.3. API untuk Pembelian Premium
Google Play Billing
5.4. API untuk Sync (Opsional)
Google Drive API
Dropbox API
6. Desain UI/UX
6.1. User Flow
User membuka aplikasi → Pilih tools → Pilih file PDF → Proses → Simpan/Share
6.2. Screen Flow
Splash Screen → Home Screen → Tool Selection → File Selection → Processing → Result Screen
6.3. Navigation
Bottom Navigation Bar (untuk tools utama)
Drawer Menu (untuk settings & info)
6.4. Design System
Material Design 3
Dark Mode support
Accessibility support
7. Desain Keamanan
7.1. Data Privacy
Tidak menyimpan data user di server
Semua processing dilakukan on‑device
Tidak mengumpulkan data sensitif
7.2. File Security
Scoped Storage implementation
File access permission handling
7.3. Network Security
HTTPS/TLS untuk semua komunikasi API
Certificate pinning (opsional)
7.4. Obfuscation
ProGuard/R8 untuk obfuscation
Anti‑tampering protection
8. Desain Performansi
8.1. Memory Management
Bitmap caching strategy
Lazy loading untuk file besar
Garbage collection optimization
8.2. CPU Optimization
Background processing untuk operasi berat
Progress bar untuk operasi panjang
Cancellation support
8.3. Storage Optimization
Cache management
File cleanup strategy
8.4. Battery Optimization
WorkManager untuk background tasks
Batch operations
9. Desain Testing
9.1. Unit Tests
Business logic
UseCases
Repositories
9.2. Integration Tests
ViewModel + Repository
9.3. UI Tests
Espresso tests
Compose tests
9.4. Performance Tests
Memory usage tests
CPU usage tests
File processing time tests
10. Desain Deployment
10.1. Build Variants
Debug build
Release build
10.2. App Bundles
Android App Bundle (AAB)
10.3. Versioning
Semantic versioning
10.4. Release Management
Staged rollouts
Beta testing track
11. Desain Monitoring
11.1. Crash Reporting
Firebase Crashlytics
11.2. Analytics
Firebase Analytics
11.3. Performance Monitoring
Firebase Performance Monitoring
11.4. Error Logging
Timber
12. Desain Monetization
12.1. Free Version
Ads supported
Limited features
12.2. Premium Version
One‑time purchase
All features unlocked
No ads
12.3. Subscription (Opsional)
Monthly/yearly subscription
Cloud sync features
13. Desain Scalability
13.1. Modular Architecture
Memungkinkan penambahan tools baru dengan mudah
13.2. Database Schema
Schema evolution support
13.3. API Versioning
Backward compatibility
14. Desain Maintenance
14.1. Code Quality
Lint checks
Code formatting
14.2. Documentation
Technical documentation
API documentation
15. Desain Testing
15.1. Unit Tests
Business logic
UseCases
Repositories
15.2. Integration Tests
ViewModel + Repository
15.3. UI Tests
Espresso tests
Compose tests
15.4. Performance Tests
Memory usage tests
CPU usage tests
File processing time tests
16. Desain Deployment
16.1. Build Variants
Debug build
Release build
16.2. App Bundles
Android App Bundle (AAB)
16.3. Versioning
Semantic versioning
16.4. Release Management
Staged rollouts
Beta testing track
17. Desain Monitoring
17.1. Crash Reporting
Firebase Crashlytics
17.2. Analytics
Firebase Analytics
17.3. Performance Monitoring
Firebase Performance Monitoring
17.4. Error Logging
Timber
18. Desain Monetization
18.1. Free Version
Ads supported
Limited features
18.2. Premium Version
One‑time purchase
All features unlocked
No ads
18.3. Subscription (Opsional)
Monthly/yearly subscription
Cloud sync features
19. Desain Scalability
19.1. Modular Architecture
Memungkinkan penambahan tools baru dengan mudah
19.2. Database Schema
Schema evolution support
19.3. API Versioning
Backward compatibility
20. Desain Maintenance
20.1. Code Quality
Lint checks
Code formatting
20.2. Documentation
Technical documentation
API documentation
21. Lingkup Fitur Sistem
- Fitur Inti:
    - Scan Foto / Image → PDF
    - JPG / PNG → PDF
    - PDF Merge
    - PDF Compress
    - PDF Lock (Password)
- Fitur Pendukung:
    - Preview PDF
    - Reorder halaman
    - Rename file
    - Share & download
- Alur Scan Foto → PDF:
    - Ambil bitmap dari kamera / galeri
    - Deteksi area dokumen
    - Auto crop & deskew
    - Enhance brightness & contrast
    - Generate PDF
