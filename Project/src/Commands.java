import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;
import java.util.Vector;

/**
 * Enumeration команд, использующихся в программе
 */
enum Commands implements Command {
    /** Удаляет элемент из коллекции */

    remove {
        public String toString() {
            return "remove element - удалить элемент под номером element.\nremove_last - удалить последний элемент.\n";
        }

        @Override
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
    /** Печатает справку по командам приложения */
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
    /** Записывает коллекцию в JSON-файл. */
    save {
        public String toString() {
            return "save - сохранить коллекцию в JSON-файл.\n";
        }

        public void doIt() {
            try (PrintWriter pw = new PrintWriter(new File(jsonFile))) {
                pw.flush();
                pw.print(gson.toJson(TextGenerator.collection));
            } catch (IOException | NullPointerException err) {
                System.err.println("Файл вывода не найден. Не буду ничего писать. $jsonFile = " + jsonFile);
            }
        }
    },
    /** Считывает коллекцию из JSON-файла */
    load {
        public String toString() {
            return "load - считать коллекцию из JSON-файла.\n";
        }

        public void doIt() {
            try (BufferedReader br = new BufferedReader(new FileReader(jsonFile))) {
                String read = br.readLine();
                if (!(read == null || read.isEmpty())) {
                    try {
                        TextGenerator.collection = gson.fromJson(read, new TypeToken<Vector<Humans>>() {
                        }.getType());
                    } catch (JsonSyntaxException err) {
                        System.err.println("Ошибка чтения коллекции из файла.");
                    }
                } else System.out.println("Файл пуст");
            } catch (IOException | NullPointerException err) {
                System.err.println("Файл ввода не найден. Не буду ничего читать. $jsonFile = " + jsonFile);
            }
        }
    },
    /** Печатает содержимое коллекции и количество элементов в ней */
    print {
        public String toString() {
            return "print - напечатать коллекцию.\n";
        }

        public void doIt() {
            System.out.println("В коллекции " + TextGenerator.collection.size() + " элемент(ов)(а):");
            TextGenerator.collection.forEach(human -> System.out.println(human.toString()));
        }
    },
    /** Выход из программы */
    exit {
        public String toString() {
            return "exit - сохранить и выйти.\n";
        }

        public void doIt() {
            System.exit(0);
        }
    },
    /** Добавляет нового человека */
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
            Humans human = null;
            while (human == null) {
                System.out.print("Введите степень родства: ");
                String relative = TextGenerator.scanner.nextLine();
                try {
                    human = new Humans(name, character, new Location(location), time, Relative.valueOf(relative));
                } catch (IllegalArgumentException err) {
                    System.err.println("Не могу понять. Если что, надо на английском.");
                }
            }
            TextGenerator.collection.add(human);
        }
    },
    /** Генерирует новых людей */
    generate {
        public String toString() {
            return "generate - сгенерировать нового человека на основе данных, заложенных разработчиком" +
                    "(может быть довольно весело)";
        }

        private Humans generate() {
            String name = names[randomize.nextInt(names.length)];
            String time = times[randomize.nextInt(times.length)];
            String character = characters[randomize.nextInt(characters.length)];
            Location location = new Location(locations[randomize.nextInt(locations.length)]);
            Relative relative = Relative.values()[randomize.nextInt(Relative.values().length)];
            return new Humans(name, character, location, time, relative);
        }

        public void doIt() {
            System.err.println("Нужно указать количество элементов, которое нужно сгенерировать.");
        }

        public void doIt(int amountOfElements) {
            if (amountOfElements >= 0 && amountOfElements <= 100) {
                for (int i = 0; i < amountOfElements; i++) {
                    TextGenerator.collection.add(generate());
                }
                System.out.println("Сгенерировал " + amountOfElements + " элементов.");
            } else {
                System.err.println("Кажется, ты ошибся в количестве элементов. Я работаю с числами в диапозоне [0;100].");
            }
        }
    };

    /** Переменная с именем файла, в котором хранится коллекция */
    private static final String jsonFile = System.getenv("jsonFile");

    /** Служебная переменная для работы с файлом */
    private static final Gson gson = new Gson();

    /** "Коллекция" имён для генератора */
    private static final String[] names = {"Папа", "Мама", "Юлиус", "Боссе", "Бетан", "Хильдур Бок"};

    /** "Коллекция" мест для генератора */
    private static final String[] locations = {"дома", "на крыше", "на улице", "у бабушки"};

    /** "Коллекция" времён для генератора (как долго персонаж будет в этом месте) */
    private static final String[] times = {"вечно", "на каникулы", "весь отпуск", "день", "неделю"};
    /** "Коллекция" характеров */
    private static final String[] characters = {"твёрдым", "мягким", "игривым", "тяжёлым", "весёлым"};

    /** Служебная переменная рандомайзер для генератора */
    private static final Random randomize = new Random();

    /** Метод, который вызывается у управляющих команд без параметра */
    public void doIt() {
        System.err.println("Что-то пошло не так.");
    }

    /**
     * Метод, вызывающийся у управляющих команд с параметром
     * @param i параметр, передающийся команде
     */
    public void doIt(int i) {
        System.err.println("Что-то пошло не так.");
    }
}
