import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;

public class ProductOwner extends TeamMember{

    private String url = "jdbc:mysql://localhost:3306/yazm457hw2";
    private String username = "root";
    private String password = "koc1234*";

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
            int index = ran.nextInt(3);
            String taskName = gorevler[index];

            int priority = ran.nextInt(10);

            return new Task(taskName, backlogId, priority);
        }
        @Override
        public String toString() {
            return String.format("(name:%s, p:%d, backlogId:%d)",
                    name, priority, backlogId);
        }
    }

    public ProductOwner(int teamSize, int sprintCount) {
        super(teamSize, "ProductOwner", sprintCount);
    }

    @Override
    public void operate() {
        try {
            Connection dbConnection = DriverManager.getConnection(url, username, password);
            for (int index = 2; index < teamSize; index++) {
                Task currentTask = Task.generateTask(index - 1);

                String insertQuery = "INSERT INTO product_backlog(taskname, backlogId, priority) VALUES ('%s', %d, %d)"
                        .formatted(currentTask.name, currentTask.backlogId, currentTask.priority);

                Statement dbStatement = dbConnection.createStatement();
                dbStatement.execute(insertQuery);
            }
            dbConnection.close();
        } catch (SQLException sqlException) {
            throw new IllegalStateException("Veritabanına bağlanılamıyor!", sqlException);
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
