import java.util.concurrent.Semaphore;

public class Main {

    public static void main(String[] args) {

        int teamSize = 5;
        int sprintCount = 1;
        Semaphore semaphore = new Semaphore(1);

        TeamMember[] scrum = new TeamMember[5];

        scrum[0] = new ProductOwner(teamSize, sprintCount, semaphore);
        scrum[1] = new ScrumMaster(teamSize, sprintCount, semaphore);

        for(int i=2; i < scrum.length; i++)
            scrum[i] = new Developer(teamSize, String.format("Developer%d", (i-1)), sprintCount, semaphore);

        for(TeamMember member : scrum)
            member.start();

    }
}
