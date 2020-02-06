package com.example.my_book_store_back_end;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
public class Controller {
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private AuthorRepository authorRepository;

    public Author getAuthPlayer(Authentication authentication) {
        return authorRepository.findByEmail(authentication.getName());
    }

    @RequestMapping("/api/authors") // Return an API with all the books
    public Map<String, Object>  getAuthors() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("authors", authorRepository.findAll().stream().map(author -> AuthorDTO(author)).collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> AuthorDTO(Author author) {  // makes the Book DTO
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("firstName", author.getFirstName());
        dto.put("lastName", author.getLastName());
        dto.put("userName", author.getUserName());
        dto.put("email", author.getEmail());
        dto.put("books", author.getBooks().stream().map(book -> AuthorBookDTO(book)));

        return dto;
    }
    private Map<String, Object> AuthorBookDTO(Book book) {  // makes the Book DTO
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("cover", book.getCover());
        dto.put("detail", book.getDetail());
        dto.put("title", book.getTitle());
        dto.put("description", book.getDescription());
        dto.put("language", book.getLanguage());
        // ADD AUTHOR LATER ON
        return dto;
    }
    @RequestMapping("/api/books") // Return an API with all the books
    public Map<String, Object>  getAll(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();

        if (isGuest(authentication)) {
            dto.put("logged", null);
        } else {
            dto.put("logged", loginDTO(authentication));
        }
        dto.put("books", bookRepository.findAll().stream().map(book -> BookDTO(book)).collect(Collectors.toList()));
        return dto;
    }

    private Map<String, Object> BookDTO(Book book) {  // makes the Book DTO
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("cover", book.getCover());
        dto.put("detail", book.getDetail());
        dto.put("title", book.getTitle());
        dto.put("description", book.getDescription());
        dto.put("language", book.getLanguage());

        // ADD AUTHOR LATER ON
        return dto;
    }




    @RequestMapping(value= "/api/addBook", method = RequestMethod.POST) // Create a book
    public ResponseEntity<Map<String,Object>> newBook(@RequestBody Book book) {
        System.out.println("request received");
        // MUST CHECK IF USER IS AUTHOR BEFORE CREATING
        Book isBook = bookRepository.findByTitle(book.getTitle());

        if (isBook != null) {
        return new ResponseEntity<>(makeMap("error", "This book already exists"), HttpStatus.CONFLICT);
        }

            Book newBook = new Book(book.getTitle(), book.getLanguage(), book.getDescription(), book.getCover(), book.getDetail());
            bookRepository.save(newBook);
            return new ResponseEntity<>(makeMap("success", "New book added"), HttpStatus.ACCEPTED);


    }


    @RequestMapping(value = "/api/signup/author", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addAuthor(@RequestBody Author author) { // Add an Author
        Author isAuthor = authorRepository.findByEmail(author.getEmail());
        if (isAuthor != null) {
            return new ResponseEntity<>(makeMap("error","Author already exists"),HttpStatus.CONFLICT);
        }
            Author newAuthor = new Author(author.getFirstName(), author.getLastName(), author.getUserName(), author.getEmail(), author.getPassword());
            authorRepository.save(newAuthor);
            System.out.println("Author saved: " + newAuthor);
        return new ResponseEntity<>(makeMap("success","Author Added"),HttpStatus.CREATED);
    }
    private Map<String, Object> loginDTO(Authentication authentication) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("firstname", authorRepository.findByEmail(authentication.getName()).getFirstName());
        dto.put("lastname", authorRepository.findByEmail(authentication.getName()).getLastName());
        dto.put("username", authorRepository.findByEmail(authentication.getName()).getUserName());

        return dto;
    }
    private Map<String, Object> makeMap(String key, Object value) { // Makes the response sent with the response entity
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}