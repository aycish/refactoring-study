# 냄새1. 이해하기 힘든 이름

## 책의 내용

---

- 깔금한 코드에서 가장 중요한 것 중 하나가 바로 “좋은 이름”이다.
- 함수, 변수, 클래스, 모듈의 이름 등 모두 어떤 역할을 하는지 어떻게 쓰이는지 직관적이어야 한다.
- 사용할 수 있는 리팩토링 기술
    - 함수 선언 변경하기 (Change Function Declaration)
    - 변수 이름 바꾸기 (Rename Variable)
    - 필드 이름 바꾸기 (Rename Field)

## 함수 선언 변경하기 (Change Function Declaration)

---

### 특징

- 좋은 이름을 가진 함수는 함수가 어떻게 구현되었는지 코드를 보지 않아도 이름만 보고도 이해할 수 있다.
- 좋은 이름을 찾아내는 방법은 함수에 주석을 작성한 다음, 주석을 함수 이름으로 만들어본다.
- 함수의 매개변수
    - 함수 내부의 문맥을 결정한다. (ex. 전화번호 포매팅 함수)
    - 의존성을 결정한다. (ex. Payment 만기일 계산 함수)

### 변경 이전

```java
public class StudyDashboard {

    private Set<String> usernames = new HashSet<>();

    private Set<String> reviews = new HashSet<>();

    private void studyReviews(GHIssue issue) throws IOException {
        List<GHIssueComment> comments = issue.getComments();
        for (GHIssueComment comment : comments) {
            usernames.add(comment.getUserName());
            reviews.add(comment.getBody());
        }
    }

    public Set<String> getUsernames() {
        return usernames;
    }

    public Set<String> getReviews() {
        return reviews;
    }

    public static void main(String[] args) throws IOException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(30);

        StudyDashboard studyDashboard = new StudyDashboard();
        studyDashboard.studyReviews(issue);
        studyDashboard.getUsernames().forEach(System.out::println);
        studyDashboard.getReviews().forEach(System.out::println);
    }
}
```

### 변경 Step 1. 주석 달아보기

```java
public class StudyDashboard {

    private Set<String> usernames = new HashSet<>();

    private Set<String> reviews = new HashSet<>();

    /**
     * 스터디 리뷰 이슈에 작성되어 있는리뷰어 목록과 리뷰를 읽어옵니다.
     * @param issue
     * @throws IOException
     */
    private void studyReviews(GHIssue issue) throws IOException {
        List<GHIssueComment> comments = issue.getComments();
        for (GHIssueComment comment : comments) {
            usernames.add(comment.getUserName());
            reviews.add(comment.getBody());
        }
    }

    public Set<String> getUsernames() {
        return usernames;
    }

    public Set<String> getReviews() {
        return reviews;
    }

    public static void main(String[] args) throws IOException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(30);

        StudyDashboard studyDashboard = new StudyDashboard();
        studyDashboard.studyReviews(issue);
        studyDashboard.getUsernames().forEach(System.out::println);
        studyDashboard.getReviews().forEach(System.out::println);
    }
}

```

- 주석을 달아보면, 해당 내용을 생각해봤을 때, studyReview라는 메서드 명은 어울리지 않다.
- 또한, 전체 로직에서 생각해보면, 해당 메서드는 id가 30인 이슈만 접근함을 알 수 있다.
- 그에 따라, Github Issue를 매개변수로 전달해주는것은 어울리지 않는다.
- 현재 최선을 다해서 생각해야한다. 즉, 매개변수를 없애는게 최선
    - Q? 매개변수를 굳이 지워야 했을 까.. ?

### 변경 후

```java
public class StudyDashboard {

    private Set<String> usernames = new HashSet<>();

    private Set<String> reviews = new HashSet<>();

    /**
     * 스터디 리뷰 이슈에 작성되어 있는리뷰어 목록과 리뷰를 읽어옵니다.
     * @throws IOException
     */
    private void loadReview() throws IOException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(30);

        List<GHIssueComment> comments = issue.getComments();
        for (GHIssueComment comment : comments) {
            usernames.add(comment.getUserName());
            reviews.add(comment.getBody());
        }
    }

    public Set<String> getUsernames() {
        return usernames;
    }

    public Set<String> getReviews() {
        return reviews;
    }

    public static void main(String[] args) throws IOException {
        StudyDashboard studyDashboard = new StudyDashboard();
        studyDashboard.studyReviews(issue);
        studyDashboard.getUsernames().forEach(System.out::println);
        studyDashboard.getReviews().forEach(System.out::println);
    }
}
```

## 변수 이름 바꾸기 (Rename Variable)

---

### 특징

- 더 많이 사용되는 변수일수록 그 이름이 더 중요하다.
    - 람다식에서 사용하는 변수 vs 함수의 매개 변수
- 다이나믹 타입을 지원하는 언어에서는 타입을 이름에 넣기도 한다.
- 여러 함수에 걸쳐 쓰이는 필드 이름에는 더 많이 고민하고 이름을 짓는다.

### 변경 전

