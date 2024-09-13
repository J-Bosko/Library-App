package application;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import javafx.scene.control.Alert;
import java.util.ArrayList;
import java.util.List;

public class Loan {
    public User user;
    public Book book;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private static final int MAX_LOANS = 2;
    private static final int LOAN_PERIOD_DAYS = 5;
    public static List<Loan> activeLoans = new ArrayList<>();
    public static List<String> borrowedBookTitles = new ArrayList<>();

    public Loan(User user, Book book) {
        if (activeLoans.size() >= MAX_LOANS) {
            showAlert("Σφάλμα Δανεισμού", "Έχετε ήδη δανειστεί το μέγιστο επιτρεπτό αριθμό βιβλίων.");
            return;
        }
        this.user = user;
        this.book = book;
        this.loanDate = LocalDate.now();
        this.returnDate = loanDate.plusDays(LOAN_PERIOD_DAYS);
        activeLoans.add(this);
        borrowedBookTitles.add(book.getTitle());
    }
    
    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getLoanDate() {
        return loanDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }
    
    public static void UserLoan(User user, Book book) {
        int openLoansCount = user.getOpenLoansCount();

        if (openLoansCount < 2) {
            if (book.getCopiesAvailable() > 0) {
                if (book.getCopiesAvailable() > 0) {
                    Loan loan = attemptLoan(user, book); 
                    if (loan != null) {
                        book.setCopiesAvailable(book.getCopiesAvailable() - 1);
                        addBorrowedBookTitle(book.getTitle()); 
                        saveBorrowedBookTitles("borrowed_books.ser");
                        showAlert("Επιβεβαίωση Δανεισμού", "Ο δανεισμός επιβεβαιώθη.");
                    } else {
                        showAlert("Σφάλμα Δανεισμού", "Υπήρξε σφάλμα κατά τη δημιουργία του δανεισμού.");
                    }
                } else {
                    showAlert("Σφάλμα Δανεισμού", "Δυστυχώς, δεν υπάρχουν διαθέσιμα αντίτυπα του βιβλίου.");
                }
            } else {
                showAlert("Σφάλμα Δανεισμού", "Δυστυχώς, δεν υπάρχουν διαθέσιμα αντίτυπα του βιβλίου.");
            }
        } else {
            showAlert("Σφάλμα Δανεισμού", "Ο χρήστης έχει ήδη δανειστεί το μέγιστο επιτρεπτό αριθμό βιβλίων.");
        }
    }
    
    public static void addBorrowedBookTitle(String bookTitle) {
        borrowedBookTitles.add(bookTitle);
    }
    
    public static void saveBorrowedBookTitles(String fileName) {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName))) {
            outputStream.writeObject(borrowedBookTitles);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static void loadBorrowedBookTitles(String fileName) {
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(fileName))) {
            borrowedBookTitles = (List<String>) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    public static List<String> getBorrowedBookTitles(User user) {
        List<String> borrowedBookTitles = new ArrayList<>();
        for (Loan loan : activeLoans) {
            if (loan.getUser().equals(user)) {
                borrowedBookTitles.add(loan.getBook().getTitle());
            }
        }
        return borrowedBookTitles;
    }

    private static Loan attemptLoan(User user, Book book) {
        if (activeLoans.size() >= MAX_LOANS) {
            showAlert("Σφάλμα Δανεισμού", "Έχετε ήδη δανειστεί το μέγιστο επιτρεπτό αριθμό βιβλίων.");
            return null;
        }

        Loan loan = new Loan(user, book);
        if (loan.getReturnDate() != null) {
            activeLoans.add(loan); 
            return loan;
        } else {
            return null;
        }
    }
    
    public static void showActiveLoans(User user) {
        loadBorrowedBookTitles("borrowed_books.ser");
        System.out.println("Περιεχόμενα λίστας borrowedBookTitles: " + borrowedBookTitles);
        
        if (borrowedBookTitles.isEmpty()) {
            showAlert("Δεν υπάρχουν δανεισμένα βιβλία", "Ο χρήστης δεν έχει δανειστεί κανένα βιβλίο.");
        } 
        else {
            String bookTitles = String.join("\n", borrowedBookTitles);
            showAlert("Δανεισμένα Βιβλία", "Ο χρήστης έχει δανειστεί τα παρακάτω βιβλία:\n" + bookTitles);            
        }
    }
    
    private static void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
