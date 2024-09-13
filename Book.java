package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.scene.control.ListView;

public class Book extends Application implements Serializable {
    String title;
    String author;
    String publisher;
    String ISBN;
    int publicationYear;
    String category;
    int copiesAvailable;
    private Stage stage;
    private static final long serialVersionUID = 1L;
    public static ObservableList<Book> allBooks = FXCollections.observableArrayList();
    private static transient VBox bookBox = new VBox(10);

    public Book(String title, String author, String publisher, String ISBN,
                int publicationYear, String category, int copiesAvailable) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.ISBN = ISBN;
        this.publicationYear = publicationYear;
        this.category = category;
        this.copiesAvailable = copiesAvailable;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getPublisher() {
        return publisher;
    }
    
    public String getISBN() {
        return ISBN;
    }
    
    public int getPublicationYear() {
        return publicationYear;
    }

    public int getCopiesAvailable() {
        return copiesAvailable;
    }
    
    public void setCopiesAvailable(int copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    
    public String getCategory() {
        return category;
    }
    
    public static ObservableList<Book> getAllBooks() {
        return allBooks;
    }
    
    public static void main(String[] args) {
    	launch(args);
    	loadBooksFromFile();
    }
    
    @Override
    public void stop() {
        saveBooksToFile();
    }
    
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Διαχείριση Βιβλίων");

        Button addBookButton = new Button("Προσθήκη Βιβλίου");
        Button viewAllBooksButton = new Button("Όλα τα Βιβλία");

        addBookButton.setOnAction(e -> addBook());
        viewAllBooksButton.setOnAction(e -> viewAllBooks());


        VBox buttonsBox = new VBox(10, addBookButton, viewAllBooksButton);
        buttonsBox.setPadding(new Insets(10));

        VBox layout = new VBox(10, buttonsBox);
        layout.setPadding(new Insets(10));

        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
        
        Comment.loadRatings();
        loadBooksFromFile();
        updateBookView();
        primaryStage.setOnCloseRequest(event -> Comment.saveRatings());
    }
    
    public static Book addBook() {
        TextInputDialog titleDialog = new TextInputDialog();
        titleDialog.setTitle("Προσθήκη Βιβλίου");
        titleDialog.setHeaderText(null);
        titleDialog.setContentText("Εισάγετε τον τίτλο του βιβλίου:");
        Optional<String> titleResult = titleDialog.showAndWait();

        if (titleResult.isPresent()) {
            String title = titleResult.get();

            TextInputDialog authorDialog = new TextInputDialog();
            authorDialog.setTitle("Προσθήκη Βιβλίου");
            authorDialog.setHeaderText(null);
            authorDialog.setContentText("Εισάγετε τον συγγραφέα:");
            Optional<String> authorResult = authorDialog.showAndWait();
            String author = authorResult.orElse("");

            TextInputDialog publisherDialog = new TextInputDialog();
            publisherDialog.setTitle("Προσθήκη Βιβλίου");
            publisherDialog.setHeaderText(null);
            publisherDialog.setContentText("Εισάγετε τον εκδοτικό οίκο:");
            Optional<String> publisherResult = publisherDialog.showAndWait();
            String publisher = publisherResult.orElse("");

            TextInputDialog ISBNDialog = new TextInputDialog();
            ISBNDialog.setTitle("Προσθήκη Βιβλίου");
            ISBNDialog.setHeaderText(null);
            ISBNDialog.setContentText("Εισάγετε το ISBN:");
            Optional<String> ISBNResult = ISBNDialog.showAndWait();
            String ISBN = ISBNResult.orElse("");

            TextInputDialog publicationYearDialog = new TextInputDialog();
            publicationYearDialog.setTitle("Προσθήκη Βιβλίου");
            publicationYearDialog.setHeaderText(null);
            publicationYearDialog.setContentText("Εισάγετε το έτος έκδοσης:");
            Optional<String> publicationYearResult = publicationYearDialog.showAndWait();
            int publicationYear = Integer.parseInt(publicationYearResult.orElse("0"));

            TextInputDialog categoryDialog = new TextInputDialog();
            categoryDialog.setTitle("Προσθήκη Βιβλίου");
            categoryDialog.setHeaderText(null);
            categoryDialog.setContentText("Εισάγετε την κατηγορία:");
            Optional<String> categoryResult = categoryDialog.showAndWait();
            String category = categoryResult.orElse("");

            TextInputDialog copiesAvailableDialog = new TextInputDialog();
            copiesAvailableDialog.setTitle("Προσθήκη Βιβλίου");
            copiesAvailableDialog.setHeaderText(null);
            copiesAvailableDialog.setContentText("Εισάγετε τον διαθέσιμο αριθμό αντιτύπων:");
            Optional<String> copiesAvailableResult = copiesAvailableDialog.showAndWait();
            int copiesAvailable = Integer.parseInt(copiesAvailableResult.orElse("0"));

            Book newBook = new Book(title, author, publisher, ISBN, publicationYear, category, copiesAvailable);

            allBooks.add(newBook);

            showAlert("Επιτυχής Προσθήκη", "Το βιβλίο προστέθηκε με επιτυχία.");

            updateBookView();

            return newBook;
        } else {
            return null;
        }
    }
    
