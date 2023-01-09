# 냄새 2. 중복 코드

## 책의 내용

---

### 중복 코드의 단점

- 비슷한지, 완전히 동일한 코드인지 주의 깊게 봐야한다.
    - 구조가 동일하지만, 하는 행동은 다른 경우가 비슷한 경우에 해당한다.
- 코드를 변경할 때, 동일한 모든 곳의 코드를 변경해야한다.

### 사용할 수 있는 리팩토링 기술

- 동일한 코드를 여러 메소드에서 사용하는 경우, 함수 추출하기 (Extract Function)
- 코드가 비슷하게 생겼지만 완전히 같지는 않은 경우, 코드 분리하기 (Slide Statements)
- 여러 하위 클래스에 동일한 코드가 있다면, 메소드 올리기 (Pull Up Method)

## 함수 추출하기 (Extract Function)

---

### “의도”와 “구현” 분리하기

- 코드를 읽을 때, 어떤 일을 하는지 읽히지 않는다면 책에서는 “구현을 보고 있다”고 한다.
- 반대의 경우, “의도를 드러내고 있다”라고 표현하고 있다.
- 무슨 일을 하는 코드인지 알아내려고 노력해야하는 코드라면, 해당 코드를 함수로 분리하고, 함수 이름으로 “무슨 일을 하는지” 표현할 수 있다.
- (백기선님 멘트) Connection 얻기, Query 지정하기 등은 “구현”이라고 본다.
    - 반대로, Connection을 얻어오고 이후에 특정 쿼리를 통해 데이터를 프로세싱하고… 이런 식으로 Step이 분명하게 보인다면 “의도”가 보인다 라고한다.

### 한줄 짜리 메소드도 괜찮은가?

- 의도를 드러내기 위해서라면 괜찮다.
- 거대한 함수 안에 들어있는 주석은 추출한 함수를 찾는데 있어서 좋은 단서가 될 수 있다.

### 변경 전

```java
private void printParticipants(int eventId) throws IOException {
    // Get github issue to check homework
    GitHub gitHub = GitHub.connect();
    GHRepository repository = gitHub.getRepository("whiteship/live-study");
    GHIssue issue = repository.getIssue(eventId);

    // Get participants
    Set<String> participants = new HashSet<>();
    issue.getComments().forEach(c -> participants.add(c.getUserName()));

    // Print participants
    participants.forEach(System.out::println);
}
```

- 전반적인 내용을 파악해야 의도를 알 수 있으므로, 소요 시간이 길다.
- 즉, 유지보수가 힘들다

### 변경 후

```java
private void printParticipants(int eventId) throws IOException {
    GHIssue issue = getGitHubIssue(eventId);
    Set<String> participants = getUserNames(issue);
    print(participants);
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

```

- 주석도 필요 없어질 정도로, 의도가 드러나는 코드로 리팩토링했다.

## 코드 정리하기 (Slide Statements)

---

- 관련있는 코드끼리 묶여있어야 코드를 더 쉽게 이해할 수 있다.
- 함수에서 사용할 변수를 상단에 미리 정의하기 보다는, 해당 변수를 사용하는 코드 바로 위에 선언하자.
- 관련있는 코드끼리 묶은 다음, 함수 추출하기(Extract Function)을 사용해서 더 깔끔하게 분리할 수있다.
- (백기선님 멘트) 특정 변수를 선언할 때, 사용하기 바로 직전에 선언해둬야 맥락을 이해할 수 있다.

### 변경 전

```java
private void printParticipants(int eventId) throws IOException {
    // Get github issue to check homework
    GitHub gitHub = GitHub.connect();
    Set<String> participants = new HashSet<>();
    GHRepository repository = gitHub.getRepository("whiteship/live-study");
    GHIssue issue = repository.getIssue(eventId);

    // Get participants
    issue.getComments().forEach(c -> participants.add(c.getUserName()));

    // Print participants
    participants.forEach(System.out::println);
}
```

- 변수의 선언부가 한 곳에 몰려있다면, 코드 맥락을 이해하면서 변수들을 모두 기억하고 있어야한다.
- 코드 맥락 이해에 집중하기 힘들다.

