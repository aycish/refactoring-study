# 냄새 3. 긴 함수

## 강의 내용

---

### 짧은 함수 vs 긴 함수

- 함수가 길수록 더 이해하기 어렵다 vs 짧은 함수는 더 많은 문맥 전환을 필요로 한다.
- “과거에는” 작은 함수를 사용하는 경우에 더 많은 서브 루틴 호출로 인한 오버헤드가 있었다.
- 작은 함수에 “좋은 이름”을 사용했다면, 해당 함수의 코드를 보지 않고도 이해할 수 있다.
- 어떤 코드에 “주석”을 남기고 싶다면, 주석 대신 함수를 만들고 함수의 이름으로 “의도”를 표현해보자

### 사용할 수 있는 리팩토링 기술

- 99%는 함수 추출하기(Extract Function)으로 해결할 수 있다.
- 함수로 분리하면서 해당 함수로 전달해야 할 매개변수가 많아진다면, 다음과 같은 리팩토링을 고려해볼 수 있다.
    - 임시 변수를 질의 함수로 바꾸기 (Replace Temp with Query)
    - 매개 변수 객체 만들기 (Introduce Paramter Object)
    - 객체 통째로 넘기기 (Preserve Whole Object)
- 조건문 분해하기 (Decompose Conditional)를 사용해 조건문을 분리할 수 있다.
- 같은 조건으로 여러개의 Switch문이 있다면, 조건문을 다형성으로 바꾸기 (Replace Conditional with Polymorphism)을 사용할 수 있다.
- 반복문 안에서 여러 작업을 하고 있어서 하나의 메소드로 추출하기 어렵다면, 반복문 쪼개기(Split Loop)를 적용할 수 있다.

### 느낌 정리

- 의도를 충분히 잘 나타내고 있다면, 너무 길지 않은 이상 긴 함수로 보지 않는다.
- call stack 오버해드까지 고려하면서 코드를 짤 필요는 없다.
- 코드에 주석이 있다면, 주석이 있는 부분을 함수로 추출하고 함수의 이름을 변경하자!
- 긴 함수가 발생한 원인을 분석하고, 그에 따라 다양한 기법을 적용해보자

## 임시 변수를 질의 함수로 바꾸기 (Replace Temp with Query)

---

- 변수를 사용하면 반복해서 동일한 식을 계산하는 것을 피할 수 있고, 이름을 사용해 의미를 표현할 수도 있다.
- 긴 함수를 리팩토링할 때, 그러한 임시 변수를 함수로 추출하여 분리한다면,. 빼낸 함수로 전달해야 할 매개변수를 줄일 수 있다.
- 임시 변수를 함수로 분리하자!

### 예제 - before

```java

participants.forEach(p -> {
		long count = p.homework().values().stream()
		        .filter(v -> v == true)
		        .count();
		double rate = count * 100 / totalNumberOfEvents;
		
		String markdownForHomework = String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, totalNumberOfEvents), rate);
		writer.print(markdownForHomework);
 });
```

- 반복문 안의 코드의 가독성이 떨어진다.
- 계산문을 표현하고, 포맷이 드러나면서 의도보다는 구현쪽으로 가까워 졌다.

### 예제 - after

```java
participants.forEach(p -> {
				String markdownForHomework = getMarkdownForParticipant(totalNumberOfEvents, p);
        writer.print(markdownForHomework);
});

private String getMarkdownForParticipant(int totalNumberOfEvents, Participant p) {
        String markdownForHomework = String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, totalNumberOfEvents), getRate(totalNumberOfEvents, p));
        return markdownForHomework;
    }
```

- 출력하고자 하는 값(rate)를 함수로 추출하여 getMarkDownForParticipant의 매개변수를 줄일 수 있다.
- 기존 로직 부분의 구현 부분들이 숨겨져 가독성이 높아진다.

## 매개변수 객체 만들기 (Introduce Paramter Object)

---

- 같은 매개변수들이 여러 메서드에 걸쳐 나타난다면 그 매개변수들을 묶은 자료 구조를 만들 수 있다.
- 그렇게 만든 자료구조의 특징
    - 해당 데이터간의 관계를 보다 명시적으로 나타낼 수 있다.
    - 함수에 전달할 매개변수 개수를 줄일 수 있다.
    - 도메인을 이해하는데 중요한 역할을 하는 클래스로 발전할 수도 있다.

### 예제 - before