    public static void showBookList() {
        Stage stage = new Stage();
        stage.setTitle("Λίστα Βιβλίων");
        ListView<Book> listView = new ListView<>();
        listView.setItems(allBooks);
        HBox hBox = new HBox();
        hBox.getChildren().add(listView);
        Scene scene = new Scene(hBox, 400, 400);
        stage.setScene(scene);
        stage.setOnCloseRequest(e -> stage.close());
        stage.show();
    }
    
    private static void deleteBook(Book bookToDelete) {
        allBooks.removeIf(book -> book.equals(bookToDelete));
        updateBookView();
    }


    public static void editBook(Book bookToEdit) {
        TextInputDialog dialog = new TextInputDialog(bookToEdit.getTitle());
        dialog.setTitle("Τροποποίηση Βιβλίου");
        dialog.setHeaderText(null);
        dialog.setContentText("Εισάγετε τον νέο τίτλο του βιβλίου:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String newTitle = result.get();
            bookToEdit.setTitle(newTitle);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Επιτυχής Τροποποίηση");
            alert.setHeaderText(null);
            alert.setContentText("Το βιβλίο τροποποιήθηκε με επιτυχία.");
            alert.showAndWait();
            updateBookView();
        }
    }

    public static void showBookDetails(Book book) {
        Stage bookDetailsStage = new Stage();
        bookDetailsStage.setTitle("Λεπτομέρειες Βιβλίου");

        GridPane bookDetailsGrid = new GridPane();
        bookDetailsGrid.setAlignment(Pos.CENTER);
        bookDetailsGrid.setHgap(10);
        bookDetailsGrid.setVgap(10);
        bookDetailsGrid.setPadding(new Insets(25, 25, 25, 25));

        bookDetailsGrid.add(new Label("Τίτλος:"), 0, 0);
        bookDetailsGrid.add(new Label(book.getTitle()), 1, 0);
        bookDetailsGrid.add(new Label("Συγγραφέας:"), 0, 1);
        bookDetailsGrid.add(new Label(book.getAuthor()), 1, 1);
        bookDetailsGrid.add(new Label("Εκδοτικός Οίκος:"), 0, 2);
        bookDetailsGrid.add(new Label(book.getPublisher()), 1, 2);
        bookDetailsGrid.add(new Label("ISBN:"), 0, 3);
        bookDetailsGrid.add(new Label(book.getISBN()), 1, 3);
        bookDetailsGrid.add(new Label("Έτος Έκδοσης:"), 0, 4);
        bookDetailsGrid.add(new Label(Integer.toString(book.getPublicationYear())), 1, 4);
        bookDetailsGrid.add(new Label("Κατηγορία:"), 0, 5);
        bookDetailsGrid.add(new Label(book.getCategory()), 1, 5);
        bookDetailsGrid.add(new Label("Διαθέσιμα Αντίτυπα:"), 0, 6);
        bookDetailsGrid.add(new Label(Integer.toString(book.getCopiesAvailable())), 1, 6);
        bookDetailsGrid.add(new Label("Μέσος Όρος Βαθμολογίας:"), 0, 7);
        bookDetailsGrid.add(new Label(Double.toString(Comment.getRating())), 1, 7);

        Button closeButton = new Button("Κλείσιμο");
        closeButton.setOnAction(e -> bookDetailsStage.close());
        bookDetailsGrid.add(closeButton, 1, 8);

        Button commentsButton = new Button("Σχόλια Χρηστών");
        commentsButton.setOnAction(e -> {
            List<Comment> loadedComments = Comment.loadComments();
            if (!loadedComments.isEmpty()) {
                Comment.showComments(loadedComments);
            } else {
                showAlert("Δεν υπάρχουν σχόλια", "Δεν υπάρχουν σχόλια για αυτό το βιβλίο ακόμη.");
            }
        });
        bookDetailsGrid.add(commentsButton, 0, 8);

        Scene bookDetailsScene = new Scene(bookDetailsGrid, 400, 300);
        bookDetailsStage.setScene(bookDetailsScene);
        bookDetailsStage.show();
    }

    
    public void addRating(Comment comment) {
        comment.addRating(); 
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Επιτυχής Προσθήκη Βαθμολογίας");
        alert.setHeaderText(null);
        alert.setContentText("Η βαθμολογία προστέθηκε με επιτυχία.");
        alert.showAndWait();
    }

    
    public static void updateBookView() {
        bookBox.getChildren().clear();
        for (Book book : allBooks) {
            HBox bookRow = new HBox(10);
            Label titleLabel = new Label(book.getTitle());
            Button editButton = new Button("Τροποποίηση");
            Button deleteButton = new Button("Διαγραφή");
            Button detailsButton = new Button("Λεπτομέρειες");

            editButton.setOnAction(e -> editBook(book));
            deleteButton.setOnAction(e -> deleteBook(book));
            detailsButton.setOnAction(e -> showBookDetails(book));

            bookRow.getChildren().addAll(titleLabel, editButton, deleteButton, detailsButton);
            bookBox.getChildren().add(bookRow);
        }
        saveBooksToFile();
    }
   
