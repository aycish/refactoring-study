# 냄새 11. 기본형 집착

태그: 기본형을 객체로 바꾸기, 매개변수 객체 만들기, 조건부 로직을 다형성으로 바꾸기, 클래스 추출하기, 타입 코드를 서브클래스로 바꾸기

## 강의 내용 정리

---

- 어플리케이션이 다루고 있는 도메인에 필요한 기본 타입을 만들지 않고, 프로그래밍 언어가 제공하는 기본 타입을 사용하는 경우가 많다.
    - ex. 전화번호, 좌표, 돈, 범위, 수량 등
- 기본형으로는 단위(인치 vs 미터) 또는 표기법을 표현하기 어렵다.

### 관련 리팩토링 기술

- 기본형을 객체로 바꾸기
- 타입 코드를 서브클래스로 바꾸기
- 조건부 로직을 다형성으로 바꾸기
- 클래스 추출하기
- 매개변수 객체 만들기

## 기본형을 객체로 바꾸기

---

- 개발 초기에는 기본형으로 표현한 데이터가 나중에는 해당 데이터와 관련있는 다양한 기능을 필요로하는 경우가 발생한다.
    - 예시 ) 문자열로 표현하던 전화번호의 지역 코드가 필요하거나 다양한 포맷을 지원하는 경우
    - 예시 ) 숫자로 표현하던 온도의 단위를 변환하는 경우
- 기본형을 사용한 데이터를 감싸 줄 클래스를 만들면, 필요한 기능을 추가할 수 있다.
- 그렇다고 초기 개발단계에서 모두 고려할 필요는 없다. 리팩토링 기술을 익혀두고, 개발이 필요한 경우 잘 적용해보자.

### 객체 변경 시점

- 단순 출력 이상의 기능이 데이터나 타입에 필요하게되는 경우, 전용 클래스를 정의한다.

### 절차

1. **변수를 캡슐화**한다.
2. 단순한 값 클래스를 생성한다.
    1. 생성자는 기존 값을 인수로 받아서 저장한다.
    2. 이 값을 반환하는 게터를 추가한다.
3. 값 클래스의 인스턴스를 새로 만들어서 필드에 저장하도록 세터를 수정한다.
    1. 이미 존재한다면, 필드 타입을 적절히 변경한다.
4. 새로 만든 클래스의 게터를 호출한 결과를 반환하도록 게터를 수정한다.
5. **함수 이름을 바꾸면**, 원본 접근자의 동작을 더 잘 드러낼 수 있는지 검토한다.
    1. **참조를 값으로 바꾸거**나 **값을 참조로 바꾸**면 새로 만든 객체의 역할이 더 잘드러나는지 또한 검토한다.

### 예시 - before

```java
public class Order {

    private String priority;

    public Order(String priority) {
        this.priority = priority;
    }

    public String getPriority() {
        return priority;
    }
}

public class OrderProcessor {

    public long numberOfHighPriorityOrders(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getPriority() == "high" || o.getPriority() == "rush")
                .count();
    }
}
```

- Order에 있는 Priority를 활용하여, 높은 우선순위의 Order를 걸러내는 기능
- 초기 개발단계에서는 high, rush등 문자열로 담을 수 있는 우선순위만 있지만, 추후에는 대소비교등이 필요할 수 있다.
- 또한, String type이기 때문에, 아무 문자열이나 올 수 있다. 즉, Type safety가 보장되지 않는다.
- Priority를 표현할 수 있는 클래스를 따로 만들

### 예시 - after

