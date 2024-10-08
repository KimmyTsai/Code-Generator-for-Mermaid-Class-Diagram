# Code Generator for Mermaid Class Diagram

## 介紹

Code Generator for Mermaid Class Diagram 是一個工具，可以將使用者輸入的Mermaid類圖語法轉換成對應的Java程式碼。此工具可幫助您快速將UML類圖轉換為實際的Java代碼，節省開發時間。

## 功能

- 將Mermaid類圖的語法轉換成Java程式碼。
- 自動生成對應的類、屬性及方法。

## 安裝

1. 確保您已安裝Java環境。
2. 將此專案的源代碼克隆至本地：

    ```bash
    git clone https://github.com/your-repo/code-generator.git
    ```

3. 編譯並運行：

    ```bash
    javac CodeGenerator.java
    java CodeGenerator
    ```

## 使用方式

1. 輸入一段Mermaid類圖語法，如下所示：

    ```mermaid
    classDiagram
    class BankAccount
    BankAccount : -String owner
    BankAccount : -int balance
    BankAccount : +setOwner(String owner) void
    BankAccount : +isEnough(int value, int balance) void
    BankAccount : +getOwner() String
    ```

2. 程式將自動轉換為對應的Java類程式碼：

    ```java
    public class BankAccount {
        private String owner;
        private int balance;

        public void setOwner(String owner) {
            this.owner = owner;
        }

        public void isEnough(int value, int balance) {
            // Implementation of isEnough method here
        }

        public String getOwner() {
            return owner;
        }
    }
    ```
