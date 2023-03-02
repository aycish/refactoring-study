# 냄새 8. 산탄총 수술

태그: 단계 쪼개기, 여러 함수를 클래스로 묶기, 클래스 인라인, 필드 옮기기, 함수 옮기기, 함수 인라인

## 강의 정리 내용

---

### 어떤 한 변경 사항이 생겼을 때 여러 모듈을 (여러 함수 또는 여러 클래스를) 수정해야 하는 상황.

- “뒤엉킨 변경” 냄새와 유사하지만 반대의 상황이다.
- 응집도가 낮고, 결합도가 높을 때 적용하기 좋다.
- 예) 새로운 결제 방식을 도입하려면 여러 클래스의 코드를 수정해야 한다.
- 변경 사항이 여러곳에 흩어진다면 찾아서 고치기도 어렵고 중요한 변경 사항을 놓칠 수 있는 가능성도 생긴다.

### 관련 리팩토링 기술

- “함수 옮기기 (Move Function)” 또는 “필드 옮기기 (Move Field)”를 사용해서 필요한 변경 내역을 하나의 클래스로 모을 수 있다.
- 비슷한 데이터를 사용하는 여러 함수가 있다면 “여러 함수를 클래스로 묶기 (Combine Functions into Class)”를 사용할 수 있다.
- “단계 쪼개기 (Split Phase)”를 사용해 공통으로 사용되는 함수의 결과물들을 하나로 묶을 수 있다.
- “함수 인라인 (Inline Function)”과 “클래스 인라인 (Inline Class)”로 흩어진 로직을 한 곳으로 모을 수도 있다.

## 필드 옮기기

---

- 좋은 데이터 구조를 가지고 있다면, 해당 데이터에 기반한 어떤 행위를 코드로 (메소드나 함수) 옮기는 것도 간편하고 단순해진다.
- 처음에는 타당해 보였던 설계적인 의사 결정도 프로그램이 다루고 있는 도메인과 데이터 구조에 대해 더 많이 익혀나가면서, 틀린 의사 결정으로 바뀌는 경우도 있다.
- 필드 옮기기 리팩터링은 대체로 더 큰 변경의 일환으로 수행된다.
    - 예를들어, 필드 하나를 잘 옮기면, 그 필드를 사용하던 많은 호출 코드까지 변경해야한다.

### 필드를 옮기는 단서

- 어떤 데이터를 항상 어떤 레코드와 함께 전달하는 경우.
- 어떤 레코드를 변경할 때 다른 레코드에 있는 필드를 변경해야 하는 경우.
- 여러 레코드에 동일한 필드를 수정해야 하는 경우.
- (여기서 언급한 ‘레코드’는 클래스 또는 객체로 대체할 수도 있음)

### 필드 옮기기 절차

1. 소스 필드가 캡슐화되어 있지 않다면 캡슐화한다.
2. 변경사항에 대해서 테스트한다.
3. 타겟 객체에 필드와 접근자 메서드들을 생성한다.
4. 정적 검사를 수행한다.
5. 소스 객체에서 타겟 객체를 참조할 수 있는지 확인한다.
    1. 기존 필드, 메서드에서 타겟 객체를 넘겨주는 게 있을 수도 있다. 없다면 이런 기능의 메서드를 쉽게 만들 수 있는지 살펴본다. 간단하지 않다면 타겟 객체를 저장할 새 필드를 소스 객체에 생성하자.
6. 접근자들이 타겟 필드를 사용하도록 수정한다.
    1. 여러 소스에서 같은 타겟을 공유한다면, 먼제 세터를 수정하여 타겟 필드와 소스 필드 모두를 갱신하게 하고, 이어서 일관성을 깨뜨리는 갱신을 검출할 수 있도록 assertion을 추가하자. 이후, 접근자들이 타겟 필드를 사용하도록 수정한다.
7. 테스트한다.
8. 소스 필드를 제거한다.
9. 테스트한다.

### 예시 - before