```java
public class Order {

    private Priority priority;

    public Order(String priorityValue) {
        this(new Priority(priorityValue));
    }

    public Order(Priority priority) {
        this.priority = priority;
    }

    public Priority getPriority() {
        return priority;
    }
}

public class OrderProcessor {

    public long numberOfHighPriorityOrders(List<Order> orders) {
        return orders.stream()
                .filter(o -> o.getPriority().higherThan(new Priority("normal")))
                .count();
    }
}

public class Priority {

    private String value;
    private List<String> legalValues = List.of("low", "normal", "high", "rush");

    public Priority(String value) {
        if (legalValues.contains(value))
            this.value = value;
        else
            throw new IllegalArgumentException("illegal value for prioirty " + value);
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.value;
    }

    private int index() {
        return this.legalValues.indexOf(this.value);
    }

    public boolean higherThan(Priority other) {
        return this.index() > other.index();
    }
}
```

- type safety를 만족시킬 수 있는 Priority 클래스 생성

## 타입 코드를 서브클래스로 바꾸기

---

- 비슷하지만 다른 것들을 표현해야 하는 경우, 문자열(String), 열거형(enum), 숫자(int)등으로 표현하기도 한다.
    - 예) 주문 타입, “일반 주문”, “빠른 주문”
    - 예) 직원 타입, “엔지니어”, “매니저”, “세일즈”

### 서브 클래스 적용 시점

- 조건에 따라 다르게 동작하는 로직이 여러개 존재할 때
    - 해당 메서드들에게 **조건부 로직을 다형성으로 바꾸기** 적용 가능
- 특정 타입에서만 의미가 있는 값을 사용하는 필드나 메서드가 있을 때
    - **서브 클래스를 만들고, 필요한 서브클래스만 필드를 갖도록 정리한다. (필드 내리기)**

### 절차

1. 타입 코드 필드를 자가 캡슐화한다.
2. 타입코드 값 하나를 선택하여 그 값에 해당하는 서브 클래스를 만든다. 타입 코드 게터 메서드를 오버라이드하여 해당 타입 코드의 리터럴 값을 반환하게한다.
3. 매개변수로 받은 타입코드와 방금 만든 서브클래스를 매핑하는 선택 로직을 만든다.
    1. 직접 상속일 때는 생성자를 **팩터리 함수로 바꾸기**를 적용하고 선택 로직을 팩토리에 넣는다.
    2. 간접 상속일 떄는 선택 로직을 생성자에 두는 방향으로 진행한다.
4. 타입 코드 값 각각에 대해 서브클래스 생성과 선택 로직 추가를 반복한다.
5. 타입 코드 필드를 제거한다.
6. 타입 코드 접근자를 이용하는 메서드 모두에 **메서드 내리기**와 **조건부 로직을 다형성으로 바꾸기**를 적용한다.

### 예제1 - before : 직접 상속을 사용할 수 있는 경우 (아직 상속을 사용하고 있지 않은 경우)

```java
public class Employee {

    private String name;

    private String type;

    public Employee(String name, String type) {
        this.validate(type);
        this.name = name;
        this.type = type;
    }

    private void validate(String type) {
        List<String> legalTypes = List.of("engineer", "manager", "salesman");
        if (!legalTypes.contains(type)) {
            throw new IllegalArgumentException(type);
        }
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
```

- 직접 상속 적용이 가능한 경우
- 팩토리 메서드를 생성하여 상속한 클래스들을 생성하여 반환하는 메서드를 생성한다.

### 예제1 - after

```java
public abstract class Employee {

    private String name;

    protected Employee(String name) {
        this.name = name;
    }

    public static Employee createEmployee(String name, String type) {
        return switch (type) {
            case "engineer" -> new Engineer(name);
            case "manager" -> new Manager(name);
            case "salesman" -> new Salesman(name);
            default -> throw new IllegalArgumentException(type);
        };
    }

    protected abstract String getType();
    
    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + getType() + '\'' +
                '}';
    }
}

## 상속받은 클래스
public class Engineer extends Employee {

    public Engineer(String name) {
        super(name);
    }

    @Override
    public String getType() {
        return "engineer";
    }
}

.. 이하 동일
```

- Employee에서 type을 제거하고, 상속으로 이를 표현한다.

### 예제2- before (이미 상속 구조를 사용하고 있는 경우)

