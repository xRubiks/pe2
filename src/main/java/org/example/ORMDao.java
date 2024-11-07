package org.example;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.*;

public class ORMDao {
    private static final Logger logger = Logger.getLogger(ORMDao.class.getName());
    private ConnectionSource connectionSource;
    private Dao<Letter, Integer> letterDao;
    private final String connectionString = "";
    private final String user = "";
    private final String password = "";

    static {
        // Set up logger with INFO level and custom console formatting
        logger.setLevel(Level.INFO);

        ConsoleHandler handler = new ConsoleHandler();
        handler.setLevel(Level.INFO); // Only log INFO level or higher
        handler.setFormatter(new SimpleFormatter() {
            @Override
            public synchronized String format(LogRecord record) {
                return String.format(
                        "[%1$tF %1$tT] [%2$-7s] %3$s%n",
                        record.getMillis(),
                        record.getLevel().getName(),
                        record.getMessage()
                );
            }
        });

        logger.setUseParentHandlers(false);
        logger.addHandler(handler);
    }

    private boolean connectToDB(String connectionString, String user, String password) {
        try {
            this.connectionSource = new JdbcConnectionSource(connectionString, user, password);
            letterDao = DaoManager.createDao(connectionSource, Letter.class);
            logger.info("Database connection established.");
            return true;
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Failed to connect to database: " + exception.getMessage());
            return false;
        }
    }

    private String solveRiddle(int[] arrayIndexes) {
        StringBuilder result = new StringBuilder();
        for (int index : arrayIndexes) {
            try {
                Letter letter = this.letterDao.queryForId(index);
                result.append(letter.getLetter());
            } catch (SQLException exception) {
                logger.log(Level.SEVERE, "Error fetching letter for index " + index + ": " + exception.getMessage());
            }
        }
        logger.info("Concatenated letters result: " + result);
        return result.toString();
    }

    private void findIdByLetter(char letter) {
        try {
            List<Letter> letters = letterDao.queryForEq("letter", Character.toString(letter));
            StringBuilder result = new StringBuilder("IDs for letter '" + letter + "': ");
            for (Letter l : letters) {
                result.append(l.getId()).append(" ");
            }
            logger.info(result.toString().trim());
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Error finding IDs for letter '" + letter + "': " + exception.getMessage());
        }
    }

    private void calcSumAndAverageOfIds() {
        try {
            List<Letter> letters = letterDao.queryForAll();
            int sum = letters.stream().mapToInt(Letter::getId).sum();
            double average = letters.isEmpty() ? 0 : (double) sum / letters.size();
            logger.info("Sum of IDs: " + sum);
            logger.info("Average of IDs: " + average);
        } catch (SQLException exception) {
            logger.log(Level.SEVERE, "Error calculating sum and average of IDs: " + exception.getMessage());
        }
    }

    private void closeConnectionToDB() {
        try {
            if (this.connectionSource != null) {
                this.connectionSource.close();
                logger.info("Database connection closed.");
            }
        } catch (Exception exception) {
            logger.log(Level.SEVERE, "Error closing database connection: " + exception.getMessage());
        }
    }

    public void run() {
        int[] arrayIndexes = { 20, 44, 50, 13, 17, 33, 41, 68, 77, 44, 29, 72, 48, 71, 37, 48, 11, 69, 5, 65, 65 };
        if (connectToDB(connectionString, user, password)) {
            logger.info("Result of solveRiddle: " + solveRiddle(arrayIndexes));
            findIdByLetter('V');
            findIdByLetter('b');
            findIdByLetter('t');
            calcSumAndAverageOfIds();
            closeConnectionToDB();
        } else {
            logger.severe("Cannot run tasks without a database connection.");
        }
    }
}
