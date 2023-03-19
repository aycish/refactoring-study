# 냄새 12. 반복되는 switch문

태그: 조건부 로직을 다형성으로 바꾸기

## 반복되는 switch문

---

- 예전에는 switch문이 한번만 등장해도 코드 냄새로 생각하고 리팩토링 대상으로 보았었다.
- 하지만, 최근에는 다형성이 꽤 널리 사용되고 있으며, 여러 프로그래밍 언어에서 보다 세련된 형태의 switch문을 지원하고 있다.
    - Java의 경우, Switch expression을 제공한다.
- 따라서 오늘날은 **“반복해서 등장하는 동일한 switch 문”**을 냄새로 여기고 있다.
- 반복해서 동일한 switch문이 등장할 경우, 새로운 조건을 추가하거나 기존의 조건을 변경할 때 모든 switch문을 찾아서 코드를 고쳐야할 수도 있다.

### 예제 - before

```java
public class SwitchImprovements {

    public int vacationHours(String type) {
        int result;
        switch (type) {
            case "full-time": result = 120; break;
            case "part-time": result = 80; break;
            case "temporal": result = 32; break;
            default: result = 0;
        }
        return result;
    }
}
```

- 본 예제에서는 Switch 문을 현대에서는 어떤식으로 표현해야하는가를 살펴본다.

### 예제 - after

```java
public int vacationHours2(String type) {
    return switch(type) {
        case "full-time" -> 120;
        case "part-time" -> 80;
        case "temporal" -> 32;
        default -> 0;
    };
}
```

- 자바 17에서 제공하는 Switch Expression을 사용하여 좀더 간결하게 표현할 수 있다.