```java
public class Employee {

    private String name;

    private String type;

    public Employee(String name, String type) {
        this.validate(type);
        this.name = name;
        this.type = type;
    }

    private void validate(String type) {
        List<String> legalTypes = List.of("engineer", "manager", "salesman");
        if (!legalTypes.contains(type)) {
            throw new IllegalArgumentException(type);
        }
    }

    public String capitalizedType() {
        return this.type.substring(0, 1).toUpperCase() + this.type.substring(1).toLowerCase();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
```

- Employee는 Full-time employee등 이미 상속 구조를 갖고 있기 때문에, 직접적으로 서브 클래스를 둘 수 없다.
- type과 관련된 로직들이 많이 보이므로, tpye에 대한 서브 클래스를 두어, 관련 로직들을 모두 위임하는 방식으로 리팩토링 가능하다.

### 예제2- after

```java
public class Employee {

    private String name;
    private EmployeeType type;

    public Employee(String name, String typeValue) {
        this.name = name;
        this.type = this.employeeType(typeValue);
    }

    private EmployeeType employeeType(String type) {
        return switch (type) {
            case "engineer" -> new Engineer();
            case "manager" -> new Manager();
            case "salesman" -> new Salesman();
            default -> throw new IllegalArgumentException(type);
        };
    }

    public String capitalizedType() {
				/* Employee type에게 위임한다. */
        return this.type.capitalizedType();
    }

    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", type='" + this.type.toString() + '\'' +
                '}';
    }
}

public class EmployeeType {
    /* 위임 */
    public String capitalizedType() {
        return this.toString().substring(0, 1).toUpperCase() + this.toString().substring(1).toLowerCase();
    }
}

/* Engineer, Salesman class 모두 동일 */
public class Manager extends EmployeeType {
    @Override
    public String toString() {
        return "manager";
    }
}

```

- type을 subclass화 하고, capitalizedType과 같은 메서드들의 기능 구현을 EmployeeType에게 위임하였다.
- 상기와 같은 방식으로 type 자체에 대한 객체화를 적용하여 추후에 타입별로 필요한 로직이나 공통으로 적용될 로직들을 추가할 여지를 두었다.

## 조건부 로직을 다형성으로 바꾸기

---

- 복잡한 조건식을 상속과 다형성을 사용해 코드를 보다 직관적으로 구조화할 수 있다.
    - switch문을 사용해서 타입에 따라 각기 다른 로직을 사용하는 코드
    - case문과 그 변형 동작 (특정 타입에 특정 로직이 추가로 실행되는 경우)
- 기본 동작과 특수한 기능이 섞여있는 경우에 상속 구조를 만들어서 기본 동작을 상위클래스에 두고, 특수한 기능을 하위 클래스로 옮겨서 각 타입에 따른 “차이점”을 강조할 수 있다.
- 모든 조건문을 다형성을 활용해 좀 더 나은 코드로 만들 수 있는 경우에만 적용한다.

### 절차

1. 다형적 동작을 표현하는 클래스들이 아직 없다면 생성해준다.
    1. 이왕이면 적합한 인스턴스를 알아서 만들어 반환하는 팩토리 함수도 함께 생성해준다.
2. 호출하는 코드에서 팩토리 함수를 사용하게 한다.
3. 조건부 로직 함수를 슈퍼 클래스로 옮긴다.
    1. 조건부 로직이 온전한 함수로 분리되어 있지 않다면 먼저 함수로 추출한다.
4. 서브 클래스 중 하나를 선택한다. 서브 클래스에서 슈퍼클래스의 조건부 로직 메서드를 오버라이드 한다.
    1. 조건부 문장 중 선택된 서브클래스에 해당하는 조건절을 서브클래스 메서드로 복사한 다음 적절히 수정한다.
5. 같은 방식으로 각 조건절을 해당 서브클래스에서 메서드로 구현한다.
6. 슈퍼클래스 메서드에는 기본 동작 부분만 남긴다. 혹은 슈퍼클래스가 추상 클래스여야 한다면, 이 메서드를 추상으로 선언하거나 서브 클래스에서 처리해야 함을 알리는 에러를 던진다.

