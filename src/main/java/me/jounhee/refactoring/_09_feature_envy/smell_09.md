# 냄새 9. 기능 편애

태그: 함수 옮기기, 함수 추출하기

## 강의 내용 정리

---

- 어떤 모듈에 있는 함수가 다른 모듈에 있는 데이터나 함수를 더 많이 참조하는 경우에 발생한다.
    - 예) 다른 객체의 getter를 여러개 사용하는 메소드

### “기본은 데이터를 얻어오는 곳과 행동하는 곳이 같아야하지만, 예외적인 경우도 있다.”

- 어떤 함수가 다른 모듈의 데이터나 함수를 자주 참조한다는 것은, 다르게 말하면 해당 데이터나 책임을 지고싶어한다는 의중이 드러난다고 볼 수 있으므로 같은 곳으로 옮겨준다.
- 예외적인 경우, 같은 데이터를 다루는 코드를 한 곳에서 변경할 수 있도록 옮긴다.

### 관련 리팩토링 기술

- “함수 옮기기 (Move Function)”를 사용해서 함수를 적절한 위치로 옮긴다.
- 함수 일부분만 다른 곳의 데이터와 함수를 많이 참조한다면 “함수 추출하기 (Extract Function)”로 함수를 나눈 다음에 함수를 옮길 수 있다.
- 만약에 여러 모듈을 참조하고 있다면? 그 중에서 가장 많은 데이터를 참조하는 곳으로 옮기거나, 함수를 여러개로 쪼개서 각 모듈로 분산 시킬 수도 있다.
- 데이터와 해당 데이터를 참조하는 행동을 같은 곳에 두도록 하자.
- 예외적으로, 데이터와 행동을 분리한 디자인 패턴 (전략 패턴 또는 방문자 패턴)을 적용할 수도 있다.

### 예제 - before

```java
public class Bill {

    private ElectricityUsage electricityUsage;

    private GasUsage gasUsage;

    public double calculateBill() {
        var electicityBill = electricityUsage.getAmount() * electricityUsage.getPricePerUnit();
        var gasBill = gasUsage.getAmount() * gasUsage.getPricePerUnit();
        return electicityBill + gasBill;
    }

}
```

- 전기 요금과 가스 요금은 각각의 electricityUsage, gasUsage에서 계산해서 전달해주는 메서드를 통해 얻을 수 도 있는데, 그렇지 않고 굳이 Bill에서 구현되어 있다.

### 예제 - after

```java
public class Bill {

    private ElectricityUsage electricityUsage;

    private GasUsage gasUsage;

    public double calculateBill() {
        return electricityUsage.getElecticityUsagePrice() + gasUsage.getGasUsagePrice(this);
    }
}

//전기 요금
public class ElectricityUsage {

    private double amount;

    private double pricePerUnit;

    public ElectricityUsage(double amount, double pricePerUnit) {
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
    }

    public double getAmount() {
        return amount;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    double getElecticityUsagePrice() {
        return amount * pricePerUnit;
    }
}

// 가스 요금
public class GasUsage {

    private double amount;

    private double pricePerUnit;

    public GasUsage(double amount, double pricePerUnit) {
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
    }

    public double getAmount() {
        return amount;
    }

    public double getPricePerUnit() {
        return pricePerUnit;
    }

    double getGasUsagePrice(Bill bill) {
        return amount * pricePerUnit;
    }
}
```

- 직접 연산하지 않고, 가스 사용량 클래스와 전기 사용량 클래스에서 사용요금을 계산하는것을 요청하도록 변경하였다.