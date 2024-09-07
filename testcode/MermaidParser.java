package testcode;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class MermaidParser {
    public static void main(String[] args) {
        try {
            File inputFile = new File("input.txt"); // 输入文件名
            File outputFile = new File("output.txt"); // 输出文件名
            Scanner scanner = new Scanner(inputFile);
            FileWriter writer = new FileWriter(outputFile);
            StringBuilder javaCode = new StringBuilder();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("classDiagram")) {
                    // 忽略以classDiagram开头的行
                    continue;
                } else if (line.startsWith("class ")) {
                    // 解析class定义行
                    String className = line.substring(6).trim(); // 提取类名
                    javaCode.append("public class ").append(className).append(" {\n");
                } else if (line.contains(":")) {
                    // 解析成员行
                    String[] parts = line.split(":"); // 根据冒号分割
                    String memberType = parts[1].trim(); // 成员类型
                    String memberName = parts[0].substring(parts[0].lastIndexOf(" ") + 1).trim(); // 成员名
                    String accessModifier = memberType.startsWith("+") ? "public" : "private"; // 访问修饰符

                    javaCode.append("    ").append(accessModifier).append(" ").append(memberType).append(" ")
                            .append(memberName).append(";\n");
                }
            }

            scanner.close();
            javaCode.append("}\n");
            writer.write(javaCode.toString()); // 将Java代码写入输出文件
            writer.close();
            System.out.println("Java代码已成功转换并保存到output.txt文件中。");
        } catch (IOException e) {
            System.out.println("发生IO异常。");
            e.printStackTrace();
        }
    }
}
