package application;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

public class LibraryManager implements Serializable {
    List<Book> books = new ArrayList<>();
    List<Category> categories = new ArrayList<>();
    List<User> users = new ArrayList<>();
    List<Loan> loans = new ArrayList<>();
    
    public static class RegistrationResult {
        private boolean success;
        private String message;

        public RegistrationResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
    
    public static class EditResult {
        private boolean success;
        private String message;

        public EditResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }

    public RegistrationResult performActualRegistration(String username, String password, String name, String surname, String adt, String email) {
        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty() || adt.isEmpty() || email.isEmpty()) {
            return new RegistrationResult(false, "Παρακαλώ συμπληρώστε όλα τα πεδία.");
        }

        if (isUsernameUnique(username)) {
            User newUser = new User(username, password, name, surname, adt, email);
            users.add(newUser);
            return new RegistrationResult(true, "Επιτυχής εγγραφή!");
        } else {
            return new RegistrationResult(false, "Το όνομα χρήστη υπάρχει ήδη. Επιλέξτε διαφορετικό όνομα.");
        }
    }
    
    public EditResult performActualEdit(String username, String password, String name, String surname, String adt, String email) {
        if (username.isEmpty() || password.isEmpty() || name.isEmpty() || surname.isEmpty() || adt.isEmpty() || email.isEmpty()) {
            return new EditResult(false, "Παρακαλώ συμπληρώστε όλα τα πεδία.");
        }

        if (isUsernameUnique(username)) {
            User newUser = new User(username, password, name, surname, adt, email);
            users.add(newUser);
            return new EditResult(true, "Επιτυχής τροποποίηση στοιχείων χρήστη!");
        } else {
            return new EditResult(false, "Το όνομα χρήστη υπάρχει ήδη. Επιλέξτε διαφορετικό όνομα.");
        }
    }
    
    private boolean isUsernameUnique(String username) {
        for (User existingUser : users) {
            if (existingUser.getUsername().equals(username)) {
                return false;
            }
        }
        return true;
    }

    public void addBook(Book book) {
        books.add(book);
    }

    public void deleteBook(Book book) {
        books.remove(book);
        loans.removeIf(loan -> loan.getBook().equals(book));
    }

    public void updateBookInformation(Book book, String newTitle, String newAuthor,
                                      String newPublisher, String newISBN, int newPublicationYear,
                                      String newCategory, int newCopiesAvailable) {
        book.title = newTitle;
        book.author = newAuthor;
        book.publisher = newPublisher;
        book.ISBN = newISBN;
        book.publicationYear = newPublicationYear;
        book.category = newCategory;
        book.copiesAvailable = newCopiesAvailable;
    }

    public void addCategory(String categoryTitle) {
        Category category = new Category(categoryTitle);
        categories.add(category);
    }

    public void deleteCategory(String categoryTitle) {
        categories.removeIf(category -> category.getTitle().equals(categoryTitle));
        books.removeIf(book -> book.category.equals(categoryTitle));
    }

    public void updateUserInformation(User user, String newName, String newSurname,
                                      String newID, String newEmail) {
        user.name = newName;
        user.surname = newSurname;
        user.ID = newID;
        user.email = newEmail;
    }

    public void deleteUser(User user) {
        users.remove(user);
        loans.stream()
             .filter(loan -> loan.getUser().equals(user))
             .forEach(loan -> loan.getBook().copiesAvailable++);
        loans.removeIf(loan -> loan.getUser().equals(user));
    }
}