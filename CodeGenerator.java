import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.*;;

public class CodeGenerator {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.err.println("請輸入mermaid檔案名稱");           
        }
        else {
            // get input
            String fileName = args[0];

            FileReaderHelper mermaidCodeReader = new FileReaderHelper(); //FileReaderHelper為一個自訂Class，為了與FileReader做區隔
            mermaidCodeReader.read(fileName);
        }
    }
}

class FileReaderHelper {
    //@SuppressWarnings("static-access")  //告訴編譯器忽略static
    public int lineNumber;
    public int lineOrder = 1;

    public void read(String fileName) {
        //System.out.println(lineOrder);
        
        Parser writeLine = new Parser();
        try {
            lineNumber = (int) Files.lines(Paths.get(fileName)).count();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            String mode = "1"; //mode 1為正常的冒號 mode 2 為大括號
            while ((line = reader.readLine()) != null) {
                if(lineOrder == 1) mode = "1";
                mode = writeLine.splitByClass(line, lineOrder, lineNumber, mode);
                //System.out.println(mode);
                lineOrder++;
            }

        } catch (IOException e) {
            System.err.println("Error:" + e.getMessage());
        }
    }
}

class Parser { //分析器

    static ArrayList<String> outputName = new ArrayList<>();
    static String output;
    
    public String splitByClass(String input, int lineOrder, int lineNumber, String mode) { //需要static?
        try {
            int fileIsNew = 0;
            if (input.indexOf("class") != -1 && input.indexOf("classDiagram") == -1 && input.indexOf(":") == -1){
                if(lineOrder != 2){ //讓第一個檔案的結尾有右大括號
                    BufferedWriter endReader = new BufferedWriter(new FileWriter(output, true));
                    //endReader.write("}");
                    endReader.close();
                }
                if(input.indexOf("class") != -1 && mode.equals("2")) {;}
                else if(input.indexOf("{") == -1) output = input.substring(input.indexOf("class")+6).trim() + ".java";
                else output = input.substring(input.indexOf("class")+6,input.indexOf("{")).trim() + ".java";
                if(!outputName.contains(output)){
                    outputName.add(output);
                    fileIsNew = 1;
                }
            }
            if(mode == "1" && input.indexOf(":") != -1){
                int colonSite = input.indexOf(":");
                output = input.substring(0, colonSite).trim() + ".java";
            }

            if(lineOrder == 1) return mode;
            File file = new File(output);

            if(input.indexOf("class") != -1 && fileIsNew == 1){
                BufferedWriter clear = new BufferedWriter(new FileWriter(output, false)); //清空輸出檔案
                clear.write("");
                clear.close();
            }
            

            if (!file.exists()) {
                file.createNewFile();
            }
            //System.out.println(lineOrder);
            if(input.indexOf("classDiagram") != -1){
                //System.out.println("Start");
                return mode;
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            //開始輸出

            if (input.indexOf("class") != -1 && input.indexOf(":") == -1 && input.indexOf("classDiagram") == -1 && mode.equals("1")){
                String className;
                if(input.indexOf("{") == -1) className = input.substring(input.indexOf("class")+6).trim();

                else{
                    className = input.substring(input.indexOf("class")+6,input.indexOf("{")).trim();
                    mode = "2";
                }
                if(fileIsNew == 1) bw.write("public class " + className + " {\n");
                
            }
            else if(input.indexOf(":") != -1 || mode.equals("2")){ //沒有class
                //int site = input.indexOf(":");
                if(input.indexOf("+") != -1){ //public
                    int site2 = input.indexOf("+"); //加的位置
                    int site3 = input.indexOf("(",site2);
                    String publicObject;

                    if(input.indexOf("(") == -1){ //無括號，代表只有單純的物件
                        publicObject = input.substring(site2+1).trim();

                        bw.write("    public " + publicObject + ";\n");
                    }
                    else{
                        publicObject = input.substring(site2+1,site3);
                        Pattern setPattern = Pattern.compile("set");    //setter與getter判定
                        Matcher setMatcher = setPattern.matcher(input);
                        Pattern getPattern = Pattern.compile("get");
                        Matcher getMatcher = getPattern.matcher(input);

                        Pattern contentPattern = Pattern.compile("\\((.*?)\\)(.*?)$");
                        Matcher contentMatcher = contentPattern.matcher(input);
                        if (contentMatcher.find()) {
                            String contentInParentheses = contentMatcher.group(1).replaceAll("\\s+", " ").trim(); //括號中內容
                            contentInParentheses = contentInParentheses.replaceAll("\\s*,\\s*", ", ").replaceAll("\\s*\\(\\s*", "(").replaceAll("\\s*\\)\\s*", ")");
                            String rightParenthesis = contentMatcher.group(2).replaceAll("\\s+", "").trim(); //右括號內容

                            // 去除多餘空格
                            ArrayList<String> parameters = new ArrayList<>();
                            String[] paramArray = contentInParentheses.split(",");  //參數列表
                            for (String param : paramArray) {
                                String cleanedParam = param.trim().replaceAll("\\s+", " ");
                                parameters.add(cleanedParam);
                            }
                            //System.out.println("參數列表: " + parameters);
                            if(setMatcher.find()){ //setter
                                bw.write("    public void " + publicObject + "(" + contentInParentheses + ") {\n");
                                for (int i = 0; i < parameters.size(); i++) {
                                    String param = parameters.get(i);
                                    String[] parts = param.split("\\s+");
                                    //String type = parts[0]; // 參數類型
                                    String name = parts[1]; // 參數名稱
                                    //String capitalized = name.substring(0, 1).toUpperCase() + name.substring(1); // 參數名稱開頭大寫
                        
                                    // 生成輸出
                                    bw.write("        this." + name + " = " + name + ";\n");
                                    if (i == parameters.size() - 1) {
                                        bw.write("    }\n");
                                    }
                                }
                            }
                            else if(getMatcher.find()){  //getter
                                bw.write("    public " + rightParenthesis + " " + publicObject + "() {\n");
                                String returnString = publicObject.substring(publicObject.indexOf("get")+3).trim();
                                returnString = returnString.substring(0, 1).toLowerCase() + returnString.substring(1);
                                bw.write("        return " + returnString + ";\n    }\n");
                            }
                            else{
                                bw.write("    public " + rightParenthesis + " " + publicObject + "(" + contentInParentheses + ")");
                                if(rightParenthesis.indexOf("int") != -1) bw.write(" {return 0;}\n");
                                else if(rightParenthesis.indexOf("boolean") != -1) bw.write(" {return false;}\n");
                                else if(rightParenthesis.indexOf("String") != -1) bw.write(" {return ;}\n");
                                else bw.write(" {;}\n");
                            }
                        }

                    }
                }
                else if(input.indexOf("-") != -1){ //private
                    int site2 = input.indexOf("-"); //減的位置
                    if(input.indexOf("(") == -1){ //無括號，代表只有單純的物件
                        String privateObject = input.substring(site2+1).replaceAll("\\s+", " ").trim();
                        bw.write("    " + "private " + privateObject + ";\n");
                    } 
                }

            }
            
                
            if(input.indexOf("}") != -1) mode = "1";
            bw.close();

            if(lineOrder == lineNumber){
                for (String element : outputName) {
                    BufferedWriter endWriter = new BufferedWriter(new FileWriter(element, true));
                    endWriter.write("}");
                    endWriter.close();
                }
            }
            
            return mode;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }  
    }
}