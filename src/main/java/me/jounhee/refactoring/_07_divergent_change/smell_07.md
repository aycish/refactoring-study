# 냄새 7. 뒤엉킨 변경

태그: 단계 쪼개기, 클래스 추출하기, 함수 옮기기, 함수 추출하기

## 강의 내용 정리

---

- 소프트웨어는 변경에 유연하게(soft) 대처할 수 있어야 한다.
- 어떤 한 모듈이 (함수 또는 클래스가) 여러가지 이유로 다양하게 변경되어야 하는 상황
    - 예) 새로운 결제 방식을 도입하거나, DB를 변경할 때 동일한 클래스에 여러 메소드를 수정해야 하는 경우.
- 서로 다른 문제는 서로 다른 모듈에서 해결해야 한다.
- 모듈의 책임이 분리되어 있을수록 해당 문맥을 더 잘 이해할 수 있으며 다른 문제는 신경쓰지 않아도 된다.

### 관련 리팩토링 기술

- “단계 쪼개기 (Split Phase)”를 사용해 서로 다른 문맥의 코드를 분리할 수 있다.
- “함수 옮기기 (Move Function)”를 사용해 적절한 모듈로 함수를 옮길 수 있다.
- 여러가지 일이 하나의 함수에 모여 있다면 “함수 추출하기 (Extract Function)”를 사용할 수 있다.
- 모듈이 클래스 단위라면 “클래스 추출하기 (Extract Class)”를 사용해 별도의 클래스로 분리할 수 있다.

## 단계 쪼개기

---

- 서로 다른 일을 하는 코드를 각기 다른 모듈로 분리한다.
    - 그래야 어떤 것을 변경할 때, 그것과 관련있는 것만 신경쓸 수 있다.
- 여러 일을 하는 함수의 처리과정을 각기 다른 단계로 구분할 수 있다.
- 서로 다른 데이터를 사용한다면 단계를 나누는데 있어 중요한 단서가 될 수 있다.
- 중간 데이터를 만들어 단계를 구분하고, 매개변수를 줄이는데 활용할 수 있다.

### 절차

1. 두 번째 단계에 해당하는 코드를 독립 함수로 추출한다.
2. 중간 데이터 구조를 만들어 앞에서 추출한 함수의 인수로 추가한다.
3. 추출한 두 번째 단계 함수의 매개변수를 하나씩 검토한다. 그 중 첫 번째 단계에서 사용되는 것은 중간 데이터 구조로 옮긴다. 하나씩 옮길 때마다 테스트한다.
4. 첫 번째 단계 코드를 함수로 추출하면서 중간 데이터 구조를 반환하도록 만든다.

### 예제 - before

```java
public double priceOrder(Product product, int quantity, ShippingMethod shippingMethod) {
    final double basePrice = product.basePrice() * quantity;
    final double discount = Math.max(quantity - product.discountThreshold(), 0)
            * product.basePrice() * product.discountRate();

    final double shippingPerCase = (basePrice > shippingMethod.discountThreshold()) ?
            shippingMethod.discountedFee() : shippingMethod.feePerCase();
    final double shippingCost = quantity * shippingPerCase;
    final double price = basePrice - discount + shippingCost;
    return price;
}
```

- basePrice, discount, shippingPerCase, shippingCost, price등의 지역변수들이 존재하는데, 모두 전달받은 매개변수들을 이용하여 계산하고 있다.
- shippingPerCase를 계산하는 부분부터는 product를 사용하지 않고 계산된 결과값을 사용하므로 분리하여 깔끔하게 정리할 여지가 있다.

### 예제 - after

```java
public class PriceOrder {

    public double priceOrder(Product product, int quantity, ShippingMethod shippingMethod) {
        final PriceData priceData = calculatePriceData(product, quantity);
        final double price = applyShipping(priceData, shippingMethod);
        return price;
    }

    private static PriceData calculatePriceData(Product product, int quantity) {
        final double basePrice = product.basePrice() * quantity;
        final double discount = Math.max(quantity - product.discountThreshold(), 0)
                * product.basePrice() * product.discountRate();
        return new PriceData(basePrice, discount, quantity);
    }

    private static double applyShipping(PriceData priceData, ShippingMethod shippingMethod) {
        final double shippingPerCase = (priceData.basePrice() > shippingMethod.discountThreshold()) ?
                shippingMethod.discountedFee() : shippingMethod.feePerCase();
        final double shippingCost = priceData.discount() * shippingPerCase;
        final double price = priceData.basePrice() - priceData.discount() + shippingCost;
        return price;
    }
}
```

- 중간 데이터 (PriceData)를 추가하고, shippingPerCase 전후로 단계를 나눈다.
- 각각의 단계를 메서드 추출하여 priceOrder를 깔끔하게 만든다.

