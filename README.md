<div align="center" >
  <img src="./etc/icon.PNG", width=70% alt="logo">  
  <h1>온라인 화상 교육 및 모임 플랫폼</h1>
</div>
이 프로젝트는 실시간 온라인 강의를 제공하는 플랫폼입니다. 사용자들은 다양한 카테고리의 강의를 수강하고, 강사들은 실시간으로 강의를 진행할 수 있습니다. 화상 통화, 실시간 채팅, 화면 공유 등의 기능을 통해 효과적인 온라인 학습 환경을 제공합니다.

| **역할**     | **이름** | **주담당**                                              |
|--------------|----------|---------------------------------------------------------|
| **팀장**     | 남동균   | DB 설계, Backend 개발                                    |
| **팀원**     | 강유미   | WebRTC 구현, Backend 개발                                |
|              | 유석민   | 로그인 서비스, Backend 개발                               |
|              | 정다운   | UI/UX디자인, Frontend 개발                               |
|              | 정현호   | 인프라 설정, Frontend 개발, AI                           |


## 목차

1. [프로젝트 소개](#프로젝트-소개)
2. [주요 기능](#주요-기능)
3. [기술 스택](#기술-스택)
4. [시스템 아키텍처](#시스템-아키텍처)
5. [데이터 모델링](#데이터-모델링)
6. [API 명세서](#API-명세서)
7. [설치 및 실행 방법](#설치-및-실행-방법)

## 프로젝트 소개

이 프로젝트는 사용자들이 실시간으로 온라인 강의를 제공하고 수강할 수 있는 플랫폼입니다. 다양한 카테고리의 강의를 제공하며, 강사들은 별도의 소프트웨어 없이 플랫폼 내에서 실시간으로 강의를 진행할 수 있습니다. 플랫폼은 화상 통화, 실시간 채팅, 화면 공유 등 다양한 기능을 통해 효과적인 온라인 학습 환경을 제공합니다.

특히, 누구나 부담 없이 자신만의 강의를 생성하고 참여할 수 있는 기능을 통해 보다 넓은 범위의 사용자들이 자신의 지식과 경험을 나눌 수 있는 기회를 제공합니다. 플랫폼은 누구에게나 강의 생성 권한을 부여하여 강의 생성의 제약을 최소화하여 사용자들이 쉽게 강의를 시작할 수 있도록 지원합니다.

이를 통해, 사용자들은 간단한 써클 생성과 참여를 통해 자신만의 학습 커뮤니티를 형성할 수 있으며, 강사와 학생 간의 실시간 소통을 통해 더욱 풍부한 학습 경험을 제공합니다.

## 주요 기능

### 1. 강의 목록 및 추천

- 태그기반의 추천 강의 표시
- 최신 강의, 인기 강의, 무료 강의 목록 제공

![메인페이지](./etc/pages/1_%EB%A9%94%EC%9D%B8%ED%8E%98%EC%9D%B4%EC%A7%80.JPG)


### 2. 검색 및 태그 기능

- 강의 검색 기능
![강의검색](./etc/pages/2_%EA%B0%95%EC%9D%98%EA%B2%80%EC%83%89.JPG)

- 태그 기반 강의 필터링 (#요리, #운동, #드로잉, #음악, #프로그래밍 등)
![강의태그](./etc/pages/2_%EA%B0%95%EC%9D%98%ED%83%9C%EA%B7%B8.JPG)

### 3. 강의 상세 페이지

- 강의 소개 (제목, 한 줄 소개, 상세 소개, 커리큘럼 등)
![강의상세페이지](./etc/pages/3_%EA%B0%95%EC%9D%98%EC%83%81%EC%84%B8%ED%8E%98%EC%9D%B4%EC%A7%80.JPG)
![커리큘럼](./etc/pages/3_%EC%BB%A4%EB%A6%AC%ED%81%98%EB%9F%BC.JPG)

- 공지사항 및 후기
![후기](./etc/pages/3_%ED%9B%84%EA%B8%B0.JPG)

- 수강 신청 기능
![수강신청](./etc/pages/3_%EC%88%98%EA%B0%95%EC%8B%A0%EC%B2%AD.JPG)

### 4. 실시간 화상 강의

- WebRTC 기반 실시간 화상 통신
- 화면 공유 기능, 실시간 채팅
- 강의가 끝나면 자동으로 녹화 영상 업로드 기능
![개설한강의](./etc/pages/4_%EA%B0%95%EC%82%AC%EA%B0%80%EA%B0%9C%EC%84%A4%ED%95%9C%EA%B0%95%EC%9D%98.JPG)
![강의열기](./etc/pages/4_%EA%B0%95%EC%82%AC%EA%B0%95%EC%9D%98%EC%97%B4%EA%B8%B0.JPG)
![화면공유채팅](./etc/pages/4_%ED%99%94%EB%A9%B4%EA%B3%B5%EC%9C%A0%EC%B1%84%ED%8C%85.JPG)

### 5. 로그인 및 회원가입

- Spring Security와 JWT 토큰을 이용하여 자체 로그인 기능을 구현
- 회원가입 시, 강사를 선택하면 사이트 내 강의 개설 가능
![로그인](./etc/pages/5_%EB%A1%9C%EA%B7%B8%EC%9D%B8.JPG)
![회원가입](./etc/pages/5_%ED%9A%8C%EC%9B%90%EA%B0%80%EC%9E%85.JPG)

### 6. 마이페이지

- 수강 중인 강의 목록
- 회원 정보 수정
- 선호 태그 설정
![마이페이지](./etc/pages/6_%EB%A7%88%EC%9D%B4%ED%8E%98%EC%9D%B4%EC%A7%80.JPG)
![정보수정](./etc/pages/6_%EC%A0%95%EB%B3%B4%EC%88%98%EC%A0%95.JPG)

### 7. AI 기능

- NSFW Detection : ViT기반으로 NSFW 데이터셋을 통해 Fine-tunning 진행한 자체 모델 사용
- 10회 이상 탐지시 실시간 강의에서 자동 강퇴 처리
<div align="center" >
  <img src="./etc/pages/7_%EC%9B%B9%EC%86%8C%EC%BC%93.png", width=70% alt="websocket">  
  <img src="./etc/pages/7_nsfw.png", width=30% alt="nsfw">  
</div>


## 기술 스택

![기술스택](./etc/%EA%B8%B0%EC%88%A0%EC%8A%A4%ED%83%9D.PNG)

- Frontend: React 18, TypeScript 5.5.3
- Backend: Java 17, Spring Boot 2.4.5
- Database: MariaDB, Redis
- Streaming: openVidu
- AI: Python, WebSocket, ONNX
- Infra : Docker, Nginx, AWS
- Test : Sonarqube, JMeter

## 시스템 아키텍처

![시스템 아키텍처](./etc/%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98%EA%B5%AC%EC%A1%B0.png)

## 데이터 모델링

- ERD 다이어그램

![ERD](./etc/ERD%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8.png)

- 강사 입장의 클래스 다이어그램

![강사](./etc/%ED%81%B4%EB%9E%98%EC%8A%A4%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8_%EA%B0%95%EC%82%AC.png)

- 사용자 입장의 클래스 다이어그램
  
![사용자](./etc/%ED%81%B4%EB%9E%98%EC%8A%A4%EB%8B%A4%EC%9D%B4%EC%96%B4%EA%B7%B8%EB%9E%A8_%EC%82%AC%EC%9A%A9%EC%9E%90.png)


## API명세서

### Live 서비스
화상 통화, 실시간 채팅, 녹화 등의 기능을 포함한 라이브 서비스
| 기능 | 메서드 | 요청 주소 |
|------|--------|------------|
| Live 여부 확인 | GET | `/api/sessions/{course_id}` |
| Live 방 개설 | POST | `/api/sessions/{course_id}` |
| Live 방 접속 | POST | `/api/sessions/{course_id}/connections` |
| Live 종료 | DELETE | `/api/sessions/{course_id}` |
| Live 방 퇴출 | DELETE | `/api/sessions/{course_id}/connections` |

### 강의 서비스
로그인하지 않고, 누구나 얻을 수 있는 강의 관련한 정보
| 기능 | 메서드 | 요청 주소 |
|------|--------|------------|
| 태그 목록 조회 | GET | `/api/tag` |
| 강의 목록 조회 | GET | `/api/courses` |
| 강의 정보 상세 조회 | GET | `/api/courses/{course_id}` |
| 강의의 커리큘럼 조회 | GET | `/api/courses/{course_id}/curriculums` |
| 강사 정보 조회 | GET | `/api/courses/{course_id}/owner` |
| 강의 검색 결과 조회 | GET | `/api/courses/search` |

### 강의(관리자) 서비스
강사가 등록한 강의를 승인하거나 반려할 수 있는 기능
| 기능 | 메서드 | 요청 주소 |
|------|--------|------------|
| 승인 요청 대기 중인 강의 목록 | GET | `/api/course/admin/pending` |
| 요청된 강의 승인 처리 | PATCH | `/api/course/admin/approve/{course_id}` |
| 요청된 강의 반려 처리 | PATCH | `/api/course/admin/reject/{course_id}` |

### 강의(강사) 서비스
강사 입장에서 사용할 수 있는 강의 CRUD, 커리큘럼 CRUD 기능
| 기능 | 메서드 | 요청 주소 |
|------|--------|------------|
| 새로운 강의 만들기 | POST | `/api/courses/instructions` |
| 내가 개설한 강의 목록 조회 | GET | `/api/courses/instructions` |
| 기존 강의 수정 | PATCH | `/api/courses/instructions/{course_id}` |
| 강의 삭제 | DELETE | `/api/courses/instructions/{course_id}` |
| 새로운 커리큘럼 만들기 | POST | `/api/courses/{course_id}/curriculum` |
| 기존 커리큘럼 수정 | PATCH | `/api/courses/{course_id}/curriculum/{curriculum_id}` |
| 커리큘럼 삭제 | DELETE | `/api/courses/{course_id}/curriculum/{curriculum_id}` |
| 상태 별 강사의 강의 목록 | GET | `/api/courses/instructions/{status}` |
| 개설한 강의 승인 요청 보내기 | PATCH | `/api/courses/instructions/request/{course_id}` |
| 개설한 강의 승인 요청 취소하기 | PATCH | `/api/courses/instructions/cancelRequest/{course_id}` |

### 강의(수강생) 서비스
강의를 수강신청하고 강의 자료와 녹화 영상을 받을 수 있는 서비스
| 기능 | 메서드 | 요청 주소 |
|------|--------|------------|
| 내가 수강한 강의 목록 조회 | GET | `/api/courses/registers` |
| 수강 신청 | POST | `/api/courses/registers/{course_id}` |
| 수강 여부 조회 | GET | `/api/courses/registers/{course_id}` |
| 수강 취소 | DELETE | `/api/courses/registers/{course_id}` |
| 강의 학습용 문서 url, 녹화 영상 url 반환 | GET | `/api/courses/{course_id}/dataUrls` |


### 공지사항 및 리뷰 서비스
강사의 공지사항 CRUD, 수강생의 리뷰 CRUD 기능
| 기능 | 메서드 | 요청 주소 |
|------|--------|------------|
| 모든 공지사항 조회 | GET | `/api/courses/{courseId}/announcements` |
| 공지사항 작성 | POST | `/api/courses/{courseId}/announcements` |
| 공지사항 조회 | GET | `/api/courses/{courseId}/announcements/{announcementId}` |
| 공지사항 삭제 | DELETE | `/api/courses/{courseId}/announcements/{announcementId}` |
| 공지사항 수정 | PATCH | `/api/courses/{courseId}/announcements/{announcementId}` |
| 강의별 리뷰 조회 | GET | `/api/courses/{courseId}/reviews` |
| 리뷰 작성 | POST | `/api/courses/{courseId}/reviews` |
| 리뷰 조회 | GET | `/api/courses/{courseId}/reviews/{reviewId}` |
| 리뷰 삭제 | DELETE | `/api/courses/{courseId}/reviews/{reviewId}` |
| 리뷰 수정 | PATCH | `/api/courses/{courseId}/reviews/{reviewId}` |

### 멤버 및 인증 서비스
회원가입 및 로그인, Security관련 기능
| 기능 | 메서드 | 요청 주소 |
|------|--------|------------|
| 회원가입 | POST | `/api/member/signup` |
| 회원탈퇴 | DELETE | `/api/member/withdraw` |
| 로그인 | POST | `/api/auth/login` |
| 마이페이지 | GET | `/api/member` |
| 회원정보수정 | PATCH | `/api/member/modify` |
| Access Token 재발급 | POST | `/api/auth/reissue` |


## 설치 및 실행 방법


1. 저장소 클론

```
git clone https://github.com/l-umi/circleup
```
실시간 강의를 위해서는 openVidu 서버가 필요합니다 
(openVidu가 없다면, ./backend/src/main/java/com/ssafy/api/controller/SessionController.java 파일을 삭제하세요)

2. 데이터베이스 생성

etc 파일 내의 SQL 파일(data.sql)을 실행합니다
  

4. 백엔드 실행

```
cd backend
./gradlew bootRun
```

4. 프론트엔드 실행

```
cd frontend
npm install
npm start
```

5. 브라우저에서 `http://localhost:5173` 접속


## 라이센스

이 프로젝트는 SSAFY 하에 있습니다.
