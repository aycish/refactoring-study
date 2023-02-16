# 냄새 4. 긴 매개변수 목록

## 강의 내용 정리

---

### 긴 매개변수 목록

- 어떤 함수에 매개변수가 많을 수록, 함수의 역할을 이해하기 어려워진다.
    - 과연 그 함수는 한가지 일을 하고 있는게 맞는가?
    - 불필요한 매개변수는 없는가?
    - 하나의 레코드로 뭉칠 수 있는 매개변수 목록은 없는가?

### 사용할 수 있는 기법

- 어떤 매개변수를 다른 매개변수를 통해 알아낼 수 있다면, “매개변수를 질의 함수로 바꾸기 (Replace Parameter with Query)”를 사용할 수 있다.
- 기존 자료구조에서 세부적인 데이터를 가져와서 여러 매개변수로 넘기는 대신, “객체 통째로 넘기기 (Preserve Whole Object)”를 사용할 수 있다.
- 일부 매개변수들이 대부분 같이 넘겨진다면, “매개변수 객체 만들기 (Introduce Parameter Object)”를 적용할 수 있다.
- 매개변수가 플래그로 사용된다면, “플래그 인수 제거하기 (Remove Falg Argument)”를 사용할 수 있다.
- 여러 함수가 일부 매개변수를 공통적으로 사용한다면 “여러 함수를 클래스로 묶기 (Combine Functions into Class)”를 통해 매개변수를 해당 클래스의 필드로 만들고 매서드에 전달해야 할 매개변수 목록을 줄일 수 있다.

## 매개변수를 질의 함수로 바꾸기

---

- 함수의 매개변수 목록은 함수의 다양성을 대변하며, 짧을수록 이해하기 좋다.
- 어떤 한 매개변수를 다른 매개변수를 통해 알아낼 수 있다면 “중복 매개변수”라 생각할 수
  있다.
- 매개변수에 값을 전달하는 것은 “함수를 호출하는 쪽”의 책임이다. 가능하면 함수를 호출하
  는 쪽의 책임을 줄이고 함수 내부에서 책임지도록 노력한다.
- “임시 변수를 질의 함수로 바꾸기”와 “함수 선언 변경하기”를 통해 이 리팩토링을 적용한
  다.

### 느낀점 정리

- 항상 옳은 것은 아니며, 매개 변수에 의해 생기는 의존성을 제거하는게 맞는지 아닌지를 항상 판단하며 적용해야한다.

### 예제 - before

```java
public class Order {

    private int quantity;
    private double itemPrice;

    public Order(int quantity, double itemPrice) {
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public double finalPrice() {
        double basePrice = this.quantity * this.itemPrice;
        int discountLevel = this.quantity > 100 ? 2 : 1;
        return this.discountedPrice(basePrice, discountLevel);
    }

    private double discountedPrice(double basePrice, int discountLevel) {
        return discountLevel == 2 ? basePrice * 0.9 : basePrice * 0.95;
    }
}
```

- Quantity를 통해서 discountLevel을 알아 낼 수 있음에도, discountLevel로 전달하고 있다.

### 예제 - after

```java
public class Order {

    private int quantity;
    private double itemPrice;

    public Order(int quantity, double itemPrice) {
        this.quantity = quantity;
        this.itemPrice = itemPrice;
    }

    public double finalPrice() {
        double basePrice = this.quantity * this.itemPrice;
        return this.discountedPrice(basePrice);
    }

    private int getDiscountLevel() {
        return this.quantity > 100 ? 2 : 1;
    }

    private double discountedPrice(double basePrice) {
        return getDiscountLevel() == 2 ? basePrice * 0.90 : basePrice * 0.95;
    }
}
```

- discountPrice 내부에서 discountLevel을 얻어오도록 책임을 전가하면서, 함수를 호출하는 쪽에서의 책임이 줄어들었다.

## 플래그 인수 제거하기

---

- 플래그는 보통 함수에 매개변수로 전달해서, 함수 내부의 로직을 분기하는데 사용한다.
- 플래그를 사용한 함수는 차이를 파악하기 어렵다.
    - bookConcert(customer, false), bookConcert(customer, true)
    - bookConcert(customer), premiumBookConcert(customer)
- 조건문 분해하기 (Decompose Condition)를 활용할 수 있다

### 느낀 점

- 플래그가 존재한다면, 해당 메서드가 너무 많은 일을 하고 있는게 아닌지 확인해보기

### 예제 - before

```java
public class Shipment {

    public LocalDate deliveryDate(Order order, boolean isRush) {
        if (isRush) {
            int deliveryTime = switch (order.getDeliveryState()) {
                case "WA", "CA", "OR" -> 1;
                case "TX", "NY", "FL" -> 2;
                default -> 3;
            };
            return order.getPlacedOn().plusDays(deliveryTime);
        } else {
            int deliveryTime = switch (order.getDeliveryState()) {
                case "WA", "CA" -> 2;
                case "OR", "TX", "NY" -> 3;
                default -> 4;
            };
            return order.getPlacedOn().plusDays(deliveryTime);
        }
    }
}
```

### 예제 - after

