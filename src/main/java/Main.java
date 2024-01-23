import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) {
        try {
            // Aşağıdaki sorgular;
            // Her run ettiğimizde tablolar arası ilişkisel bütünlüğü sağlamak ve
            // 'sprintId' karışıklığını önlemek için tabloları temizlemek adına kullanılıyor
            // TKK...

            Connection dbConnection = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/yazm457hw2",
                    "root",
                    "koc1234*"
            );

            Statement dbStatement = dbConnection.createStatement();

            dbStatement.execute("DELETE FROM board");
            dbStatement.execute("DELETE FROM sprint_backlog");
            dbStatement.execute("DELETE FROM product_backlog");
        } catch (SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

        int teamSize = 5;
        int sprintCount = 3;

        TeamMember[] scrum = new TeamMember[5];

        scrum[0] = new ProductOwner(teamSize, sprintCount);
        scrum[1] = new ScrumMaster(teamSize, sprintCount);

        for(int i=2; i < scrum.length; i++)
            scrum[i] = new Developer(teamSize, String.format("Developer%d", (i-1)), sprintCount);

        for(TeamMember member : scrum)
            member.start();
    }
}
