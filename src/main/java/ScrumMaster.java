import java.sql.*;
import java.util.ArrayList;

public class ScrumMaster extends TeamMember{

    private String url = "jdbc:mysql://localhost:3306/yazm457hw2";
    private String username = "root";
    private String password = "koc1234*";

    public ScrumMaster(int teamSize, int sprintCount) {
        super(teamSize, "ScrumMaster", sprintCount);
    }

    @Override
    public void operate() {
        try {
            Connection dbConnection = DriverManager.getConnection(url, username, password);
            String selectQuery = """
            SELECT *
            FROM (
                SELECT *
                FROM product_backlog
                ORDER BY taskId DESC
                LIMIT 3
            ) AS subQuery
            ORDER BY priority;
            """;

            Statement dbStatement = dbConnection.createStatement();

            ResultSet taskResultSet = dbStatement.executeQuery(selectQuery);

            String insertQuery = "INSERT INTO sprint_backlog(taskname, backlogId, sprintId, priority) " +
                    "VALUES ('%s', %d, %d, %d)";
            ArrayList<String> insertStatements = new ArrayList<>();

            while (taskResultSet.next()){
                insertStatements.add(
                        insertQuery.formatted(
                                taskResultSet.getString("taskname"),
                                taskResultSet.getInt("backlogId"),
                                sprintId,
                                taskResultSet.getInt("priority")
                        )
                );
            }

            for (String insertStatement : insertStatements){
                dbStatement.execute(insertStatement);
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
                thread.join(200);
                operate();
                Thread.sleep(1000);
                sprintId++;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(threadName + " bitti...");
    }
}
