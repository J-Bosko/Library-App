package application;

import java.util.List;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;
    private Stage welcomeStage;  
    private boolean isLoggedIn = false;
    private LibraryManager libraryManager = new LibraryManager();
    public static ListOfUsers listOfUsers = new ListOfUsers();
    
    public static void main(String[] args) {
        launch(args);        
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Electronic Library Application");

        GridPane grid = new GridPane();
        grid.setAlignment(javafx.geometry.Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Button loginButton = new Button("Σύνδεση ως διαχειριστής");
        loginButton.setOnAction(e -> {
            showLoginScreen();
            });

        Button registerButton = new Button("Σύδεση/Εγγραφή χρήστη");
        registerButton.setOnAction(e -> {
            showRegistrationScreen();
            });


        grid.add(loginButton, 0, 0);
        grid.add(registerButton, 1, 0);

        Scene scene = new Scene(grid, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        List<Comment> loadedComments = Comment.loadComments();
        //Comment.loadRatings();
        listOfUsers.loadUserList();
                      
        showWelcomeScreen();
    }
    
    @Override
    public void stop() {
        if (listOfUsers != null) {
            listOfUsers.saveUserList();
        }        
    }

    private void showLoginScreen() {
        Stage loginStage = new Stage();
        loginStage.setTitle("Σύνδεση");

        GridPane loginGrid = new GridPane();
        loginGrid.setAlignment(javafx.geometry.Pos.CENTER);
        loginGrid.setHgap(10);
        loginGrid.setVgap(10);
        loginGrid.setPadding(new Insets(25, 25, 25, 25));

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();

        Button signInButton = new Button("Σύνδεση");
        signInButton.setOnAction(e -> {
            if (usernameField.getText().equals("medialab") && passwordField.getText().equals("medialab_2024")) {
                isLoggedIn = true;
                showWelcomeScreen();
                loginStage.close();
            }
            else {
                showAlert("Σφάλμα", "Μη έγκυρα στοιχεία σύνδεσης διαχειριστή.");
            }
        });

        loginGrid.add(usernameLabel, 0, 0);
        loginGrid.add(usernameField, 1, 0);
        loginGrid.add(passwordLabel, 0, 1);
        loginGrid.add(passwordField, 1, 1);
        loginGrid.add(signInButton, 1, 2);

        Scene loginScene = new Scene(loginGrid, 300, 200);
        loginStage.setScene(loginScene);
        loginStage.show();
    }

    private void showRegistrationScreen() {
        Stage registrationStage = new Stage();
        registrationStage.setTitle("Εγγραφή");

        GridPane registrationGrid = new GridPane();
        registrationGrid.setAlignment(javafx.geometry.Pos.CENTER);
        registrationGrid.setHgap(10);
        registrationGrid.setVgap(10);
        registrationGrid.setPadding(new Insets(25, 25, 25, 25));

        Label usernameLabel = new Label("Username:");
        TextField usernameField = new TextField();
        Label passwordLabel = new Label("Password:");
        PasswordField passwordField = new PasswordField();
        Label nameLabel = new Label("Όνομα:");
        TextField nameField = new TextField();
        Label surnameLabel = new Label("Επώνυμο:");
        TextField surnameField = new TextField();
        Label adtLabel = new Label("ΑΔΤ:");
        TextField adtField = new TextField();
        Label emailLabel = new Label("Email:");
        TextField emailField = new TextField();

        Button signUpButton = new Button("Ολοκλήρωση Εγγραφής");
        signUpButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String name = nameField.getText();
            String surname = surnameField.getText();
            String adt = adtField.getText();
            String email = emailField.getText();

            boolean userExists = listOfUsers.userExistsByADT(adt) || listOfUsers.userExistsByEmail(email);

            if (userExists) {
                showAlert("Σφάλμα", "Υπάρχει ήδη χρήστης με το ίδιο ΑΔΤ ή email.");
            } else {
                LibraryManager.RegistrationResult registrationResult = libraryManager.performActualRegistration(username, password, name, surname, adt, email);

                if (registrationResult.isSuccess()) {
                    User newUser = new User(username, password, name, surname, adt, email);
                    listOfUsers.addUser(newUser);

                    registrationStage.close();

                    UserSearch userSearch = new UserSearch();
                    userSearch.showMaterialPresentation();
                } else {
                    showAlert("Σφάλμα", registrationResult.getMessage());
                }
            }
        });


        registrationGrid.add(usernameLabel, 0, 0);
        registrationGrid.add(usernameField, 1, 0);
        registrationGrid.add(passwordLabel, 0, 1);
        registrationGrid.add(passwordField, 1, 1);
        registrationGrid.add(nameLabel, 0, 2);
        registrationGrid.add(nameField, 1, 2);
        registrationGrid.add(surnameLabel, 0, 3);
        registrationGrid.add(surnameField, 1, 3);
        registrationGrid.add(adtLabel, 0, 4);
        registrationGrid.add(adtField, 1, 4);
        registrationGrid.add(emailLabel, 0, 5);
        registrationGrid.add(emailField, 1, 5);
        registrationGrid.add(signUpButton, 1, 6);

        Scene registrationScene = new Scene(registrationGrid, 400, 300);
        registrationStage.setScene(registrationScene);
        registrationStage.show();
    }

    private void showWelcomeScreen() {
        if (isLoggedIn) {
            if (welcomeStage != null) {
                welcomeStage.close();
            }

            VBox welcomeLayout = new VBox(10);
            welcomeLayout.setPadding(new Insets(10));

            for (Category category : Category.getObservableCategories()) {
                Label categoryLabel = new Label(category.getTitle());
                welcomeLayout.getChildren().add(categoryLabel);
            }

            Button categoriesButton = new Button("Κατηγορίες");
            Button booksButton = new Button("Βιβλία");
            Button userListButton = new Button("Λίστα Εγγεγραμμένων Χρηστών");

            categoriesButton.setOnAction(e -> {
                showCategoriesScreen();
            });
            booksButton.setOnAction(e -> {
                showBooksScreen();
            });
            userListButton.setOnAction(e -> {
                showUserListScreen();
            });
            

            welcomeLayout.getChildren().addAll(categoriesButton, booksButton, userListButton);

            Scene welcomeScene = new Scene(welcomeLayout, 400, 300);
            welcomeStage = new Stage();
            welcomeStage.setScene(welcomeScene);
            welcomeStage.setTitle("Καλωσήλθατε!");
            
            welcomeStage.initModality(Modality.WINDOW_MODAL);
            welcomeStage.initOwner(primaryStage);
            
            welcomeStage.show();
        }
    }


    private void showCategoriesScreen() {
    	Category categoryApp = new Category("Κατηγορίες Βιβλίων");
        Stage categoryStage = new Stage();

        categoryStage.initModality(Modality.WINDOW_MODAL);
        categoryStage.initOwner(primaryStage);

        categoryApp.start(categoryStage);
        categoryStage.show();
    }

    private void showBooksScreen() {
    	Book booksApp = new Book("Βιβλία", "", "", "", 0, "", 0);
        Stage booksStage = new Stage();

        booksStage.initModality(Modality.WINDOW_MODAL);
        booksStage.initOwner(primaryStage);

        booksApp.start(booksStage);
        booksStage.show();
    }
    
    private void showUserListScreen() {
        listOfUsers.showUserList();
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public boolean isLoggedIn() {
        return isLoggedIn;
    }
}
