# 냄새 14. 성의없는 요소

태그: 계층 합치기, 클래스 인라인, 함수 인라인

## 성의없는 요소

---

- 여러 프로그래밍적인 요소(변수, 메서드, 클래스등)을 만드는 이유
    - 나중에 발생할 변화를 대비해서
    - 해당 함수 또는 클래스를 재사용하려고
    - 의미있는 이름을 지어주려고
- 가끔 그렇게 예상하고 만들어 놓은 요소들이 기대에 부응하지 못하는 경우가 있는데, 그런 경우에 해당 요소들을 제거해야 한다.
- 관련 리팩토링 기술
    - “함수 인라인”
    - “클래스 인라인”
    - 불필요한 상속 구조는 “계층 합치기”를 사용할 수 있다.

## 계층 합치기

---

- 상속 구조를 리팩토링 하는 중에 기능을 올리고 내리다 보면 하위 클래스와 상위 클래스 코드에 차이가 없는 경우가 발생할 수 있다. 그런 경우에 그 둘을 합칠 수 있다.
- 하위 클래스와 상위 클래스 중에 어떤 것을 없애야 하는가? (둘 중에 보다 이름이 적절한 쪽을 선택하지만, 애매하다면 어느쪽을 선택해도 문제없다.)

### 절차

1. 두 클래스 중 제거할 것을 고른다.
    1. 미래를 생각하여 더 적합한 이름의 클래스를 남기자. 둘 다 적절치 않다면 임의로 하나를 고른다.
2. **필드 올리기**와 **메서드 올리기** 혹은 **필드 내리기**와 **메서드 내리기**를 적용하여 모든 요소를 하나의 클래스로 옮긴다.
3. 제거할 클래스를 참조하던 모든 코드가 남겨질 클래스를 참조하도록 고친다.
4. 빈 클래스를 제거한다.
5. 테스트한다.

### 예시 - before

```java
public class Reservation {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<String> members;
    private String owner;
    private boolean paid;
}

public class CourtReservation extends Reservation {
    private String courtNumber;
}
```

- Reservation이라는 정보를 다른 목적으로 사용하지 않는다던지, CourtReservation에 대한 특정 로직이 존재하지 않는다면, 두 클래스를 합치는게 더 용이할 수 있다.

### 예제 - after

```java
public class Reservation {
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private List<String> members;
    private String owner;
    private boolean paid;

    private String courtNumber;
}
```

- IntelliJ의 refactor → pull up members로 손쉽게 상위 클래스로 합칠 수 있다.
- 반대의 경우도 push members down으로 손쉽게 하위 클래스로 내릴 수 있으므로 해당 기능들을 잘 사용하자.