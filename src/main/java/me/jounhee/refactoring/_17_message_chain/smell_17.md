# 냄새 17. 메세지 체인

태그: 위임 숨기기, 함수 추출하기

## 메세지 체인

---

- 레퍼런스를 따라 계속해서 메소드 호출이 이어지는 코드.
    - 예) this.member.getCredit().getLevel().getDescription()
- 해당 코드의 클라이언트가 코드 체인을 모두 이해해야 한다.
- 체인 중 일부가 변경된다면 클라이언트의 코드도 변경해야 한다.

### 관련 리팩토링

- “위임 숨기기 (Hide Delegate)”를 사용해 메시지 체인을 캡슐화를 할 수 있다.
- “함수 추출하기 (Extract Function)”로 메시지 체인 일부를 함수로 추출한 뒤, “함수 옮기기(Move Function)”으로 해당 함수를 적절한 이동할 수 있다.

## 위임 숨기기

---

- 캡슐화 (Encapsulation)란 어떤 모듈이 시스템의 다른 모듈을 최소한으로 알아야 한다는 것이다. 그래야 어떤 모듈을 변경할 때, 최소한의 모듈만 그 변경에 영향을 받을 것이고, 그래야 무언가를 변경하기 쉽다.
- 처음 객체 지향에서 캡슐화를 배울 때 필드를 메소드로 숨기는 것이라 배우지만, 메소드 호출도 숨길 수 있다.
    - person.department().manager(); -> person.getManager()
    - 이전의 코드는 Department를 통해 Manager에 접근할 수 있다는 정보를 알아야 하지만, getManager()를 통해 위임을 숨긴다면 클라이언트는 person의 getManager()만 알아도 된다. 나중에 getManager() 내부 구현이 바뀌더라도 getManager()를 사용한 코드는 그대로유지할 수 있다.


### 절차

1. 위임 객체의 각 메서드에 해당하는 위임 메서드를 서버에 생성한다.
2. 클라이언트가 위임 객체 대신 서버를 호출하도록 수정한다.
3. 모두 수정했다면, 서버로부터 위임 객체를 얻는 접근자를 제거한다.

### 예제 - before

```java
public class Person {

    private String name;

    private Department department;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }
}

public class Department {

    private String chargeCode;

    private Person manager;

    public Department(String chargeCode, Person manager) {
        this.chargeCode = chargeCode;
        this.manager = manager;
    }

    public String getChargeCode() {
        return chargeCode;
    }

    public Person getManager() {
        return manager;
    }
}
```

- 만약, Person 객체에서부터 Manager를 보려고한다면, person.getDepartment().getManager() 식으로 메세지를 체이닝해야한다.
- 결국 Client에서는 해당 구조등 상세 내부 정보들을 알아야한다.
- 이를 위임 숨기기로 해결해보자.

### 예제 - after

```java
public class Person {

    private String name;

    private Department department;

    public Person(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

		public Person getManager() {
        return this.department.getManager();
    }
}
```

- Client는 더이상 Person의 department에 접근하여 manager를 직접 접근할 필요가 없다.
- person.getDepartment().getManager() → person.getManager() 식으로 코드가 간략하게 정리되며 내부 구조에 대해서 Client가 알 필요가 없다.