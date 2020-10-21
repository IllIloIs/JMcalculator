package javamentor;

        import java.util.Scanner;
        import java.util.regex.Matcher;
        import java.util.regex.Pattern;

class WrongInputException extends RuntimeException { // выбрал исключение с сообщением, чтобы подсказывать пользователю, где он ошибся
    WrongInputException(String message) {
        super(message);
    }
}

public class Calculator {
    Integer firstOperand;
    Integer secondOperand;
    Character operation;

    public Calculator(Integer firstOperand, Integer secondOperand, Character operation) {
        this.firstOperand = firstOperand;
        this.secondOperand = secondOperand;
        this.operation = operation;
    }

    public void operandChecker(Integer i) { // проверка корректности введенного числа на диапазон 1-10
        if (i < 0 || i > 10) {
            throw new WrongInputException("Ошибка во введённой информации! Убедитесь, что введены числа в диапазоне от 1 до 10 включительно.");
        }
    }

    public int doTheMath() { // выполнение калькулятором непосредственно расчётов
        int result;
        switch (operation) {
            case '+':
                result = firstOperand + secondOperand;
                break;
            case '-':
                result = firstOperand - secondOperand;
                break;
            case '*':
                result = firstOperand * secondOperand;
                break;
            case '/':
                result = firstOperand / secondOperand;
                break;
            default: // в случае, если введена другая операция
                throw new WrongInputException("Ошибка во введённой информации! Убедитесь, что вы ввели разрешённый символ математической операции: '+', '-', '*', '/'.");
        }
        return result;
    }

    public static int checkAndAssignOperatorIndex(Matcher matcher) {
        int matchingCounter = 0;
        int result = 0;
        while (matcher.find()) {
            matchingCounter += 1; // считаем, сколько математических знаков в строке
            switch (matchingCounter) {
                case 1:
                    result = matcher.start(); // присвоил переменной индекс, по которому находится символ мат.операции. Это будет разделитель операнд
                    break;
                case 0: // если ни одного или
                default: // если больше одного — выбрасываем исключение
                    throw new WrongInputException("Ошибка во введённой информации! Убедитесь, что вы ввели только один символ математической операции.");
            }
        }
        return result;
    }

    // далее два перегруженных метода для конвертации чисел из римских в арабские и обратно
    public static int swapNumericSystems(String inputNumber) {
        int result = 0;
        int[] arabic = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        String[] roman = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        for (int i = 0; i < roman.length; i++) {
            if (inputNumber.equals(roman[i])) {
                result = arabic[i];
                break;
            }
        }
        return result;
    }

    public static String swapNumericSystems(int inputNumber) {  // пригодится только для конвертации результата в римское число от 1 до 100
        StringBuilder result = new StringBuilder();
        while (inputNumber == 100) {
            result.append("C");
            inputNumber -= 100;
        }
        while (inputNumber >= 90) {
            result.append("XC");
            inputNumber -= 90;
        }
        while (inputNumber >= 50) {
            result.append("L");
            inputNumber -= 50;
        }
        while (inputNumber >= 40) {
            result.append("XL");
            inputNumber -= 40;
        }
        while (inputNumber >= 10) {
            result.append("X");
            inputNumber -= 10;
        }
        while (inputNumber >= 9) {
            result.append("IX");
            inputNumber -= 9;
        }
        while (inputNumber >= 5) {
            result.append("V");
            inputNumber -= 5;
        }
        while (inputNumber >= 4) {
            result.append("IV");
            inputNumber -= 4;
        }
        while (inputNumber >= 1) {
            result.append("I");
            inputNumber -= 1;
        }
        return result.toString();
    }

    public static boolean isOperandRoman(String operand) { // чекер типа числа. Пригодится перед вычислениями
        boolean result = false;
        String[] roman = {"I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X"};
        for (String s : roman) {
            if (operand.equals(s)) {
                result = true;
                break;
            }
        }
        return result;
    }

}

class RunCalculator {
    public static void main(String[] args) {
        System.out.println("Калькулятор умеет выполнять операции сложения, вычитания, умножения и деления с двумя римскими или арабскими числами от 1 до 10.");
        System.out.println("Введите выражение вида a + b, a - b, a * b или a / b");
        Scanner sc = new Scanner(System.in); // создал сканер консоли
        String input = sc.nextLine().replaceAll("\\s", "");// получил со сканера строку, очищенную от пробельных символов
        sc.close(); // закрыл сканер, ибо нечего есть ресурсы
        if (input.contains(".") || input.contains(",")) { // пресекаем попытки юзера ввести double
            throw new WrongInputException("Ошибка во введённой информации! Убедитесь, что введённые числа ЦЕЛЫЕ.");
        }
        Pattern operationSymbol = Pattern.compile("\\W"); //  регулярка для поиска символа мат. операции в строке. Не реагирует на римские цифры. Ок
        Matcher matcher = operationSymbol.matcher(input);
        int operationIndex = Calculator.checkAndAssignOperatorIndex(matcher); // получили индекс, где стоит наш единственный мат. оператор
        String firstOperandRaw = input.substring(0, operationIndex).toUpperCase();
        String secondOperandRaw = input.substring(operationIndex + 1).toUpperCase();
        int firstOperandFinal;
        int secondOperandFinal;
        int requiredResultType = 0;
        if (Calculator.isOperandRoman(firstOperandRaw) && Calculator.isOperandRoman(secondOperandRaw)) { // если оба введенных числа римские
            firstOperandFinal = Calculator.swapNumericSystems(firstOperandRaw);                          // - конвертим и присваиваем
            secondOperandFinal = Calculator.swapNumericSystems(secondOperandRaw);
            requiredResultType = 1;     // ставим маркер, что на инпуте работали с римскими и на выходе, соответственно, тоже нужны римские
        } else if (!Calculator.isOperandRoman(firstOperandRaw) && !Calculator.isOperandRoman(secondOperandRaw)) { // если оба числа арабские
            firstOperandFinal = Integer.parseInt(firstOperandRaw);                                                // — то берем их как есть
            secondOperandFinal = Integer.parseInt(secondOperandRaw);
        } else {
            throw new WrongInputException("Ошибка во введённой информации! Убедитесь, что оба числа ОДНОГО ТИПА (римские ИЛИ арабские).");
        }
        Calculator calculator = new Calculator(firstOperandFinal, secondOperandFinal, input.charAt(operationIndex));
        calculator.operandChecker(calculator.firstOperand); // проверка корректности введенного числа на диапазон 1-10
        calculator.operandChecker(calculator.secondOperand); // проверка корректности введенного числа на диапазон 1-10
        int calcResult = calculator.doTheMath(); // производим вычисления, на аутпуте int
        if (requiredResultType == 1) { // маркер подсказывает, с каким типом чисел мы работали и, соответственно, правит/не правит аутпут в консоль
            System.out.println(Calculator.swapNumericSystems(calcResult));
        } else {
            System.out.println(calcResult);
        }
    }
}

