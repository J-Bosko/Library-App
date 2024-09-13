package application;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.io.Serializable;

public class ListOfUsers implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<User> usersList;
    private LibraryManager libraryManager = new LibraryManager();
    private transient ObservableList<User> observableList;
    private Book selectedBook;

    public ListOfUsers() {
        this.usersList = new ArrayList<>();
        this.observableList = FXCollections.observableArrayList(usersList);
    }

    public void addUser(User user) {
        usersList.add(user);
    }
    
    public boolean userExistsByADT(String adt) {
        for (User user : usersList) {
            if (user.getAdt().equals(adt)) {
                return true;
            }
        }
        return false;
    }

    public boolean userExistsByEmail(String email) {
        for (User user : usersList) {
            if (user.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    public List<User> getUsersList() {
        return usersList;
    }
    
    public void selectBook(Book book) {
        this.selectedBook = book;
    }

    public void initiateLoanProcess() {
        showUserList();
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public void showUserList() {
        Stage stage = new Stage();
        stage.setTitle("Λίστα Χρηστών");

        VBox vBox = new VBox();
        ListView<User> listView = new ListView<>();
        ObservableList<User> observableList = FXCollections.observableArrayList(usersList);
        listView.setItems(observableList);

        for (User user : usersList) {
            HBox userBox = new HBox();
            Label userLabel = new Label(user.toString());
            Button deleteButton = new Button("Διαγραφή");
            Button editButton = new Button("Επεξεργασία");
            Button activeLoansButton = new Button("Ενεργοί Δανεισμοί"); // Νέο κουμπί

            deleteButton.setOnAction(e -> {
                usersList.remove(user);
                observableList.remove(user);
            });

            editButton.setOnAction(e -> showEditUserScreen());

            activeLoansButton.setOnAction(e -> {
                Loan.showActiveLoans(user);
            });

            userBox.getChildren().addAll(userLabel, deleteButton, editButton, activeLoansButton);
            HBox.setHgrow(deleteButton, Priority.ALWAYS);
            HBox.setHgrow(editButton, Priority.ALWAYS);
            HBox.setHgrow(activeLoansButton, Priority.ALWAYS);
            vBox.getChildren().add(userBox);
        }

        Scene scene = new Scene(vBox, 450, 400);
        stage.setScene(scene);
        stage.show();
    }

 
    public void editUser(User user, String newUsername, String newPassword, String newName, String newSurname,
            String newID, String newEmail) {
        user.setUsername(newUsername);
        user.setPassword(newPassword);
        user.setName(newName);
        user.setSurname(newSurname);
        user.setID(newID);
        user.setEmail(newEmail);

        showUserList();
    }
    
	    private void showEditUserScreen() {
	        Stage editUserStage = new Stage();
	        editUserStage.setTitle("Επεξεργασία Χρήστη");
	
	        GridPane editUserGrid = new GridPane();
	        editUserGrid.setAlignment(Pos.CENTER);
	        editUserGrid.setHgap(10);
	        editUserGrid.setVgap(10);
	        editUserGrid.setPadding(new javafx.geometry.Insets(25, 25, 25, 25));
	
	        Label usernameLabel = new Label("Νεο Username:");
	        TextField usernameField = new TextField();
	        Label passwordLabel = new Label("Νεο Password:");
	        PasswordField passwordField = new PasswordField();
	        Label nameLabel = new Label("Νεο Όνομα:");
	        TextField nameField = new TextField();
	        Label surnameLabel = new Label("Νεο Επώνυμο:");
	        TextField surnameField = new TextField();
	        Label adtLabel = new Label("Νεο ΑΔΤ:");
	        TextField adtField = new TextField();
	        Label emailLabel = new Label("Νεο Email:");
	        TextField emailField = new TextField();
	
	        Button signUpButton = new Button("Ολοκλήρωση Τροποποίησης");
	        signUpButton.setOnAction(e -> {
	            libraryManager.performActualEdit(usernameField.getText(), passwordField.getText(),
	                    nameField.getText(), surnameField.getText(), adtField.getText(), emailField.getText());
	
	            editUserStage.close();
	        });
	
	        editUserGrid.add(usernameLabel, 0, 0);
	        editUserGrid.add(usernameField, 1, 0);
	        editUserGrid.add(passwordLabel, 0, 1);
	        editUserGrid.add(passwordField, 1, 1);
	        editUserGrid.add(nameLabel, 0, 2);
	        editUserGrid.add(nameField, 1, 2);
	        editUserGrid.add(surnameLabel, 0, 3);
	        editUserGrid.add(surnameField, 1, 3);
	        editUserGrid.add(adtLabel, 0, 4);
	        editUserGrid.add(adtField, 1, 4);
	        editUserGrid.add(emailLabel, 0, 5);
	        editUserGrid.add(emailField, 1, 5);
	        editUserGrid.add(signUpButton, 1, 6);
	
	        Scene editUserScene = new Scene(editUserGrid, 400, 300);
	        editUserStage.setScene(editUserScene);
	        editUserStage.show();
	    }

    private void refreshUserList() {
        observableList.clear();
        observableList.addAll(usersList);
        showUserList();
    }
       
    public void saveUserList() {
    	System.out.println("Λίστα Χρηστών πριν την αποθήκευση: " + usersList);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("usersList.ser"))) {
            oos.writeObject(usersList);
            oos.flush();
            System.out.println("Η λίστα χρηστών αποθηκεύτηκε επιτυχώς.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Σφάλμα κατά την αποθήκευση της λίστας χρηστών.");
        }
    }

    public void loadUserList() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("usersList.ser"))) {
            List<User> loadedUsers = (List<User>) ois.readObject();
            usersList.addAll(loadedUsers);
            System.out.println("Η λίστα χρηστών φορτώθηκε επιτυχώς.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Σφάλμα κατά την φόρτωση της λίστας χρηστών.");
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
