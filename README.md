# 냥씨알림(Alert weather cat)
> 비오는 날 우산을 깜빡하신 적 있으신가요? 이제 냥씨알림이 도와드릴게요!
![awc](https://github.com/guswns9302/alert_weather_cat/assets/87797716/a523d706-098f-4ab4-9544-b6a310434f14)
https://www.notion.so/exit-here/d598e38889a248c59318f3c431833eae

## 개발 목표
### 1. 이런 문제를 해결해요.
- 눈, 비, 소나기 등의 악천후 날씨에 밖에 있어서 우산이 없었던 곤란한 경험이 있으시죠? 
- 악천후 날씨에 알림을 보내 곤란한 상황을 해결해요.
- 매번 날씨 앱을 켜서 날씨를 확인해아 하는 번거로움이 있으시죠?
- 날씨를 요약해서 알림을 보내 해결해요.

### 2. 원하는 시간에, 원하는 곳의 날씨 정보를 확인하는 날씨 앱
- 푸시알림, 음성알림(update 예정), 앱 접속 등 사용자가 원하는 형태로
- 오늘, 내일, 주간 날씨 정보를 확인할 수 있는 서비스가 되는 것이 목표에요.

## 개발 환경
- Spring boot / gradle / Jpa
- mySql
- Intellij / dataGrip

## 배포 서버
- AWS Lightsail(Ubuntu 20.04)
- Lightsail Load balancer

## Architecture
![image](https://github.com/guswns9302/alert_weather_cat/assets/87797716/c5f57fbd-a498-4533-8e63-3664f935bb8d)

## 주요 기능
### 1. Firebase push
<img src="https://github.com/guswns9302/alert_weather_cat/assets/87797716/f8108a36-c07f-4fef-99ee-8777845f9c7c" width="50%"></img>
- fcm push flow
> 1. 앱 실행시 Notification Server(Firebase)로부터 클라이언트 각각의 앱을 구분하는 인증 Token을 발급 받는다.
> 2. 클라이언트 앱에서 Provider(awc server)로 Token을 전달한다.
> 3. awc server은 전달받은 Token을 저장한다.
> 4. 사용자 정의에 따라 push를 보내는 상황이 오면 awc server은 Notification Server로 access token을 발급 받는다.
> 5. 발급받은 access token을 request header에 담고, push message를 구성해 Notification Server로 push message를 요청한다.
> 6. 클라이언트 앱에서 Push 확인한다.
![image](https://github.com/guswns9302/alert_weather_cat/assets/87797716/40262335-6a69-484c-9cfa-57c203a76d12)

### 2. Weather summary
<img src="https://github.com/guswns9302/alert_weather_cat/assets/87797716/4a64fa96-4316-4466-bb25-ae833245e65f" width="50%"></img>
- 날씨 정보를 제공하기 위해서 기상청 API와 에어코리아 API를 사용한다.
> 1. 단기예보 조회 서비스 (조회일 기준 최대 3일의 기상 정보) 
> 2. 중기예보 조회 서비스 (조회일 기준 3일 후 ~ 10일 후 의 기상 정보)
> 3. 대기 오염 정보 조회 서비스 (미세먼지/초미세먼지)
- API를 통해 제공 받은 기상 데이터를 조합해서 클라이언트 앱으로 내려줄 데이터 가공
> 1. 단기예보 api로 부터 조회시점 기준 24시간 기상 정보 parsing
> 2. 단기예보 api와 중기예보 api로부터 주간 기상 정보 parsing
> 3. 대기 오염 정보 api로부터 미세먼지, 초미세먼지 정보 parsing

## 진행하며 겪은 문제점 및 해결
### 1. 공공데이터 포털에서 제공하는 API의 일일 트래픽 문제
- 각 API의 일일 트래픽은 정해져 있지만, 발급받는 트래픽으로 전국의 날씨 데이터를 조회할 수 없음.
> 1. 단기예보, 중기예보 -> 일일 트래픽 10000 건.
> 2. 대기 오염 정보 -> 일일 트래픽 500 건.
- 해결
> 1. 사용자 요청에 의해 매번 API를 호출해 데이터를 parsing 하는 부분을 변경한다.
> 2. 1차적으로 API를 통해 제공 받은 기상 정보를 Database에 저장하고, 동일한 기준 시간, 지역의 기상 정보 요청이 오면 Database에서 먼저 조회하여 중복된 요청의 API 트래픽을 제거한다.
> 3. API Document를 보면 행정구역별 격자 x,y좌표가 제공되는데, 인접 지역의 경우 격자 x,y 좌표가 동일한 경우 중복 요청이 된다.
> 4. 제공되는 행정구역별 격자 x,y 좌표를 키로 행정구역 이름을 매칭시켜서 중복 요청을 제거하여 API 트래픽을 제거한다. 

## 아쉬운 점
- 기획 단계에서 시장조사 시 나왔던 예상 사용량에 한참 못미쳐 아쉽다.
- 그로 인해 서비스 종료로 결정이 났다.
