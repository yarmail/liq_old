package project;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * В данном случае все вопросы подключения берет
 * на себя тестовый класс src/test/java/project/IntegrationTest.java
 * который и будет использовать методы данного класса
 */
public class Dao {
    private final Connection connection;

    public Dao(Connection connection) {
        this.connection = connection;
    }

    /**
     * Для того чтобы получить id сгенерированное БД.
     * Нужно при создании PrepareStatement вторым аргументом передать
     * Statement.RETURNING_GENERATED_KEYS. После как обычно выполнить запрос.
     * Наконец, чтобы получить ключ нужно вызвать метод getGeneratedKeys().
     */
    public Item add(Item item) {
        try (PreparedStatement statement = connection.prepareStatement
                ("insert into items(name, created) values (?, ?)",
                             Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, item.getName());
            statement.setTimestamp(2, Timestamp.valueOf(item.getCreated()));
            statement.execute();
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    item.setId(generatedKeys.getInt(1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    public boolean replace(int id, Item item) {
        boolean result = false;
        try (PreparedStatement statement = connection.prepareStatement
                ("update items set name = ?, created = ? where id = ?;")) {
            statement.setString(1, item.getName());
            statement.setTimestamp(2, Timestamp.valueOf(item.getCreated()));
            statement.setInt(3, id);
            result = statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean delete(int id) {
        boolean result = false;
        try (PreparedStatement statement = connection.prepareStatement
                ("delete from items where id = ?;")) {
            statement.setInt(1, id);
            result = statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public Item returnItem(ResultSet set) throws SQLException {
        return new Item(set.getInt(1), set.getString(2),
                set.getTimestamp(3).toLocalDateTime());
    }

    public List<Item> findAll() {
        List<Item> items = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement
                ("select * from items;")) {
            statement.execute();
            try (ResultSet set = statement.getResultSet()) {
                while (set.next()) {
                    items.add(returnItem(set));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Item> findByName(String key) {
        List<Item> items = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement
                ("select * from items where name = ?;")) {
            statement.setString(1, key);
            statement.execute();
            try (ResultSet set = statement.getResultSet()) {
                while (set.next()) {
                    items.add(returnItem(set));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public Item findById(int id) {
        Item item = null;
        try (PreparedStatement statement = connection.prepareStatement
                ("select * from items where id = ?;")) {
            statement.setInt(1, id);
            statement.execute();
            try (ResultSet set = statement.getResultSet()) {
                if (set.next()) {
                    item = returnItem(set);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }
}