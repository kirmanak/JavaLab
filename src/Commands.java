import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.Vector;

/**
 * Enumation команд, использующихся в программе
 */
enum Commands {
    /**
     * Удаляет элемент из коллекции
     */
    remove {
        public String toString() {
            return "remove element - удалить элемент под номером element.\nremove_last - удалить последний элемент.\n";
        }

        public void doIt(int index) {
            index--;
            if (index < TextGenerator.collection.size() && index >= 0) {
                TextGenerator.collection.remove(index);
                System.out.println(index + 1 + "-й элемент удалён.");
            } else {
                System.out.println("Нет такого элемента.");
            }
        }

        public void doIt() {
            System.err.println("Нужен номер элемента.");
        }
    },
    /**
     * Печатает справку по командам приложения
     */
    help {
        public String toString() {
            String help = "Я могу следующее:\nhelp - напечатать эту справку.";
            for (Commands commands : Commands.values()) {
                if (!commands.equals(Commands.help)) {
                    help = help.concat(commands.toString());
                }
            }
            return help;
        }

        public void doIt() {
            System.out.println(toString());
        }
    },
    /**
     * Записывает коллекцию в JSON-файл.
     */
    save {
        public String toString() {
            return "save - сохранить коллекцию в JSON-файл.\n";
        }

        public void doIt() {
            try (PrintWriter pw = new PrintWriter(new File(TextGenerator.jsonFile))) {
                pw.flush();
                pw.print(TextGenerator.gson.toJson(TextGenerator.collection));
            } catch (IOException | NullPointerException err) {
                System.err.println("Файл вывода не найден. Не буду ничего писать.");
            }
        }
    },
    /**
     * Считывает коллекцию из JSON-файла
     */
    load {
        public String toString() {
            return "load - считать коллекцию из JSON-файла.\n";
        }

        public void doIt() {
            try (BufferedReader br = new BufferedReader(new FileReader(TextGenerator.jsonFile))) {
                String read = br.readLine();
                if (!(read == null || read.equals("null") || read.isEmpty())) {
                    TextGenerator.collection.clear();
                    try {
                        TextGenerator.collection = TextGenerator.gson.fromJson(read, new TypeToken<Vector<Adults>>() {
                        }.getType());
                    } catch (JsonSyntaxException err) {
                        System.err.println("Ошибка чтения коллекции из файла.");
                    }
                } else System.out.println("Файл пуст");
            } catch (IOException | NullPointerException err) {
                System.err.println("Файл ввода не найден. Не буду ничего читать.");
            }
        }
    },
    /**
     * Печатает содержимое коллекции и количество элементов в ней
     */
    print {
        public String toString() {
            return "print - напечатать коллекцию.\n";
        }

        public void doIt() {
            System.out.println("В коллекции " + TextGenerator.collection.size() + " элемент(ов)(а):");
            TextGenerator.collection.forEach(adult -> System.out.println(adult.toString()));
        }
    },
    /**
     * Выход из программы
     */
    exit {
        public String toString() {
            return "exit - сохранить и выйти.\n";
        }

        public void doIt() {
            System.exit(0);
        }
    },
    /**
     * Добавляет нового человека
     */
    add {
        public String toString() {
            return "add - добавить нового человека.\n";
        }

        public void doIt() {
            System.out.print("Введите имя: ");
            String name = TextGenerator.scanner.nextLine();
            System.out.print("Введите характер: ");
            String character = TextGenerator.scanner.nextLine();
            System.out.print("Введите местонахождение: ");
            String location = TextGenerator.scanner.nextLine();
            System.out.print("Введите время: ");
            String time = TextGenerator.scanner.nextLine();
            System.out.println("Степени родства: ");
            for (Relative relative : Relative.values()) {
                System.out.print(relative.name() + " ");
            }
            System.out.println();
            Adults adult = null;
            while (adult == null) {
                System.out.print("Введите степень родства: ");
                String relative = TextGenerator.scanner.nextLine();
                try {
                    adult = new Adults(name, character, new Location(location), time, Relative.valueOf(relative));
                } catch (IllegalArgumentException err) {
                    System.err.println("Не могу понять. Если что, надо на английском.");
                }
            }
            TextGenerator.collection.add(adult);
        }
    };

    public void doIt() {
        System.err.println("Что-то пошло не так.");
    }

    public void doIt(int i) {
        System.err.println("Что-то пошло не так.");
    }
}
