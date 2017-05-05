package ru.ifmo.se.kirmanak;

import com.sun.rowset.JdbcRowSetImpl;
import javafx.collections.ObservableList;

import javax.sql.rowset.JdbcRowSet;
import java.sql.*;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Enumeration команд, использующихся в программе
 */
enum Commands {
    /** Удаляет элемент из коллекции */
    remove {
        public String toString() {
            return "Удалить элемент, на который указывает слайдер.\n";
        }

        public String doIt(int index) {
            index--;
            if (index < EntryPoint.getCollection().size() && index >= 0) {
                EntryPoint.getCollection().remove(index);
                return String.format("%d-й элемент удалён", index+1);
            } else {
                return "Нет такого элемента.";
            }
        }
    },
    /** Записывает коллекцию в базу данных. */
    save {
        public String toString() {
            return "Сохранить коллекцию в базу данных.\n";
        }

        public String doIt() {
            PreparedStatement ps = null;
            Connection connection = null;
            Savepoint savepoint = null;
            try {
                connection = EntryPoint.getConnection();
                connection.setAutoCommit(false);
                savepoint = connection.setSavepoint();
                ps = connection.prepareStatement(
                        "INSERT INTO "+EntryPoint.getDbTableName()
                                +"(hashcode, name, relative, time, character, location) " +
                                "VALUES (?,?,?::relative,?::date,?,?) ON CONFLICT DO NOTHING;"
                );
                ObservableList<Humans> collection = EntryPoint.getCollection();
                for (Humans humans : collection) {
                    ps.setInt(1, humans.hashCode());
                    ps.setString(2, humans.getName());
                    ps.setString(3, humans.getRelative().name());
                    ps.setString(4, humans.getTime().toString());
                    ps.setString(5, humans.getCharacter());
                    ps.setString(6, humans.getLocation().toString());
                    ps.executeUpdate();
                    connection.commit();
                }
                return "Запись выполнена успешно";
            } catch (SQLException e) {
                try {
                    if (connection != null && savepoint != null)
                        connection.rollback(savepoint);
                    else throw new SQLException("Null savepoint или connection.");
                    System.err.println("Rollback сработал, всё в порядке.");
                    return e.getMessage();
                } catch (SQLException err) {
                    System.err.println("Rollback не сработал, всё плохо.");
                    return err.getMessage();
                }
            } finally {
                try {
                    if (ps != null) ps.closeOnCompletion();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    System.err.println("Не смог закрыть соединение.");
                    e.printStackTrace();
                }
            }
        }
    },
    /** Считывает коллекцию из базы данных */
    load {
        public String toString() {
            return "Cчитать коллекцию из базы данных.\n";
        }

        public String doIt() {
            try (Connection connection = EntryPoint.getConnection()) {
                Statement statement = connection.createStatement();
                ResultSet set = statement.executeQuery(
                        "SELECT * FROM " + EntryPoint.getDbTableName() + ";"
                );
                JdbcRowSet jdbcRowSet = new JdbcRowSetImpl(set);
                ObservableList<Humans> collection = EntryPoint.getCollection();
                List<Humans> list = new ArrayList<>();
                while (jdbcRowSet.next()) {
                    int hashCode = jdbcRowSet.getInt("hashcode");
                    String name = jdbcRowSet.getString("name");
                    Relative relative = Relative.valueOf(jdbcRowSet.getString("relative"));
                    LocalDate time = jdbcRowSet.getDate("time").toLocalDate();
                    String character = jdbcRowSet.getString("character");
                    Location location = new Location(jdbcRowSet.getString("location"));
                    Humans human = new Humans(name, character, relative, time, location, hashCode);
                    list.add(human);
                }
                statement.closeOnCompletion();
                collection.setAll(list);
                return "Загрузка прошла успешно";
            } catch (SQLException e) {
                return e.getMessage();
            }
        }
    },
    /** Добавляет нового человека */
    add {
        public String toString() {
            return "Добавить нового человека.\n";
        }

        public String doIt() {
            String name = Interface.getName(),
                    character = Interface.getCharacter();
            LocalDate time = Interface.getDate();
            Relative relative = Interface.getRelative();
            Location location = new Location(Interface.getLocation());
            EntryPoint.getCollection().add(new Humans(name, character, location, time, relative));
            return "Новый человек добавлен в коллекцию.";
        }
    },
    /** Генерирует новых людей */
    generate {
        public String toString() {
            return "Сгенерировать элементов столько, сколько показывает слайдер, " +
                    "\n со случайными наборами полей (может быть довольно весело)";
        }

        private Humans generate() {
            String name = names[randomize.nextInt(names.length)];
            LocalDate time = LocalDate.now();
            try {
                time = LocalDate.ofEpochDay(LocalDate.now().toEpochDay() + Math.abs(randomize.nextInt(365)));
            } catch (DateTimeException err) {
                System.err.println(err.getLocalizedMessage());
            }
            String character = characters[randomize.nextInt(characters.length)];
            Location location = new Location(locations[randomize.nextInt(locations.length)]);
            Relative relative = Relative.values()[randomize.nextInt(Relative.values().length)];
            return new Humans(name, character, location, time, relative);
        }

        public String doIt() {
            return "Нужно указать количество элементов, которое нужно сгенерировать.";
        }

        public String doIt(int amountOfElements) {
            if (amountOfElements >= 0 && amountOfElements <= 100) {
                for (int i = 0; i < amountOfElements; i++) {
                    EntryPoint.getCollection().add(generate());
                }
                return "Сгенерировал " + amountOfElements + " элементов.";
            } else {
                return "Кажется, ты ошибся в количестве элементов. Я работаю с числами в диапозоне [0;100].";
            }
        }
    };

    /** "Коллекция" имён для генератора */
    private static final String[] names = {"Папа", "Мама", "Юлиус", "Боссе", "Бетан", "Хильдур Бок"};
    /** "Коллекция" мест для генератора */
    private static final String[] locations = {"дома", "на крыше", "на улице", "у бабушки"};
    /** "Коллекция" характеров */
    private static final String[] characters = {"твёрдым", "мягким", "тяжёлым", "весёлым"};
    /** Служебная переменная-рандомайзер для генератора */
    private static final Random randomize = new Random();

    /** Метод, который вызывается у управляющих команд без параметра */
    public String doIt() {
        return "Что-то пошло не так.";
    }

    /**
     * Метод, вызывающийся у управляющих команд с параметром
     * @param i параметр, передающийся команде
     */
    public String doIt(int i) {
        return "Что-то пошло не так.";
    }
}
