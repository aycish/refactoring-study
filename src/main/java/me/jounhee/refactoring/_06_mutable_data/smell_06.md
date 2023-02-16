# 냄새 6. 가변 데이터

태그: 변수 쪼개기, 변수 캡슐화하기, 세터 제거하기, 여러 함수를 변환 함수로 묶기, 여러 함수를 클래스로 묶기, 질의함수와 변경 함수 분리하기, 참조를 값으로 바꾸기, 코드 정리하기, 파생 변수를 질의 함수로 변경하기, 함수 추출하기

## 강의 내용 정리

---

- 데이터를 변경하다보면 예상치 못했던 결과나 해결하기 어려운 버그가 발생하기도 한다.
    - 함수형 프로그래밍에서 데이터는 절대 변하지 않고, 복사본을 만들어 전달한다.
    - 자바의 경우, 데이터 변경을 허용하고 있어 문제다.
- 따라서 변경되는 데이터 사용 시 발생할 수 있는 리스크를 관리할 수 있는 방법을 적용하는 것이 좋다.

### 관련 리팩토링

- “변수 캡슐화하기 (Encapsulate Variable)”를 적용해 데이터를 변경할 수 있는 메소드를 제한하고 관리할 수 있다.
- “변수 쪼개기 (Split Variable)”을 사용해 여러 데이터를 저장하는 변수를 나눌 수 있다.
- “코드 정리하기 (Slide Statements)”를 사용해 데이터를 변경하는 코드를 분리하고 피할 수 있다.
- “함수 추출하기 (Extract Function)”으로 데이터를 변경하는 코드로부터 사이드 이팩트가 없는 코드를 분리할 수 있다.
- “질의 함수와 변경 함수 분리하기 (Separate Query from Modifier)”를 적용해서 클라이언트가 원하는 경우에만 사이드 이팩트가 있는 함수를 호출하도록 API를 개선할 수 있
  다.
- 가능하다면 “세터 제거하기 (Remove Setting Method)”를 적용한다.
    - 변경 여지를 줄여준다.
- 계산해서 알아낼 수 있는 값에는 “파생 변수를 질의 함수로 바꾸기 (Replace Derived Variable with Query)”를 적용할 수 있다.
- 변수가 사용되는 범위를 제한하려면 “여러 함수를 클래스로 묶기(Combine Functions into Class)”또는 “여러 함수를 변환 함수로 묶기 (Combine Functions intoTransform)”을 적용할 수 있다.
- “참조를 값으로 바꾸기 (Change Reference to Value)”를 적용해서 데이터 일부를 변경하기 보다는 데이터 전체를 교체할 수 있다.

## 변수 쪼개기

---

### 언제 적용할까?

- 어떤 변수가 여러번 재할당 되어도 적절한 경우
    - 반복문에서 순회하는데 사용하는 변수 또는 인덱스
    - 값을 축적시키는데 사용하는 변수
        - ex) StringBuilder같은 경우

### 판단하는 방법

- 그 밖의 경우에 재할당 되는 변수가 있다면, 해당 변수는 여러 용도로 사용되는 것
- 변수를 분리해야 더 이해하기 좋은 코드를 만들 수 있다.
    - 변수 하나 당 하나의 책임을 지도록 만든다.
    - 상수를 활용하자.

### 절차

1. 변수를 선언한 곳과 값을 처음 대입하는 곳에서 변수 이름을 바꾼다.
    1. 이후의 대입이 항상 i = i + (무엇) 형태라면 수집 변수이므로 쪼개면 안된다. 수집 변수는 총합 계산, 문자열 연결, 스트림에 쓰기, 컬렉션에 추가하기 등의 용도로 흔히 쓰인다.
2. 가능하면 이때 불변으로 선언한다.
3. 이 변수에 두 번째로 값을 대입하는 곳 앞까지의 모든 참조를 새로운 변수 이름으로 바꾼다.
4. 두 번째 대입 시 변수를 원래 이름으로 다시 선언한다.
5. 테스트한다.
6. 반복한다. 매 시도마다 변수를 새로운 이름으로 선언하고 다음번 대입 때 까지 모든 참조를 새 변수명으로 변경한다.

### 예제 - before

