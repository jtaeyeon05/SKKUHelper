# SKKUHelper

> 모바일앱프로그래밍실습 텀 프로젝트

성균관대학교 학생들을 위한 편의 기능들을 제공하는 안드로이드 애플리케이션

## 유저 메뉴얼

### Canvas LMS API 토큰 발급

1. [canvas.skku.edu/profile/settings](https://canvas.skku.edu/profile/settings)에 방문하고 로그인합니다.   
2. '승인된 통합' 문단에서 '새 엑세스 토큰' 버튼을 클릭합니다.     
3. 목적과 만료일을 입력한 뒤, 토큰을 생성합니다.   
4. 발급한 토큰을 저장해둡니다.   

### 설치 및 실행

#### 요구 사항

IDE: Android Studio (AGP 8.13.1가 호환되는 최신 버전 권장)  
JDK: Java 11  
Android SDK: Minimum 29, Target 36  

#### 프로젝트 불러오기

1. git clone "https://github.com/jtaeyeon05/SKKUHelper.git" 을 입력하여 프로젝트를 클론합니다.   
2. Android Studio를 실행하고 File > Open을 통해 프로젝트의 루트 폴더를 엽니다.   
3. Gradle Sync가 완료될 때까지 기다립니다.   

#### 테스트 코드 작성

1. app/src/main/java/com/skku_team2/skku_helper/ 폴더에 Secret.kt 파일을 생성합니다.   
2. Secret.kt에 아래와 같은 코드를 작성합니다. CANVAS_KEY에는 이전에 발급받은 토큰을 작성합니다.   
```kotlin
package com.skku_team2.skku_helper
object Secret { const val CANVAS_KEY = "{토큰}" }
```

#### 앱 실행

1. Android Studio 상단 툴바에서 실행할 디바이스를 선택합니다.   
2. Run (▶) 버튼을 눌러 앱을 빌드하고 실행합니다.   
