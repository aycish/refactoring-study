# 냄새 5. 전역 데이터

## 강의 내용 정리

---

- 전역 데이터
    - 전역 데이터는 아무곳에서나 변경될 수 있다는 문제가 있다.
    - 어떤 코드로 인해 값이 바뀐 것인지 파악하기 어렵다.
    - 클래스 변수도 비슷한 문제를 겪을 수 있다.

### 적용할 수 있는 리팩토링 기술

- “변수 캡슐화하기 (Encapsulate Variable)”을 적용해서 접근을 제어하거나 어디서 사용하는지 파악하기 쉽게 만들 수 있다.
- 파라켈수스의 격언, “약과 독의 차이를 결정하는 것은 사용량일 뿐이다.”
    - 전역 데이터가 실제로 필요한 경우가 있어, 해당 사항에서는 유용하지만 남용, 오용하게 되는 경우 독이 된다.

## 변수 캡슐화하기

---

- 메서드는 점진적으로 새로운 메서드로 변경할 수 있으나, 데이터는 한번에 모두 변경해야한다.
- 데이터 구조를 변경하는 작업을 그보다는 조금 더 수월한 메서드 구조 변경 작업으로 대체할 수 있다.
- 데이터가 사용되는 범위가 클수록 캡슐화를 하는 것이 더 중요해진다.
    - 함수를 사용해서 값을 변경하면 보다 쉽게 검증 로직을 추가하거나 변경에 따르는 후속 작업을 추가하는 것이 편리하다.
- 불변 데이터의 경우에는 이런 리팩토링을 적용할 필요가 없다.

### 책의 내용

- 필드 캡슐화 적용 (public 필드 → private 필드)한 뒤, 접근 메서드를 둔다.

### 절차

1. 변수로의 접근과 갱신을 전담하는 캡슐화 함수들을 만든다.
2. 정적 검사를 수행한다.
3. 변수를 직접 참조하던 부분을 모두 적절한 캡슐화 함수 호출로 바꾼다. 하나씩 바꿀 때마다 테스트 한다.
4. 변수의 접근 범위를 제한한다.
    1. 변수로의 직접 접근을 막을 수 없을 때도 있다. 그럴 때는 변수 이름을 바꿔서 테스트해보면 해당 변수를 참조하는 곳을 쉽게 찾아낼 수 있다.
5. 테스트한다.
6. 변수 값이 레코드라면 레코드 캡슐화하기를 적용할지 고려해본다.

### 예제 - before

```java
public class Thermostats {
    public static Integer targetTemperature = 70;
    public static Boolean heating = true;
    public static Boolean cooling = false;
    public static Boolean fahrenheit = true;
}
```

- Global하게 참조만 하는 데이터에 대해서 public static 변수를 사용하며, 참조만 하는 것을 보장하기 위해 final 키워드를 사용한다.
- 어떤 객체든 해당 객체에 대해서 모든 필드들에 접근이 가능하므로 수정이 필요하다.

### 예제 - after

```java
public class Thermostats {
    private static Integer targetTemperature = 70;
    private static Boolean heating = true;
    private static Boolean cooling = false;
    private static Boolean readInFahrenheit = true;

    public static Integer getTargetTemperature() {
        return targetTemperature;
    }

    public static void setTargetTemperature(Integer targetTemperature) {
        Thermostats.targetTemperature = targetTemperature;
    }

    public static Boolean getHeating() {
        return heating;
    }

    public static void setHeating(Boolean heating) {
        Thermostats.heating = heating;
    }

    public static Boolean getCooling() {
        return cooling;
    }

    public static void setCooling(Boolean cooling) {
        Thermostats.cooling = cooling;
    }

    public static Boolean getReadInFahrenheit() {
        return readInFahrenheit;
    }

    public static void setReadInFahrenheit(Boolean readInFahrenheit) {
        Thermostats.readInFahrenheit = readInFahrenheit;
    }
}

... Client

public static void main(String[] args) {
    System.out.println(Thermostats.getTargetTemperature());
    Thermostats.setTargetTemperature(68);
    Thermostats.setReadInFahrenheit(false);
}
```

- 전역 데이터를 캡슐화하는 과정 중에서 해당 변수를 참조하는 곳을 쉽게 찾을 수 있지만, 본 예제에서는 적합하지 않았다.
- 아직도 의문이긴 함