package com.example.my_book_store_back_end;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


@Entity
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;


    @OneToMany(mappedBy="author", fetch=FetchType.EAGER)
    Set<Book> books = new HashSet<>();

    public Author() { }

    public Author(String first, String last, String userName, String email, String password) {
        this.firstName = first;
        this.lastName = last;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }





    public void addBook(Book book){ this.books.add(book); } // add a book to the Author's collection

    public Set<Book> getBooks() { return books; } // return all the Author's books
    public Long getId() {
        return id;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getUserName() {
        return userName;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() { return password; }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setEmail(String email) {this.email = email; }
    public void setPassword(String password) { this.password = password; }



    @Override
    public String toString() {
        return "Author{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}