```java
private double getRate(int totalNumberOfEvents, Participant p) {
        long count = p.homework().values().stream()
                .filter(v -> v == true)
                .count();
        double rate = count * 100 / totalNumberOfEvents;
        return rate;
}

private String getMarkdownForParticipant(int totalNumberOfEvents, Participant p) {
		return String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p, totalNumberOfEvents), getRate(totalNumberOfEvents, p));
}
```

- totalNumberOfEvents와 Participant가 동시에 사용되고 있다.

### 예제 - milestone

```java
private String getMarkdownForParticipant(ParticipantPrinter participantPrinter) {
        return String.format("| %s %s | %.2f%% |\n", participantPrinter.p().username(), checkMark(participantPrinter.p(), participantPrinter.totalNumberOfEvents()), getRate(participantPrinter.totalNumberOfEvents(), participantPrinter.p()));
 }

...

public record ParticipantPrinter(int totalNumberOfEvents, Participant p) {
}
```

- intelliJ에서 제공하는 refactor중, introduce parameter Object 기능을 사용하여 ParticipantPrinter 적용
- 이보다 더 깔끔하게 만드는 방법이 있다.

### 예제 - after

```java
participants.forEach(p -> {
    String markdownForHomework = getMarkdownForParticipant(p);
    writer.print(markdownForHomework);
});

private double getRate(Participant p) {
    long count = p.homework().values().stream()
            .filter(v -> v == true)
            .count();
    double rate = count * 100 / this.totalNumberOfEvents;
    return rate;
}

private String getMarkdownForParticipant(Participant p) {
    return String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p), getRate(p));
}
```

- introduce field variable을 사용하여 자주 사용되지만, 값의 변경이 없는 매개변수들을 필드로 선언
- 불필요한 매개변수를 줄일 수 있다.

### 느낀 점

- 해당 메서드들에 공통적으로 사용되는 변수들은 로직을 잘 따져보고, Class 변수로 승격시킨다.
- 메서드들의 매개변수를 줄인다는 이점이 있지만, Runtime debuging을 해봐야한다.
- 외부에서 접근하지 못하도록 승격 시키고, atomic을 고려해야한다.

## 객체 통째로 넘기기 (Preserve Whole Object)

---

- 어떤 한 레코드에서 구할 수 있는 여러 값들을 함수에 전달하는 경우, 해당 매개변수를 레코드 하나로 교체할 수 있다.
- 매개변수 목록을 줄일 수 있다. (향후에 추가할지도 모를 매개변수까지도…)
- 이 기술을 적용하기 전에 의존성을 고려해야 한다.
- 어쩌면 해당 메서드의 위치가 적절하지 않을 수도 있다. (기능 편애 “Feature Envy” 냄새에 해당한다.)

### 예제 - before

```java
participants.forEach(p -> {
    String markdownForHomework = getMarkdownForParticipant(p.username(), p.homework());
    writer.print(markdownForHomework);
});

double getRate(Map<Integer, Boolean> homework) {
	  long count = homework.values().stream()
          .filter(v -> v == true)
          .count();
	  return (double) (count * 100 / this.totalNumberOfEvents);
}

private String getMarkdownForParticipant(String username, Map<Integer, Boolean> homework) {
    return String.format("| %s %s | %.2f%% |\n", username,
            checkMark(homework, this.totalNumberOfEvents),
            getRate(homework));
}
```

- getMarkdownForParticipant 메서드를 살펴보면, username, homework 매개변수를 전달하기 위해 participant 객체에서 가져오고 있다.
- 이를 객체 통째로 넘기기를 통해 불필요한 매개변수의 수를 줄일 수 있다.

### 예제 - after

```java
participants.forEach(p -> {
            String markdownForHomework = getMarkdownForParticipant(p);
            writer.print(markdownForHomework);
        });
    }
}

double getRate(Participant p) {
    long count = p.homework().values().stream()
            .filter(v -> v == true)
            .count();
    return (double) (count * 100 / this.totalNumberOfEvents);
}

private String getMarkdownForParticipant(Participant p) {
    return String.format("| %s %s | %.2f%% |\n", p.username(),
            checkMark(p, this.totalNumberOfEvents),
            getRate(p));
}
```

- preserve whole object를 통해 매개변수를 줄일 수 있었다.
- 다만, 함수가 Participant에 의존하는게 맞는가에 대해서 고민해봐야한다.
    - HashMap을 유지하여 다른 도메인에서 이를 보게끔 할 수 있게 해야하는가 등
- 이후에 해당 메서드가 이 위치에 존재하는것이 맞는 것인가등을 고려해야한다.

