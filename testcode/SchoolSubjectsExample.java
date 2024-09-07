package testcode;
import java.util.*;

//透過HashMap同時使用List
public class SchoolSubjectsExample {
    public static void main(String[] args) {
        Map<String, List<String>> subjectsToStudents = new HashMap<>();

        // 直接新增一個arraylist
        List<String> mathStudents = new ArrayList<>();
        mathStudents.add("Alice");
        mathStudents.add("Bob");
        subjectsToStudents.put("Math", mathStudents);

        // iterate全部的值
        System.out.println("School Subjects and Student Lists:");
        for (Map.Entry<String, List<String>> entry : subjectsToStudents.entrySet()) {
            String subject = entry.getKey();
            List<String> students = entry.getValue();
            System.out.println(subject + " students: " + students);
        }

        // 向已經存在的key新增value
        if (subjectsToStudents.containsKey("Math")) {
            subjectsToStudents.get("Math").add("Eva");
        }
    }
}
