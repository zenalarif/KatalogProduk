# Katalog Produk 
Aplikasi desktop untuk mengelola katalog produk, dibuat dengan Java, JavaFX, dan SQLite. Data produk disimpan di database sehingga tetap ada saat aplikasi ditutup dan dibuka kembali. Pengguna dapat menambah produk lengkap dengan foto, mengedit, dan menghapusnya.
<img width="1920" height="1080" alt="gambar" src="https://github.com/user-attachments/assets/84d04598-eeb1-4561-9681-fd8bf36bd2ee" />
# Fitur
- Tambah produk — isi nama, harga, dan pilih foto dari kategori yang telah di sediakan, lalu produk tampil sebagai kartu di katalog.
- Edit produk — tombol Edit pada kartu memasukkan data ke form; ubah nama, harga, atau ganti foto, lalu Simpan Perubahan.
- Hapus produk — tombol Hapus meminta konfirmasi, lalu menghapus data dari database beserta file fotonya.
- Data permanen — disimpan di file SQLite, bukan hanya di memori.
- Validasi input — data yang tidak valid ditolak dengan pesan yang jelas (lihat bagian Validasi).
# Struktur Project
KatalogProduk
├── pom.xml
├── data/                           
│   ├── katalog.db                  
│   └── gambar/                    
└── src/main
    ├── java
    │   ├── main
    │   │   └── MainApp.java        
    │   ├── model
    │   │   └── Produk.java         
    │   ├── db
    │   │   └── Database.java       
    │   ├── data
    │   │   ├── ProdukRepository.java   
    │   │   └── FotoStorage.java        
    │   └── controller
    │       └── KatalogController.java  
    └── resources
        ├── view
        │   ├── katalog.fxml        
        │   └── styles/app.css      
        └── image/                  
Operasi SQL ada di ProdukRepository
Fitur	Perintah
Tambah =	INSERT INTO produk (nama, harga, foto) VALUES (?, ?, ?)
Tampilkan =	SELECT id, nama, harga, foto FROM produk ORDER BY id
Edit	UPDATE = produk SET nama = ?, harga = ?, foto = ? WHERE id = ?
Hapus =	DELETE FROM produk WHERE id = ?
# Cara Menjalankan
Prasyarat
JDK 17 atau lebih baru
Maven 3.6+
- Lewat terminal
Jalankan dari folder utama proyek (yang berisi pom.xml), karena folder data/ dibuat relatif terhadap lokasi tersebut.
bash
git clone https://github.com/<username>/KatalogProduk.git
cd KatalogProduk
mvn clean javafx:run
- Lewat NetBeans
File → Open Project, lalu pilih folder KatalogProduk.
Pastikan proyek memakai JDK 17 atau lebih baru.
Klik kanan proyek → Run (atau F6).
# Cara Menggunakan
1. Tambah: isi nama dan harga, klik Pilih Gambar... dan pilih foto, lalu klik Simpan.
2. Edit: klik Edit pada kartu produk. Kartu diberi bingkai biru dan form terisi data produk. Ubah datanya, lalu klik Simpan Perubahan. Klik Batal untuk membatalkan.
3. Hapus: klik Hapus pada kartu, lalu konfirmasi pada dialog.
4. Tutup dan buka kembali aplikasi untuk melihat bahwa data tetap tersimpan.
# Catatan
Menghapus atau menimpa data/katalog.db berarti menghilangkan atau mengganti seluruh data produk.
Folder data/ ada di luar target/ sehingga mvn clean tidak menghapus database.
Kolom id tidak memakai AUTOINCREMENT; jika produk dengan ID terbesar dihapus, ID tersebut dapat dipakai lagi oleh produk baru.
Pembuat
Nama: Zainal Arifin