### 예제 - before

```java
public class Employee {

    private String type;

    private List<String> availableProjects;

    public Employee(String type, List<String> availableProjects) {
        this.type = type;
        this.availableProjects = availableProjects;
    }

    public int vacationHours() {
        return switch (type) {
            case "full-time" -> 120;
            case "part-time" -> 80;
            case "temporal" -> 32;
            default -> 0;
        };
    }

    public boolean canAccessTo(String project) {
        return switch (type) {
            case "full-time" -> true;
            case "part-time", "temporal" -> this.availableProjects.contains(project);
            default -> false;
        };
    }
}

```

- Type에 따라 각기 다른 로직 (vacationHours)을 실행한다.
- 다형성을 활용하여 해당 로직들을 분리할 수 있어보이는 예제 코드
- 하위 클래스에서는 vacationHours와 canAccessTo 메서드를 오버라이드하여 각 타입에 맞게 구현하면 깔끔해질 여지가 있다.

### 예제 - after

```java
public abstract class Employee {

    protected List<String> availableProjects;

    public Employee(List<String> availableProjects) {
        this.availableProjects = availableProjects;
    }

    public Employee() {
    }

    public int vacationHours() {
        return 0;
    }

    public boolean canAccessTo(String project) {
        return this.availableProjects.contains(project);
    }
}

## 하위 클래스
public class FullTimeEmployee extends Employee {

    @Override
    public int vacationHours() {
        return 120;
    }

    @Override
    public boolean canAccessTo(String project) {
        return true;
    }
}

public class PartTimeEmployee extends Employee {
    public PartTimeEmployee(List<String> availableProjects) {
        super(availableProjects);
    }

    @Override
    public int vacationHours() {
        return 80;
    }
}

public class TemporalEmployee extends Employee {
    public TemporalEmployee(List<String> availableProjects) {
        super(availableProjects);
    }

    @Override
    public int vacationHours() {
        return 32;
    }
}
```

- 상위 클래스인 Employee는 Abstract로 생성해준다.
    - 무조건 하위 클래스 타입으로 객체를 생성할 것이기 때문
- FullTimeEmployee 클래스의 경우는 모든 프로젝트에 접근 가능하기 때문에 따로 오버라이드하여 구현해준다.
- 이외의 하위클래스의 경우, availableProject에 해당 project가 포함되어있는지만 확인하면 되기 때문에 Abstract Class에서 이를 구현해준다.
- vacationHours만 하위클래스에서 오버라이드하여 기존 로직의 값들을 풀어준다.

### 예제2 - befroe

```java
public class VoyageRating {

    private Voyage voyage;

    private List<VoyageHistory> history;

    public VoyageRating(Voyage voyage, List<VoyageHistory> history) {
        this.voyage = voyage;
        this.history = history;
    }

    public char value() {
        final int vpf = this.voyageProfitFactor();
        final int vr = this.voyageRisk();
        final int chr = this.captainHistoryRisk();
        return (vpf * 3 > (vr + chr * 2)) ? 'A' : 'B';
    }

    private int captainHistoryRisk() {
        int result = 1;
        if (this.history.size() < 5) result += 4;
        result += this.history.stream().filter(v -> v.profit() < 0).count();
        if (this.voyage.zone().equals("china") && this.hasChinaHistory()) result -= 2;
        return Math.max(result, 0);
    }

    private int voyageRisk() {
        int result = 1;
        if (this.voyage.length() > 4) result += 2;
        if (this.voyage.length() > 8) result += this.voyage.length() - 8;
        if (List.of("china", "east-indies").contains(this.voyage.zone())) result += 4;
        return Math.max(result, 0);
    }

    private int voyageProfitFactor() {
        int result = 2;

        if (this.voyage.zone().equals("china")) result += 1;
        if (this.voyage.zone().equals("east-indies")) result +=1 ;
        if (this.voyage.zone().equals("china") && this.hasChinaHistory()) {
            result += 3;
            if (this.history.size() > 10) result += 1;
            if (this.voyage.length() > 12) result += 1;
            if (this.voyage.length() > 18) result -= 1;
        } else {
            if (this.history.size() > 8) result +=1 ;
            if (this.voyage.length() > 14) result -= 1;
        }

        return result;
    }

    private boolean hasChinaHistory() {
        return this.history.stream().anyMatch(v -> v.zone().equals("china"));
    }

}
```