```java
public class Customer {

    private String name;
    private double discountRate;
    private CustomerContract contract;

    public Customer(String name, double discountRate) {
        this.name = name;
        this.discountRate = discountRate;
        this.contract = new CustomerContract(dateToday());
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void becomePreferred() {
        this.discountRate += 0.03;
        // 다른 작업들
    }

    public double applyDiscount(double amount) {
        BigDecimal value = BigDecimal.valueOf(amount);
        return value.subtract(value.multiply(BigDecimal.valueOf(this.discountRate))).doubleValue();
    }

    private LocalDate dateToday() {
        return LocalDate.now();
    }
}

public class CustomerContract {

    private LocalDate startDate;

    public CustomerContract(LocalDate startDate) {
        this.startDate = startDate;
    }
}
```

- Customer 클래스의 discountRate를 CustomerContract 클래스로 옮기고 싶다고 가정한다.
- 먼저, discountRate를 캡슐화한다. (Getter, Setter 생성)
- 이후, CustomerContract 클래스에 필드 하나와 접근자들을 추가한다.
- Customer의 접근자들이 새로운 필드를 사용하도록 수정한다.
- 이후, 문제가 없다면 접근자들을 다시 수정하여 새로운 CustomerContract 객체를 사용하게 한다.

### 예시 - after

```java
public class Customer {

    private String name;
    private CustomerContract contract;

    public Customer(String name, double discountRate) {
        this.name = name;
        this.contract = new CustomerContract(dateToday(), discountRate);
    }

    public double getDiscountRate() {
        return this.contract.getDiscountRate();
    }

    public void setDiscountRate(double discountRate) {
        this.contract.setDiscountRate(discountRate);
    }
    
    public double applyDiscount(double amount) {
        BigDecimal value = BigDecimal.valueOf(amount);
        return value.subtract(value.multiply(BigDecimal.valueOf(getDiscountRate()))).doubleValue();
    }

    private LocalDate dateToday() {
        return LocalDate.now();
    }
}

public class CustomerContract {

    private LocalDate startDate;
    private double discountRate;
    public CustomerContract(LocalDate startDate, double discountRate) {
        this.discountRate = discountRate;
        this.startDate = startDate;
    }

    double getDiscountRate() {
        return this.discountRate;
    }

    void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }
}
```

- discountRate가 CustomerContract에 위치해야 책임 소재가 적절해보인다.
- 필드 옮기기를 통해 각 객체는 본연의 책임에 집중할 수 있게 되었다.

## 함수 인라인

---

- “함수 추출하기 (Extract Function)”의 반대에 해당하는 리팩토링
    - 함수로 추출하여 함수 이름으로 의도를 표현하는 방법.
- 간혹, 함수 본문이 함수 이름 만큼 또는 그보다 더 잘 의도를 표현하는 경우도 있다.
    - 간접 호출이 많은 경우도 해당된다.
- 함수 리팩토링이 잘못된 경우에 여러 함수를 인라인하여 커다란 함수를 만든 다음에 다시 함수
  추출하기를 시도할 수 있다.
- 단순히 메소드 호출을 감싸는 우회형 (indirection) 메소드라면 인라인으로 없앨 수 있다.

### 적용을 못하는 경우

- 상속 구조에서 오버라이딩 하고 있는 메소드는 인라인 할 수 없다. (해당 메소드는 일종의 규약
  이니까)

### 절차

1. 다형 메서드인지 확인한다.
2. 인라인할 함수를 호출하는 곳을 모두 찾는다.
3. 각 호출문을 함수 본문으로 교체한다.
4. 하나씩 교체할 때마다 테스트한다.
    1. 인라인 작업을 한 번에 처리할 필요는 없다. 인라인하기가 까다로운 부분이 있다면 일단 남겨두고 여유가 생길 때마다 틈틈이 처리한다.
5. 함수 정의를 삭제한다.

### 예제 - before

```java
public class Rating {

    public int rating(Driver driver) {
        return moreThanFiveLateDeliveries(driver) ? 2 : 1;  // 오히려 복잡해 보인다.
    }

    private boolean moreThanFiveLateDeliveries(Driver driver) {
        return driver.getNumberOfLateDeliveries() > 5;
    }
}
```

- 본래 메서드의 동작 로직보다 이름이 더 복잡해보여 리팩토링이 필요하다. → 쓸데 없는 간접 호출 + 긴 이름