```java
public class Rectangle {

    private double perimeter;
    private double area;

    public void updateGeometry(double height, double width) {
        double temp = 2 * (height + width);
        System.out.println("Perimeter: " + temp);
        perimeter = temp;

        temp = height * width;
        System.out.println("Area: " + temp);
        area = temp;
    }

    public double getPerimeter() {
        return perimeter;
    }

    public double getArea() {
        return area;
    }
}
```

- updateGeometry 메서드를 보면, temp라는 변수가 perimeter, area를 구하는 데 중복하여 사용하고 있다.
- 각 용도에 맞춰 적절한 변수를 선언해야한다.

### 예제 - after

```java
public void updateGeometry(double height, double width) {
    double perimeter = 2 * (height + width);
    System.out.println("Perimeter: " + perimeter);
    this.perimeter = perimeter;

    double area = height * width;
    System.out.println("Area: " + area);
    this.area = area;
}
```

- temp라는 재사용되는 변수가 사라져 가독성이 올라갔다.
- 다만, 해당 예제는 불필요한 변수할당을 하고 있어, 변수 인라인하기가 적용되어야 한다.

## 질의 함수와 변경 함수 분리하기

---

- 질의 함수 (Query) : 특정 값을 조회하여 값을 가져오는 함수
- 변경 함수 (Modifier) : 특정 값이나 상태를 변경하는 함수
- “눈에 띌만한” 사이드 이펙트 없이 값을 조회할 수 있는 메서드는 테스트하기도 쉽고, 메서드를 이동하기도 편하다.
    - 사이드 이펙트가 없는 함수는 여러번 호출해도 상관 없고, 호출 시점을 변경해도 영향을 주지 않기 때문
    -

### 명렁-조회 분리 (Command-query separation) 규칙

- 어떤 값을 리턴하는 함수는 사이드 이펙트가 없어야한다.

### 눈에 띌만한(observable) 사이드 이팩트

- 예를 들어, Cache는 중요한 객체 상태 변화는 아니다.
- 따라서 어떤 메서드 호출로 인해, 케시 데이터를 변경하더라도 분리할 필요는 없다.

### 절차

1. 대상 함수를 복제하고, 질의 목적에 충실한 이름을 짓는다.
    1. 함수 내부 로직을 살펴 반환되는 것을 분석하자. 어떤 변수의 값을 반환한다면 그 변수 이름이 단초가 된다.
2. 새 질의 함수에서 부수효과를 모두 제거한다.
3. 정적 검사를 수행한다.
4. 변경 전 함수를 호출하는 곳을 모두 찾아낸다. 호출하는 곳에서 반환 값을 사용한다면, 질의 함수를 호출하도록 변경하고, 원래 함수를 호출하는 코드를 바로 아래 줄에 새로 추가한다. 하나 수정할 때마다 테스트한다.
5. 원래 함수에서 질의 관련 코드를 제거한다.
    1. 변경 함수와 관련된 로직만을 살려놓는다.
6. 테스트한다.

### 예제 - before

```java
public double getTotalOutstandingAndSendBill() {
    double result = customer.getInvoices().stream()
            .map(Invoice::getAmount)
            .reduce((double) 0, Double::sum);
    sendBill(); // 변경함수가 있어, 사이드 이펙트가 있음을 예상할 수 있다.
    return result;
}

// Client
...
double outstanding = getTotalOutstandingAndSendBill()
```

- sendBill() 메서드를 내부에서 호출하기 때문에, 내부 로직 변경 시, 영향을 받을 수 있다.

### 예제 - after

```java
public double getTotalOutstanding() {
    return customer.getInvoices().stream()
            .map(Invoice::getAmount)
            .reduce((double) 0, Double::sum);
}

public void sendBill() {
    emailGateway.send(formatBill(customer));
}

// Client
double outstanding = getTotalOutstanding();
sendBill();
```

## 세터 제거하기

---

- 세터를 제공한다는 것은 해당 필드가 변경될 수 있다는 것을 의미한다.
- 객체 생성시, 처음 설정된 값이 변경될 필요가 없다면 해당 값을 설정할 수 있는 생성자를 만들고, 세터를 제거해서 변경될 수 있는 가능성을 제거한다.