## 함수 옮기기

---

- 모듈화가 잘 된 소프트웨어는 최소한의 지식만으로 프로그램을 변경할 수 있다.
    - 즉, 모듈성이 좋은 소프트웨어 설계의 척도이다.
- 관련있는 함수나 필드가 모여있어야 더 쉽게 찾고 이해할 수 있다.
    - 만약 어떤 함수가 자신이 속한 모듈 A의 요소들보다 다른 모듈 B의 요소들을 더 많이 참조한다면 모듈 B로 옮겨줘야한다.
    - 그렇게 해줌으로써 B의 요소들에 대해 캡슐화되며 B의 세부 사항들에 대한 의존도가 낮아진다.
- 하지만 관련있는 함수나 필드가 항상 고정적인 것은 아니기 때문에 때에 따라 옮겨야 할 필요가 있다.
    - 미리 예측해서 옮겨두는 것 또한 가능하면 좋다.
- 함수를 옮길 적당한 위치를 찾기가 어렵다면, 그대로 두어도 괜찮다. 언제든 나중에 옮길 수 있다.
    - 어떤 곳이든 괜찮은 경우, 그냥 냅두자. 괜찮다는 뜻
    - 다만, **대상 함수를 호출하는 함수들이 무엇인지**, **대상 함수가 호출하는 함수들은 무엇이 있는지**, 대상 **함수가 사용하는 데이터는 무엇인지** 꼭 살펴보고 옮길지 냅둘지 결정해야한다.

### 모듈성

- 프로그램의 어딘가를 수정하고자 할 때, 해당 기능과 깊이 관련된 작은 일부만 이해해도 가능하게 해주는 것
- 모듈성을 높이기 위해선 서로 연관된 요소들을 함께 묵고, 요소 사이의 연결 관계를 쉽게 찾고 이해할 수 있도록 해야한다.
- 프로그램에 대한 이해도에 따라 구체적인 방법이 달라질 수 있다.
- 객체 지향 프로그래밍의 핵심 모듈화 컨텍스트는 **클래스**다.

### 함수를 옮겨야 하는 경우

- 해당 함수가 다른 문맥 (클래스)에 있는 데이터 (필드)를 더 많이 참조하는 경우.
- 해당 함수를 다른 클라이언트 (클래스)에서도 필요로 하는 경우.

### 관련 리팩토링 기술

- 함수를 옮겨갈 새로운 문맥 (클래스)이 필요한 경우에는 “여러 함수를 클래스로 묶기 (Combine Functions info Class)” 또는 “클래스 추출하기 (Extract Class)”를 사용한다.

### 절차

1. 선택한 함수가 현재 컨텍스트에서 사용중인 모든 프로그램 요소를 살펴보며, 각각의 요소들에 대해서도 옮겨야 할 게 있는지 살펴본다.
    1. 호출되는 함수 중 함께 옮길 게 있다면 대체로 그 함수를 먼저 옮기는게 좋다. 얽혀 있는 함수가 여러 개라면, 다른 곳에 미치는 영향이 적은 함수부터 옮기자.
    2. 하위 함수들의 호출자가 고수준 함수 하나뿐이면 먼저 하위 함수들을 고수준 함수에 인라인한 다음, 고수준 함수를 옮기고, 옮긴 위치에서 개별 함수들로 다시 추출하자.
2. 선택한 함수가 다형 메서드인지 확인한다.
3. 선택한 함수를 타겟 컨택스트로 복사하고, 적절하게 수정한다.
4. 정적 분석을 수행한다.
5. 소스 컨텍스트에서 타겟 함수를 참조할 방법을 찾아 반영한다.
6. 소스 함수를 타겟 함수의 위임함수가 되도록 수정한다.
7. 테스트한다.
8. 소스 함수를 인라인할 지 고민한다.
    1. 소스 함수를 호출하는 곳에서 타겟 함수를 직접 호출하는 데 무리가 없다면 중간 단계는 제거하는 편이 좋다.

### 예제 - before

```java

public class Account {

    private int daysOverdrawn;
    private AccountType type;

    public Account(int daysOverdrawn, AccountType type) {
        this.daysOverdrawn = daysOverdrawn;
        this.type = type;
    }

    public double getBankCharge() {
        double result = 4.5;
        if (this.daysOverdrawn() > 0) {
            result += this.overdraftCharge();
        }
        return result;
    }

    private int daysOverdrawn() {
        return this.daysOverdrawn;
    }

    private double overdraftCharge() {
        if (this.type.isPremium()) {
            final int baseCharge = 10;
            if (this.daysOverdrawn <= 7) {
                return baseCharge;
            } else {
                return baseCharge + (this.daysOverdrawn - 7) * 0.85;
            }
        } else {
            return this.daysOverdrawn * 1.75;
        }
    }
}
```