    public static void saveBooksToFile() {
        System.out.println("Λίστα Βιβλίων πριν την αποθήκευση: " + allBooks);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("books.ser"))) {
            oos.writeObject(new ArrayList<>(allBooks));
            oos.flush();
            System.out.println("Η λίστα βιβλίων αποθηκεύτηκε επιτυχώς.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Σφάλμα κατά την αποθήκευση της λίστας βιβλίων.");
        }
    }

    public static void loadBooksFromFile() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("books.ser"))) {
            List<Book> loadedBooks = (List<Book>) ois.readObject();
            allBooks.setAll(loadedBooks);
            System.out.println("Η λίστα βιβλίων φορτώθηκε επιτυχώς: " + allBooks);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("Σφάλμα κατά την φόρτωση της λίστας βιβλίων.");
        }
    }
    
    public static void viewAllBooks() {
        Stage stage = new Stage();
        stage.setTitle("Λίστα Βιβλίων");

        VBox vBox = new VBox();
        ListView<Book> listView = new ListView<>();
        ObservableList<Book> observableList = FXCollections.observableArrayList(allBooks);
        listView.setItems(observableList);

        for (Book book : allBooks) {
            HBox bookBox = new HBox();
            Label bookLabel = new Label(book.getTitle());
            Button detailsButton = new Button("Λεπτομέρειες");
            Button deleteButton = new Button("Διαγραφή");
            Button editButton = new Button("Τροποποίηση");

            detailsButton.setOnAction(e -> showBookDetails(book));
            deleteButton.setOnAction(e -> deleteBook(book));

            editButton.setOnAction(e -> editBook(book));

            bookBox.getChildren().addAll(bookLabel, detailsButton, deleteButton, editButton);
            
            HBox.setHgrow(detailsButton, Priority.ALWAYS);
            HBox.setHgrow(deleteButton, Priority.ALWAYS);
            HBox.setHgrow(editButton, Priority.ALWAYS);
            vBox.getChildren().add(bookBox);
        }

        Scene scene = new Scene(vBox, 400, 400);
        stage.setScene(scene);

        stage.setOnCloseRequest(e -> stage.close());

        stage.show();
    }
   
    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
