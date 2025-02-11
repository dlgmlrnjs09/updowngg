# views/PrivacyPolicy.vue
<template>
  <div class="min-h-screen bg-[#0A0A0A] text-gray-200 py-8 px-4">
    <div class="max-w-3xl mx-auto">
      <h1 class="text-2xl font-bold text-white mb-8 text-center">개인정보 처리방침</h1>

      <div class="space-y-8">
        <!-- 수집항목 -->
        <section>
          <h2 class="text-lg font-semibold text-white mb-3 flex items-center">
            <span class="text-[#00ed84] mr-2">01</span> 수집하는 개인정보 항목
          </h2>
          <div class="space-y-2 text-sm leading-relaxed">
            <div class="bg-gray-900/50 p-4 rounded">
              <p class="mb-2">updownGG는 서비스 제공을 위해 다음과 같은 개인정보를 수집합니다:</p>
              <ul class="space-y-2">
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>필수항목: 이메일 주소, Riot/Discord 계정 정보</span>
                </li>
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>서비스 이용 과정에서 생성되는 정보: 접속 로그, IP 주소, 쿠키, 브라우저 종류</span>
                </li>
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>League of Legends 관련 정보: 소환사명, 게임 전적</span>
                </li>
              </ul>
            </div>

            <div class="bg-gray-900/50 p-4 rounded mt-4">
              <p class="font-semibold mb-2">소셜 로그인(OAuth) 정보</p>
              <ul class="space-y-2">
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>Riot Games 계정: 소환사명, Riot ID, 태그라인</span>
                </li>
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>Discord 계정: 사용자명, 이메일, 프로필 정보</span>
                </li>
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>OAuth 서비스 이용 시 자동 생성되는 토큰 정보</span>
                </li>
              </ul>
              <p class="mt-2 text-gray-400">* OAuth 서비스는 언제든지 연결 해제가 가능하며, 해제 시 관련 정보는 즉시 삭제됩니다.</p>
            </div>
          </div>
        </section>

        <!-- 이용목적 -->
        <section>
          <h2 class="text-lg font-semibold text-white mb-3 flex items-center">
            <span class="text-[#00ed84] mr-2">02</span> 개인정보의 수집 및 이용목적
          </h2>
          <div class="space-y-2 text-sm leading-relaxed">
            <div class="bg-gray-900/50 p-4 rounded">
              <ul class="space-y-2">
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>회원 식별 및 게임 전적 검색 서비스 제공</span>
                </li>
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>소환사 평가 시스템 운영 및 관리</span>
                </li>
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>서비스 이용 통계 분석 및 서비스 개선</span>
                </li>
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>사용자 경험 개선을 위한 행동 패턴 분석 (Google Analytics)</span>
                </li>
              </ul>
            </div>
          </div>
        </section>

        <!-- 보유기간 -->
        <section>
          <h2 class="text-lg font-semibold text-white mb-3 flex items-center">
            <span class="text-[#00ed84] mr-2">03</span> 개인정보의 보유 및 이용기간
          </h2>
          <div class="space-y-2 text-sm leading-relaxed">
            <div class="bg-gray-900/50 p-4 rounded">
              <ul class="space-y-2">
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>회원 가입 정보: 회원 탈퇴 시까지</span>
                </li>
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>게임 전적 및 평가 정보: 작성일로부터 1년</span>
                </li>
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>접속 기록 및 로그: 3개월</span>
                </li>
              </ul>
            </div>
          </div>
        </section>

        <!-- 개인정보 처리 위탁 -->
        <section>
          <h2 class="text-lg font-semibold text-white mb-3 flex items-center">
            <span class="text-[#00ed84] mr-2">04</span> 개인정보 처리 위탁
          </h2>
          <div class="space-y-2 text-sm leading-relaxed">
            <div class="bg-gray-900/50 p-4 rounded">
              <p class="mb-2">updownGG는 서비스 향상을 위해 아래와 같이 개인정보 처리 업무를 외부 전문업체에 위탁하여 운영하고 있습니다:</p>
              <ul class="space-y-2">
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>Google LLC: 서비스 이용 패턴 분석 (Google Analytics)</span>
                </li>
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>Riot Games Inc.: 소환사 정보 및 게임 전적 데이터 제공</span>
                </li>
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>Discord Inc.: 회원 인증 및 로그인</span>
                </li>
              </ul>
            </div>
          </div>
        </section>

        <!-- 쿠키 정책 -->
        <section>
          <h2 class="text-lg font-semibold text-white mb-3 flex items-center">
            <span class="text-[#00ed84] mr-2">05</span> 쿠키 정책
          </h2>
          <div class="space-y-2 text-sm leading-relaxed">
            <div class="bg-gray-900/50 p-4 rounded">
              <p class="mb-2">updonwGG는 서비스 향상을 위해 쿠키를 사용합니다:</p>
              <ul class="space-y-2">
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>필수 쿠키: 로그인 상태 유지, 서비스 이용을 위한 필수 정보</span>
                </li>
                <li class="flex items-start">
                  <span class="text-[#00ed84] mr-2">•</span>
                  <span>분석 쿠키: Google Analytics를 통한 서비스 이용 패턴 분석</span>
                </li>
              </ul>
              <p class="mt-2 text-gray-400">* 브라우저 설정을 통해 쿠키 저장을 거부할 수 있으나, 일부 서비스 이용이 제한될 수 있습니다.</p>
            </div>
          </div>
        </section>

        <!-- 보호책임자 -->
        <section>
          <h2 class="text-lg font-semibold text-white mb-3 flex items-center">
            <span class="text-[#00ed84] mr-2">06</span> 개인정보 보호책임자
          </h2>
          <div class="space-y-2 text-sm leading-relaxed">
            <div class="bg-gray-900/50 p-4 rounded flex items-center justify-between">
              <div>
                <p class="text-white">사이트 운영자</p>
                <p class="text-gray-400">lcb0867@naver.com</p>
              </div>
            </div>
          </div>
        </section>

        <!-- 시행일 -->
        <div class="text-sm text-gray-400 pt-4 space-y-1">
          <p>시행일자: 2024년 2월 11일</p>
          <p>본 개인정보 처리방침은 관련 법령 및 지침의 변경이나 updownGG의 약관 및 내부 정책에 따라 변경될 수 있습니다.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
// No additional script needed for this static page
</script>