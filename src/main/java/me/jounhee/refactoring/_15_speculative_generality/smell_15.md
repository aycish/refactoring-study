# 냄새 15. 추측성 일반화

태그: 계층 합치기, 죽은 코드 제거하기, 클래스 인라인, 함수 선언 변경하기, 함수 인라인

## 추측성 일반화

---

- 나중에 추가될 기능들을 염두해두고, 여러 경우에 필요한 기능을 추가해 뒀지만, 결국에 쓰이지 않는 코드를 생성한 경우
- XP의 YAGNI (You aren’t gonna need it) 원칙을 따르자.
    - 지금 당장 필요한 기능이 아니라면, 생성해 두지 않는다는 원칙

### 관련 리팩토링 기술

- “계층 합치기” → 추상 클래스가 유효하지 않은 경우
- “함수 인라인” or “클래스 인라인” → 불필요한 위임이 있는 경우
- “함수 선언 변경하기” → 사용하지 않는 매개변수를 가진 함수
- “죽은 코드 제거하기” → 오로지 테스트 코드에서만 사용하고 있는 코드인 경우

## 죽은 코드 제거하기

---

- 사용하지 않는 코드가 애플리케이션 성능이나 기능에 영향을 끼치지는 않는다.
    - 최신 컴파일러들은 해당 코드들을 알아서 제거해준다.
- 하지만, 해당 소프트웨어가 어떻게 동작하는지 이해하려는 사람들에게 꽤 고통을 줄 수있다.
    - 해당 코드들이 호출되지 않으니 무시해도 되는 함수이다라는 표시를 남기지 않기 때문이다.
- 실제로 나중에 필요해질 코드라 하더라도 지금 쓰이지 않는 코드라면 (주석으로 감싸는게아니라) 삭제해야 한다.
- 나중에 정말로 다시 필요해진다면 git과 같은 버전 관리 시스템을 사용해 복원할 수 있다.
    - 어떤 리비전에서 삭제했는지를 커밋 메세지로 남겨두자.

### 절차

1. 죽은 코드를 외부에서 참조할 수 있는 경우라면 혹시라도 호출하는 곳이 있는지 확인한다.
2. 없다면 죽은 코드를 제거한다.
3. 테스트한다.

### 예제

```java
public class Reservation {
    private String title;
    private LocalDateTime from;
    private LocalDateTime to;
    private LocalDateTime alarm;
    
    public Reservation(String title, LocalDateTime from, LocalDateTime to) {
        this.title = title;
        this.from = from;
        this.to = to;
    }
    public void setAlarmBefore(int minutes) {
        this.alarm = this.from.minusMinutes(minutes);
    }
    public LocalDateTime getAlarm() {
        return alarm;
    }
}
```

- intelliJ에서는 No usage, 1 usage, 2 usages 등 Code vision 기능을 통해서 제공해주고 있다.
- 해당 기능들을 사용해서 Test Code에서만 참조하고 있는지 등을 체크할 수 있다.