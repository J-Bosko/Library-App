package application;

import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.awt.Button;
import java.awt.event.ActionEvent;
import java.io.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Comment implements Serializable {
    private static final long serialVersionUID = 1L;
    private String text;
    private static double rating;
    int usersRated = 0;
    private static ObservableList<Double> ratings = FXCollections.observableArrayList();
    private static ObservableList<Comment> comments = FXCollections.observableArrayList(); 

    // Κατασκευαστής για το Comment με σχόλιο
    public Comment(String text) {
        this.text = text;
        this.rating = 0; // Η βαθμολογία αρχικά ορίζεται ως 0
    }

    // Κατασκευαστής για το Comment με βαθμολογία
    public Comment(int rating) {
        this.text = ""; // Το σχόλιο αρχικά είναι κενό
        this.rating = rating;
    }

    // Κατασκευαστής για το Comment με σχόλιο και βαθμολογία
    public Comment(String text, int rating) {
        this.text = text;
        Comment.rating = rating;
        this.usersRated = 1; // Ένας χρήστης έχει κάνει ήδη βαθμολογία κατά τη δημιουργία του αντικειμένου
    }
    
    public Comment(double rating, int usersRated, String comments) {
        Comment.rating = rating;
        this.usersRated = usersRated;
        this.text = comments;
    }
    
    public String getText() {
        return text;
    }
    
    public static ObservableList<Comment> getObservableComments() {
        return comments;
    }
    
    public void addRating(double rating) {
        Comment.rating = rating;
        ratings.add(rating);
        calculateAverageRating();
    }
    
    private static void calculateAverageRating() {
        double sum = 0.0;
        for (double r : ratings) {
            sum += r;
        }
        Comment.rating = sum / ratings.size();
    }
    
    public static double getRating() {
        double sum = 0.0;
        for (double r : ratings) {
            sum += r;
        }
        return ratings.isEmpty() ? 0.0 : sum / ratings.size();
    }

    
    public int getUsersRated() {
        return usersRated;
    }
    
    public static void addComment() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Προσθήκη Σχολίου");
        dialog.setHeaderText(null);
        dialog.setContentText("Εισάγετε το σχόλιο:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String commentText = result.get();
            Comment comment = new Comment(commentText);
            comments.add(comment);
            comment.showComment();
            saveComments();
        }
    }

    public void showComment() {
        System.out.println("Σχόλιο: " + text);
    }
    
    public static void addRating() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Προσθήκη Βαθμολογίας");
        dialog.setHeaderText(null);
        dialog.setContentText("Εισάγετε τη βαθμολογία (από 1 έως 5):");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                int ratingValue = Integer.parseInt(result.get());
                if (isValidRating(ratingValue)) {
                    ratings.add((double) ratingValue);
                    Comment comment = new Comment(ratingValue);
                    comment.showRating();
                    saveRatings();
                } else {
                    showAlert("Σφάλμα", "Μη έγκυρη βαθμολογία. Η βαθμολογία πρέπει να είναι από 1 έως 5.");
                }
            } catch (NumberFormatException e) {
                showAlert("Σφάλμα", "Μη έγκυρη είσοδος. Εισάγετε έναν ακέραιο αριθμό.");
            }
        }
    }

    private static boolean isValidRating(int rating) {
        return rating >= 1 && rating <= 5;
    }
   
    public void showRating() {
        System.out.println("Βαθμολογία: " + rating);
    }
    
    public static void showComments(List<Comment> newComments) {
        Stage commentsStage = new Stage();
        commentsStage.setTitle("Σχόλια Χρηστών");

        ListView<String> listView = new ListView<>();

        for (Comment comment : newComments) {
            comments.add(comment);
            listView.getItems().add(comment.getText());
        }

        VBox layout = new VBox(10);
        layout.getChildren().addAll(listView);
        layout.setPrefWidth(400);
        layout.setPrefHeight(300);
        layout.setSpacing(10);
        layout.setStyle("-fx-padding: 10;");

        Scene scene = new Scene(layout);
        commentsStage.setScene(scene);
        commentsStage.initModality(Modality.APPLICATION_MODAL);
        commentsStage.showAndWait();
    }


    
    public static void saveComments() {
        System.out.println("Λίστα Σχολίων πριν την αποθήκευση: " + comments);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("comments.ser"))) {
            oos.writeObject(new ArrayList<>(comments));
            oos.flush();
            System.out.println("Η λίστα σχολίων αποθηκεύτηκε επιτυχώς.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Σφάλμα κατά την αποθήκευση της λίστας σχολίων.");
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Comment> loadComments() {
        List<Comment> comments = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("comments.ser"))) {
            comments = (List<Comment>) ois.readObject();
            System.out.println("Τα σχόλια φορτώθηκαν επιτυχώς.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Σφάλμα κατά την φόρτωση των σχολίων: " + e.getMessage());
        }
        return comments;
    }

    
    public static void saveRatings() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("ratings.ser"))) {
            oos.writeDouble(getRating());
            oos.flush();
            System.out.println("Ο μέσος όρος των βαθμολογιών αποθηκεύτηκε επιτυχώς.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Σφάλμα κατά την αποθήκευση του μέσου όρου των βαθμολογιών.");
        }
    }
    
    public static void loadRatings() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("ratings.ser"))) {
            rating = ois.readDouble();
            System.out.println("Ο μέσος όρος των βαθμολογιών φορτώθηκε επιτυχώς.");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Σφάλμα κατά την φόρτωση του μέσου όρου των βαθμολογιών.");
        }
    }
    
    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
