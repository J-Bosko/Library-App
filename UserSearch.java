package application;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

public class UserSearch {
    public static User currentUser = new User(null, null, null, null, null, null);

    public static void searchMaterial() {
        Book.loadBooksFromFile();

        Stage searchStage = new Stage();
        searchStage.setTitle("Αναζήτηση Υλικού");

        GridPane searchGrid = new GridPane();
        searchGrid.setAlignment(javafx.geometry.Pos.CENTER);
        searchGrid.setHgap(10);
        searchGrid.setVgap(10);
        searchGrid.setPadding(new Insets(25, 25, 25, 25));

        Label titleLabel = new Label("Τίτλος:");
        TextField titleField = new TextField();
        Label authorLabel = new Label("Συγγραφέας:");
        TextField authorField = new TextField();
        Label yearLabel = new Label("Έτος Έκδοσης:");
        TextField yearField = new TextField();

        Button searchByTitleButton = new Button("Αναζήτηση με Τίτλο");
        searchByTitleButton.setOnAction(e -> {
            String title = titleField.getText();
            if (!title.isEmpty()) {
                Book foundBook = findBookByTitle(title);
                if (foundBook != null) {
                    performSearch(title, "", "", currentUser);
                    searchStage.close();
                } else {
                    showAlert("Σφάλμα", "Το βιβλίο με τίτλο '" + title + "' δεν βρέθηκε.");
                }
            }
        });

        Button searchByAuthorButton = new Button("Αναζήτηση με Συγγραφέα");
        searchByAuthorButton.setOnAction(e -> {
            String author = authorField.getText();
            if (!author.isEmpty()) {
                Book foundBook = findBookByAuthor(author);
                if (foundBook != null) {
                    performSearch("", author, "", currentUser);
                    searchStage.close();
                } else {
                    showAlert("Σφάλμα", "Δεν βρέθηκαν βιβλία από τον συγγραφέα '" + author + "'.");
                }
            }
        });

        Button searchByYearButton = new Button("Αναζήτηση με Έτος Έκδοσης");
        searchByYearButton.setOnAction(e -> {
            String year = yearField.getText();
            if (!year.isEmpty()) {
                Book foundBook = findBookByPublicationYear(year);
                if (foundBook != null) {
                    performSearch("", "", year, currentUser);
                    searchStage.close();
                } else {
                    showAlert("Σφάλμα", "Δεν βρέθηκαν βιβλία με έτος έκδοσης '" + year + "'.");
                }
            }
        });

        searchGrid.add(titleLabel, 0, 0);
        searchGrid.add(titleField, 1, 0);
        searchGrid.add(searchByTitleButton, 2, 0);
        searchGrid.add(authorLabel, 0, 1);
        searchGrid.add(authorField, 1, 1);
        searchGrid.add(searchByAuthorButton, 2, 1);
        searchGrid.add(yearLabel, 0, 2);
        searchGrid.add(yearField, 1, 2);
        searchGrid.add(searchByYearButton, 2, 2);

        Scene searchScene = new Scene(searchGrid, 500, 200);

        searchStage.setScene(searchScene);

        searchStage.show();
    }

    public static void showMaterialPresentation() {
        Stage presentationStage = new Stage();
        presentationStage.setTitle("Βιβλία και Δανεισμός");

        VBox presentationVBox = new VBox(10);
        presentationVBox.setAlignment(javafx.geometry.Pos.CENTER);
        presentationVBox.setPadding(new Insets(25, 25, 25, 25));

        Button searchBookButton = new Button("Αναζήτηση Βιβλίου");
        searchBookButton.setOnAction(e -> {
            UserSearch.searchMaterial();
        });

        presentationVBox.getChildren().addAll(searchBookButton);

        Scene presentationScene = new Scene(presentationVBox, 300, 150);

        presentationStage.setScene(presentationScene);

        presentationStage.show();
    }

    private static void performSearch(String title, String author, String year, User currentUser) {
        Book.loadBooksFromFile();
        
        Book foundTitle = findBookByTitle(title);
        Book foundAuthor = findBookByAuthor(author);
        Book foundYear = findBookByPublicationYear(year);

        if (foundTitle != null || foundAuthor != null || foundYear != null) {
            Stage presentationStage = new Stage();
            presentationStage.setTitle("Παρουσίαση Βιβλίου");

            Button detailsButton = new Button("Λεπτομέρειες");
            detailsButton.setOnAction(e -> {
                if (foundTitle != null) {
                    Book.showBookDetails(foundTitle);
                } else if (foundAuthor != null) {
                    Book.showBookDetails(foundAuthor);
                } else {
                    Book.showBookDetails(foundYear);
                }
            });

            Button borrowButton = new Button("Δανεισμός Βιβλίου");
            borrowButton.setOnAction(event -> {
                Book bookToBorrow = (foundTitle != null) ? foundTitle : (foundAuthor != null) ? foundAuthor : foundYear;
                Loan.UserLoan(currentUser, bookToBorrow);                
            });

            Button addCommentButton = new Button("Προσθήκη Σχολίου");
            addCommentButton.setOnAction(e -> Comment.addComment());

            Button addRatingButton = new Button("Προσθήκη Βαθμολογίας");
            addRatingButton.setOnAction(e -> Comment.addRating());

            VBox presentationVBox = new VBox(10);
            presentationVBox.setAlignment(Pos.CENTER);
            presentationVBox.setPadding(new Insets(25, 25, 25, 25));
            presentationVBox.getChildren().addAll(
                    new Label("Επιλεγμένο Βιβλίο:"),
                    new Label((foundTitle != null) ? foundTitle.getTitle() :
                            (foundAuthor != null) ? foundAuthor.getTitle() : foundYear.getTitle()),
                    detailsButton,
                    borrowButton,
                    addCommentButton,
                    addRatingButton
            );

            Scene presentationScene = new Scene(presentationVBox, 300, 200);

            presentationStage.setScene(presentationScene);
            presentationStage.show();
        } else {
            showAlert("Σφάλμα", "Δεν βρέθηκε κανένα βιβλίο με τις παρεχόμενες πληροφορίες.");
        }
    }



    private static Book findBookByTitle(String title) {
        for (Book book : Book.getAllBooks()) {
            if (book.getTitle().equalsIgnoreCase(title)) {
                return book;
            }
        }
        return null;
    }

    private static Book findBookByAuthor(String author) {
        for (Book book : Book.getAllBooks()) {
            if (book.getAuthor().equalsIgnoreCase(author)) {
                return book;
            }
        }
        return null;
    }

    private static Book findBookByPublicationYear(String year) {
        for (Book book : Book.getAllBooks()) {
            if (String.valueOf(book.getPublicationYear()).equalsIgnoreCase(year)) {
                return book;
            }
        }
        return null;
    }

    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
