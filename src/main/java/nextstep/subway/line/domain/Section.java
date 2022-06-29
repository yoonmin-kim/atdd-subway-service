package nextstep.subway.line.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import nextstep.subway.station.domain.Station;

@Entity
public class Section {
    private static final String ERROR_MESSAGE_STATIONS_NULL = "상행역 또는 하행역은 null 이 될 수 없습니다.";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Embedded
    private Distance distance;

    protected Section() {
    }

    public Section(Station upStation, Station downStation, int distance) {
        validateStationsNull(upStation, downStation);

        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = new Distance(distance);
    }

    public Section(Line line, Station upStation, Station downStation, int distance) {
        this(upStation, downStation, distance);
        this.line = line;
    }

    private void validateStationsNull(Station upStation, Station downStation) {
        if (upStation == null || downStation == null) {
            throw new IllegalArgumentException(ERROR_MESSAGE_STATIONS_NULL);
        }
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public Distance getDistance() {
        return distance;
    }

    public int getDistanceValue() {
        return distance.getDistance();
    }

    public int getExtraCharge() {
        return line.getExtraCharge();
    }

    public void updateUpStation(Station station, Distance newDistance) {
        this.upStation = station;
        this.distance.subDistance(newDistance);
    }

    public void updateDownStation(Station station, Distance newDistance) {
        this.downStation = station;
        this.distance.subDistance(newDistance);
    }

    public void updateForCombine(Station downStation, Distance distance) {
        this.downStation = downStation;
        this.distance.addDistance(distance);
    }

    public void updateLine(Line line) {
        this.line = line;
    }

    public boolean hasStation(Station station) {
        return equalUpStation(station) || equalDownStation(station);
    }

    public boolean equalUpStation(Station station) {
        return upStation.equals(station);
    }

    public boolean equalDownStation(Station station) {
        return downStation.equals(station);
    }
}