```java
public class Shipment {
	private static LocalDate regularDeliveryDate(Order order) {
		  int deliveryTime = switch (order.getDeliveryState()) {
		      case "WA", "CA" -> 2;
		      case "OR", "TX", "NY" -> 3;
		      default -> 4;
		  };
		  return order.getPlacedOn().plusDays(deliveryTime);
	}
	
	private static LocalDate rushDeliveryDate(Order order) {
		  int deliveryTime = switch (order.getDeliveryState()) {
		      case "WA", "CA", "OR" -> 1;
		      case "TX", "NY", "FL" -> 2;
		      default -> 3;
		  };
		  return order.getPlacedOn().plusDays(deliveryTime);
	}
}
```

- caller쪽에서 적절한 상황에 따라 필요한 메서드를 호출하도록 수정

## 여러 함수를 클래스로 묶기

---

### 책의 내용

- 공통 데이터를 중심으로 긴밀하게 엮여 작동하는 함수 무리를 발견한 경우, 클래스 하나로 묶고 싶다.
- 클래스로 묶으면 이 함수들이 공유하는 공통 환경을 명확하게 표현 가능하다.
- 각 함수에 전달되는 인수를 줄여서 객체 안에서의 함수 호출을 간결하게 만들 수 있다.
- 또한, 이런 객체를 시스템의 다른 부분에 전달하기 위한 참조를 제공할 수 있다.
- 클라이언트가 객체의 핵심 데이터를 변경할 수 있고, 파생 객체들을 일관되게 관리할 수 있다.

**절차**

1. 함수들이 공유하는 공통 데이터 레코드를 캡슐화한다.
    1. 공통 데이터가 레코드 구조로 묶여 있지 않다면, 먼저 매개변수 객체 만들기로 데이터를 하나로 묶는 레코드를 만든다.
2. 공통 레코드를 사용하는 함수 각각을 새 클래스로 옮긴다. (함수 옮기기)
    1. 공통 레코드의 멤버는 함수 호출문의 인수 목록에서 제거한다.
3. 데이터를 조작하는 로직들은 함수로 추출하여 새 클래스로 옮긴다.

### 강의 내용

- 비슷한 매개변수 목록을 여러 함수에서 사용하고 있다면 해당 메소드를 모아서 클래스를
  만들 수 있다.
- 클래스 내부로 메소드를 옮기고, 데이터를 필드로 만들면 메소드에 전달해야 하는 매개변
  수 목록도 줄일 수 있다.

### 예제 - before

```java
...

try (FileWriter fileWriter = new FileWriter("participants.md");
             PrintWriter writer = new PrintWriter(fileWriter)) {
    participants.sort(Comparator.comparing(Participant::username));

    writer.print(header(participants.size()));

    participants.forEach(p -> {
        String markdownForHomework = getMarkdownForParticipant(p.username(), p.homework());
        writer.print(markdownForHomework);
    });
}
```

- participants를 알고 있으면, username, homework등은 알아서 추출해낼 수 있음에도 매개변수로 나와 있다.
- participants의 멤버에 관련된 메서드로는 size, username, homework 등이 있는데, 이를 사용하는 메서드로는 writer의 print, header 등이 있다. 이를 한 클래스로 묶는다.

### 예제 - after

```java
public class StudyPrinter {

    private List<Participant> participants;
    private int totalNumberOfEvents;
    
    public StudyPrinter(List<Participant> participants, int totalNumberOfEvents) {
        this.participants = participants;
        this.totalNumberOfEvents = totalNumberOfEvents;
    }

    public void write() throws IOException {
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

    /**
     * | 참여자 (420) | 1주차 | 2주차 | 3주차 | 참석율 |
     * | --- | --- | --- | --- | --- |
     */
    private String header() {
        StringBuilder header = new StringBuilder(String.format("| 참여자 (%d) |", this.participants.size()));

        for (int index = 1; index <= this.participants.size(); index++) {
            header.append(String.format(" %d주차 |", index));
        }
        header.append(" 참석율 |\n");

        header.append("| --- ".repeat(Math.max(0, this.totalNumberOfEvents + 2)));
        header.append("|\n");

        return header.toString();
    }
    
    private String getMarkdownForParticipant(Participant p) {
        return String.format("| %s %s | %.2f%% |\n", p.username(), checkMark(p.homework()), getRate(p.homework()));
    }
    
    /**
     * |:white_check_mark:|:white_check_mark:|:white_check_mark:|:x:|
     */
    private String checkMark(Map<Integer, Boolean> homework) {
        StringBuilder line = new StringBuilder();
        for (int i = 1 ; i <= this.totalNumberOfEvents ; i++) {
            if(homework.containsKey(i) && homework.get(i)) {
                line.append("|:white_check_mark:");
            } else {
                line.append("|:x:");
            }
        }
        return line.toString();
    }

    private double getRate(Map<Integer, Boolean> homework) {
        long count = homework.values().stream()
                .filter(v -> v == true)
                .count();
        return (double) (count * 100 / this.totalNumberOfEvents);
    }
}
```
- StudyPrinter라는 클래스를 생성, 관련된 메서드들을 한 곳에 모았다.
- 해당 방법을 적용함으로써, 객체 안에서의 함수 호출을 간결하게 만들 수 있다.