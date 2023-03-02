# 냄새 10. 데이터 뭉치

태그: 매개변수 객체 만들기, 클래스 추출하기

## 강의 내용 정리

---

- 항상 뭉쳐 다니는 데이터는 한 곳으로 모아두는 것이 좋다.
    - 여러 클래스에 존재하는 비슷한 필드 목록
    - 여러 함수에 전달하는 매개변수 목록

### 판별하기

- 데이터 뭉치인지 판별하고싶다면, 값 하나를 삭제해보자.
- 나머지 데이터만으로는 의미가 없다면 객체로 만들어주는 것을 고려해보자.
- 레코드가 아닌 클래스로 만드는 것임을 주의하자.

### 관련 리팩토링 기술

- “클래스 추출하기 (Extract Class)”를 사용해 여러 필드를 하나의 객체나 클래스로 모을 수있다.
- “매개변수 객체 만들기 (Introduce Parameter Object)” 또는 “객체 통째로 넘기기(Preserve Whole Object)”를 사용해 메소드 매개변수를 개선할 수 있다.