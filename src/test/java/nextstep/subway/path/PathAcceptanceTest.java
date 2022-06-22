package nextstep.subway.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.auth.domain.Age;
import nextstep.subway.auth.dto.TokenResponse;
import nextstep.subway.line.acceptance.LineAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static nextstep.subway.auth.acceptance.AuthAcceptanceTest.로그인_요청;
import static nextstep.subway.auth.acceptance.AuthAcceptanceTest.암호_이메일_입력;
import static nextstep.subway.line.acceptance.LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청;
import static nextstep.subway.member.MemberAcceptanceTest.*;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@DisplayName("지하철 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {
    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;

    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;

    private TokenResponse tokenResponse;

    private String EMAIL = "email@email.com";

    @BeforeEach
    public void setUp() {
        super.setUp();

        tokenResponse = Login(EMAIL, 20);

        강남역 = StationAcceptanceTest.지하철역_등록되어_있음("강남역").as(StationResponse.class);
        양재역 = StationAcceptanceTest.지하철역_등록되어_있음("양재역").as(StationResponse.class);
        교대역 = StationAcceptanceTest.지하철역_등록되어_있음("교대역").as(StationResponse.class);
        남부터미널역 = StationAcceptanceTest.지하철역_등록되어_있음("남부터미널역").as(StationResponse.class);

        신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역, 양재역, 10, 200);
        이호선 = 지하철_노선_등록되어_있음("이호선", "bg-red-600", 교대역, 강남역, 10, 500);
        삼호선 = 지하철_노선_등록되어_있음("삼호선", "bg-red-600", 교대역, 양재역, 5, 100);

        지하철_노선에_지하철역_등록_요청(삼호선, 교대역, 남부터미널역, 3);
    }

    @DisplayName("출발역과 도착역 정보로 최단경로 요청시 경로 정보를 알려준다.")
    @Test
    void findShortedRoute() {
        // When
        final ExtractableResponse<Response> response = 최단_경로_검색(tokenResponse, 교대역, 양재역);

        // Then
        최단_경로_기준으로_지하철역_정보가_출력됨(response, Arrays.asList(교대역, 남부터미널역, 양재역));
    }

    /**
     * When : 등록되지 않은 지하철 역으로 요청 시
     * Then : 검색이 되지 않는다.
     */
    @DisplayName("등록되지 않은 지하철역 정보로 경로 요청시 검색이 되지 않는다.")
    @Test
    void findInvalidStationId() {
        // Given
        final StationResponse 등록되지않은역 = new StationResponse(100L, "잘못된역", LocalDateTime.now(), LocalDateTime.now());

        // When
        final ExtractableResponse<Response> reponse = 최단_경로_검색(tokenResponse, 등록되지않은역, 양재역);

        // Then
        검색이_안됨(reponse);
    }

    /**
     * Given : 출발역과 도착역이 서로 연결되어 있지 않은 상태에서
     * When : 출발역과 도착역 정보로 경로 검색 시
     * Then : 검색이 되지 않는다.
     */
    @DisplayName("출발역과 도착역 구간이 서로 연결 되어 있지 않으면 검색이 되지 않는다.")
    @Test
    void noConnectionSectionTest() {
        // Given
        final StationResponse 수원역 = StationAcceptanceTest.지하철역_등록되어_있음("수원역").as(StationResponse.class);
        final StationResponse 병점역 = StationAcceptanceTest.지하철역_등록되어_있음("병점역").as(StationResponse.class);
        final LineResponse 일호선 = 지하철_노선_등록되어_있음("일호선", "bg-red-600", 수원역, 병점역, 10, 0);

        // When
        final ExtractableResponse<Response> response = 최단_경로_검색(tokenResponse, 교대역, 병점역);

        // Then
        검색이_안됨(response);
    }

    /**
     * When : 출발역과 도착역을 같은 값으로 경로 요청 시
     * Then : 검색이 되지 않는다.
     */
    @DisplayName("경로 검색시 출발역과 도착역이 같으면 검색이 안된다.")
    @Test
    void sameStationTest() {
        // When
        final ExtractableResponse<Response> response = 최단_경로_검색(tokenResponse, 교대역, 교대역);

        // Then
        검색이_안됨(response);
    }


    /**
     * When : 출발역과 도착역을 입력시
     * Then : 경로와 거리 , 금액을 제공한다.
     */
    @DisplayName("구간정보와 거리, 금액을 알수있다.")
    @Test
    void checkSectionAndDistanceAndCharge() {
        // when
        final ExtractableResponse<Response> response = 최단_경로_검색(tokenResponse, 교대역, 양재역);

        // then
        거리_금액_확인(response, 5, 1350);
    }

    public static void 거리_금액_확인(ExtractableResponse<Response> response, int expectedDistance, int expectedPrice) {
        final PathResponse pathResponse = response.as(PathResponse.class);
        assertThat(pathResponse.getDistance()).isEqualTo(expectedDistance);
        assertThat(pathResponse.getExtraCharge()).isEqualTo(expectedPrice);
    }

    public static void 최단_경로_기준으로_지하철역_정보가_출력됨(ExtractableResponse<Response> response, List<StationResponse> expectedResult) {
        final PathResponse pathResponse = response.as(PathResponse.class);
        final List<String> resultStationNames = pathResponse.getStations().stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        final List<String> expectedStationNames = expectedResult.stream()
                .map(StationResponse::getName)
                .collect(Collectors.toList());

        assertThat(resultStationNames).containsExactlyElementsOf(expectedStationNames);
    }

    public ExtractableResponse<Response> 최단_경로_검색(final TokenResponse tokenResponse, final StationResponse source, final StationResponse target) {
        return RestAssured.given().log().all()
                .auth().oauth2(tokenResponse.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().get("/paths?source={sourceId}&target={targetId}", source.getId(), target.getId())
                .then().log().all()
                .extract();
    }

    public static LineResponse 지하철_노선_등록되어_있음(String name, String color, StationResponse preStation, StationResponse downStation, int distance, long extraCharge) {
        return LineAcceptanceTest.지하철_노선_등록되어_있음(new LineRequest(name, color, preStation.getId(), downStation.getId(), distance, extraCharge)).as(LineResponse.class);
    }


    public static void 검색이_안됨(ExtractableResponse<Response> reponse) {
        assertThat(HttpStatus.valueOf(reponse.statusCode())).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    public static TokenResponse Login(final String email, final int age) {
        final String PASSWORD = "password";
        ExtractableResponse<Response> createResponse = 회원_생성을_요청(email, PASSWORD, age);
        회원_생성됨(createResponse);

        return 토큰정보_획득(로그인_요청(암호_이메일_입력(PASSWORD, email)));
    }
}
