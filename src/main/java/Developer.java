import java.sql.*;

public class Developer extends TeamMember {

    public Developer(int teamSize, String threadName, int sprintCount) {
        super(teamSize, threadName, sprintCount);
    }

    @Override
    public void run() {
        for (int i=1;i <= this.sprintCount; i++){
            try {
                thread.join(500);
                operate();
                Thread.sleep(1000);
                backlogId = 1;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(threadName + " bitti...");
    }

    static int backlogId = 1;

    @Override
    public void operate() {
        function(threadName);
    }

    private static synchronized void function(String name){
        try (Connection dbConnection = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/yazm457hw2",
                "root",
                "koc1234*")) {

            String selectQuery = "SELECT * FROM sprint_backlog WHERE sprintId = ? AND backlogId = ?";
            try (PreparedStatement selectStatement = dbConnection.prepareStatement(selectQuery)) {
                selectStatement.setInt(1, sprintId);
                selectStatement.setInt(2, backlogId);

                ResultSet sprintBacklogResultSet = selectStatement.executeQuery();

                if (sprintBacklogResultSet.next()) {
                    String insertQuery = "INSERT INTO board (taskname, backlogId, sprintId, developerName, priority) VALUES (?, ?, ?, ?, ?)";
                    try (PreparedStatement insertStatement = dbConnection.prepareStatement(insertQuery)) {
                        insertStatement.setString(1, sprintBacklogResultSet.getString("taskname"));
                        insertStatement.setInt(2, sprintBacklogResultSet.getInt("backlogId"));
                        insertStatement.setInt(3, sprintBacklogResultSet.getInt("sprintId"));
                        insertStatement.setString(4, name);
                        insertStatement.setInt(5, sprintBacklogResultSet.getInt("priority"));

                        insertStatement.executeUpdate();
                        backlogId++;
                    }
                }
            }
        } catch (SQLException sqlException) {
            throw new RuntimeException("Veritabanı işlemi sırasında hata oluştu: " + sqlException.getMessage(), sqlException);
        }
    }
}
