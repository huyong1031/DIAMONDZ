import axios from "axios";
import { Cookies } from "react-cookie";

const jaxios = axios.create();
const cookies = new Cookies();

const beforeReq = async (config) => {
  // 1) 만약 현재 요청 URL이 "/api/product/bestPro", "/api/product/newPro" 처럼
  //    누구나 접근 가능한 공개 API라면, '사용자 정보 체크' 로직을 SKIP:
  if (
    config.url.includes("/api/product/bestPro") ||
    config.url.includes("/api/product/newPro") ||
    config.url.includes("/api/member/auth/send-email") || // ✅ JWT 없이 접근 가능해야 하는 API 추가
    config.url.includes("/api/member/auth/verify-email")
  ) {
    return config; // 사용자 정보 없이 그냥 통과
  }

  // 2) 그 외엔 로그인 사용자 정보가 필요한 로직 수행
  try {
    console.log("beforeReq 호출됨", config); // 요청 전 로그

    // 쿠키에서 사용자 정보를 추출합니다
    const currentUser = cookies.get("loginUser");

    if (!currentUser) {
      console.error("현재 사용자 정보가 없습니다.");
      throw new Error("사용자 정보가 없습니다.");
    }

    const accessToken = currentUser.accessToken;
    const refreshToken = currentUser.refreshToken;

    if (!accessToken || !refreshToken) {
      console.error("로그인 정보가 부족합니다.");
      throw new Error("로그인 정보가 부족합니다.");
    }

    // Header 양식으로 조립해서 (기존 토큰을 헤더에 세팅)
    const headers = {
      Authorization: `Bearer ${accessToken}`,
    };

    console.log("토큰 갱신 요청 시작");

    // axios로 토큰 검증을 요청합니다. (params 제거):  refreshToken을 사용해 토큰 검증/갱신 요청
    const res = await axios.get(`/api/member/refresh`, {
      headers,
      params: { refreshToken },
    });

    console.log("토큰 갱신 결과:", res.data); // 갱신된 토큰 정보 로그 출력

    // 검증 후 반환받은 두 개의 토큰을 현재 유저의 사용자 정보에 추가하고
    if (res.data && res.data.accessToken && res.data.refreshToken) {
      currentUser.accessToken = res.data.accessToken;
      currentUser.refreshToken = res.data.refreshToken;

      // 쿠키 업데이트(객체를 JSON 문자열로 변환해서 저장)
      cookies.set("loginUser", JSON.stringify(currentUser));

      // accessToken만 따로 다시 헤더에 조립하여 config 완성
      config.headers.Authorization = `Bearer ${currentUser.accessToken}`;

      console.log("업데이트된 헤더:", config.headers); // 최종 헤더 확인
    } else {
      console.error("토큰 갱신 실패: 서버에서 새로운 토큰을 반환하지 않음");
      throw new Error("토큰 갱신 실패");
    }

    return config; // 요청 완료 후 config 리턴
  } catch (error) {
    console.error("토큰 갱신 중 오류 발생:", error);
    throw error; // 오류 발생 시 에러를 throw
  }
};

const requestFail = (err) => {
  console.error("요청 실패:", err);
};

const beforeRes = async (res) => {
  console.log("응답 데이터:", res); // 응답 데이터 로그 출력
  return res;
};

const responseFail = (err) => {
  console.error("응답 실패:", err);
};

jaxios.interceptors.request.use(beforeReq, requestFail);
jaxios.interceptors.response.use(beforeRes, responseFail);

export default jaxios;
