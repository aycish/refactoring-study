# 냄새 18. 중개자

태그: 중개자 제거하기, 함수 인라인

## 중개자

---

- 캡슐화를 통해 내부의 구체적인 정보를 최대한 감출 수 있다.
    - 하지만, 너무 과도하게 캡슐화가 되어 있다면, 공개되어야 하는 API를 꼭 누군가를 거쳐서 사용하게 되는 상황이 온다.
    - 따라서 캡슐화는 적정선을 지켜야한다.
- 그러나, 어떤 클래스의 메소드가 대부분 다른 클래스로 메소드 호출을 위임하고 있다면 중개자를 제거하고 클라이언트가 해당 클래스를 직접 사용하도록 코드를 개선할 수 있다.

### 의심 시점

- Client가 특정 클래스를 왜 직접 접근을 못할까? 라는 생각이 들때 쯤

### 관련 리팩토링

- “중개자 제거하기 (Remove Middle Man)” 리팩토링을 사용해 클라이언트가 필요한 클래스를 직접 사용하
  도록 개선할 수 있다.
- “함수 인라인 (Inlince Function)”을 사용해서 메소드 호출한 쪽으로 코드를 보내서 중재자를 없앨 수도 있다.
- “슈퍼클래스를 위임으로 바꾸기 (Replace Superclass with Delegate)”
- “서브클래스를 위임으로 바꾸기 (Replace Subclass with Delegate)”

## 중개자 제거하기

---

- “위임 숨기기”의 반대에 해당하는 리팩토링.
- 필요한 캡슐화의 정도는 시간에 따라 그리고 상황에 따라 바뀔 수 있다.
- 캡슐화의 정도를 “중재자 제거하기”와 “위임 숨기기” 리팩토링을 통해 조절할 수 있으다.
- 위임하고 있는 객체를 클라이언트가 사용할 수 있도록 getter를 제공하고, 클라이언트는 메시지 체인을 사용하도록 코드를 고친 뒤에 캡슐화에 사용했던 메소드를 제거한다.
- Law of Demeter를 지나치게 따르기 보다는 상황에 맞게 활용하도록 하자.
    - 디미터의 법칙, “가장 가까운 객체만 사용한다.”

### 절차

1. 위임 객체를 얻는 게터를 만든다.
2. 위임 메서드를 호출하는 클라이언트가 모두 이 게터를 거치도록 수정한다. 하나씩 바꿀 때마다 테스트한다.
3. 모두 수정했다면 위임 메서드를 삭제한다.
    1. 자동 리팩토링 도구를 사용할 때는 위임 필드를 캡슐화한 다음, 이를 사용하는 모든 메서드를 인라인한다.

### 예제 - before

```java
public class Department {
    private Person manager;

    public Department(Person manager) {
        this.manager = manager;
    }
    public Person getManager() {
        return manager;
    }
}

## 중재자가된 Person Class
public class Person {

    private Department department;

    private String name;

    public Person(String name, Department department) {
        this.name = name;
        this.department = department;
    }

    public Person getManager() {
        return this.department.getManager();
    }
}
```

- Person의 메서드는 department에 직접 Client가 접근해도 크게 무리가 없어 보인다.
- getManager와 같은 중재자 메서드를 제거하여 중재자 패턴을 없애보자.

### 예제 - after

```java
public class Person {

    private Department department;

    private String name;

    public Person(String name, Department department) {
        this.name = name;
        this.department = department;
    }

    public getDepartment() {
        return department;
    }
}
```

- 이런식으로 department 객체를 반환을 하던, Client에서 직접 Department 객체를 사용하던식으로 취향에 따라 정리해도 된다.

```java
## Client 코드

...

somthing = person.getDepartment().getManager();

...

```