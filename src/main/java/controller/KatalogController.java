package controller;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import data.ProdukRepository;
import model.Produk;

public class KatalogController {

    private static final String PETUNJUK_TAMBAH = "Isi form, simpan, lalu lihat katalog.";

    @FXML
    private TextField namaField;
    @FXML
    private TextField hargaField;
    @FXML
    private ComboBox<String> fotoBox;
    @FXML
    private Label petunjukLabel;
    @FXML
    private Button simpanButton;
    @FXML
    private Button batalButton;
    @FXML
    private Label pesanLabel;
    @FXML
    private Label jumlahLabel;
    @FXML
    private TilePane katalogPane;
    private final ProdukRepository repository = new ProdukRepository();

    // Produk yang sedang diedit. null berarti form dalam mode tambah produk baru.
    private Produk produkDiedit;

    @FXML
    private void initialize() {
        fotoBox.getItems().add("roti.png");
        fotoBox.getItems().add("susu.png");
        fotoBox.getItems().add("kopi.png");
        fotoBox.setValue("roti.png");
        try {
            showData();
        } catch (SQLException e) {
            pesanLabel.setText("Gagal membaca database. Lihat Output.");
            e.printStackTrace();
        }
    }

    private void showData() throws SQLException {
        ArrayList<Produk> daftar = repository.getAll();
        katalogPane.getChildren().clear();
        for (Produk produk : daftar) {
            VBox kartu = createProductCard(produk);
            katalogPane.getChildren().add(kartu);
        }
        jumlahLabel.setText("Jumlah produk: " + daftar.size());
    }

    private VBox createProductCard(Produk produk) {
        ImageView fotoView = new ImageView();
        fotoView.setFitWidth(170);
        fotoView.setFitHeight(110);
        fotoView.setPreserveRatio(true);
        StackPane bingkai = new StackPane();
        bingkai.setPrefSize(180, 120);
        bingkai.setMinSize(180, 120);
        bingkai.setMaxSize(180, 120);

        URL lokasi = getClass().getResource("/image/" + produk.getFoto());
        if (lokasi != null) {
            Image gambar = new Image(lokasi.toExternalForm(),
                    170, 110, true, true);
            if (!gambar.isError()) {
                fotoView.setImage(gambar);
                bingkai.getChildren().add(fotoView);
            } else {
                bingkai.getChildren().add(new Label("Gambar tidak terbaca"));
            }
        } else {
            bingkai.getChildren().add(new Label("Gambar tidak tersedia"));
        }

        Label namaLabel = new Label(produk.getNama());
        namaLabel.setWrapText(true);
        namaLabel.getStyleClass().add("nama-produk");
        Label hargaLabel = new Label("Rp" + produk.getHarga());
        Label idLabel = new Label("ID: " + produk.getId());
        idLabel.getStyleClass().add("muted");

        // Tombol Edit dan Hapus pada setiap kartu. Keduanya membawa objek produk kartu itu.
        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("button-secondary");
        editButton.getStyleClass().add("button-kecil");
        editButton.setOnAction(e -> startEdit(produk));

        Button hapusButton = new Button("Hapus");
        hapusButton.getStyleClass().add("button-danger");
        hapusButton.setOnAction(e -> deleteProduct(produk));

        HBox aksi = new HBox();
        aksi.setSpacing(8);
        aksi.getChildren().add(editButton);
        aksi.getChildren().add(hapusButton);

        VBox kartu = new VBox();
        kartu.setSpacing(8);
        kartu.setMinWidth(220);
        kartu.setPrefWidth(220);
        kartu.setMaxWidth(220);
        kartu.getStyleClass().add("kartu-produk");
        // Beri bingkai biru pada kartu yang sedang diedit.
        if (produkDiedit != null && produkDiedit.getId() == produk.getId()) {
            kartu.getStyleClass().add("kartu-diedit");
        }
        kartu.getChildren().add(bingkai);
        kartu.getChildren().add(namaLabel);
        kartu.getChildren().add(hargaLabel);
        kartu.getChildren().add(idLabel);
        kartu.getChildren().add(aksi);
        return kartu;
    }