### 변경 대상

- 사람들이 무조건 접근자 메서드를 통해서만 필드를 다루려고 할 때, 수정 필요
    - 예를 들어, 생성자 내부에서만 호출하는 세터가 존재하는 경우
    - 객체 생성 이후, 값이 바뀌면 안된다는 뜻을 의미하는 경우가 다분하므로 제거한다.
- 클라이언트에서 생성 스크립트를 사용해 객체를 생성하는 경우
    - 생성 스크립트 : 생성자를 호출한 후, 일련의 세터를 호출하여 객체를 완성하는 형태의 코드를 의미
    - 설계자는 스크립트가 완료된 뒤로는 그 객체의 필드는 변경되지 않는것을 기대하므로 결국 생성자 내부에서만 호출하는 세터가 존재하는 경우와 동일하다.

### 절차

1. 설정해야 할 값을 생성자에서 받지 않는다면, 그 값을 받을 매개변수를 생성자에 추가한다. (함수 선언 바꾸기) 이후, 생성자 안에서 적절한 세터를 호출한다.
    1. 세터 여러개를 제거하려면, 해당 값 모두를 한꺼번에 생성자에 추가한다.
2. 생성자 밖에서 세터를 호출하는 곳을 찾아 제거하고, 대신 새로운 생성자를 사용하도록 한다. 하나 수정할 때 마다 테스트한다.
    1. 새로운 객체를 생성하는 방식으로는 세터 호출을 대체할 수 없다면, 이 방법 적용을 파기한다.
3. 생성자 내부로 옮겨놓았던 세터 메서드를 인라인한다. 가능하다면 해당 필드를 불변으로 만든다.
4. 테스트

### 예제 - before

```java
public class Person {

  private String name;

  private int id;

  public String getName() {
      return name;
  }

  public void setName(String name) {
      this.name = name;
  }

  public int getId() {
      return id;
  }

  public void setId(int id) {
      this.id = id;
  }
}
```

- 해당 필드가 변경될 수 있음을 나타내고 있어, 생성자를 통한 값 설정 적용이 어렵지 않다면 적용해야한다.

### 예제 - after

```java
public class Person {

  private String name;
  private int id;

	public Person (String name, int id) {
		this.name = name;
		this.id = id;
	}

  public String getName() {
      return name;
  }

  public int getId() {
      return id;
  }
}
```

- 생성자를 통해 클래스 변수의 값을 설정하도록 변경하여 세터를 제거하였다.
- 해당 클래스 멤버들은 생성하는 시점에만 값 설정이 가능하므로, 데이터 변경의 여지가 없다.

## 파생 변수를 질의 함수로 바꾸기

---

- 변경할 수 있는 데이터를 최대한 줄이도록 노력해야 한다.
- 계산해서 알아낼 수 있는 변수는 제거할 수 있다.
    - 계산 자체가 데이터의 의미를 잘 표현하는 경우도 있다.
    - 해당 변수가 어디선가 잘못된 값으로 수정될 수 있는 가능성을 제거할 수 있다.
- 계산에 필요한 데이터가 변하지 않는 값이라면, 계산의 결과에 해당하는 데이터 역시 불변 데이터기 때문에 해당 변수는 그대로 유지할 수 있다.

### 예제 - before

```java
public class Discount {

  private double discountedTotal;
  private double discount;

  private double baseTotal;

  public Discount(double baseTotal) {
      this.baseTotal = baseTotal;
  }

  public double getDiscountedTotal() {
      return this.discountedTotal;
  }

  public void setDiscount(double number) {
      this.discount = number;
      this.discountedTotal = this.baseTotal - this.discount;
  }
}
```

- discoutedTotal이라는 변수는 다른 멤버 변수들을 활용하여 나오는 파생된 변수이다.
- set메서드가 호출되기 전까지 값이 설정되지 않기 때문에, 버그가 발생할 수 있다.

### 예제 - after

```java
public class Discount {
    private double discount;
    private double baseTotal;

    public Discount(double baseTotal) {
        this.baseTotal = baseTotal;
    }

    public double getDiscountedTotal() {
        return this.baseTotal - this.discount;
    }

    public void setDiscount(double number) {
        this.discount = number;
    }
}
```

