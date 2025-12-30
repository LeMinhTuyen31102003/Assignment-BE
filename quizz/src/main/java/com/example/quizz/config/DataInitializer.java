package com.example.quizz.config;

import com.example.quizz.entity.*;
import com.example.quizz.enums.QuestionType;
import com.example.quizz.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Data Initializer
 * Seeds initial roles, admin user, and sample data on application startup
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    @Bean
    public CommandLineRunner initializeData(
            RoleRepository roleRepository, 
            UserRepository userRepository,
            QuizRepository quizRepository,
            QuestionRepository questionRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            log.info("========================================");
            log.info("Starting data initialization...");
            log.info("========================================");
            
            // 1. Create Roles
            Role userRole = createOrGetRole(roleRepository, "USER");
            Role adminRole = createOrGetRole(roleRepository, "ADMIN");
            
            // 2. Create Admin User
            User adminUser = createAdminUser(userRepository, passwordEncoder, adminRole, userRole);
            
            // 3. Create Sample Users
            List<User> sampleUsers = createSampleUsers(userRepository, passwordEncoder, userRole);
            
            // 4. Create Sample Quizzes with Questions
            if (quizRepository.count() == 0) {
                log.info("Creating sample quizzes...");
                
                // Save questions first
                List<Question> javaQuestions = createJavaQuestions(questionRepository);
                List<Question> springQuestions = createSpringQuestions(questionRepository);
                List<Question> dbQuestions = createDatabaseQuestions(questionRepository);
                
                // Then create quizzes and link questions
                Quiz javaQuiz = createQuiz("Java Programming Fundamentals", 
                    "Test your knowledge of core Java concepts including OOP, data types, and basic syntax", 
                    30, javaQuestions);
                javaQuiz = quizRepository.save(javaQuiz);
                log.info("Created quiz: {}", javaQuiz.getTitle());
                
                Quiz springQuiz = createQuiz("Spring Boot & REST API",
                    "Assessment covering Spring Boot basics, dependency injection, and RESTful web services",
                    45, springQuestions);
                springQuiz = quizRepository.save(springQuiz);
                log.info("Created quiz: {}", springQuiz.getTitle());
                
                Quiz dbQuiz = createQuiz("Database & SQL Fundamentals",
                    "Test your understanding of relational databases, SQL queries, and JPA",
                    40, dbQuestions);
                dbQuiz = quizRepository.save(dbQuiz);
                log.info("Created quiz: {}", dbQuiz.getTitle());
            }
            
            log.info("========================================");
            log.info("Data initialization completed");
            log.info("Total Users: {}", userRepository.count());
            log.info("Total Quizzes: {}", quizRepository.count());
            log.info("Total Questions: {}", questionRepository.count());
            log.info("========================================");
        };
    }
    
    private Role createOrGetRole(RoleRepository roleRepository, String roleName) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = Role.builder().name(roleName).build();
            role = roleRepository.save(role);
            log.info("Created role: {}", roleName);
            return role;
        }
        return roleRepository.findByName(roleName).orElseThrow();
    }
    
    private User createAdminUser(UserRepository userRepository, PasswordEncoder passwordEncoder, 
                                  Role adminRole, Role userRole) {
        String adminEmail = "admin@quiz.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User adminUser = User.builder()
                    .email(adminEmail)
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .roles(Set.of(adminRole, userRole))
                    .build();
            adminUser.setIsActive(true);
            adminUser = userRepository.save(adminUser);
            log.info("========================================");
            log.info("DEFAULT ADMIN USER CREATED:");
            log.info("Email: {}", adminEmail);
            log.info("Password: admin123");
            log.info("========================================");
            return adminUser;
        }
        return userRepository.findByEmail(adminEmail).orElseThrow();
    }
    
    private List<User> createSampleUsers(UserRepository userRepository, PasswordEncoder passwordEncoder, Role userRole) {
        List<User> users = new ArrayList<>();
        
        String[][] sampleUsers = {
            {"john.doe@example.com", "John Doe"},
            {"jane.smith@example.com", "Jane Smith"},
            {"bob.wilson@example.com", "Bob Wilson"}
        };
        
        for (String[] userData : sampleUsers) {
            if (!userRepository.existsByEmail(userData[0])) {
                User user = User.builder()
                        .email(userData[0])
                        .password(passwordEncoder.encode("password123"))
                        .fullName(userData[1])
                        .roles(Set.of(userRole))
                        .build();
                user.setIsActive(true);
                user = userRepository.save(user);
                users.add(user);
                log.info("Created user: {} ({})", user.getFullName(), user.getEmail());
            }
        }
        
        return users;
    }
    
    private Quiz createQuiz(String title, String description, int durationMinutes, List<Question> questions) {
        Quiz quiz = Quiz.builder()
                .title(title)
                .description(description)
                .durationMinutes(durationMinutes)
                .questions(new ArrayList<>())
                .build();
        quiz.setIsActive(true);
        for (Question q : questions) {
            quiz.addQuestion(q);
        }
        return quiz;
    }
    
    private List<Question> createJavaQuestions(QuestionRepository questionRepository) {
        List<Question> questions = new ArrayList<>();
        
        // Q1
        Question q1 = Question.builder()
                .content("What is the size of int data type in Java?")
                .type(QuestionType.SINGLE_CHOICE)
                .score(10)
                .answers(new ArrayList<>())
                .build();
        q1.setIsActive(true);
        q1.addAnswer(Answer.builder().content("8 bits").isCorrect(false).build());
        q1.addAnswer(Answer.builder().content("16 bits").isCorrect(false).build());
        q1.addAnswer(Answer.builder().content("32 bits").isCorrect(true).build());
        q1.addAnswer(Answer.builder().content("64 bits").isCorrect(false).build());
        questions.add(questionRepository.save(q1));
        
        // Q2
        Question q2 = Question.builder()
                .content("Which keyword is used to inherit a class in Java?")
                .type(QuestionType.SINGLE_CHOICE)
                .score(10)
                .answers(new ArrayList<>())
                .build();
        q2.setIsActive(true);
        q2.addAnswer(Answer.builder().content("implements").isCorrect(false).build());
        q2.addAnswer(Answer.builder().content("extends").isCorrect(true).build());
        q2.addAnswer(Answer.builder().content("inherits").isCorrect(false).build());
        q2.addAnswer(Answer.builder().content("super").isCorrect(false).build());
        questions.add(questionRepository.save(q2));
        
        // Q3
        Question q3 = Question.builder()
                .content("Which of the following are Java keywords? (Select all that apply)")
                .type(QuestionType.MULTIPLE_CHOICE)
                .score(15)
                .answers(new ArrayList<>())
                .build();
        q3.setIsActive(true);
        q3.addAnswer(Answer.builder().content("static").isCorrect(true).build());
        q3.addAnswer(Answer.builder().content("Boolean").isCorrect(false).build());
        q3.addAnswer(Answer.builder().content("void").isCorrect(true).build());
        q3.addAnswer(Answer.builder().content("main").isCorrect(false).build());
        questions.add(questionRepository.save(q3));
        
        return questions;
    }
    
    private List<Question> createSpringQuestions(QuestionRepository questionRepository) {
        List<Question> questions = new ArrayList<>();
        
        // Q1
        Question q1 = Question.builder()
                .content("What annotation is used to mark a class as a Spring Boot REST controller?")
                .type(QuestionType.SINGLE_CHOICE)
                .score(10)
                .answers(new ArrayList<>())
                .build();
        q1.setIsActive(true);
        q1.addAnswer(Answer.builder().content("@Controller").isCorrect(false).build());
        q1.addAnswer(Answer.builder().content("@RestController").isCorrect(true).build());
        q1.addAnswer(Answer.builder().content("@Component").isCorrect(false).build());
        q1.addAnswer(Answer.builder().content("@Service").isCorrect(false).build());
        questions.add(questionRepository.save(q1));
        
        // Q2
        Question q2 = Question.builder()
                .content("Which HTTP method is typically used for creating a new resource?")
                .type(QuestionType.SINGLE_CHOICE)
                .score(10)
                .answers(new ArrayList<>())
                .build();
        q2.setIsActive(true);
        q2.addAnswer(Answer.builder().content("GET").isCorrect(false).build());
        q2.addAnswer(Answer.builder().content("POST").isCorrect(true).build());
        q2.addAnswer(Answer.builder().content("PUT").isCorrect(false).build());
        q2.addAnswer(Answer.builder().content("DELETE").isCorrect(false).build());
        questions.add(questionRepository.save(q2));
        
        // Q3
        Question q3 = Question.builder()
                .content("Select all valid Spring Boot annotations for dependency injection:")
                .type(QuestionType.MULTIPLE_CHOICE)
                .score(15)
                .answers(new ArrayList<>())
                .build();
        q3.setIsActive(true);
        q3.addAnswer(Answer.builder().content("@Autowired").isCorrect(true).build());
        q3.addAnswer(Answer.builder().content("@Inject").isCorrect(true).build());
        q3.addAnswer(Answer.builder().content("@Dependency").isCorrect(false).build());
        q3.addAnswer(Answer.builder().content("@Resource").isCorrect(true).build());
        questions.add(questionRepository.save(q3));
        
        return questions;
    }
    
    private List<Question> createDatabaseQuestions(QuestionRepository questionRepository) {
        List<Question> questions = new ArrayList<>();
        
        // Q1
        Question q1 = Question.builder()
                .content("Which SQL clause is used to filter records?")
                .type(QuestionType.SINGLE_CHOICE)
                .score(10)
                .answers(new ArrayList<>())
                .build();
        q1.setIsActive(true);
        q1.addAnswer(Answer.builder().content("SELECT").isCorrect(false).build());
        q1.addAnswer(Answer.builder().content("WHERE").isCorrect(true).build());
        q1.addAnswer(Answer.builder().content("FROM").isCorrect(false).build());
        q1.addAnswer(Answer.builder().content("HAVING").isCorrect(false).build());
        questions.add(questionRepository.save(q1));
        
        // Q2
        Question q2 = Question.builder()
                .content("What does ACID stand for in database transactions?")
                .type(QuestionType.SINGLE_CHOICE)
                .score(15)
                .answers(new ArrayList<>())
                .build();
        q2.setIsActive(true);
        q2.addAnswer(Answer.builder().content("Atomicity, Consistency, Isolation, Durability").isCorrect(true).build());
        q2.addAnswer(Answer.builder().content("Association, Composition, Integration, Dependency").isCorrect(false).build());
        q2.addAnswer(Answer.builder().content("Abstract, Concrete, Interface, Data").isCorrect(false).build());
        q2.addAnswer(Answer.builder().content("Access, Control, Index, Database").isCorrect(false).build());
        questions.add(questionRepository.save(q2));
        
        return questions;
    }
}