### 느낀 점

- Introduce Parameter Object와 동일해보이지만, 첫 설계시에 시도하기 좋다.
- 객체 통째로 넘기기를 적용해 봤을 때, 해당 적용된 메서드가 기존의 primitive 매개변수를 사용하는게 맞는지, 해당 객체에 대한 의존성을 있는게 맞는지 따져봐야 한다.
- 해당 메서드가 만약 public하게 공개되는 API인 경우, 알 수 없는 의존성이 생긴다.
- 또한, 통쨰로 넘긴 이후에 해당 메서드의 위치가 어떤 클래스에 존재하는게 적절한지 결정해야한다. → intelliJ의 move method를 고려하자

## 함수를 명령으로 바꾸기 (Replace Function with Command)

---

- 함수를 독립적인 객체인, Command로 만들어 사용할 수 있다.
- 커맨드 패턴을 적용하면 다음과 같은 장점을 취할 수 있다.
- 부가적인 기능으로 undo 기능을 만들 수도 있다.
- 더 복잡한 기능을 구현하는데 필요한 여러 메소드를 추가할 수 있다.
- 상속이나 템플릿을 활용할 수도 있다.
- 복잡한 메소드를 여러 메소드나 필드를 활용해 쪼갤 수도 있다.
- 대부분의 경우에 “커맨드” 보다는 “함수”를 사용하지만, 커맨드 말고 다른 방법이 없는 경우에만 사용한다

### 예제 - before

```java
try (FileWriter fileWriter = new FileWriter("participants.md");
     PrintWriter writer = new PrintWriter(fileWriter)) {
    participants.sort(Comparator.comparing(Participant::username));

    writer.print(header(participants.size()));

    participants.forEach(p -> {
        String markdownForHomework = getMarkdownForParticipant(p);
        writer.print(markdownForHomework);
    });
}

private String getMarkdownForParticipant(Participant p) {
	/* 생략 */
}

private String header() {
  /* 생략 */
}

private String checkMark(Participant p, int totalEvents) {
  /* 생략 */
}
```

- 향후에 여러 형식으로 출력할 수 있기 때문에, 확장 가능성이 있다.
- 만약 그런 경우에 현 상황에서는 확장성을 고려하지 않았기 때문에 코드가 지저분해질 여지가 다분하다.
- command로 뽑아내보자

### 예제 - after

```java
...
new StudyPrinter(this.totalNumberOfEvents, participants).execute();

...

public class StudyPrinter {

    private int totalNumberOfEvents;
    private List<Participant> participants;

    public StudyPrinter(int totalNumberOfEvents, List<Participant> participants) {
        this.totalNumberOfEvents = totalNumberOfEvents;
        this.participants = participants;
    }

    public void execute() throws IOException {
        try (FileWriter fileWriter = new FileWriter("participants.md");
             PrintWriter writer = new PrintWriter(fileWriter)) {
            this.participants.sort(Comparator.comparing(Participant::username));

            writer.print(header());

            this.participants.forEach(p -> {
                String markdownForHomework = getMarkdownForParticipant(p);
                writer.print(markdownForHomework);
            });
        }
    }

    private String getMarkdownForParticipant(Participant p) {
			/* 생략 */
    }

    private String header() {
	    /* 생략 */
		}

    private String checkMark(Participant p, int totalEvents) {
		  /* 생략 */
		}
}
```

- 기존의 코드에서 절반가량 줄일 수 있었고, Printer를 변경하는 경우에 대해서도 유연하다.
    - Command 패턴처럼 StudyPrinter를 interface로 생성하여 유연성을 강화시킬 수 있다.

### 느낀 점

- 함수 분리를 고민하고, 향후 더 복잡해질 가능성이 포착된다고 하면 커맨드 패턴으로 분리하는 것을 고려하자
- 함수 분리 후, 관련된 메서드들을 일부 한 클래스에 몰아두고, 매개 변수들을 확인하여 필드로 뽑아 낸다.
- 점점 제거해나가며 하나의 커맨드로 만들어 낸다.

## 조건문 분해하기 (Decompose Conditional)

---

- 여러 조건에 따라 달라지는 코드를 작성하다보면 종종 긴 함수가 만들어지는 것을 목격할 수 있다.
- “조건”과 “액션” 모두 “의도”를 표현해야 한다.
- 기술적으로는 “함수 추출하기”와 동일한 리팩토링이지만 의도만 다를 뿐이다.

### 예제 - before

