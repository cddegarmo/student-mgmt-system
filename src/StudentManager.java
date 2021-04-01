import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class StudentManager {
   // Enforce singleton
   private static final StudentManager sm = new StudentManager();

   // Keep track of total enrollment
   private static final int MAX_STUDENTS = 2000;
   private static int numOfStudents = 0;
   private static final int COURSE_FEE = 600;
   private static final int ID = 1000;
   private static final Logger logger = Logger.getLogger(Student.class.getName());

   // Nested class purely for formatting
   private static class StudentFormatter {
      private ResourceBundle resource = ResourceBundle.getBundle("students");

      private String formatStudent(Student student) {
         return MessageFormat.format(resource.getString("student.format"),
                                     student.getLastName(),
                                     student.getFirstName(),
                                     student.getYearName(student.getYear()),
                                     student.getTuitionBalance());
      }
   }

   public final List<Student> students = new ArrayList<>();
   private final StudentFormatter sf = new StudentFormatter();
   private final ResourceBundle config = ResourceBundle.getBundle("config");
   private final MessageFormat studentFormat = new MessageFormat(config.getString("student.data"));
   private final Path dataFolder = Path.of(config.getString("data.folder"));

   private StudentManager() {}

   public static StudentManager getInstance() {
      return sm;
   }

   private Student parseStudent(String text) {
      Student student = null;
      try {
         Object[] values = studentFormat.parse(text);
         String firstName = (String) values[0];
         String lastName = (String) values[1];
         int year = Integer.parseInt((String) values[2]);
         student = new Student(firstName, lastName, year);
      } catch (ParseException e) {
         logger.log(Level.WARNING, "Error parsing student " + e.getMessage(), e);
      }
      return student;
   }

   public void loadStudent() {
      try (BufferedReader in = new BufferedReader(new FileReader(config.getString("data.file")))) {
         String line = null;
         while ((line = in.readLine()) != null)
            students.add(parseStudent(line));
      } catch (IOException e) {
         logger.log(Level.SEVERE, "Error loading student " + e.getMessage(), e);
      }
   }
}