    @FXML
    private void saveProduct() {
        Produk produk;
        try {
            String nama = namaField.getText();
            long harga = Long.parseLong(hargaField.getText().trim());
            String foto = fotoBox.getValue();
            if (produkDiedit == null) {
                // Mode tambah: ID 0 karena ID baru ditentukan oleh database.
                produk = new Produk(nama, harga, foto);
            } else {
                // Mode edit: pakai ID produk lama supaya UPDATE mengenai baris yang benar.
                produk = new Produk(produkDiedit.getId(), nama, harga, foto);
            }
        } catch (NumberFormatException e) {
            pesanLabel.setText("Harga harus angka bulat, contoh 12000.");
            return;
        } catch (IllegalArgumentException e) {
            pesanLabel.setText(e.getMessage());
            return;
        }

        boolean modeEdit = (produkDiedit != null);
        try {
            if (modeEdit) {
                boolean berhasil = repository.update(produk);
                if (!berhasil) {
                    // ID tidak ada lagi di database (misalnya sudah dihapus).
                    resetForm();
                    try {
                        showData();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    pesanLabel.setText("Produk tidak ditemukan, mungkin sudah dihapus.");
                    return;
                }
            } else {
                repository.add(produk);
            }
        } catch (SQLException e) {
            pesanLabel.setText("Gagal menyimpan. Lihat Output.");
            e.printStackTrace();
            return;
        }

        resetForm();
        try {
            showData();
            if (modeEdit) {
                pesanLabel.setText("Produk berhasil diperbarui.");
            } else {
                pesanLabel.setText("Produk berhasil disimpan.");
            }
        } catch (SQLException e) {
            pesanLabel.setText("Data tersimpan, tetapi katalog gagal dimuat ulang.");
            e.printStackTrace();
        }
    }

    /** Dipanggil tombol Edit pada kartu: isi form dengan data produk lalu masuk mode edit. */
    private void startEdit(Produk produk) {
        produkDiedit = produk;
        namaField.setText(produk.getNama());
        hargaField.setText(String.valueOf(produk.getHarga()));
        fotoBox.setValue(produk.getFoto());
        petunjukLabel.setText("Mengedit produk ID " + produk.getId()
                + ". Ubah data, lalu klik Simpan Perubahan.");
        simpanButton.setText("Simpan Perubahan");
        batalButton.setVisible(true);
        batalButton.setManaged(true);
        pesanLabel.setText("");
        try {
            showData(); // muat ulang agar kartu yang diedit diberi bingkai biru
        } catch (SQLException e) {
            pesanLabel.setText("Gagal memuat ulang katalog. Lihat Output.");
            e.printStackTrace();
        }
    }

    /** Dipanggil tombol Batal: keluar dari mode edit tanpa mengubah database. */
    @FXML
    private void cancelEdit() {
        resetForm();
        try {
            showData();
            pesanLabel.setText("Edit dibatalkan.");
        } catch (SQLException e) {
            pesanLabel.setText("Gagal memuat ulang katalog. Lihat Output.");
            e.printStackTrace();
        }
    }

    /** Dipanggil tombol Hapus pada kartu: minta konfirmasi, lalu hapus dari database. */
    private void deleteProduct(Produk produk) {
        ButtonType ya = new ButtonType("Hapus", ButtonBar.ButtonData.OK_DONE);
        ButtonType tidak = new ButtonType("Batal", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert konfirmasi = new Alert(Alert.AlertType.CONFIRMATION,
                "Data akan dihapus permanen dari database.", ya, tidak);
        konfirmasi.setTitle("Hapus Produk");
        konfirmasi.setHeaderText("Hapus \"" + produk.getNama() + "\"?");
        Optional<ButtonType> jawaban = konfirmasi.showAndWait();
        if (jawaban.isEmpty() || jawaban.get() != ya) {
            return; // pengguna membatalkan, tidak ada yang dihapus
        }

        boolean terhapus;
        try {
            terhapus = repository.delete(produk.getId());
        } catch (SQLException e) {
            pesanLabel.setText("Gagal menghapus. Lihat Output.");
            e.printStackTrace();
            return;
        }

        // Jika produk yang dihapus sedang diedit, keluar dari mode edit.
        if (produkDiedit != null && produkDiedit.getId() == produk.getId()) {
            resetForm();
        }
        try {
            showData();
            if (terhapus) {
                pesanLabel.setText("Produk \"" + produk.getNama() + "\" berhasil dihapus.");
            } else {
                pesanLabel.setText("Produk tidak ditemukan, mungkin sudah dihapus.");
            }
        } catch (SQLException e) {
            pesanLabel.setText("Data terhapus, tetapi katalog gagal dimuat ulang.");
            e.printStackTrace();
        }
    }

    /** Mengosongkan form dan mengembalikannya ke mode tambah produk baru. */
    private void resetForm() {
        produkDiedit = null;
        namaField.clear();
        hargaField.clear();
        fotoBox.setValue("roti.png");
        petunjukLabel.setText(PETUNJUK_TAMBAH);
        simpanButton.setText("Simpan");
        batalButton.setVisible(false);
        batalButton.setManaged(false);
    }
}
