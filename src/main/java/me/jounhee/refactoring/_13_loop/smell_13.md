# 냄새 13. 반복문

태그: 반복문을 파이프라인으로 바꾸기

## 반복문

---

- 책의 초판 발행 당시, 반복문에 대한 대안이 존재하지 않아 어쩔 수 없이 남겨두었다.
- 하지만,  최근 Java와 같은 언어에서 함수형 프로그래밍 (일급 함수)을 지원하면서 반복문에 비해 더 나은 대안책이 생겼다.
- “반복문을 파이프라인으로 바꾸는 (Replace Loop with Pipeline)” 리팩토링을 적용하면 필터나 맵핑과 같은 파이프라인 기능을 사용해 보다 빠르게 어떤 작업을 하는지 파악할 수 있다.

## 반복문을 파이프라인으로 바꾸기

---

- 컬렉션 파이프라인
- 고전적인 반복문을 파이프라인 오퍼레이션을 사용해 표현하면 코드를 더 명확하게 만들 수 있다. (대표적인 연산은 map과 filter가 있다.)
    - 필터
        - 또 다른 함수를 사용해 입력 컬렉션을 필터링하여 부분집합을 생성
        - 예시 ) 전달받은 조건의 true에 해당하는 데이터만 다음 오퍼레이션으로 전달
    - 맵
        - 함수를 사용하여 입력 컬렉션의 각 원소를 변환한다.
- 많은 예시가 있으니 하기 링크 참고할 것
    - [link](https://martinfowler.com/articles/refactoring-pipelines.html)

### 절차

1. 반복문에서 사용하는 컬렉션을 가리키는 변수를 하나 만든다.
    1. 기존 변수를 단순히 복사한것일 수 있다.
2. 반복문의 첫 줄부터 시작해서, 각각의 단위 행위를 적절한 컬렉션 파이프라인 연산으로 대체한다.
    1. 이 때, 컬렉션 파이프라인 연산은 1에서 만든 반복문 컬렉션 변수에서 시작하여, 이전 연산의 결과를 기초로 연쇄적으로 수행된다.
3. 반복문의 모든 동작을 대체했다면, 반복문 자체를 지운다.
    1. 반목문이 결과를 누적 변수에 대입했다면, 파이프라인의 결과를 그 누적 변수에 대입한다.

### 예시 - before

```java
public class Author {

    private String company;

    private String twitterHandle;

    public Author(String company, String twitterHandle) {
        this.company = company;
        this.twitterHandle = twitterHandle;
    }

    static public List<String> TwitterHandles(List<Author> authors, String company) {
        var result = new ArrayList<String>();
        for (Author a : authors) {
            if (a.company.equals(company)) {
                var handle = a.twitterHandle;
                if (handle != null)
                    result.add(handle);
            }
        }
        return result;
    }
}
```

- 컬렉션을 순회하면서, 특정 요소가 tiwtterHandle이 있다면, 해당 handle들을 찾아 반환한다.
- 반복문 안의 코드를 면밀히 살펴봐야 맥락이 이해되기 때문에 좀더 직관적인 파이프라인으로 변경하자.

### 예시 - after

```java
static public List<String> TwitterHandles(List<Author> authors, String company) {
    return authors.stream()
            .filter(a -> a.company.equals(company))
            .map(a -> a.twitterHandle)
            .filter(handle -> handle != null)
            .collect(Collectors.toList());
}
```

- 만약 자바 람다식에 대한 이해가 있다면, 반복문보다 해당 파이프라인의 로직이 좀더 직관적임을 알 수 있다.