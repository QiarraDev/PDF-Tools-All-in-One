Dokumen ini adalah dokumen sistem lengkap (system document) sebagai pelengkap system-design, berisi spesifikasi fungsional, non‑fungsional, dan panduan implementasi.

1. Overview

Nama Produk (sementara): Smart Utility Tools
Platform: Android (Play Store)
Model Bisnis: Freemium (Ads + Premium Unlock)

Aplikasi Utility menyediakan kumpulan tools kecil, cepat, dan ringan yang dapat digunakan secara offline untuk kebutuhan harian pengguna.

2. Ruang Lingkup Fitur
2.1 Fitur Utama (MVP – PDF Tools All‑in‑One):
 - Scan Dokumen → PDF
 - PDF Merge (gabung beberapa PDF)
 - PDF Compress
 - JPG / PNG → PDF
 - PDF Lock (password)

2.2 Fitur Premium:
 - Unlimited proses
 - Batch processing
 - Tanpa watermark
 - Tanpa iklan
 - Kualitas compress lebih tinggi

3. Functional Requirements
    FR-01 Image Resize: 
        - User memilih gambar dari device
        - User memilih ukuran output
        - Sistem memproses gambar secara lokal
        - Sistem menyimpan hasil ke device

    FR-02 Image Compress:
        - Sistem mengurangi ukuran file tanpa mengubah rasio
    FR-03 JPG to PDF:
        - User memilih beberapa gambar
        - Sistem menggabungkan menjadi 1 PDF


4. Non-Functional Requirements:
    - Aplikasi ringan (< 30 MB)
    - Proses lokal (tanpa upload file)
    - Respons < 2 detik per proses
    - Stabil di Android 8+  