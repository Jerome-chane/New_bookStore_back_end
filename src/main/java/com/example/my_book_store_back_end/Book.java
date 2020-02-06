package com.example.my_book_store_back_end;


import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

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

        public Book() { }

        public Book(String title,String language, String description, String cover, String detail) {
           this.title = title;
           this.language = language;
           this.description = description;
           this.cover = cover;
           this.detail = detail;
//           author.addBook(this);

        }

    public String getTitle() { return title; }
    public String getCover() { return cover;}
        public String getDescription() { return description; }
        public String getDetail() { return detail; }
        public String getLanguage() { return language; }

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
