import java.sql.*;
import java.util.concurrent.Semaphore;

public class ScrumMaster extends TeamMember{

    String url = "jdbc:mysql://localhost:3306/yazm457hw2"; // Veritabanı adını ekleyin
    String user = "root"; // Kullanıcı adı
    String password = "koc1234*"; // Şifre

    private Semaphore semaphore;

    public ScrumMaster(int teamSize, int sprintCount, Semaphore semaphore) {
        super(teamSize, "ScrumMaster", sprintCount);
        this.semaphore = semaphore;
    }

    @Override
    public void operate() {
        try {
            semaphore.acquire();
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                for (int i = 0; i < teamSize - 2; i++) {
                    String sqlSelect = "SELECT * FROM product_backlog WHERE backlogId = ?";
                    String sqlInsert = "INSERT INTO sprint_backlog (taskname, backlogId, sprintId, priority) VALUES (?, ?, ?, ?)";
                    try(PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect);
                        PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert)) {
                        pstmtSelect.setInt(1, i + 1);
                        ResultSet rs = pstmtSelect.executeQuery();
                        if (rs.next()) {
                            pstmtInsert.setString(1, rs.getString("taskname"));
                            pstmtInsert.setInt(2, rs.getInt("backlogId"));
                            pstmtInsert.setInt(3, i + 1);  // Sprint ID
                            pstmtInsert.setInt(4, rs.getInt("priority"));
                            pstmtInsert.executeUpdate();
                        }
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            semaphore.release();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (int i=1; i <= sprintCount; i++){
            try {
                operate();
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(threadName + " bitti...");
    }
}