- overdraftCharge → AccountType에 대해 의존성으로 로직이 시작되므로, 해당 문맥을 AccountType으로 옮겨야한다.

### 예제 - after

```java
public class Account {

    private int daysOverdrawn;

    private AccountType type;

    public Account(int daysOverdrawn, AccountType type) {
        this.daysOverdrawn = daysOverdrawn;
        this.type = type;
    }

    public double getBankCharge() {
        double result = 4.5;
        if (this.daysOverdrawn() > 0) {
            result += this.type.overdraftCharge(this.daysOverdrawn);
        }
        return result;
    }
    private int daysOverdrawn() {
        return this.daysOverdrawn;
    }
}

public class AccountType {
    private boolean premium;

    public AccountType(boolean premium) {
        this.premium = premium;
    }

    public boolean isPremium() {
        return this.premium;
    }

    double overdraftCharge(int daysOverdrawn) {
        if (isPremium()) {
            final int baseCharge = 10;
            if (daysOverdrawn <= 7) {
                return baseCharge;
            } else {
                return baseCharge + (daysOverdrawn - 7) * 0.85;
            }
        } else {
            return daysOverdrawn * 1.75;
        }
    }
}
```

- overdraftCharge의 위치를 AccountType으로 변경
- 이후에 만약 overdraftCharge 메서드에서 Account 정보가 더 필요한 경우, 매개 변수를 늘리다보면 Account에 좀더 의존적이게 된다.
- 그때에는 Account에 다시 옮기자.

## 클래스 추출하기

---

- 클래스가 다루는 책임(Responsibility)이 많아질수록 클래스가 점차 커진다.
- 하위 클래스를 만들어 책임을 분산 시킬 수도 있다

### 클래스를 추출하는 기준

- 데이터나 메소드 중 일부가 매우 밀접한 관련이 있어 묶을 수 있는 경우
- 일부 데이터가 대부분 같이 바뀌는 경우
    - ex) 특정 로직에 의해 여러 필드가 한번에 바뀌는 경우
- 데이터 또는 메소드 중 일부를 삭제해봤을 때, 논리적으로 문제가 없는 경우
    - 문제가 없다면, 분리할 수 있음을 의미한다.

### 절차

1. 클래스의 역할을 분리할 방법을 정한다.
2. 분리될 역할을 담당할 클래스를 새로 만든다.
    1. 원래 클래스에 남은 역할과 클래스 이름이 어울리지 않는다면 적절히 바꾼다.
3. 원래 클래스의 생성자에서 새로운 클래스의 인스턴스를 생성하여 필드에 저장해둔다.
4. 분리될 역할에 필요한 필드들을 새 클래스로 옮긴다.
5. 메서드들도 새 클래스로 옮긴다. 이때, 저수준 메서드, 즉 다른 메서드를 호출하기 보다는 호출을 당하는 일이 많은 메서드부터 옮긴다.
6. 양쪽 클래스의 인터페이스를 살펴보면서 불필요한 메서드를 제거하고, 이름도 적절히 변경한다.
7. 새 클래스를 외부로 노출할지 결정한다. 노출하려거든 새 클래스에 참조를 값으로 바꾸기를 적용할지 고민해본다.,

### 예제 - before

```java
public class Person {

    private String name;
    private String officeAreaCode;
    private String officeNumber;

    public String telephoneNumber() {
        return this.officeAreaCode + " " + this.officeNumber;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String officeAreaCode() {
        return officeAreaCode;
    }

    public void setOfficeAreaCode(String officeAreaCode) {
        this.officeAreaCode = officeAreaCode;
    }

    public String officeNumber() {
        return officeNumber;
    }

    public void setOfficeNumber(String officeNumber) {
        this.officeNumber = officeNumber;
    }
}
```

- officeAreaCode, officeNumber등 서로 같이 붙어다니는 데이터가 존재하므로 리팩토링을 수행해야한다.

### 예제 - after

```java
public class Person {

    private final TelephoneNumber telephoneNumber = new TelephoneNumber();
    private String name;

    public String telephoneNumber() {
        return this.telephoneNumber.getAreaCode() + " " + this.telephoneNumber.getNumber();
    }

    public TelephoneNumber getTelephoneNumber() {
        return telephoneNumber;
    }

    public String name() {
        return name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

public class TelephoneNumber {
    String areaCode;
    String number;

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
```

- officeAreaCode, offcieNumber를 하나의 클래스로 묶어, TelephoneNumber로 만들었다.
- 이후에 Person에서 접근하고 있었던 방법을 accessor를 두어 해결한다.
    - record 형식과 자바 빈즈 스펙 두 방법이 있는데, 상용되는 프레임워크에서는 자바 빈즈 스펙을 잘 사용하므로, 웬만하면 자바 빈즈를 사용하자.