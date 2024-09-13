package application;

import java.io.Serializable;

class User implements Serializable{
	private static final long serialVersionUID = 1L;
    String username;
    String password;
    String name;
    String surname;
    String ID;
    String email;
    private int userLoans = 0;

	    public User(String username, String password, String name, String surname,
	                String ID, String email) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.ID = ID;
        this.email = email;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getAdt() {
        return ID;
    }

    public String getEmail() {
        return email;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    
    public void setUserLoans(int loans) {
        this.userLoans = loans;
    }

    public int getUserLoans() {
        return userLoans;
    }
    
    @Override
    public String toString() {
        return this.username; 
    }
    
    public int getOpenLoansCount() {
        int count = 0;
        for (Loan loan : Loan.activeLoans) {
            if (loan.getUser().equals(this) && loan.getReturnDate() == null) {
                count++;
            }
        }
        return count;
    }
    
}