```java
private Participant findParticipant(String username, List<Participant> participants) {
    Participant participant;
    if (participants.stream().noneMatch(p -> p.username().equals(username))) {
        participant = new Participant(username);
        participants.add(participant);
    } else {
        participant = participants.stream().filter(p -> p.username().equals(username)).findFirst().orElseThrow();
    }

    return participant;
}
```

- if - else가 1depth로 끝나긴 하지만, 의도가 드러나지 않는다.
- 구현 부분을 제거하고 의도를 드러내기 위해 조건문을 분해해보자

### 예제 - after

```java
private Participant findParticipant(String username, List<Participant> participants) {
    Participant participant;
    if (isNewParticipant(username, participants)) {
        participant = createNewParticipant(username, participants);
    } else {
        participant = findExistingParticipant(username, participants);
    }
    return participant;
}

private static Participant createNewParticipant(String username, List<Participant> participants) {
    Participant participant;
    participant = new Participant(username);
    participants.add(participant);
    return participant;
}

private static Participant findExistingParticipant(String username, List<Participant> participants) {
    return participants.stream().filter(p -> p.username().equals(username)).findFirst().orElseThrow();
}

private static boolean isNewParticipant(String username, List<Participant> participants) {
    return participants.stream().noneMatch(p -> p.username().equals(username));
}
```

- extract method를 적극 활용하여 구현 부분을 제거하면 의도를 드러내고 가독성 좋은 코드가 생성된다.
- if - else문에서도 멈출 수 있지만, 삼항 연산자를 도입하면 이를 좀 더 간소화시킬 수 있다.

```java
private Participant findParticipant(String username, List<Participant> participants) {
    return isNewParticipant(username, participants) ?
            createNewParticipant(username, participants) :
            findExistingParticipant(username, participants);
}
```

- 삼항 연산자 도입은 취향인거 같은데…

### 느낀 점

- extract method를 적극적으로 활용해야한다.

## 반복문 쪼개기 (Split Loop)

---

- 하나의 반복문에서 여러 다른 작업을 하는 코드를 쉽게 찾아볼 수 있다.
- 해당 반복문을 수정할 때 여러 작업을 모두 고려하며 코딩을 해야한다.
- 반복문을 여러개로 쪼개면 보다 쉽게 이해하고 수정할 수 있다.
- 성능 문제를 야기할 수 있지만, “리팩토링”은 “성능 최적화”와 별개의 작업이다. 리팩토링을 마친 이후에 성능 최적화를 시도할 수 있다.

### 예제 - before

```java
@Override
public void run() {
    try {
        GHIssue issue = ghRepository.getIssue(eventId);
        List<GHIssueComment> comments = issue.getComments();
        Date firstCreatedAt = null;
        Participant first = null;

        for (GHIssueComment comment : comments) {
            Participant participant = findParticipant(comment.getUserName(), participants);
            participant.setHomeworkDone(eventId);

            if (firstCreatedAt == null || comment.getCreatedAt().before(firstCreatedAt)) {
                firstCreatedAt = comment.getCreatedAt();
                first = participant;
            }
        }

        firstParticipantsForEachEvent[eventId - 1] = first;
        latch.countDown();
    } catch (IOException e) {
        throw new IllegalArgumentException(e);
    }
}
```

- 성능상의 이점 때문에 많은 사람들이 한 반복문 내부에서 다른 로직들을 사용한다.
- 먼저 동일한 의도를 드러내는 부분들을 분류하여 다른 반복문으로 쪼갠다.

### 예제 - after

```java
@Override
public void run() {
    try {
        GHIssue issue = ghRepository.getIssue(eventId);
        List<GHIssueComment> comments = issue.getComments();

        checkHomeWork(comments, eventId);
        firstParticipantsForEachEvent[eventId - 1] = findFirst(comments);
        
        latch.countDown();
    } catch (IOException e) {
        throw new IllegalArgumentException(e);
    }
}
```

- 반복문을 분리한 뒤, 각각을 extract method한다.
- 의도가 드러나는 가독성있는 코드를 제공할 수 있다.

### 느낀 점

- 한 루프 안에서 관련된 사항들을 처리하는게 효율적
- 하지만, 기능 추가하는 입장에서 생각해보면 같이 수행되는 작업에 대해서 항상 고려해야한다.
- 리팩토링을 다 수행한 다음에 성능 최적화를 하자.
- 루프를 복사해서 붙여놓고, 한 반복문 안에서 하나의 작업만 수행하도록 변경해보자
- 이후에 메서드로 추출
- Concurrency에 안전한 컬렉션을 사용해야하는 경우, new CopyOnWriteArrayList() 사용을 고려해보자

