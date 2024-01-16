import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;
import java.util.concurrent.Semaphore;

public class ProductOwner extends TeamMember{

    // Veritabanı bağlantı bilgileri
    String url = "jdbc:mysql://localhost:3306/yazm457hw2"; // Veritabanı adını ekleyin
    String user = "root"; // Kullanıcı adı
    String password = "koc1234*"; // Şifre

    public static class Task {

        String name;
        int backlogId;
        int priority;

        public Task(String name, int bid, int p) {
            this.name = name;
            this.backlogId = bid;
            this.priority = p;
        }

        public static Task generateTask(int backlogId) {

            String[] gorevler = {"testing", "documenting", "coding"};
            Random ran = new Random();
            // Returns number between 0-2
            int index = ran.nextInt(3);
            String taskName = gorevler[index];

            // Returns number between 0-9
            int priority = ran.nextInt(10);

            return new Task(taskName, backlogId, priority);
        }
        @Override
        public String toString() {
            return String.format("(name:%s, p:%d, backlogId:%d)",
                    name, priority, backlogId);
        }
    }

    private Semaphore semaphore;

    public ProductOwner(int teamSize, int sprintCount, Semaphore semaphore) {
        super(teamSize, "ProductOwner", sprintCount);
        this.semaphore = semaphore;
    }

    @Override
    public void operate() {
        try {
            semaphore.acquire();
            try (Connection connection = DriverManager.getConnection(url, user, password)) {
                for (int i = 0; i < teamSize - 2; i++) {
                    Task task = Task.generateTask(i + 1);
                    String sql = "INSERT INTO product_backlog (taskname, backlogId, priority) VALUES (?, ?, ?)";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setString(1, task.name);
                        pstmt.setInt(2, task.backlogId);
                        pstmt.setInt(3, task.priority);
                        pstmt.executeUpdate();
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

        for (int i=1;i <= sprintCount; i++){
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
