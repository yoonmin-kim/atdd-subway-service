package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.util.List;
import nextstep.subway.station.domain.Station;
import org.assertj.core.api.ThrowableAssert.ThrowingCallable;
import org.junit.jupiter.api.Test;

class LineTest {

    Station 강남 = new Station("강남");
    Station 판교 = new Station("판교");
    Station 모란 = new Station("모란");
    Station 서현 = new Station("서현");

    @Test
    void 구간_목록을_가져온다() {
        Line line = new Line("신분당선", "red", 강남, 판교, 10);

        List<Section> sections = line.getSections();

        assertThat(sections).hasSize(1);
    }

    @Test
    void 가장_앞구간_상행역을_가져온다() {
        Line line = new Line("신분당선", "red", 강남, 판교, 10);
        List<Section> sections = line.getSections();
        sections.add(new Section(line, 판교, 모란, 5));

        Station upStation = line.findUpStation();

        assertThat(upStation.getName()).isEqualTo("강남");
    }

    @Test
    void 노선에_속한_역들을_가져온다() {
        Line line = new Line("신분당선", "red", 강남, 판교, 10);

        List<Station> stations = line.getStations();

        assertThat(stations).containsExactly(강남, 판교);
    }

    @Test
    void 이미_등록된_구간은_동일하게_등록_불가능() {
        Line line = new Line("신분당선", "red", 강남, 판교, 10);

        ThrowingCallable addExistedSection = () -> line.addSection(강남, 판교, 5);

        assertThatIllegalArgumentException().isThrownBy(addExistedSection)
                .withMessageContaining("이미 등록된 구간 입니다.");
    }

    @Test
    void 매칭되는_역이_전혀_없으면_등록_불가능() {
        Line line = new Line("신분당선", "red", 강남, 판교, 10);

        ThrowingCallable addNotExistedSection = () -> line.addSection(모란, 서현, 5);

        assertThatIllegalArgumentException().isThrownBy(addNotExistedSection)
                .withMessageContaining("등록할 수 없는 구간 입니다.");
    }

    @Test
    void 등록된_역이_없으면_구간을_추가한다() {
        Line line = new Line("신분당선", "red");

        line.addSection(모란, 서현, 5);

        assertAll(
                () -> assertThat(line.getStations()).containsExactly(모란, 서현),
                () -> assertThat(line.getSections()).hasSize(1)
        );
    }

    @Test
    void 상행역_매칭을_기준으로_사이_구간_추가() {
        Line line = new Line("신분당선", "red", 강남, 판교, 10);

        line.addSection(강남, 서현, 5);

        assertAll(
                () -> assertThat(line.getStations()).containsExactly(강남, 서현, 판교),
                () -> assertThat(line.getSections()).hasSize(2)
        );

    }

    @Test
    void 하행역_매칭을_기준으로_사이_구간_추가() {
        Line line = new Line("신분당선", "red", 강남, 판교, 10);

        line.addSection(서현, 판교, 5);

        assertAll(
                () -> assertThat(line.getStations()).containsExactly(강남, 서현, 판교),
                () -> assertThat(line.getSections()).hasSize(2)
        );
    }

    @Test
    void 구간이_하나_이하일때_지우려고_시도하면_오류가_발생한다() {
        Line line = new Line("신분당선", "red");

        ThrowingCallable 구간_하나_이하일때_삭제시_오류_발생 = () -> line.removeSection(null);

        assertThatIllegalStateException().isThrownBy(구간_하나_이하일때_삭제시_오류_발생);
    }

    @Test
    void 두개의_구간_사이_역을_지우면_하나의_구간으로_합쳐진다() {
        Line line = new Line("신분당선", "red", 강남, 판교, 10);
        line.addSection(서현, 판교, 5);

        line.removeSection(서현);

        assertAll(
                () -> assertThat(line.getStations()).containsExactly(강남, 판교),
                () -> assertThat(line.getSections()).hasSize(1)
        );
    }

    @Test
    void 두개의_구간_종점_역을_지우면_종점_구간만_지워진다() {
        Line line = new Line("신분당선", "red", 강남, 판교, 10);
        line.addSection(서현, 판교, 5);

        line.removeSection(판교);

        assertAll(
                () -> assertThat(line.getStations()).containsExactly(강남, 서현),
                () -> assertThat(line.getSections()).hasSize(1)
        );
    }
}
