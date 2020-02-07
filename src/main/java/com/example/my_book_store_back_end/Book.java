package com.example.my_book_store_back_end;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Book {


        @Id
        @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
        @GenericGenerator(name = "native", strategy = "native")
        private long id;
        private String title;
        private String language;
        private String description;
        private String cover;
        private String detail;

        @ManyToOne(fetch = FetchType.EAGER)
        @JoinColumn(name="author_id")
        private Author author;

//         @ManyToOne(fetch = FetchType.EAGER)
//         @JoinColumn(name="purchase_id")
//         private Purchase purchase;
//
@ManyToMany(mappedBy = "books")
Set<Purchase> purchases;


        public Book() { }

        public Book(String title,String language, String description, String cover, String detail, Author author) {
           this.title = title;
           this.language = language;
           this.description = description;
           this.cover = cover;
           this.detail = detail;
           if(author!=null) {
               author.addBook(this);
                this.author = author;}
        }

    public Set<Purchase> getPurchases() {
        return purchases;
    }

    //    public Purchase getPurchase() { return purchase; }
    public Author getAuthor() { return author; }
    public String getTitle() { return title; }
    public String getCover() { return cover;}
        public String getDescription() { return description; }
        public String getDetail() { return detail; }
        public String getLanguage() { return language; }

         public void addPurchase(Purchase purchase){
//            purchase.addBook(this);
            this.purchases.add(purchase)
         ; }

        public void setTitle(String title) { this.title = title; }
        public void setCover(String cover) { this.cover = cover; }
        public void setDescription(String description) { this.description = description; }
        public void setDetail(String detail) { this.detail = detail; }
        public void setLanguage(String language) { this.language = language; }

    @Override
    public String toString() {
        return "Book{" +
                "title='" + title + '\'' +
//                ", cover='" + cover + '\'' +
//                ", detail='" + detail + '\'' +
//                ", description='" + description + '\'' +
                ", language='" + language + '\'' +
                '}';
    }
}
