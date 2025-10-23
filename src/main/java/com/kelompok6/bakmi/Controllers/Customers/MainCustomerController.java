package com.kelompok6.bakmi.Controllers.Customers;

import com.kelompok6.bakmi.App;
import com.kelompok6.bakmi.Database.MenuDAO;
import com.kelompok6.bakmi.Database.PesananDAO;
import com.kelompok6.bakmi.Models.Menu;
import com.kelompok6.bakmi.Models.Pesanan;
import com.kelompok6.bakmi.Models.StatusPesanan;
import com.kelompok6.bakmi.Models.Customer;
import com.kelompok6.bakmi.Models.DineInCustomer;
import com.kelompok6.bakmi.Models.TakeAwayCustomer;
import com.kelompok6.bakmi.Session;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MainCustomerController {
    @FXML private TilePane menuGrid;
    @FXML private Label categoryTitle;
    @FXML private Label lblCartTotal;
    @FXML private Button btnSemua, btnFilter1, btnFilter2, btnFilter3, btnFilterLainnya;
    @FXML private StackPane orderPanel;
    @FXML private ImageView orderImage;
    @FXML private Label orderTitle, orderDesc, orderPrice, lblQty;
    @FXML private BorderPane blurLayer;

    @FXML private Button btnCheckout;

    @FXML private StackPane cartPanel;
    @FXML private VBox cartItemsContainer;
    @FXML private Label lblCartPanelTotal;
    @FXML private Button btnClearCart;
    @FXML private Button btnCheckoutPanel;
    @FXML private Button btnCloseCartPanel;

    @FXML private StackPane confirmPanel;
    @FXML private TextField tfCustomerName;
    @FXML private Label lblConfirmTotal;
    @FXML private Button btnConfirmOrder;
    @FXML private Button btnCancelConfirm;

    @FXML private StackPane tableSelectionBox;
    @FXML private ChoiceBox<Integer> cbTableNumbers;
    @FXML private Button btnTableOk;
    @FXML private Button btnTableCancel;

    private final MenuDAO menuDAO = new MenuDAO();
    private final PesananDAO pesananDAO = new PesananDAO();

    private final List<Menu> allMenus = new ArrayList<>();
    private final Map<Integer, Set<String>> menuTags = new HashMap<>();
    private final Map<String, List<Menu>> categoryMap = new HashMap<>();
    private MenuController menuController;
    private final List<CartItem> cart = new ArrayList<>();
    private Menu currentMenu;
    private int currentQty = 1;
    private final NumberFormat currency = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
    private String currentLeftCategory = "Promo";

    private Customer currentCustomer = null;

    @FXML
    private void initialize() {
        if (menuGrid != null) {
            menuGrid.setHgap(18);
            menuGrid.setVgap(18);
            menuGrid.setPadding(new Insets(8));
            menuGrid.setPrefColumns(4);
        }

        menuController = new MenuController(menuGrid, categoryTitle, this);

        loadMenusFromDb();
        buildTagsAndCategories();

        if (categoryMap.containsKey("Promo") && !categoryMap.get("Promo").isEmpty()) {
            currentLeftCategory = "Promo";
            renderCategory("Promo");
            updateTopFilterButtons("Promo");
            highlightTopButton(null);
        } else if (categoryMap.containsKey("Makanan") && !categoryMap.get("Makanan").isEmpty()) {
            currentLeftCategory = "Makanan";
            renderCategory("Makanan");
            updateTopFilterButtons("Makanan");
            highlightTopButton(btnSemua);
        } else {
            renderAll();
            updateTopFilterButtons(null);
            highlightTopButton(null);
        }

        if (btnClearCart != null) btnClearCart.setOnAction(ev -> {
            cart.clear();
            updateCartTotalDisplay();
            refreshCartPanel();
        });
        if (btnCloseCartPanel != null) btnCloseCartPanel.setOnAction(ev -> closeCartPanel());

        if (btnCheckoutPanel != null) btnCheckoutPanel.setOnAction(ev -> openConfirmPanel());
        if (btnConfirmOrder != null) btnConfirmOrder.setOnAction(ev -> onConfirmOrder(null));
        if (btnCancelConfirm != null) btnCancelConfirm.setOnAction(ev -> onCancelConfirm(null));

        if (btnCheckout != null) btnCheckout.setOnAction(ev -> onCheckout(ev));

        if (cbTableNumbers != null) {
            List<Integer> nums = new ArrayList<>();
            for (int i = 1; i <= 20; i++) nums.add(i);
            cbTableNumbers.getItems().setAll(nums);
            cbTableNumbers.getSelectionModel().selectFirst();
        }
        if (tableSelectionBox != null) {
            tableSelectionBox.setVisible(false);
            tableSelectionBox.setManaged(false);
        }

        if (btnTableOk != null) btnTableOk.setOnAction(this::onTableOk);
        if (btnTableCancel != null) btnTableCancel.setOnAction(this::onTableCancel);

        updateCartTotalDisplay();
    }

    private void loadMenusFromDb() {
        allMenus.clear();
        try { allMenus.addAll(menuDAO.getAll()); } catch (Exception ex) { ex.printStackTrace(); }
        LinkedHashMap<Integer, Menu> unique = new LinkedHashMap<>();
        for (Menu m : allMenus) if (m != null) unique.putIfAbsent(m.getId(), m);
        allMenus.clear();
        allMenus.addAll(unique.values());
        allMenus.sort(Comparator.comparingInt(Menu::getId));
    }

    private static String normalizeFilterRaw(String raw) {
        if (raw == null) return null;
        String s = raw.trim();
        if (s.isEmpty()) return null;
        String low = s.toLowerCase(Locale.ROOT).replaceAll("[_]+", " ").trim();

        if (low.matches("^lainnya\\b.*")) return "Lainnya";
        if (low.equals("dingin") || low.contains("iced") || low.contains("es ")) return "Dingin";
        if (low.equals("panas") || low.contains("hot")) return "Panas";
        if (low.contains("jus") || low.contains("juice")) return "Jus";

        if (low.contains("bakmi pedas") || low.contains("mercon") || low.contains("chili")) return "Bakmi Pedas";
        if (low.contains("bakmi") || low.contains("bakmi ayam") || low.contains("bakmi panggang") || low.contains("bakmi pangsit")) {
            if (low.contains("pedas")) return "Bakmi Pedas";
            return "Bakmi Original";
        }
        if (low.contains("nasi")) return "Nasi";
        if (low.contains("pangsit")) return "Pangsit";
        if (low.contains("dimsum")) return "Dimsum";
        if (low.contains("bakso")) return "Bakso";

        String[] parts = s.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].toLowerCase(Locale.ROOT);
            if (p.isEmpty()) continue;
            sb.append(Character.toUpperCase(p.charAt(0))).append(p.substring(1));
            if (i < parts.length - 1) sb.append(' ');
        }
        return sb.toString();
    }

    private void buildTagsAndCategories() {
        menuTags.clear();
        categoryMap.clear();
        categoryMap.put("Promo", new ArrayList<>());
        categoryMap.put("Makanan", new ArrayList<>());
        categoryMap.put("Minuman", new ArrayList<>());
        categoryMap.put("Camilan", new ArrayList<>());
        for (Menu m : allMenus) {
            if (m == null) continue;
            String rawCategory = m.getCategory() == null ? "" : m.getCategory().trim();
            String category = rawCategory;
            String nf = normalizeFilterRaw(m.getFilter());
            Set<String> tags = new HashSet<>();
            if (!category.isBlank()) tags.add(category);
            if (nf != null && !nf.isBlank()) tags.add(nf);
            menuTags.put(m.getId(), tags);

            if ("Promo".equalsIgnoreCase(category)) { categoryMap.get("Promo").add(m); continue; }
            if ("Makanan".equalsIgnoreCase(category)) { categoryMap.get("Makanan").add(m); continue; }
            if ("Minuman".equalsIgnoreCase(category)) { categoryMap.get("Minuman").add(m); continue; }
            if ("Camilan".equalsIgnoreCase(category)) { categoryMap.get("Camilan").add(m); continue; }

            if (nf != null) {
                switch (nf) {
                    case "Bakmi Original", "Bakmi Pedas", "Nasi" -> categoryMap.get("Makanan").add(m);
                    case "Dingin", "Panas", "Jus" -> categoryMap.get("Minuman").add(m);
                    case "Pangsit", "Dimsum", "Bakso" -> categoryMap.get("Camilan").add(m);
                    case "Lainnya" -> categoryMap.get("Makanan").add(m);
                    default -> categoryMap.get("Makanan").add(m);
                }
            } else {
                String text = (m.getName() + " " + (m.getDescription() == null ? "" : m.getDescription())).toLowerCase();
                if (text.contains("es ") || text.contains("iced") || text.contains("jus") || text.contains("kopi") || text.contains("teh"))
                    categoryMap.get("Minuman").add(m);
                else if (text.contains("pangsit") || text.contains("dimsum") || text.contains("bakso") || text.contains("lumpia"))
                    categoryMap.get("Camilan").add(m);
                else
                    categoryMap.get("Makanan").add(m);
            }
        }
        for (Map.Entry<String, List<Menu>> e : categoryMap.entrySet()) {
            e.getValue().sort(Comparator.comparingInt(allMenus::indexOf));
        }
    }

    private void renderCategory(String category) {
        List<Menu> list = categoryMap.getOrDefault(category, List.of());
        if ("Camilan".equalsIgnoreCase(category)) {
            if (menuGrid != null) menuGrid.setPrefColumns(3);
        } else {
            if (menuGrid != null) menuGrid.setPrefColumns(4);
        }
        menuController.render(list);
        categoryTitle.setText(category);
        currentLeftCategory = category;
    }

    private void renderAll() {
        if (menuGrid != null) menuGrid.setPrefColumns(4);
        menuController.render(allMenus);
        categoryTitle.setText("Semua");
        currentLeftCategory = "Semua";
    }

    @FXML private void showPromo(ActionEvent e)   { renderCategory("Promo"); updateTopFilterButtons("Promo"); highlightTopButton(null); }
    @FXML private void showMakanan(ActionEvent e) { renderCategory("Makanan"); updateTopFilterButtons("Makanan"); highlightTopButton(btnSemua); }
    @FXML private void showMinuman(ActionEvent e) { renderCategory("Minuman"); updateTopFilterButtons("Minuman"); highlightTopButton(btnSemua); }
    @FXML private void showCamilan(ActionEvent e) { renderCategory("Camilan"); updateTopFilterButtons("Camilan"); highlightTopButton(btnSemua); }

    @FXML private void btnBackClick(ActionEvent e) { try { App.stage.setScene(App.scene1); } catch (Exception ex) { ex.printStackTrace(); } }

    private void updateTopFilterButtons(String leftCategory) {
        currentLeftCategory = leftCategory == null ? currentLeftCategory : leftCategory;
        boolean isPromo = "Promo".equalsIgnoreCase(currentLeftCategory);
        if (isPromo) {
            Arrays.asList(btnSemua, btnFilter1, btnFilter2, btnFilter3, btnFilterLainnya)
                    .forEach(b -> { if (b != null) { b.setVisible(false); b.setManaged(false); }});
            return;
        }
        if (btnSemua != null) { btnSemua.setText("Semua"); btnSemua.setVisible(true); btnSemua.setManaged(true); }
        switch (currentLeftCategory) {
            case "Makanan" -> {
                setTopButton(btnFilter1, "Bakmi Original", true);
                setTopButton(btnFilter2, "Bakmi Pedas", true);
                setTopButton(btnFilter3, "Nasi", true);
                setTopButton(btnFilterLainnya, "Lainnya", true);
            }
            case "Minuman" -> {
                setTopButton(btnFilter1, "Dingin", true);
                setTopButton(btnFilter2, "Panas", true);
                setTopButton(btnFilter3, "Jus", true);
                setTopButton(btnFilterLainnya, "Lainnya", true);
            }
            case "Camilan" -> {
                setTopButton(btnFilter1, "Pangsit", true);
                setTopButton(btnFilter2, "Dimsum", true);
                setTopButton(btnFilter3, "Bakso", true);
                setTopButton(btnFilterLainnya, "Lainnya", true);
            }
            default -> {
                setTopButton(btnFilter1, "", false);
                setTopButton(btnFilter2, "", false);
                setTopButton(btnFilter3, "", false);
                setTopButton(btnFilterLainnya, "", false);
            }
        }
    }

    private void setTopButton(Button btn, String text, boolean visible) {
        if (btn == null) return;
        if (!visible || text == null || text.isBlank()) {
            btn.setVisible(false);
            btn.setManaged(false);
            btn.setUserData(null);
        } else {
            btn.setText(text);
            btn.setVisible(true);
            btn.setManaged(true);
            btn.setUserData(normalizeFilterRaw(text));
        }
    }

    @FXML private void onFilterSemua(ActionEvent e) { if ("Semua".equalsIgnoreCase(currentLeftCategory)) renderAll(); else renderCategory(currentLeftCategory); highlightTopButton(btnSemua); }
    @FXML private void onFilter1(ActionEvent e) { execTopFilter(btnFilter1); }
    @FXML private void onFilter2(ActionEvent e) { execTopFilter(btnFilter2); }
    @FXML private void onFilter3(ActionEvent e) { execTopFilter(btnFilter3); }
    @FXML private void onFilterLainnya(ActionEvent e) { execTopFilter(btnFilterLainnya); }

    private void execTopFilter(Button b) {
        if (b == null || !b.isVisible()) return;
        Object ud = b.getUserData();
        String rawTag = (ud instanceof String) ? (String) ud : b.getText();
        filterByTagWithinCategory(rawTag, currentLeftCategory);
        highlightTopButton(b);
    }

    private void filterByTagWithinCategory(String tag, String leftCategory) {
        if (tag == null || tag.isBlank()) { renderCategory(leftCategory); return; }
        if ("Semua".equalsIgnoreCase(tag)) { renderCategory(leftCategory); highlightTopButton(btnSemua); return; }
        String requested = normalizeFilterRaw(tag);
        List<Menu> scope = categoryMap.getOrDefault(leftCategory, List.of());
        List<Menu> matched = new ArrayList<>();
        for (Menu m : scope) {
            if (m == null) continue;
            String nf = normalizeFilterRaw(m.getFilter());
            if (nf != null && requested != null && nf.equalsIgnoreCase(requested)) { matched.add(m); continue; }
            Set<String> tags = menuTags.getOrDefault(m.getId(), Set.of());
            for (String t : tags) {
                String nt = t == null ? null : normalizeFilterRaw(t);
                if (nt != null && nt.equalsIgnoreCase(requested)) { matched.add(m); break; }
            }
        }
        if ("Camilan".equalsIgnoreCase(leftCategory) && menuGrid != null) menuGrid.setPrefColumns(3);
        menuController.render(matched);
        categoryTitle.setText(requested == null ? tag : requested);
    }

    private void highlightTopButton(Button active) {
        List<Button> topButtons = Arrays.asList(btnSemua, btnFilter1, btnFilter2, btnFilter3, btnFilterLainnya);
        for (Button b : topButtons) if (b != null) b.getStyleClass().remove("top-active");
        if (active != null) active.getStyleClass().add("top-active");
    }

    public void openOrderPanel(Menu m) {
        this.currentMenu = m;
        this.currentQty = 1;
        if (orderTitle != null) orderTitle.setText(m.getName());
        if (orderDesc != null) orderDesc.setText(m.getDescription() == null ? "" : m.getDescription());
        if (orderPrice != null) orderPrice.setText(formatPrice(m.getPrice()));
        if (lblQty != null) lblQty.setText(String.valueOf(currentQty));
        if (orderImage != null) {
            try {
                InputStream s = getClass().getResourceAsStream(m.getImagePath());
                if (s != null) orderImage.setImage(new Image(s));
                else orderImage.setImage(null);
            } catch (Exception ex) { orderImage.setImage(null); }
        }
        if (blurLayer != null) blurLayer.setEffect(new BoxBlur(12,12,3));
        if (orderPanel != null) { orderPanel.setVisible(true); orderPanel.setManaged(true); orderPanel.toFront(); }
    }

    @FXML private void increaseQty() { currentQty++; if (lblQty != null) lblQty.setText(String.valueOf(currentQty)); }
    @FXML private void decreaseQty() { if (currentQty>1) currentQty--; if (lblQty != null) lblQty.setText(String.valueOf(currentQty)); }
    @FXML private void closeOrderPanel() { if (blurLayer!=null) blurLayer.setEffect(null); if (orderPanel!=null) {orderPanel.setVisible(false); orderPanel.setManaged(false);} }

    @FXML
    private void confirmAddToCart() {
        if (currentMenu == null) { closeOrderPanel(); return; }

        Optional<CartItem> exists = cart.stream()
                .filter(c -> c.menu.getId() == currentMenu.getId())
                .findFirst();

        if (exists.isPresent()) {
            exists.get().qty += currentQty;
        } else {
            cart.add(new CartItem(currentMenu, currentQty, ""));
        }

        closeOrderPanel();
        updateCartTotalDisplay();
        new Alert(Alert.AlertType.INFORMATION, currentMenu.getName() + " x" + currentQty + " ditambahkan. Total: " + formatPrice(getCartTotal())).showAndWait();
    }

    private long getCartTotal() { return cart.stream().mapToLong(ci -> ci.menu.getPrice() * ci.qty).sum(); }
    private void updateCartTotalDisplay() {
        if (lblCartTotal!=null) lblCartTotal.setText(formatPrice(getCartTotal()));
        if (lblCartPanelTotal!=null) lblCartPanelTotal.setText(formatPrice(getCartTotal()));
        if (lblConfirmTotal!=null) lblConfirmTotal.setText(formatPrice(getCartTotal()));
    }

    @FXML private void showCartWindow(ActionEvent e) { openCartPanel(); }

    public void setCurrentCustomer(Customer customer) {
        this.currentCustomer = customer;
    }

    @FXML
    private void openCartPanel() {
        currentCustomer = Session.getCurrentCustomer();

        if (blurLayer != null) blurLayer.setEffect(new BoxBlur(12,12,3));
        refreshCartPanel();
        if (cartPanel != null) { cartPanel.setVisible(true); cartPanel.setManaged(true); cartPanel.toFront(); }
        else {
            Stage d = new Stage();
            d.initModality(Modality.APPLICATION_MODAL);
            d.setTitle("Keranjang (fallback)");
            VBox r = new VBox(8);
            r.setPadding(new Insets(12));
            if (cart.isEmpty()) r.getChildren().add(new Label("Keranjang kosong"));
            else {
                for (CartItem ci : cart) r.getChildren().add(new Label(ci.menu.getName() + " x" + ci.qty));
            }
            Button close = new Button("Tutup");
            close.setOnAction(ev -> d.close());
            r.getChildren().add(close);
            d.setScene(new Scene(r));
            d.showAndWait();
        }
    }

    private void closeCartPanel() {
        if (blurLayer != null) blurLayer.setEffect(null);
        if (cartPanel != null) { cartPanel.setVisible(false); cartPanel.setManaged(false); }
    }

    @FXML
    private void openConfirmPanel() {
        currentCustomer = Session.getCurrentCustomer();

        if (cart.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Keranjang kosong").showAndWait();
            return;
        }
        if (lblConfirmTotal != null) lblConfirmTotal.setText(formatPrice(getCartTotal()));
        if (tfCustomerName != null) {
            String prefName = getCustomerNameSafe();
            tfCustomerName.setText(prefName == null ? "" : prefName);
        }

        if (blurLayer != null) blurLayer.setEffect(new BoxBlur(12,12,3));
        if (confirmPanel != null) { confirmPanel.setVisible(true); confirmPanel.setManaged(true); confirmPanel.toFront(); }

        if (currentCustomer instanceof DineInCustomer) {
            DineInCustomer dine = (DineInCustomer) currentCustomer;
            boolean needsTable = (dine.getNomorMeja() <= 0);
            if (tableSelectionBox != null && cbTableNumbers != null) {
                tableSelectionBox.setVisible(needsTable);
                tableSelectionBox.setManaged(needsTable);
                if (needsTable) {
                    if (cbTableNumbers.getItems().isEmpty()) {
                        for (int i = 1; i <= 20; i++) cbTableNumbers.getItems().add(i);
                    }
                    if (dine.getNomorMeja() > 0) {
                        int idx = Math.max(0, Math.min(cbTableNumbers.getItems().size()-1, dine.getNomorMeja()-1));
                        cbTableNumbers.getSelectionModel().select(idx);
                    } else {
                        cbTableNumbers.getSelectionModel().selectFirst();
                    }
                    tableSelectionBox.toFront();
                    tableSelectionBox.requestFocus();
                }
            }
        } else {
            if (tableSelectionBox != null) {
                tableSelectionBox.setVisible(false);
                tableSelectionBox.setManaged(false);
            }
        }
    }

    @FXML
    private void onCancelConfirm(ActionEvent event) {
        closeConfirmPanel();
    }

    private void closeConfirmPanel() {
        if (blurLayer != null) blurLayer.setEffect(null);
        if (confirmPanel != null) { confirmPanel.setVisible(false); confirmPanel.setManaged(false); }
        if (tableSelectionBox != null) { tableSelectionBox.setVisible(false); tableSelectionBox.setManaged(false); }
    }

    @FXML
    private void onTableOk(ActionEvent event) {
        try {
            Integer chosen = cbTableNumbers == null ? null : cbTableNumbers.getValue();
            if (chosen == null) {
                new Alert(Alert.AlertType.INFORMATION, "Silakan pilih nomor meja terlebih dahulu.").showAndWait();
                return;
            }
            currentCustomer = Session.getCurrentCustomer();
            if (currentCustomer instanceof DineInCustomer) {
                DineInCustomer dine = (DineInCustomer) currentCustomer;
                dine.setNomorMeja(chosen);
                Session.setCurrentCustomer(dine);
                new Alert(Alert.AlertType.INFORMATION, "Nomor meja " + chosen + " dipilih. Lanjutkan konfirmasi untuk menyimpan pesanan.").showAndWait();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal memilih meja: " + ex.getMessage()).showAndWait();
        } finally {
            if (tableSelectionBox != null) {
                tableSelectionBox.setVisible(false);
                tableSelectionBox.setManaged(false);
            }
        }
    }

    @FXML
    private void onTableCancel(ActionEvent event) {
        if (tableSelectionBox != null) {
            tableSelectionBox.setVisible(false);
            tableSelectionBox.setManaged(false);
        }
    }

    @FXML
    private void onConfirmOrder(ActionEvent event) {
        currentCustomer = Session.getCurrentCustomer();

        String customerName = tfCustomerName == null ? "" : tfCustomerName.getText().trim();

        if (currentCustomer == null) {
            Customer fallback = new Customer();
            fallback.setNamaCustomer(customerName == null ? "" : customerName);
            currentCustomer = fallback;
        } else {
            try {
                if (customerName != null && !customerName.isBlank()) {
                    Method m = null;
                    try { m = currentCustomer.getClass().getMethod("setNamaCustomer", String.class); }
                    catch (NoSuchMethodException ignore) { }
                    try { if (m == null) m = currentCustomer.getClass().getMethod("setNama", String.class); } catch (NoSuchMethodException ignore) {}
                    try { if (m == null) m = currentCustomer.getClass().getMethod("setName", String.class); } catch (NoSuchMethodException ignore) {}
                    if (m != null) m.invoke(currentCustomer, customerName.trim());
                }
            } catch (Exception ex) {
            }
        }

        if (currentCustomer instanceof DineInCustomer) {
            DineInCustomer dine = (DineInCustomer) currentCustomer;
            if (dine.getNomorMeja() <= 0) {
                Integer chosenTable = null;
                if (tableSelectionBox != null && tableSelectionBox.isVisible() && cbTableNumbers != null) {
                    chosenTable = cbTableNumbers.getValue();
                } else {
                    List<Integer> mejaList = new ArrayList<>();
                    for (int i = 1; i <= 20; i++) mejaList.add(i);
                    ChoiceDialog<Integer> choice = new ChoiceDialog<>(1, mejaList);
                    choice.setTitle("Pilih Nomor Meja");
                    choice.setHeaderText("Silakan pilih nomor meja yang tersedia");
                    choice.setContentText("Nomor meja:");
                    Optional<Integer> opt = choice.showAndWait();
                    if (opt.isPresent()) chosenTable = opt.get();
                }

                if (chosenTable == null) {
                    new Alert(Alert.AlertType.INFORMATION, "Checkout dibatalkan — silakan pilih nomor meja untuk makan di sini.").showAndWait();
                    return;
                }

                dine.setNomorMeja(chosenTable);
                Session.setCurrentCustomer(dine);
                currentCustomer = dine;
            }
        }

        StringBuilder sbNotes = new StringBuilder();
        if (!customerName.isBlank()) sbNotes.append("[customer_name=").append(customerName).append("]\n");
        for (CartItem ci : cart) {
            if (ci.note != null && !ci.note.isBlank()) {
                sbNotes.append(ci.menu.getName()).append(": ").append(ci.note).append("\n");
            }
        }
        String finalCatatan = sbNotes.toString();

        boolean saved = checkoutAndSave(currentCustomer, finalCatatan, null);

        if (!saved) {
            return;
        }

        try {
            LocalDateTime readyAt = null;
            if (currentCustomer instanceof TakeAwayCustomer) {
                TakeAwayCustomer tak = (TakeAwayCustomer) currentCustomer;
                readyAt = tak.getWaktuAmbil();
                if (readyAt == null) readyAt = LocalDateTime.now().plusMinutes(20);
            } else {
                readyAt = LocalDateTime.now().plusMinutes(20);
            }
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
            String timeText = readyAt.format(fmt);
            String message = "Terima kasih. Pesanan akan siap sekitar pukul " + timeText + " (±20 menit).";
            new Alert(Alert.AlertType.INFORMATION, message).showAndWait();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.INFORMATION, "Terima kasih. Pesanan akan siap dalam 20 menit.").showAndWait();
        }

        closeConfirmPanel();
        closeCartPanel();

        try {
            if (App.scene1 != null) {
                App.stage.setScene(App.scene1);
            } else {
                try {
                    javafx.scene.Parent root = javafx.fxml.FXMLLoader.load(getClass().getResource("/Fxml/Welcome/Welcome.fxml"));
                    Scene s = new Scene(root);
                    App.stage.setScene(s);
                } catch (Exception ex) {
                    System.err.println("[MainCustomerController] Tidak dapat kembali ke Welcome: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void refreshCartPanel() {
        if (cartItemsContainer == null) return;
        cartItemsContainer.getChildren().clear();

        if (cart.isEmpty()) {
            Label empty = new Label("Keranjang kosong");
            empty.setPadding(new Insets(12));
            cartItemsContainer.getChildren().add(empty);
            updateCartTotalDisplay();
            return;
        }

        for (CartItem ci : new ArrayList<>(cart)) {
            HBox row = new HBox(10);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPadding(new Insets(8));

            ImageView iv = new ImageView();
            iv.setFitWidth(64);
            iv.setFitHeight(64);
            try {
                InputStream s = getClass().getResourceAsStream(ci.menu.getImagePath());
                if (s != null) iv.setImage(new Image(s));
            } catch (Exception ignored) {}

            VBox det = new VBox(4);
            Label name = new Label(ci.menu.getName());
            name.setStyle("-fx-font-weight:bold");
            Label qtyPrice = new Label(ci.qty + " x " + formatPrice(ci.menu.getPrice()) + " = " + formatPrice(ci.menu.getPrice()*ci.qty));
            det.getChildren().addAll(name, qtyPrice);

            Label noteLabel = new Label();
            noteLabel.getStyleClass().add("muted");
            if (ci.note != null && !ci.note.isBlank()) {
                noteLabel.setText("Catatan: " + ci.note);
                noteLabel.setWrapText(true);
                det.getChildren().add(noteLabel);
            }

            Button editNote = new Button(ci.note == null || ci.note.isBlank() ? "Tambah Catatan" : "Edit Catatan");
            editNote.setOnAction(ev -> {
                TextInputDialog d = new TextInputDialog(ci.note == null ? "" : ci.note);
                d.setTitle("Catatan untuk " + ci.menu.getName());
                d.setHeaderText("Tambah/Edit catatan untuk item ini (opsional)");
                d.setContentText("Catatan:");
                Optional<String> rr = d.showAndWait();
                rr.ifPresent(txt -> {
                    ci.note = txt == null ? "" : txt.trim();
                    refreshCartPanel();
                    updateCartTotalDisplay();
                });
            });

            Button remove = new Button("Hapus");
            remove.setOnAction(ev -> {
                cart.remove(ci);
                refreshCartPanel();
                updateCartTotalDisplay();
            });

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            VBox rightBox = new VBox(6, editNote, remove);
            rightBox.setAlignment(Pos.CENTER_RIGHT);

            row.getChildren().addAll(iv, det, spacer, rightBox);
            cartItemsContainer.getChildren().add(row);
        }

        updateCartTotalDisplay();
    }

    private boolean checkoutAndSave(Customer customer, String catatan, Stage dialogToClose) {
        if (cart.isEmpty()) {
            new Alert(Alert.AlertType.INFORMATION, "Keranjang kosong").showAndWait();
            return false;
        }
        Pesanan p = new Pesanan();
        p.setCatatan(catatan == null ? "" : catatan);
        for (CartItem ci : new ArrayList<>(cart)) p.tambahItem(ci.menu, ci.qty);
        p.hitungTotal();
        p.setStatusPesanan(StatusPesanan.MenungguPembayaran);

        Integer customerId = customer != null ? customer.getIdCustomer() : null;
        String layanan = customer != null && customer.getTipeLayanan() != null ? customer.getTipeLayanan().name() : null;
        Integer nomorMeja = (customer instanceof DineInCustomer) ? ((DineInCustomer) customer).getNomorMeja() : null;
        String waktuAmbil = null;
        if (customer instanceof TakeAwayCustomer) {
            var w = ((TakeAwayCustomer) customer).getWaktuAmbil();
            if (w != null) waktuAmbil = w.toString();
            else {
                ((TakeAwayCustomer) customer).aturWaktuAmbil(LocalDateTime.now().plusMinutes(20));
                waktuAmbil = ((TakeAwayCustomer) customer).getWaktuAmbil().toString();
            }
        }

        try {
            int orderId = pesananDAO.save(p, customerId, layanan, nomorMeja, waktuAmbil);
            new Alert(Alert.AlertType.INFORMATION, "Pesanan berhasil dibuat.\nKode: " + p.getKodePesanan() + "\nTotal: " + formatPrice(p.getTotalHarga())).showAndWait();
            cart.clear();
            updateCartTotalDisplay();
            refreshCartPanel();
            if (dialogToClose != null) dialogToClose.close();
            System.out.println("New order id=" + orderId + " kode=" + p.getKodePesanan() + " total=" + p.getTotalHarga());
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Gagal menyimpan pesanan: " + ex.getMessage()).showAndWait();
            return false;
        }
    }

    @FXML private void onCheckout(ActionEvent e) { openCartPanel(); }

    private String formatPrice(long price) { try { return currency.format(price); } catch (Exception ex) { return "Rp " + price; } }

    private String getCustomerNameSafe() {
        currentCustomer = Session.getCurrentCustomer();
        if (currentCustomer == null) return "";
        String[] tryNames = {"getNamaCustomer", "getNama", "getName"};
        for (String mName : tryNames) {
            try {
                Method m = currentCustomer.getClass().getMethod(mName);
                Object val = m.invoke(currentCustomer);
                if (val != null) return String.valueOf(val);
            } catch (NoSuchMethodException ignored) {
            } catch (Exception ex) {
            }
        }
        return "";
    }

    private static class CartItem {
        final Menu menu;
        int qty;
        String note;
        CartItem(Menu menu, int qty, String note) {
            this.menu = menu;
            this.qty = qty;
            this.note = note == null ? "" : note;
        }
    }
}