### 변경 후

```java
private void printParticipants(int eventId) throws IOException {
    // Get github issue to check homework
    GitHub gitHub = GitHub.connect();
    GHRepository repository = gitHub.getRepository("whiteship/live-study");
    GHIssue issue = repository.getIssue(eventId);

    // Get participants
    Set<String> participants = new HashSet<>();
    issue.getComments().forEach(c -> participants.add(c.getUserName()));

    // Print participants
    participants.forEach(System.out::println);
}
```

- 선언부와 사용처를 한 곳에 몰아두었기 때문에, 맥락 이해하는데 크게 집중을 요하지 않는다(?)
- 함수 추출하기(Extract Method)를 용이하게 할 수 있게 구조화되었다.

## 메서드 올리기 (Pull Up Method)

---

- 중복 코드는 당장은 잘 동작하더라도 미래에 버그를 만들어 낼 빌미를 제공한다.
    - 예) A에서 코드를 고치고, B에는 반영하지 않은 경우
- 여러 하위 클래스에 동일한 코드가 있다면, 손쉽게 이 방법을 적용할 수 있다.
- 비슷하지만 일부 값만 다른 경우라면, “함수 매개변수화하기 (동작 파라미터화)” 리팩토링을 적용한 이후, 이 방법을 사용할 수 있다.
- 하위 클래스에 있는 코드가 상위 클래스가 아닌 하위 클래스 기능에 의존하고 있다면, “필드 올리기”를 적용한 이후에 이 방법을 적용할 수 있다.
- 두 메서드가 비슷한 절차를 따르고 있다면, “템플릿 메서드 패턴” 적용을 고려할 수 있다.

### 변경 전

```java
public class Dashboard {

  public static void main(String[] args) throws IOException {
      ReviewerDashboard reviewerDashboard = new ReviewerDashboard();
      reviewerDashboard.printReviewers();

      ParticipantDashboard participantDashboard = new ParticipantDashboard();
      participantDashboard.printParticipants(15);
  }
}

public class ParticipantDashboard extends Dashboard {
    public void printParticipants(int eventId) throws IOException {
        // Get github issue to check homework
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(eventId);

        // Get participants
        Set<String> participants = new HashSet<>();
        issue.getComments().forEach(c -> participants.add(c.getUserName()));

        // Print participants
        participants.forEach(System.out::println);
    }
}

public class ReviewerDashboard extends Dashboard {
    public void printReviewers() throws IOException {
        // Get github issue to check homework
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(30);

        // Get reviewers
        Set<String> reviewers = new HashSet<>();
        issue.getComments().forEach(c -> reviewers.add(c.getUserName()));

        // Print reviewers
        reviewers.forEach(System.out::println);
    }
}
```

- printReviewers와 printParticipants의 기능 동작이 매우 비슷하다.
- 동일한 코드를 2벌 들고 있는 느낌이 강하게 들기 때문에, Pull Up Method 기법을 통해 정리해보자

### 변경 후

```java
public class Dashboard {

    public static void main(String[] args) throws IOException {
        ReviewerDashboard reviewerDashboard = new ReviewerDashboard();
        reviewerDashboard.printReviewers();

        ParticipantDashboard participantDashboard = new ParticipantDashboard();
        participantDashboard.printParticipants(15);
    }

    public void printUserNames(int eventId) throws IOException {
        // Get github issue to check homework
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(30);

        // Get userNames
        Set<String> userNames = new HashSet<>();
        issue.getComments().forEach(c -> userNames.add(c.getUserName()));

        // Print userNames
        userNames.forEach(System.out::println);
    }
}

public class ReviewerDashboard extends Dashboard {
    public void printReviewers() throws IOException {
        printUserNames(30);
    }
}

public class ParticipantDashboard extends Dashboard {
    public void printParticipants(int eventId) throws IOException {
        super.printUserNames(eventId);
    }
}
```

- 두 구현체의 구현 내용이 간결해졌다.
- 자주 사용하는 기법은 아니지만, IntelliJ를 통해서 쉽게 할 수 있음을 기억하고, 기회가 된다면 사용해보자