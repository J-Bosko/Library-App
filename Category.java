package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Category extends Application implements Serializable {

    private String title;
    private static ObservableList<Category> observableCategories = FXCollections.observableArrayList();
    private static transient VBox categoryBox = new VBox(10);
    private transient Stage stage;
    private static final long serialVersionUID = 1L;

    public Category(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String newTitle) {
        this.title = newTitle;
    }

    public static ObservableList<Category> getObservableCategories() {
        return observableCategories;
    }

    public static void addCategory(Category category) {
        if (!observableCategories.contains(category)) {
            observableCategories.add(category);
        } else {
            System.out.println("Η κατηγορία υπάρχει ήδη.");
        }
    }

    public static void main(String[] args) {
        loadCategoriesFromFile();
        launch(args);
    }

    @Override
    public void stop() {
        saveCategoriesToFile();
    }

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        primaryStage.setTitle("Κατηγορίες Βιβλίων");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));

        Button addCategoryButton = new Button("Προσθήκη Κατηγορίας");
        addCategoryButton.setOnAction(e -> addCategory());

        layout.getChildren().addAll(addCategoryButton, categoryBox);
        primaryStage.setScene(new Scene(layout, 400, 300));

        primaryStage.setOnCloseRequest(event -> {
            saveCategoriesToFile();
        });

        primaryStage.show();

        loadCategoriesFromFile();
        updateCategoryView();
    }


    public static void showCategoryList() {
        Stage stage = new Stage();
        stage.setTitle("Λίστα Κατηγοριών");

        VBox vBox = new VBox();
        ListView<Category> listView = new ListView<>();
        ObservableList<Category> observableList = FXCollections.observableArrayList(observableCategories);
        listView.setItems(observableList);

        for (Category category : observableCategories) {
            HBox categoryBox = new HBox();
            Label categoryLabel = new Label(category.getTitle());
            Button deleteButton = new Button("Διαγραφή");
            Button editButton = new Button("Τροποποίηση");

            deleteButton.setOnAction(e -> {
                observableCategories.remove(category);
                observableList.remove(category);
            });

            editButton.setOnAction(e -> editCategory(category));

            categoryBox.getChildren().addAll(categoryLabel, deleteButton, editButton);
            HBox.setHgrow(deleteButton, Priority.ALWAYS);
            HBox.setHgrow(editButton, Priority.ALWAYS);
            vBox.getChildren().add(categoryBox);
        }

        Scene scene = new Scene(vBox, 400, 400);
        stage.setScene(scene);

        stage.setOnCloseRequest(e -> stage.close());

        stage.show();
    }

    private void addCategory() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Προσθήκη Κατηγορίας");
        dialog.setHeaderText(null);
        dialog.setContentText("Εισάγετε τον τίτλο της νέας κατηγορίας:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String title = result.get();
            Category newCategory = new Category(title);
            addCategory(newCategory);
            showAlert("Επιτυχής Προσθήκη", "Η κατηγορία προστέθηκε με επιτυχία.");
            updateCategoryView();
        }
    }

    private static void deleteCategory(Category category) {
        observableCategories.remove(category);
        showAlert("Επιτυχής Διαγραφή", "Η κατηγορία διαγράφηκε με επιτυχία.");
        updateCategoryView();
    }

    private static void editCategory(Category category) {
        TextInputDialog dialog = new TextInputDialog(category.getTitle());
        dialog.setTitle("Τροποποίηση Κατηγορίας");
        dialog.setHeaderText(null);
        dialog.setContentText("Εισάγετε τον νέο τίτλο της κατηγορίας:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            category.setTitle(result.get());
            showAlert("Επιτυχής Τροποποίηση", "Η κατηγορία τροποποιήθηκε με επιτυχία.");
            updateCategoryView();
        }
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private static void updateCategoryView() {
        categoryBox.getChildren().clear();
        for (Category category : observableCategories) {
            HBox categoryRow = new HBox(10);
            Label titleLabel = new Label(category.getTitle());
            Button editButton = new Button("Τροποποίηση");
            Button deleteButton = new Button("Διαγραφή");

            editButton.setOnAction(e -> editCategory(category));
            deleteButton.setOnAction(e -> deleteCategory(category));

            categoryRow.getChildren().addAll(titleLabel, editButton, deleteButton);
            categoryBox.getChildren().add(categoryRow);
        }
        saveCategoriesToFile();
    }

    public static void saveCategoriesToFile() {
        System.out.println("Λίστα Κατηγοριών πριν την αποθήκευση: " + observableCategories);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("categories.ser"))) {
            oos.writeObject(new ArrayList<>(observableCategories));
            oos.flush();
            System.out.println("Η λίστα κατηγοριών αποθηκεύτηκε επιτυχώς.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Σφάλμα κατά την αποθήκευση της λίστας κατηγοριών.");
        }
    }

    public static void loadCategoriesFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("categories.ser"))) {
            List<Category> loadedCategories = (List<Category>) ois.readObject();
            observableCategories.setAll(loadedCategories);
            System.out.println("Η λίστα κατηγοριών φορτώθηκε επιτυχώς.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Σφάλμα κατά την φόρτωση της λίστας κατηγοριών.");
        }
    }
}
