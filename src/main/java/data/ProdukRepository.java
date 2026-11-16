package data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import db.Database;
import model.Produk;

public class ProdukRepository {

    public void add(Produk produk) throws SQLException {
        String sql = "INSERT INTO produk (nama, harga, foto) VALUES (?, ?, ?)";
        try ( 
            Connection koneksi = Database.bukaKoneksi();  
            PreparedStatement perintah = koneksi.prepareStatement(sql)) {
            perintah.setString(1, produk.getNama());
            perintah.setLong(2, produk.getHarga());
            perintah.setString(3, produk.getFoto());
            perintah.executeUpdate();
        }
    }

    /**
     * Mengubah nama, harga, dan foto produk yang ID-nya sama.
     * @return true jika ada baris yang berubah, false jika ID tidak ditemukan
     */
    public boolean update(Produk produk) throws SQLException {
        String sql = "UPDATE produk SET nama = ?, harga = ?, foto = ? WHERE id = ?";
        try (
            Connection koneksi = Database.bukaKoneksi();
            PreparedStatement perintah = koneksi.prepareStatement(sql)) {
            perintah.setString(1, produk.getNama());
            perintah.setLong(2, produk.getHarga());
            perintah.setString(3, produk.getFoto());
            perintah.setLong(4, produk.getId());
            int baris = perintah.executeUpdate();
            return baris > 0;
        }
    }

    /**
     * Menghapus produk berdasarkan ID.
     * @return true jika ada baris yang terhapus, false jika ID tidak ditemukan
     */
    public boolean delete(long id) throws SQLException {
        String sql = "DELETE FROM produk WHERE id = ?";
        try (
            Connection koneksi = Database.bukaKoneksi();
            PreparedStatement perintah = koneksi.prepareStatement(sql)) {
            perintah.setLong(1, id);
            int baris = perintah.executeUpdate();
            return baris > 0;
        }
    }

    public ArrayList<Produk> getAll() throws SQLException {
        ArrayList<Produk> daftarProduk = new ArrayList<Produk>();
        String sql = "SELECT id, nama, harga, foto FROM produk ORDER BY id";
        try ( 
            Connection koneksi = Database.bukaKoneksi();  
            PreparedStatement perintah = koneksi.prepareStatement(sql);  
            ResultSet hasil = perintah.executeQuery()) {
            while (hasil.next()) {
                long id = hasil.getLong("id");
                String nama = hasil.getString("nama");
                long harga = hasil.getLong("harga");
                String foto = hasil.getString("foto");
                Produk produk = new Produk(id, nama, harga, foto);
                daftarProduk.add(produk);
            }
        }
        return daftarProduk;
    }
}
