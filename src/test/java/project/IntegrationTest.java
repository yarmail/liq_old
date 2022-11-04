package project;

import org.junit.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class IntegrationTest {
    private static Connection connection;
    private static Dao dao;

    private final Item item1 = new Item("item1");
    private final Item item2 = new Item("item2");
    private final Item item3 = new Item("item3");
    private final Item item4 = new Item("item2");

    @BeforeClass
    public static void initConnection() {
        try (InputStream in = IntegrationTest.class.getClassLoader()
                .getResourceAsStream("test.properties")) {
            Properties config = new Properties();
            config.load(in);
            Class.forName(config.getProperty("driver-class-name"));
            connection = DriverManager.getConnection(
                    config.getProperty("url"),
                    config.getProperty("username"),
                    config.getProperty("password")

            );
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        dao = new Dao(connection);
    }

    @AfterClass
    public static void closeConnection() throws SQLException {
        connection.close();
    }

    @After
    public void wipeTable() throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement
                ("delete from items")) {
            statement.execute();
        }
    }

    @Test
    public void addTest() {
        dao.add(item1);
        assertThat(dao.findById(item1.getId()), is(item1));
    }

    @Test
    public void FindByIdTest() {
        dao.add(item1);
        Item result = dao.findById(item1.getId());
        assertThat(result.getName(), is(item1.getName()));
    }

    @Test
    public void FindAllTest() {
        dao.add(item1);
        dao.add(item2);
        assertThat(dao.findAll(), is(List.of(item1, item2)));
    }

    @Test
    public void FindByNameTest() {
        dao.add(item1);
        dao.add(item2);
        dao.add(item3);
        dao.add(item4);
        assertThat(dao.findByName("item2"), is(List.of(item2, item4)));
    }

    @Test
    public void ReplaceTest() {
        dao.add(item1);
        dao.replace(item1.getId(), item2);
        assertThat(dao.findById(item1.getId()).getName(), is(item2.getName()));
    }

    @Test
    public void DeleteTest() {
        dao.add(item1);
        dao.delete(item1.getId());
        assertThat(dao.findById(item1.getId()), is(nullValue()));
    }
}