- 대부분은 제너럴한 로직을 수행하지만, 특수한 경우에만 특수한 로직이 필요한 예제
    - voyageProfitFactor (china인 경우)
    - voyageRisk Method (china인 경우)
- 따라서 일반적인 VoyageRating을 사용할 것이냐, China인 경우만 특수 로직이 추가된 형태로 사용할 것이냐를 결정할 수 있도록 클래스를 분리하자

### 예제2- after

```java
## Factory Class 도입
public class RatingFactory {

    public static VoyageRating createRating(Voyage voyage, List<VoyageHistory> history) {
        if (voyage.zone().equals("china") && hasChinaHistory(history)) {
            return new ChinaExperiencedVoyageRating(voyage, history);
        } else {
            return new VoyageRating(voyage, history);
        }
    }

    private static boolean hasChinaHistory(List<VoyageHistory> history) {
        return history.stream().anyMatch(v -> v.zone().equals("china"));
    }
}

## Default Class
public class VoyageRating {

    protected Voyage voyage;

    protected List<VoyageHistory> history;

    public VoyageRating(Voyage voyage, List<VoyageHistory> history) {
        this.voyage = voyage;
        this.history = history;
    }

    public char value() {
        final int vpf = this.voyageProfitFactor();
        final int vr = this.voyageRisk();
        final int chr = this.captainHistoryRisk();
        return (vpf * 3 > (vr + chr * 2)) ? 'A' : 'B';
    }

    protected int captainHistoryRisk() {
        int result = 1;
        if (this.history.size() < 5) result += 4;
        result += this.history.stream().filter(v -> v.profit() < 0).count();
        return Math.max(result, 0);
    }

    protected int voyageRisk() {
        int result = 1;
        if (this.voyage.length() > 4) result += 2;
        if (this.voyage.length() > 8) result += this.voyage.length() - 8;
        if (List.of("china", "east-indies").contains(this.voyage.zone())) result += 4;
        return Math.max(result, 0);
    }

    protected int voyageProfitFactor() {
        int result = 2;
        if (this.voyage.zone().equals("china")) result += 1;
        if (this.voyage.zone().equals("east-indies")) result +=1 ;
        result += voyageLengthFactor();
        result += historyLengthFactor();

        return result ;
    }

    protected int voyageLengthFactor() {
        return (this.voyage.length() > 14) ? -1 : 0;
    }

    protected int historyLengthFactor() {
        return (this.history.size() > 8) ? 1 : 0;
    }
}

## China Class (특수 로직)
public class ChinaExperiencedVoyageRating extends VoyageRating {
    public ChinaExperiencedVoyageRating(Voyage voyage, List<VoyageHistory> history) {
        super(voyage, history);
    }

    @Override
    protected int voyageRisk() {
        int result = super.voyageRisk() - 2;
        return Math.max(result, 0);
    }

    @Override
    protected int voyageProfitFactor() {
        return super.voyageProfitFactor() + 3;
    }

    @Override
    protected int voyageLengthFactor() {
        int result = 0;
        if (this.voyage.length() > 12) result += 1;
        if (this.voyage.length() > 18) result -= 1;
        return result;
    }

    @Override
    protected int historyLengthFactor() {
        return (this.history.size() > 10) ? 1 : 0;
    }
}
```

- China인 경우, 동작하게 되는 특수한 로직들을 Override 형태로 풀어준다.