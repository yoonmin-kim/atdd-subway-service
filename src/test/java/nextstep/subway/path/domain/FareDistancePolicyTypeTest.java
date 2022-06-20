package nextstep.subway.path.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class FareDistancePolicyTypeTest {
    @DisplayName("올바른 Type 생성 테스트")
    @ParameterizedTest(name = "올바른 Type 생성: distance = {0}, 생성타입 = {1}")
    @MethodSource("provideParametersForFareDistanceTypeCreate")
    void FareDistancePolicyType_생성(int distance, FareDistancePolicyType type){
        FareDistancePolicyType createdType = FareDistancePolicyType.of(distance);
        assertThat(createdType.equals(type)).isTrue();
    }

    @DisplayName("Type 별 요금 계산 테스트")
    @ParameterizedTest(name = "Type 별 요금 계산: distance = {0}, 요금 = {1}")
    @MethodSource("provideParametersForFareDistanceTypeCalculate")
    void FareDistancePolicyType_요금_계산(int distance, int fare){
        int calculatedFare = FareDistancePolicyType.of(distance).calculateFare(distance);
        assertThat(calculatedFare).isEqualTo(fare);
    }

    private static Stream<Arguments> provideParametersForFareDistanceTypeCreate() {
        return Stream.of(
                Arguments.of(10, FareDistancePolicyType.BASIC_DISTANCE),
                Arguments.of(50, FareDistancePolicyType.MIDDLE_DISTANCE),
                Arguments.of(100, FareDistancePolicyType.LONG_DISTANCE)
        );
    }

    private static Stream<Arguments> provideParametersForFareDistanceTypeCalculate() {
        return Stream.of(
                Arguments.of(10, 1250),
                Arguments.of(12, 1350),
                Arguments.of(50, 2050),
                Arguments.of(100, 2750)
        );
    }
}