## 여러 함수를 변환 함수로 묶기

---

- 관련있는 여러 파생 변수를 만들어내는 함수가 여러곳에서 만들어지고 사용된다면 그러한
  파생 변수를 “변환 함수 (transform function)”를 통해 한 곳으로 모아둘 수 있다.
- 소스 데이터가 변경될 수 있는 경우에는“여러 함수를 클래스로 묶기 (Combine Functions into Class)”를 사용하는 것이 적절하다.
- 소스 데이터가 변경되지 않는 경우에는 두 가지 방법을 모두 사용할 수 있지만, 변환 함수를 사용해서 불변 데이터의 필드로 생성해 두고 재사용할 수도 있다.

### 예제 - before

```java
public class Client1 {

  double baseCharge;

  public Client1(Reading reading) {
      this.baseCharge = baseRate(reading.month(), reading.year()) * reading.quantity();
  }

  private double baseRate(Month month, Year year) {
      return 10;
  }

  public double getBaseCharge() {
      return baseCharge;
  }
}

public class Client2 {

  private double base;
  private double taxableCharge;

  public Client2(Reading reading) {
      this.base = baseRate(reading.month(), reading.year()) * reading.quantity();
      this.taxableCharge = Math.max(0, this.base - taxThreshold(reading.year()));
  }

  private double taxThreshold(Year year) {
      return 5;
  }

  private double baseRate(Month month, Year year) {
      return 10;
  }

  public double getBase() {
      return base;
  }

  public double getTaxableCharge() {
      return taxableCharge;
  }
}

public class Client3 {

  private double basicChargeAmount;

  public Client3(Reading reading) {
      this.basicChargeAmount = calculateBaseCharge(reading);
  }

  private double calculateBaseCharge(Reading reading) {
      return baseRate(reading.month(), reading.year()) * reading.quantity();
  }

  private double baseRate(Month month, Year year) {
      return 10;
  }

  public double getBasicChargeAmount() {
      return basicChargeAmount;
  }
}
```

- baseRate가 각 Client 클래스에서 반복적으로 사용하고 있다.

### 예제 - after

```java

```

## 참조를 값으로 바꾸기

---

- 레퍼런스 (Reference) 객체 vs 값 (Value) 객체
    - [https://martinfowler.com/bliki/ValueObject.html](https://martinfowler.com/bliki/ValueObject.html)
    - “Objects that are equal due to the value of their properties, in this case their xand y coordinates, are called value objects.”
    - 값 객체는 객체가 가진 필드의 값으로 동일성을 확인한다.
    - 값 객체는 변하지 않는다.
    - 어떤 객체의 변경 내역을 다른 곳으로 전파시키고 싶다면 레퍼런스, 아니라면 값 객체를 사용한다.

### 예제 - before

```java
public class TelephoneNumber {

  private String areaCode;

  private String number;

  public String areaCode() {
      return areaCode;
  }

  public void areaCode(String areaCode) {
      this.areaCode = areaCode;
  }

  public String number() {
      return number;
  }

  public void number(String number) {
      this.number = number;
  }
}
```

- telephoneNumber를 value 객체로 바꾸고 싶은 경우의 예제

### 예제 - after

```java
public class TelephoneNumber {

    private final String areaCode;
    private final String number;

    public TelephoneNumber(String areaCode, String number) {
        this.areaCode = areaCode;
        this.number = number;
    }

    public String areaCode() {
        return areaCode;
    }
    public String number() {
        return number;
    }
}

public class Person {

  private TelephoneNumber officeTelephoneNumber;
  public String officeAreaCode() {
      return this.officeTelephoneNumber.areaCode();
  }

  public void officeAreaCode(String areaCode) {
      this.officeTelephoneNumber = new TelephoneNumber(areaCode, officeNumber());
  }

  public String officeNumber() {
      return this.officeTelephoneNumber.number();
  }

  public void officeNumber(String number) {
      this.officeTelephoneNumber = new TelephoneNumber(officeAreaCode(), number);
  }
}
```

- 참조 객체를 활용하던 객체에서는 새로운 객체를 생성하는 방식으로 setter를 대신해야한다.