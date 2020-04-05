package view;

import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.TreeItemPropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.CopyBuilder;
import model.Etat;
import model.Fichier;
import vm.VM;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;

/**
 * @author momo
 */
public class View extends VBox {

    //    private final MyTreeTableView left;
//    private final MyTreeTableView right;
    private HBox hBoxCenter = new HBox();
    private Image imageButtonChoose = new Image("Images/flat_folder.png");
    private HBox hBoxBot = hBoxBot();
    private HBox hBoxFilter = new HBox();
    private VBox vbox = new VBox();
    private VM vm;
    private MyButtonFilters myButtonFilters;
    private HBox hBoxButtonFolder = new HBox();
    private Button buttonFolderL = new Button("L");
    private Button buttonFolderR = new Button("R");
    private DirectoryChooser dirChooser = new DirectoryChooser();
    private Stage primaryStage;
    private TreeTableView<Fichier> tTVLeft = new TreeTableView<>();
    private TreeTableView<Fichier> tTVRight = new TreeTableView<>();
    private Label labelL = new Label();
    private Label labelR = new Label();


    public View(Stage primaryStage, VM vm) {
        this.vm = vm;
        new EditView(primaryStage, vm.getEditVM());
        myButtonFilters = new MyButtonFilters(vm, this);
        setBindingAndListeners(vm);
        configColumns(tTVLeft);
        configColumns(tTVRight);
        dirChooserButtons(buttonFolderR, vm);
        dirChooserButtons(buttonFolderL, vm);
        setupScene(primaryStage);
    }

    private void dirChooserButtons(Button button, VM vm) {
        button.setOnAction(event -> {
            File dir = dirChooser.showDialog(primaryStage);
            if (dir != null) {
                if (button.getText() == "R") {
                    try {
                        labelR.setText(dir.getAbsolutePath());
                        Fichier newFichierR = new CopyBuilder().build(Paths.get(dir.getAbsolutePath()));
                        vm.setNewDirRight(newFichierR);
                        vm.setNewDirLeft(vm.getTiLeft().getValue());
                        vm.setRoot();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        labelL.setText(dir.getAbsolutePath());
                        Fichier newFichierL = new CopyBuilder().build(Paths.get(dir.getAbsolutePath()));
                        vm.setNewDirLeft(newFichierL);
                        vm.setNewDirRight(vm.getTiRight().getValue());
                        vm.setRoot();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                vm.setRoot();
            }


        });
    }

    private void setBindingAndListeners(VM vm) {
        tTVLeft.rootProperty().bind(vm.rootPropertyLeft());
        tTVRight.rootProperty().bind(vm.rootPropertyRight());
        vm.selectedTreeLeft().bind(tTVLeft.getSelectionModel().selectedItemProperty());
        vm.selectedTreeRight().bind(tTVRight.getSelectionModel().selectedItemProperty());

        tTVLeft.setOnMousePressed(e -> {
            if (e.getClickCount() == 2) {
                vm.openSelectedFileLeft();
            }
        });
        tTVRight.setOnMousePressed(e -> {
            if (e.getClickCount() == 2) {
                vm.openSelectedFileRight();
            }
        });
    }

    private void configColumns(TreeTableView<Fichier> tTV) {
        tTV.setShowRoot(false);
        setPrefWidth(8000);
        TreeTableColumn<Fichier, String> nameCol = new TreeTableColumn<>("Nom");
        TreeTableColumn<Fichier, Fichier> typeCol = new TreeTableColumn<>("Type");
        TreeTableColumn<Fichier, Long> sizeCol = new TreeTableColumn<>("Size");
        TreeTableColumn<Fichier, LocalDateTime> dateCol = new TreeTableColumn<>("Date modif");

        nameCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("name"));
        typeCol.setCellValueFactory(r -> new SimpleObjectProperty<>(r.getValue().getValue()));
        dateCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("dateTime"));
        sizeCol.setCellValueFactory(new TreeItemPropertyValueFactory<>("size"));

        nameCol.setPrefWidth(300);

        nameCol.setCellFactory((param) -> {
            return new NomFichierCell();
        });
        typeCol.setCellFactory((param) -> new TypeFichierCell());

        dateCol.setCellFactory((param) -> {
            return new DateModifFichierCell();
        });
        sizeCol.setCellFactory((param) -> {
            return new SizeFichierCell();
        });
        tTV.getColumns().setAll(nameCol, typeCol, dateCol, sizeCol);
    }

    private void setupScene(Stage primaryStage) {
        hBoxCenter.getChildren().addAll(tTVLeft, tTVRight);
        labelL.setText(tTVLeft.getRoot().getValue().getPath().toString());
        labelR.setText(tTVRight.getRoot().getValue().getPath().toString());
        buttonFolderL.setGraphic(new ImageView(imageButtonChoose));
        buttonFolderR.setGraphic(new ImageView(imageButtonChoose));

        hBoxButtonFolder.getChildren().addAll(labelL, buttonFolderL, buttonFolderR, labelR);
        hBoxButtonFolder.setAlignment(Pos.CENTER);
        hBoxButtonFolder.setSpacing(30);
        myButtonFilters = new MyButtonFilters(vm, this);
        hBoxFilter.getChildren().addAll(myButtonFilters);
        hBoxFilter.setAlignment(Pos.CENTER);
        hBoxFilter.setSpacing(100);
        hBoxFilter.setPadding(new Insets(15, 20, 10, 10));
        etatValues(hBoxBot);

        vbox.getChildren().addAll(hBoxFilter, hBoxButtonFolder, hBoxCenter, hBoxBot);


        Scene scene = new Scene(vbox, 1100, 500);
        scene.getStylesheets().add("model/cssView.css");
        primaryStage.setTitle("Beyond Compare");
        primaryStage.getIcons().add(new Image("Images/syncFilesIcon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void etatValues(HBox hBoxBot) {
        for (Etat e : Etat.values()) {
            if (e != Etat.UNDEFINED) {
                Label l = new Label(e.toString());
                l.getStyleClass().add(e.toString());
                hBoxBot.getChildren().add(l);
            }
        }
    }

    private HBox hBoxBot() {
        HBox hBoxBot = new HBox();
        hBoxBot.getStylesheets().add("model/cssView.css");
        hBoxBot.setAlignment(Pos.TOP_CENTER);
        hBoxBot.setSpacing(30);
        return hBoxBot;
    }
}
