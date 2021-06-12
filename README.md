<br>
<br>

## DeMoim 서비스 소개

- 해당 프로젝트는 부트캠프 항해 99에서 작업한 실전 프로젝트로 실제 런칭을 통해 실 사용자를 상대로 운영경험을 쌓는 프로젝트입니다.
- 협업경험이 필요한 취준생들을 위한 팀원 모집 플랫폼
- 그외에도 자신이 작업한 프로젝트에 대한 정보를 공유
- 지원과 지원취소 기능을 통해 간편하게 지원가능

![image](https://user-images.githubusercontent.com/78028746/121785601-a7593700-cbf5-11eb-8406-009f18524c06.png)

🎉 [사이트 주소] [https://demoim.co.kr/]
🎬 [시연 영상] [https://www.youtube.com/watch?v=8u1KISOf3OU]
🧑 [팀원 소개] [https://www.notion.so/Demoim-87856b49c18545358ee657b434bff365]

<br>

## DeMoim Frontend 
https://github.com/holasim91/demoim_fe

<br>

## 목차
### 1. 개요
### 2. 프로젝트 주요 기능
### 3. 페이지별 기능 소개
  * 문자인증
  * Signup
  * Home Page
  * Detail Page
  * Product Registration Page
  * Mypage + MyStore
  * Chatting
### 4. 트러블 슈팅
### 5. 고객 반응 및 개선 사항
### 6. 기타



## 개요

- 프로젝트 명 :: DeMiom

- 프로젝트 소개 :: 프로젝트 팀원을 모집할 수 있는 웹 플랫폼.

- 개발 인원 :: 백엔드 3명, 프론트엔드 3명, 디자이너 1명

- 개발 언어 ::  Java 8 , React

- 개발 기간 :: 2021.04.23 ~2021.05.19

- 운영 기간 :: 2021.05.19 ~ (사용자의 피드백을 받아 서비스 개선)

- 담당 업무  
  * 이은지
    + 구현 기능 별 테이블 설계
    +  Coolsms를 이용한 문자인증 
    +  SmallTalk 및 프로젝트 자랑하기(게시판) 기능 구현
    +  관련 테스트 코드 작성 
  * 김준엽
    + 구현 기능별 테이블 설계
    + 팀 메이킹, 지원 ,마이페이지 Api 구현
    + 배포
  * 정석진
    + 구현  테이블 설계
    + 회원가입 및 로그인,알림,마이페이지,지원,댓글 api구현
    + 배포(HTTPS)

- 개발 환경 :: Springboot 2.4.5, jdk 1.8 , Spring data JPA , Spring security, Junit4

- 배포환경 :: Gradle, AWS S3, AWS EC2

- 데이터 베이스 :: Mysql(AWS RDS)

- 형성 관리 툴 :: git

- 일정 관리 툴 :: Notion,Slack

- 주요 기능 
  * 마이페이지 
  * 프로젝트 지원 및 지원 취소
  * smalltalk,프로젝트 자랑하기 게시글 및 댓글 CRUD
  * 서비스 데이터 자동 최신화(프로젝트 모집글)

<br>

## 테이블 설계 

![image](https://user-images.githubusercontent.com/78028746/119464301-8da69b80-bd7d-11eb-9f0e-b94edf8f95c2.png)


<br>

## 주요 기능 소개 

### `문자인증`

* 기존의 인증 시스템은 사업자 등록이 필요. -> 사용자가 입력한 전화번호로 인증 번호를 발송하고 해당 번호가 일치하는 지를 확인하는 방식으로 대체
* coolsms 라이브러리를 이용한 문자 발송 
     → Coolsms를 선택한 이유 

     + 토스,한국투자증권같은 기업에서도 사용할 만큼의 안정성
 
     + 많은 사람들이 사용하는 만큼 레퍼런스가 풍부 → 트러블 슈팅에 용이


```java
 public void sendCertNumberSms(String phoneNum,String certNumber){

        Message coolsms = new Message(CoolsmsProperties.api_key, CoolsmsProperties.api_secret);
        HashMap<String, String> params = new HashMap<String, String>();

        params.put("to",phoneNum );
        params.put("from",CoolsmsProperties.fromNumber );
        params.put("type", "SMS");
        params.put("text", "[DeMoim]인증번호는"+certNumber+"입니다.");
        params.put("app_version", "test app 1.2");

        try {

            JSONObject obj = (JSONObject) coolsms.send(params);
            System.out.println(obj.toString());

            //에러가 발생하면
            if(obj.containsKey("error_list")){
                throw new IllegalArgumentException("문자메세지가 발송되지못했습니다.");
            }

        } catch (
                CoolsmsException e) {
            throw new IllegalArgumentException("문자메세지가 발송되지못했습니다.");

        }
    }
```

<br>

### `엔티티 설계시 양방향 매핑`
* 테이블과 패러다임의 불일치를 해소하기위해서 객체가 서로를 참조 할 수있도록 양방향 .
* LAZY 타입을 통해 불필요하게 참조되는 데이터 조회를 해결 -> 성능 이슈를 방지

```java
 @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User smallTalkUser;

    @Builder
    public SmallTalk(String contents) {
        this.contents = contents;
    }

    public void setUser(User user){

        //기존에 있던 smallTalk을 제거
        if(this.smallTalkUser != null){
            this.smallTalkUser.getSmallTalks().remove(this);
        }
        this.smallTalkUser = user;
        smallTalkUser.getSmallTalks().add(this);
    }
```

<br>

### `S3를 이용한 이미지 업로드`

* Quill 에디터 사용 
 + Quill 선택 이유 : ck 에디터, 토스트 등 다른 에디터와 비교하여 가볍고 커스터마이징하기에 용이
* 클라이언트에서 요청이 올때마다 S3에서 이미지를 업로드후 S3_url을 반환


<br>

### `테스트코드 작성`

* mock을 이용해서 unit 테스트 작성.
```java

    @Test
    public void 프로젝트자랑하기_등록() throws Exception {

        //given
        // 형식을 맞추주기위한 빈 객체 생성
        User user = new User();
        ExhibitionDto exhibitionDto = new ExhibitionDto();
        ResponseUserDto responseUserDto = new ResponseUserDto();
        ExhibitionResponseDto exhibitionResponseDto = new ExhibitionResponseDto();
        MockMultipartFile file = new MockMultipartFile("file", "img.jpg", "/jpg,png", "data".getBytes(StandardCharsets.UTF_8));

        Authentication authentication = mock(Authentication.class);

        //when

        // method의 input과 out 값을 지정해줌.
        given(exhibitionService.createExhibition(authentication,exhibitionDto.toString(),file))
                .willReturn(exhibitionResponseDto);

        given(userService.findCurUser(authentication)).willReturn(java.util.Optional.of(user));

        // then
        mvc.perform(multipart("/api/exhibition")
                .file("requestBody",exhibitionDto.toString().getBytes(StandardCharsets.UTF_8))
                .file(file)
                .param("authentication",new ObjectMapper().writeValueAsString(authentication)))
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

```
<br>

### 프로젝트 지원 및 팀원 선택 기능
* Apply 테이블을 만들어 해당 프로젝트의 지원관련 데이터를 관리 
* 리더인지 팀원인지 따로 권한을 줘서 설정하지않은 이유 
 + 한 유저가 팀원이 될 수도 있고 리더가 될 수도 있기때문에 Spring Security의 권한을 이용하여 관리하기 복잡하다고 판단.
 + 회의 결과 사실상 게시글의 작성자가 리더임으로 따로 권한을 줘서 불필요한 비용을 발생시킬 이유가 없다는 결론을 내림.
 
 

<br>
<br>

## 트러블 슈팅

### 1. n+1상황에서 join fetch가 작동하지않는 문제 

* 문제 발생

     + join fetch를 통해 n+1 문제를 해결하고자했지만 n+1 문제해결 시도.

* 문제 해결

     + paging되어있는 경우 join fetch이 적용되지않는 다는 문제를 발견 -> @Entity Graph를 통해 n+1 문제 해결

![image](https://user-images.githubusercontent.com/78028746/120094693-e5b61700-c15c-11eb-8e3e-3ba2ec694117.png)

<br>

### 2. CoolSms를 이용한 문자 인증 코드가 작동하는데 문자가 안가는 문제

![image](https://user-images.githubusercontent.com/78028746/120095121-14cd8800-c15f-11eb-8e3b-f8c71a55099f.png)


* 문제 발생 

     + 코드가 작동하는데 문자를 받지못하는 피드백 발생.
     + 사업자등록을 하지않아 안심번호로 문자를 발송하고있었는데 **통신사의 설정에 따라 안심번호로 오는 문자가 거부되는 상황이 발생**

* 해결 방법 

     + 010으로 시작하는 번호로 문자 발송 → 문자가 문제없이 발송됨을 확인
     + 코드를 제대로 구현해도 외부적인 요인으로 인해 사용자에게 불편함을 줄 수 있다는 것, **개발자가 개발만 잘하면 되는 게 아니라 기획적인 요소 , 외부적인 요인 까지 고려해야한다는 배움**을 얻었습니다.
    
<br>

### 3. 테스트 코드 작성시 authentication을 파라미터로 받아오지못하는 문제.

* 문제 발생 

     + 파라미터로 authetication을 받아오지못하는 controller의 테스트 작성 중 authentication을 받아오지못하는 문제 발생했습니다.
 
 ```java 
 @PostMapping("/api/exhibition")
    public ExhibitionResponseDto createExhibition(Authentication authentication,
																									 @RequestPart(value = "requestBody") String requestBody, 
																									@RequestPart MultipartFile file) {
        return exhibitionService.createExhibition(authentication,requestBody,file);
    }
 ```
 * 문제 해결
 
     + MockCustomUser를 통해 ROLE_USER에 대한 권한 문제는 해결 했지만 authentication을 받아오지 못함
     + 강제로 authentcation을 만들어주는 방법을 시도 → 성공했지만 authenticatino을 실제로 만들어서 불필요한 비용을 발생시킨다는 점 테스트 코드에 적합하지않다고 판단
     + mock을 이용해 authentication 클래스를 생성해서 테스트 코드를 작성했습니다.

![image](https://user-images.githubusercontent.com/78028746/121785366-6b71a200-cbf4-11eb-990e-3c80ab2640df.png)

<br>
<br>


## 최종 성과 

* 작은 숫자라고 할수있지만 실제로 사용자를 런칭하고 피드백을 받아보는  목표를 실제로 이루워낸 경험입니다. 제가 개발한 사이트를 실제로 사용해주시는 분들이 있다는 점에서 뿌듯함을 느꼈던 프로젝트입니다.


![image](https://user-images.githubusercontent.com/78028746/121785420-ab388980-cbf4-11eb-98e8-0a2f4935797c.png)


![image](https://user-images.githubusercontent.com/78028746/121783238-6dcdff00-cbe8-11eb-9f34-2abfa34a9e0b.png)

<br>
<br>

## 실제 사이트에 올라온 사용자 게시글

![image](https://user-images.githubusercontent.com/78028746/121785373-74fb0a00-cbf4-11eb-8e79-d7c237ac3171.png)
![image](https://user-images.githubusercontent.com/78028746/121785380-7cbaae80-cbf4-11eb-91d6-7f59deadca97.png)
![image](https://user-images.githubusercontent.com/78028746/121785386-83e1bc80-cbf4-11eb-8999-614cba71e9cb.png)
![image](https://user-images.githubusercontent.com/78028746/121785393-87754380-cbf4-11eb-95f0-ade27b7436a5.png)




