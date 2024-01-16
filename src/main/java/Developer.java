import java.sql.*;
import java.util.concurrent.Semaphore;

public class Developer extends TeamMember {

    String url = "jdbc:mysql://localhost:3306/yazm457hw2";
    String user = "root"; // Kullanıcı adı
    String password = "koc1234*"; // Şifre

    private Semaphore semaphore;

    public Developer(int teamSize, String threadName, int sprintCount, Semaphore semaphore) {
        super(teamSize, threadName, sprintCount);
        this.semaphore = semaphore;
    }

    @Override
    public void run() {
        for (int i = 1; i <= this.sprintCount; i++) {
            try {
                operate();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(threadName + " bitti...");
    }

    public void operate() {
        try {
            semaphore.acquire();

            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                String sqlSelect = "SELECT * FROM sprint_backlog WHERE sprintId = ?";
                String sqlInsert = "INSERT INTO board (taskname, backlogId, sprintId, developerName, priority) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect);
                     PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert)) {

                    pstmtSelect.setInt(1, sprintCount);  // Sprint ID
                    ResultSet rs = pstmtSelect.executeQuery();

                    while (rs.next()) { // Her bir sonuç için işlem yapılıyor
                        pstmtInsert.setString(1, rs.getString("taskname"));
                        pstmtInsert.setInt(2, rs.getInt("backlogId"));
                        pstmtInsert.setInt(3, rs.getInt("sprintId"));
                        pstmtInsert.setString(4, this.threadName);
                        pstmtInsert.setInt(5, rs.getInt("priority"));
                        pstmtInsert.executeUpdate();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}