### 예제 - after

```java
public int rating(Driver driver) {
    return driver.getNumberOfLateDeliveries() > 5 ? 2 : 1;
}
```

## 클래스 인라인

---

- “클래스 추출하기 (Extract Class)”의 반대에 해당하는 리팩토링
- 리팩토링을 하는 중에 클래스의 책임을 옮기다보면 클래스의 존재 이유가 빈약해지는 경우가 발생할 수 있다.
- 또한, 두 클래스의 기능을 지금과 다르게 배분하고 싶을 때도 인라인할 수 있다.
    - 코드를 재구성할 때 흔히 사용하는 방식
- 두개의 클래스를 여러 클래스로 나누는 리팩토링을 하는 경우에, 우선 “클래스 인라인”을 적용해서 두 클래스의 코드를 한 곳으로 모으고 그런 다음에 “클래스 추출하기”를 적용해서 새롭게 분리하는 리팩토링을 적용할 수 있다.

### 절차

- 소스 클래스의 각 public 메서드에 대응하는 메서드들을 타겟 클래스에 생성한다.
    - 해당 메서드들은 단순히 작업을 소스 클래스로 위임해야 한다.
- 소스 클래스의 메서드를 사용하는 코드를 모두 타겟 클래스의 위임 메서드를 사용하도록 바꾼다. 하나씩 바꿀때 마다 테스트한다.
- 소스 클래스의 메서드와 필드를 모두 타겟 클래스로 옮긴다. 하나씩 옮길 때 마다 테스트한다.
- 소스 클래스를 삭제한다.

### 예제 - before

```java
public class TrackingInformation {

    private String shippingCompany;
    private String trackingNumber;

    public TrackingInformation(String shippingCompany, String trackingNumber) {
        this.shippingCompany = shippingCompany;
        this.trackingNumber = trackingNumber;
    }

    /* 모두 옮기는 대상이 된다. */
    public String display() {
        return this.shippingCompany + ": " + this.trackingNumber;
    }

    public String getShippingCompany() {
        return shippingCompany;
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}

public class Shipment {

    private TrackingInformation trackingInformation;

    public Shipment(TrackingInformation trackingInformation) {
        this.trackingInformation = trackingInformation;
    }

    public TrackingInformation getTrackingInformation() {
        return trackingInformation;
    }

    public void setTrackingInformation(TrackingInformation trackingInformation) {
        this.trackingInformation = trackingInformation;
    }

    public String getTrackingInfo() {
        return this.trackingInformation.display();      
    }
}
```

- TrackingInformation 클래스는 Shipment 클래스의 일부처럼 사용된다. 그에 따라, TrackingInformation의 책임이 다소 미약해졌다. 이를 Shipment 클래스에 인라인하자.
- 먼저, Client에서 TrackingInformation의 메서드를 호출하는 부분을 모두 찾아 Shipment의 메서드를 호출하도록 변경한다.
    - 관련된 TrackingInformation의 메서드들을 모두 Shipment로 옮기며, Shipment의 메서드들에서 TrackingInformation에 접근하는 메서드들도 수정한다.
- 이후, TrackingInformation의 필드들을 모두 Shipment로 옮긴다.
    - TrackingInformation의 필드에 접근하던 메서드들을 모두 Shipment의 필드를 참조하도록 변경한다.
- TrackingInformation을 삭제한다.
- 기선님의 경우, 필드부터 먼저 옮긴다.

### 예제 - after

```java
public class Shipment {

	private String shippingCompany;
    private String trackingNumber;

    public Shipment(String shippingCompany, String trackingNumber) {
        this.shippingCompany = shippingCompany;
		this.trackingNumber = trackingNumber;
    }

    public String getTrackingInfo() {
        return this.shippingCompany + ": " + this.trackingNumber;
    }
	
	  /* Tracking Info에서 넘어온 메서드들 */
    public String getShippingCompany() {
        return this.shippingCompany;
    }

    public void setShippingCompany(String shippingCompany) {
        this.shippingCompany = shippingCompany;
    }

    public String getTrackingNumber() {
        return this.trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }
}
```