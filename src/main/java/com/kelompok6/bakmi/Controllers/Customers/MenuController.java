package com.kelompok6.bakmi.Controllers.Customers;

import com.kelompok6.bakmi.Models.Menu;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.InputStream;
import java.util.List;

public class MenuController {

    private final TilePane menuGrid;
    private final javafx.scene.control.Label categoryTitle;
    private final MainCustomerController parent;

    public MenuController(TilePane menuGrid, javafx.scene.control.Label categoryTitle, MainCustomerController parent) {
        this.menuGrid = menuGrid;
        this.categoryTitle = categoryTitle;
        this.parent = parent;
    }

    public void render(List<Menu> list) {
        menuGrid.getChildren().clear();
        for (Menu m : list) menuGrid.getChildren().add(createCard(m));
    }

    private VBox createCard(Menu m) {
        VBox card = new VBox(8);
        card.getStyleClass().add("menu-card");
        card.setAlignment(Pos.CENTER);
        card.setId("menu-" + m.getId());

        ImageView iv = new ImageView();
        iv.setPreserveRatio(true);
        iv.setFitWidth(160);
        iv.setFitHeight(140);
        try {
            InputStream s = getClass().getResourceAsStream(m.getImagePath());
            if (s != null) iv.setImage(new Image(s));
        } catch (Exception ignored) {}

        Text name = new Text(m.getName());
        name.getStyleClass().add("menu-name");

        Text price = new Text(String.format("Rp %,d", m.getPrice()).replace(',', '.'));
        price.getStyleClass().add("menu-price");

        card.getChildren().addAll(iv, name, price);

        card.addEventHandler(MouseEvent.MOUSE_CLICKED, ev -> parent.openOrderPanel(m));

        return card;
    }
}