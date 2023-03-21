# 냄새 16. 임시 필드

태그: 클래스 추출하기, 특이 케이스 추가하기, 함수 옮기기

## 임시 필드

---

- 클래스에 있는 어떤 필드가 특정한 경우에만 값을 갖는 경우.
    - NULL, “empty”등 특정 상태를 임시적으로 나타낼때 사용하는 필드들
- 어떤 객체의 필드가 “특정한 경우에만” 값을 가진다는 것을 이해하는 것은 일반적으로 예상하지 못하기 때문에 이해하기 어렵다.

### 관련 리팩토링

- “클래스 추출하기 (Extract Class)”를 사용해 해당 변수들을 옮길 수 있다.
- “함수 옮기기 (Move Function)”을 사용해서 해당 변수를 사용하는 함수를 특정 클래스로 옮길 수 있
  다.
- “특이 케이스 추가하기 (Introduce Special Case)”를 적용해 “특정한 경우”에 해당하는 클래스를
  만들어 해당 조건을 제거할 수 있다.

## 특이 케이스 추가하기

---

- 어떤 필드의 특정한 값에 따라 동일하게 동작하는 코드가 반복적으로 나타난다면, 해당 필드를 감싸는 “특별한 케이스”를 만들어 해당 조건을 표현할 수 있다.
- 이러한 매커니즘을 “특이 케이스 패턴”이라고 부르고 “Null Object 패턴”을 이러한 패턴의 특수한 형태라고 볼 수 있다.

### 절차

리팩토링 대상이 될 속성을 담은 데이터 구조에서부터 시작한다. 이 데이터 구조를 컨테이너라 하겠다. 목표는 컨테이너의 속성 중, 특별하게 다뤄야 할 값을 특이 케이스 클래스로 대체하는 것이 목표이다.

1. 컨테이너에 특이 케이스인지를 검사하는 속성을 추가하고, false를 반환하게 한다.
2. 특이 케이스 객체를 만든다. 이 객체는 특이 케이스인지를 검사하는 속성만 포함하며, 이 속성은 true를 반환하게 한다.
3. 클라이언트에서 특이 케이스인지를 검사하는 코드를 함수로 추출한다. 모든 클라이언트가 값을 직접 비교하는 대신, 방금 추출한 함수를 사용하도록 고친다.
4. 코드에 새로운 특이 케이스 대상을 추가한다. 함수의 반환 값으로 받거나 변환 함수를 적용하면 된다.
5. 특이 케이스를 검사하는 함수 본문을 수정하여 특이 케이스 객체의 속성을 사용하도록 한다.
6. **여러 함수를 클래스로 묶기**나 **여러 함수를 변환 함수로 묶기**를 적용하여 특이 케이스를 처리하는 공통 동작을 새로운 요소로 옮긴다.
    1. 특이 케이스 클래스는 간단한 요청에는 항상 같은 값을 반환하는 게 보통이므로, 해당 특이 케이스의 리터럴 레코드를 만들어 활용할 수 있을 것이다.
7. 아직도 특이 케이스 검사 함수를 이용하는 곳이 남아 있다면 검사 **함수를 인라인**한다.

### 예제 - before

```java
public class CustomerService {
    public String customerName(Site site) {
        Customer customer = site.getCustomer();

        String customerName;
        if (customer.getName().equals("unknown")) {
            customerName = "occupant";
        } else {
            customerName = customer.getName();
        }

        return customerName;
    }

    public BillingPlan billingPlan(Site site) {
        Customer customer = site.getCustomer();
        return customer.getName().equals("unknown") ? new BasicBillingPlan() : customer.getBillingPlan();
    }

    public int weeksDelinquent(Site site) {
        Customer customer = site.getCustomer();
        return customer.getName().equals("unknown") ? 0 : customer.getPaymentHistory().getWeeksDelinquentInLastYear();
    }

}
```

- cutomor의 이름이 unknown인 경우와 아닌 경우를 나누어 서로 다른 로직을 수행한다.
- unknow인 경우를 특이케이스라고 보고, 특이 케이스들을 별도의 클래스로 분리하여 리팩토링 해보자.

### 예제 - after

```java
## CustomerService
public class CustomerService {
    public String customerName(Site site) {
        return site.getCustomer().getName();
    }

    public BillingPlan billingPlan(Site site) {
        return site.getCustomer().getBillingPlan();
    }

    public int weeksDelinquent(Site site) {
        return site.getCustomer().getPaymentHistory().getWeeksDelinquentInLastYear();
    }
}

## UnknowCustomer
public class UnknownCustomer extends Customer {

    public UnknownCustomer() {
        super("unknown", new BasicBillingPlan(), new NullPaymentHistory());
    }

    @Override
    public boolean isUnknown() {
        return true;
    }
}

## NullPaymentHistory
public class NullPaymentHistory extends PaymentHistory {
    public NullPaymentHistory() {
        super(0);
    }
}
```

- NullObject 패턴을 적용 (Null 이거나 특수 상태를 표현하기 위한 클래스를 생성)하여 UnknowCustomer, NullPaymentHistory를 생성하여, site에서 동일한 메서드로 간단하게 정리할 수 있다.