```java
public class StudyDashboard {

    private Set<String> usernames = new HashSet<>();

    private Set<String> reviews = new HashSet<>();

    /**
     * 스터디 리뷰 이슈에 작성되어 있는 리뷰어 목록과 리뷰를 읽어옵니다.
     * @throws IOException
     */
    private void loadReviews() throws IOException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(30);

        List<GHIssueComment> comments = issue.getComments();
        for (GHIssueComment comment : comments) {
            usernames.add(comment.getUserName());
            this.reviews.add(comment.getBody());
        }
    }

    public Set<String> getUsernames() {
        return usernames;
    }

    public Set<String> getReviews() {
        return reviews;
    }

    public static void main(String[] args) throws IOException {
        StudyDashboard studyDashboard = new StudyDashboard();
        studyDashboard.loadReviews();
        studyDashboard.getUsernames().forEach(name -> System.out.println(name));
        studyDashboard.getReviews().forEach(review -> System.out.println(review));
    }
}
```

- main의 람다식을 살펴보면, name과 review를 매개변수로 출력하는데, method reference를 사용하면 더 간결하게 정리할 수 있다.
- 즉, name과 review를 굳이 사용하지 않아도 된다.
- loadReviews 메서드를 살펴보면, review를 load하는 맥락임에도 불구하고, comments를 사용하고 있는데, 이는 적절하지 않다.
- reviews로 바꾸자

### 변경 후

```java
public class StudyDashboard {

    private Set<String> usernames = new HashSet<>();

    private Set<String> reviews = new HashSet<>();

    /**
     * 스터디 리뷰 이슈에 작성되어 있는 리뷰어 목록과 리뷰를 읽어옵니다.
     * @throws IOException
     */
    private void loadReviews() throws IOException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(30);

        List<GHIssueComment> reviews = issue.getComments();
        for (GHIssueComment comment : reviews) {
            usernames.add(comment.getUserName());
            this.reviews.add(comment.getBody());
        }
    }

    public Set<String> getUsernames() {
        return usernames;
    }

    public Set<String> getReviews() {
        return reviews;
    }

    public static void main(String[] args) throws IOException {
        StudyDashboard studyDashboard = new StudyDashboard();
        studyDashboard.loadReviews();
        studyDashboard.getUsernames().forEach(System.out::println);
        studyDashboard.getReviews().forEach(System.out::println);
    }
}
```

- 쓸데 없이 사용되고 있었던 변수 레퍼런스가 지워졌으며, 맥락에 맞게 변수가 선언되어 가독성 및 이해하기 편해졌다.

## 필드 이름 바꾸기 (Rename Field)

---

### 특징

- Record 자료 구조의 필드 이름은 프로그램 전반에 걸쳐 참조될 수 있기 때문에 매우 중요하다.
    - Record 자료 구조 : 특정 데이터와 관련있는 필드를 묶어 놓은 자료구조
    - 파이썬의 Dictionary와 비슷
    - 자바 14부터 지원. (record 키워드)
    - 자바에서는 Getter와 Setter 메소드 이름도 필드의 이름과 비슷하게 간주할 수 있다.

### 변경 전

```java
public class StudyDashboard {

    private Set<String> usernames = new HashSet<>();

    private Set<String> reviews = new HashSet<>();

    /**
     * 스터디 리뷰 이슈에 작성되어 있는 리뷰어 목록과 리뷰를 읽어옵니다.
     * @throws IOException
     */
    private void loadReviews() throws IOException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(30);

        List<GHIssueComment> reviews = issue.getComments();
        for (GHIssueComment comment : reviews) {
            usernames.add(comment.getUserName());
            this.reviews.add(comment.getBody());
        }
    }

    public Set<String> getUsernames() {
        return usernames;
    }

    public Set<String> getReviews() {
        return reviews;
    }

    public static void main(String[] args) throws IOException {
        StudyDashboard studyDashboard = new StudyDashboard();
        studyDashboard.loadReviews();
        studyDashboard.getUsernames().forEach(System.out::println);
        studyDashboard.getReviews().forEach(System.out::println);
    }
}
```

- usernames라는 필드 이름 보다는 reviewers가 더 문맥상 맞아 보인다.
- 또한, String을 직접 사용하기 보다는, reviewer와 review 내용을 record로 묶어 처리한다면 불필요한 필드가 제거될 것같다.

### 변경 후

```java
public class StudyDashboard {

    private Set<StudyReview> studyReviews;

    private void loadReviews() throws IOException {
        GitHub gitHub = GitHub.connect();
        GHRepository repository = gitHub.getRepository("whiteship/live-study");
        GHIssue issue = repository.getIssue(30);
        
        List<GHIssueComment> reviews = issue.getComments();
        for (GHIssueComment comment : reviews) {
            studyReviews.add(new StudyReview(comment.getUserName(), comment.getBody()));
        }
    }

    public Set<StudyReview> getStudyReviews() {
        return studyReviews;
    }

    public static void main(String[] args) throws IOException {
        StudyDashboard studyDashboard = new StudyDashboard();
        studyDashboard.loadReviews();
        studyDashboard.getStudyReviews().forEach(System.out::println);
    }
}

## Record 정의
public record StudyReview(String reviewer, String review) {
}
```

- main을 기준으로 생각해본다면, 코드가 상당히 간결해졌고, 따로 loadReview에 대해서 사전 지식이 없어도 로직을 이해할 수 있다.
- 다만 Record를 까봐야하는 단점이 있는것 같은데… Set이 하나가 더 줄어든다는것을 의의로 가져보자…

## ToDo
- 책에서 관련된 내용 찾아 정리
- MD 파일 합치기