## 조건문을 다형성으로 바꾸기 (Replace Conditional with Polymorphism)

---

- 여러 타입에 따라 각기 다른 로직으로 처리해야하는 경우, 다형성을 적용해서 조건문을 보다 명확하게 분리할 수 있다. 반복되는 switch문을 각기 다른 클래스를 만들어 제거할 수 있다.
- 공통으로 사용되는 로직은 상위클래스에 두고, 달라지는 부분만 하위 클래스에 둠으로써, 달라지는 부분만 강조할 수 있다.
- 모든 조건문을 다형성으로 바꿔야하는 것은 아니다.

### 예제 - before

```java
public void execute() throws IOException {
    switch (printerMode) {
        case CVS -> {
            try (FileWriter fileWriter = new FileWriter("participants.cvs");
                 PrintWriter writer = new PrintWriter(fileWriter)) {
                writer.println(cvsHeader(this.participants.size()));
                this.participants.forEach(p -> {
                    writer.println(getCvsForParticipant(p));
                });
            }
        }
        case CONSOLE -> {
            this.participants.forEach(p -> {
                System.out.printf("%s %s:%s\n", p.username(), checkMark(p), p.getRate(this.totalNumberOfEvents));
            });
        }
        case MARKDOWN -> {
            try (FileWriter fileWriter = new FileWriter("participants.md");
                 PrintWriter writer = new PrintWriter(fileWriter)) {

                writer.print(header(this.participants.size()));

                this.participants.forEach(p -> {
                    String markdownForHomework = getMarkdownForParticipant(p);
                    writer.print(markdownForHomework);
                });
            }
        }
    }
}

private String getCvsForParticipant(Participant participant) {
		/* 생략 */
}

private String cvsHeader(int totalNumberOfParticipants) {
	/* 생략 */
}

private String getMarkdownForParticipant(Participant p) {
    /* 생략 */
}
```

- 다양한 출력형식에 따라 기능을 지원해주기 위해 불필요한 메서드 및 장황한 로직이 생겼다.
- 이를 다형성을 통해 극복해보자

### 예제 - after

```java
public abstract void execute() throws IOException;

public class CsvPrinter extends StudyPrinter {
    public CsvPrinter(int totalNumberOfEvents, List<Participant> participants) {
        super(totalNumberOfEvents, participants);
    }

    @Override
    public void execute() throws IOException {
        try (FileWriter fileWriter = new FileWriter("participants.csv");
             PrintWriter writer = new PrintWriter(fileWriter)) {
            writer.println(csvHeader(this.participants.size()));
            this.participants.forEach(p -> {
                writer.println(getCsvForParticipant(p));
            });
        }
    }
	...
}

public class ConsolePrinter extends StudyPrinter {
    public ConsolePrinter(int totalNumberOfEvents, List<Participant> participants) {
        super(totalNumberOfEvents, participants);
    }

    @Override
    public void execute() throws IOException {
        this.participants.forEach(p -> {
            System.out.printf("%s %s:%s\n", p.username(), checkMark(p), p.getRate(this.totalNumberOfEvents));
        });
    }
}

...

public class MarkdownPrinter extends StudyPrinter {

    public MarkdownPrinter(int totalNumberOfEvents, List<Participant> participants) {
        super(totalNumberOfEvents, participants);
    }

    @Override
    public void execute() throws IOException {
        try (FileWriter fileWriter = new FileWriter("participants.md");
             PrintWriter writer = new PrintWriter(fileWriter)) {

            writer.print(header(this.participants.size()));

            this.participants.forEach(p -> {
                String markdownForHomework = getMarkdownForParticipant(p);
                writer.print(markdownForHomework);
            });
        }
    }
}
```

- 상속을 통해, PrintMode라는 Enum 타입을 지울 수 있었고, 기존 사용하던 비즈니스 로직에서도 새로운 타입이 추가될 때, 굳이 수정이 필요해지지 않았다.
- 어떤 곳에서든 결국 타입을 던져주긴 하기 때문에, 취향에 따라 소스코드에 직접 지정하여 사용해도 크게 상관 없다

### 느낀 점

- 공통 부분이 있고, 달라지는 부분(크게 보면 같은 로직인데, 내부 구현이나 약간의 특색이 있는걸 의미하는 듯)이 있는 경우에만 조건문을 다형성으로 바꾸는 것을 고려한다.