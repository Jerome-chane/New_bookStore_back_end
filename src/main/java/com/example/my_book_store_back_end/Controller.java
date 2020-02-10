package com.example.my_book_store_back_end;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.ws.Response;
import java.util.*;
import java.util.stream.Collectors;

@RestController
public class Controller {
    private boolean isGuest(Authentication authentication) {
        return authentication == null || authentication instanceof AnonymousAuthenticationToken;
    }
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private PurchaseResository purchaseResository;


    public Person getAuthPerson(Authentication authentication) {
        return personRepository.findByEmail(authentication.getName());
    }

    @RequestMapping(value = "/purchase", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> purchase(@RequestBody Set<Book> books, Authentication authentication) {

        if(isGuest(authentication)){
            return new ResponseEntity<>(makeMap("error", "You must be logged is to make a purchase"), HttpStatus.UNAUTHORIZED);
        }
        Person customer = getAuthPerson(authentication);
        Date date = new Date();
        Set<Book> bookSet = new HashSet<>();
        for (Book book : books){
            bookSet.add(bookRepository.findByTitle(book.getTitle()));
        }
        Purchase newPurchase = new Purchase(customer,bookSet,date);
        purchaseResository.save(newPurchase);
        return new ResponseEntity<>(makeMap("success","Purchase realized"),HttpStatus.CREATED);
    }


    @RequestMapping("/api/customers") // Return an API with all the books
    public Map<String, Object>  getCustomers() {

        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Set<Person> customers = new HashSet<>();
        personRepository.findAll().stream().map(person ->{if (person.getRole().contentEquals("customer")){customers.add(person);}return customers;}).collect(Collectors.toList());
// check in the personRespository which Person as the role of "customer" and add these persons to the Set of Customers
        dto.put("customers", customers.stream().map(customer->CustomerDTO(customer)).collect(Collectors.toList()));
        return dto;
    }
    @RequestMapping("/api/authors") // Return an API with all the books
    public Map<String, Object>  getAuthors() {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Set<Person> authors = new HashSet<>();
        personRepository.findAll().stream().map(person ->{if (person.getRole().contentEquals("author")){authors.add(person);}return authors;}).collect(Collectors.toList());
        // check in the personRespository which Person as the role of "author" and add these persons to the Set of Authors
        dto.put("authors", authors.stream().map(author->AuthorDTO(author)).collect(Collectors.toList()));
        return dto;
    }
    private Map<String, Object> CustomerDTO(Person customer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("firstName", customer.getFirstName());
        dto.put("lastName", customer.getLastName());
        dto.put("userName", customer.getUserName());
        dto.put("email", customer.getEmail());
        dto.put("purchases", customer.getPurchases().stream().map(purchase ->PurchaseDTO(purchase)));
        return dto;
    }    private Map<String, Object> CustomerPurchaseDTO(Person customer) {
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("firstName", customer.getFirstName());
        dto.put("lastName", customer.getLastName());
        dto.put("userName", customer.getUserName());
//        dto.put("email", customer.getEmail());

        return dto;
    }
    private  Map<String, Object> PurchaseDTOforBooks(Purchase purchase){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", purchase.getId());
        dto.put("date", purchase.getDate());
        dto.put("customer",CustomerPurchaseDTO(purchase.getCustomer()));
        return dto;
    }
    private  Map<String, Object> PurchaseDTO(Purchase purchase){
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("id", purchase.getId());
        dto.put("date", purchase.getDate());
        dto.put("books", purchase.getBooks().stream().map(book -> PurchasedBookDTO(book)));
        return dto;
    }
    private Map<String, Object> PurchasedBookDTO(Book book) {  // makes the Book DTO
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("title", book.getTitle());
        dto.put("detail", book.getDetail());

        return  dto;
    }
    private Map<String, Object> AuthorDTOforBook(Person author) {  // makes the Book DTO
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        dto.put("firstName", author.getFirstName());
        dto.put("lastName", author.getLastName());
        dto.put("userName", author.getUserName());
        dto.put("email", author.getEmail());
        return dto;
    }

    private Map<String, Object> AuthorDTO(Person author) {  // makes the Book DTO
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
        dto.put("id", book.getId());
        dto.put("cover", book.getCover());
        dto.put("detail", book.getDetail());
        dto.put("title", book.getTitle());
        dto.put("description", book.getDescription());
        dto.put("language", book.getLanguage());
        dto.put("price", book.getPrice());

        if(book.getAuthor()==null) {
            dto.put("author", "unknown");
        }if(book.getAuthor()!=null) {
            dto.put("author", AuthorDTOforBook(book.getAuthor()));
        } if(book.getPurchases().size()==0){
            dto.put("purchases", "none");
        } if(book.getPurchases().size()>0){
            dto.put("purchases", book.getPurchases().stream().map(purchase -> PurchaseDTOforBooks(purchase)));
        }

        return dto;
    }


    @RequestMapping(value= "/api/addBook", method = RequestMethod.POST) // Create a book
    public ResponseEntity<Map<String,Object>> newBook(@RequestBody Book book, Authentication authentication) {

        Person person = getAuthPerson(authentication);
        if (isGuest(authentication) || !person.getRole().contentEquals("author")) { // Check is the current user is an Author
            return new ResponseEntity<>(makeMap("error", "You must be logged in as an Author to add a book"), HttpStatus.UNAUTHORIZED);
        }

        Book isBook = bookRepository.findByTitle(book.getTitle());
        if (isBook != null) {
        return new ResponseEntity<>(makeMap("error", "This book already exists"), HttpStatus.CONFLICT);
        }
            Book newBook = new Book(book.getTitle(), book.getLanguage(), book.getDescription(), book.getCover(), book.getDetail(),book.getPrice(),person);
            person.addBook(newBook);
            bookRepository.save(newBook);
            return new ResponseEntity<>(makeMap("success", "New book added"), HttpStatus.ACCEPTED);

    }



    @RequestMapping(value = "/api/signup", method = RequestMethod.POST)
    public ResponseEntity<Map<String, Object>> addPerson(@RequestBody Person person) { // Add a Perosn
        Person isPerson = personRepository.findByEmail(person.getEmail());
        if (isPerson != null) {
            return new ResponseEntity<>(makeMap("error","Person already exists"),HttpStatus.CONFLICT);
        }
        Person newPerson = new Person(person.getFirstName(), person.getLastName(), person.getUserName(), person.getEmail(),person.getRole() ,passwordEncoder.encode(person.getPassword()));
        personRepository.save(newPerson);

        return new ResponseEntity<>(makeMap("success","Person Added"),HttpStatus.CREATED);
    }


    private Map<String, Object> loginDTO(Authentication authentication) { // Loging DTO will check which user is logged in and will return the apropriate information
        Map<String, Object> dto = new LinkedHashMap<String, Object>();
        Person person = getAuthPerson(authentication);

        if(person != null) {
            dto.put("role", personRepository.findByEmail(authentication.getName()).getRole());
            dto.put("firstname", personRepository.findByEmail(authentication.getName()).getFirstName());
            dto.put("lastname", personRepository.findByEmail(authentication.getName()).getLastName());
            dto.put("username", personRepository.findByEmail(authentication.getName()).getUserName());
        }
        return dto;
    }
    private Map<String, Object> makeMap(String key, Object value) { // Makes the response sent with the response entity
        Map<String, Object> map = new HashMap<>();
        map.put(key, value);
        return map;
    }
}
