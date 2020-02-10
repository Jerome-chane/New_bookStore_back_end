package com.example.my_book_store_back_end;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;



@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")

    private Long id;
    private Date date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="customer_id")
    private Person customer;

//
//    @OneToMany(mappedBy="purchase", fetch = FetchType.EAGER)
//    Set<Book> books = new HashSet<>();
//
@ManyToMany
Set<Book> books;


    public Purchase() {

    }
    public Purchase(Person person ,Set<Book> bookSet, Date date) {
        person.addPurchase(this);
        this.customer = person;
        this.date = date;
        this.books = bookSet;
    }
    public void addBook(Book book){ books.add(book); };
    public Long getId() {
        return id;
    }

    public Person getCustomer() {
        return customer;
    }

    public Set<Book> getBooks() {
        return books;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public String toString() {
        return "Purchase{" +
                "id=" + id +
                ", date=" + date +
//                ", customer=" + customer +
                ", books=" + books +
                '}';
    }
}
