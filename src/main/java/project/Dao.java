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
            System.out.println(item.getCreated());
            Timestamp timestamp = Timestamp.valueOf(item.getCreated());
            System.out.println(timestamp);
            statement.setTimestamp(2, timestamp);
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
                ("update items set name = ? where id = ?")) {
            statement.setString(1, item.getName());
            statement.setInt(2, id);
            result = statement.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Item> findAll() {
        List<Item> result = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("select * from items;")) {
            ResultSet resultSet =  statement.executeQuery();
            while (resultSet.next()) {
                Item temp = new Item();
                temp.setName((resultSet.getString("name")));
                temp.setId(resultSet.getInt("id"));
                temp.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                result.add(temp);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Item> findByName(String key) {
        List<Item> result = new ArrayList<>();
        try (PreparedStatement statement
                     = connection.prepareStatement("select * from items where name = ?;")) {
            statement.setString(1, key);
            Item temp = new Item();
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                temp.setName(resultSet.getString("name"));
                temp.setId(resultSet.getInt("id"));
                temp.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
                result.add(temp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public Item findById(int id) {
        Item result = new Item();
        try (PreparedStatement statement
                     = connection.prepareStatement("select * from items where id = ?;")) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                result.setName(resultSet.getString("name"));
                result.setId(resultSet.getInt("id"));
                result.setCreated(resultSet.getTimestamp("created").toLocalDateTime());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}