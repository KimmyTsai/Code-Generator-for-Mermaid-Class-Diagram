package testcode;
import java.util.ArrayList;

public class OutputGenerator {
    public static void main(String[] args) {
        ArrayList<String> paramArray = new ArrayList<>();
        paramArray.add("int color");
        paramArray.add("int size");
        paramArray.add("int width");

        StringBuilder output = new StringBuilder();
        for (int i = 0; i < paramArray.size(); i++) {
            String param = paramArray.get(i);
            String[] parts = param.split("\\s+");
            String type = parts[0]; // 参数类型
            String name = parts[1]; // 参数名称
            String capitalized = name.substring(0, 1).toUpperCase() + name.substring(1); // 参数名称首字母大写

            // 生成输出语句
            output.append("this.").append(name).append(" = ").append(name).append(";");
            if (i < paramArray.size() - 1) {
                output.append("\n");
            }
        }

        System.out.println(output.toString());
    }
}
