package com.example.configuration;

import com.example.model.*;
import com.example.repository.CourseRepository;
import com.example.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    // @Bean  
    @Profile("!prod")
    public CommandLineRunner initData() {
        return args -> {
            // Create a teacher user if not exists
            User teacher = userRepository.findByUsername("teacher");
            if (teacher == null) {
                teacher = User.builder()
                        .name("Teacher")
                        .fullName("John Smith")
                        .username("teacher")
                        .password("$2a$10$mR4MU5esBbUd6JWuwFKQV.1sDwVWM.vEe8RqIXLJ1RhiMQsfHNgQi") // "password"
                        .email("teacher@example.com")
                        .userRole(UserRole.TEACHER)
                        .emailVerified(true)
                        .build();
                teacher = userRepository.save(teacher);
            }

            // Course definitions
            String[][] courseDetails = {
                // name, category, price, isFree, summary, description, thumbnail
                {"Web Development Fundamentals", "Programming", "49.99", "false", 
                 "Learn the fundamentals of web development including HTML, CSS, and JavaScript.",
                 "This comprehensive course covers all the essential skills needed to build modern websites. You'll start with HTML structure, move to CSS styling, and finish with JavaScript interactivity. By the end, you'll be able to create responsive websites from scratch.",
                 "https://example.com/images/webdev.jpg"},
                
                {"Data Science Bootcamp", "Data Science", "99.99", "false",
                 "Master data analysis, visualization, and machine learning fundamentals.",
                 "This bootcamp will take you from data science novice to practitioner. Learn Python, pandas, NumPy, and scikit-learn to analyze data, create visualizations, and build predictive models. Work on real-world projects to build your portfolio.",
                 "https://example.com/images/datascience.jpg"},
                
                {"Introduction to Spring Boot", "Java", "79.99", "false",
                 "Build robust Java applications with Spring Boot framework.",
                 "Get up and running with Spring Boot quickly. Learn how to create RESTful APIs, connect to databases, handle security, and deploy your applications. This course covers the core concepts of Spring Boot with practical hands-on exercises.",
                 "https://example.com/images/springboot.jpg"},
                
                {"UI/UX Design Principles", "Design", "0", "true",
                 "Learn essential design principles for creating intuitive user interfaces.",
                 "This course covers the fundamentals of UI/UX design including color theory, typography, layout, interaction design, and user research. Learn to create wireframes, prototypes, and conduct usability testing for your designs.",
                 "https://example.com/images/uiux.jpg"},
                
                {"Mobile App Development with Flutter", "Mobile Development", "89.99", "false",
                 "Create beautiful, natively compiled applications for mobile from a single codebase.",
                 "Learn Flutter from the ground up and build cross-platform mobile apps for iOS and Android. This course covers Dart programming language, Flutter widgets, state management, and how to connect to backend services.",
                 "https://example.com/images/flutter.jpg"},
                
                {"Cybersecurity Fundamentals", "Security", "69.99", "false",
                 "Learn essential cybersecurity concepts and practices to protect digital assets.",
                 "This course introduces you to the world of cybersecurity. Learn about common vulnerabilities, threat actors, encryption, network security, and best practices for securing systems and data against various cyber threats.",
                 "https://example.com/images/cybersecurity.jpg"},
                
                {"Cloud Computing with AWS", "Cloud", "109.99", "false",
                 "Master Amazon Web Services (AWS) cloud platform and services.",
                 "Get hands-on experience with AWS cloud services including EC2, S3, Lambda, DynamoDB, and more. Learn how to architect, deploy, and scale applications in the cloud following best practices and security guidelines.",
                 "https://example.com/images/aws.jpg"},
                
                {"Graphic Design Essentials", "Design", "59.99", "false",
                 "Master essential design tools and techniques for visual communication.",
                 "Learn the principles of graphic design and how to use industry-standard tools like Adobe Photoshop, Illustrator, and InDesign. Create logos, marketing materials, digital art, and understand visual composition, typography, and color theory.",
                 "https://example.com/images/graphicdesign.jpg"}
            };
            
            // Create and save courses
            List<Course> allCourses = new ArrayList<>();
            
            for (String[] details : courseDetails) {
                Course course = Course.builder()
                        .name(details[0])
                        .category(details[1])
                        .price(new BigDecimal(details[2]))
                        .isFree(Boolean.parseBoolean(details[3]))
                        .isDraft(false)
                        .summary(details[4])
                        .description(details[5])
                        .thumbnailUrl(details[6])
                        .teacher(teacher)
                        .weeks(new ArrayList<>())
                        .build();
                
                allCourses.add(course);
            }
            
            // Save all courses first
            courseRepository.saveAll(allCourses);
            
            // Week titles and descriptions for different courses
            String[][][] courseWeeks = {
                // Web Development
                {
                    {"HTML Foundations", "Learn the basics of HTML and document structure"},
                    {"CSS Styling", "Learn how to style HTML elements with CSS"},
                    {"JavaScript Basics", "Introduction to JavaScript programming"}
                },
                // Data Science
                {
                    {"Python Fundamentals", "Introduction to Python programming language"},
                    {"Data Analysis with Pandas", "Learn data manipulation and analysis with pandas library"},
                    {"Machine Learning Basics", "Introduction to machine learning algorithms and techniques"}
                },
                // Spring Boot
                {
                    {"Spring Boot Basics", "Introduction to Spring Boot framework"},
                    {"RESTful API Development", "Building RESTful APIs with Spring Boot"}
                },
                // UI/UX Design
                {
                    {"Design Principles", "Fundamental principles of good design"},
                    {"Wireframing and Prototyping", "Creating wireframes and interactive prototypes"}
                },
                // Flutter
                {
                    {"Dart Programming", "Introduction to Dart programming language"},
                    {"Flutter Widgets", "Building UI with Flutter widgets"},
                    {"State Management", "Managing state in Flutter applications"}
                },
                // Cybersecurity
                {
                    {"Security Fundamentals", "Basic concepts of information security"},
                    {"Network Security", "Protecting network infrastructure and communications"}
                },
                // AWS
                {
                    {"AWS Core Services", "Introduction to fundamental AWS services"},
                    {"Serverless Computing", "Building serverless applications with AWS Lambda"},
                    {"AWS Security", "Implementing security best practices in AWS"}
                },
                // Graphic Design
                {
                    {"Design Fundamentals", "Basic principles of graphic design"}
                }
            };
            
            // Learning item titles by type
            String[][] learningItemsByType = {
                // VIDEO
                {
                    "Introduction to %s",
                    "Understanding %s Concepts",
                    "Advanced %s Techniques",
                    "%s in Practice",
                    "Getting Started with %s"
                },
                // EXERCISE
                {
                    "%s Practice Exercise",
                    "Hands-on %s Project",
                    "%s Challenge",
                    "Building with %s",
                    "Implementing %s"
                },
                // DOCUMENT
                {
                    "%s Reference Guide",
                    "%s Documentation",
                    "%s Best Practices",
                    "Understanding %s",
                    "%s Cheat Sheet"
                },
                // QUIZ
                {
                    "%s Knowledge Check",
                    "Test Your %s Knowledge",
                    "%s Concepts Quiz",
                    "%s Assessment",
                    "%s Review Quiz"
                }
            };
            
            // Topics for learning items
            String[] topics = {
                "HTML", "CSS", "JavaScript", "Python", "Data Analysis",
                "Spring Boot", "API Design", "UI Design", "Flutter", "Dart",
                "Security", "Networks", "AWS", "Cloud Computing", "Design Principles",
                "Typography", "Color Theory", "Machine Learning", "Algorithms"
            };
            
            // Add weeks and learning items to each course
            for (int i = 0; i < allCourses.size(); i++) {
                Course course = allCourses.get(i);
                
                // Determine number of weeks for this course (1-3)
                int numWeeks = Math.min(random.nextInt(3) + 1, courseWeeks[i].length);
                List<Week> weeks = new ArrayList<>();
                
                for (int w = 0; w < numWeeks; w++) {
                    Week week = Week.builder()
                            .title(courseWeeks[i][w][0])
                            .description(courseWeeks[i][w][1])
                            .weekNumber(w + 1)
                            .course(course)
                            .learningItems(new ArrayList<>())
                            .build();
                    
                    // Determine number of learning items for this week (1-3)
                    int numItems = random.nextInt(3) + 1;
                    List<LearningItem> items = new ArrayList<>();
                    
                    for (int li = 0; li < numItems; li++) {
                        // Randomly select learning item type
                        LearningItemType type = LearningItemType.values()[random.nextInt(LearningItemType.values().length)];
                        
                        // Select random topic relevant to the course
                        String topic = topics[random.nextInt(topics.length)];
                        
                        // Select title template based on type
                        String titleTemplate = learningItemsByType[type.ordinal()][random.nextInt(learningItemsByType[type.ordinal()].length)];
                        String title = String.format(titleTemplate, topic);
                        
                        // Create content based on type
                        String content;
                        if (type == LearningItemType.VIDEO) {
                            content = "https://example.com/videos/" + topic.toLowerCase().replace(" ", "-") + ".mp4";
                        } else if (type == LearningItemType.DOCUMENT) {
                            content = "This document covers important concepts and techniques related to " + topic + ".";
                        } else if (type == LearningItemType.QUIZ) {
                            content = "Test your knowledge of " + topic + " concepts and applications.";
                        } else { // EXERCISE
                            content = "Practice applying your " + topic + " skills with this hands-on exercise.";
                        }
                        
                        LearningItem item = LearningItem.builder()
                                .title(title)
                                .type(type)
                                .content(content)
                                .durationMinutes(10 + random.nextInt(50)) // 10-60 minutes
                                .orderIndex(li + 1)
                                .week(week)
                                .build();
                        
                        items.add(item);
                    }
                    
                    week.setLearningItems(items);
                    weeks.add(week);
                }
                
                course.setWeeks(weeks);
                courseRepository.save(course);
            }
            
            System.out.println("Mock data loaded successfully: 8 courses with 1-3 weeks each, and 1-3 learning items per week");
        };
    }
} 