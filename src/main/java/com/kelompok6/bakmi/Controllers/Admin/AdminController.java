package com.kelompok6.bakmi.Controllers.Admin;

import com.kelompok6.bakmi.App;
import com.kelompok6.bakmi.Database.DBUtil;
import com.kelompok6.bakmi.Database.MenuDAO;
import com.kelompok6.bakmi.Models.Menu;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Window;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminController {

    @FXML private Button btnTopRefresh;
    @FXML private Button btnDashboard, btnOrders, btnMenu, btnReports, btnLogout;
    @FXML private Button btnBackToWelcome;
    @FXML private TabPane mainTabs;

    @FXML private ComboBox<String> cbStatusFilter;
    @FXML private TextField tfSearchOrders;
    @FXML private Button btnRefreshOrders;
    @FXML private TableView<OrderRow> tblOrders;
    @FXML private TableColumn<OrderRow, String> colKode;
    @FXML private TableColumn<OrderRow, String> colCustomer;
    @FXML private TableColumn<OrderRow, String> colTotal;
    @FXML private TableColumn<OrderRow, String> colStatus;
    @FXML private TableColumn<OrderRow, String> colWaktu;
    @FXML private TableColumn<OrderRow, Void> colAction;
    @FXML private TextArea taOrderDetail;

    @FXML private TextField tfSearchMenu;
    @FXML private Button btnAddMenu, btnEditMenu, btnDeleteMenu;
    @FXML private TableView<Menu> tblMenu;
    @FXML private TableColumn<Menu, Integer> colMenuId;
    @FXML private TableColumn<Menu, String> colMenuName;
    @FXML private TableColumn<Menu, String> colMenuCategory;
    @FXML private TableColumn<Menu, String> colMenuFilter;
    @FXML private TableColumn<Menu, String> colMenuPrice;
    @FXML private TableColumn<Menu, Void> colMenuAction;

    @FXML private Label lblStatusBar;

    private final ObservableList<OrderRow> orders = FXCollections.observableArrayList();
    private final ObservableList<Menu> menuItems = FXCollections.observableArrayList();
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("id","ID"));
    private final MenuDAO menuDAO = new MenuDAO();

    @FXML
    public void initialize() {
        setupOrdersTable();
        populateStatusFilter();
        loadOrders();

        tblOrders.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) showOrderDetail(newV);
            else taOrderDetail.clear();
        });

        if (tfSearchOrders != null) tfSearchOrders.textProperty().addListener((obs, o, n) -> applyFilters());
        if (cbStatusFilter != null) cbStatusFilter.valueProperty().addListener((obs, o, n) -> applyFilters());

        if (btnTopRefresh != null) btnTopRefresh.setOnAction(e -> refreshAll());
        if (btnRefreshOrders != null) btnRefreshOrders.setOnAction(e -> loadOrders());

        setupMenuTable();
        loadMenus();

        if (tfSearchMenu != null) tfSearchMenu.textProperty().addListener((obs, o, n) -> applyMenuFilter());
        if (btnAddMenu != null) btnAddMenu.setOnAction(e -> onAddMenu());
        if (btnEditMenu != null) btnEditMenu.setOnAction(e -> onEditMenu());
        if (btnDeleteMenu != null) btnDeleteMenu.setOnAction(e -> onDeleteMenu());

        if (btnDashboard != null) btnDashboard.setOnAction(e -> showDashboard(e));
        if (btnOrders != null) btnOrders.setOnAction(e -> showOrders(e));
        if (btnMenu != null) btnMenu.setOnAction(e -> showMenu(e));
        if (btnReports != null) btnReports.setOnAction(e -> showReports(e));
        if (btnLogout != null) btnLogout.setOnAction(e -> onLogout(e));

        if (btnBackToWelcome != null) btnBackToWelcome.setOnAction(e -> onBackToWelcome(e));
    }

    private void setupOrdersTable() {
        colKode.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().kode));
        colCustomer.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().customerLabel));
        colTotal.setCellValueFactory(data -> new SimpleStringProperty(currency.format(data.getValue().total)));
        colStatus.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().status == null ? "" : data.getValue().status));
        colWaktu.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().waktu == null ? "" : data.getValue().waktu));

        colAction.setCellFactory(tc -> new TableCell<>() {
            private final Button btnView = new Button("Lihat");
            private final Button btnPrint = new Button("Cetak Resi");
            private final HBox box = new HBox(6, btnView, btnPrint);
            {
                btnView.setOnAction(e -> {
                    OrderRow or = getTableView().getItems().get(getIndex());
                    if (or != null) {
                        tblOrders.getSelectionModel().select(or);
                        showOrderDetail(or);
                    }
                });
                btnPrint.setOnAction(e -> {
                    OrderRow or = getTableView().getItems().get(getIndex());
                    printReceipt(or, btnPrint);
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    setGraphic(box);
                }
            }
        });

        tblOrders.setItems(orders);
    }

    private void populateStatusFilter() {
        if (cbStatusFilter == null) return;
        cbStatusFilter.getItems().clear();
        cbStatusFilter.getItems().addAll("Semua", "Draft", "Terkonfirmasi", "MenungguPembayaran", "Dibayar", "Diproses", "SiapAmbil", "Selesai", "Dibatalkan");
        cbStatusFilter.setValue("Semua");
    }

    private void loadOrders() {
        orders.clear();
        String q = "SELECT id, kode, total, catatan, status, waktu FROM pesanan ORDER BY waktu DESC";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(q);
             ResultSet rs = p.executeQuery()) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String kode = rs.getString("kode");
                long total = rs.getLong("total");
                String catatan = rs.getString("catatan");
                String status = rs.getString("status");
                String waktu = rs.getString("waktu");
                String customerLabel = parseCustomerLabel(catatan);
                orders.add(new OrderRow(id, kode, total, status, waktu, catatan, customerLabel));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal memuat daftar pesanan: " + ex.getMessage()).showAndWait();
        }
        applyFilters();
    }

    private String parseCustomerLabel(String catatan) {
        if (catatan == null) return "";

        Pattern pName = Pattern.compile("\\[customer_name=([^\\]]+)]");
        Matcher mName = pName.matcher(catatan);
        if (mName.find()) {
            String name = mName.group(1).trim();
            if (!name.isEmpty()) return name;
        }

        Pattern pId = Pattern.compile("\\[customer_id=(\\d+)]");
        Matcher mId = pId.matcher(catatan);
        if (mId.find()) {
            String idStr = mId.group(1);
            try {
                int id = Integer.parseInt(idStr);
                try (Connection c = DBUtil.getConnection();
                     PreparedStatement ps = c.prepareStatement("SELECT name FROM customer WHERE id = ?")) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            String name = rs.getString(1);
                            if (name != null && !name.isBlank()) return name;
                        }
                    }
                } catch (Exception ignore) {
                }
            } catch (NumberFormatException ignored) { }

            return "Customer #" + idStr;
        }

        return "";
    }

    private void showOrderDetail(OrderRow row) {
        if (row == null) { taOrderDetail.clear(); return; }
        StringBuilder sb = new StringBuilder();
        sb.append("Kode: ").append(row.kode).append("\n");
        sb.append("Status: ").append(row.status).append("\n");
        sb.append("Waktu: ").append(row.waktu).append("\n\n");
        sb.append("Items:\n");

        String qItems = "SELECT nama, harga, qty, subtotal FROM pesanan_item WHERE pesanan_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(qItems)) {
            p.setInt(1, row.id);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    String nama = rs.getString("nama");
                    long harga = rs.getLong("harga");
                    int qty = rs.getInt("qty");
                    long subtotal = rs.getLong("subtotal");
                    sb.append(String.format(" - %s x%d @ %s = %s\n", nama, qty, currency.format(harga), currency.format(subtotal)));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        sb.append("\nCatatan:\n").append(row.catatan == null ? "" : row.catatan).append("\n");
        taOrderDetail.setText(sb.toString());
    }

    @FXML private void onSetDiproses(ActionEvent e)  { changeSelectedOrderStatusWithConfirm("Diproses"); }
    @FXML private void onSetSiapAmbil(ActionEvent e) { changeSelectedOrderStatusWithConfirm("SiapAmbil"); }
    @FXML private void onSetSelesai(ActionEvent e)   { changeSelectedOrderStatusWithConfirm("Selesai"); }
    @FXML private void onCancelOrder(ActionEvent e)  {
        OrderRow sel = getSelectedOrder();
        if (sel == null) { warnSelect(); return; }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Batalkan pesanan " + sel.kode + " ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> r = conf.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.YES) changeSelectedOrderStatus(sel, "Dibatalkan");
    }

    private void changeSelectedOrderStatusWithConfirm(String newStatus) {
        OrderRow sel = getSelectedOrder();
        if (sel == null) { warnSelect(); return; }
        Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Ubah status pesanan " + sel.kode + " menjadi " + newStatus + " ?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> r = conf.showAndWait();
        if (r.isPresent() && r.get() == ButtonType.YES) changeSelectedOrderStatus(sel, newStatus);
    }

    private void changeSelectedOrderStatus(OrderRow sel, String newStatus) {
        String u = "UPDATE pesanan SET status = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(u)) {
            p.setString(1, newStatus);
            p.setInt(2, sel.id);
            int updated = p.executeUpdate();
            if (updated > 0) {
                sel.status = newStatus;
                tblOrders.refresh();
                lblStatusBar.setText("Status pesanan " + sel.kode + " diubah menjadi " + newStatus);
                showOrderDetail(sel);
            } else {
                new Alert(Alert.AlertType.ERROR, "Gagal memperbarui status.").showAndWait();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Error saat update status: " + ex.getMessage()).showAndWait();
        }
    }

    private OrderRow getSelectedOrder() { return tblOrders.getSelectionModel().getSelectedItem(); }
    private void warnSelect() { new Alert(Alert.AlertType.INFORMATION, "Pilih pesanan terlebih dahulu.").showAndWait(); }

    private void printReceipt(OrderRow row, Node owner) {
        if (row == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("====== BAKMI PENS ======\n");
        sb.append("Kode: ").append(row.kode).append("\n");
        sb.append("Waktu: ").append(row.waktu).append("\n");
        sb.append("Status: ").append(row.status).append("\n\n");
        sb.append("Items:\n");

        String qItems = "SELECT nama, harga, qty, subtotal FROM pesanan_item WHERE pesanan_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(qItems)) {
            p.setInt(1, row.id);
            try (ResultSet rs = p.executeQuery()) {
                while (rs.next()) {
                    String nama = rs.getString("nama");
                    long harga = rs.getLong("harga");
                    int qty = rs.getInt("qty");
                    long subtotal = rs.getLong("subtotal");
                    sb.append(String.format("%s x%d  %s\n", nama, qty, currency.format(subtotal)));
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        sb.append("\nTotal: ").append(currency.format(row.total)).append("\n");
        sb.append("\nCatatan:\n").append(row.catatan == null ? "" : row.catatan).append("\n");
        sb.append("\nTerima kasih\n");

        TextArea printable = new TextArea(sb.toString());
        printable.setWrapText(false);
        printable.setEditable(false);

        PrinterJob job = PrinterJob.createPrinterJob();
        if (job == null) {
            new Alert(Alert.AlertType.ERROR, "Tidak dapat membuat PrinterJob pada sistem ini.").showAndWait();
            return;
        }
        Window window = (owner == null || owner.getScene() == null) ? App.stage : owner.getScene().getWindow();
        boolean proceed = job.showPrintDialog(window);
        if (proceed) {
            boolean ok = job.printPage(printable);
            if (ok) job.endJob();
            else new Alert(Alert.AlertType.ERROR, "Gagal mencetak halaman.").showAndWait();
        } else {
            job.endJob();
        }
    }

    private void applyFilters() {
        String q = tfSearchOrders.getText() == null ? "" : tfSearchOrders.getText().trim().toLowerCase();
        String status = cbStatusFilter.getValue();
        tblOrders.setItems(orders.filtered(or -> {
            boolean m1 = q.isEmpty() ||
                    (or.kode != null && or.kode.toLowerCase().contains(q)) ||
                    (or.customerLabel != null && or.customerLabel.toLowerCase().contains(q)) ||
                    (or.catatan != null && or.catatan.toLowerCase().contains(q));
            boolean m2 = status == null || status.equalsIgnoreCase("Semua") || status.isBlank()
                    || (or.status != null && or.status.equalsIgnoreCase(status));
            return m1 && m2;
        }));
    }

    private void refreshAll() {
        loadOrders();
        loadMenus();
        if (lblStatusBar != null) lblStatusBar.setText("Refreshed");
    }

    private void setupMenuTable() {
        colMenuId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colMenuName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colMenuCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colMenuFilter.setCellValueFactory(new PropertyValueFactory<>("filter"));
        colMenuPrice.setCellValueFactory(cell -> new SimpleStringProperty(currency.format(cell.getValue().getPrice())));

        colMenuAction.setCellFactory(tc -> new TableCell<>() {
            private final Button btnEdit = new Button("Edit");
            private final Button btnDel = new Button("Hapus");
            private final HBox box = new HBox(6, btnEdit, btnDel);
            {
                btnEdit.setOnAction(e -> {
                    Menu m = getTableView().getItems().get(getIndex());
                    if (m != null) editMenuDialog(m);
                });
                btnDel.setOnAction(e -> {
                    Menu m = getTableView().getItems().get(getIndex());
                    if (m != null) {
                        Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Hapus menu \"" + m.getName() + "\" ?", ButtonType.YES, ButtonType.NO);
                        Optional<ButtonType> r = conf.showAndWait();
                        if (r.isPresent() && r.get() == ButtonType.YES) {
                            deleteMenu(m.getId());
                        }
                    }
                });
            }
            @Override protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) setGraphic(null);
                else setGraphic(box);
            }
        });

        tblMenu.setItems(menuItems);
    }

    private void loadMenus() {
        menuItems.clear();
        try {
            menuItems.addAll(menuDAO.getAll());
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal memuat menu: " + ex.getMessage()).showAndWait();
        }
    }

    private void applyMenuFilter() {
        String q = tfSearchMenu.getText() == null ? "" : tfSearchMenu.getText().trim().toLowerCase();
        if (q.isEmpty()) tblMenu.setItems(menuItems);
        else {
            tblMenu.setItems(menuItems.filtered(m -> (m.getName() != null && m.getName().toLowerCase().contains(q))
                    || (m.getCategory() != null && m.getCategory().toLowerCase().contains(q))
                    || (m.getFilter() != null && m.getFilter().toLowerCase().contains(q))));
        }
    }

    @FXML private void onAddMenu() { addMenuDialog(); }
    @FXML private void onEditMenu() { Menu sel = tblMenu.getSelectionModel().getSelectedItem(); if (sel!=null) editMenuDialog(sel); else new Alert(Alert.AlertType.INFORMATION, "Pilih menu terlebih dahulu.").showAndWait(); }
    @FXML private void onDeleteMenu() { Menu sel = tblMenu.getSelectionModel().getSelectedItem(); if (sel!=null) { Alert conf = new Alert(Alert.AlertType.CONFIRMATION, "Hapus menu \""+sel.getName()+"\" ?", ButtonType.YES, ButtonType.NO); Optional<ButtonType> r = conf.showAndWait(); if (r.isPresent() && r.get()==ButtonType.YES) deleteMenu(sel.getId()); } else new Alert(Alert.AlertType.INFORMATION, "Pilih menu terlebih dahulu.").showAndWait(); }

    private void addMenuDialog() {
        Dialog<Menu> dlg = new Dialog<>();
        dlg.setTitle("Tambah Menu");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        TextField tfName = new TextField();
        TextField tfDesc = new TextField();
        TextField tfCategory = new TextField();
        TextField tfFilter = new TextField();
        TextField tfPrice = new TextField();
        TextField tfImage = new TextField();

        g.addRow(0, new Label("Nama:"), tfName);
        g.addRow(1, new Label("Deskripsi:"), tfDesc);
        g.addRow(2, new Label("Kategori:"), tfCategory);
        g.addRow(3, new Label("Filter:"), tfFilter);
        g.addRow(4, new Label("Harga (angka):"), tfPrice);
        g.addRow(5, new Label("Image path:"), tfImage);

        dlg.getDialogPane().setContent(g);
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                long price = 0;
                try { price = Long.parseLong(tfPrice.getText().trim()); } catch (Exception ignored) {}
                return new Menu(0, tfName.getText(), tfDesc.getText(), tfCategory.getText(), tfFilter.getText(), price, tfImage.getText());
            }
            return null;
        });

        Optional<Menu> res = dlg.showAndWait();
        res.ifPresent(this::insertMenu);
    }

    private void editMenuDialog(Menu m) {
        Dialog<Menu> dlg = new Dialog<>();
        dlg.setTitle("Edit Menu");
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane g = new GridPane(); g.setHgap(8); g.setVgap(8);
        TextField tfName = new TextField(m.getName());
        TextField tfDesc = new TextField(m.getDescription());
        TextField tfCategory = new TextField(m.getCategory());
        TextField tfFilter = new TextField(m.getFilter());
        TextField tfPrice = new TextField(String.valueOf(m.getPrice()));
        TextField tfImage = new TextField(m.getImagePath() == null ? "" : m.getImagePath());

        g.addRow(0, new Label("Nama:"), tfName);
        g.addRow(1, new Label("Deskripsi:"), tfDesc);
        g.addRow(2, new Label("Kategori:"), tfCategory);
        g.addRow(3, new Label("Filter:"), tfFilter);
        g.addRow(4, new Label("Harga (angka):"), tfPrice);
        g.addRow(5, new Label("Image path:"), tfImage);

        dlg.getDialogPane().setContent(g);
        dlg.setResultConverter(btn -> {
            if (btn == ButtonType.OK) {
                long price = m.getPrice();
                try { price = Long.parseLong(tfPrice.getText().trim()); } catch (Exception ignored) {}
                return new Menu(m.getId(), tfName.getText(), tfDesc.getText(), tfCategory.getText(), tfFilter.getText(), price, tfImage.getText());
            }
            return null;
        });

        Optional<Menu> res = dlg.showAndWait();
        res.ifPresent(this::updateMenu);
    }

    private void insertMenu(Menu m) {
        String ins = "INSERT INTO menu(name, description, category, filter, price, image_path) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(ins)) {
            p.setString(1, m.getName());
            p.setString(2, m.getDescription());
            p.setString(3, m.getCategory());
            p.setString(4, m.getFilter());
            p.setLong(5, m.getPrice());
            p.setString(6, m.getImagePath());
            p.executeUpdate();
            loadMenus();
            if (lblStatusBar != null) lblStatusBar.setText("Menu ditambahkan.");
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal menambah menu: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML
    private void onRefreshOrders(ActionEvent event) {
        loadOrders();
    }

    private void updateMenu(Menu m) {
        String u = "UPDATE menu SET name=?, description=?, category=?, filter=?, price=?, image_path=? WHERE id=?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(u)) {
            p.setString(1, m.getName());
            p.setString(2, m.getDescription());
            p.setString(3, m.getCategory());
            p.setString(4, m.getFilter());
            p.setLong(5, m.getPrice());
            p.setString(6, m.getImagePath());
            p.setInt(7, m.getId());
            p.executeUpdate();
            loadMenus();
            if (lblStatusBar != null) lblStatusBar.setText("Menu diupdate.");
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal update menu: " + ex.getMessage()).showAndWait();
        }
    }

    private void deleteMenu(int id) {
        String d = "DELETE FROM menu WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement p = c.prepareStatement(d)) {
            p.setInt(1, id);
            p.executeUpdate();
            loadMenus();
            if (lblStatusBar != null) lblStatusBar.setText("Menu dihapus.");
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal menghapus menu: " + ex.getMessage()).showAndWait();
        }
    }

    @FXML private void onRefreshAll(ActionEvent e) { refreshAll(); }
    @FXML private void onClose(ActionEvent e) { try { App.stage.close(); } catch (Exception ex) { ex.printStackTrace(); } }

    @FXML private void showDashboard(ActionEvent e) { mainTabs.getSelectionModel().select(0); }
    @FXML private void showOrders(ActionEvent e)    { mainTabs.getSelectionModel().select(0); }
    @FXML private void showMenu(ActionEvent e)      { mainTabs.getSelectionModel().select(1); }
    @FXML private void showReports(ActionEvent e)   { mainTabs.getSelectionModel().select(2); }
    @FXML private void onLogout(ActionEvent e)      { App.stage.close(); }

    @FXML private void onBackToWelcome(ActionEvent e) {
        try {
            if (App.scene1 != null) App.stage.setScene(App.scene1);
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal kembali ke Welcome: " + ex.getMessage()).showAndWait();
        }
    }

    public static class OrderRow {
        final int id;
        final String kode;
        long total;
        String status;
        String waktu;
        String catatan;
        String customerLabel;
        public OrderRow(int id, String kode, long total, String status, String waktu, String catatan, String customerLabel) {
            this.id = id; this.kode = kode; this.total = total; this.status = waktu; this.waktu = waktu; this.catatan = catatan; this.customerLabel = customerLabel;
            this.status = status;
        }
    }
}