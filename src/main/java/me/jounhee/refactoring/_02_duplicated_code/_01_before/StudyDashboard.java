package me.jounhee.refactoring._02_duplicated_code._01_before;

import org.kohsuke.github.GHIssue;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class StudyDashboard {

    private void printParticipants(int eventId) throws IOException {
        GHIssue issue = getGitHubIssue(eventId);
        Set<String> participants = getUserNames(issue);
        print(participants);
    }

    private void printReviewers() throws IOException {
        GHIssue issue = getGitHubIssue(30);
        Set<String> reviewers = getUserNames(issue);
        print(reviewers);
    }

    private static GHIssue getGitHubIssue(int eventId) throws IOException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(eventId);
        return issue;
    }

    private static Set<String> getUserNames(GHIssue issue) throws IOException {
        Set<String> userNames = new HashSet<>();
        issue.getComments().forEach(c -> userNames.add(c.getUserName()));
        return userNames;
    }

    private void print(Set<String> participants) {
        participants.forEach(System.out::println);
    }

    public static void main(String[] args) throws IOException {
        StudyDashboard studyDashboard = new StudyDashboard();
        studyDashboard.printReviewers();
        studyDashboard.printParticipants(15);

    }

}
